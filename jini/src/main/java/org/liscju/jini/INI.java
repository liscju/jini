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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * INI file.
 * <p>
 * INI file is a configuration file meta-format consisting of text-based content
 * with a structure and syntax consisting of properties (name-value pairs) and
 * sections that organize the properties.
 * <p>
 * INI file contains properties inside sections. Properties outside of any
 * section are kept in special section with {@code null} name.
 * <p>
 * Note that INI file is by design low-level representation of the INI file which
 * implies that section can be duplicated, order of section matters (it applies
 * to properties as well). INI file comments are not loaded from file or stored
 * into file.
 * <p>
 * Example INI file:
 * <pre>{@code
 *   ; last modified 1 April 2001 by John Doe
 *   [owner]
 *   name = John Doe
 *   organization = Acme Widgets Inc.
 *   [database]
 *   ; use IP address in case network name resolution is not working
 *   server = 192.0.2.62
 *   port = 143
 *   file = "payroll.dat"
 * }</pre>
 */
public class INI {

    /**
     * Sections.
     */
    private List<INISection> sections;

    // Building INI

    /**
     * Creates INI file with empty content.
     */
    public INI() {
        sections = new LinkedList<>();
    }

    // I/O operations

    /**
     * Loads INI file.
     *
     * @param reader INI file character input stream
     * @throws NullPointerException when reader is null
     * @throws INISyntaxException when INI file has syntax error
     * @throws java.io.UncheckedIOException when unexpected error while reading
     * has occurred
     */
    public void load(Reader reader) {
        Objects.requireNonNull(reader);

        INIParser parser = new INIParser();
        String content = new BufferedReader(reader)
            .lines()
            .collect(Collectors.joining("\n"));

        INI ini = parser.parse(content);
        sections = ini.sections;
    }

    /**
     * Stores INI file.
     *
     * @param writer INI file character output stream
     * @throws NullPointerException when writer is null
     * @throws java.io.UncheckedIOException when unexpected error while writing
     * has occurred
     */
    public void store(Writer writer) {
        Objects.requireNonNull(writer);

        try {
            INIFormatter formatter = new INIFormatter();
            writer.write(formatter.format(this));
            writer.flush();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    // Accessors

    /**
     * Gets sections.
     *
     * @return sections
     */
    public List<INISection> getSections() {
        return sections;
    }

}
