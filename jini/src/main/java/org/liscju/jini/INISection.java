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

import java.util.LinkedList;
import java.util.List;

/**
 * Section of the INI file.
 * <p>
 * Section of the INI file is the named group of properties containing data about
 * specific subject. INI file consists of properties outside any section and
 * properties inside consecutive sections. Properties outside any section
 * are put in special section with {@code null} section name.
 * <p
 * Note that INI section is by design the low level representation of the section
 * in INI file which implies that section properties order matters and properties
 * can be duplicated (same name).
 * <p>
 * Example database section containing server, port, file properties:
 * <pre>{@code
 *   [database]
 *   server = 192.0.2.62
 *   port = 143
 *   file = "payroll.dat"
 * }</pre>
 */
class INISection {

    /**
     * Section name.
     */
    private String name;

    /**
     * Section properties.
     */
    private List<INIProperty> properties;

    /**
     * Creates outside INI section.
     */
    INISection() {
        this.name = null;
        this.properties = new LinkedList<>();
    }

    /**
     * Constructs INI section.
     *
     * @param name section name
     */
    INISection(String name) {
        this.name = name;
        this.properties = new LinkedList<>();
    }

    /**
     * Gets section name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets section properties.
     *
     * @return properties
     */
    public List<INIProperty> getProperties() {
        return properties;
    }

}
