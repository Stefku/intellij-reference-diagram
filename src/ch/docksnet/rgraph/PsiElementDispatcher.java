/*
 * Copyright (C) 2019 Stefan Zeller
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

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;

/**
 * @author Stefan Zeller
 */
public abstract class PsiElementDispatcher<T> {

    public T dispatch(PsiElement psiElement) {
        if (psiElement instanceof PsiClass) {
            if (((PsiClass) psiElement).getContainingClass() == null) {
                return processClass((PsiClass) psiElement);
            } else {
                if (((PsiClass) psiElement).isEnum()) {
                    return processEnum((PsiClass) psiElement);
                } else {
                    if (((PsiClass) psiElement).hasModifierProperty("static")) {
                        return processStaticInnerClass((PsiClass) psiElement);
                    } else {
                        return processInnerClass((PsiClass) psiElement);
                    }
                }
            }
        }
        if (psiElement instanceof PsiMethod) {
            return processMethod((PsiMethod) psiElement);
        }
        if (psiElement instanceof PsiField) {
            return processField((PsiField) psiElement);
        }
        if (psiElement instanceof PsiClassInitializer) {
            return processClassInitializer((PsiClassInitializer) psiElement);
        }
        if (psiElement instanceof PsiJavaDirectoryImpl) {
            return processPackage((PsiJavaDirectoryImpl) psiElement);
        }
        if (psiElement instanceof PsiJavaFile) {
            return processFile((PsiJavaFile) psiElement);
        }
        throw new IllegalArgumentException("Type of PsiElement not supported: " + psiElement);
    }


    public abstract T processClass(PsiClass psiClass);

    public abstract T processMethod(PsiMethod psiMethod);

    public abstract T processField(PsiField psiField);

    public abstract T processClassInitializer(PsiClassInitializer psiClassInitializer);

    public abstract T processInnerClass(PsiClass innerClass);

    public abstract T processStaticInnerClass(PsiClass staticInnerClass);

    public abstract T processEnum(PsiClass anEnum);

    public abstract T processPackage(PsiJavaDirectoryImpl aPackage);

    public abstract T processFile(PsiJavaFile psiElement);
}
