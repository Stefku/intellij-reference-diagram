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

package ch.docksnet.rgraph.toolwindow;

import javax.swing.*;
import java.awt.*;


public class ReferenceListToolWindow {
    private JPanel myToolWindowContent;
    DefaultListModel listModel;

    public ReferenceListToolWindow() {
        this.myToolWindowContent = new JPanel(new BorderLayout());

        this.listModel = new DefaultListModel();
        JList list = new JList(this.listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        this.myToolWindowContent.add(listScrollPane, BorderLayout.CENTER);
    }


    public JPanel getContent() {
        return this.myToolWindowContent;
    }

    public void replaceContent(java.util.List<String> entries) {
        this.listModel.clear();
        for (String entry : entries) {
            this.listModel.addElement(entry);
        }
    }

}