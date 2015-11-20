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

import java.text.DecimalFormat;

import com.intellij.diagram.DiagramAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author Stefan Zeller
 */
public class ShowLcomHsAction extends DiagramAction {

    private static DecimalFormat decimalFormat = new DecimalFormat("0.##");

    @Override
    public void perform(AnActionEvent anActionEvent) {
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    @Override
    public String getActionName() {
        return "Show LCOM-HS";
    }

    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(false);
        if (getDataModel(e) instanceof ReferenceDiagramDataModel) {
            double currentLcomHsValue = ((ReferenceDiagramDataModel) getDataModel(e)).getLcomHsValue();
            e.getPresentation().setText("LCOM-HS: " + decimalFormat.format(currentLcomHsValue));
        } else {
            e.getPresentation().setText("LCOM-HS: calculate");
        }
        super.update(e);
    }

}
