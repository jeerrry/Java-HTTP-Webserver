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
    private final CommandLine cmd;
    private static ApplicationConfigs instance;

    public static void init(String[] args) throws ParseException {
        if (instance == null) {
            instance = new ApplicationConfigs(args);
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
