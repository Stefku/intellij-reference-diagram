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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Stefan Zeller
 */
public class IncrementableSetTest {

    private IncrementableSet<String> sut;

    @Before
    public void setUp() throws Exception {
        sut = new IncrementableSet<>();
    }

    @Test
    public void can_create() throws Exception {
    }

    @Test
    public void get_zero_when_element_has_not_been_added() throws Exception {
        long result = sut.get("test");

        assertEquals(0, result);
    }

    @Test
    public void get_one_when_added_once() throws Exception {
        sut.increment("test");
        long result = sut.get("test");

        assertEquals(1, result);
    }

}