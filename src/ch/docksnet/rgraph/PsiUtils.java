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

import java.util.Set;

import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.Nullable;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;

/**
 * @author u215942 (Stefan Zeller)
 */
public class PsiUtils {

    public static final String NO_PARENT_METHOD_FOUND = "[No parent method found!]";

    @Nullable
    public static PsiClass getClassFromHierarchy(PsiElement psiElement) {
        PsiElement parent = psiElement.getParent();
        if (parent == null) {
            return null;
        }
        if (parent instanceof PsiClass) {
            return ((PsiClass) parent);
        } else {
            return getClassFromHierarchy(parent);
        }
    }

    public static String getParentName(PsiElement psiElement) {
        PsiElement parent = psiElement.getParent();
        if (parent == null) {
            // when called from fields (static and non-static)
            return NO_PARENT_METHOD_FOUND;
        }
        if (parent instanceof PsiMethodImpl) {
            return ((PsiMethodImpl) parent).getName();
        } else if (parent instanceof PsiClassInitializer) {
            return resolveClassInitializerName((PsiClassInitializer) parent);
        } else if (parent instanceof PsiField) {
            return ((PsiField) parent).getName();
        } else {
            return getParentName(parent);
        }
    }

    private static String resolveClassInitializerName(PsiClassInitializer classInitializer) {
        Set<ReferenceElement.Modifier> modifiers = ReferenceElement.resolveModifiers(classInitializer);
        String name = ReferenceElement.resolveClassInitializerName(modifiers);
        return name;
    }

    public static String getClassName(PsiElement psiElement) {
        PsiElement parent = psiElement.getParent();
        if (parent == null) {
            return "[No parent class found!]";
        }
        if (parent instanceof PsiClassImpl) {
            return ((PsiClassImpl) parent).getName();
        } else {
            return getClassName(parent);
        }
    }

}
