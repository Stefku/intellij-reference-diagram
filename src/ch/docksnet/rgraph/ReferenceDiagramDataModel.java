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
import java.util.Map;

import ch.docksnet.utils.IncrementableSet;
import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
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
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramDataModel extends DiagramDataModel<PsiElement> {

    private List<ReferenceNode> nodes = new ArrayList<>();
    private List<ReferenceEdge> edges = new ArrayList<>();

    public ReferenceDiagramDataModel(Project project, PsiClass psiClass) {
        super(project, ReferenceDiagramProvider.getInstance());
        collectNodes(psiClass);
        IncrementableSet<SourceTargetPair> incrementableSet = resolveRelationships(psiClass);
        createEdges(incrementableSet);
    }

    public void createEdges(IncrementableSet<SourceTargetPair> incrementableSet) {
        for (Map.Entry<SourceTargetPair, Long> element : incrementableSet.elements()) {

            PsiElement caller = element.getKey().getSource();
            PsiElement callee = element.getKey().getTarget();

            final DiagramRelationshipInfo relationship;
            if (caller instanceof PsiField) {
                relationship = createEdgeFromField();
            } else {
                relationship = createEdgeFromNonField(element.getValue());
            }

            ReferenceNode callerNode = new ReferenceNode(caller, getProvider());
            ReferenceNode calleeNode = new ReferenceNode(callee, getProvider());

            edges.add(new ReferenceEdge(callerNode, calleeNode, relationship));
        }
    }

    @NotNull
    public IncrementableSet<SourceTargetPair> resolveRelationships(PsiClass psiClass) {
        IncrementableSet<SourceTargetPair> incrementableSet = new IncrementableSet<>();

        for (ReferenceNode node : nodes) {
            PsiElement callee = node.getIdentifyingElement();

            Collection<PsiReference> all = ReferencesSearch.search(callee, new LocalSearchScope
                    (psiClass)).findAll();

            for (PsiReference psiReference : all) {
                if (!(psiReference instanceof PsiReferenceExpression)) {
                    continue;
                }
                PsiReferenceExpression referenceExpression = (PsiReferenceExpression) psiReference;
                PsiElement caller = PsiUtils.getRootPsiElement(referenceExpression);

                incrementableSet.increment(new SourceTargetPair(caller, callee));
            }
        }
        return incrementableSet;
    }

    @NotNull
    private DiagramRelationshipInfo createEdgeFromNonField(final long count) {
        DiagramRelationshipInfo r;
        r = new DiagramRelationshipInfoAdapter(ReferenceEdge.Type.REFERENCE.name()) {
            @Override
            public Shape getStartArrow() {
                return STANDARD;
            }

            @Override
            public String getToLabel() {
                if (count == 1) {
                    return "";
                } else {
                    return Long.toString(count);
                }
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

    public void collectNodes(PsiClass psiClass) {
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            nodes.add(new ReferenceNode(psiMethod, getProvider()));
        }

        for (PsiField psiField : psiClass.getFields()) {
            nodes.add(new ReferenceNode(psiField, getProvider()));
        }

        for (PsiClassInitializer psiClassInitializer : psiClass.getInitializers()) {
            nodes.add(new ReferenceNode(psiClassInitializer, getProvider()));
        }
    }

    @NotNull
    @Override
    public Collection<? extends DiagramNode<PsiElement>> getNodes() {
        return nodes;
    }

    @NotNull
    @Override
    public Collection<? extends DiagramEdge<PsiElement>> getEdges() {
        return edges;
    }

    @NotNull
    @Override
    public String getNodeName(DiagramNode<PsiElement> diagramNode) {
        return PsiUtils.getPresentableName(diagramNode.getIdentifyingElement());
    }

    @Nullable
    @Override
    public DiagramNode<PsiElement> addElement(PsiElement psiElement) {
        return null;
    }

    @Override
    public void removeNode(DiagramNode<PsiElement> node) {
        nodes.remove(node);
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
