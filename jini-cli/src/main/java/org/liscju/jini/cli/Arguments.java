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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Comma separated argument parser.
 */
class Arguments implements Iterable<String> {

    /**
     * List of arguments.
     */
    private List<String> arguments;

    /**
     * Creates empty argument list.
     */
    private Arguments() {
        arguments = new LinkedList<>();
    }

    /**
     * Parses arguments.
     *
     * @param arguments arguments to parse
     * @return arguments
     */
    public static Arguments parse(String arguments) {
        if (arguments == null)
            return new Arguments();
        arguments = arguments.trim();
        if (arguments.isEmpty())
            return new Arguments();

        Arguments instance = new Arguments();

        String[] args = arguments.split(",");
        for (String arg : args) {
            if (arg.isEmpty())
                continue;

            instance.arguments.add(arg.trim());
        }

        return instance;
    }

    @Override
    public Iterator<String> iterator() {
        return arguments.iterator();
    }

    /**
     * Returns count of arguments.
     *
     * @return count of arguments
     */
    public int count() {
        return arguments.size();
    }

    /**
     * Gets argument at specified index.
     *
     * @param index index of argument
     * @return argument or null when index out of range
     * ({@code index < 0 || index >= count()})
     */
    public String at(int index) {
        if (index < 0)
            return null;
        if (index >= arguments.size())
            return null;

        return arguments.get(index);
    }

    /**
     * Returns argument list.
     *
     * @return argument list
     */
    public List<String> list() {
        return new LinkedList<>(arguments);
    }

}
