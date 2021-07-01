Krow
=====

### Opus #8

// Unfinished.

The bare-bones JVM language.

## Introduction

Krow is a very basic compiled JVM language.
The language's core goal is to tackle some of Java's deficits, while also targeting a slightly different niche.

Java's long-standing and established nature is both a strength and a flaw: Java is feature-rich and well-known, but outdated and badly-designed concepts cannot be replaced or improved. Java is safe and reliable, but limited in execution. Java is simple and efficient, but direct control is locked behind the compiler.

Clearly, there are many situations where these benefits outweigh their drawbacks. Many programmers benefit from Java's simplicity, newer programmers benefit from Java's safety and everybody benefits from Java's reliability. However, some users may find themselves longing for source-level access to JVM internals or to patch the many gaps and inconsistencies in Java.

The Krow language spec and the original ReKrow compiler were designed and written within six days. I rested on the seventh. ;) More than anything, they were created for proof of concept: a JVM language entirely interoperable with Java, yet able to do things far outside the Java language spec. A JVM language that provides more than sugar.

> *Nomenclature:*
> 
> Krow spells 'work' backwards, but this is coincidental. The name comes from it being in row 'K' of my project table. 
> However, the back-to-front name has been embraced for other elements: rekrow (worker) .ark (Krow ARchive)
> 
> The pronunciation doesn't matter, I'm partial to 'kay-row', but 'crow' is equally fine. The tag-line is a reference to crows being carrion birds.
> 
> Coincidentally, K comes after J in the alphabet.


## The ReKrow Compiler

The basic Krow compiler is called 'ReKrow'. 

ReKrow is very rudimentary. It is designed to be as close to 'what-you-see-is-what-you-get' as possible, with no re-ordering or rearranging of entries.

It also has very little verification, allowing a lot of instructions to be compiled that Java's compiler would consider illegal. Some of these will still be caught by the runtime verifier when the class is loaded, if they would actually prevent the code from running.

In practice, this means you can:
- Create default classes with abstract methods.
- Extend an abstract class without implementing all of its methods.
- Write unreachable code after the return statement.
- Much, much more.

ReKrow is a 'flyover' compiler. This means it reads the input source in one line: the compiler does not know about something until it reaches it. While this does make forward referencing more problematic, it also makes the compiler less complex and more efficient: elements are dealt with as they are reached, then discarded. The compiler requires no recursion or sub-parsing.

ReKrow uses [ASM](https://asm.ow2.io/) for assembling its output bytecode. ASM is a very old and established tool, so much so that the Java JDK uses it internally.

## Keywords

### Metadata Keywords

Metadata keywords provide information to the compiler for the upcoming element.
While they exist only at source level, they can have side effects in the compiled result.
See the [metadata particle](#the-metadata-particle-) for more information.

|Word|Value|Target|Description|
|----|-----------|------|-----------|
|`import`|Reference `<>` metadata.|Class declaration, field declaration, method declaration.|Asserts the given references exist in the target area, allows use of their shortened names.|
|`export`|Reference `<>` metadata.|Class declaration, field declaration, method declaration.|Exports the given element for use outside the class. Exports any included elements to users of this element.|
|`bridge`|Reference `<>` metadata for a method.|Method declaration.|Sets the bridge target of the upcoming method.|
|`const`|Identifier and literal value.|Class body, method body.|Stores a compile-time constant.|

### Modifier Keywords

Modifier keywords modify the upcoming element. They are effective in the compiled result.
Note: Krow does not directly offer visibility modifiers.

|Word|Target|Description|
|----|-----------|------|
|`static`|Field declaration, method declaration.|Marks the upcoming class member as static.|
|`abstract`|Class declaration, method declaration.|Marks the upcoming method as implemented elsewhere.|
|`final`|Field declaration, class declaration, method declaration.|Marks the upcoming element as final.|
|`native`|Method declaration.|Marks the upcoming element as implemented elsewhere.|

### Instruction Keywords

Instruction keywords are used in code bodies. They perform a function, and may only be used at the start of a statement. 

|Word|Accepts|Description|
|----|-----------|------|
|`label`|`identifier`|Marks a point which can be jumped to.|
|`goto`|`identifier`|Jumps to the given label.|
|`if`|`(boolean)`|Runs the subsequent statement if the condition is true. Note: the statement is from root-level.|
|`return`|`value?`|Returns the given value from the method, or returns empty.|
|`assert`|`boolean`|Asserts that the given value is true.|

### Type Keywords

Type keywords are used to designate primitive (or built-in constants) in fields, methods, variables and returns.

|Word|Length|Machine Words|Descriptor|
|----|------|-------------|----------|
|`void`|1|1|V|
|`boolean`|1|1|Z|
|`char`|2|1|C|
|`short`|2|1|S|
|`int`|4|1|I|
|`long`|8|2|J|
|`float`|4|1|F|
|`double`|8|2|D|
|`struct`|8 (16+ in real memory)|2|S(...) // See [struct](#Structs) section.|

## Core Features

### Literals

Krow introduces source-level literals for methods, fields, classes and structs.

|Type|Java Example|Krow Literal|Description|
|----|-------|---------|-----------|
|Class|`com.example.Thing`|`com/example/Thing`|A type literal.|
|Field|`static Type name`|`Owner.name:Type`|A field reference. Used for importing, exporting and reflective access.|
|Method|`public void myMethod(Type param, int i)`|`Owner::myMethod(Type,I)V`|A method reference. Used for importing, exporting and reflective access.|
|Struct|Java doesn't have structs :(|`S(name:Type,name:Type)`|A structure reference, used for automatic type inference.|

### Jump Instructions

Krow allows for routine jumps in methods by use of the `goto` and `label` keywords.

These are very raw and potentially dangerous instructions - it is very easy to create infinite loops or inaccessible code that the JVM simply cannot process because the jump instructions cannot be processed. As such, non-trivial use of `goto` is not recommended.

Warnings aside, the instruction is very simple.
Labels are denoted by a `label name;` statement.
That label can be jumped to by a `goto name;` statement.

For beginners, it is recommended to keep these instructions in pairs (rather than having multiple jumps to a single point) as it lowers the risk of jump asymmetry.

> N.B: Jump asymmetry occurs when the stack/variable map could have two different states at a label.
> The Krow compiler is able to prevent this in most situations.

Jump instructions can be used to create more complex conditional sections:
```java 
int a = 6;
int b = 5;
if (a == b) goto end;
System.out.println(a);
System.out.println(b);
label end;
System.out.println("end");
```

It is very easy to create inaccessible areas of code.
This is not an issue in itself - unlike Java, Krow is perfectly able to handle unreachable statements - but programmers should be careful with doing this accidentally.

Unreachable code: (Valid but pointless.)
```java 
int a = 10;
int b = 5;
goto box;
int c = 6;
System.out.println(a);
System.out.println(c);
label box;
System.out.println(b);
```

This can be used to manually assemble Java's `while` and `for` loops.
Example:
```java 
int a = 0;
label top;
if (a == 12) goto exit;
a = a + 1;
goto top;
label exit;
System.out.println(a);
```

Manual jump instructions can be used to create much more efficient and direct loops - a skilled programmer can always create a more effective version than a machine compiler.
However, the reverse is also true: misuse of jump instructions will likely be less efficient than a simple loop. Use them wisely.

### Constants

Like other languages, Krow offers constants using the `const` keyword.
However, this is a compile-time keyword. The constant is *not* available at runtime as a field or variable. It is simply a programming tool to make use of the `LDC` instruction instead of assigning unnecessary variables for constant values.

Constants must either be primitive or of a compile-time constant type, such as a string, class or handle.

This feature is a better replacement for Java's final assigned variables and fields.
The variable `final String name = "Bob";` would have its value copied to all uses within the method using `LDC`, but the variable will still occupy a slot in memory which it doesn't need to - the variable itself is never accessed. Krow avoids this waste with `const` by using the value as-is and never assigning a variable for it.
The same principle applies to final primitive variables which are assigned at declaration.


Constants can be declared at the class or the method level, and are available within their scope like regular variables.
```java 
class Example {
    const name = "Henry";
    
    void myMethod() {
        const surname = "Carter";
        // name is available here
        // surname is available here
    }
}
```

Constants may also be used in the implicit struct constructor.

### Structs

Structs are compile-time anonymous classes that hold only field data and have no declared methods.

They are referenced and assigned through the `struct` keyword, and can be used in any place an object can.

Structs can be assigned through one of two ways:
1. The implied `struct(var0, var1, ...)` constructor.
2. The literal `{ Type name = val; Type name = val; }` block.

The implicit constructor is quicker and easier to use, but only accepts fields/variables as arguments. This is because the compiler uses the variable names/types as the struct fields.
This implicit constructor is only provided for ease-of-use.

The literal block is the safer and clearer way to create a struct.

Structs do not need to be declared in order to be used publicly. The compiler calculates the runtime class coordinates so that any struct with the same field types/names will use the same class.

*Example:* Simple data structure using the implicit constructor.
```java 
import <java/util/UUID>
export <>
struct myMethod(String name, int age, UUID id) {
    return struct(name, age);
    // implicit struct { String name; int age; }
}
```

*Example:* Using structs for multiple return.
```java 
import <org/bukkit/Player, Player::getHealth, Player::getName>
export <this::getData(Player)S(health:D,name:String)>
struct getData(Player player) {
    return {
        double health = player.getHealth();
        String name = player.getName();
    }
}

import <org/bukkit/Player>
void use(Player player) {
    struct data = this.getData(player);
    if (data.health > 20) //...
    if (data.name.equals("henry")) // ...
}
```

### Deferred Initialisation

Unlike Java, Krow is very lenient when it comes to object initialisation rules.

While this can be a lot more dangerous (it is possible to write code that passes compilation but would error at runtime) it also gives advanced users a lot more freedom to be creative in unusual situations.

Creating an object is broken up into two stages: allocation and initialisation.

Allocation is done via the `new` keyword and the provided type. Initialisation is done using `()` parentheses to call a constructor.
In most cases, users will want to do both together as you would in Java:
```MyType var = new MyType(1, 2);```

However, there may be cases when it is more convenient (or even necessary) to do these separately.
```
MyType var = new MyType; // allocate
var(1, 2); // initialise
```
In this special situation, the constructor `()` may be called on the variable directly because it is still an `uninitialised_n` on the stack.

These two parts do not need to be done together - you may have other code or logic between, as long as the object is not directly used before it is initialised.

### Targeted Exporting

Krow does not include visibility modifiers at the language level.
This is a deliberate choice - partly because it is very simple to ignore access modifiers in Krow using the built-in [dynamic particle](#the-dynamic-particle-). This would make a `private` modifier a little pointless.
Instead, Krow opts for an `export` metadata system.

This is currently unfinished.

## Particles

Krow uses particles to indicate new functionality without making the language more verbose or confusing than it needs to be.
Particles reduce the need for new keywords that would clutter the language, are easier to spot and highlight and make parsing more efficient.

|Symbol|Name|Description|
|------|----|-----------|
|`<>`|[Meta Section](#the-metadata-particle-)|Reference metadata section (for imports, exports, bridges).|
|`#`|[Dynamic](#the-dynamic-particle-)|Invoking inaccessible methods.|
|`?`|[Optional](#the-optional-particle-)|Null inference and control.|
|`@`|[Target](#the-target-particle-)|Reserved.|

### The Metadata Particle `<>`

The metadata particle is used to store signatures, which are source-level representations of the basic language elements.
This particle is used during the compiler import phase to register locally-available constructs that can be referenced by their simple name within code (e.g. `System.out` rather than `java/lang/System.out`.)
Signature metadata is also used during the export phase to show where elements should be made available to (via targeted visibility.)

In method body code, the metadata particle can be used for type assurance and to tell the compiler to add an explicit `CHECKCAST` instruction in. This can be used after any value-giving code element (such as a non-void method, field, variable or literal.)

Signatures may also be used in code for obtaining member references. 

#### Type

Types may always be referenced by their fully-qualified names, but once imported can always be referred to by simple name.
So-called 'nested' classes from java are referred by their full `Root$Nested` name, since this is the actual name of the class in memory.

Example: `import <java/io/PrintStream>`
Example: `String var1 = var2<String>;` (String type assurance.)
```
org/example/Type
    |         |
    |       Type name
    |
  Package path
```

#### Method

The method signature can be recognised by the `::` and `()` elements.

Example: `import <Arrays::toString(Object[])String`
```
Owner::methodName(Param[],Z,J)Return
  |         |       |           |
  |        Name     |          Return type
  |               Parameter types
 Declaring class
```

Constructors use `Type::new(...)V` in their signature, rather than their true `<init>` name.

#### Field

The field signature can be recognised by the `.` and `:` combination.

Example: `import <System.out:PrintStream>`
```
System.out:PrintStream
  |     |       | 
  |    Name    Type
  |   
 Declaring class
```

### The Dynamic Particle `#`

The dynamic particle is a powerful feature of the Krow source schema that permits use of the `INVOKEDYNAMIC` instruction, allowing access to otherwise-inaccessible members.

The particle is used in place of the `.` during regular access.

A compile-time constant of the member handle is generated, which is then passed to the bootstrapper in the Krow runtime on first use. After this first access, the bootstrap is no longer necessary.

Krow's dynamic particle is an alternative to Java reflection in a lot of cases.

Advantages:
- Much simpler to use.
- Much shorter to write in code.
- No need to cache method/field objects.
- Significantly faster than reflective access.

Disadvantages:
- Method signature must be known at compile time.
- The same bootstrap is not reused in multiple places.

Krow's dynamic particle is inappropriate in some cases, such as using reflection to blindly/dynamically access classes and methods whose names are not known at runtime.

For methods:
Regular: `String c = object.myPrivateMethod(a, b);` (Throws error at verification - `myPrivateMethod` is private and inaccessible.)
Dynamic: `String c = object#myPrivateMethod(a, b);` (No error - invocation is permitted.)

For fields:
Regular: `PrintStream stream = System.out;`
Dynamic: `PrintStream stream = System#out;` 

Note: regular access should always be preferred as it is more efficient and easier for the JIT compiler to speed up. However, this is still significantly better for performance than Java's typical reflection, or even using Java's MethodHandles API manually. It is of comparable speed to calling a lambda function.

### The Optional Particle `?`

The optional particle is designed to introduce basic null safety to Krow at both the source and execution level, without needing to resort to third-party meta like Java's `@Nullable` annotations (and similar.)

The optional has four main uses:
1. On variables, fields and method return types, as an indicator of potential null-values. (Semantic.)
2. In field and method calls, to prevent `NullPointerException`s. (Functional.)
3. As an alternative to ternaries, to prevent double evaluation of the input. (Functional.)

#### Semantic Null Marker
Example: Potential null return value.
```java 
String? getName() {
    return this.name;
}
```

The `String?` tells the compiler (and any verifier tools) that the result of the method can be `null` during regular operation of the program. This should, of course, be used sparingly - if a value would only be `null` during an error or some extreme state, using the operator here should be avoided.

Similarly, this can also be used with fields and variables: `final Object? obj;`
The same caveat applies - overuse and abuse of the optional particle will do nothing more than produce annoying warnings.

#### Null Check

Krow's compiler uses `?value` for the `ISNULL` instruction. 
Similarly, `!?value` is effectively the `NOTNULL` instruction, since `!` negates the boolean.

Example:
```java 
Object a = null;
Object b = "hello";

if (?a) // never reached
if (!?a) // always reached

if (?b) // always reached
if (!?b) // never reached
```

#### Default Value

The typical Java ternary `(str != null ? str : "default")` requires the evaluation of the checked element twice. While this is not a problem for small variables, you could not do this with a method without calling the method result twice.
This is also mirrored in Java's compile result, which will load the `str` variable twice.

Krow adds an alternative for this case: the default value `var1 ? var2`.
This `?` default operator is an implicit null check, and functions very similarly to the above ternary, but without loading the variable twice.

This is not just syntax sugar - there is a minor performance improvement over a ternary, but the difference is minimal.

Example:
```java 
String name = player.getName();
return name ? "Unknown";
```

#### Early Exit
Example: The target on which this method is called is potentially null.
```java 
String string = null;
String sub = string?.substring(3);
assert !?sub;

```

Krow's optional particle will make the compiler verify the target is set before calling the method. If the target is null, the instruction will be skipped.
Note: this should be used CAUTIOUSLY as it can have some unintended side-effects, which cannot always be verified by the Krow compiler!

```java 
// x and y are objects
Object value = 
obj? // obj is not null
    .method()? // this is null
    .method(); // so this is skipped
    // value = null
```

The Krow compiler handles this by keeping the stack map constant so that linear jump instructions can be used at each `?` particle so the null value left on the stack will be stored.

In cases where the method is void or the value is not used, the null-value is popped when the dead end `;` is reached.

### The Target Particle `@`

Reserved.
Potentially for easier invocation of utilities from the Krow runtime.
