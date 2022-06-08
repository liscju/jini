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
import java.util.Objects;

/**
 * Thrown when INI file does not conform to specification.
 */
public class INISpecViolationException extends RuntimeException {

    /**
     * Violation details.
     */
    private final String detail;

    /**
     * Specification requirements.
     */
    private final List<String> requirements;

    /**
     * Constructs INI specification violation exception.
     *
     * @param detail violation details
     */
    public INISpecViolationException(String detail) {
        Objects.requireNonNull(detail);

        this.detail = detail;
        this.requirements = new LinkedList<>();
    }

    /**
     * Constructs INI specification violation exception.
     *
     * @param detail violation details
     * @param requirements specification requirements
     */
    public INISpecViolationException(String detail, List<String> requirements) {
        Objects.requireNonNull(detail);
        Objects.requireNonNull(requirements);

        this.detail = detail;
        this.requirements = requirements;
    }

    /**
     * Gets violation details.
     *
     * @return violation details
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Gets specification requirements.
     *
     * @return requirements
     */
    public List<String> getRequirements() {
        return requirements;
    }

}
