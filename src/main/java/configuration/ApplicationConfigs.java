// ApplicationConfigs.java
//
// Thread-safe application configuration backed by Apache Commons CLI.
// Uses volatile + double-checked locking because initialization requires
// command-line arguments that are only available at startup.

package configuration;

import infrastructure.networking.Protocol;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationConfigs {
    public static final Protocol PROTOCOL = new Protocol("HTTP", "1.1");
    public static final Set<String> SUPPORTED_COMPRESSIONS = new HashSet<>(List.of("gzip"));
    private static volatile ApplicationConfigs instance;
    private final CommandLine cmd;

    /** Initializes the singleton with command-line arguments. Must be called once at startup. */
    public static void init(String[] args) throws ParseException {
        if (instance == null) {
            synchronized (ApplicationConfigs.class) {
                if (instance == null) {
                    instance = new ApplicationConfigs(args);
                }
            }
        }
    }

    public static ApplicationConfigs getInstance() {
        return instance;
    }

    private ApplicationConfigs(String[] args) throws ParseException {
        var options = new Options();
        options.addOption("d", "directory", true, "Directory holding server files");

        var parser = new DefaultParser();
        cmd = parser.parse(options, args);
    }

    public String getFilesDirectory() {
        return cmd.getOptionValue("d");
    }
}
