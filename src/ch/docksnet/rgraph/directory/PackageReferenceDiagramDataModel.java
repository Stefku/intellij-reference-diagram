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
import ch.docksnet.rgraph.ReferenceDiagramProvider;
import ch.docksnet.rgraph.method.ReferenceEdge;
import ch.docksnet.rgraph.method.ReferenceNode;
import ch.docksnet.rgraph.method.SourceTargetPair;
import ch.docksnet.utils.IncrementableSet;
import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramProvider;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static ch.docksnet.rgraph.PsiUtils.getFqn;

public class PackageReferenceDiagramDataModel extends DiagramDataModel<PsiElement> {

    private final Map<String, SmartPsiElementPointer<PsiElement>> elementsAddedByUser = new HashMap();
    private final Map<String, SmartPsiElementPointer<PsiElement>> elementsRemovedByUser = new HashMap();
    private final Map<PsiElement, DiagramNode<PsiElement>> nodesPool = new HashMap<>();
    private final SmartPointerManager spManager;

    private final Collection<DiagramNode<PsiElement>> nodes = new HashSet<>();
    private final Collection<DiagramEdge<PsiElement>> edges = new HashSet<>();

    public PackageReferenceDiagramDataModel(Project project, PsiJavaDirectoryImpl directory) {
        super(project, ReferenceDiagramProvider.getInstance());
        this.spManager = SmartPointerManager.getInstance(getProject());
        init(directory);
    }

    private void init(PsiJavaDirectoryImpl directory) {
        for (PsiElement child : directory.getChildren()) {
            if (child instanceof PsiJavaFile) {
                this.elementsAddedByUser.put(getFqn(child), this.spManager.createSmartPsiElementPointer(child));
            }
        }
    }

    @NotNull
    @Override
    public Collection<? extends DiagramNode<PsiElement>> getNodes() {
        if (this.nodes == null) {
            throw new IllegalStateException("@NotNull method %s.%s must not return null");
        } else {
            return this.nodes;
        }
    }

    @NotNull
    @Override
    public Collection<? extends DiagramEdge<PsiElement>> getEdges() {
        if (this.edges == null) {
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null",
                    new Object[]{"com/intellij/uml/java/JavaUmlDataModel", "getEdges"}));
        } else {
            return this.edges;
        }
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
    public void refreshDataModel() {
        clearAll();
        updateDataModel();
    }

    private void clearAll() {
        this.nodes.clear();
        this.edges.clear();
    }

    private synchronized void updateDataModel() {
        DiagramProvider provider = getBuilder().getProvider();
        Set<PsiElement> elements = getElements();

        for (PsiElement element : elements) {
            if (isAllowedToShow(element)) {
                this.nodes.add(getReferenceNode(provider, element));
            }
        }

        IncrementableSet<SourceTargetPair> relationships = resolveRelationships();
        for (Map.Entry<SourceTargetPair, Long> sourceTargetPair : relationships.elements()) {
            SourceTargetPair key = sourceTargetPair.getKey();
            DiagramNode<PsiElement> source = findNode(key.getSource().getContainingFile());
            DiagramNode<PsiElement> target = findNode(key.getTarget());
            if (source != null && target != null) {
                this.edges.add(toEdge(source, target, sourceTargetPair.getValue()));
            }
        }
    }

    private Set<PsiElement> getElements() {
        Set<PsiElement> result = new HashSet<>();

        for (SmartPsiElementPointer<PsiElement> psiElementPointer : this.elementsAddedByUser.values()) {
            PsiElement element = psiElementPointer.getElement();
            result.add(element);
        }

        for (SmartPsiElementPointer<PsiElement> psiElementPointer : this.elementsRemovedByUser.values()) {
            PsiElement element = psiElementPointer.getElement();
            result.remove(element);
        }

        return result;
    }

    @NotNull
    private ReferenceNode getReferenceNode(DiagramProvider provider, PsiElement element) {
        if (this.nodesPool.containsKey(element)) {
            return (ReferenceNode) this.nodesPool.get(element);
        }
        ReferenceNode node = new ReferenceNode(element, provider);
        this.nodesPool.put(element, node);
        return node;
    }

    @Nullable
    private DiagramEdge<PsiElement> toEdge(final @NotNull DiagramNode<PsiElement> from, final @NotNull DiagramNode<PsiElement> to,
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

    private boolean isAllowedToShow(PsiElement element) {
        return true;
    }

    @NotNull
    private IncrementableSet<SourceTargetPair> resolveRelationships() {
        IncrementableSet<SourceTargetPair> incrementableSet = new IncrementableSet<>();

        for (DiagramNode<PsiElement> node : this.nodes) {
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

    @NotNull
    @Override
    public ModificationTracker getModificationTracker() {
        return PsiManager.getInstance(getProject()).getModificationTracker();
    }

    @Override
    public void dispose() {
    }

    @Override
    public void rebuild(PsiElement element) {
        super.rebuild(element);
        this.elementsRemovedByUser.clear();
        clearAll();
        init((PsiJavaDirectoryImpl) element);
        refreshDataModel();
    }

    @Override
    public boolean hasElement(PsiElement element) {
        return findNode(element) != null;
    }

    /**
     * @param psiElement
     * @return {@code true} if {@code nodes} contains {@code psiElement}.
     */
    @Nullable
    private DiagramNode<PsiElement> findNode(PsiElement psiElement) {
        Iterator ptr = (new ArrayList(this.nodes)).iterator();

        while (ptr.hasNext()) {
            DiagramNode node = (DiagramNode) ptr.next();
            String fqn = PsiUtils.getFqn((PsiElement) node.getIdentifyingElement());
            if (fqn != null && fqn.equals(PsiUtils.getFqn(psiElement))) {
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean isPsiListener() {
        return true;
    }
}
