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
     * All version numbers of the jar package
     */
    private List<String> versionList;
    /**
     * Earliest version
     */
    private MavenArtifact firstAddedArtifact;
    /**
     * Earliest version removed
     */
    private String firstRemovedVersion;

    private String metadataUrl;
    private String mirrorMetadataUrl;

    private String latest;
    private String release;
    private String lastUpdated;
}