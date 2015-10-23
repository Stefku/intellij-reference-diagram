/*
 * Copyright (C) 2015 Stefan Zeller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.docksnet.app;

public abstract class MainClass {

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

    int field1;

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
    }

    abstract void abstractMethod();

    private void recursiveMethod() {
        recursiveMethod();
    }

    class InnerClass {
        // TODO what about inner classes?
        String innerField = staticMethod2();
    }

}

/*
 * This class must be ignored in the diagram
 */
class SiblingClass {

    public static String siblingStaticField = MainClass.staticField1;

}
