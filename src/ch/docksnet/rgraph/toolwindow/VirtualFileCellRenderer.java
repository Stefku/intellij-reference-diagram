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

import ch.docksnet.rgraph.fqn.FileFQNReference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VFileProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.LayeredIcon;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.IconUtil;
import com.intellij.util.PlatformIcons;

import javax.swing.*;
import java.awt.*;

import static com.intellij.openapi.fileChooser.FileElement.isFileHidden;
import static com.intellij.openapi.util.IconLoader.getTransparentIcon;

public class VirtualFileCellRenderer {
    private static final Color HIDDEN = SimpleTextAttributes.DARK_TEXT.getFgColor();

    public static void render(SimpleColoredComponent renderer, FileFQNReference ref, Project project) {
        VirtualFile virtualFile = ref.getPsiElement().getContainingFile().getVirtualFile();
        PsiJavaFile psiFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(virtualFile);
        int style = SimpleTextAttributes.STYLE_PLAIN;
        Color color = SimpleTextAttributes.LINK_BOLD_ATTRIBUTES.getFgColor();
        Icon icon = getIcon(virtualFile);
        String comment = null;
        if (!virtualFile.isValid()) style |= SimpleTextAttributes.STYLE_STRIKEOUT;
        boolean fileHidden = isFileHidden(virtualFile);
        if (fileHidden) {
            color = HIDDEN;
        } ;
        renderer.setIcon(!fileHidden || icon == null ? icon : getTransparentIcon(icon));
        SimpleTextAttributes attributes = new SimpleTextAttributes(style, color);
        renderer.append(psiFile.getPackageName() + "." + psiFile.getName(), attributes);
        renderer.append(" " + ref.toUsageString(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, SimpleTextAttributes.GRAY_ATTRIBUTES.getFgColor()));
        if (comment != null) renderer.append(comment, attributes);
    }

    private static Icon getIcon(final VirtualFile file) {
        return dressIcon(file, IconUtil.getIcon(file, Iconable.ICON_FLAG_READ_STATUS, null));
    }

    private static Icon dressIcon(final VirtualFile file, final Icon baseIcon) {
        return file.isValid() && file.is(VFileProperty.SYMLINK) ? new LayeredIcon(baseIcon, PlatformIcons.SYMLINK_ICON) : baseIcon;
    }
}
