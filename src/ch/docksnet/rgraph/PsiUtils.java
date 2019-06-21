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

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan Zeller
 */
class PsiUtils {

    @Nullable
    static PsiElement getRootPsiElement(PsiClass psiClass, PsiElement psiElement) {
        return getRootPsiElementWithStack(psiClass, psiElement, new LinkedList<PsiElement>());
    }

    private static PsiElement getRootPsiElementWithStack(PsiClass psiClass, PsiElement psiElement, List<PsiElement> stack) {
        stack.add(psiElement);
        PsiElement parent = psiElement.getParent();
        if (parent == null) {
            return null;
        }
        try {
            if (parent instanceof PsiMethod) {
                if (PsiUtils.classHasMethod(psiClass, (PsiMethod) parent)) {
                    return parent;
                }
            } else if (parent instanceof PsiClassInitializer) {
                if (PsiUtils.classHasClassInitializer(psiClass, (PsiClassInitializer) parent)) {
                    return parent;
                }
            } else if (parent instanceof PsiField) {
                if (PsiUtils.classHasField(psiClass, (PsiField) parent)) {
                    return parent;
                }
            } else if (parent instanceof PsiClass) {
                if (psiClass.equals(((PsiClass) parent).getContainingClass())) {
                    return parent;
                }
            } else if (parent instanceof PsiAnonymousClass) {
                if (((PsiAnonymousClass) parent).getContainingClass().equals(psiClass)) {
                    return parent;
                }
            }
        } catch (Exception ex) {
            stack.add(parent);
            String preparedStack = prepareStack(stack);
            throw new IllegalStateException("Cannot get root element. Stack: " + preparedStack);
        }

        return getRootPsiElementWithStack(psiClass, parent, stack);
    }

    private static String prepareStack(List<PsiElement> stack) {
        StringBuilder sb = new StringBuilder();

        for (PsiElement element : stack) {
            sb.append(element.toString());
            sb.append(", ");
        }

        return sb.toString();
    }

    private static boolean classHasMethod(PsiClass psiClass, PsiMethod other) {
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            if (psiMethod.equals(other)) {
                return true;
            }
        }
        return false;
    }

    private static boolean classHasField(PsiClass psiClass, PsiField other) {
        for (PsiField psiField : psiClass.getFields()) {
            if (psiField.equals(other)) {
                return true;
            }
        }
        return false;
    }

    private static boolean classHasClassInitializer(PsiClass psiClass, PsiClassInitializer other) {
        for (PsiClassInitializer classInitializer : psiClass.getInitializers()) {
            if (classInitializer.equals(other)) {
                return true;
            }
        }
        return false;
    }

    static PsiClass getPsiClass(String classFQN, Project project) {
        return JavaPsiFacade.getInstance(project).findClass(classFQN, GlobalSearchScope
                .projectScope(project));
    }

    private static String getName(PsiClassInitializer psiClassInitializer) {
        if (psiClassInitializer.getModifierList().hasModifierProperty("static")) {
            return "[static init]";
        } else {
            return "[init]";
        }
    }

    static String getPresentableName(PsiElement psiElement) {
        PsiElementDispatcher<String> psiElementDispatcher = new PsiElementDispatcher<String>() {

            @Override
            public String processClass(PsiClass psiClass) {
                return psiClass.getName();
            }

            @Override
            public String processMethod(PsiMethod psiMethod) {
                List<String> parameterArray = MethodFQN.getParameterArray(psiMethod);
                String parameterRepresentation = MethodFQN.createParameterRepresentation(parameterArray);
                return psiMethod.getName() + "(" + parameterRepresentation + ")";
            }

            @Override
            public String processField(PsiField psiField) {
                return psiField.getName();
            }

            @Override
            public String processClassInitializer(PsiClassInitializer psiClassInitializer) {
                return getName(psiClassInitializer);
            }

            @Override
            public String processInnerClass(PsiClass innerClass) {
                return innerClass.getName();
            }

            @Override
            public String processStaticInnerClass(PsiClass staticInnerClass) {
                return staticInnerClass.getName();
            }

            @Override
            public String processEnum(PsiClass anEnum) {
                return anEnum.getName();
            }
        };

        return psiElementDispatcher.dispatch(psiElement);
    }

    static String getFqn(PsiElement psiElement) {
        PsiElementDispatcher<String> psiElementDispatcher = new PsiElementDispatcher<String>() {

            @Override
            public String processClass(PsiClass psiClass) {
                return ClassFQN.create(psiClass).getFQN();
            }

            @Override
            public String processMethod(PsiMethod psiMethod) {
                return MethodFQN.create(psiMethod).getFQN();
            }

            @Override
            public String processField(PsiField psiField) {
                return FieldFQN.create(psiField).getFQN();
            }

            @Override
            public String processClassInitializer(PsiClassInitializer psiClassInitializer) {
                return getName(psiClassInitializer);
            }

            @Override
            public String processInnerClass(PsiClass innerClass) {
                return ClassFQN.create(innerClass).getFQN();
            }

            @Override
            public String processStaticInnerClass(PsiClass staticInnerClass) {
                return ClassFQN.create(staticInnerClass).getFQN();
            }

            @Override
            public String processEnum(PsiClass anEnum) {
                return ClassFQN.create(anEnum).getFQN();
            }
        };

        return psiElementDispatcher.dispatch(psiElement);
    }

}
