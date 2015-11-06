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

/**
 * Count independend clusters of a graph. Two clusters are independend of each other, if there is no connection
 * between them. I.e. Two clusters of methods, fields that have no dependency between each other.
 *
 * @author Stefan Zeller
 */
public class ClusterAnalyzer {

    private final LCOMAnalyzerData data;

    public ClusterAnalyzer(LCOMAnalyzerData data) {
        this.data = data;
    }

    public long countCluster() {
        long result = 0;
        for (LCOMNode node : data.getNodeRegistry().values()) {
            if (isNotVisited(node)) {
                result += 1;
                visitCluster(node);
            }
        }
        return result;
    }

    private void visitCluster(LCOMNode node) {
        if (isNotVisited(node)) {
            markVisited(node);
            for (LCOMNode caller : node.getCallers()) {
                visitCluster(caller);
            }
            for (LCOMNode callee : node.getCallees()) {
                visitCluster(callee);
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
