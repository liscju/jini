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

import org.liscju.jini.INI;
import org.liscju.jini.INIConfiguration;
import org.liscju.jini.INIPath;
import org.liscju.jini.INISpecViolationException;
import org.liscju.jini.INISyntaxException;
import picocli.CommandLine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "jini"
    , mixinStandardHelpOptions = true
    , versionProvider = JINI.VersionProvider.class
    , description = "jini - tool for manipulating configuration in INI files"
)
public class JINI implements Callable<Integer> {

    /**
     * Version provider from JAR manifest.
     */
    static class VersionProvider implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() throws Exception {
            return new String[] {
                "jini " + JINI.class.getPackage().getImplementationVersion()
            };
        }
    }

    @CommandLine.Option(
        names = {"-c", "--create"}
        , description = "Create new configuration."
    )
    private boolean create;

    @CommandLine.ArgGroup(
        exclusive = false
    )
    private TransformOptions transform;

    /**
     * Transforming INI options.
     */
    static class TransformOptions {

        @CommandLine.Option(
            names = {"-f", "--filter"}
            , description = "Filter properties matching PATH(s)."
            , paramLabel = "PATH"
        )
        private String filter;

        @CommandLine.Option(
            names = {"-p", "--put"}
            , description = "Put properties matching ASSIGNMENT(s)."
            , paramLabel = "ASSIGNMENT"
        )
        private String put;

    @CommandLine.Option(
            names = {"-d", "--delete"}
            , description = "Delete properties matching PATH(s)."
            , paramLabel = "PATH"
        )
        private String delete;

    }

    @CommandLine.ArgGroup()
    private WriteOptions write;

    /**
     * Write options.
     */
    static class WriteOptions {

        @CommandLine.Option(
            names = {"-l", "--list"}
            , description = "Write list of properties."
        )
        private boolean list;

        @CommandLine.Option(
            names = {"-s", "--sections"}
            , description = "Write section names."
        )
        private boolean sections;

        @CommandLine.Option(
            names = {"-g", "--get"}
            , description = "Write properties matching PATH(s)."
            , paramLabel = "PATH"
        )
        private String get;

    }

    @CommandLine.Option(
        names = {"-o", "--output-file"}
        , description = "Write output to FILE."
        , paramLabel = "FILE"
    )
    private File output;

    @CommandLine.Parameters(
        arity = "0..*"
        , description = "INI configuration FILE(s)."
        , paramLabel = "FILE"
    )
    private File[] files;

    @Override
    public Integer call() {
        try {
            OutputStream out = chooseOutputStream();

            INIConfiguration conf;
            if (create)
                conf = new INIConfiguration();
            else
                conf = configuration(collectINIs());

            if (transform != null) {
                if (transform.filter != null)
                    conf = filterProperties(conf);

                if (transform.delete != null)
                    deleteProperties(conf);

                if (transform.put != null)
                    putProperties(conf);
            }

            if (write != null) {
                if (write.list)
                    printPropertyList(conf, out);

                if (write.sections)
                    printSections(conf, out);

                if (write.get != null)
                    printProperties(conf, out);
            } else
                printConfiguration(conf, out);

            return 0;
        } catch (JINIException ex) {
            System.err.println(ex.getDetail());
            return 1;
        } catch (RuntimeException ex) {
            System.err.println("Unexpected error, stack trace:");
            System.err.println("----------------------------------------------");
            ex.printStackTrace(System.err);
            System.err.println("----------------------------------------------");
            System.err.println(
                "Submit bug providing full command line, input INI and stack "
                + "trace as issue in https://github.com/liscju/jini"
            );
            return 2;
        }
    }

    /**
     * Chooses output stream.
     *
     * @return output stream
     */
    private OutputStream chooseOutputStream() {
        try {
            OutputStream out = System.out;
            if (output != null)
                out = new FileOutputStream(output);

            return out;
        } catch (FileNotFoundException ex) {
            throw new JINIException(
                String.format(
                    "Output file '%s' cannot be opened for writing: %s"
                    , output
                    , ex.getMessage()
                )
            );
        }
    }

    /**
     * Collects INIs from files or standard input.
     *
     * @return INIs
     */
    private List<INI> collectINIs() {
        if (files == null)
            return Collections.singletonList(collectINIFromSTDIN());
        else
            return collectINIsFromFiles();
    }

    /**
     * Creates collective INI configuration from INIs.
     *
     * @param inis INIs
     * @return INI configuration
     */
    private INIConfiguration configuration(List<INI> inis) {
        INIConfiguration conf = new INIConfiguration();

        for (INI ini : inis)
            conf.extend(INIConfiguration.fromINI(ini));

        return conf;
    }

    /**
     * Collects INI from standard input.
     *
     * @return INI
     */
    private INI collectINIFromSTDIN() {
        try {
            INI ini = new INI();
            InputStreamReader reader = new InputStreamReader(System.in);

            ini.load(reader);
            INIConfiguration.verify(ini);

            reader.close();
            return ini;
        } catch (UncheckedIOException ex) {
            throw new JINIException(
                String.format(
                    "Unexpected error while reading standard input: %s"
                    , ex.getMessage()
                )
            );
        } catch (IOException ex) {
            throw new JINIException(
                String.format(
                    "Standard input cannot be closed: %s"
                    , ex.getMessage()
                )
            );
        } catch (INISyntaxException ex) {
            throw new JINIException(
                String.format(
                    "Syntax error in standard input: %s\n  %d: %s"
                    , ex.getExplanation()
                    , ex.getLineNo()
                    , ex.getLine()
                )
            );
        } catch (INISpecViolationException ex) {
            String err = String.format(
                "INI from STDIN does not conform to specification: %s"
                , ex.getDetail()
            );
            if (!ex.getRequirements().isEmpty()) {
                err += "\n\nINI must satisfy the following requirements:";
                for (String requirement : ex.getRequirements())
                    err += String.format("\n  * %s", requirement);
            }
            throw new JINIException(err);
        }
    }

    /**
     * Collect INIs from input files.
     *
     * @return INIs
     */
    private List<INI> collectINIsFromFiles() {
        List<INI> inis = new LinkedList<>();

        for (File file : files)
            inis.add(loadINI(file));

        return inis;
    }

    /**
     * Loads INI file.
     *
     * @param file file
     * @return INI
     */
    private INI loadINI(File file) {
        try {
            INI ini = new INI();
            FileReader reader = new FileReader(file);

            ini.load(reader);
            INIConfiguration.verify(ini);

            reader.close();
            return ini;
        } catch (FileNotFoundException ex) {
            throw new JINIException(
                String.format("File '%s' not found", file.getAbsolutePath())
            );
        } catch (IOException ex) {
            throw new JINIException(
                String.format(
                    "File '%s' cannot be closed: %s"
                    , file.getAbsolutePath()
                    , ex.getMessage()
                )
            );
        } catch (UncheckedIOException ex) {
            throw new JINIException(
                String.format(
                    "File '%s' cannot be read: %s"
                    , file.getAbsolutePath()
                    , ex.getMessage()
                )
            );
        } catch (INISyntaxException ex) {
            throw new JINIException(
                String.format(
                    "Syntax error in file '%s': %s\n%d: %s"
                    , file.getAbsolutePath()
                    , ex.getExplanation()
                    , ex.getLineNo()
                    , ex.getLine()
                )
            );
        } catch (INISpecViolationException ex) {
            String err = String.format(
                "INI from file '%s' does not conform to specification: %s"
                , file.getAbsolutePath()
                , ex.getDetail()
            );
            if (!ex.getRequirements().isEmpty()) {
                err += "\n\nINI must satisfy the following requirements:";
                for (String requirement : ex.getRequirements())
                    err += String.format("\n  * %s", requirement);
            }
            throw new JINIException(err);
        }
    }

    /**
     * Puts properties in INI configuration.
     *
     * @param conf configuration
     */
    private void putProperties(INIConfiguration conf) {
        for (String assignment : Arguments.parse(transform.put)) {
            if (assignment.isEmpty())
                continue;

            Assignment assign = Assignment.parse(assignment);
            String ppath = assign.variable();
            String value = assign.value();

            if (ppath.isEmpty())
                throw new JINIException(
                    String.format(
                        "Put assignment '%s' without property is illegal"
                        , assignment
                    )
                );

            try {
                conf.put(ppath, value);
            } catch (IllegalArgumentException ex) {
                throw new JINIException(
                    String.format(
                        "Put assignment '%s' has invalid property name"
                        , assignment
                    )
                );
            }
        }
    }

    /**
     * Deletes properties from INI configuration.
     *
     * @param conf configuration
     */
    private void deleteProperties(INIConfiguration conf) {
        for (String ppath : Arguments.parse(transform.delete)) {
            if (conf.sections().contains(ppath)) {
                String section = ppath;
                for (String property : conf.properties(section))
                    conf.delete(section, property);
            } else
                for (String path : conf.properties(INIPath.of(ppath)))
                    conf.delete(path);
        }
    }

    /**
     * Filters properties.
     *
     * @param conf configuration
     * @return filtered configuration
     */
    private INIConfiguration filterProperties(INIConfiguration conf) {
        List<String> ppaths = Arguments.parse(transform.filter).list();
        Map<String, String> properties = findProperties(conf, ppaths);

        INIConfiguration filtered = new INIConfiguration();
        for (String property : properties.keySet())
            filtered.put(property, properties.get(property));
        return filtered;
    }

    /**
     * Prints property list from INI configuration to output.
     *
     * @param conf configuration
     * @param out output
     */
    private void printPropertyList(INIConfiguration conf, OutputStream out) {
        PrintWriter writer = new PrintWriter(out);

        Set<String> sections = conf.sections();

        if (sections.contains("."))
            for (String property : conf.properties("."))
                writer.println(property);

        sections.remove(".");

        for (String section : sections)
            for (String property : conf.properties(section))
                writer.println(String.join(".", section, property));

        writer.flush();
    }

    /**
     * Prints sections of INI configuration to output.
     *
     * @param conf configuration
     * @param out output
     */
    private void printSections(INIConfiguration conf, OutputStream out) {
        PrintWriter writer = new PrintWriter(out);

        Set<String> sections = conf.sections();
        if (sections.remove("."))
            writer.println(".");
        for (String section : sections)
            writer.println(section);

        writer.flush();
    }

    /**
     * Prints properties of INI configuration to output.
     *
     * @param conf configuration
     * @param out output
     */
    private void printProperties(INIConfiguration conf, OutputStream out) {
        try {
            PrintWriter writer = new PrintWriter(out);

            Arguments args = Arguments.parse(write.get);
            if (args.count() == 1 && conf.get(args.at(0)) != null) {
                String ppath = args.at(0);
                String value = conf.get(ppath);
                if (value != null)
                    writer.println(value);
            } else {
                Map<String, String> properties = findProperties(conf, args.list());
                for (String ppath : properties.keySet())
                    writer.println(ppath + "=" + properties.get(ppath));
            }

            writer.flush();
        } catch (UncheckedIOException ex) {
            throw new JINIException(
                String.format(
                    "Writing properties to output ended with error: %s"
                    , ex.getMessage()
                )
            );
        }
    }

    /**
     * Finds properties from configuration.
     *
     * @param conf configuration
     * @param ppaths properties paths
     * @return properties as mapping between properties paths and values
     */
    private Map<String, String> findProperties(
            INIConfiguration conf,
            List<String> ppaths) {
        Map<String, String> properties = new LinkedHashMap<>();

        for (String ppath : ppaths) {
            if (ppath.startsWith(".")
                && ppath.length() > 1
                && !ppath.startsWith(".."))
                ppath = ppath.substring(1);

            if (conf.sections().contains(ppath)) {
                String section = ppath;

                if (section.equals(INIConfiguration.DEFAULT_SECTION))
                    for (String property : conf.properties(section))
                        properties.put(property, conf.get(property));
                else
                    for (String property : conf.properties(section))
                        properties.put(
                            String.join(".", section, property)
                            , conf.get(section, property)
                        );
            } else
                for (String path : conf.properties(INIPath.of(ppath)))
                    properties.put(path, conf.get(path));
        }

        return properties;
    }

    /**
     * Prints INI configuration to output.
     *
     * @param conf configuration
     * @param out output
     */
    private void printConfiguration(INIConfiguration conf, OutputStream out) {
        try {
            INI ini = conf.toINI();
            ini.store(new PrintWriter(out, true));
        } catch (UncheckedIOException ex) {
            throw new JINIException(
                String.format(
                    "Writing configuration to output ended with error: %s"
                    , ex.getMessage()
                )
            );
        }
    }

    /**
     * JINI processing exception.
     */
    private static class JINIException extends RuntimeException {

        /**
         * Details of the exception.
         */
        private String detail;

        /**
         * Constructs JINI exception with specified detail.
         *
         * @param detail detail message
         */
        public JINIException(String detail) {
            this.detail = detail;
        }

        /**
         * Gets detail of the exception.
         *
         * @return detail
         */
        public String getDetail() {
            return detail;
        }

    }

    /**
     * Application starting point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new JINI()).execute(args);
        System.exit(exitCode);
    }

}
