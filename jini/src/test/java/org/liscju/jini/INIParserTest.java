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

import java.util.List;

/**
 * Tests for {@link INIParser}.
 */
public class INIParserTest {

    /**
     * Instance under tests.
     */
    private INIParser instance;

    @Before
    public void setUp() {
        this.instance = new INIParser();
    }

    /**
     * Parse INI file with null content.
     */
    @Test
    public void testNullContent() {
        INI ini = instance.parse(null);

        assertTrue(ini.getSections().isEmpty());
    }

    /**
     * Parse INI file with empty content.
     */
    @Test
    public void testEmptyContent() {
        INI ini = instance.parse("");

        assertTrue(ini.getSections().isEmpty());
    }

    /**
     * Parse INI file with section definition syntax error.
     */
    @Test
    public void testSectionNotProperlyEnded() {
        try {
            instance.parse("[owner");
        } catch (INISyntaxException ex) {
            assertEquals(1, ex.getLineNo());
            assertEquals("[owner", ex.getLine());
            assertEquals(
                "Start of section not properly ended"
                    , ex.getExplanation()
            );
        }
    }

    /**
     * Parse INI file with section declaration not in separate line.
     */
    @Test
    public void testSectionAndOtherLexem() {
        try {
            instance.parse("[owner] name=John Doe");
        } catch (INISyntaxException ex) {
            assertEquals(1, ex.getLineNo());
            assertEquals("[owner] name=John Doe", ex.getLine());
            assertEquals(
                "Section declaration must be the only lexem in line"
                    , ex.getExplanation()
            );
        }
    }

    /**
     * Parse INI file with empty property name.
     */
    @Test
    public void testPropertyNoName() {
        String content = "=John Doe";

        try {
            instance.parse(content);
        } catch (INISyntaxException ex) {
            assertEquals(1, ex.getLineNo());
            assertEquals("=John Doe", ex.getLine());
            assertEquals(
                "Line does not contain variable to assign"
                    , ex.getExplanation()
            );
        }
    }

    /**
     * Parse INI file with comments.
     */
    @Test
    public void testComment() {
        String content = "; [owner]";

        INI ini = instance.parse(content);

        assertTrue(ini.getSections().isEmpty());
    }

    /**
     * Parse INI file with single property in default section.
     */
    @Test
    public void testSectionDefaultProperty() {
        String content = "name = John Doe";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertNull(section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("name", property.getName());
        assertEquals("John Doe", property.getValue());
    }

    /**
     * Parse INI file with single property in section.
     */
    @Test
    public void testSectionWithProperty() {
        String content =
            "[owner]\n" +
            "name = John Doe";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertEquals("owner", section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty first = properties.get(0);
        assertEquals("name", first.getName());
        assertEquals("John Doe", first.getValue());
    }

    /**
     * Parse INI file with empty section name.
     */
    @Test
    public void testSectionNameEmpty() {
        String content =
            "[]\n" +
            "name = John Doe";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertEquals("", section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("name", property.getName());
        assertEquals("John Doe", property.getValue());
    }

    /**
     * Parse INI file with empty property name.
     */
    @Test
    public void testPropertyNameEmpty() {
        String content = "=Mary";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertNull(section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("", property.getName());
        assertEquals("Mary", property.getValue());
    }

    /**
     * Parse INI file with empty property value.
     */
    @Test
    public void testPropertyValueEmpty() {
        String content = "author=";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertNull(section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("author", property.getName());
        assertEquals("", property.getValue());
    }

    /**
     * Parse INI file with empty property name and empty property value.
     */
    @Test
    public void testPropertyNameEmptyValueEmpty() {
        String content = "=";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertNull(section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("", property.getName());
        assertEquals("", property.getValue());
    }

    /**
     * Parse INI file with property without value assignment.
     */
    @Test
    public void testPropertyNoAssignment() {
        String content = "author";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertNull(section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("author", property.getName());
        assertEquals("", property.getValue());
    }

    /**
     * Parse INI file with section definition containing whitespaces.
     */
    @Test
    public void testSectionWhitespaces() {
        String content =
            "[   owner      ]\n" +
            "name = John Doe";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertEquals("owner", section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("name", property.getName());
        assertEquals("John Doe", property.getValue());
    }

    /**
     * Parse INI file with property name/values containing whitespaces.
     */
    @Test
    public void testPropertyWhitespaces() {
        String content =
            "[owner]\n" +
            "     name       =       John   Doe    ";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertEquals("owner", section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("name", property.getName());
        assertEquals("John   Doe", property.getValue());
    }

    /**
     * Parse INI file with few properties in section.
     */
    @Test
    public void testFewProperties() {
        String content =
            "[owner]\n" +
            "name = John Doe\n" +
            "organization=ACME";

        INI ini = instance.parse(content);

        assertEquals(1, ini.getSections().size());
        INISection section = ini.getSections().get(0);
        assertEquals("owner", section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(2, properties.size());

        INIProperty property = properties.get(0);
        assertEquals("name", property.getName());
        assertEquals("John Doe", property.getValue());

        property = properties.get(1);
        assertEquals("organization", property.getName());
        assertEquals("ACME", property.getValue());
    }

    /**
     * Parse INI file with few sections.
     */
    @Test
    public void testFewSections() {
        String content =
            "author=Mary Jane\n" +
            "[owner]\n" +
            "name=John Doe\n" +
            "[database]\n" +
            "server = 192.0.2.62";

        INI ini = instance.parse(content);

        List<INISection> sections = ini.getSections();
        assertEquals(3, sections.size());

        INISection section = sections.get(0);
        assertNull(section.getName());
        List<INIProperty> properties = section.getProperties();
        assertEquals(1, properties.size());
        assertEquals("author", properties.get(0).getName());
        assertEquals("Mary Jane", properties.get(0).getValue());

        section = sections.get(1);
        assertEquals("owner", section.getName());
        properties = section.getProperties();
        assertEquals(1, properties.size());
        assertEquals("name", properties.get(0).getName());
        assertEquals("John Doe", properties.get(0).getValue());

        section = sections.get(2);
        assertEquals("database", section.getName());
        properties = section.getProperties();
        assertEquals("server", properties.get(0).getName());
        assertEquals("192.0.2.62", properties.get(0).getValue());
    }

}