package org.apache.skywalking.uranus;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.requests.Requests;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.io.File;
import java.util.*;

@Slf4j
public class MavenUtils {
    private static final String REPO_ROOT_URL = "https://repo1.maven.org/maven2/";
    private static final String REPO_ROOT_MIRROR_URL = "https://maven.aliyun.com/nexus/content/groups/public/";

    public static MavenArtifact artifactMetadata(String groupId, String artifactId) {
        MavenArtifact mavenArtifact = new MavenArtifact().setGroupId(groupId).setArtifactId(artifactId);
        Preconditions.checkArgument(StringUtils.isNotBlank(groupId), "The parameter groupId cannot be black.");
        Preconditions.checkArgument(StringUtils.isNotBlank(artifactId), "The parameter artifactId cannot be black.");
        String groupIdUrl = getGroupIdUrl(groupId);
        String metaDataUrl = REPO_ROOT_MIRROR_URL + groupIdUrl + "/" + artifactId + "/maven-metadata.xml";
        String readToText = Requests.get(metaDataUrl).send().readToText();
        Map<String, Object> map;
        try {
            map = XmlMapConverter.xmlToMap(readToText);
        } catch (Exception e) {
            log.info("info error groupId:[{}] artifactId:[{}]", groupId, artifactId, e);
            return mavenArtifact;
        }
        mavenArtifact.setMetadataUrl(REPO_ROOT_URL + groupIdUrl + "/" + artifactId + "/maven-metadata.xml");
        mavenArtifact.setMirrorMetadataUrl(metaDataUrl);
        JSONObject jsonObject = JsonUtils.parseObject(JsonUtils.toJSONString(map));
        JSONObject versioning = jsonObject.getJSONObject("versioning");
        mavenArtifact.setLatest(jsonObject.getString("version"));
        mavenArtifact.setRelease(versioning.getString("release"));
        mavenArtifact.setLastUpdated(versioning.getString("lastUpdated"));
        JSONArray jsonArray = versioning.getJSONObject("versions").getJSONArray("version");
        List<String> strings = jsonArray.toJavaList(String.class);
        strings.sort(MavenUtils::compareVersion);
        mavenArtifact.setVersionList(strings);
        return mavenArtifact;
    }

    public static List<String> getVersionList(String groupId, String artifactId) {
        return artifactMetadata(groupId, artifactId).getVersionList();
    }

    private static String getGroupIdUrl(String groupId) {
        return StringUtils.replace(groupId, ".", "/");
    }

    public static String getJarFileUrl(MavenArtifact mavenArtifact) {
        return getJarFileUrl(mavenArtifact.getGroupId(), mavenArtifact.getArtifactId(), mavenArtifact.getVersion());
    }

    public static String getJarFileUrl(String groupId, String artifactId, String version) {
        return REPO_ROOT_MIRROR_URL + getGroupIdUrl(groupId) + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar";
    }

    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，x代表左边大，-x代表右边大
     * MavenUtils.compareVersion("2.7.0-rc2", "2.7.0")<0
     */
    public static int compareVersion(String v1, String v2) {
        return new ComparableVersion(v1).compareTo(new ComparableVersion(v2));
    }

    /**
     * 某版本的包中是否存在某类
     *
     * @param className
     * @param mavenArtifact
     * @return
     */
    public static boolean exists(String className, MavenArtifact mavenArtifact) {
        return getAllClassNameListByMavenArtifact(mavenArtifact).contains(className);
    }

    public static void fillArtifactInfo(String className, MavenArtifact mavenArtifact) {
        ArrayList<MavenArtifact> aliasArtifactList = new ArrayList<>(mavenArtifact.getAliasArtifactList());
        aliasArtifactList.add(0, mavenArtifact);

        for (MavenArtifact aliasArtifact : aliasArtifactList) {
            List<String> versionList = MavenUtils.getVersionList(aliasArtifact.getGroupId(), aliasArtifact.getArtifactId());
            aliasArtifact.setVersionList(versionList);
        }
        ArrayListMultimap<String, MavenArtifact> artifactArrayListMultimap = ArrayListMultimap.create();
        for (MavenArtifact artifact : aliasArtifactList) {
            for (String v : artifact.getVersionList()) {
                artifactArrayListMultimap.put(v, artifact);
            }
        }
        Set<String> strings = artifactArrayListMultimap.keySet();
        ArrayList<String> stringArrayList = new ArrayList<>(strings);
        stringArrayList.sort(MavenUtils::compareVersion);
        for (String version : stringArrayList) {
            List<MavenArtifact> mavenArtifacts = artifactArrayListMultimap.get(version);
            //同一版本所有包都不存在该类的次数
            int notExistsNum = 0;
            for (MavenArtifact artifact : mavenArtifacts) {
                if (!artifact.getVersionList().contains(version)) {
                    continue;
                }
                List<String> allClassNameListByMavenArtifact = getAllClassNameListByMavenArtifact(artifact.getGroupId(), artifact.getArtifactId(), version);
                boolean contains = allClassNameListByMavenArtifact.contains(className);
                if (!contains) {
                    notExistsNum++;
                }
                if (mavenArtifact.getFirstAddedArtifact() == null && contains) {
                    MavenArtifact firstAddedArtifact = new MavenArtifact()
                            .setGroupId(artifact.getGroupId())
                            .setArtifactId(artifact.getArtifactId())
                            .setVersion(version)
                            .setVersionList(artifact.getVersionList());
                    mavenArtifact.setFirstAddedArtifact(firstAddedArtifact);
                }
            }
            boolean remove = mavenArtifact.getFirstAddedArtifact() != null
                    && compareVersion(version, mavenArtifact.getFirstAddedArtifact().getVersion()) > 0
                    && notExistsNum == mavenArtifacts.size();
            if (remove) {
                mavenArtifact.setFirstRemovedVersion(version);
                break;
            }
        }
        aliasArtifactList.remove(mavenArtifact);
    }

    public static List<String> getNewClassList(MavenArtifact oldArtifact, List<MavenArtifact> newArtifactList) {
        return getNewClassList(Collections.singletonList(oldArtifact), newArtifactList);
    }

    public static List<String> getNewClassList(List<MavenArtifact> oldArtifactList, List<MavenArtifact> newArtifactList) {
        List<String> allOldClassNameListByMavenArtifact = Lists.newArrayList();
        List<String> allNewClassNameListByMavenArtifact = Lists.newArrayList();
        for (MavenArtifact oldArtifact : oldArtifactList) {
            allOldClassNameListByMavenArtifact.addAll(getAllClassNameListByMavenArtifact(oldArtifact));
        }
        for (MavenArtifact newArtifact : newArtifactList) {
            allNewClassNameListByMavenArtifact.addAll(getAllClassNameListByMavenArtifact(newArtifact));
        }
        allNewClassNameListByMavenArtifact.removeAll(allOldClassNameListByMavenArtifact);
        return allNewClassNameListByMavenArtifact;
    }

    public static List<String> getNewClassList(MavenArtifact oldArtifact, MavenArtifact newArtifact) {
        return getNewClassList(Collections.singletonList(oldArtifact), Collections.singletonList(newArtifact));
    }

    public static List<String> getOldClassList(List<MavenArtifact> oldArtifactList, List<MavenArtifact> newArtifactList) {
        return getNewClassList(newArtifactList, oldArtifactList);
    }

    public static List<String> getOldClassList(MavenArtifact oldArtifact, MavenArtifact newArtifact) {
        return getNewClassList(newArtifact, oldArtifact);
    }

    public static List<String> getOldClassList(MavenArtifact oldArtifact, List<MavenArtifact> newArtifactList) {
        return getNewClassList(newArtifactList, Collections.singletonList(oldArtifact));
    }

    public static List<String> getAllClassNameListByMavenArtifact(MavenArtifact mavenArtifact) {
        return getAllClassNameListByMavenArtifact(mavenArtifact.getGroupId(), mavenArtifact.getArtifactId(), mavenArtifact.getVersion());
    }

    public static String getCacheDirectory() {
        return FileUtils.getCurrentUserHomePath() + "/.m1";
    }

    @SneakyThrows
    public static List<String> getAllClassNameListByMavenArtifact(String groupId, String artifactId, String version) {
        String jarFileUrl = MavenUtils.getJarFileUrl(groupId, artifactId, version);
        String substring = jarFileUrl.substring(REPO_ROOT_MIRROR_URL.length() - 1);
        String directoryPath = FileUtils.getDirectoryPath(substring);
        File filePath = new File(getCacheDirectory() + directoryPath);
        FileUtils.forceMkdir(filePath);
        File file = new File(filePath.getPath() + File.separator + new File(substring).getName());
        if (!file.exists()) {
            byte[] bytes = Requests.get(jarFileUrl).socksTimeout(10000).send().readToBytes();
            FileUtils.writeByteArrayToFile(file, bytes);
        }
        try {
            return JarUtils.getClassNameListByJarFile(file);
        } catch (Exception e) {
            file.delete();
            return Collections.emptyList();
        }
    }
}
