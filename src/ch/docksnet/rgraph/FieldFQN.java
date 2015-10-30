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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.psi.PsiField;

/**
 * @author Stefan Zeller
 */
public class FieldFQN {
    private final String className;
    private final String fieldName;
    private final String fqn;

    private FieldFQN(String className, String fieldName) {
        this.className = className;
        this.fieldName = fieldName;
        this.fqn = createFqn();
    }

    private String createFqn() {
        return className + "#" + fieldName;
    }

    public static FieldFQN create(String string) {
        Pattern pattern = Pattern.compile("(.*)#(.*)");
        Matcher matcher = pattern.matcher(string);

        matcher.find();

        String className = matcher.group(1);
        String fieldName = matcher.group(2);

        return new FieldFQN(className, fieldName);
    }

    public String getClassName() {
        return className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFQN() {
        return fqn;
    }

    public static FieldFQN create(PsiField psiField) {
        String qualifiedClassName = psiField.getContainingClass().getQualifiedName();
        return new FieldFQN(qualifiedClassName, psiField.getName());
    }

    public static boolean isFieldFQN(String fqn) {
        if (fqn.contains("#") && !fqn.contains("(")) {
            return true;
        }
        return false;
    }

    public static class Builder {
        private final String className;
        private final String fieldName;

        public Builder(String className, String fieldName) {
            this.className = className;
            this.fieldName = fieldName;
        }

        public FieldFQN create() {
            return new FieldFQN(className, fieldName);
        }

    }

}
