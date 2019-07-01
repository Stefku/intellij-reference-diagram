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

import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class ReferenceDiagramDataModel extends DiagramDataModel<PsiElement> {

    protected final Collection<DiagramNode<PsiElement>> nodes = new HashSet<>();
    protected final Map<PsiElement, DiagramNode<PsiElement>> nodesPool = new HashMap<>();
    protected final Collection<DiagramEdge<PsiElement>> edges = new HashSet<>();

    private final SmartPointerManager spManager;

    public ReferenceDiagramDataModel(Project project, DiagramProvider<PsiElement> provider) {
        super(project, provider);
        this.spManager = SmartPointerManager.getInstance(getProject());
    }

    protected SmartPsiElementPointer<PsiElement> createSmartPsiElementPointer(PsiElement psiElement) {
        return this.spManager.createSmartPsiElementPointer(psiElement);
    }
}
