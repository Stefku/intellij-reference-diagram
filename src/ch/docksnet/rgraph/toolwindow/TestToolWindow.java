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

import ch.docksnet.rgraph.PsiUtils;
import ch.docksnet.rgraph.method.OuterReferences;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.impl.ContentImpl;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TestToolWindow extends JPanel {
    private static final String TOOL_WINDOW_ID = "References";
    private final Tree myTree;

    private TestToolWindow(Project project) {
        this.myTree = new Tree(new DefaultTreeModel(new DefaultMutableTreeNode()));
        this.myTree.setRootVisible(false);
        this.myTree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree,
                                              Object value,
                                              boolean selected,
                                              boolean expanded,
                                              boolean leaf,
                                              int row,
                                              boolean hasFocus) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                final Object userObject = node.getUserObject();
                if (userObject == null) {
                    return;
                }
                if (userObject instanceof PsiJavaFileImpl) {
                    VirtualFile virtualFile = ((PsiJavaFileImpl) ((DefaultMutableTreeNode) value).getUserObject()).getVirtualFile();
                    VirtualFileCellRenderer.render(this, virtualFile);
                } else if (userObject instanceof String) {
                    append((String) userObject, SimpleTextAttributes.GRAY_ATTRIBUTES);
                } else {
                    append(userObject.toString());
                }
            }
        });
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                Tree theTree = (Tree) event.getSource();
                if (event.getClickCount() == 2) {
                    TreePath pathForLocation = theTree.getPathForLocation(event.getPoint().x, event.getPoint().y);
                    Object o = pathForLocation.getLastPathComponent();
                    if (!(o instanceof DefaultMutableTreeNode)) {
                        return;
                    }
                    navigate((DefaultMutableTreeNode) o, project);
                }
            }
        };
        this.myTree.addMouseListener(mouseListener);

        KeyListener keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                Tree theTree = (Tree) event.getSource();
                if (event.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    Object o = theTree.getSelectionPath().getLastPathComponent();
                    if (!(o instanceof DefaultMutableTreeNode)) {
                        return;
                    }
                    navigate((DefaultMutableTreeNode) o, project);
                }
            }
        };
        this.myTree.addKeyListener(keyListener);
        setLayout(new BorderLayout());
        add(new JBScrollPane(this.myTree));
    }

    private void navigate(DefaultMutableTreeNode node, Project project) {
        if (node.getUserObject() instanceof PsiElement) {
            PsiUtils.navigate((PsiElement) node.getUserObject(), project);
        }
    }

    public static void show(Project project, OuterReferences outerReferences) {
        final TestToolWindow view = createViewTab(project, outerReferences.getBaseElement());
        final DefaultMutableTreeNode node = outerReferences.asTree();
        ((DefaultTreeModel) view.myTree.getModel()).setRoot(node);
    }

    private static TestToolWindow createViewTab(Project project, PsiElement baseElement) {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow(TOOL_WINDOW_ID,
                    true,
                    ToolWindowAnchor.BOTTOM);
        }
        final TestToolWindow view = new TestToolWindow(project);
        ToolWindow finalToolWindow = toolWindow;
        toolWindow.activate(() -> {
            final String text = SymbolPresentationUtil.getSymbolPresentableText(baseElement);
            final ContentImpl content = new ContentImpl(view, "to " + text, true);
            finalToolWindow.getContentManager().addContent(content);
            finalToolWindow.getContentManager().setSelectedContent(content, true);
        });
        return view;
    }
}
