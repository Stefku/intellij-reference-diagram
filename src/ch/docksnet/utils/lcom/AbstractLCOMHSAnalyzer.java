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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * LCOM-HS:  LCOM modification proposed by Henderson-Sellers [1] [2].
 * <p/>
 * [1]: Henderson-Sellers, B., Constantine, L., Graham, I. Coupling and Cohesion: towards a valid metrics suite
 * for object-oriented analysis and design, Object-oriented Systems, Vol. 3(3), 1996, pp. 143–158.
 * [2]: Henderson-Sellers, B. Object-oriented metrics: measures of complexity. Prentice-Hall, 1996, pp.142-147.
 *
 * @author Stefan Zeller
 */
public abstract class AbstractLCOMHSAnalyzer {

    protected final LCOMAnalyzerData data;

    public AbstractLCOMHSAnalyzer(LCOMAnalyzerData data) {
        this.data = data;
    }

    public BigDecimal analyze() {
        int methodCount = countMethods();
        LCOMNode[] variables = getVariables();
        int variableCount = variables.length;

        int sum = 0;

        for (LCOMNode variable : variables) {
            sum += countMethodsAccessingVariable(variable);
        }

        BigDecimal result = new BigDecimal(sum)
                .divide(new BigDecimal(variableCount))
                .subtract(new BigDecimal(methodCount))
                .divide(new BigDecimal(1 - methodCount));

        return result;
    }

    private int countMethods() {
        int result = 0;
        for (LCOMNode lcomNode : data.getNodeRegistry().values()) {
            if (isMethod(lcomNode)) {
                result += 1;
            }
        }
        return result;
    }

    private LCOMNode[] getVariables() {
        List<LCOMNode> result = new ArrayList<>();
        for (LCOMNode lcomNode : data.getNodeRegistry().values()) {
            if (isVariable(lcomNode)) {
                result.add(lcomNode);
            }
        }
        return (LCOMNode[]) result.toArray();
    }

    private int countMethodsAccessingVariable(LCOMNode variable) {
        int result = 0;
        for (LCOMNode lcomNode : variable.getCallers()) {
            if (isMethod(lcomNode)) {
                result += 1;
            }
        }
        return result;
    }

    protected abstract boolean isVariable(LCOMNode node);

    protected abstract boolean isMethod(LCOMNode node);

}
