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

import com.intellij.diagram.DiagramProvider;
import com.intellij.diagram.PsiDiagramNode;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.uml.UmlIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Stefan Zeller
 */
public class ReferenceNode extends PsiDiagramNode<PsiElement> {

    private boolean marked;

    public ReferenceNode(PsiElement psiElement, DiagramProvider provider) {
        super(psiElement, provider);
    }

    @Nullable
    @Override
    public String getTooltip() {
        return null;
    }

    @Override
    public String toString() {
        return "ReferenceNode{} " + getIdentifyingElement();
    }

    @Override
    public Icon getIcon() {
        if (getIdentifyingElement() instanceof PsiMethod && ((PsiMethod) getIdentifyingElement()).isConstructor()) {
            return UmlIcons.Constructor;
        }
        return getElement().getIcon(Iconable.ICON_FLAG_VISIBILITY);
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void setMarked() {
        this.marked = true;
    }

    public void unsetMarked() {
        this.marked = false;
    }

    public void switchMarked() {
        if (isMarked()) {
            unsetMarked();
        } else {
            setMarked();
        }
    }

    @NotNull
    @Override
    public PsiElement getIdentifyingElement() {
        return super.getIdentifyingElement();
    }
}
