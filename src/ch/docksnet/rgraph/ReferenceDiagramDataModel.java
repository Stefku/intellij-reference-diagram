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

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.diagram.presentation.DiagramLineType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramDataModel extends DiagramDataModel<ReferenceElement> {
    private List<ReferenceNode> myNodes = new ArrayList<>();
    private List<ReferenceEdge> myEdges = new ArrayList<>();

    public ReferenceDiagramDataModel(Project project, ReferenceElement classElement) {
        super(project, ReferenceDiagramProvider.getInstance());

        prepareReferenceElements(classElement);

        DiagramRelationshipInfo r;

        for (final ReferenceElement callerMethod : classElement.getMembers()) {
            ReferenceNode caller = new ReferenceNode(callerMethod);
            for (final ReferenceElement calleeMethod : callerMethod.getCallees()) {
                ReferenceNode callee = new ReferenceNode(calleeMethod);
                r = resolveEdgeType(caller, calleeMethod);
                myEdges.add(new ReferenceEdge(caller, callee, r));
            }
        }
    }

    public void prepareReferenceElements(ReferenceElement classElement) {
        PsiClass psiClass = (PsiClass) classElement.getPsiElement();

        collectClassInitializers(classElement, psiClass);
        collectFields(classElement, psiClass);
        collectMethods(classElement, psiClass);

        for (ReferenceElement referenceElement : classElement.getMembers()) {
            if (referenceElement.getType().equals(ReferenceElement.Type.Method) || referenceElement.getType
                    ().equals(ReferenceElement.Type.Field)) {
                wireUpDependencies(referenceElement);
            }
        }
    }

    private DiagramRelationshipInfo resolveEdgeType(final ReferenceNode
            caller, final ReferenceElement calleeMethod) {
        final DiagramRelationshipInfo r;
        if (caller.getIdentifyingElement().getType() == ReferenceElement.Type.Field) {
            r = createEdgeFromField();
        } else {
            r = createEdgeFromNonField(caller, calleeMethod);
        }
        return r;
    }

    @NotNull
    private DiagramRelationshipInfo createEdgeFromNonField(final ReferenceNode caller, final
    ReferenceElement calleeMethod) {
        DiagramRelationshipInfo r;
        r = new DiagramRelationshipInfoAdapter(ReferenceEdge.Type.REFERENCE.name()) {
            @Override
            public Shape getStartArrow() {
                return STANDARD;
            }

            @Override
            public String getToLabel() {
                return caller.getIdentifyingElement().getCalleeCount(calleeMethod) + "";
            }
        };
        return r;
    }

    @NotNull
    private DiagramRelationshipInfo createEdgeFromField() {
        DiagramRelationshipInfo r;
        r = new DiagramRelationshipInfoAdapter(ReferenceEdge.Type.FIELD_TO_METHOD.name()) {
            @Override
            public Shape getStartArrow() {
                return DELTA_SMALL;
            }

            @Override
            public DiagramLineType getLineType() {
                return DiagramLineType.DASHED;
            }
        };
        return r;
    }

    private void wireUpDependencies(ReferenceElement referenceElement) {
        PsiElement psiElement = referenceElement.getPsiElement();
        PsiFile psiFile = psiElement.getContainingFile();
        Collection<PsiReference> all = ReferencesSearch.search(psiElement, GlobalSearchScope.fileScope
                (psiFile)).findAll();

        String containingClassName = ((PsiClass) psiElement.getContext()).getName();

        for (PsiReference psiReference : all) {
            if (!(psiReference instanceof PsiReferenceExpression)) {
                continue;
            }
            PsiReferenceExpression referenceExpression = (PsiReferenceExpression) psiReference;
            String className = PsiUtils.getClassName(referenceExpression);
            String callerName = PsiUtils.getParentName(referenceExpression);
            if (className.equals(containingClassName)) {
                ReferenceElement caller = ReferenceElementFactory.getElement(callerName);
                caller.addCallee(referenceElement);
            }
        }
    }

    private void collectMethods(ReferenceElement classElement, PsiClass psiClass) {
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            ReferenceElement element = ReferenceElementFactory.createInstance(psiMethod);
            myNodes.add(new ReferenceNode(element));
            classElement.addMember(element);
        }
    }

    private void collectFields(ReferenceElement classElement, PsiClass psiClass) {
        for (PsiField psiField : psiClass.getFields()) {
            ReferenceElement element = ReferenceElementFactory.createInstance(psiField);
            myNodes.add(new ReferenceNode(element));
            classElement.addMember(element);
        }
    }

    private void collectClassInitializers(ReferenceElement classElement, PsiClass psiClass) {
        for (PsiClassInitializer psiClassInitializer : psiClass.getInitializers()) {
            ReferenceElement element = ReferenceElementFactory.createInstance(psiClassInitializer);
            myNodes.add(new ReferenceNode(element));
            classElement.addMember(element);
        }
    }

    @NotNull
    @Override
    public Collection<ReferenceNode> getNodes() {
        return myNodes;
    }

    @NotNull
    @Override
    public Collection<ReferenceEdge> getEdges() {
        return myEdges;
    }

    @NotNull
    @Override
    public String getNodeName(DiagramNode<ReferenceElement> node) {
        return node.getIdentifyingElement().getName();
    }

    @Nullable
    @Override
    public ReferenceNode addElement(ReferenceElement referenceElement) {
        System.out.println("add Element");
        return null;
    }

    @Override
    public void removeNode(DiagramNode<ReferenceElement> node) {
        System.out.println("removeNode");
    }

    @Override
    public void refreshDataModel() {
    }

    @NotNull
    @Override
    public ModificationTracker getModificationTracker() {
        return VirtualFileManager.getInstance();
    }

    @Override
    public void dispose() {
    }

}
