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

package ch.docksnet.rgraph;

import ch.docksnet.rgraph.directory.PackageReferenceDiagramDataModel;
import ch.docksnet.rgraph.method.MethodReferenceDiagramDataModel;
import com.intellij.diagram.BaseDiagramProvider;
import com.intellij.diagram.DiagramCategory;
import com.intellij.diagram.DiagramColorManager;
import com.intellij.diagram.DiagramDataModel;
import com.intellij.diagram.DiagramElementManager;
import com.intellij.diagram.DiagramNodeContentManager;
import com.intellij.diagram.DiagramPresentationModel;
import com.intellij.diagram.DiagramProvider;
import com.intellij.diagram.DiagramVfsResolver;
import com.intellij.diagram.extras.DiagramExtras;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Stefan Zeller
 */
@SuppressWarnings("MethodDoesntCallSuperMethod")
public class ReferenceDiagramProvider extends BaseDiagramProvider<PsiElement> {

    private static final String ID = "ReferenceDiagramProvider";
    private final DiagramElementManager<PsiElement> myElementManager = new ReferenceDiagramElementManager();
    private final DiagramVfsResolver<PsiElement> myVfsResolver = new ReferenceDiagramVfsResolver();
    private final ReferenceDiagramExtras myExtras = new ReferenceDiagramExtras();
    private final DiagramColorManager myColorManager = new ReferenceDiagramColorManager();
    private final ReferenceUmlCategoryManager myUmlCategoryManager = new ReferenceUmlCategoryManager();

    @Pattern("[a-zA-Z0-9_-]*")
    @Override
    public String getID() {
        return ID;
    }

    @Override
    public DiagramElementManager<PsiElement> getElementManager() {
        return this.myElementManager;
    }

    @Override
    public DiagramVfsResolver<PsiElement> getVfsResolver() {
        return this.myVfsResolver;
    }

    @Override
    public String getPresentableName() {
        return "Java Reference Diagram";
    }

    @NotNull
    @Override
    public DiagramExtras<PsiElement> getExtras() {
        return this.myExtras;
    }

    @Override
    public DiagramDataModel createDataModel(@NotNull Project project, @Nullable PsiElement
            psiElement, @Nullable VirtualFile virtualFile, DiagramPresentationModel model) {
        if (psiElement instanceof PsiClass) {
            return new MethodReferenceDiagramDataModel(project, (PsiClass) psiElement);
        }
        if (psiElement instanceof PsiJavaDirectoryImpl) {
            return new PackageReferenceDiagramDataModel(project, (PsiJavaDirectoryImpl) psiElement);
        }
        return null;
    }

    @Override
    public DiagramColorManager getColorManager() {
        return this.myColorManager;
    }

    public static ReferenceDiagramProvider getInstance() {
        return (ReferenceDiagramProvider) DiagramProvider.<PsiElement>findByID(ID);
    }

    @Override
    public DiagramNodeContentManager getNodeContentManager() {
        return this.myUmlCategoryManager;
    }

    @Override
    public @NotNull DiagramNodeContentManager createNodeContentManager() {
        return new ReferenceUmlCategoryManager();
    }

    @Override
    public @NotNull DiagramCategory[] getAllContentCategories() {
        return ReferenceUmlCategoryManager.CATEGORIES;
    }
}
