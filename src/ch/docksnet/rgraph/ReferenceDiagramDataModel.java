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

import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.diagram.PsiDiagramNode;
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
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramDataModel extends DiagramDataModel<PsiElement> {
    private List<PsiDiagramNode> myNodes = new ArrayList<>();
    private List<ReferenceEdge> myEdges = new ArrayList<>();

    public ReferenceDiagramDataModel(Project project, PsiClass psiClass) {
        super(project, ReferenceDiagramProvider.getInstance());

            for (PsiMethod psiMethod : psiClass.getMethods()) {
                Collection<PsiReference> all = ReferencesSearch.search(psiMethod, new LocalSearchScope(psiClass)).findAll();
            }

            for (PsiField psiField : psiClass.getFields()) {
            }

            for (PsiClassInitializer psiClassInitializer : psiClass.getInitializers()) {
//                myNodes.add(new PsiDiagramNode<>(element));
            }
    }



//    @NotNull
//    private DiagramRelationshipInfo createEdgeFromNonField(final ReferenceNode caller, final ReferenceElement calleeMethod) {
//        DiagramRelationshipInfo r;
//        r = new DiagramRelationshipInfoAdapter(ReferenceEdge.Type.REFERENCE.name()) {
//            @Override
//            public Shape getStartArrow() {
//                return STANDARD;
//            }
//
//            @Override
//            public String getLabel() {
//                return caller.getIdentifyingElement().getCalleeCount(calleeMethod) + "";
//            }
//        };
//        return r;
//    }
//
//    @NotNull
//    private DiagramRelationshipInfo createEdgeFromField() {
//        DiagramRelationshipInfo r;
//        r = new DiagramRelationshipInfoAdapter(ReferenceEdge.Type.FIELD_TO_METHOD.name()) {
//            @Override
//            public Shape getStartArrow() {
//                return DELTA_SMALL;
//            }
//
//            @Override
//            public DiagramLineType getLineType() {
//                return DiagramLineType.DASHED;
//            }
//        };
//        return r;
//    }



    @NotNull
    @Override
    public Collection<PsiDiagramNode> getNodes() {
        return myNodes;
    }

    @NotNull
    @Override
    public Collection<? extends DiagramEdge<PsiElement>> getEdges() {
        return myEdges;
    }

    @NotNull
    @Override
    public String getNodeName(DiagramNode<PsiElement> diagramNode) {
        if (diagramNode.getIdentifyingElement() instanceof PsiNamedElement) {
            return ((PsiNamedElement) diagramNode.getIdentifyingElement()).getName();
        }
        throw new IllegalStateException("PsiElement has no name: " + diagramNode.getIdentifyingElement());
    }

    @Nullable
    @Override
    public DiagramNode<PsiElement> addElement(PsiElement psiElement) {
        return null;
    }

//    @NotNull
//    @Override
//    public String getNodeName(PsiDiagramNode node) {
//        if (node.getIdentifyingElement() instanceof PsiNamedElement) {
//            return ((PsiNamedElement) node.getIdentifyingElement()).getName();
//        }
//        throw new IllegalStateException("PsiElement has no name: " + node.getIdentifyingElement());
//    }

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
