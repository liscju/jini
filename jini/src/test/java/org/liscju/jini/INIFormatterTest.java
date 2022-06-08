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
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link INIFormatter}.
 */
public class INIFormatterTest {

    /**
     * Instance under tests.
     */
    private INIFormatter instance;

    @Before
    public void setUp() {
        instance = new INIFormatter();
    }

    /**
     * Formatting null INI.
     */
    @Test
    public void testININull() {
        assertEquals("", instance.format(null));
    }

    /**
     * Formatting INI with empty content.
     */
    @Test
    public void testINIEmpty() {
        assertEquals("", instance.format(new INI()));
    }

    /**
     * Formatting INI containing section with null name.
     */
    @Test
    public void testSectionNameNull() {
        INI ini = new INI();
        INISection section = new INISection(null);
        section.getProperties().add(
            new INIProperty("modified", "1-April-2001")
        );
        ini.getSections().add(section);

        assertEquals("modified=1-April-2001", instance.format(ini));
    }

    /**
     * Formatting INI containing section with empty name.
     */
    @Test
    public void testSectionNameEmpty() {
        INI ini = new INI();
        INISection section = new INISection("");
        section.getProperties().add(
            new INIProperty("author", "Mary Jane")
        );
        ini.getSections().add(section);

        String expected =
            "[]\n" +
            "author=Mary Jane"
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI containing null property name.
     */
    @Test
    public void testPropertyNameNull() {
        INI ini = new INI();
        INISection section = new INISection();
        section.getProperties().add(
            new INIProperty(null, "Mary Jane")
        );
        ini.getSections().add(section);

        String expected =
            "=Mary Jane"
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI containing empty property name.
     */
    @Test
    public void testPropertyNameEmpty() {
        INI ini = new INI();
        INISection section = new INISection();
        section.getProperties().add(
            new INIProperty(null, "1-April-2001")
        );
        ini.getSections().add(section);

        String expected =
            "=1-April-2001"
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI containing null property value.
     */
    @Test
    public void testPropertyValueNull() {
        INI ini = new INI();
        INISection section = new INISection();
        section.getProperties().add(
            new INIProperty("street", null)
        );
        ini.getSections().add(section);

        String expected =
            "street="
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI containing empty property value.
     */
    @Test
    public void testPropertyValueEmpty() {
        INI ini = new INI();
        INISection section = new INISection();
        section.getProperties().add(
            new INIProperty("street", "")
        );
        ini.getSections().add(section);

        String expected =
            "street="
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI file containing property with null name and value.
     */
    @Test
    public void testPropertyNameNullValueNull() {
        INI ini = new INI();
        INISection section = new INISection();
        section.getProperties().add(
            new INIProperty(null, null)
        );
        ini.getSections().add(section);

        String expected =
            "="
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI file containing few properties in default section.
     */
    @Test
    public void testPropertiesDefaultSection() {
        INI ini = new INI();
        INISection section = new INISection();
        section.getProperties().add(
            new INIProperty("modified", "1-April-2001")
        );
        section.getProperties().add(
            new INIProperty("author", "Mary Jane")
        );
        section.getProperties().add(
            new INIProperty("street", "")
        );
        ini.getSections().add(section);

        String expected =
            "modified=1-April-2001\n" +
            "author=Mary Jane\n" +
            "street="
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI files with multiple properties in section.
     */
    @Test
    public void testPropertiesInSection() {
        INI ini = new INI();
        INISection storage = new INISection("database.storage");
        storage.getProperties().add(
            new INIProperty("mount", "/home/ora/fs")
        );
        storage.getProperties().add(
            new INIProperty("server", "192.0.5.132")
        );
        ini.getSections().add(storage);

        String expected =
            "[database.storage]\n"   +
            "mount=/home/ora/fs\n"   +
            "server=192.0.5.132"
            ;
        assertEquals(expected, instance.format(ini));
    }

    /**
     * Formatting INI files with multiple properties in sections.
     */
    @Test
    public void testPropertiesInSections() {
        INI ini = new INI();
        INISection default_ = new INISection(null);
        default_.getProperties().add(
            new INIProperty("author", "Mary Jane")
        );
        ini.getSections().add(default_);
        INISection owner = new INISection("owner");
        owner.getProperties().add(
            new INIProperty("name", "John Doe")
        );
        ini.getSections().add(owner);
        INISection database = new INISection("database");
        database.getProperties().add(
            new INIProperty("author", "John Stark")
        );
        ini.getSections().add(database);

        String expected =
            "author=Mary Jane\n"   +
            "[owner]\n"            +
            "name=John Doe\n"      +
            "[database]\n"         +
            "author=John Stark"
            ;
        assertEquals(expected, instance.format(ini));
    }

}