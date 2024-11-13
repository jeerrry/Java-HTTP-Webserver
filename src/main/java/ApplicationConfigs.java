import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ApplicationConfigs {
    private static ApplicationConfigs instance;
    private final CommandLine cmd;

    public static ApplicationConfigs getInstance(String[] args) throws ParseException {
        if (instance == null) {
            instance = new ApplicationConfigs(args);
        }

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
