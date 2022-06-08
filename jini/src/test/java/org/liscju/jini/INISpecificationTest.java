/*
 * JINI - Tool for manipulating configuration in INI files.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.liscju.jini;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Tests for {@link INISpecification}.
 */
public class INISpecificationTest {

    /**
     * Specification without requirements is satisfied by any INI file.
     */
    @Test
    public void testNoRequirements() {
        INI ini = new INI();
        INISection section = new INISection(".owner");
        section.getProperties().add(new INIProperty(".name", "Mary"));
        ini.getSections().add(section);

        INISpecification spec = INISpecification.define().create();

        spec.verify(ini);
    }

    /**
     * Specification that section name must conform to pattern.
     */
    @Test
    public void testSectionPattern() {
        INI ini = new INI();
        INISection section = new INISection(".owner");
        section.getProperties().add(new INIProperty(".name", "Mary"));
        ini.getSections().add(section);

        INISpecification spec =
            INISpecification.define()
                .section(Pattern.compile("[^.]*"))
                .create();

        try {
            spec.verify(ini);
        } catch (INISpecViolationException ex) {
            assertEquals("Section '.owner' has illegal name", ex.getDetail());
        }
    }

    /**
     * Specification that pattern name must conform to pattern.
     */
    @Test
    public void testPropertyPattern() {
        INI ini = new INI();
        INISection section = new INISection(".owner");
        section.getProperties().add(new INIProperty(".name", "Mary"));
        ini.getSections().add(section);

        INISpecification spec =
            INISpecification.define()
                .property(Pattern.compile("[^.]*"))
                .create();

        try {
            spec.verify(ini);
        } catch (INISpecViolationException ex) {
            assertEquals("Property '.name' has illegal name", ex.getDetail());
        }
    }

}