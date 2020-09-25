package org.apache.skywalking.uranus.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.uranus.JsonUtils;
import org.apache.skywalking.uranus.MavenArtifact;
import org.apache.skywalking.uranus.MavenUtils;

import java.util.ArrayList;
import java.util.List;

public class TargetClassInfoInArtifacts extends BaseBootstrap {

    public static void main(String[] args) {
        parseOptions(args);
    }

    public static void parseOptions(String[] args) {
        Options options = getOptionsWithGidAid();
        options.addOption(
                Option.builder("c")
                        .longOpt("class")
                        .hasArg(true)
                        .desc("class fully name.")
                        .required(true)
                        .build());
        options.addOption(
                Option.builder("a")
                        .longOpt("alias")
                        .hasArg(true)
                        .desc("alias artifacts(groupId:artifactId,groupId:artifactId)")
                        .build());
        CommandLine commandLine = getCommandLine(options, args);
        String className = commandLine.getOptionValue("c");
        String groupId = commandLine.getOptionValue("gid");
        String artifactId = commandLine.getOptionValue("aid");
        String aliasArtifacts = commandLine.getOptionValue("a");

        MavenArtifact mavenArtifact = new MavenArtifact();
        mavenArtifact.setGroupId(groupId);
        mavenArtifact.setArtifactId(artifactId);
        if (StringUtils.isNotBlank(aliasArtifacts)) {
            mavenArtifact.setAliasArtifactList(getArtifactListByGroupIdAndArtifactId(aliasArtifacts));
        }
        MavenArtifact classInfoInArtifacts = MavenUtils.getTargetClassInfoInArtifacts(className, mavenArtifact);
        System.out.println(JsonUtils.toJSONString(classInfoInArtifacts));
    }

    private static List<MavenArtifact> getArtifactListByGroupIdAndArtifactId(String artifacts) {
        List<MavenArtifact> list = new ArrayList<>();
        String[] split = StringUtils.split(artifacts, ',');
        for (String s : split) {
            String[] artifact = StringUtils.split(s, ':');
            if (artifact.length != 2) {
                throw new RuntimeException("artifacts format is error");
            }
            MavenArtifact mavenArtifact = new MavenArtifact()
                    .setGroupId(artifact[0])
                    .setArtifactId(artifact[1]);
            list.add(mavenArtifact);
        }
        return list;
    }
}
