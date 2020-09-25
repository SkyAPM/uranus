package org.apache.skywalking.uranus.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.skywalking.uranus.JsonUtils;
import org.apache.skywalking.uranus.MavenArtifact;
import org.apache.skywalking.uranus.MavenUtils;

import java.util.List;

public class NotOldNotNewClass extends BaseBootstrap {

    public static void main(String[] args) {
        parseOptions(args, true);
    }

    public static void parseOptions(String[] args, boolean old) {
        Options options = new Options();
        options.addOption(
                Option.builder("o")
                        .longOpt("old")
                        .hasArg(true)
                        .required(true)
                        .desc("old artifacts(groupId:artifactId:version,groupId:artifactId:version).")
                        .build());
        options.addOption(
                Option.builder("c")
                        .longOpt("current")
                        .hasArg(true)
                        .required(true)
                        .desc("current artifacts(groupId:artifactId:version,groupId:artifactId:version).")
                        .build());
        options.addOption(
                Option.builder("n")
                        .longOpt("new")
                        .hasArg(true)
                        .required(true)
                        .desc("new artifacts(groupId:artifactId:version,groupId:artifactId:version).")
                        .build());
        withJsonOutputOption(options);
        withCount(options);
        CommandLine commandLine = getCommandLine(options, args);
        String oldArtifacts = commandLine.getOptionValue("o");
        String currentArtifacts = commandLine.getOptionValue("c");
        String newArtifacts = commandLine.getOptionValue("n");

        List<MavenArtifact> oldArtifactList = getArtifactList(oldArtifacts);
        List<MavenArtifact> currentArtifactList = getArtifactList(currentArtifacts);
        List<MavenArtifact> newArtifactList = getArtifactList(newArtifacts);
        List<String> classList = MavenUtils.getNotOldNotNewClassList(oldArtifactList, currentArtifactList, newArtifactList);
        if (commandLine.hasOption("j")) {
            System.out.println(JsonUtils.toJSONString(classList));
        } else {
            classList.forEach(s -> System.out.println(s + "\r\n"));
        }
        if (commandLine.hasOption("s")) {
            System.out.println("size:" + classList.size());
        }
    }


}
