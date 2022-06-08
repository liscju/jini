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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Tests for {@link INIConfiguration}.
 */
public class INIConfigurationTest {

    // Test helper functions

    /**
     * Finds named section.
     *
     * @param sections sections
     * @param name section name
     * @return section with given name or null when not find
     */
    private INISection find(List<INISection> sections, String name) {
        return sections
            .stream()
            .filter(it ->
                it.getName() == null ? name == null : it.getName().equals(name)
            ).findFirst()
            .get();
    }

    // Constructing INI configuration

    /**
     * Configuration extended by null configuration.
     */
    @Test
    public void testExtendNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "name", "Mary Jane");

        conf.extend(null);

        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.name"));
        assertEquals("Mary Jane", conf.get("owner.name"));
    }

    /**
     * Configuration extended by empty configuration.
     */
    @Test
    public void testExtendEmpty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "name", "Mary Jane");
        INIConfiguration other = new INIConfiguration();

        conf.extend(other);

        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.name"));
        assertEquals("Mary Jane", conf.get("owner.name"));
    }

    /**
     * Configuration extended by other configuration with new property.
     */
    @Test
    public void testExtendNewProperty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "name", "Mary Jane");
        INIConfiguration other = new INIConfiguration();
        other.put("database", "author", "John Stark");

        conf.extend(other);

        Set<String> properties = conf.properties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("owner.name"));
        assertTrue(properties.contains("database.author"));
        assertEquals("Mary Jane", conf.get("owner.name"));
        assertEquals("John Stark", conf.get("database.author"));
    }

    /**
     * Configuration extended by other configuration with new properties
     * added and others overridden.
     */
    @Test
    public void testExtendFewProperties() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "name", "Mary Jane");
        conf.put("modified", "1-April-2001");
        INIConfiguration other = new INIConfiguration();
        conf.put("owner", "name", "John Stark");
        other.put("database.storage", "server", "192.0.5.132");

        conf.extend(other);

        Set<String> properties = conf.properties();
        assertEquals(3, properties.size());
        assertTrue(properties.contains("owner.name"));
        assertTrue(properties.contains("modified"));
        assertTrue(properties.contains("database.storage.server"));
        assertEquals("John Stark", conf.get("owner.name"));
        assertEquals("1-April-2001", conf.get("modified"));
        assertEquals("192.0.5.132", conf.get("database.storage.server"));
    }

    /**
     * Configuration extended by other configuration with property overridden.
     */
    @Test
    public void testExtendOverrideProperty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "name", "Mary Jane");
        INIConfiguration other = new INIConfiguration();
        other.put("owner", "name", "John Stark");

        conf.extend(other);

        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.name"));
        assertEquals("John Stark", conf.get("owner.name"));
    }

    // Listing properties/sections

    /**
     * Listing sections when configuration has none.
     */
    @Test
    public void testSectsEmpty() {
        INIConfiguration conf = new INIConfiguration();

        Set<String> sections = conf.sections();

        assertTrue(sections.isEmpty());
    }

    /**
     * Listing sections when configuration has only default section.
     */
    @Test
    public void testSectsDefault() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");

        Set<String> sections = conf.sections();

        assertEquals(1, sections.size());
        assertTrue(sections.contains("."));
    }

    /**
     * Listing sections when configuration has one section.
     */
    @Test
    public void testSectsSingle() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner.name", "Mary Jane");

        Set<String> sections = conf.sections();

        assertEquals(1, sections.size());
        assertTrue(sections.contains("owner"));
    }

    /**
     * Listing sections when configuration has one section with multiple
     * properties.
     */
    @Test
    public void testSectsPropsInSection() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner.name", "John Doe");
        conf.put("owner.organization", "Acme Widgets");
        conf.put("owner.boss", "");

        Set<String> sections = conf.sections();

        assertEquals(1, sections.size());
        assertTrue(sections.contains("owner"));
    }

    /**
     * Listing sections when configuration has few sections.
     */
    @Test
    public void testSectsFew() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.name", "John Doe");
        conf.put("database.port", "143");

        Set<String> sections = conf.sections();

        assertEquals(3, sections.size());
        assertTrue(sections.contains("."));
        assertTrue(sections.contains("owner"));
        assertTrue(sections.contains("database"));
    }

    /**
     * Listing properties when configuration is empty.
     */
    @Test
    public void testPropsEmpty() {
        INIConfiguration conf = new INIConfiguration();

        Set<String> properties = conf.properties();

        assertTrue(properties.isEmpty());
    }

    /**
     * Listing properties when configuration contain single property.
     */
    @Test
    public void testPropsSingleDefault() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");

        Set<String> properties = conf.properties();

        assertEquals(1, properties.size());
        assertTrue(properties.contains("author"));
    }

    /**
     * Listing properties when configuration contains few properties in default
     * section.
     */
    @Test
    public void testPropsFewDefault() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("modified", "1-April-2001");
        conf.put("street", "");

        Set<String> properties = conf.properties();

        assertEquals(3, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("modified"));
        assertTrue(properties.contains("street"));
    }

    /**
     * Listing properties when configuration has few sections.
     */
    @Test
    public void testPropsFewSections() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.name", "John Doe");
        conf.put("database.port", "143");

        Set<String> properties = conf.properties();

        assertEquals(3, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.name"));
        assertTrue(properties.contains("database.port"));
    }

    /**
     * Listing properties when configuration has nested section.
     */
    @Test
    public void testPropsNested() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("database.storage.server", "192.0.5.132");

        Set<String> properties = conf.properties();

        assertEquals(1, properties.size());
        assertTrue(properties.contains("database.storage.server"));
    }

    /**
     * Listing properties of non existing sections.
     */
    @Test
    public void testPropsSectNonExisting() {
        INIConfiguration conf = new INIConfiguration();

        Set<String> properties = conf.properties("owner");

        assertTrue(properties.isEmpty());
    }

    /**
     * Listing properties of default section (referred to it by empty).
     */
    @Test
    public void testPropsSectDefaultEmpty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("modified", "1-April-2001");
        conf.put("street", "");

        Set<String> properties = conf.properties("");

        assertEquals(3, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("modified"));
        assertTrue(properties.contains("street"));
    }

    /**
     * Listing properties of default section (referred by dot).
     */
    @Test
    public void testPropsSectDefaultDot() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("modified", "1-April-2001");
        conf.put("street", "");

        Set<String> properties = conf.properties(".");

        assertEquals(3, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("modified"));
        assertTrue(properties.contains("street"));
    }

    /**
     * Listing properties of section.
     */
    @Test
    public void testPropsSectOtherSect() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner.author", "Mary Jane");
        conf.put("owner.modified", "1-April-2001");
        conf.put("owner.street", "");

        Set<String> properties = conf.properties("owner");

        assertEquals(3, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("modified"));
        assertTrue(properties.contains("street"));
    }

    /**
     * Listing properties with INIPath null.
     */
    @Test
    public void testPropsINIPathNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "John Stark");
        conf.put("owner.author", "Mary Jane");

        Set<String> properties = conf.properties((INIPath) null);

        assertEquals(0, properties.size());
    }

    /**
     * Listing properties INI Path empty.
     */
    @Test
    public void testPropsINIPathEmpty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "John Stark");
        conf.put("owner.author", "Mary Jane");

        Set<String> properties = conf.properties(INIPath.of(""));

        assertTrue(properties.isEmpty());
    }

    /**
     * Listing properties INIPath addressing section.
     */
    @Test
    public void testPropsINIPathSection() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "John Stark");
        conf.put("owner.author", "Mary Jane");

        Set<String> properties = conf.properties(INIPath.of("owner"));

        assertTrue(properties.isEmpty());
    }

    /**
     * Listing properties INI Path addressing multiple properties.
     */
    @Test
    public void testPropsINIPathMultiple() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "John Stark");
        conf.put("owner.author", "Mary Jane");

        Set<String> properties = conf.properties(INIPath.of("**.author"));

        assertEquals(2, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.author"));
    }

    // Operations on properties

    /**
     * Gets property referenced by property coordinates (section, property)
     * when property is null
     */
    @Test
    public void testGetCordPropNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.author", "John Stark");

        assertNull(conf.get("author", null));
    }

    /**
     * Gets property referenced when property coordinates (section, property)
     * addresses default section (section is null).
     */
    @Test
    public void testGetCordSectNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.author", "John Stark");

        assertEquals("Mary Jane", conf.get(null, "author"));
    }

    /**
     * Gets property referenced by property coordinates (section, property)
     * addresses existing property.
     */
    @Test
    public void testGetCordExist() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.author", "John Stark");

        assertEquals("John Stark", conf.get("owner", "author"));
    }

    /**
     * Gets property referenced by property coordinates (section, property)
     * addresses non-existing property.
     */
    @Test
    public void testGetCordNonExist() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.author", "John Stark");

        assertNull(conf.get("owner", "street"));
    }

    /**
     * Gets property referenced by property coordinates (section, property)
     * addresses property in default section (section pointed by dot).
     */
    @Test
    public void testGetCordDefaultSectByDot() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.author", "John Stark");

        assertEquals("Mary Jane", conf.get(".", "author"));
    }

    /**
     * Gets property value by property path null.
     */
    @Test
    public void testGetPPathNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertNull(conf.get(null));
    }

    /**
     * Gets property value referenced by property path empty.
     */
    @Test
    public void testGetPPathEmpty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertNull(conf.get(""));
    }

    /**
     * Gets property value referenced by property path addressing section.
     */
    @Test
    public void testGetPPathSection() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertNull(conf.get("owner"));
    }

    /**
     * Gets property value referenced by property path addressing property
     * in default section.
     */
    @Test
    public void testGetPPathInDefaultSect() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertEquals("Mary Jane", conf.get("author"));
    }

    /**
     * Gets property value referenced by property path addressing property
     * in default section (by dot).
     */
    @Test
    public void testGetPPathInDefaultSectDot() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertEquals("Mary Jane", conf.get(".author"));
    }

    /**
     * Gets property value referenced by property path addressing existing
     * property.
     */
    @Test
    public void testGetPPathExist() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertEquals("John Stark", conf.get("owner.author"));
    }

    /**
     * Gets property value referenced by property path addressing non-existing
     * property.
     */
    @Test
    public void testGetPPathNonExist() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertNull(conf.get("owner.address"));
    }

    /**
     * Puts property referenced by coordinates (section, name) when property
     * name is blank (null, empty or containing only whitespaces).
     */
    @Test
    public void testPutCordPropertyBlank() {
        INIConfiguration conf = new INIConfiguration();
        try {
            conf.put("owner", null, "Mary Jane");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Property name is blank", ex.getMessage());
        }
    }

    /**
     * Puts property referenced by coordinates (section, name) with property
     * value null.
     */
    @Test
    public void testPutCordValueNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "author", null);

        assertEquals("", conf.get("owner.author"));
    }

    /**
     * Puts property referenced by coordinates (section, name) in default section
     * addressed with null section.
     */
    @Test
    public void testPutCordSectionNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put(null, "author", "Mary Jane");

        assertEquals("Mary Jane", conf.get("author"));
    }

    /**
     * Puts new property referenced by coordinates (section, name).
     */
    @Test
    public void testPutCordNewProperty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "author", "Mary Jane");

        assertEquals("Mary Jane", conf.get("owner.author"));
    }

    /**
     * Puts property referenced by coordinates (section, name) that already
     * exist and overrides value.
     */
    @Test
    public void testPutCordOverride() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner", "author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertEquals("John Stark", conf.get("owner.author"));
    }

    /**
     * Puts property by property path null.
     */
    @Test
    public void testPutPPathNull() {
        INIConfiguration conf = new INIConfiguration();

        try {
            conf.put(null, "Mary Jane");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("Property path is blank", ex.getMessage());
        }
    }

    /**
     * Puts property by property path does not containing property name.
     */
    @Test
    public void testPutPPathNoPropName() {
        INIConfiguration conf = new INIConfiguration();
        try {
            conf.put("owner.", "Mary Jane");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                "Property path 'owner.' does not contain property name"
                    , ex.getMessage()
            );
        }
    }

    /**
     * Puts property by property path addressing property in default section.
     */
    @Test
    public void testPutPPathDefaultSect() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");

        assertEquals("Mary Jane", conf.get("author"));
    }

    /**
     * Puts property by property path addressing property in default section
     * by dot.
     */
    @Test
    public void testPutPPathDefaultSectDot() {
        INIConfiguration conf = new INIConfiguration();
        conf.put(".author", "Mary Jane");

        assertEquals("Mary Jane", conf.get("author"));
    }

    /**
     * Puts property by property path addressing property in specified section.
     */
    @Test
    public void testPutPPathNewProp() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner.name", "Mary Jane");

        assertEquals("Mary Jane", conf.get("owner", "name"));
    }

    /**
     * Puts property by property path addressing property that already exist.
     */
    @Test
    public void testPutPPathOverride() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("owner.name", "Mary Jane");
        conf.put("owner.name", "John Stark");

        assertEquals("John Stark", conf.get("owner", "name"));
    }

    /**
     * Delete by coordinate (section, property) with property null
     */
    @Test
    public void testDeleteCordPropNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertNull(conf.delete("owner", null));
        Set<String> properties = conf.properties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by coordinate (section, property) with default section referenced
     * by null.
     */
    @Test
    public void testDeleteCordSectNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertEquals("Mary Jane", conf.delete(null, "author"));
        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by coordinate (section, property) with default section referenced
     * by dot.
     */
    @Test
    public void testDeleteCordSectDot() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        assertEquals("Mary Jane", conf.delete(".", "author"));
        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by coordinate non-existing property.
     */
    @Test
    public void testDeleteCordNonExisting() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete(".", "modified");

        assertNull(value);
        Set<String> properties = conf.properties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by coordinate existing property.
     */
    @Test
    public void testDeleteCordExisting() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete("owner", "author");

        assertEquals("John Stark", value);
        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("author"));
    }

    /**
     * Delete by null property path.
     */
    @Test
    public void testDeletePPathNull() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete(null);

        assertNull(value);
        Set<String> properties = conf.properties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by empty property path.
     */
    @Test
    public void testDeletePPathEmpty() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete(null);

        assertNull(value);
        Set<String> properties = conf.properties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by property path property in default section.
     */
    @Test
    public void testDeletePPathDefault() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete("author");

        assertEquals("Mary Jane", value);
        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by property path property in default section referenced by dot.
     */
    @Test
    public void testDeletePPathDefaultDot() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete(".author");

        assertEquals("Mary Jane", value);
        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by property path non existing property.
     */
    @Test
    public void testDeletePPathNonExisting() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete("modified");

        assertNull(value);
        Set<String> properties = conf.properties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains("author"));
        assertTrue(properties.contains("owner.author"));
    }

    /**
     * Delete by property path existing property.
     */
    @Test
    public void testDeletePPathExisting() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner", "author", "John Stark");

        String value = conf.delete("owner.author");

        assertEquals("John Stark", value);
        Set<String> properties = conf.properties();
        assertEquals(1, properties.size());
        assertTrue(properties.contains("author"));
    }

    // Conversions between INI configuration to/from INI file

    /**
     * Empty configuration converted to INI.
     */
    @Test
    public void testToINIEmpty() {
        INIConfiguration conf = new INIConfiguration();

        INI ini = conf.toINI();

        assertTrue(ini.getSections().isEmpty());
    }

    /**
     * Single property configuration converted to INI.
     */
    @Test
    public void testToINISingleProp() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("modified", "1-April-2001");

        INI ini = conf.toINI();

        List<INISection> sections = ini.getSections();
        assertEquals(1, sections.size());
        INISection section = sections.get(0);
        assertNull(section.getName());
    }

    /**
     * Multiple properties in default section converted to INI.
     */
    @Test
    public void testToINIDefaultSectFewSect() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("modified", "1-April-2001");
        conf.put("street", "Wall");

        INI ini = conf.toINI();

        List<INISection> sections = ini.getSections();
        assertEquals(1, sections.size());
        INISection section = sections.get(0);
        List<INIProperty> properties = section.getProperties();
        assertEquals(3, properties.size());
        assertTrue(properties.contains(
            new INIProperty("author", "Mary Jane")
        ));
        assertTrue(properties.contains(
            new INIProperty("modified", "1-April-2001")
        ));
        assertTrue(properties.contains(
            new INIProperty("street", "Wall")
        ));
    }

    /**
     * Properties in multiple sections converted to INI.
     */
    @Test
    public void testToINIFewSects() {
        INIConfiguration conf = new INIConfiguration();
        conf.put("author", "Mary Jane");
        conf.put("owner.name", "John Stark");
        conf.put("database.storage","mount", "/mnt/db");

        INI ini = conf.toINI();

        List<INISection> sections = ini.getSections();
        assertEquals(3, sections.size());
        INISection default_ = find(sections, null);
        assertNotNull(default_);
        default_.getProperties().equals(
            Arrays.asList(new INIProperty("author", "Mary Jane"))
        );
        INISection owner = find(sections, "owner");
        owner.getProperties().equals(
            Arrays.asList(new INIProperty("name", "John Stark"))
        );
        INISection datastore = find(sections, "database.storage");
        datastore.getProperties().equals(
            Arrays.asList(new INIProperty("mount", "/mnt/db"))
        );
    }

    /**
     * Converts null INI file to INI configuration.
     */
    @Test
    public void testFromININull() {
        INIConfiguration conf = INIConfiguration.fromINI(null);
        assertTrue(conf.properties().isEmpty());
    }

    /**
     * Converts empty INI file to INI configuration.
     */
    @Test
    public void testFromINIEmpty() {
        INIConfiguration conf = INIConfiguration.fromINI(new INI());
        assertTrue(conf.properties().isEmpty());
    }

    /**
     * Converts few properties in default section of INI file to INI configuration.
     */
    @Test
    public void testFromINIDefaultSect() {
        INI ini = new INI();
        INISection default_ = new INISection();
        default_.getProperties().add(
            new INIProperty("author", "Mary Jane")
        );
        default_.getProperties().add(
            new INIProperty("modified", "1-April-2001")
        );
        ini.getSections().add(default_);

        INIConfiguration conf = INIConfiguration.fromINI(ini);

        assertEquals(2, conf.properties().size());
        assertEquals("Mary Jane", conf.get("author"));
        assertEquals("1-April-2001", conf.get(".modified"));
    }

    /**
     * Converts few properties in few sections in INI file to INI configuration.
     */
    @Test
    public void testFromINIFewSects() {
        INI ini = new INI();
        INISection default_ = new INISection();
        default_.getProperties().add(
                new INIProperty("author", "Mary Jane")
        );
        ini.getSections().add(default_);
        INISection owner = new INISection("owner");
        owner.getProperties().add(
                new INIProperty("name", "John Stark")
        );
        ini.getSections().add(owner);

        INIConfiguration conf = INIConfiguration.fromINI(ini);

        assertEquals(2, conf.properties().size());
        assertEquals("Mary Jane", conf.get("author"));
        assertEquals("John Stark", conf.get("owner.name"));
    }

}