package org.apache.skywalking.uranus.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.skywalking.uranus.MavenUtils;

public class ArtifactExistsClass extends BaseBootstrap {

    public static void main(String[] args) {
        parseOptions(args);
    }

    private static void parseOptions(String[] args) {
        Options options = getOptionsWithGidAidVersion();
        options.addOption(
                Option.builder("c")
                        .longOpt("class")
                        .hasArg(true)
                        .numberOfArgs(1)
                        .desc("class fully name")
                        .required(true)
                        .build());
        CommandLine commandLine = getCommandLine(options, args);
        System.out.println(MavenUtils.exists(commandLine.getOptionValue("c"), commandLine.getOptionValue("gid"),
                commandLine.getOptionValue("aid"), commandLine.getOptionValue("v")));
    }
}
