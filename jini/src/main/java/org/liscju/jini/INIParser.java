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

import java.util.List;

/**
 * INI file parser.
 */
class INIParser {

    /**
     * Parses INI file.
     * <p>
     * When INI file content is null or empty it returns empty INI file.
     *
     * @param content INI file content
     * @return INI file
     * @throws INISyntaxException when INI file has syntax error
     */
    public INI parse(String content) {
        if (content == null)
            return new INI();
        if (content.isEmpty())
            return new INI();

        INI ini = new INI();
        List<INISection> sections = ini.getSections();

        String[] lines = content.split("\n");
        int lineNo = 0;
        INISection section = null;
        for (String line : lines) {
            lineNo++;

            line = line.trim();
            if (line.isEmpty())
                continue;

            char c = line.charAt(0);
            int comment;
            switch (c) {
                case ';': // comment
                    break;
                case '[': // section
                    comment = line.indexOf(';');
                    if (comment > 0) {
                        line = line.substring(0, comment);
                        line = line.trim();
                    }
                    int sectionEnd = line.lastIndexOf(']');
                    if (sectionEnd == -1)
                        throw new INISyntaxException(
                            lineNo, line
                            , "Start of section not properly ended"
                        );
                    if (sectionEnd != line.length() - 1)
                        throw new INISyntaxException(
                            lineNo, line
                            , "Section declaration must be the only lexem in line"
                        );

                    section = new INISection(line.substring(1, sectionEnd).trim());
                    sections.add(section);
                    break;
                default:
                    comment = line.indexOf(';');
                    if (comment > 0) {
                        line = line.substring(0, comment);
                        line = line.trim();
                    }

                    String name, value;
                    int equal = line.indexOf('=');
                    if (equal >= 0) {
                        name = line.substring(0, equal);
                        value = line.substring(equal + 1);
                    } else {
                        name = line;
                        value = "";
                    }
                    name = name.trim();
                    value = value.trim();

                    if (section == null) {
                        section = new INISection();
                        sections.add(section);
                    }

                    section.getProperties().add(new INIProperty(name, value));
                    break;
            }
        }

        return ini;
    }

}
