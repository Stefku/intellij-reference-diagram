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

package ch.docksnet.rgraph.method.actions;

import ch.docksnet.rgraph.method.OuterReferences;
import ch.docksnet.rgraph.method.ReferenceDiagramDataModel;
import ch.docksnet.rgraph.toolwindow.ReferenceToolWindow;
import com.intellij.diagram.DiagramAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * @author Stefan Zeller
 */
public class ShowOuterReferencesAction extends DiagramAction {

    @Override
    public void perform(AnActionEvent e) {
        Project project = e.getProject();
        ApplicationManager.getApplication().invokeLater(
                () -> ToolWindowManager.getInstance(project).
                        getToolWindow(ReferenceToolWindow.ID)
                        .show(null)
        );
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    @Override
    public String getActionName() {
        return "show PkgRefs";
    }

    @Override
    public void update(AnActionEvent e) {
        if (getDataModel(e) instanceof ReferenceDiagramDataModel) {
            e.getPresentation().setVisible(true);
            e.getPresentation().setEnabled(false);
            OuterReferences outerReferences = ((ReferenceDiagramDataModel) getDataModel(e)).getOuterReferences();
            e.getPresentation().setText("PkgRefs: " + outerReferences.toToolbarString());
        } else {
            e.getPresentation().setVisible(false);
        }
        super.update(e);
    }

}
