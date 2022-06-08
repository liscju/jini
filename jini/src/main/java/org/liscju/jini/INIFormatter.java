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
 * Formatter for the INI file.
 */
class INIFormatter {

    /**
     * Formats INI file.
     * <p>
     * When INI file is null it returns empty string.
     *
     * @param ini INI file
     * @return formatted INI
     */
    public String format(INI ini) {
        if (ini == null)
            return "";

        List<String> lines = new LinkedList<>();

        for (INISection section : ini.getSections()) {
            if (section == null) {
                lines.add("[]");
                continue;
            }

            if (section.getName() != null)
                lines.add("[" + stringify(section.getName()) + "]");

            for (INIProperty property : section.getProperties())
                if (property == null)
                    lines.add("=");
                else
                    lines.add(
                        stringify(property.getName())
                        + "="
                        + stringify(property.getValue())
                    );
        }

        return String.join("\n", lines);
    }

    /**
     * Stringifies section or property name.
     * <p>
     * When section or property name is null it returns empty string. Otherwise
     * it returns the same name as provided.
     *
     * @param name section or property name
     * @return section or property name
     */
    private String stringify(String name) {
        if (name == null)
            return "";

        return name;
    }

}
