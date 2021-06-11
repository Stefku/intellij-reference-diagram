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

import ch.docksnet.rgraph.fqn.ClassFQN;
import ch.docksnet.rgraph.fqn.FQN;
import ch.docksnet.rgraph.fqn.FieldFQN;
import ch.docksnet.rgraph.fqn.FileFQN;
import ch.docksnet.rgraph.fqn.MethodFQN;
import ch.docksnet.rgraph.fqn.PackageFQN;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan Zeller
 */
public class PsiUtils {

    @Nullable
    public static PsiElement getRootPsiElement(PsiClass psiClass, CompositePsiElement psiElement) {
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

    public static PsiDirectory getPsiJavaDirectory(String packageFQN, Project project) {
        return JavaPsiFacade.getInstance(project).findPackage(packageFQN).getDirectories()[0];
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

            @Override
            public String processPackage(PsiJavaDirectoryImpl aPackage) {
                return aPackage.getName();
            }

            @Override
            public String processFile(PsiJavaFile aFile) {
                return aFile.getName();
            }
        };

        return psiElementDispatcher.dispatch(psiElement);
    }

    public static FQN getFqn(PsiElement psiElement) {
        PsiElementDispatcher<FQN> psiElementDispatcher = new PsiElementDispatcher<FQN>() {

            @Override
            public FQN processClass(PsiClass psiClass) {
                return ClassFQN.create(psiClass);
            }

            @Override
            public FQN processMethod(PsiMethod psiMethod) {
                return MethodFQN.create(psiMethod);
            }

            @Override
            public FQN processField(PsiField psiField) {
                return FieldFQN.create(psiField);
            }

            @Override
            public FQN processClassInitializer(PsiClassInitializer psiClassInitializer) {
                return new FQN() {
                    @Override
                    public String getFQN() {
                        return getName(psiClassInitializer);
                    }
                };
            }

            @Override
            public FQN processInnerClass(PsiClass innerClass) {
                return ClassFQN.create(innerClass);
            }

            @Override
            public FQN processStaticInnerClass(PsiClass staticInnerClass) {
                return ClassFQN.create(staticInnerClass);
            }

            @Override
            public FQN processEnum(PsiClass anEnum) {
                return ClassFQN.create(anEnum);
            }

            @Override
            public FQN processPackage(PsiJavaDirectoryImpl aPackage) {
                return PackageFQN.create(aPackage);
            }

            @Override
            public FQN processFile(PsiJavaFile psiElement) {
                return FileFQN.create(psiElement);
            }
        };

        return psiElementDispatcher.dispatch(psiElement);
    }

    public static void navigate(PsiElement psiElement, Project project) {
        ApplicationManager.getApplication().invokeLater(
                () -> {
                    ApplicationManager.getApplication().assertIsDispatchThread();
                    Navigatable n = (Navigatable) psiElement;
                    //this is for better cursor position
                    if (psiElement instanceof PsiFile) {
                        VirtualFile file = ((PsiFile) psiElement).getVirtualFile();
                        if (file == null) return;
                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file, 0, 0);
                        n = descriptor.setUseCurrentWindow(true);
                    }

                    if (n.canNavigate()) {
                        n.navigate(true);
                    }
                }
        );
    }

}
