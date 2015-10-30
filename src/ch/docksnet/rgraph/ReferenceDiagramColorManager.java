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

import java.awt.*;

import com.intellij.diagram.DiagramColorManagerBase;
import com.intellij.diagram.DiagramEdge;
import com.intellij.ui.JBColor;

/**
 * @author Stefan Zeller
 */
public class ReferenceDiagramColorManager extends DiagramColorManagerBase {

    @Override
    public Color getEdgeColor(DiagramEdge edge) {
        final String edgeType = edge.getRelationship().toString();
        if (ReferenceEdge.Type.FIELD_TO_METHOD.name().equals(edgeType)) {
            return new JBColor(new Color(9, 128, 0), new Color(83, 128, 103));
        }
        if (ReferenceEdge.Type.REFERENCE.name().equals(edgeType)) {
            return new JBColor(new Color(0, 26, 128), new Color(140, 177, 197));
        }
        return super.getEdgeColor(edge);
    }

}
