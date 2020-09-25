package org.apache.skywalking.uranus.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.skywalking.uranus.JsonUtils;
import org.apache.skywalking.uranus.MavenUtils;

import java.util.List;

public class AllClass extends BaseBootstrap {

    public static void main(String[] args) {
        parseOptions(args);
    }

    private static void parseOptions(String[] args) {
        Options options = getOptionsWithGidAidVersion();
        withJsonOutputOption(options);
        withCount(options);
        CommandLine commandLine = getCommandLine(options, args);
        List<String> allClassNameListByMavenArtifact = MavenUtils.getAllClassNameListByMavenArtifact(commandLine.getOptionValue("gid"),
                commandLine.getOptionValue("aid"), commandLine.getOptionValue("v"));
        if (commandLine.hasOption("j")) {
            System.out.println(JsonUtils.toJSONString(allClassNameListByMavenArtifact));
        } else {
            allClassNameListByMavenArtifact.forEach(s -> System.out.println(s + "\r\n"));
        }
        if (commandLine.hasOption("s")) {
            System.out.println("size:" + allClassNameListByMavenArtifact.size());
        }
    }


}
