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

/**
 * Location of the property in INI file.
 * <p>
 * INI property path is a means of describing location of the property in INI
 * file. Location is described using a mini language constructed from joining
 * section and property with a dot. Properties in default section can be described
 * by a property name without section or by prefixing path with a "dot".
 * Properties in other sections can be described by joining a section and property
 * name with a "dot".
 * <p>
 * Example of INI file:
 * <pre>{@code
 *   modified = 1-April-2001
 *   [database]
 *   server = 192.0.2.62
 *   [database.storage]
 *   mount = /home/oracle/fs
 * }</pre>
 * Valid INI property paths for example:
 * <ul>
 *   <li>. (describes default section)
 *   <li>modified (describes "modified" property in default section)
 *   <li>.modified (describes "modified" property in default section)
 *   <li>database.server (describes "server" property in "database" section)
 *   <li>database.storage.mount (describes "mount" property in "database.storage" section)
 * </ul>
 */
class INIPropertyPath {

    /**
     * Default section name.
     */
    private static final String DEFAULT_SECTION = ".";

    /**
     * Section name.
     */
    private String section;

    /**
     * Property name.
     */
    private String property;

    /**
     * Constructs INI property with property coordinates.
     *
     * @param section section name
     * @param property property name
     */
    private INIPropertyPath(String section, String property) {
        this.section = section;
        this.property = property;
    }

    /**
     * Gets section.
     *
     * @return section name
     */
    public String getSection() {
        return section;
    }

    /**
     * Gets property.
     *
     * @return property name
     */
    public String getProperty() {
        return property;
    }

    /**
     * Constructs INI property path.
     *
     * @param ppath property path
     * @return INI property path
     */
    public static INIPropertyPath of(String ppath) {
        if (ppath == null)
            return new INIPropertyPath(DEFAULT_SECTION, null);
        if (ppath.isEmpty())
            return new INIPropertyPath(DEFAULT_SECTION, null);
        if (ppath.equals(DEFAULT_SECTION))
            return new INIPropertyPath(DEFAULT_SECTION, null);
        int dots = count(ppath, '.');
        if (ppath.startsWith(DEFAULT_SECTION) && dots == 1)
            return new INIPropertyPath(DEFAULT_SECTION, ppath.substring(1).trim());

        if (ppath.startsWith(DEFAULT_SECTION) && dots > 1)
            ppath = ppath.substring(1);
        int dot = ppath.lastIndexOf('.');
        if (dot == -1)
            return new INIPropertyPath(DEFAULT_SECTION, ppath.trim());
        if (dot == ppath.length() - 1)
            return new INIPropertyPath(
                ppath.substring(0, ppath.length() - 1).trim()
                , null
            );

        String section = ppath.substring(0, dot).trim();
        String property = ppath.substring(dot + 1).trim();
        return new INIPropertyPath(section, property);
    }

    /**
     * Counts number of occurrences of character in string.
     *
     * @param string string
     * @param character character
     * @return number of occurrences of character in string
     */
    private static int count(String string, char character) {
        int count = 0;

        for (int i = 0; i < string.length(); i++)
            if (string.charAt(i) == character)
                count += 1;

        return count;
    }

}
