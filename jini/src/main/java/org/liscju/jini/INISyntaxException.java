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
 * Thrown to indicate INI file has syntax error.
 */
public class INISyntaxException extends RuntimeException {

    /**
     * Line number with syntax error.
     */
    private int lineNo;

    /**
     * Line with syntax error.
     */
    private String line;

    /**
     * Explanation of the syntax error.
     */
    private String explanation;

    /**
     * Constructs INI syntax exception for specified line.
     *
     * @param lineNo line number
     * @param line line content
     * @param explanation explanation of an error
     */
    public INISyntaxException(
            int lineNo,
            String line,
            String explanation) {
        this.lineNo = lineNo;
        this.line = line;
        this.explanation = explanation;
    }

    /**
     * Gets line number with syntax error.
     *
     * @return line number
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * Gets line with syntax error.
     *
     * @return line
     */
    public String getLine() {
        return line;
    }

    /**
     * Gets syntax error explanation.
     *
     * @return explanation
     */
    public String getExplanation() {
        return explanation;
    }

}
