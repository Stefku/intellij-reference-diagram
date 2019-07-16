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

package ch.docksnet.rgraph;

import java.util.Arrays;

import ch.docksnet.rgraph.fqn.MethodFQN;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stefan Zeller
 */
public class MethodFQNTest {

    @Test
    public void test_create_from_string() throws Exception {
        MethodFQN result = MethodFQN.create("ch.docksnet.app.MainClass#method1(int,String,int)");

        assertEquals("ch.docksnet.app.MainClass", result.getClassName());
        assertEquals("method1", result.getMethodName());
        assertEquals(Arrays.asList("int", "String", "int"), result.getParameters());
    }

    @Test
    public void create_and_get_fqn() throws Exception {
        MethodFQN sut = new MethodFQN.Builder("ch.docksnet.app.MainClass", "method1")
                .addParameter("int")
                .addParameter("String")
                .addParameter("int")
                .create();

        String result = sut.getFQN();

        assertEquals("ch.docksnet.app.MainClass#method1(int,String,int)", result);
    }

}