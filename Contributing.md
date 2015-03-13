#How to contribute to the Stajistics project

# How to Contribute #

If you wish to contribute code, the first thing to do is to become familiar with the project. Read the available documentation. Read a lot of source code. Ask questions on the mailing lists (listed on the [Introduction](Introduction.md) page). Get to know the goals and direction of the project.

The next step would be to make some small code changes and submit patches to the development mailing list. The project maintainers may then decide to grant commit privileges.

There are also many ways to contribute that do not involve writing code:
  * Submit bug reports
  * Perform code reviews
  * Evaluate the product
  * Write documentation
  * Participate in mailing list discussions
  * Propose new features
  * Provide feedback on existing features
  * Run performance tests
  * Improve the maven generated website

# Coding Standards and Conventions #

An Eclipse code formatter profile can be downloaded [here](http://stajistics.googlecode.com/files/stajistics.eclipse_formatter_profile.xml).

The Stajistics project adheres to the following standards and conventions.

**Indentation**
  * All spaces, no tabs
  * 4 spaces for an indentation
  * Contents of class bodies, method bodies, constructor bodies, statements within blocks and case bodies are indented

**Braces**
  * Braces are always used where they are optional in flow control statements
  * Opening braces start on the same line as the declaration, including:
    * Class, interface, enum, annotation declaration
```
    class A {
        // ...
    }
```
    * Anonymous class declaration
```
    A a = new A {
        // ...
    };
```
    * Contructor, method declaration
```
    public A() {
        // ...
    }
```
    * Enum constant body
```
    enum Numbers {
        ZERO(0) {
            // ...
        }
    }
```
    * Array initializers
```
    int[] a = { 0, 1, 2, 3 };
```

**Whitespace**
  * A single space is placed:
    * Before the opening brace of a class
    * Before the opening brace of an anonymous class
    * Before and after all binary and ternary operators
    * After all commas
    * Between if, for, while, synchronized, catch and the opening bracket
    * Between do and the opening brace
    * Before the opening bracket of parenthesized assert, return, throw
    * Before the opening brace of array initializers
    * After the colon of a label
  * Whitespace is excluded:
    * Before and after the opening bracket, and before the closing bracket of a method call
    * Before and after the opening bracket, and before the closing bracket of an instantiation
    * Before and after the opening bracket, and before the closing bracket of a this or super call

**Blank lines**
  * After a package declaration
  * Between class declarations
  * Between method and constructor declarations

**New lines**
  * At the end of a java file
  * After annotations on members and local variables
  * After one or more commas within long array initializations
  * After commas on long method or constructor parameter lists

**Line wrapping**
  * The maximum line width is loosely 110 characters
  * Parameter lists line up vertically

**Flow control statements**
  * if/else blocks appear as:
```
    if (a == 1) {
        // ...
    } else if (a == 2) {
        // ...
    } else {
        // ...
    }
```
  * for loops appear as:
```
    for (int i = 0; i < 10; i++) {
        // ...
    }

    for (Object o : objects) {
        // ...
    }
```
  * while and do/while blocks appear as:
```
    while (a) {
        // ...
    }

    do {
        // ...
    } while (a);
```
  * try/catch blocks appear as:
```
    try {
        // ...
    } catch (Exception e) {
        // ...
    } finally {
        // ...
    }
```