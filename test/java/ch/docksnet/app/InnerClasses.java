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

/**
 * @author Stefan Zeller
 */
public class InnerClasses {

    Enum e = Enum.a;

    class InnerClass {

        {
            String test = method2();
        }

        String innerField = method1();

        void innerMethod() {
            method3();
        }

        class InnerInnerClass {

            String innerInnerField = method4();
            Enum e = Enum.a;

        }

    }

    private String method4() {
        return null;
    }

    private void method3() {

    }

    private String method2() {
        return null;
    }

    private String method1() {
        return null;
    }


    private static String sstaticMethod4() {
        return null;
    }

    private static void staticMethod3() {

    }

    private static String staticMethod2() {
        return null;
    }

    private static String staticMethod1() {
        return null;
    }

    enum Enum {
        a, b, c
    }

    static class StaticInnerClass {
        {
            String test = staticMethod2();
        }

        String innerField = staticMethod1();

        void innerMethod() {
            staticMethod3();
        }

        class InnerInnerClass {

            String innerInnerField = sstaticMethod4();
            Enum e = Enum.a;

        }
    }

}
