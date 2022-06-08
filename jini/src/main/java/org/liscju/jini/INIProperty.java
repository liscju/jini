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

import java.util.Objects;

/**
 * Property of the INI file.
 * <p>
 * INI property is the atom of the INI file containing data. Property consists
 * of name and value. Properties are grouped into sections.
 * <p>
 * Example of the properties name and organization inside owner section:
 * <pre>{@code
 *   [owner]
 *   name = John Doe
 *   organization = Acme Widgets Inc.
 * }</pre>
 * <p>
 * Property is immutable.
 */
class INIProperty {

    /**
     * Property name.
     */
    private String name;

    /**
     * Property value.
     */
    private String value;

    /**
     * Constructs the INI property.
     *
     * @param name property name
     * @param value property value
     */
    INIProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets property name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets property value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        INIProperty property = (INIProperty) o;
        return Objects.equals(name, property.name)
                && Objects.equals(value, property.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

}
