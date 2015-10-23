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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.docksnet.utils.IncrementableSet;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;

/**
 * @author Stefan Zeller
 */
public class ReferenceElement {
    private final PsiElement psiElement;
    private final String name;
    private final Type type;
    private final Set<Modifier> modifiers;

    private final List<ReferenceElement> callees;
    private final List<ReferenceElement> callers;

    private final IncrementableSet<ReferenceElement> calleeCount = new IncrementableSet<>();

    private final List<ReferenceElement> members;

    public ReferenceElement(PsiElement psiElement) {
        this.psiElement = psiElement;
        this.callees = new ArrayList<>();
        if (psiElement instanceof PsiClassImpl) {
            // TODO to constructor?
            this.name = ((PsiClassImpl) psiElement).getName();
            this.type = Type.Class;
            this.modifiers = PsiUtils.resolveModifiers((PsiClassImpl) psiElement);
            this.members = new ArrayList<>();
            this.callers = Collections.emptyList();
        } else if (psiElement instanceof PsiMethodImpl) {
            this.name = PsiUtils.createMethodName((PsiMethodImpl) psiElement);
            this.type = Type.Method;
            this.modifiers = PsiUtils.resolveModifiers((PsiMethodImpl) psiElement);
            this.members = Collections.EMPTY_LIST;
            this.callers = new ArrayList<>();
        } else if (psiElement instanceof PsiClassInitializer) {
            this.type = Type.ClassInitializer;
            this.members = Collections.EMPTY_LIST;
            this.modifiers = PsiUtils.resolveModifiers((PsiClassInitializer) psiElement);
            this.name = PsiUtils.resolveClassInitializerName(modifiers);
            this.callers = Collections.emptyList();
        } else if (psiElement instanceof PsiField) {
            this.type = Type.Field;
            this.members = Collections.EMPTY_LIST;
            this.modifiers = PsiUtils.resolveModifiers((PsiField) psiElement);
            this.name = ((PsiField) psiElement).getName();
            this.callers = new ArrayList<>();
        } else {
            throw new IllegalStateException("Unsupported type of PsiElement: " + psiElement);
        }
    }

    public String getName() {
        return name;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public Type getType() {
        return type;
    }

    public List<ReferenceElement> getCallees() {
        return callees;
    }

    public void addCallee(ReferenceElement element) {
        calleeCount.increment(element);
        if (!callees.contains(element)) {
            callees.add(element);
            element.addCaller(this);
        }
    }

    public long getCalleeCount(ReferenceElement element) {
        return calleeCount.get(element);
    }

    private void addCaller(ReferenceElement element) {
        callers.add(element);
    }

    public void addMember(ReferenceElement element) {
        if (type != Type.Class) {
            throw new IllegalStateException("Only ReferenceElements of type class can have members");
        }
        members.add(element);
    }

    public List<ReferenceElement> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public String getModifierAsString() {
        String result = StringUtil.join(modifiers, " ");
        return result.toLowerCase();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ReferenceElement other = (ReferenceElement) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return "ReferenceElement{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public enum Type {
        Class, Method, ClassInitializer, Field
    }

    public enum Modifier {
        PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, ABSTRACT
    }

}
