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

package org.liscju.jini.cli;

/**
 * Assignment.
 */
class Assignment {

    /**
     * Assignment variable (left side of assignment).
     */
    private final String variable;

    /**
     * Assignment value (right side of assignment).
     */
    private final String value;

    /**
     * Constructs assignment.
     *
     * @param variable variable name
     * @param value variable value
     */
    private Assignment(String variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    /**
     * Parses assignment.
     *
     * @param assignment textual assignment
     * @return assignment
     */
    public static Assignment parse(String assignment) {
        if (assignment == null)
            return new Assignment("", "");
        assignment = assignment.trim();
        if (assignment.isEmpty())
            return new Assignment("", "");
        if (assignment.equals("="))
            return new Assignment("", "");

        int equal = assignment.indexOf('=');
        if (equal == -1)
            return new Assignment(assignment, "");
        if (equal == 0)
            return new Assignment("", assignment.substring(1).trim());
        if (equal == assignment.length() - 1)
            return new Assignment(
                assignment.substring(0, assignment.length() - 1).trim()
                , ""
            );

        String variable = assignment.substring(0, equal).trim();
        String value = assignment.substring(equal + 1).trim();
        return new Assignment(variable, value);
    }

    /**
     * Returns variable assigned.
     *
     * @return variable
     */
    public String variable() {
        return variable;
    }

    /**
     * Returns value assigned to variable.
     *
     * @return value
     */
    public String value() {
        return value;
    }

}
