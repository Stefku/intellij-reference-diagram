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

package ch.docksnet.rgraph.directory;

import ch.docksnet.rgraph.PsiUtils;
import ch.docksnet.rgraph.ReferenceDiagramDataModel;
import ch.docksnet.rgraph.ReferenceDiagramProvider;
import ch.docksnet.rgraph.method.ReferenceEdge;
import ch.docksnet.rgraph.method.SourceTargetPair;
import ch.docksnet.utils.IncrementableSet;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;

public class PackageReferenceDiagramDataModel extends ReferenceDiagramDataModel {

    public PackageReferenceDiagramDataModel(Project project, PsiJavaDirectoryImpl directory) {
        super(project, ReferenceDiagramProvider.getInstance());
        init(directory);
    }

    private void init(PsiJavaDirectoryImpl directory) {
        for (PsiElement child : directory.getChildren()) {
            if (child instanceof PsiJavaFile) {
                addUserElement(child);
            } else if (child instanceof PsiJavaDirectoryImpl) {
                addUserElement(child);
            }
        }
    }

    @NotNull
    @Override
    public String getNodeName(DiagramNode<PsiElement> diagramNode) {
        return PsiUtils.getPresentableName(diagramNode.getIdentifyingElement());
    }

    protected synchronized void updateDataModel() {
        super.updateDataModel();
    }

    @Nullable
    @Override
    protected DiagramEdge<PsiElement> toEdge(@NotNull DiagramNode<PsiElement> from,
                                             @NotNull DiagramNode<PsiElement> to,
                                             Long value) {
        final DiagramRelationshipInfo relationship = createEdgeFromNonField(value == null ? 0 : value);
        return new ReferenceEdge(from, to, relationship);
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

    @Override
    protected boolean isAllowedToShow(PsiElement element) {
        return true;
    }

    @NotNull
    @Override
    protected IncrementableSet<SourceTargetPair> resolveRelationships() {
        IncrementableSet<SourceTargetPair> incrementableSet = new IncrementableSet<>();

        for (DiagramNode<PsiElement> node : getNodes()) {
            PsiElement callee = node.getIdentifyingElement();


            if (callee instanceof PsiJavaFile) {
                PsiClass[] classes = ((PsiJavaFile) callee).getClasses();
                for (PsiClass psiClass : classes) {
                    Collection<PsiReference> references = ReferencesSearch.search(psiClass, GlobalSearchScope.projectScope(getProject())).findAll();

                    for (PsiReference psiReference : references) {
                        if (!(psiReference instanceof CompositePsiElement)) {
                            continue;
                        }
                        PsiElement caller = ((CompositePsiElement) psiReference).getContainingFile();

                        if (caller == null) {
                            continue;
                        }

                        incrementableSet.increment(new SourceTargetPair(caller, callee));
                    }

                }
            }
        }
        return incrementableSet;
    }

    @Override
    public void rebuild(PsiElement element) {
        super.rebuild(element);
        clearAll();
        init((PsiJavaDirectoryImpl) element);
        refreshDataModel();
    }

}
