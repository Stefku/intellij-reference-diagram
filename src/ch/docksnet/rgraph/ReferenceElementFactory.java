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

import java.util.HashMap;
import java.util.Map;

import com.intellij.psi.PsiElement;

/**
 * @author Stefan Zeller
 */
public class ReferenceElementFactory {

    private static Map<String, ReferenceElement> elements = new HashMap<>();

    public static ReferenceElement createInstance(PsiElement psiElement) {
        ReferenceElement referenceElement = new ReferenceElement(psiElement);
        elements.put(referenceElement.getName(), referenceElement);
        return referenceElement;
    }

    public static ReferenceElement getElement(String name) {
        if (elements.containsKey(name)) {
            return elements.get(name);
        }
        throw new IllegalStateException("element is not available: " + name);
    }

}
