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

import org.jetbrains.annotations.Nullable;

import com.intellij.diagram.AbstractDiagramElementManager;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleColoredText;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramElementManager extends AbstractDiagramElementManager<ReferenceElement> {

    @Nullable
    @Override
    public ReferenceElement findInDataContext(DataContext context) {

        if (CommonDataKeys.PSI_FILE.getData(context) == null) {
            return null;
        }

        if (CommonDataKeys.EDITOR.getData(context) == null) {
            return null;
        }

        PsiElement psiElement = CommonDataKeys.PSI_FILE.getData(context).findElementAt(CommonDataKeys.EDITOR
                .getData(context).getCaretModel().getOffset());

        PsiClass psiClass = PsiUtils.getClassFromHierarchy(psiElement);

        if (psiClass == null) {
            return null;
        }

        return ReferenceElementFactory.createInstance(psiClass);
    }

    @Override
    public boolean isAcceptableAsNode(Object o) {
        return o != null;
    }

    @Nullable
    @Override
    public String getElementTitle(ReferenceElement referenceElement) {
        return referenceElement.getName();
    }

    @Nullable
    @Override
    public SimpleColoredText getItemName(Object o, DiagramState state) {
        return null;
    }

    @Override
    public String getNodeTooltip(ReferenceElement referenceElement) {
        return null;
    }
}
