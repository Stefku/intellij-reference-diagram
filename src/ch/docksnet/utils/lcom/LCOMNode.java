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

import ch.docksnet.rgraph.method.ReferenceNode;
import ch.docksnet.utils.PreConditionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Zeller
 */
public class LCOMNode {

    private List<LCOMNode> callees = new ArrayList<>();
    private List<LCOMNode> callers = new ArrayList<>();
    private final String fqn;
    private final Type type;
    private final ReferenceNode identifyingElement;

    public LCOMNode(String fqn, Type type, ReferenceNode identifyingElement) {
        this.identifyingElement = identifyingElement;
        PreConditionUtil.assertTrue(fqn != null, "Full qualified name must be set");
        PreConditionUtil.assertTrue(type != null, "Type must be set");
        this.type = type;
        this.fqn = fqn;
    }

    public void addCallee(LCOMNode callee) {
        callees.add(callee);
        callee.addCaller(this);
    }

    private void addCaller(LCOMNode caller) {
        callers.add(caller);
    }

    public String getFqn() {
        return fqn;
    }

    @Override
    public String toString() {
        return "LCOMNode{" +
                "fqn='" + fqn + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LCOMNode lcomNode = (LCOMNode) o;

        if (!fqn.equals(lcomNode.fqn)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return fqn.hashCode();
    }

    public List<LCOMNode> getCallers() {
        return callers;
    }

    public List<LCOMNode> getCallees() {
        return callees;
    }

    public ReferenceNode getIdentifyingElement() {
        return identifyingElement;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        Method, Field, Constant, Constructur, ClassInitializer, Class, InnerClass, StaticInnerClass, Enum, File, Package
    }

}
