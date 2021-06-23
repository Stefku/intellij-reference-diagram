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

import ch.docksnet.rgraph.PsiUtils;
import ch.docksnet.rgraph.ReferenceDiagramDataModel;
import ch.docksnet.rgraph.ReferenceDiagramProvider;
import ch.docksnet.rgraph.fqn.FQN;
import ch.docksnet.rgraph.fqn.FileFQN;
import ch.docksnet.utils.IncrementableSet;
import com.intellij.diagram.DiagramCategory;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.diagram.presentation.DiagramLineType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScopes;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;

/**
 * @author Stefan Zeller
 */
public class MethodReferenceDiagramDataModel extends ReferenceDiagramDataModel {

    private SmartPsiElementPointer<PsiElement> baseElement;

    public MethodReferenceDiagramDataModel(Project project, PsiClass psiClass) {
        super(project, ReferenceDiagramProvider.getInstance());
        init(psiClass);
    }

    private void init(PsiClass psiClass) {
        this.baseElement = psiClass == null ? null : createSmartPsiElementPointer(psiClass);
        collectNodes(psiClass);
    }

    private void collectNodes(PsiClass psiClass) {
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            addUserElement(psiMethod);
        }

        for (PsiField psiField : psiClass.getFields()) {
            addUserElement(psiField);
        }

        for (PsiClassInitializer psiClassInitializer : psiClass.getInitializers()) {
            addUserElement(psiClassInitializer);
        }

        for (PsiClass innerClass : psiClass.getInnerClasses()) {
            addUserElement(innerClass);
        }
    }

    @Override
    public void rebuild(PsiElement element) {
        super.rebuild(element);
        clearAll();
        init((PsiClass) element);
        refreshDataModel();
    }

    @Nullable
    @Override
    protected DiagramEdge<PsiElement> toEdge(@NotNull DiagramNode<PsiElement> from,
                                             @NotNull DiagramNode<PsiElement> to,
                                             Long value) {
        final DiagramRelationshipInfo relationship;
        if (from.getIdentifyingElement() instanceof PsiField) {
            relationship = createEdgeFromField();
        } else {
            relationship = createEdgeFromNonField(value == null ? 0 : value);
        }

        return new ReferenceEdge(from, to, relationship);
    }

    @NotNull
    @Override
    protected IncrementableSet<SourceTargetPair> resolveRelationships() {
        PsiElement psiElement = getBaseElement();
        IncrementableSet<SourceTargetPair> incrementableSet = new IncrementableSet<>();

        for (DiagramNode<PsiElement> node : getNodes()) {
            PsiElement callee = node.getIdentifyingElement();

            Collection<PsiReference> all = ReferencesSearch.search(callee, new LocalSearchScope
                    (psiElement)).findAll();

            for (PsiReference psiReference : all) {
                if (!(psiReference instanceof CompositePsiElement)) {
                    continue;
                }
                PsiElement caller = PsiUtils.getRootPsiElement((PsiClass) psiElement, (CompositePsiElement) psiReference);

                if (caller == null) {
                    continue;
                }

                incrementableSet.increment(new SourceTargetPair(caller, callee));
            }
        }
        return incrementableSet;
    }

    @Nullable
    @Override
    protected PsiElement getBaseElement() {
        if (this.baseElement == null) {
            return null;
        } else {
            PsiElement element = this.baseElement.getElement();
            if (element != null && element.isValid()) {
                return element;
            } else {
                return null;
            }
        }
    }

    @Override
    protected FQN getBaseForOuterReferences(PsiElement psiElement) {
        return FileFQN.from((PsiJavaFile) psiElement.getContainingFile());
    }

    @NotNull
    @Override
    protected Collection<PsiReference> resolveOuterReferences(PsiElement callee) {
        return ReferencesSearch.search(callee, GlobalSearchScopes.projectProductionScope(getProject())).findAll();
    }

    @Override
    protected boolean isAllowedToShow(PsiElement psiElement) {
        if (psiElement != null && psiElement.isValid()) {
            final DiagramNodeContentManager nodeContentManager = getNodeContentManager();
            for (DiagramCategory enabledCategory : nodeContentManager.getEnabledCategories()) {
                if (nodeContentManager.isInCategory(psiElement, enabledCategory)) {
                    return true;
                }
            }
        }
        return false;
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

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof DiagramRelationshipInfoAdapter)) {
                    return false;
                }
                return toString().equals(obj.toString()) && getToLabel().equals(((DiagramRelationshipInfoAdapter) obj).getToLabel());
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

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof DiagramRelationshipInfoAdapter)) {
                    return false;
                }
                return toString().equals(obj.toString());
            }
        };
        return r;
    }

}
