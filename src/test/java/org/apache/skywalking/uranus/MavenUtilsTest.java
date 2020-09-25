package org.apache.skywalking.uranus;

import lombok.extern.slf4j.Slf4j;
import net.dongliu.requests.Requests;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MavenUtilsTest {


    private MavenArtifact oldMavenArtifact = new MavenArtifact()
            .setGroupId("org.mongodb")
            .setArtifactId("mongo-java-driver")
            .setVersion("3.6.0");

    private MavenArtifact currentMavenArtifact = new MavenArtifact()
            .setGroupId("org.mongodb")
            .setArtifactId("mongo-java-driver")
            .setVersion("3.7.0");

    private MavenArtifact newMavenArtifact = new MavenArtifact()
            .setGroupId("org.mongodb")
            .setArtifactId("mongo-java-driver")
            .setVersion("3.8.0");

    private List<MavenArtifact> newMavenArtifactList = Arrays.asList(newMavenArtifact, new MavenArtifact()
            .setGroupId("org.mongodb")
            .setArtifactId("mongodb-driver-sync")
            .setVersion("4.0.0"), new MavenArtifact()
            .setGroupId("org.mongodb")
            .setArtifactId("bson")
            .setVersion("4.0.0"));

    @Test
    void getAllClassNameList() {
        MavenArtifact mavenArtifact = new MavenArtifact()
                .setGroupId("org.mongodb")
                .setArtifactId("mongo-java-driver")
                .setVersion("3.8.0");
        List<String> allClassNameList = MavenUtils.getAllClassNameListByMavenArtifact(mavenArtifact);
        System.err.println(allClassNameList.size());
        allClassNameList.forEach(s -> System.err.println(s + "\r\n"));
    }

    @Test
    void getNewClassList() {
        List<String> newClassList = MavenUtils.getNewClassList(currentMavenArtifact, newMavenArtifactList);
        System.err.println(newClassList.size());
        newClassList.forEach(s -> System.err.println(s + "\r\n"));
    }

    @Test
    void getOldClassList() {
        List<String> oldClassList = MavenUtils.getOldClassList(currentMavenArtifact, newMavenArtifactList);
        System.err.println(oldClassList.size());
        oldClassList.forEach(s -> System.err.println(s + "\r\n"));
    }

    @Test
    void getNotOldNotNewClassList() {
        List<String> newOldClassList = MavenUtils.getNotOldNotNewClassList(Collections.singletonList(oldMavenArtifact), Collections.singletonList(currentMavenArtifact), newMavenArtifactList);
        System.err.println(newOldClassList.size());
        newOldClassList.forEach(s -> System.err.println(s + "\r\n"));
    }

    @Test
    void findTargetClassInfoInArtifacts() {
        MavenArtifact mavenArtifact = new MavenArtifact()
                .setGroupId("org.mongodb")
                .setArtifactId("mongo-java-driver")
                .setAliasArtifactList(
                        Arrays.asList(
                                new MavenArtifact().setGroupId("org.mongodb").setArtifactId("mongodb-driver-sync"),
                                new MavenArtifact().setGroupId("org.mongodb").setArtifactId("mongodb-driver-core"),
                                new MavenArtifact().setGroupId("org.mongodb").setArtifactId("bson")
                        )
                );
        MavenUtils.getTargetClassInfoInArtifacts("com.mongodb.client.MongoClientImpl", mavenArtifact);
        log.info("mavenArtifact:[{}]", JsonUtils.toJSONString(mavenArtifact));
    }

    @Test
    void exists() {
        boolean exists = MavenUtils.exists("com.mongodb.client.MongoClientImpl",
                new MavenArtifact()
                        .setGroupId("org.mongodb")
                        .setArtifactId("mongo-java-driver")
                        .setVersion("3.6.0"));
        assertFalse(exists);
    }

    @Test
    public void getVersionList() {
        List<String> versionList = MavenUtils.getVersionList("org.mongodb", "mongo-java-driver");
        log.info("versionList:[{}]" + JsonUtils.toJSONString(versionList));
        assertTrue(versionList.size() > 0);
    }

    @Test
    void getJarFileUrl() {
        String jarFileUrl = MavenUtils.getJarFileUrl("org.mongodb", "mongodb-driver-core", "4.1.0");
        log.info("jarFileUrl[{}]:", jarFileUrl);
        assertThat(Requests.head(jarFileUrl).send().statusCode(), is(200));
    }

    @Test
    void compareVersion() {
        assertTrue(MavenUtils.compareVersion("3.8.0", "3.10") < 0);
        assertTrue(MavenUtils.compareVersion("2.7.0-rc2", "2.7.0") < 0);
        assertEquals(0, MavenUtils.compareVersion("2.7.0", "2.7.0"));
    }
}