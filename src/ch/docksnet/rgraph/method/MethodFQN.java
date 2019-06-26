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

package ch.docksnet.rgraph.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Zeller
 */
public class MethodFQN {
    public static final Pattern METHOD_PATTERN = Pattern.compile("(.*)#(.*)\\((.*)\\)");
    private final String className;
    private final String methodName;
    private final List<String> parameters;
    private final String fqn;

    private MethodFQN(String className, String methodName, List<String> parameters) {
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
        this.fqn = createFqn();
    }

    @NotNull
    private String createFqn() {
        final String parameterString = createParameterRepresentation(this.parameters);
        return this.className + "#" + this.methodName + "(" + parameterString + ")";
    }

    @NotNull
    public static String createParameterRepresentation(List<String> parameters) {
        return StringUtil.join(parameters, ",");
    }

    @NotNull
    public static MethodFQN create(String string) {
        Matcher matcher = METHOD_PATTERN.matcher(string);

        if (!matcher.find()) {
            throw new IllegalArgumentException("String does not match the pattern: " + string);
        }

        String className = matcher.group(1);
        String methodName = matcher.group(2);
        String parameterString = matcher.group(3);

        String[] parameters = parameterString.split(",");

        return new MethodFQN(className, methodName, Arrays.asList(parameters));
    }

    public static MethodFQN create(PsiMethod psiMethod) {
        String classFqn = ClassFQN.create(psiMethod.getContainingClass()).getFQN();
        String methodName = psiMethod.getName();
        Builder builder = new Builder(classFqn, methodName);

        List<String> parameters = getParameterArray(psiMethod);

        for (String parameter : parameters) {
            builder.addParameter(parameter);
        }

        return builder.create();
    }

    @NotNull
    public static List<String> getParameterArray(PsiMethod psiMethod) {
        List<String> parameters = new ArrayList<>();
        for (PsiParameter psiParameter : psiMethod.getParameterList().getParameters()) {
            String parameter = psiParameter.getType().getPresentableText();
            parameters.add(parameter);
        }
        return parameters;
    }

    public static boolean isMethodFQN(String string) {
        Matcher matcher = METHOD_PATTERN.matcher(string);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    public String getClassName() {
        return this.className;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<String> getParameters() {
        return this.parameters;
    }

    public String getFQN() {
        return this.fqn;
    }

    public static class Builder {
        private final String className;
        private final String methodName;
        private List<String> parameters = new ArrayList<>();

        public Builder(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }

        public Builder addParameter(String parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public MethodFQN create() {
            return new MethodFQN(this.className, this.methodName, Collections.unmodifiableList(this.parameters));
        }

    }

}
