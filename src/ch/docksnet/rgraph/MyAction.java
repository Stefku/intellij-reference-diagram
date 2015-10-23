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

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.pom.Navigatable;
import com.intellij.pom.NavigatableWithText;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.util.OpenSourceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
class MyAction extends AnAction implements DumbAware {

    private final boolean a;

    protected MyAction(boolean var1) {
        this.a = var1;
    }

    public void actionPerformed(AnActionEvent var1) {
        DataContext var2 = var1.getDataContext();
        OpenSourceUtil.navigate(this.a, this.getNavigatables(var2));
    }

    public void update(AnActionEvent var1) {
        boolean var2 = ActionPlaces.isPopupPlace(var1.getPlace());
        Navigatable var3 = this.a(var1.getDataContext());
        boolean var4 = var3 != null;
        var1.getPresentation().setVisible((var4 || !var2) && (this.a || !(var3 instanceof
                NavigatableWithText)));
        var1.getPresentation().setEnabled(var4);
        String var5 = this.a && var3 instanceof NavigatableWithText ? ((NavigatableWithText) var3)
                .getNavigateActionText(true) : null;
        var1.getPresentation().setText(var5 == null ? this.getTemplatePresentation().getText() : var5);
    }

    @Nullable
    private Navigatable a(@NotNull DataContext var1) {
        Navigatable[] var2 = this.getNavigatables(var1);
        if (var2 == null) {
            return null;
        } else {
            Navigatable[] var3 = var2;
            int var4 = var2.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Navigatable var6 = var3[var5];
                if (var6.canNavigate()) {
                    return (Navigatable) (var6 instanceof PomTargetPsiElement ? ((PomTargetPsiElement)
                            var6).getTarget() : var6);
                }
            }

            return null;
        }
    }

    @Nullable
    protected Navigatable[] getNavigatables(DataContext var1) {
        return (Navigatable[]) CommonDataKeys.NAVIGATABLE_ARRAY.getData(var1);
    }

}
