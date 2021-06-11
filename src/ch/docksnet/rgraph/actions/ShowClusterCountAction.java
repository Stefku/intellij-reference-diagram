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

package ch.docksnet.rgraph.actions;

import ch.docksnet.rgraph.ReferenceDiagramDataModel;
import com.intellij.diagram.DiagramAction;
import com.intellij.diagram.DiagramBuilder;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Zeller
 */
public class ShowClusterCountAction extends DiagramAction {

    @Override
    public void perform(AnActionEvent e) {
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    @Override
    public String getActionName() {
        return "Show Cluster Count";
    }

    @Override
    public void update(AnActionEvent e) {
        if (getDataModel(e) instanceof ReferenceDiagramDataModel) {
            e.getPresentation().setVisible(true);
            e.getPresentation().setEnabled(false);
            long currentClusterCount = ((ReferenceDiagramDataModel) getDataModel(e)).getCurrentClusterCount();
            e.getPresentation().setText("Cluster Count: " + currentClusterCount);
        } else {
            e.getPresentation().setVisible(false);
        }
        super.update(e);
    }

}
