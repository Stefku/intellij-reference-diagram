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

package ch.docksnet.utils.lcom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefan Zeller
 */
public class CalleesSubgraphAnalyzer {

    private final LCOMAnalyzerData data;

    public CalleesSubgraphAnalyzer(LCOMAnalyzerData data) {
        this.data = data;
    }

    public List<LCOMNode> getCallees(LCOMNode root) {
        final List<LCOMNode> result = new ArrayList<>();
        visitCallees(root, result);
        return result;
    }

    private void visitCallees(LCOMNode node, List<LCOMNode> visited) {
        if (isNotVisited(node)) {
            markVisited(node);
            visited.add(node);
            for (LCOMNode callee : node.getCallees()) {
                visitCallees(callee, visited);
            }
        }
    }

    private boolean isNotVisited(LCOMNode node) {
        return !isVisited(node);
    }

    private Boolean isVisited(LCOMNode node) {
        return data.getNodeMarker().get(node.getFqn());
    }

    private void markVisited(LCOMNode node) {
        data.getNodeMarker().put(node.getFqn(), true);
    }

}
