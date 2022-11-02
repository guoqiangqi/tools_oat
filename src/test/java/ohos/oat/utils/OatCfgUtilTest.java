/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ohos.oat.utils;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatFileFilter;
import ohos.oat.config.OatPolicy;
import ohos.oat.config.OatPolicyItem;
import ohos.oat.config.OatProject;
import ohos.oat.config.OatTask;

import org.junit.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenyaxun
 * @since 1.0
 */
public class OatCfgUtilTest {

    private OatConfig oatConfig;

    private String filePath;

    private String initOATCfgFile;

    private String sourceCodeRepoPath;

    @org.junit.Before
    public void setUp() throws Exception {
        this.oatConfig = new OatConfig();
        final String jarOatPkgPath = new File(this.getClass().getResource("/ohos/oat").getFile()).getCanonicalPath();
        final String jarRootPath = jarOatPkgPath.substring(0, jarOatPkgPath.length() - 8);
        this.oatConfig.setBasedir(jarRootPath + "");
        this.initOATCfgFile = jarRootPath + "OAT-Default-Test.xml";
        this.sourceCodeRepoPath = "";
        this.filePath = jarRootPath + "testproject/apache/A.java";
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void getShortPathWithFilePath() {
        final String result = OatCfgUtil.getShortPath(this.oatConfig, this.filePath);
        Assert.assertEquals(result, "testproject/apache/A.java");
    }

    @org.junit.Test
    public void getShortPath() {
        final File file = new File(this.filePath);
        final String result = OatCfgUtil.getShortPath(this.oatConfig, file);
        Assert.assertEquals(result, "testproject/apache/A.java");
    }

    @org.junit.Test
    public void formatPath() {
        String result = OatCfgUtil.formatPath("C:\\_chen\\TestFiles\\third_party\\zlib\\");
        Assert.assertEquals(result, "C:/_chen/TestFiles/third_party/zlib/");

        result = OatCfgUtil.formatPath(null);
        Assert.assertEquals(result, null);

        result = OatCfgUtil.formatPath("");
        Assert.assertEquals(result, "");

    }

    @org.junit.Test
    public void initOatConfig() {
        this.oatConfig.setPluginMode(false);
        this.oatConfig.setRepositoryName("");
        this.oatConfig.setPluginCheckMode("");
        this.oatConfig.setSrcFileList("");
        this.oatConfig.putData("TestMode", "true");
        this.oatConfig.putData("initOATCfgFile", this.initOATCfgFile);
        OatCfgUtil.initOatConfig(this.oatConfig, this.sourceCodeRepoPath);
        Assert.assertEquals(this.oatConfig.getBasedir(), "/home/OpenHarmony/");

        final List<OatTask> taskList;
        Map<String, OatPolicy> policyMap;
        Map<String, OatFileFilter> fileFilterMap;
        Map<String, List<String>> licenseText2NameMap;
        Map<String, List<String>> licenseCompatibilityMap;

        taskList = new ArrayList<>();
        OatTask task = new OatTask("defaultTask", "defaultPolicy", "defaultFilter", "");
        OatProject project = new OatProject("defaultProject", "defaultProject/", "defaultPolicy", "defaultFilter");
        task.addProject(project);
        // taskList.add(task);

        task = new OatTask("testTask", "defaultPolicy", "defaultFilter", "");
        final OatPolicy defaultPolicy = new OatPolicy("defaultPolicy", "");
        final OatFileFilter defaultFilter = new OatFileFilter("defaultFilter", "Files that do not need to be scanned");
        defaultFilter.addFilterItem("OAT*.xml|zunit|signature|Makefile|MANIFEST.MF|Kconfig|*.crt|*.markdown|*.git", "");
        defaultFilter.addFilterItem(".mk|.ld|.gitkeep|.gitignore|.gitattributes|.config|*.te|*.json|*.svg|*.swp", "");
        defaultFilter.addFilterItem("*.sandbox|*.rslp|*.rc|*.pydeps|*.properties|*.pluginmeta|*.php|*.sgml|.adoc|NEWS",
            "");
        defaultFilter.addFilterItem("*.patch|*.p7b|*.md|*.log|*.ini|*.html|*.htm|*.hml|*.hcs|__init__.py|MANIFEST.in",
            "");
        defaultFilter.addFilterItem(
            "*.hcb|*.gradle|*.gen|*.dic|*.d|*.css|*.cmake|*.cer|*.build|*.aff|*.err|*.pro|*.clang-format|*.ld", "");
        defaultFilter.addFilterItem(
            "*.babelrc|*.editorconfig|*.eslintignore|*.prettierrc|*.eslintrc.js|*.template|*.tmpl|*.vcproj|*.def|*"
                + ".sln", "");
        defaultFilter.addFilterItem(
            "*_contexts|*.vcxproj|*.vcxproj.filters|*.vcxproj.user|*visual_studio.sln|*.bundle|CERTIFICATE|SIGNATURE",
            "");
        defaultFilter.addFilterItem("*.lds|SConscript|*.pod|*.arb|*.repo", "");
        defaultFilter.addFilePathFilterItem("projectroot/target/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/output/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/out/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/log/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/logs/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/.idea/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/.git/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/.svn/.*", "");
        defaultFilter.addFilePathFilterItem("projectroot/[a-zA-Z0-9]{20,}.sh", "");

        final OatFileFilter defaultPolicyFilter = new OatFileFilter("defaultPolicyFilter", "");
        defaultPolicyFilter.addFilterItem("README.OpenSource", "");
        defaultPolicyFilter.addFilterItem("README", "");
        defaultPolicyFilter.addFilterItem("README.md", "");
        defaultPolicyFilter.addFilterItem("README_zh.md", "");

        final OatFileFilter copyrightPolicyFilter = new OatFileFilter("copyrightPolicyFilter", "");
        copyrightPolicyFilter.addFilterItem("README.OpenSource", "");
        copyrightPolicyFilter.addFilterItem("README", "");
        copyrightPolicyFilter.addFilterItem("README.md", "");
        copyrightPolicyFilter.addFilterItem("README_zh.md", "");

        final OatFileFilter licenseFileNamePolicyFilter = new OatFileFilter("licenseFileNamePolicyFilter", "");

        final OatFileFilter readmeFileNamePolicyFilter = new OatFileFilter("readmeFileNamePolicyFilter", "");

        final OatFileFilter readmeOpenSourcefileNamePolicyFilter = new OatFileFilter(
            "readmeOpenSourcefileNamePolicyFilter", "");

        final OatFileFilter binaryFileTypePolicyFilter = new OatFileFilter("binaryFileTypePolicyFilter", "");

        defaultPolicy.addPolicyItem(
            new OatPolicyItem("license", "Apache-2.0", "!.*LICENSE", "may", "defaultGroup", "defaultPolicyFilter", "",
                defaultPolicyFilter));
        defaultPolicy.addPolicyItem(
            new OatPolicyItem("license", "ApacheStyleLicense", ".*LICENSE", "may", "defaultGroup",
                "defaultPolicyFilter", "", defaultPolicyFilter));
        defaultPolicy.addPolicyItem(
            new OatPolicyItem("license", "Apache-2.0", ".*LICENSE", "may", "defaultGroup", "defaultPolicyFilter", "",
                defaultPolicyFilter));
        defaultPolicy.addPolicyItem(
            new OatPolicyItem("copyright", "Huawei Device Co., Ltd.", ".*", "may", "defaultGroup",
                "copyrightPolicyFilter", "", copyrightPolicyFilter));
        defaultPolicy.addPolicyItem(new OatPolicyItem("filename", "LICENSE", ".*/adapter/", "may", "defaultGroup",
            "licenseFileNamePolicyFilter", "", licenseFileNamePolicyFilter));
        defaultPolicy.addPolicyItem(new OatPolicyItem("filename", "LICENSE", "projectroot", "may", "defaultGroup",
            "licenseFileNamePolicyFilter", "", licenseFileNamePolicyFilter));
        defaultPolicy.addPolicyItem(new OatPolicyItem("filename", "README.md", "projectroot", "may", "defaultGroup",
            "readmeFileNamePolicyFilter", "", readmeFileNamePolicyFilter));
        defaultPolicy.addPolicyItem(new OatPolicyItem("filename", "README_zh.md", "projectroot", "may", "defaultGroup",
            "readmeFileNamePolicyFilter", "", readmeFileNamePolicyFilter));
        defaultPolicy.addPolicyItem(
            new OatPolicyItem("filetype", "!binary", ".*", "must", "defaultGroup", "binaryFileTypePolicyFilter", "",
                binaryFileTypePolicyFilter));
        defaultPolicy.addPolicyItem(
            new OatPolicyItem("filetype", "!archive", ".*", "must", "defaultGroup", "binaryFileTypePolicyFilter", "",
                binaryFileTypePolicyFilter));

        task.setPolicyData(defaultPolicy);
        task.setFileFilterObj(defaultFilter);

        project = new OatProject("testproject", "testproject/", "defaultPolicy", "defaultFilter");
        project.setOatPolicy(defaultPolicy);
        project.setFileFilterObj(defaultFilter);
        project.setLicenseFiles(new String[] {});
        task.addProject(project);
        taskList.add(task);

        Assert.assertEquals(this.oatConfig.getTaskList(), taskList);

    }

    @org.junit.Test
    public void getSplitStrings() {
        String[] result = OatCfgUtil.getSplitStrings("abc|def|ghij");
        Assert.assertArrayEquals(result, new String[] {"abc", "def", "ghij"});

        result = OatCfgUtil.getSplitStrings("");
        Assert.assertArrayEquals(result, new String[] {});

        result = OatCfgUtil.getSplitStrings("abcde");
        Assert.assertArrayEquals(result, new String[] {"abcde"});

    }

    @org.junit.Test
    public void testGetSplitStrings() {
        String[] result = OatCfgUtil.getSplitStrings("abc12345ghij", "12345");
        Assert.assertArrayEquals(result, new String[] {"abc", "ghij"});

        result = OatCfgUtil.getSplitStrings("abc|ghi|j", "\\|");
        Assert.assertArrayEquals(result, new String[] {"abc", "ghi", "j"});
    }
}