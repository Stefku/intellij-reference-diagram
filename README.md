# Java Reference Diagram Plugin

This plugin for IntelliJ IDEA provides a diagram showing a reference graph for methods in java classes.

A future version shall show orphaned groups of methods, thus visiualize [lack of cohesion of methods](http://sonar-jenkins.blogspot.ch/2012/12/what-is-lcom4.html) 
which is probably slighty different from a graph of dependencies.

This plugin is available in IntelliJ Plugin Registry: [https://plugins.jetbrains.com/plugin/7996](https://plugins.jetbrains.com/plugin/7996)

## Example

This example shows three cohesive clusters in MainClass (source is showed below). 
A blue line indicates a method call, where a green line means that a field is coupled to a method in terms of cohesion. 

![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/master/test/ExampleDiagram_ch.docksnet.app.MainClass.png)

```java
public final class MainClass {

    {
        // class initializer
        method1();
    }

    static {
        // static class initializer
        staticMethod();
        staticField2 = "";
    }

    // field is coupled to a method, in terms of cohesion
    public static String staticField1 = staticMethod2();

    private static String staticField2;

    private int field1;

    // field is coupled to a method, in terms of cohesion
    private int field2 = createInt();

    private int createInt() {
        return 0;
    }

    public static void staticMethod() {
        staticMethod2();
    }

    private static String staticMethod2() {
        return null;
    }

    public void method1() {
        method2();
    }

    public void method1(int a) {
        method2();
    }

    private void method2() {
        field1 = 4;
    }

}
```