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

/**
 * @author Stefan Zeller
 */
public class OuterReferences {

    private int samePackage;
    private int inHierarchy;
    private int otherHierarchy;

    public static OuterReferences empty() {
        return new OuterReferences();
    }

    public void update(FileFQN ownFile, FileFQN otherFile) {
        if (ownFile.equals(otherFile)) {
            return;
        }
        if (ownFile.samePackage(otherFile)) {
            samePackage += 1;
        } else if (ownFile.sameHierarchy(otherFile)) {
            inHierarchy += 1;
        } else {
            otherHierarchy += 1;
        }
    }

    private boolean samePackage(String ownPackageName, String otherPackageName) {
        return ownPackageName.equals(otherPackageName);
    }

    private boolean sameHierarchiy(String packageName, String otherPackageName) {
        return packageName.startsWith(otherPackageName);
    }

    public String toToolbarString() {
        return "" + this.samePackage + "/" + this.inHierarchy + "/" + this.otherHierarchy;
    }
}
