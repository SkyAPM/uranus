package org.apache.skywalking.uranus.bootstrap;

import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.uranus.MavenArtifact;

import java.util.ArrayList;
import java.util.List;

public class BaseBootstrap {

    protected static Options getOptionsWithGidAid() {
        Options options = new Options();
        options.addOption(
                Option.builder("aid")
                        .longOpt("artifactId")
                        .hasArg(true)
                        .numberOfArgs(1)
                        .desc("artifactId")
                        .required(true)
                        .build());

        options.addOption(
                Option.builder("gid")
                        .longOpt("groupId")
                        .hasArg(true)
                        .numberOfArgs(1)
                        .desc("groupId")
                        .required(true)
                        .build());
        return options;
    }

    protected static Options getOptionsWithGidAidVersion() {
        Options options = getOptionsWithGidAid();
        options.addOption(
                Option.builder("v")
                        .longOpt("version")
                        .hasArg(true)
                        .numberOfArgs(1)
                        .desc("artifactId version")
                        .required(true)
                        .build());
        return options;
    }

    protected static Options withJsonOutputOption(Options options) {
        options.addOption(
                Option.builder("j")
                        .longOpt("json")
                        .hasArg(false)
                        .desc("Output in json format.")
                        .build());
        return options;
    }

    protected static Options withCount(Options options) {
        options.addOption(
                Option.builder("s")
                        .longOpt("size")
                        .hasArg(false)
                        .desc("size.")
                        .build());
        return options;
    }

    @SneakyThrows
    public static CommandLine getCommandLine(Options options, String[] args) {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(220);

        CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (MissingOptionException e) {
            hf.printHelp("artifact-versions", options, true);
            System.exit(1);
        }
        return new CommandLine.Builder().build();
    }

    protected static List<MavenArtifact> getArtifactList(String artifacts) {
        List<MavenArtifact> list = new ArrayList<>();
        String[] split = StringUtils.split(artifacts, ',');
        for (String s : split) {
            String[] artifact = StringUtils.split(s, ':');
            if (artifact.length != 3) {
                throw new RuntimeException("artifacts format is error");
            }
            MavenArtifact mavenArtifact = new MavenArtifact()
                    .setGroupId(artifact[0])
                    .setArtifactId(artifact[1])
                    .setVersion(artifact[2]);
            list.add(mavenArtifact);
        }
        return list;
    }
}
