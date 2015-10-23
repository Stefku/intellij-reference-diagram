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

package ch.docksnet.utils;

import java.util.HashMap;

/**
 * @author Stefan Zeller
 */
public class IncrementableSet<T> {

    private final HashMap<T, Long> map = new HashMap<>();

    public long get(T element) {
        if (map.containsKey(element)) {
            return map.get(element);
        } else {
            return 0;
        }
    }

    public void increment(T element) {
        if (map.containsKey(element)) {
            long count = map.get(element);
            count += 1;
            map.put(element, count);
        } else {
            map.put(element, 1L);
        }
    }

}
