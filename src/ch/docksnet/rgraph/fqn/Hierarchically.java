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

package ch.docksnet.rgraph.fqn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Hierarchically {
    default boolean samePackage(Hierarchically otherFile) {
        return getHierarchie().equals(otherFile.getHierarchie());
    }

    default boolean sameHierarchy(Hierarchically otherFile) {
        List<String> packagePathsOtherFile = Hierarchically.getPackagePaths(this);
        return packagePathsOtherFile.stream().anyMatch(it -> otherFile.getHierarchie().equals(it));
    }

    static List<String> getPackages(Hierarchically base) {
        String[] packages = base.getHierarchie().split("\\.");
        return new ArrayList<>(Arrays.asList(packages));
    }

    static List<String> getPackagePaths(Hierarchically base) {
        List<String> result = new ArrayList<>();
        List<String> packages = getPackages(base);
        for (int i = 0; i < packages.size() + 1; i++) {
            List<String> subList = packages.subList(0, i);
            if (subList.isEmpty()) {
                continue;
            }
            result.add(String.join(".", subList));
        }
        return result;
    }

    String getHierarchie();

    default String getNextHierarchyTowards(Hierarchically target) {
        if (!target.sameHierarchy(this)) {
            throw new IllegalArgumentException("target is not in same hierarchy");
        }
        String targetHierarchie = target.getHierarchie();
        String substring = targetHierarchie.substring(getHierarchie().length() + 1);
        int indexOfDot = substring.indexOf(".");
        if (indexOfDot >= 0) {
            return targetHierarchie.substring(0, getHierarchie().length() + indexOfDot +1);
        } else {
            return targetHierarchie;
        }
    }

}
