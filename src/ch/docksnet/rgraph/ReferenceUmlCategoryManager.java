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

import com.intellij.diagram.AbstractDiagramNodeContentManager;
import com.intellij.diagram.DiagramCategory;
import com.intellij.diagram.presentation.DiagramState;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.LayeredIcon;
import icons.UmlIcons;

/**
 * @author Stefan Zeller
 */
public class ReferenceUmlCategoryManager extends AbstractDiagramNodeContentManager {
    public static final DiagramCategory STATIC_FIELDS;
    public static final DiagramCategory FIELDS;
    public static final DiagramCategory CONSTRUCTORS;
    public static final DiagramCategory STATIC_METHODS;
    public static final DiagramCategory METHODS;
    public static final DiagramCategory STATIC_CLASS_INITIALIZER;
    public static final DiagramCategory CLASS_INITIALIZER;
    private static final DiagramCategory[] CATEGORIES;

    public ReferenceUmlCategoryManager() {
    }

    public DiagramCategory[] getContentCategories() {
        return CATEGORIES;
    }

    public boolean isInCategory(Object element, DiagramCategory category, DiagramState presentation) {
        if (STATIC_FIELDS.equals(category)) {
            if (element instanceof PsiField) {
                if (((PsiField) element).hasModifierProperty("static")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (FIELDS.equals(category)) {
            if (element instanceof PsiField) {
                if (!((PsiField) element).hasModifierProperty("static")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (CONSTRUCTORS.equals(category)) {
            if (element instanceof PsiMethod) {
                if (((PsiMethod) element).isConstructor()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (METHODS.equals(category)) {
            if (element instanceof PsiMethod) {
                if (!((PsiMethod) element).isConstructor()) {
                    if (!((PsiMethod) element).hasModifierProperty("static")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        if (STATIC_METHODS.equals(category)) {
            if (element instanceof PsiMethod) {
                if (!((PsiMethod) element).isConstructor()) {
                    if (((PsiMethod) element).hasModifierProperty("static")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        if (CLASS_INITIALIZER.equals(category)) {
            if (element instanceof PsiClassInitializer) {
                if (!((PsiClassInitializer) element).hasModifierProperty("static")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (STATIC_CLASS_INITIALIZER.equals(category)) {
            if (element instanceof PsiClassInitializer) {
                if (((PsiClassInitializer) element).hasModifierProperty("static")) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    static {
        FIELDS = new DiagramCategory("Fields", AllIcons.Nodes.Field, true, true);

        METHODS = new DiagramCategory("Methods", AllIcons.Nodes.Method, true, true);

        CONSTRUCTORS = new DiagramCategory("Constructors", UmlIcons.Constructor, true, true);

        LayeredIcon staticField = new LayeredIcon(AllIcons.Nodes.Field, AllIcons.Nodes.StaticMark);
        STATIC_FIELDS = new DiagramCategory("Static Fields", staticField, true, true);

        LayeredIcon staticMethod = new LayeredIcon(AllIcons.Nodes.Method, AllIcons.Nodes.StaticMark);
        STATIC_METHODS = new DiagramCategory("Static Methods", staticMethod, true, true);

        CLASS_INITIALIZER = new DiagramCategory("Class Initializer", AllIcons.Nodes.ClassInitializer, true, true);

        LayeredIcon staticClassInitializer = new LayeredIcon(AllIcons.Nodes.ClassInitializer, AllIcons.Nodes.StaticMark);
        STATIC_CLASS_INITIALIZER = new DiagramCategory("Static Class Initializer", staticClassInitializer, true, true);

        CATEGORIES = new DiagramCategory[]{FIELDS, METHODS, CONSTRUCTORS, CLASS_INITIALIZER, STATIC_FIELDS, STATIC_METHODS,
                STATIC_CLASS_INITIALIZER};
    }

}
