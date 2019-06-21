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

import ch.docksnet.rgraph.FileFQNReference;
import ch.docksnet.rgraph.PsiUtils;
import com.intellij.ide.util.gotoByName.GotoFileCellRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.components.JBList;
import com.intellij.util.AstLoadingFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ReferenceListToolWindow {
    private JPanel myToolWindowContent;
    private DefaultListModel listModel;
    private final String name;

    ReferenceListToolWindow(String name, Project project) {
        this.name = name;
        this.myToolWindowContent = new JPanel(new BorderLayout());

        this.listModel = new DefaultListModel();
        JList<Object> myList = new JBList<>(this.listModel);
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myList.setLayoutOrientation(JList.VERTICAL);
        myList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(myList);
        this.myToolWindowContent.add(listScrollPane, BorderLayout.CENTER);

        // kind of stolen from com.intellij.ide.util.gotoByName.ChooseByNameBase
        myList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> AstLoadingFilter.disallowTreeLoading(
                () -> new GotoFileCellRenderer(500).getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        ));

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList<String> theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Object o = theList.getModel().getElementAt(index);
                        if (o instanceof PsiElement) {
                            PsiUtils.navigate((PsiElement) o, project);
                        }
                    }
                }
            }
        };
        myList.addMouseListener(mouseListener);
    }


    JPanel getContent() {
        return this.myToolWindowContent;
    }

    public void replaceContent(java.util.List<FileFQNReference> entries) {
        // TODO update name of tab
        this.listModel.clear();
        for (FileFQNReference entry : entries) {
            this.listModel.addElement(entry.getPsiElement());
        }
    }

    String getName() {
        return this.name + " (" + this.listModel.size() + ")";
    }

}