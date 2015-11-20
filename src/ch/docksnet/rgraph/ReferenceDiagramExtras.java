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

import java.util.ArrayList;
import java.util.List;

import com.intellij.diagram.DiagramBuilder;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramExtras extends DiagramExtras<PsiElement> {

    @Nullable
    @Override
    public Object getData(String dataId, List<DiagramNode<PsiElement>> nodes, DiagramBuilder builder) {
        if (nodes.size() == 1) {
            if (CommonDataKeys.PSI_ELEMENT.is(dataId)) {
                PsiElement psiElement = nodes.get(0).getIdentifyingElement();
                return psiElement;
            }
        }
        return super.getData(dataId, nodes, builder);
    }

    @Override
    public List<AnAction> getExtraActions() {
        final List<AnAction> result = new ArrayList<>();
        result.add(new ShowClusterCountAction());
        result.add(new ShowLcomHsAction());
        return result;
    }

}