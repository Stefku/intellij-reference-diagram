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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.util.PsiUtil;

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

    private final List<ReferenceElement> members;

    public ReferenceElement(PsiElement psiElement) {
        this.psiElement = psiElement;
        this.callees = new ArrayList<>();
        if (psiElement instanceof PsiClassImpl) {
            // TODO to constructor?
            this.name = ((PsiClassImpl) psiElement).getName();
            this.type = Type.Class;
            this.modifiers = resolveModifiers((PsiClassImpl) psiElement);
            this.members = new ArrayList<>();
            this.callers = Collections.emptyList();
        } else if (psiElement instanceof PsiMethodImpl) {
            this.name = PsiUtils.createMethodName((PsiMethodImpl) psiElement);
            this.type = Type.Method;
            this.modifiers = resolveModifiers((PsiMethodImpl) psiElement);
            this.members = Collections.EMPTY_LIST;
            this.callers = new ArrayList<>();
        } else if (psiElement instanceof PsiClassInitializer) {
            this.type = Type.ClassInitializer;
            this.members = Collections.EMPTY_LIST;
            this.modifiers = resolveModifiers((PsiClassInitializer) psiElement);
            this.name = PsiUtils.resolveClassInitializerName(modifiers);
            this.callers = Collections.emptyList();
        } else if (psiElement instanceof PsiField) {
            this.type = Type.Field;
            this.members = Collections.EMPTY_LIST;
            this.modifiers = resolveModifiers((PsiField) psiElement);
            this.name = ((PsiField) psiElement).getName();
            this.callers = new ArrayList<>();
        } else {
            throw new IllegalStateException("Unsupported type of PsiElement: " + psiElement);
        }
    }

    private Set<Modifier> resolveModifiers(PsiField psiField) {
        Set<Modifier> result = new HashSet<>();

        if (hasModifier(psiField, "public")) {
            result.add(Modifier.PUBLIC);
        }

        if (hasModifier(psiField, "private")) {
            result.add(Modifier.PRIVATE);
        }

        if (hasModifier(psiField, "protected")) {
            result.add(Modifier.PROTECTED);
        }

        if (hasModifier(psiField, "static")) {
            result.add(Modifier.STATIC);
        }

        if (hasModifier(psiField, "final")) {
            result.add(Modifier.FINAL);
        }
        ;

        return Collections.unmodifiableSet(result);
    }

    private static boolean hasModifier(PsiField psiField, String modifier) {
        return PsiUtil.findModifierInList(psiField.getModifierList(), modifier) != null;
    }

    public static Set<Modifier> resolveModifiers(PsiClassInitializer psiClassInitializer) {
        Set<Modifier> result = new HashSet<>();

        if (hasModifier(psiClassInitializer, "public")) {
            result.add(Modifier.PUBLIC);
        }

        if (hasModifier(psiClassInitializer, "private")) {
            result.add(Modifier.PRIVATE);
        }

        if (hasModifier(psiClassInitializer, "protected")) {
            result.add(Modifier.PROTECTED);
        }

        if (hasModifier(psiClassInitializer, "static")) {
            result.add(Modifier.STATIC);
        }

        if (hasModifier(psiClassInitializer, "final")) {
            result.add(Modifier.FINAL);
        }
        ;

        return Collections.unmodifiableSet(result);
    }

    private static boolean hasModifier(PsiClassInitializer classInitializer, String modifier) {
        return PsiUtil.findModifierInList(classInitializer.getModifierList(), modifier) != null;
    }

    private Set<Modifier> resolveModifiers(PsiClassImpl psiClass) {
        Set<Modifier> result = new HashSet<>();

        if (hasModifier(psiClass, "public")) {
            result.add(Modifier.PUBLIC);
        }

        if (hasModifier(psiClass, "private")) {
            result.add(Modifier.PRIVATE);
        }

        if (hasModifier(psiClass, "protected")) {
            result.add(Modifier.PROTECTED);
        }

        if (hasModifier(psiClass, "static")) {
            result.add(Modifier.STATIC);
        }

        if (hasModifier(psiClass, "final")) {
            result.add(Modifier.FINAL);
        }
        ;

        return Collections.unmodifiableSet(result);
    }

    private boolean hasModifier(PsiClassImpl psiClass, String modifier) {
        return PsiUtil.findModifierInList(psiClass.getModifierList(), modifier) != null;
    }

    private Set<Modifier> resolveModifiers(PsiMethodImpl psiMethod) {
        Set<Modifier> result = new HashSet<>();

        if (hasModifier(psiMethod, "public")) {
            result.add(Modifier.PUBLIC);
        }

        if (hasModifier(psiMethod, "private")) {
            result.add(Modifier.PRIVATE);
        }

        if (hasModifier(psiMethod, "protected")) {
            result.add(Modifier.PROTECTED);
        }

        if (hasModifier(psiMethod, "static")) {
            result.add(Modifier.STATIC);
        }

        if (hasModifier(psiMethod, "final")) {
            result.add(Modifier.FINAL);
        }
        ;

        return Collections.unmodifiableSet(result);
    }

    private boolean hasModifier(PsiMethodImpl psiMethod, String modifier) {
        return PsiUtil.findModifierInList(psiMethod.getModifierList(), modifier) != null;
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

    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    public List<ReferenceElement> getCallers() {
        return callers;
    }

    public List<ReferenceElement> getCallees() {
        return callees;
    }

    public void addCallee(ReferenceElement element) {
        callees.add(element);
        element.addCaller(this);
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

    public enum Type {
        Class, Method, ClassInitializer, Field
    }

    public enum Modifier {
        PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL
    }

}
