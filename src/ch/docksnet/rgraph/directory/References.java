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
import ch.docksnet.rgraph.fqn.FQN;
import ch.docksnet.rgraph.fqn.Hierarchically;
import ch.docksnet.rgraph.method.SourceTargetPair;
import ch.docksnet.utils.IncrementableSet;
import com.intellij.diagram.DiagramNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class References {
    public List<PsiElement> createNodes(PsiJavaDirectoryImpl directory) {
        List<PsiElement> result = new ArrayList<>();
        for (PsiElement child : directory.getChildren()) {
            if (child instanceof PsiJavaFile) {
                result.add(child);
            } else if (child instanceof PsiJavaDirectoryImpl) {
                result.add(child);
            }
        }
        return result;
    }


    public IncrementableSet<SourceTargetPair> createRelationships(Collection<? extends DiagramNode<PsiElement>> nodes, Project project) {
        IncrementableSet<SourceTargetPair> incrementableSet = new IncrementableSet<>();

        for (DiagramNode<PsiElement> node : nodes) {
            PsiElement callee = node.getIdentifyingElement();


            if (callee instanceof PsiJavaFile) {
                PsiClass[] classes = ((PsiJavaFile) callee).getClasses();
                for (PsiClass psiClass : classes) {
                    Collection<PsiReference> references = ReferencesSearch.search(psiClass, GlobalSearchScope.projectScope(project)).findAll();

                    for (PsiReference psiReference : references) {
                        if (!(psiReference instanceof CompositePsiElement)) {
                            continue;
                        }
                        if (((CompositePsiElement) psiReference).getParent() instanceof PsiImportStatement) {
                            // don't count import statements
                            continue;
                        }
                        PsiElement caller = ((CompositePsiElement) psiReference).getContainingFile();

                        if (caller == null) {
                            continue;
                        }

                        FQN callerFqn = PsiUtils.getFqn(caller);

                        if (callerFqn instanceof Hierarchically) {
                            Hierarchically calleeH = (Hierarchically) PsiUtils.getFqn(callee);
                            Hierarchically callerH = (Hierarchically) callerFqn;
                            if (calleeH.samePackage(callerH)) {
                                incrementableSet.increment(new SourceTargetPair(caller, callee));
                            } else if (callerH.sameHierarchy(calleeH)) {
                                String accumulationPackage = calleeH.getNextHierarchyTowards(callerH);
                                PsiElement accumulator = PsiUtils.getPsiJavaDirectory(accumulationPackage, project);
                                incrementableSet.increment(new SourceTargetPair(accumulator, callee));
                            }
                        }
                    }

                }
            }
        }
        return incrementableSet;
    }

}
