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

## Keywords

### Metadata Keywords

Metadata keywords provide information to the compiler for the upcoming element.
While they exist only at source level, they can have side effects in the compiled result.

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

## Particles

Krow uses particles to indicate new functionality without making the language more verbose or confusing than it needs to be.
Particles reduce the need for new keywords, are easier to spot and highlight and make parsing more efficient.

|Symbol|Name|Description|
|------|----|-----------|
|`<>`|[Meta Section](#the-metadata-particle-)|Reference metadata section (for imports, exports, bridges).|
|`?`|[Optional](#the-optional-particle-)|Null inference and control.|
|`@`|[Target](#the-target-particle-)|Reserved.|
|`#`|[Tag](#the-tag-particle-)|Reserved.|

### The Metadata Particle `<>`

Todo.

### The Optional Particle `?`

Todo.

### The Target Particle `@`

Reserved.

### The Tag Particle `#`

Reserved.
