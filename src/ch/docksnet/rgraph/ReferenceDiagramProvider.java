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

import ch.docksnet.utils.PreConditionUtil;
import com.intellij.diagram.BaseDiagramProvider;
import com.intellij.diagram.DiagramColorManager;
import com.intellij.diagram.DiagramElementManager;
import com.intellij.diagram.DiagramPresentationModel;
import com.intellij.diagram.DiagramProvider;
import com.intellij.diagram.DiagramVfsResolver;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramProvider extends BaseDiagramProvider<PsiElement> {

    public static final String ID = "ReferenceDiagramProvider";
    private DiagramElementManager<PsiElement> myElementManager = new ReferenceDiagramElementManager();
    private DiagramVfsResolver<PsiElement> myVfsResolver = new ReferenceDiagramVfsResolver();
    private ReferenceDiagramExtras myExtras = new ReferenceDiagramExtras();
    private DiagramColorManager myColorManager = new ReferenceDiagramColorManager();

    @Pattern("[a-zA-Z0-9_-]*")
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public DiagramElementManager<PsiElement> getElementManager() {
        return myElementManager;
    }

    @Override
    public DiagramVfsResolver<PsiElement> getVfsResolver() {
        return myVfsResolver;
    }

    @Override
    public String getPresentableName() {
        return "Method Reference Diagram";
    }

    @NotNull
    @Override
    public DiagramExtras<PsiElement> getExtras() {
        return myExtras;
    }

    @Override
    public ReferenceDiagramDataModel createDataModel(@NotNull Project project, @Nullable PsiElement
            psiElement, @Nullable VirtualFile virtualFile, DiagramPresentationModel model) {
        PreConditionUtil.assertTrue(psiElement instanceof PsiClass, "PsiElement" +
                ".psiElement must be a PsiClass");
        return new ReferenceDiagramDataModel(project, (PsiClass) psiElement);
    }

    @Override
    public DiagramColorManager getColorManager() {
        return myColorManager;
    }

    public static ReferenceDiagramProvider getInstance() {
        return (ReferenceDiagramProvider) DiagramProvider.findByID(ID);
    }

}
