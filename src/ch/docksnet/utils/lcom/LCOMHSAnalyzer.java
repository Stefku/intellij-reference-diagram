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
 * LCOM-HS Analyzer where
 * <ul>
 * <li>constructors are also consideres as methods and</li>
 * <li>constants are not considered as fields.</li>
 * </ul>
 *
 * @author Stefan Zeller
 */
public class LCOMHSAnalyzer extends AbstractLCOMHSAnalyzer {

    public LCOMHSAnalyzer(LCOMAnalyzerData data) {
        super(data);
    }

    @Override
    protected boolean isVariable(LCOMNode node) {
        switch (node.getType()) {
            case Field:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected boolean isMethod(LCOMNode node) {
        switch (node.getType()) {
            case Method:
            case Constructur:
                return true;
            default:
                return false;
        }
    }

}
