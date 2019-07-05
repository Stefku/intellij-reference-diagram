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

import ch.docksnet.rgraph.fqn.FileFQN;
import ch.docksnet.rgraph.fqn.FileFQNReference;
import ch.docksnet.rgraph.fqn.Hierarchically;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * @author Stefan Zeller
 */
public class OuterReferences {

    private ReferenceCount samePackage = new ReferenceCount();
    private ReferenceCount inHierarchy = new ReferenceCount();
    private ReferenceCount otherHierarchy = new ReferenceCount();
    private final PsiElement baseElement;

    public OuterReferences(PsiElement baseElement) {
        this.baseElement = baseElement;
    }

    public static OuterReferences empty() {
        return new OuterReferences(null);
    }

    public void update(Hierarchically ownHierarchy, FileFQN otherFile) {
        if (ownHierarchy.equals(otherFile)) {
            return;
        }
        if (ownHierarchy.samePackage(otherFile)) {
            this.samePackage.increment(otherFile);
        } else if (ownHierarchy.sameHierarchy(otherFile)) {
            this.inHierarchy.increment(otherFile);
        } else {
            this.otherHierarchy.increment(otherFile);
        }
    }

    public List<FileFQNReference> getReferencesSamePackage() {
        return this.samePackage.referenceList();
    }

    public List<FileFQNReference> getReferencesSameHierarchy() {
        return this.inHierarchy.referenceList();
    }

    public List<FileFQNReference> getReferencesOtherHierarchy() {
        return this.otherHierarchy.referenceList();
    }

    public String toToolbarString() {
        return "" + this.samePackage.fileCount() + "/" + this.inHierarchy.fileCount() + "/" + this.otherHierarchy.fileCount();
    }

    public DefaultMutableTreeNode asTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(createSub("Same Package", this.getReferencesSamePackage()));
        root.add(createSub("Same Hierarchy", this.getReferencesSameHierarchy()));
        root.add(createSub("Other Hierarchy", this.getReferencesOtherHierarchy()));
        return root;
    }

    @NotNull
    private DefaultMutableTreeNode createSub(String s, List<FileFQNReference> content) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(s);
        content.forEach(it -> node.add(new DefaultMutableTreeNode(it.getPsiElement())));
        return node;
    }

    public PsiElement getBaseElement() {
        return this.baseElement;
    }

}
