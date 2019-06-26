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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.docksnet.utils.lcom.LCOMNode;
import com.intellij.diagram.DiagramEdge;
import com.intellij.diagram.DiagramNode;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;

/**
 * @author Stefan Zeller
 */
public class LCOMConverter {

    private final ReferenceDiagramVfsResolver vfsResolver = new ReferenceDiagramVfsResolver();
    private final Map<String, LCOMNode> lcomNodeRegistry = new HashMap<>();

    /**
     * Returns nodes representating a directed graph regarding to given {@code nodes} and {@code edges}.
     */
    public Collection<LCOMNode> convert(Collection<? extends DiagramNode<PsiElement>> nodes, Collection<? extends
            DiagramEdge<PsiElement>> edges) {
        for (DiagramNode<PsiElement> node : nodes) {
            String fqn = this.vfsResolver.getQualifiedName(node.getIdentifyingElement());
            LCOMNode.Type type = resolveType(node);
            this.lcomNodeRegistry.put(fqn, new LCOMNode(fqn, type, (ReferenceNode) node));
        }

        for (DiagramEdge<PsiElement> edge : edges) {
            ReferenceNode source = (ReferenceNode) edge.getSource();
            ReferenceNode target = (ReferenceNode) edge.getTarget();
            String sourceFqn = this.vfsResolver.getQualifiedName(source.getIdentifyingElement());
            String targetFqn = this.vfsResolver.getQualifiedName(target.getIdentifyingElement());

            if (isSourceOrTargetNotRegistered(sourceFqn, targetFqn)) {
                continue;
            }

            this.lcomNodeRegistry.get(sourceFqn).addCallee(this.lcomNodeRegistry.get(targetFqn));
        }

        return this.lcomNodeRegistry.values();
    }

    private boolean isSourceOrTargetNotRegistered(String sourceFqn, String targetFqn) {
        return this.lcomNodeRegistry.get(sourceFqn) == null || this.lcomNodeRegistry.get(targetFqn) == null;
    }

    public LCOMNode.Type resolveType(DiagramNode<PsiElement> referenceNode) {
        PsiElementDispatcher<LCOMNode.Type> elementDispatcher = new PsiElementDispatcher<LCOMNode.Type>() {

            @Override
            public LCOMNode.Type processClass(PsiClass psiClass) {
                return LCOMNode.Type.Class;
            }

            @Override
            public LCOMNode.Type processMethod(PsiMethod psiMethod) {
                if (psiMethod.isConstructor()) {
                    return LCOMNode.Type.Constructur;
                } else {
                    return LCOMNode.Type.Method;
                }
            }

            @Override
            public LCOMNode.Type processField(PsiField psiField) {
                if (psiField.hasModifierProperty("static")) {
                    return LCOMNode.Type.Constant;
                } else {
                    return LCOMNode.Type.Field;
                }
            }

            @Override
            public LCOMNode.Type processClassInitializer(PsiClassInitializer psiClassInitializer) {
                return LCOMNode.Type.ClassInitializer;
            }

            @Override
            public LCOMNode.Type processInnerClass(PsiClass innerClass) {
                return LCOMNode.Type.InnerClass;
            }

            @Override
            public LCOMNode.Type processStaticInnerClass(PsiClass staticInnerClass) {
                return LCOMNode.Type.StaticInnerClass;
            }

            @Override
            public LCOMNode.Type processEnum(PsiClass anEnum) {
                return LCOMNode.Type.Enum;
            }

            @Override
            public LCOMNode.Type processPackage(PsiJavaDirectoryImpl aPackage) {
                throw new IllegalStateException("not implemented");
            }

            @Override
            public LCOMNode.Type processFile(PsiJavaFile psiElement) {
                throw new IllegalStateException("not implemented");
            }
        };
        return elementDispatcher.dispatch(referenceNode.getIdentifyingElement());
    }

}