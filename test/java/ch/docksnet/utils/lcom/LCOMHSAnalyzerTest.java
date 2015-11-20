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

package ch.docksnet.utils.lcom;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stefan Zeller
 */
public class LCOMHSAnalyzerTest {

    public static final double DELTA = 0.0000000000001;

    /**
     * var = 1
     * methods = 0
     * refs = 0
     * => cohesion = 1
     */
    @Test
    public void just_variables() throws Exception {
        final Collection<LCOMNode> nodes = createSingleField();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(1d, result, DELTA);
    }

    /**
     * var = 0
     * methods = 1
     * refs = 0
     * => cohesion = 1
     */
    @Test
    public void just_methods() throws Exception {
        final Collection<LCOMNode> nodes = createSingleMethod();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(1d, result, DELTA);
    }

    /**
     * var = 2
     * methods = 2
     * refs = 4
     * => cohesion = 0
     */
    @Test
    public void full_connected() throws Exception {
        final Collection<LCOMNode> nodes = createFullConnected();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(0d, result, DELTA);
    }

    /**
     * var = 1
     * methods = 1
     * refs = 1 (each one)
     * => cohesion = 1
     */
    @Test
    public void one_to_one_with_1() throws Exception {
        final Collection<LCOMNode> nodes = createOneToOneWith1();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(1d, result, DELTA);
    }

    /**
     * var = 2
     * methods = 2
     * refs = 2 (each one)
     * => cohesion = 1
     */
    @Test
    public void one_to_one_with_2() throws Exception {
        final Collection<LCOMNode> nodes = createOneToOneWith2();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(1d, result, DELTA);
    }

    /**
     * var = 2
     * methods = 1
     * refs = 2 (each one)
     * => cohesion = 0
     */
    @Test
    public void one_access_two() throws Exception {
        final Collection<LCOMNode> nodes = createOneToTwo();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(0d, result, DELTA);
    }

    /**
     * var = 1
     * methods = 2
     * refs = 2 (one is implicit)
     * => cohesion = 0
     */
    @Test
    public void implicit_dependency_to_a_field() throws Exception {
        final Collection<LCOMNode> nodes = createImplicit();
        final LCOMAnalyzerData data = new LCOMAnalyzerData(nodes);

        LCOMHSAnalyzer sut = new LCOMHSAnalyzer(data);

        double result = sut.analyze();

        assertEquals(0d, result, DELTA);
    }

    private Collection<LCOMNode> createImplicit() {
        LCOMNode f1 = new LCOMNode("f1", LCOMNode.Type.Field);
        LCOMNode m1 = new LCOMNode("m1", LCOMNode.Type.Method);
        LCOMNode m2 = new LCOMNode("m2", LCOMNode.Type.Method);

        m1.addCallee(f1);
        m2.addCallee(m1);

        Collection<LCOMNode> result = new HashSet<>();
        result.add(f1);
        result.add(m1);
        result.add(m2);
        return result;
    }

    private Collection<LCOMNode> createSingleField() {
        Collection<LCOMNode> result = new HashSet<>();
        result.add(new LCOMNode("a", LCOMNode.Type.Field));
        return result;
    }

    private Collection<LCOMNode> createSingleMethod() {
        Collection<LCOMNode> result = new HashSet<>();
        result.add(new LCOMNode("a", LCOMNode.Type.Method));
        return result;
    }

    private Collection<LCOMNode> createFullConnected() {
        LCOMNode f1 = new LCOMNode("f1", LCOMNode.Type.Field);
        LCOMNode f2 = new LCOMNode("f2", LCOMNode.Type.Field);
        LCOMNode m1 = new LCOMNode("m1", LCOMNode.Type.Method);
        LCOMNode m2 = new LCOMNode("m2", LCOMNode.Type.Method);

        m1.addCallee(f1);
        m1.addCallee(f2);
        m2.addCallee(f1);
        m2.addCallee(f2);

        Collection<LCOMNode> result = new HashSet<>();
        result.add(f1);
        result.add(f2);
        result.add(m1);
        result.add(m2);
        return result;
    }

    private Collection<LCOMNode> createOneToOneWith1() {
        LCOMNode f1 = new LCOMNode("f1", LCOMNode.Type.Field);
        LCOMNode m1 = new LCOMNode("m1", LCOMNode.Type.Method);

        m1.addCallee(f1);

        Collection<LCOMNode> result = new HashSet<>();
        result.add(f1);
        result.add(m1);
        return result;
    }

    private Collection<LCOMNode> createOneToOneWith2() {
        LCOMNode f1 = new LCOMNode("f1", LCOMNode.Type.Field);
        LCOMNode f2 = new LCOMNode("f2", LCOMNode.Type.Field);
        LCOMNode m1 = new LCOMNode("m1", LCOMNode.Type.Method);
        LCOMNode m2 = new LCOMNode("m2", LCOMNode.Type.Method);

        m1.addCallee(f1);
        m2.addCallee(f2);

        Collection<LCOMNode> result = new HashSet<>();
        result.add(f1);
        result.add(f2);
        result.add(m1);
        result.add(m2);
        return result;
    }

    private Collection<LCOMNode> createOneToTwo() {
        LCOMNode f1 = new LCOMNode("f1", LCOMNode.Type.Field);
        LCOMNode f2 = new LCOMNode("f2", LCOMNode.Type.Field);
        LCOMNode m1 = new LCOMNode("m1", LCOMNode.Type.Method);

        m1.addCallee(f1);
        m1.addCallee(f2);

        Collection<LCOMNode> result = new HashSet<>();
        result.add(f1);
        result.add(f2);
        result.add(m1);
        return result;
    }

}