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

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.LineBorder;

import ch.docksnet.rgraph.actions.DeleteMarkedAction;
import ch.docksnet.rgraph.actions.IsolateMarkedAction;
import ch.docksnet.rgraph.actions.MarkAction;
import ch.docksnet.rgraph.actions.MarkCalleesAction;
import ch.docksnet.rgraph.actions.MarkCallersAction;
import ch.docksnet.rgraph.actions.ShowOuterReferencesAction;
import ch.docksnet.rgraph.actions.ShowClusterCountAction;
import ch.docksnet.rgraph.actions.UnmarkAction;
import ch.docksnet.rgraph.actions.UnmarkAllAction;
import com.intellij.diagram.DiagramBuilder;
import com.intellij.diagram.DiagramNode;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
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
                assert psiElement != null: "psiElement has no identifying element: " + psiElement;
                return psiElement;
            }
        }
        return super.getData(dataId, nodes, builder);
    }

    @Override
    public List<AnAction> getExtraActions() {
        final List<AnAction> result = new ArrayList<>();
        result.add(new ShowOuterReferencesAction());
        result.add(new ShowClusterCountAction());
        result.add(new MarkAction());
        result.add(new UnmarkAction());
        result.add(new UnmarkAllAction());
        result.add(new MarkCalleesAction());
        result.add(new MarkCallersAction());
        result.add(new DeleteMarkedAction());
        result.add(new IsolateMarkedAction());
        return result;
    }

    @NotNull
    @Override
    public JComponent createNodeComponent(DiagramNode<PsiElement> node, DiagramBuilder builder, Point basePoint, JPanel wrapper) {
        JComponent nodeComponent = super.createNodeComponent(node, builder, basePoint, wrapper);

        if (node instanceof ReferenceNode) {
            if (((ReferenceNode) node).isMarked()) {
                nodeComponent.setBorder(new LineBorder(Color.BLUE, 2, true));
            }
        }

        return nodeComponent;
    }

}