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

package ch.docksnet.rgraph;

import java.util.List;

/**
 * @author Stefan Zeller
 */
public class OuterReferences {

    private ReferenceCount samePackage = new ReferenceCount();
    private ReferenceCount inHierarchy = new ReferenceCount();
    private ReferenceCount otherHierarchy = new ReferenceCount();

    public static OuterReferences empty() {
        return new OuterReferences();
    }

    public void update(FileFQN ownFile, FileFQN otherFile) {
        if (ownFile.equals(otherFile)) {
            return;
        }
        if (ownFile.samePackage(otherFile)) {
            this.samePackage.increment(otherFile);
        } else if (ownFile.sameHierarchy(otherFile)) {
            this.inHierarchy.increment(otherFile);
        } else {
            this.otherHierarchy.increment(otherFile);
        }
    }

    public List<String> getReferencesSamePackage() {
        return this.samePackage.referenceList();
    }

    public List<String> getReferencesSameHierarchy() {
        return this.inHierarchy.referenceList();
    }

    public List<String> getReferencesOtherHierarchy() {
        return this.otherHierarchy.referenceList();
    }

    public String toToolbarString() {
        return "" + this.samePackage.fileCount() + "/" + this.inHierarchy.fileCount() + "/" + this.otherHierarchy.fileCount();
    }
}
