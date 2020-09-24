package org.apache.skywalking.uranus;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Accessors(chain = true)
@Data
public class MavenArtifact implements Serializable {

    private static final long serialVersionUID = -4721607536018568393L;

    private String groupId;

    private String artifactId;

    private List<MavenArtifact> aliasArtifactList = new ArrayList<>();

    private String version;

    /**
     * 该jar包所有版本号
     */
    private List<String> versionList;
    /**
     * 最早出现的版本
     */
    private MavenArtifact firstAddedArtifact;
    /**
     * 最早被移除的版本
     */
    private String firstRemovedVersion;

    private String metadataUrl;
    private String mirrorMetadataUrl;

    private String latest;
    private String release;
    private String lastUpdated;
}