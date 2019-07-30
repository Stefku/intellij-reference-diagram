# Java Reference Diagram Plugin

This plugin for IntelliJ IDEA Ultimate helps to get an overview of coupling and cohesion in java projects in two different contexts:
 - References of methods and fields in classes
 - References between files in packages

This plugin is available in IntelliJ Plugin Registry: [https://plugins.jetbrains.com/plugin/7996](https://plugins.jetbrains.com/plugin/7996).

# Usage
With this plugin a new diagram is available embedded in the action "Show UML Diagram" ⌥+⇧+⌘+U / CTRL+SHIFT+ALT+U. When focus
is on a file or inside a file then the method reference diagram will be opened. If fucus is on a package, then file reference diagram will be
opened.

# Features
- Show or hide categories of elements, like fields, methods, constructors, class initializers and their static companions.
- If the return value of a method is assigned to a field, then this is also threaded like a dependency and is shown in a different line style.
- As other diagrams, features available like Goto Source (F4), File Structure (Strg+F12) and Search (Ctrl+F), refactorings, Find Usages etc.
- Nodes can be removed from graph to help analyze cohesive structure of the cluss.
- Ability to isolate a subgraph based on a node: A subgraph of all it's callers or callees.
- Show the connection between two nodes.
- The cluster count is shown in the toolbar.
- Overview of other references not shown in graph (see chapter Outer References below).

## Cohesive clusters
If you have a suspect class and you want to analyze the cohesivnes of it this diagram helps you to visualize the [lack of cohesion of methods]
(http://sonar-jenkins.blogspot.ch/2012/12/what-is-lcom4.html). The clusters are shown visual in the diagram. Also, the cluster count is shown
in the toolbar. This is useful if you have big classes in legacy projects and you are not able to see the cluster count directly in the
diagram.

Sometimes, cohesive groups are not separeted because they are connected through common methods or fields. For example for logging purposes.
There's a great chance to see this as single nodes that are highly connected. With removing these nodes from the diagram you can see if there
were hidden disconnected clusters. Comparing the cluster count before and after the removal helps with this task.

![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/develop/doc/Example_show_cluster_count.png)
   
Does static methods and fields play a role in cohesion of a class? Just show or hide them via the toolbar. You can set the default behavior
in the usual settings dialog of the others diagrams.

![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/develop/doc/settings_default_categories.png)

## Isolate subgraphs
If you are interested in all methods and fields that can be reached by a certain method, then you can select that method and choose actions 
subsequently _Mark Callees_ and _Isolate Marked_. Or you want to see which methods can reach a given diagram element, then you choose actions
subsequently _Mark Callers_ and _Isolate Marked_.

## Show Connection between two Nodes
If you want to see the path between two methods you
1. Select the source of the desired path and _Mark Callees_ then _Isolate Marked_.
2. Select the destination of the desired path and _Mark Callers_ then _Isolate Marked_.
 
![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/develop/doc/show_path_between_nodes.gif)

## Other References
For overview of coupling there is the information of _other references_ on the top right of the diagram.
There are three numbers show:
1. Number of references from same package. (= package private)
2. Number of references from packages in the same hierarchy. (= public api)
3. Number of references from packages in a other hierarchy. (= spaghetti?)
These are different kind of references.
Where references from same package are kind of local of the package, the references from same hierarchy are part of the public api of the package.
The references from other hierarchy (i.e. from sibling packages) could be a sign of spaghetti.

**Example**
![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/develop/doc/coupling_through_OtherReferences_tool_window.png)
1. `ReferenceDiagramDataModel` references `OuterReferences`. In terms of package hierarchy `ch.docksnet.rgraph.method` is referenced from `ch.docksnet.rgraph`, which is a kind of wanted dependency direction.
2. `TestToolWindow` references `OuterReferences`. Here a sibling package ch.docksnet.rgraph.toolwindow references `ch.docksnet.rgraph.method`.

## Example

This example shows five cohesive clusters in MainClass (source is showed below). 
A blue line indicates a method call, where a green line means that a field is coupled to a method in terms of cohesion.
The numbers on the edges indicating the number of references.

![](https://raw.githubusercontent.com/stefku/intellij-reference-diagram/develop/doc/ExampleDiagram_ch.docksnet.app.MainClass.png)

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
# Known Issues
- Diagram does not work for library files [#38](https://github.com/Stefku/intellij-reference-diagram/issues/38)

# Support
[Buy Me a Coffee](https://ko-fi.com/H2H3DOZZ)
