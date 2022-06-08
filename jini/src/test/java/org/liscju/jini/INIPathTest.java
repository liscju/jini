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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests for {@link INIPath}.
 */
public class INIPathTest {

    // Matcher tests

    /**
     * Properties matcher with null ini path.
     */
    @Test
    public void testMatcherNull() {
        INIPath.Matcher matcher = INIPath.of(null).matcher();

        assertTrue(matcher.matches(null));
        assertTrue(matcher.matches(""));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("owner."));
        assertFalse(matcher.matches(".owner."));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
    }

    /**
     * Properties matcher with empty ini path.
     */
    @Test
    public void testMatcherEmpty() {
        INIPath.Matcher matcher = INIPath.of("").matcher();

        assertTrue(matcher.matches(null));
        assertTrue(matcher.matches(""));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
    }

    /**
     * Properties matcher with ini path matching everything.
     */
    @Test
    public void testMatcherAll() {
        INIPath.Matcher matcher = INIPath.of("**").matcher();

        assertTrue(matcher.matches(null));
        assertTrue(matcher.matches(""));
        assertTrue(matcher.matches("author"));
        assertTrue(matcher.matches(".author"));
        assertTrue(matcher.matches("modified"));
        assertTrue(matcher.matches(".modified"));
        assertTrue(matcher.matches("owner.name"));
        assertTrue(matcher.matches(".owner.name"));
        assertTrue(matcher.matches(".database.storage.name"));
        assertTrue(matcher.matches("database.storage.name"));
    }

    /**
     * Properties matcher with ini path matching properties in default section.
     */
    @Test
    public void testMatcherAllInDefault() {
        INIPath.Matcher matcher = INIPath.of("*").matcher();

        assertTrue(matcher.matches(null));
        assertTrue(matcher.matches(""));
        assertTrue(matcher.matches("author"));
        assertTrue(matcher.matches(".author"));
        assertTrue(matcher.matches("modified"));
        assertTrue(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
    }

    /**
     * Properties matcher with ini path matches all properties in default
     * section starting with a dot.
     */
    @Test
    public void testMatcherAllDotDefault() {
        INIPath.Matcher matcher = INIPath.of(".*").matcher();

        assertTrue(matcher.matches(null));
        assertTrue(matcher.matches(""));
        assertTrue(matcher.matches("author"));
        assertTrue(matcher.matches(".author"));
        assertTrue(matcher.matches("modified"));
        assertTrue(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
    }

    /**
     * Properties matcher with ini path matching single property.
     */
    @Test
    public void testMatcherSingleProp() {
        INIPath.Matcher matcher = INIPath.of("author").matcher();

        assertFalse(matcher.matches(null));
        assertFalse(matcher.matches(""));
        assertTrue(matcher.matches("author"));
        assertTrue(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
    }

    /**
     * Properties matcher with ini path matches all properties starting in
     * specific section.
     */
    @Test
    public void testMatcherStartFromSection() {
        INIPath.Matcher matcher = INIPath.of("database.storage.*").matcher();

        assertFalse(matcher.matches(null));
        assertFalse(matcher.matches(""));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertTrue(matcher.matches(".database.storage.name"));
        assertTrue(matcher.matches("database.storage.name"));
        assertTrue(matcher.matches(".database.storage.mount"));
        assertTrue(matcher.matches("database.storage.mount"));
    }

    /**
     * Properties matcher with ini path wildcard (**) matches all nested
     * subsections.
     */
    @Test
    public void testMatcherMatchesNestedSubsections() {
        INIPath.Matcher matcher = INIPath.of(".database.**").matcher();

        assertFalse(matcher.matches(null));
        assertFalse(matcher.matches(""));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertTrue(matcher.matches(".database.storage.name"));
        assertTrue(matcher.matches("database.storage.name"));
        assertTrue(matcher.matches(".database.storage.mount"));
        assertTrue(matcher.matches("database.storage.mount"));
    }

    /**
     * Properties matcher with ini path wildcard(*) matches all properties
     * in section.
     */
    @Test
    public void testMatcherMatchesPropertiesInSection() {
        INIPath.Matcher matcher = INIPath.of(".database.*").matcher();

        assertFalse(matcher.matches(null));
        assertFalse(matcher.matches(""));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertFalse(matcher.matches("owner.name"));
        assertFalse(matcher.matches(".owner.name"));
        assertTrue(matcher.matches("database.name"));
        assertTrue(matcher.matches(".database.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
        assertFalse(matcher.matches(".database.storage.mount"));
        assertFalse(matcher.matches("database.storage.mount"));
    }

    /**
     * Properties matcher with ini path matches property in any subsections.
     */
    @Test
    public void testMatcherPropertyInAnySection() {
        INIPath.Matcher matcher = INIPath.of("**.name").matcher();

        assertFalse(matcher.matches(null));
        assertFalse(matcher.matches(""));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertTrue(matcher.matches("name"));
        assertTrue(matcher.matches(".name"));
        assertFalse(matcher.matches("surname"));
        assertFalse(matcher.matches(".surname"));
        assertTrue(matcher.matches("owner.name"));
        assertTrue(matcher.matches(".owner.name"));
        assertTrue(matcher.matches(".database.storage.name"));
        assertTrue(matcher.matches("database.storage.name"));
        assertFalse(matcher.matches(".database.storage.mount"));
        assertFalse(matcher.matches("database.storage.mount"));
    }

    /**
     * Properties matcher with ini path matches property name ending with
     * specified suffix in any subsections.
     */
    @Test
    public void testMatcherPropertyEndsWithName() {
        INIPath.Matcher matcher = INIPath.of("**name").matcher();

        assertFalse(matcher.matches(null));
        assertFalse(matcher.matches(""));
        assertTrue(matcher.matches("name"));
        assertTrue(matcher.matches(".name"));
        assertFalse(matcher.matches("author"));
        assertFalse(matcher.matches(".author"));
        assertFalse(matcher.matches("modified"));
        assertFalse(matcher.matches(".modified"));
        assertTrue(matcher.matches("surname"));
        assertTrue(matcher.matches(".surname"));
        assertTrue(matcher.matches("owner.name"));
        assertTrue(matcher.matches(".owner.name"));
        assertTrue(matcher.matches(".database.storage.name"));
        assertTrue(matcher.matches("database.storage.name"));
        assertFalse(matcher.matches(".database.storage.mount"));
        assertFalse(matcher.matches("database.storage.mount"));
    }

    /**
     * Properties matcher with ini path matches property in any subsections.
     */
    @Test
    public void testMatcherProperty() {
        INIPath.Matcher matcher = INIPath.of("*.*").matcher();

        assertTrue(matcher.matches(null));
        assertTrue(matcher.matches(""));
        assertTrue(matcher.matches("author"));
        assertTrue(matcher.matches(".author"));
        assertTrue(matcher.matches("modified"));
        assertTrue(matcher.matches(".modified"));
        assertTrue(matcher.matches("surname"));
        assertTrue(matcher.matches(".surname"));
        assertTrue(matcher.matches("owner.name"));
        assertTrue(matcher.matches(".owner.name"));
        assertFalse(matcher.matches(".database.storage.name"));
        assertFalse(matcher.matches("database.storage.name"));
        assertFalse(matcher.matches(".database.storage.mount"));
        assertFalse(matcher.matches("database.storage.mount"));
    }

}
