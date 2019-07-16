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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class HierarchicallyTest {
    @Test
    public void getNextHierarchyTowards() {
        PackageFQN a = PackageFQN.create("root.sub");
        PackageFQN b = PackageFQN.create("root.sub.another");
        PackageFQN c = PackageFQN.create("root.sub.more.evenmore");

        assertEquals("root.sub.another", a.getNextHierarchyTowards(b));
        assertEquals("root.sub.more", a.getNextHierarchyTowards(c));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNextHierarchyTowardsError1() {
        PackageFQN a = PackageFQN.create("root.sub");
        PackageFQN b = PackageFQN.create("root.sibling");
        a.getNextHierarchyTowards(b);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNextHierarchyTowardsError2() {
        PackageFQN a = PackageFQN.create("root.sub");
        PackageFQN b = PackageFQN.create("root.subling");
        a.getNextHierarchyTowards(b);
    }

    @Test
    public void sameHierarchy() {
        assertTrue(PackageFQN.create("root.sub.another").sameHierarchy(PackageFQN.create("root.sub.another")));
        assertTrue(PackageFQN.create("root.sub.another").sameHierarchy(PackageFQN.create("root.sub")));
        assertTrue(PackageFQN.create("root.sub.another").sameHierarchy(PackageFQN.create("root")));

        assertFalse(PackageFQN.create("root.sub.another").sameHierarchy(PackageFQN.create("root.another")));
        assertFalse(PackageFQN.create("root.sub.another").sameHierarchy(PackageFQN.create("root.sub.anotherrrr")));
    }

    @Test
    public void getPackagePaths() {
        List<String> packages = Hierarchically.getPackagePaths(PackageFQN.create("root.sub.another"));
        assertEquals(Arrays.asList("root", "root.sub", "root.sub.another"), packages);
    }

    @Test
    public void getPackages() {
        List<String> packages = Hierarchically.getPackages(PackageFQN.create("root.sub.another"));
        assertEquals(Arrays.asList("root", "sub", "another"), packages);
    }
}