package org.apache.skywalking.uranus.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.skywalking.uranus.JsonUtils;
import org.apache.skywalking.uranus.MavenUtils;

import java.util.List;

public class ArtifactVersions extends BaseBootstrap {

    public static void main(String[] args) {
        parseOptions(args);
    }

    private static void parseOptions(String[] args) {
        Options options = getOptionsWithGidAid();
        withJsonOutputOption(options);
        withCount(options);
        CommandLine commandLine = getCommandLine(options, args);
        List<String> versionList = MavenUtils.getVersionList(commandLine.getOptionValue("gid"), commandLine.getOptionValue("aid"));
        if (commandLine.hasOption("j")) {
            System.out.println(JsonUtils.toJSONString(versionList));
        } else {
            versionList.forEach(System.out::println);
        }
        if (commandLine.hasOption("s")) {
            System.out.println("size:" + versionList.size());
        }
    }
}
