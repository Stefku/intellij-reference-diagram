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

package ch.docksnet;

import java.io.IOException;

import jdepend.framework.DependencyDefiner;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.junit.BeforeClass;
import org.junit.Test;

import static jdepend.framework.DependencyMatchers.hasNoCycles;
import static jdepend.framework.DependencyMatchers.matchesPackages;
import static org.junit.Assert.assertThat;

/**
 * @author Stefan Zeller
 */
public class DependencyTest {
    private static JDepend depend;

    @BeforeClass
    public static void init() throws IOException {
        depend = new JDepend(PackageFilter.all().including("ch.docksnet").excludingRest());
        depend.addDirectory("out/production/intellij-reference-diagram");
        depend.analyze();
    }

    @Test
    public void dependencies() {
        class ChDocksnet implements DependencyDefiner {
            JavaPackage rgraph, utils;

            public void dependUpon() {
                rgraph.dependsUpon(utils);
            }
        }
        assertThat(depend, matchesPackages(new ChDocksnet()));
    }

    @Test
    public void noCircularDependencies() {
        assertThat(depend, hasNoCycles());
    }

}
