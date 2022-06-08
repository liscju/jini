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

import java.util.regex.Pattern;

/**
 * INIPath is a language for addressing parts of INI properties.
 * <p>
 * INIPath language consists of the sections names, properties names, wildcards
 * (*, **) and dots joining properties with sections. Wildcard * is used for
 * matching zero or more characters except for dot whereas wildcard ** is used
 * for matching zero or more characters including dot. Properties inside default
 * section can be addressed by leading dot or not.
 * <p>
 * Example INI:
 * <pre>
 *   modified = 1-April-2001
 *   author = Mary Jane
 *   street =
 *   [owner]
 *   name = John Doe
 *   organization = Acme Widgets Inc.
 *   boss
 *   [database]
 *   author = John Stark
 *   server = 192.0.2.62
 *   [database.storage]
 *   mount = /home/oracle/fs
 *   server = 192.0.5.132
 * </pre>
 * Example INIPath expressions:
 * <pre>
 *   * - match any property (modified, author, street, owner.name, ...)
 *   .* - any property inside default section (modified, author, street)
 *   owner.* - any property inside 'owner' section (owner.name, owner.organization,
 *             owner.boss)
 *   database.* - any property inside 'database' section
 *                (database.author, database.server)
 *   database.** - any property inside 'database' section and nested subsection
 *                 (database.author, database.server, database.storage.mount,
 *                  database.storage.server)
 *   **.author - any property with name author (author, database.author)
 * </pre>
 */
public class INIPath {

    /**
     * INI path.
     */
    private String inipath;

    // Default constructor forbidden, use of() method.
    private INIPath() { }

    /**
     * Constructs INIPath.
     *
     * @param inipath INI path
     * @return INIPath
     */
    public static INIPath of(String inipath) {
        if (inipath == null)
            inipath = "";

        INIPath path = new INIPath();
        path.inipath = inipath.trim();
        return path;
    }

    /**
     * Return property matcher.
     *
     * @return property matcher
     */
    Matcher matcher() {
        return new Matcher(inipath);
    }

    /**
     * INIPath property matcher.
     */
    class Matcher {

        /**
         * Matcher pattern.
         */
        private final Pattern pattern;

        /**
         * Constructs INIPath matcher.
         */
        private Matcher(String inipath) {
            if (!inipath.startsWith(".") && !inipath.startsWith("*"))
                inipath = "." + inipath;

            String regex = inipath.replace(".", "\\.");
            regex = regex.replaceAll("\\*\\*+", ".{0,}");
            regex = regex.replaceAll("\\*", "[^.]{0,}");
            if (inipath.startsWith("*"))
                regex = "(" + regex + ")|(\\." + regex + ")";
            pattern = Pattern.compile(regex);
        }

        /**
         * Indicates whether INIPath matches property path.
         *
         * @param ppath property path
         * @return {@code true} when INIPath matches property path,
         * {@code false} otherwise
         */
        public boolean matches(String ppath) {
            if (ppath == null)
                ppath = "";
            else
                ppath = ppath.trim();

            if (!ppath.startsWith("."))
                ppath = "." + ppath;

            java.util.regex.Matcher matcher = pattern.matcher(ppath);
            return matcher.matches();
        }
    }

}
