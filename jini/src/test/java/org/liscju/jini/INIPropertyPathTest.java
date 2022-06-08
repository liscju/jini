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

/**
 * Tests for {@link INIPropertyPath}.
 */
public class INIPropertyPathTest {

    /**
     * Property path is null.
     */
    @Test
    public void testNull() {
        INIPropertyPath ppath = INIPropertyPath.of(null);

        assertEquals(".", ppath.getSection());
        assertNull(ppath.getProperty());
    }

    /**
     * Property path is empty.
     */
    @Test
    public void testEmpty() {
        INIPropertyPath ppath = INIPropertyPath.of("");

        assertEquals(".", ppath.getSection());
        assertNull(ppath.getProperty());
    }

    /**
     * Property path is default section.
     */
    @Test
    public void testDefault() {
        INIPropertyPath ppath = INIPropertyPath.of(".");

        assertEquals(".", ppath.getSection());
        assertNull(ppath.getProperty());
    }

    /**
     * Property path starts with repeated ".".
     */
    @Test
    public void testDefaultsRepeated() {
        INIPropertyPath ppath = INIPropertyPath.of("...");

        assertEquals(".", ppath.getSection());
        assertNull(ppath.getProperty());
    }

    /**
     * Property path addresses property in default section.
     */
    @Test
    public void testPropertyInDefault() {
        INIPropertyPath ppath = INIPropertyPath.of(".author");

        assertEquals(".", ppath.getSection());
        assertEquals("author", ppath.getProperty());
    }

    /**
     * Property path addresses property implicitly in default section.
     */
    @Test
    public void testImplicitDefault() {
        INIPropertyPath ppath = INIPropertyPath.of("author");

        assertEquals(".", ppath.getSection());
        assertEquals("author", ppath.getProperty());
    }

    /**
     * Property path addresses property in section starting with default (".")
     * section.
     */
    @Test
    public void testFromDefaultToSection() {
        INIPropertyPath ppath = INIPropertyPath.of(".owner.name");

        assertEquals("owner", ppath.getSection());
        assertEquals("name", ppath.getProperty());
    }

    /**
     * Property path starts from default section (".") and ends with
     * separator (".").
     */
    @Test
    public void testFromDefaultEndsWithSeparator() {
        INIPropertyPath ppath = INIPropertyPath.of(".owner.name.");

        assertEquals("owner.name", ppath.getSection());
        assertNull(ppath.getProperty());
    }

    /**
     * Property path ends with separator (".").
     */
    @Test
    public void testEndsWithSeparator() {
        INIPropertyPath ppath = INIPropertyPath.of("owner.name.");

        assertEquals("owner.name", ppath.getSection());
        assertNull(ppath.getProperty());
    }

    /**
     * Property path addresses property inside section.
     */
    @Test
    public void testPropertyInsideSection() {
        INIPropertyPath ppath = INIPropertyPath.of(".database.storage.mount");

        assertEquals("database.storage", ppath.getSection());
        assertEquals("mount", ppath.getProperty());
    }

}