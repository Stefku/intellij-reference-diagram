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

package ch.docksnet.rgraph.method;

import java.util.List;

import com.intellij.diagram.DiagramVfsResolver;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramVfsResolver implements DiagramVfsResolver<PsiElement> {

    @Override
    public String getQualifiedName(PsiElement psiElement) {
        return PsiUtils.getFqn(psiElement);
    }

    @Nullable
    @Override
    public PsiElement resolveElementByFQN(String fqn, Project project) {
        if (MethodFQN.isMethodFQN(fqn)) {
            MethodFQN methodFQN = MethodFQN.create(fqn);
            PsiClass psiClass = PsiUtils.getPsiClass(methodFQN.getClassName(), project);

            PsiMethod[] methodsByName = psiClass.findMethodsByName(methodFQN.getMethodName(), true);
            for (PsiMethod psiMethod : methodsByName) {
                List<String> parameterArray = MethodFQN.getParameterArray(psiMethod);
                String parameterRepresentation = MethodFQN.createParameterRepresentation(parameterArray);
                if (MethodFQN.createParameterRepresentation(methodFQN.getParameters()).equals
                        (parameterRepresentation)) {
                    return psiMethod;
                }
            }
            throw new IllegalArgumentException("Method not found: " + fqn);
        } else if (FieldFQN.isFieldFQN(fqn)) {
            FieldFQN fieldFQN = FieldFQN.create(fqn);
            PsiClass psiClass = PsiUtils.getPsiClass(fieldFQN.getClassName(), project);

            for (PsiField psiField : psiClass.getFields()) {
                if (psiField.getName().equals(fieldFQN.getFieldName())) {
                    return psiField;
                }
            }
            throw new IllegalArgumentException("Field not found: " + fqn);

        } else if (ClassFQN.isClassFQN(fqn)) {
            ClassFQN classFQN = ClassFQN.create(fqn);
            PsiClass psiClass = PsiUtils.getPsiClass(classFQN.getFQN(), project);
            return psiClass;
        }
        throw new IllegalStateException("Cannot processs fqn: " + fqn);
    }

}
