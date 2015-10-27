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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiMethodImpl;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
public class PsiUtils {

    public static final String NO_PARENT_METHOD_FOUND = "[No parent method found!]";
    public static final String STATIC_CLASS_INITIALIZER_NAME = "[static initializer]";
    public static final String CLASS_INITIALIZER_NAME = "[initializer]";

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
            return NO_PARENT_METHOD_FOUND;
        }
        if (parent instanceof PsiMethodImpl) {
            return createMethodName((PsiMethodImpl) parent);
        } else if (parent instanceof PsiClassInitializer) {
            return resolveClassInitializerName((PsiClassInitializer) parent);
        } else if (parent instanceof PsiField) {
            return ((PsiField) parent).getName();
        } else {
            return getParentName(parent);
        }
    }

    private static String resolveClassInitializerName(PsiClassInitializer classInitializer) {
        String name = "";
        if (classInitializer.hasModifierProperty("static")) {
            name = "static ";
        }
        name += "[initializer]";
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

    public static String createMethodName(PsiMethod psiMethod) {
        PsiType[] parameterTypes = psiMethod.getHierarchicalMethodSignature().getParameterTypes();
        String[] nameArray = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            nameArray[i] = parameterTypes[i].getPresentableText();
        }
        String parameters = StringUtil.join(nameArray, ",");
        return psiMethod.getName() + "(" + parameters + ")";
    }

}
