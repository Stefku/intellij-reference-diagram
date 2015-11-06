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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.intellij.diagram.DiagramAction;
import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.DiagramProvider;
import com.intellij.diagram.DiagramRelationshipInfo;
import com.intellij.diagram.DiagramRelationshipInfoAdapter;
import com.intellij.diagram.presentation.DiagramLineType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.uml.utils.UmlJavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ch.docksnet.rgraph.PsiUtils.getFqn;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramDataModel extends DiagramDataModel<PsiElement> {

    private final Map<String, SmartPsiElementPointer<PsiElement>> elementsAddedByUser = new HashMap();
    private final Map<String, SmartPsiElementPointer<PsiElement>> elementsRemovedByUser = new HashMap();

    private final Collection<DiagramNode<PsiElement>> myNodes = new HashSet<>();
    private final Collection<DiagramEdge<PsiElement>> myEdges = new HashSet<>();
    private final Collection<DiagramEdge<PsiElement>> myDependencyEdges = new HashSet<>();
    private final Collection<DiagramNode<PsiElement>> myNodesOld = new HashSet();
    private final Collection<DiagramEdge<PsiElement>> myEdgesOld = new HashSet();
    private final Collection<DiagramEdge<PsiElement>> myDependencyEdgesOld = new HashSet();

    private final SmartPointerManager spManager;
    private SmartPsiElementPointer<PsiClass> myInitialElement;

    public ReferenceDiagramDataModel(Project project, PsiClass psiClass) {
        super(project, ReferenceDiagramProvider.getInstance());
        spManager = SmartPointerManager.getInstance(getProject());
        init(psiClass);
    }

    /**
     * Populates elementsAddedByUser with members of given PsiClass
     */
    private void init(PsiClass psiClass) {
        myInitialElement = psiClass == null ? null : spManager.createSmartPsiElementPointer
                (psiClass);
        collectNodes(psiClass);
    }

    public void collectNodes(PsiClass psiClass) {
        for (PsiMethod psiMethod : psiClass.getMethods()) {
            elementsAddedByUser.put(getFqn(psiMethod), spManager.createSmartPsiElementPointer(
                    (PsiElement) psiMethod));
        }

        for (PsiField psiField : psiClass.getFields()) {
            elementsAddedByUser.put(getFqn(psiField), spManager.createSmartPsiElementPointer(
                    (PsiElement) psiField));
        }

        for (PsiClassInitializer psiClassInitializer : psiClass.getInitializers()) {
            elementsAddedByUser.put(getFqn(psiClassInitializer), spManager.createSmartPsiElementPointer(
                    (PsiElement) psiClassInitializer));
        }
    }

    @NotNull
    @Override
    public Collection<? extends DiagramNode<PsiElement>> getNodes() {
        if (myNodes == null) {
            throw new IllegalStateException("@NotNull method %s.%s must not return null");
        } else {
            return myNodes;
        }
    }

    @NotNull
    @Override
    public Collection<? extends DiagramEdge<PsiElement>> getEdges() {
        if (myDependencyEdges.isEmpty()) {
            Collection var10000 = myEdges;
            if (myEdges == null) {
                throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null",
                        new Object[]{"com/intellij/uml/java/JavaUmlDataModel", "getEdges"}));
            } else {
                return var10000;
            }
        } else {
            HashSet allEdges = new HashSet(myEdges);
            allEdges.addAll(myDependencyEdges);
            return allEdges;
        }
    }

    @NotNull
    @Override
    public String getNodeName(DiagramNode<PsiElement> diagramNode) {
        return PsiUtils.getPresentableName(diagramNode.getIdentifyingElement());
    }

    @Nullable
    @Override
    public DiagramEdge<PsiElement> createEdge(final @NotNull DiagramNode<PsiElement> from, final @NotNull
    DiagramNode<PsiElement> to) {

        final String commandName = "TEST";

        Callable callable = new Callable() {
            public ReferenceEdge call() throws Exception {
                final DiagramRelationshipInfo relationship;
                if (from instanceof PsiField) {
                    relationship = createEdgeFromField();
                } else {
                    // TODO where to get count?
                    relationship = createEdgeFromNonField(0);
                }

                return new ReferenceEdge(from, to, relationship);
            }
        };

        return (DiagramEdge) DiagramAction.performCommand(getBuilder(), callable, commandName,
                (String) null, new PsiElement[]{((PsiElement) from.getIdentifyingElement())
                        .getContainingFile()});
    }

    @Override
    public void removeNode(DiagramNode<PsiElement> node) {
        removeElement((PsiElement) node.getIdentifyingElement());
    }

    private void removeElement(PsiElement element) {
        DiagramNode node = findNode(element);
        if (node == null) {
            elementsAddedByUser.remove(PsiUtils.getFqn(element));
        } else {
            PsiElement toRemove = (PsiElement) node.getIdentifyingElement();
            myNodes.remove(node);
            elementsRemovedByUser.put(PsiUtils.getFqn(element), spManager.createSmartPsiElementPointer(toRemove));
            elementsAddedByUser.remove(PsiUtils.getFqn(element));
            removeAllEdgesFromOrTo(node);
        }
    }

    private void removeAllEdgesFromOrTo(DiagramNode node) {
        // TODO
    }

    @Override
    public void removeEdge(DiagramEdge<PsiElement> edge) {
        // TODO
    }

    @Override
    public void refreshDataModel() {
        clearAll();
        // TODO handle Categories
        updateDataModel();
    }

    @NotNull
    public ModificationTracker getModificationTracker() {
        return PsiManager.getInstance(getProject()).getModificationTracker();
    }

    private void clearAll() {
        clearAndBackup(myNodes, myNodesOld);
        clearAndBackup(myEdges, myEdgesOld);
        clearAndBackup(myDependencyEdges, myDependencyEdgesOld);
    }

    private static <T> void clearAndBackup(Collection<T> target, Collection<T> backup) {
        backup.clear();
        backup.addAll(target);
        target.clear();
    }

    public synchronized void updateDataModel() {
        // TODO add nodes and edges
        DiagramProvider provider = getBuilder().getProvider();
        Set<PsiElement> elements = getElements();

        for (PsiElement element : elements) {
            if (isAllowedToShow(element)) {
                myNodes.add(new ReferenceNode(element, provider));
            }
        }

        // TODO: for what these backups?
        mergeWithBackup(myNodes, myNodesOld);
        mergeWithBackup(myEdges, myEdgesOld);
        mergeWithBackup(myDependencyEdges, myDependencyEdgesOld);

    }

    private Set<PsiElement> getElements() {
        Set<PsiElement> result = new HashSet<>();

        for (SmartPsiElementPointer<PsiElement> psiElementPointer : elementsAddedByUser.values()) {
            PsiElement element = psiElementPointer.getElement();
            result.add(element);
        }

        for (SmartPsiElementPointer<PsiElement> psiElementPointer : elementsRemovedByUser.values()) {
            PsiElement element = psiElementPointer.getElement();
            result.remove(element);
        }

        return result;
    }

    private boolean isAllowedToShow(PsiElement psiElement) {
        if (psiElement != null && psiElement.isValid()) {
            // TODO DiagramScopeManager scopeManager1 = this.getScopeManager();
            return true;
        }
        return false;
    }

    private static <T> void mergeWithBackup(Collection<T> target, Collection<T> backup) {
        Iterator<T> i$ = backup.iterator();

        while (i$.hasNext()) {
            T t = i$.next();
            if (target.contains(t)) {
                target.remove(t);
                target.add(t);
            }
        }
    }

    public void dispose() {
    }

    @Nullable
    @Override
    public DiagramNode<PsiElement> addElement(PsiElement psiElement) {
        // TODO
        return null;
    }

    public boolean hasElement(PsiElement element) {
        return findNode(element) != null;
    }

    @Nullable
    public DiagramNode<PsiElement> findNode(PsiElement psiElement) {
        Iterator ptr = (new ArrayList(myNodes)).iterator();

        while (ptr.hasNext()) {
            DiagramNode node = (DiagramNode) ptr.next();
            String fqn = PsiUtils.getFqn((PsiElement) node.getIdentifyingElement());
            if (fqn != null && fqn.equals(PsiUtils.getFqn(psiElement))) {
                return node;
            }
        }

        return null;
    }

    public boolean isDependencyDiagramSupported() {
        return true;
    }

    public void collapseNode(DiagramNode<PsiElement> node) {
        // TODO
    }

    public void expandNode(DiagramNode<PsiElement> node) {
        // TODO
    }

    public boolean isPsiListener() {
        return true;
    }

    public void rebuild(PsiElement element) {
        super.rebuild(element);
        elementsRemovedByUser.clear();
        clearAll();
        myNodesOld.clear();
        myEdgesOld.clear();
        myDependencyEdgesOld.clear();
        init((PsiClass) element);
        refreshDataModel();
    }

    //    @NotNull
    //    public IncrementableSet<SourceTargetPair> resolveRelationships(PsiClass psiClass) {
    //        IncrementableSet<SourceTargetPair> incrementableSet = new IncrementableSet<>();
    //
    //        for (ReferenceNode node : nodes) {
    //            PsiElement callee = node.getIdentifyingElement();
    //
    //            Collection<PsiReference> all = ReferencesSearch.search(callee, new LocalSearchScope
    //                    (psiClass)).findAll();
    //
    //            for (PsiReference psiReference : all) {
    //                if (!(psiReference instanceof PsiReferenceExpression)) {
    //                    continue;
    //                }
    //                PsiReferenceExpression referenceExpression = (PsiReferenceExpression) psiReference;
    //                PsiElement caller = PsiUtils.getRootPsiElement(psiClass, referenceExpression);
    //
    //                if (caller == null) {
    //                    continue;
    //                }
    //
    //                incrementableSet.increment(new SourceTargetPair(caller, callee));
    //            }
    //        }
    //        return incrementableSet;
    //    }

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

}
