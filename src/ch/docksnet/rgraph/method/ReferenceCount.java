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

import ch.docksnet.rgraph.fqn.FileFQN;
import ch.docksnet.rgraph.fqn.FileFQNReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferenceCount {
    private final Map<FileFQN, Integer> references = new HashMap<>();

    public void increment(FileFQN fileFQN) {
        if (!this.references.containsKey(fileFQN)) {
            this.references.put(fileFQN, 0);
        }
        this.references.put(fileFQN, this.references.get(fileFQN) + 1);
    }

    public List<FileFQNReference> referenceList() {
        List<FileFQNReference> list = new ArrayList<>();
        for (FileFQN it : this.references.keySet()) {
            int count = this.references.get(it);
            list.add(new FileFQNReference(it, count));
        }
        return list;
    }

    public int fileCount() {
        return this.references.size();
    }
}
