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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * INI file format specification.
 */
public class INISpecification {

    // Creating specification is forbidden. Builder must be used instead.
    private INISpecification() { }

    /**
     * Pattern for section name.
     */
    private Pattern section;

    /**
     * Pattern for property name.
     */
    private Pattern property;

    /**
     * Initiates new INI specification definition.
     *
     * @return INI specification builder
     */
    public static INISpecificationBuilder define() {
        return new INISpecificationBuilder(new INISpecification());
    }

    /**
     * Verifies whether INI file satisfies specification.
     *
     * @param ini INI file
     * @throws INISpecViolationException when INI file violates specification
     */
    public void verify(INI ini) throws INISpecViolationException {
        Matcher matcher;

        for (INISection section : ini.getSections()) {
            if (this.section != null && section.getName() != null) {
                String name = section.getName();
                matcher = this.section.matcher(name);
                if (!matcher.matches())
                    throw new INISpecViolationException(
                        String.format("Section '%s' has illegal name", name)
                    );
            }

            for (INIProperty property : section.getProperties()) {
                if (this.property != null) {
                    String name = property.getName() == null ? "" : property.getName();
                    matcher = this.property.matcher(name);
                    if (!matcher.matches()) {
                        throw new INISpecViolationException(
                            String.format("Property '%s' has illegal name", name)
                        );
                    }
                }
            }
        }
    }

    /**
     * Builder for INI specification.
     */
    public static class INISpecificationBuilder {

        /**
         * INI specification.
         */
        private INISpecification spec;

        /**
         * Constructs specification builder.
         *
         * @param spec specification
         */
        private INISpecificationBuilder(INISpecification spec) {
            this.spec = spec;
        }

        /**
         * Sets pattern that section names must satisfy.
         *
         * @param section section name pattern
         * @return specification builder
         */
        INISpecificationBuilder section(Pattern section) {
            spec.section = section;
            return this;
        }

        /**
         * Sets pattern that property names must satisfy.
         *
         * @param property property name pattern
         * @return specification builder
         */
        INISpecificationBuilder property(Pattern property) {
            spec.property = property;
            return this;
        }

        /**
         * Creates INI specification.
         *
         * @return INI specification
         */
        public INISpecification create() {
            return spec;
        }

    }

}
