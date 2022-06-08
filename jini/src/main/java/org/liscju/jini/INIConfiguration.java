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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Configuration in INI format.
 * <p>
 * INI configuration manages configuration in INI format. Configuration supports
 * CRUD operations on properties and sections. It can be created manually or
 * converted from INI file. INI configuration can be converted to INI file.
 * <p>
 * INI format supported by this configuration must adhere to the following rules:
 * <ul>
 *   <li> section name is not dot ('.') and does not start with dot ('.')
 *   <li> section name can be nested (e.g. 'section.subsection')
 *   <li> property name must not be dot ('.') and must not be empty
 *   <li> property name must not be nested (e.g. 'key1.key2=value')
 * </ul>
 * Example of supported INI file:
 * <pre>{@code
 *   modified = 1-April-2001
 *   author = Mary Jane
 *   street =
 *   [database]
 *   server = 192.0.2.62
 *   port = 143
 *   [database.storage]
 *   mount = /home/oracle/fs
 *   server = 192.0.5.132
 * }</pre>
 * <p>
 * Configuration CRUD operations references properties by coordinates, property
 * path or ini path. Property coordinates or property path references single
 * property while ini path references many properties.
 * Coordinates references properties by pair of section name and property name.
 * When section name is null, empty or "." it denotes default section (properties
 * outside any section in INI file).
 * Property path consists of section name and property name joined by dot
 * (e.g. "section.key", "section.subsection.key"). Property inside default
 * section is denoted by not including section part in property path (e.g.
 * "key1" or "key5").
 * INI path is language of its own and its explanation is outside of scope.
 */
public class INIConfiguration {

    /**
     * Default section name.
     */
    public static final String DEFAULT_SECTION = ".";

    /**
     * INI specification that INI must conform to.
     * <p>
     * Updating specification should include changing its textual representation
     * in {@link #SPECIFICATION_REQUIREMENTS}.
     */
    private static final INISpecification SPECIFICATION =
        INISpecification.define()
            .section(Pattern.compile("[^.].*[^.]"))
            .property(Pattern.compile("[^.]+"))
            .create();

    /**
     * Textual representation of specification ({@link #SPECIFICATION}).
     */
    private static final List<String> SPECIFICATION_REQUIREMENTS = Arrays.asList(
        "section name must not be dot ('.') and must not start with dot ('.')"
        , "section name must not be empty"
        , "property name must not be dot ('.') and must not be empty"
        , "property name must not be nested (e.g. 'key1.key2=value')"
    );

    /**
     * Configuration properties.
     * <p>
     * Mapping between section and properties (mapping key to value).
     */
    private Map<String, Map<String, String>> properties;

    // Building configuration

    /**
     * Creates empty INI configuration.
     */
    public INIConfiguration() {
        properties = new HashMap<>();
    }

    /**
     * Extends configuration by appending other configuration sections and
     * properties.
     * <p>
     * When other configuration contains same property as this configuration
     * it overrides this configuration property value. When other configuration
     * is null nothing happens.
     *
     * @param other other configuration
     */
    public void extend(INIConfiguration other) {
        if (other == null)
            return;

        for (String section : other.sections())
            for (String property : other.properties(section))
                put(section, property, other.get(section, property));
    }

    // Listing properties/sections

    /**
     * Lists sections.
     *
     * @return sections names
     */
    public Set<String> sections() {
        return new LinkedHashSet<>(properties.keySet());
    }

    /**
     * Lists all properties (properties in all sections).
     * <p>
     * Properties returned are in property path format (joined sections names
     * and properties names e.g "outer_key", "section.key1",
     * "section.subsection.key2" etc).
     *
     * @return properties
     */
    public Set<String> properties() {
        Set<String> properties = new LinkedHashSet<>();

        for (String section : sections())
            for (String property : properties(section)) {
                if (section.equals(DEFAULT_SECTION))
                    properties.add(property);
                else
                    properties.add(String.join(".", section, property));
            }

        return properties;
    }

    /**
     * Lists properties in section.
     * <p>
     * Returned properties names do not include section name. When provided
     * section name is null, empty or "." it returns properties from default
     * section (outside section). Provided section name is trimmed before
     * section lookup.
     *
     * @param section section name
     * @return properties names
     */
    public Set<String> properties(String section) {
        if (isDefaultSection(section))
            section = DEFAULT_SECTION;
        section = section.trim();

        return new HashSet<>(
            properties.getOrDefault(section, Collections.emptyMap()).keySet()
        );
    }

    /**
     * Lists properties matching INIPath.
     * <p>
     * When INIPath is null it returns empty list. Properties are returned
     * in property path format (section and property name joined with dot).
     *
     * @param inipath INIPath
     * @return properties paths
     */
    public Set<String> properties(INIPath inipath) {
        Set<String> properties = new LinkedHashSet<>();
        if (inipath == null)
            return properties;

        INIPath.Matcher matcher = inipath.matcher();
        for (String property : properties())
            if (matcher.matches(property))
                properties.add(property);

        return properties;
    }

    // Operations on properties

    /**
     * Gets property value.
     * <p>
     * It returns property value referenced by provided property coordinates
     * (section and property name). When section name is null, empty or blank
     * it denotes property inside default (outside) section. When property
     * coordinates denotes section (which happens when property name is null)
     * it returns null. Property name is trimmed before property lookup.
     *
     * @param section section name
     * @param property property name
     * @return property value or null when property is not found
     */
    public String get(String section, String property) {
        if (property == null)
            return null;
        property = property.trim();

        if (isDefaultSection(section))
            section = DEFAULT_SECTION;
        section = section.trim();

        return properties
            .getOrDefault(section, Collections.emptyMap())
            .get(property);
    }

    /**
     * Gets property value.
     * <p>
     * It returns property value referenced by provided property path (e.g
     * "outer_key" or "section1.key1" etc). When property path denotes section
     * (for example when property path is "section1" and there exists section
     * with name "section1") it returns null.
     *
     * @param ppath property path
     * @return property value or null when property not found or path is null
     */
    public String get(String ppath) {
        INIPropertyPath path = INIPropertyPath.of(ppath);
        return properties
            .getOrDefault(path.getSection(), Collections.emptyMap())
            .get(path.getProperty());
    }

    /**
     * Puts property value.
     * <p>
     * Property to put is referenced by property coordinates (section and
     * property name). When property already exists it is overwritten. When
     * section is name null, empty or blank (contains only whitespaces) it
     * denotes default section. Value of null or empty is treated interchangeably
     * (null value is put as empty value).
     *
     * @param section section name
     * @param property property name
     * @param value property value
     * @throws IllegalArgumentException when property name is blank (null, empty
     * or contains only whitespaces)
     */
    public void put(String section, String property, String value) {
        if (isBlank(property))
            throw new IllegalArgumentException("Property name is blank");

        if (isDefaultSection(section))
            section = DEFAULT_SECTION;
        section = section.trim();
        property = property.trim();
        if (value == null)
            value = "";

        if (!properties.containsKey(section))
            properties.put(section, new HashMap<>());
        properties.get(section).put(property, value);
    }

    /**
     * Puts property value.
     * <p>
     * Property to put is referenced by property path (section and property
     * name joined with dot). When property already exists it is overwritten.
     * Value of null or empty is treated interchangeably (null value is put as
     * empty value).
     *
     * @param ppath property path
     * @param value property value
     * @throws IllegalArgumentException when property path does not addresses
     * valid property (does not contain property name)
     */
    public void put(String ppath, String value) {
        if (isBlank(ppath))
            throw new IllegalArgumentException("Property path is blank");

        INIPropertyPath path = INIPropertyPath.of(ppath);
        if (path.getProperty() == null)
            throw new IllegalArgumentException(
                String.format(
                    "Property path '%s' does not contain property name"
                    , ppath
                )
            );

        put(path.getSection(), path.getProperty(), value);
    }

    /**
     * Deletes property.
     * <p>
     * Property to delete is referenced by property coordinates (section and
     * property name). When property denotes section (property name is null) or
     * it is blank nothing is deleted.
     *
     * @param section section name
     * @param property property name
     * @return property value before deletion or null when property not found
     */
    public String delete(String section, String property) {
        if (isBlank(property))
            return null;

        if (isDefaultSection(section))
            section = DEFAULT_SECTION;
        section = section.trim();
        property = property.trim();

        if (!properties.containsKey(section))
            return null;
        String value = properties.get(section).remove(property);
        if (properties.get(section).keySet().isEmpty())
            properties.remove(section);
        return value;
    }

    /**
     * Deletes property.
     * <p>
     * Property to delete is referenced by property path. When property path
     * references section nothing happens. When property path does not contain
     * property name nothing happens.
     *
     * @param ppath property path
     * @return property value before deletion or null when property not found
     */
    public String delete(String ppath) {
        INIPropertyPath path = INIPropertyPath.of(ppath);
        return delete(path.getSection(), path.getProperty());
    }

    // Conversion between INI configuration to/from INI file

    /**
     * Converts INI configuration to INI file.
     *
     * @return INI file
     */
    public INI toINI() {
        Map<String, INISection> sections = new HashMap<>();
        for (String section : properties.keySet()) {
            Map<String, String> props = properties.get(section);
            section = isDefaultSection(section) ? null : section;

            sections.put(section, new INISection(section));
            for (String name : props.keySet()) {
                String value = props.get(name);

                sections.get(section)
                        .getProperties()
                        .add(new INIProperty(name, value));
            }
        }

        INI ini = new INI();
        INISection outside = sections.remove(null);
        if (outside != null)
            ini.getSections().add(outside);
        for (String section : sections.keySet())
            ini.getSections().add(sections.get(section));
        return ini;
    }

    /**
     * Constructs INI configuration from INI file.
     * <p>
     * When INI file contains duplicated properties in sections, properties that
     * comes later overrides previous properties. In case when INI sections
     * are duplicated, later section properties that are the same as properties
     * in previous section it overrides it.
     *
     * @param ini INI file
     * @return INI configuration
     * @throws INISpecViolationException when INI file does not conform to
     * specification
     */
    public static INIConfiguration fromINI(INI ini) {
        if (ini == null)
            return new INIConfiguration();

        verify(ini);
        INIConfiguration conf = new INIConfiguration();

        for (INISection section : ini.getSections()) {
            Map<String, String> properties = new HashMap<>();

            for (INIProperty property : section.getProperties())
                properties.put(property.getName(), property.getValue());

            if (section.getName() == null)
                conf.properties.put(DEFAULT_SECTION, properties);
            else
                conf.properties.put(section.getName(), properties);
        }

        return conf;
    }

    /**
     * Verifies INI conforms to INI configuration requirements.
     *
     * @param ini INI file
     * @throws INISpecViolationException when INI does not conform to INI
     * configuration requirements
     */
    public static void verify(INI ini) {
        try {
            SPECIFICATION.verify(ini);
        } catch (INISpecViolationException ex) {
            throw new INISpecViolationException(
                ex.getDetail(), SPECIFICATION_REQUIREMENTS
            );
        }
    }

    // Private utilities

    /**
     * Indicates whether section name denotes default section.
     *
     * @param section section name
     * @return {@code true} when section name denotes default section,
     * {@code false} otherwise
     */
    private boolean isDefaultSection(String section) {
        return isBlank(section) || section.equals(DEFAULT_SECTION);
    }

    /**
     * Indicates whether string is null, empty or contains whitespaces only.
     * <p>
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * @param string string to check
     * @return {@code true} if string is null, empty or contains whitespaces only,
     * {@code false} otherwise
     */
    private boolean isBlank(String string) {
        if (string == null)
            return true;
        if (string.isEmpty())
            return true;

        for (char character : string.toCharArray())
            if (!Character.isWhitespace(character))
                return false;
        return true;
    }

}
