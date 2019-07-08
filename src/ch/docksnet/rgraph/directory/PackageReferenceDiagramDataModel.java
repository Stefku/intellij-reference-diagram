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
import ch.docksnet.rgraph.fqn.FQN;
import ch.docksnet.rgraph.fqn.Hierarchically;
import ch.docksnet.rgraph.fqn.PackageFQN;
import ch.docksnet.rgraph.method.ReferenceEdge;
import ch.docksnet.rgraph.method.SourceTargetPair;
import ch.docksnet.utils.IncrementableSet;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import static ch.docksnet.rgraph.PsiUtils.getFqn;
import static ch.docksnet.rgraph.PsiUtils.getPsiJavaDirectory;

public class PackageReferenceDiagramDataModel extends ReferenceDiagramDataModel {

    private final PsiElement baseElement;

    public PackageReferenceDiagramDataModel(Project project, PsiJavaDirectoryImpl directory) {
        super(project, ReferenceDiagramProvider.getInstance());
        this.baseElement = directory;
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

    @Override
    public void rebuild(PsiElement element) {
        super.rebuild(element);
        clearAll();
        init((PsiJavaDirectoryImpl) element);
        refreshDataModel();
    }

    @Override
    protected PsiElement getBaseElement() {
        return this.baseElement;
    }

    @Override
    protected FQN getBaseForOuterReferences(PsiElement psiElement) {
        return PsiUtils.getFqn(psiElement);
    }

    @NotNull
    @Override
    protected Collection<PsiReference> resolveOuterReferences(PsiElement psiElement) {
        Collection<PsiReference> result = new ArrayList<>();
        if (!(psiElement instanceof PsiJavaFile)) {
            return result;
        }
        PsiClass[] classes = ((PsiJavaFile) psiElement).getClasses();
        for (PsiClass psiClass : classes) {
            result.addAll(ReferencesSearch.search(psiClass, GlobalSearchScopes.projectProductionScope(getProject())).findAll());
        }
        return result;
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
        //noinspection MethodDoesntCallSuperMethod
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
                        // TODO group all references from subpackage to one node named like the subpackage (also sub of sub)
//                        if (areInSamePackage(caller, callee)) {
//                            incrementableSet.increment(new SourceTargetPair(caller, callee));
//                        }
                    }

                }
            }
        }
        return incrementableSet;
    }

    private boolean areInSamePackage(PsiElement caller, PsiElement callee) {
        PackageFQN callerFqn = PackageFQN.create((PsiJavaDirectoryImpl) caller.getContainingFile().getContainingDirectory());
        PackageFQN calleeFqn = PackageFQN.create((PsiJavaDirectoryImpl) callee.getContainingFile().getContainingDirectory());
        return callerFqn.samePackage(calleeFqn);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    protected DiagramNode<PsiElement> createMissingNodeForExistingRelationship(PsiElement psiElement) {
        FQN fqn = getFqn(psiElement);
        if (fqn instanceof Hierarchically) {
            Hierarchically hierarchicallySource = (Hierarchically) fqn;
            if (hierarchicallySource.sameHierarchy((Hierarchically) getFqn(this.baseElement))) {
                String hierarchie = hierarchicallySource.getHierarchie();
                PsiDirectory psiJavaDirectory = getPsiJavaDirectory(hierarchie, getProject());
                return addElement(psiJavaDirectory);
            }
        }
        return null;
    }
}
