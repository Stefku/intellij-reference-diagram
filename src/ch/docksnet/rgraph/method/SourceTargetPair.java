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

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Stefan Zeller
 */
public class SourceTargetPair {
    private final PsiElement source;
    private final PsiElement target;

    public SourceTargetPair(@NotNull PsiElement source, @NotNull PsiElement target) {
        this.source = source;
        this.target = target;
    }

    public
    @NotNull
    PsiElement getSource() {
        return this.source;
    }

    public
    @NotNull
    PsiElement getTarget() {
        return this.target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SourceTargetPair that = (SourceTargetPair) o;

        if (!this.source.equals(that.source)) {
            return false;
        }
        if (!this.target.equals(that.target)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.source.hashCode();
        result = 31 * result + this.target.hashCode();
        return result;
    }

}
