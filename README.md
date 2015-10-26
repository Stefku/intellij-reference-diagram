# Java Reference Diagram Plugin

This plugin for IntelliJ IDEA provides a diagram showing a reference graph for methods in java classes.

A future version shall show orphaned groups of methods, thus visiualize [lack of cohesion of methods](http://sonar-jenkins.blogspot.ch/2012/12/what-is-lcom4.html) 
which is probably slighty different from a graph of dependencies.

This plugin is available in IntelliJ Plugin Registry: [https://plugins.jetbrains.com/plugin/7996](https://plugins.jetbrains.com/plugin/7996)

## Known Issues
- Diagram is not updated when underlying class is refactored

## Example

This example shows three cohesive clusters in MainClass (source is showed below). 
A blue line indicates a method call, where a green line means that a field is coupled to a method in terms of cohesion.
The numbers on the edges indicating the number of references.

![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/master/test/ExampleDiagram_ch.docksnet.app.MainClass.png)

```java
public abstract class MainClass {

    {
        // class initializer
        method1();
    }

    static {
        // static class initializer
        staticMethod();
        staticField2 = StaticInnerClass.TEXT;
    }

    // field is coupled to a method, in terms of cohesion
    public static String staticField1 = staticMethod2c();

    private static String staticField2;

    int field1;

    private int field2 = createInt();

    public ENUM field3 = ENUM.A;

    public MainClass() {
        InnerClass innerClass = new InnerClass();
        innerClass.getName();
    }

    public MainClass(int field1, int field2, ENUM field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    private int createInt() {
        return 0;
    }

    public static void staticMethod() {
        staticMethod2c();
    }

    private static String staticMethod2c() {
        return null;
    }

    public final void method1() {
        method2();
    }

    protected void method1(int a) {
        method2();
        method2();
        method2();
    }

    void method2() {
        field1 = 4;
        field1 = 1;
        field3.getString();
    }

    abstract void abstractMethod();

    private void recursiveMethod() {
        recursiveMethod();
    }

    class InnerClass {
        // TODO what about inner classes?
        String innerField = staticMethod2c();
        public String getName() {
            return "Name";
        }
    }

    static class StaticInnerClass {
        public int i =3;
        public static final String TEXT = "text";
        static {
            System.out.println(TEXT);
            System.out.println(staticField1);
        }
    }

    enum ENUM {
        A, B, C;

        public String getString() {
            return "String";
        }
    }

}
```
