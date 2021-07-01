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

The Krow compiler has very limited verification, allowing a lot of instructions to be compiled that Java's compiler would consider illegal. Some of these will still be caught by the runtime verifier when the class is loaded, if they would actually prevent the code from running. 

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

### Modifier Keywords

Modifier keywords modify the upcoming element. They are effective in the compiled result.
Note: Krow does not directly offer visibility modifiers.

|Word|Target|Description|
|----|-----------|------|
|`static`|Field declaration, method declaration.|Marks the upcoming class member as static.|
|`abstract`|Class declaration, method declaration.|Marks the upcoming method as implemented elsewhere.|
|`final`|Field declaration, class declaration, method declaration.|Marks the upcoming element as final.|
|`native`|Method declaration.|Marks the upcoming element as implemented elsewhere.|

### Type Keywords

Type keywords are used to designate primitive (or built-in) in fields, methods, variables and returns.

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


## Particles

Krow uses particles to indicate new functionality without making the language more verbose or confusing than it needs to be.
Particles reduce the need for new keywords, are easier to spot and highlight and make parsing more efficient.

|Symbol|Name|Description|
|------|----|-----------|
|`<>`|[Meta Section](#the-metadata-particle-)|Reference metadata section (for imports, exports, bridges).|
|`#`|[Dynamic](#the-dynamic-particle-)|Reserved.|
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
```kro
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
```
Object a = null;
Object b = "hello";

if (?a) // never reached
if (!?a) // always reached

if (?b) // always reached
if (!?b) // never reached
```

#### Early Exit
Example: The target on which this method is called is potentially null.
```kro
String string = null;
String sub = string?.substring(3);
assert !?sub;

```

Krow's optional particle will make the compiler verify the target is set before calling the method. If the target is null, the instruction will be skipped.
Note: this should be used CAUTIOUSLY as it can have some unintended side-effects, which cannot always be verified by the Krow compiler!

```kro
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
