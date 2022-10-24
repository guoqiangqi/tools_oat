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
 *
 * ChangeLog:
 * 2021.1 - Load OAT config file and stored in OhosConfig data struct to support task, project, processfilter,
 * policy, and reportfilter  customization capability.
 * 2021.3 - Add filetype, filename policy item to support check specified file type or file name in a directory.
 * 2021.3 - Support merge project OAT.xml to default OAT.xml, support integration with pipleline tools.
 * 2021.4 - Add licensematcherlist to support user defined license match rules.
 * 2021.6 - Support ignore project OAT configuration.
 * Modified by jalenchen
 */

package ohos.oat.utils;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatFileFilter;
import ohos.oat.config.OatPolicy;
import ohos.oat.config.OatPolicyItem;
import ohos.oat.config.OatProject;
import ohos.oat.config.OatTask;
import ohos.oat.input.model.OatCommandLineFilterPara;
import ohos.oat.input.model.OatCommandLinePolicyPara;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

/**
 * Stateless utility class for OAT config file
 *
 * @author chenyaxun
 * @since 1.0
 */
public final class OatCfgUtil {
    private static final String ROOTNODE = "oatconfig."; // Root node name in OAT config file

    /**
     * Private constructure to prevent new instance
     */
    private OatCfgUtil() {
    }

    /**
     * Get the short file path without base dir
     *
     * @param oatConfig Config in oat.xml
     * @param file File
     * @return File path without base dir
     */
    public static String getShortPath(final OatConfig oatConfig, final File file) {
        String filepath = OatFileUtils.getFileCanonicalPath(file);
        if (file.isDirectory()) {
            filepath = filepath + "/";
        }
        final String tmpFilepath = OatCfgUtil.formatPath(filepath);
        if (tmpFilepath == null) {
            return "";
        }
        return tmpFilepath.replace(oatConfig.getBasedir(), "");

    }

    /**
     * Format file path, use '/' instead of '\'
     *
     * @param path File path
     * @return File path with '/' file separator
     */
    public static String formatPath(final String path) {
        if (null == path || path.equals("")) {
            return path;
        }
        return path.trim().replace('\\', '/');
    }

    /**
     * Get the short file path without base dir
     *
     * @param oatConfig Config in oat.xml
     * @param filepath File path
     * @return File path without base dir
     */
    public static String getShortPath(final OatConfig oatConfig, final String filepath) {
        final String tmpFilepath = OatCfgUtil.formatPath(filepath);
        if (tmpFilepath == null) {
            return "";
        }
        return tmpFilepath.replace(oatConfig.getBasedir(), "");
    }

    /**
     * Read OAT config file and init OatConfig data structure
     *
     * @param oatConfig OatConfig data structure to be initiate
     * @param rootDir Root dir, while the run mode is plugin, this parameter is the single project root dir, otherwise
     * it is empty
     */
    public static void initOatConfig(final OatConfig oatConfig, final String rootDir) {
        final String defaultOatConfigFilePath = oatConfig.getData("initOATCfgFile");
        final XMLConfiguration xmlconfig = OatCfgUtil.getXmlConfiguration(defaultOatConfigFilePath);

        String tmpDir = "";
        if (rootDir.length() > 0) {
            tmpDir = rootDir;
        } else {
            tmpDir = OatCfgUtil.getValue(xmlconfig, "basedir");
        }
        final File tmpFile = new File(tmpDir);
        if (tmpFile.exists()) {
            tmpDir = OatCfgUtil.formatPath(OatFileUtils.getFileCanonicalPath(tmpFile));
        } else {
            OatLogUtil.println("", "The basedir is invalid, please check it.");
            final String mode = oatConfig.getData("TestMode");
            if (mode == null || mode.equals("false")) {
                System.exit(0);
            }
        }

        if (!tmpDir.endsWith("/")) {
            tmpDir += "/";
        }
        oatConfig.setBasedir(tmpDir);
        OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(), "basedir: " + tmpDir);
        OatCfgUtil.initLicenseMatcher(oatConfig, xmlconfig, null);
        OatCfgUtil.initCompatibilityLicense(oatConfig, xmlconfig, null);
        OatCfgUtil.initTask(oatConfig, xmlconfig);
        OatCfgUtil.initPolicy(oatConfig, xmlconfig, null);
        OatCfgUtil.initFilter(oatConfig, xmlconfig, null);
        oatConfig.reArrangeData();

        final String flag = oatConfig.getData("IgnoreProjectOAT");
        if (flag != null && flag.equals("true")) {
            return;
        }
        // If there is OAT.xml in project dir while in the plugin mode, combine all the configuration together
        if (oatConfig.isPluginMode()) {
            final OatTask oatTask = oatConfig.getTaskList().get(0);
            final OatProject oatProject = oatTask.getProjectList().get(0);
            OatCfgUtil.updateProjectConfig(oatConfig, tmpDir, oatProject);
        } else {
            final List<OatTask> taskList = oatConfig.getTaskList();
            for (final OatTask oatTask : taskList) {
                final List<OatProject> projectList = oatTask.getProjectList();
                for (final OatProject oatProject : projectList) {
                    final String prjDirectory = oatConfig.getBasedir() + oatProject.getPath();
                    OatCfgUtil.updateProjectConfig(oatConfig, prjDirectory, oatProject);
                }
            }
        }
    }

    /**
     * Trnasfor string in OAT config file to array split by '|'
     *
     * @param configString Config string in OAT config file
     * @return String array
     */
    public static String[] getSplitStrings(final String configString) {
        return OatCfgUtil.getSplitStrings(configString, "\\|");
    }

    public static String[] getSplitStrings(final String configString, final String splitflag) {
        String[] strings = new String[] {};
        if (configString.trim().length() <= 0) {
            return strings;
        }
        if ((splitflag.contains("|") && (!configString.contains("|"))) || (!splitflag.contains("|")
            && (!configString.contains(splitflag)))) {
            strings = new String[] {configString};
            return strings;
        }

        try {
            strings = configString.split(splitflag);
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
        }

        return strings;
    }

    private static void updateProjectConfig(final OatConfig oatConfig, final String projectDir,
        final OatProject oatProject) {
        final String prjOatFile = projectDir + "OAT.xml";
        final File oatFile = new File(prjOatFile);

        if (oatFile.exists()) {
            final StringBuilder stringBuilder = new StringBuilder();
            try {
                final FileInputStream fis = new FileInputStream(prjOatFile);
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

                final BufferedReader fileReader = new BufferedReader(isr);
                String line = "\n";
                while ((line = fileReader.readLine()) != null) {
                    stringBuilder.append("\n" + line);
                }
                oatProject.putData("ProjectOAT", stringBuilder.toString());
                IOUtils.closeQuietly(fileReader);

            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            final XMLConfiguration prjXmlconfig = OatCfgUtil.getXmlConfiguration(prjOatFile);
            final String licensefile = OatCfgUtil.getValue(prjXmlconfig, "licensefile");
            final String[] licenselist = OatCfgUtil.getSplitStrings(licensefile);
            if (licenselist != null && licenselist.length > 0) {
                oatProject.setLicenseFiles(licenselist);
            }
            OatCfgUtil.initLicenseMatcher(oatConfig, prjXmlconfig, oatProject);
            OatCfgUtil.initCompatibilityLicense(oatConfig, prjXmlconfig, oatProject);
            final String bIgnoreProjectPolicy = oatConfig.getData("IgnoreProjectPolicy");
            if (bIgnoreProjectPolicy.equals("true")) {
                OatCfgUtil.initFilter(oatConfig, prjXmlconfig, oatProject);
            } else {
                OatCfgUtil.initPolicy(oatConfig, prjXmlconfig, oatProject);
                OatCfgUtil.initFilter(oatConfig, prjXmlconfig, oatProject);
            }

        }
    }

    private static XMLConfiguration getXmlConfiguration(final String defaultOatConfigFilePath) {
        XMLConfiguration xmlconfig = new XMLConfiguration(); // XML file original infomation
        final Configurations configs = new Configurations();
        try {
            xmlconfig = configs.xml(defaultOatConfigFilePath);
            final DefaultExpressionEngine engine = new DefaultExpressionEngine(
                DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
            xmlconfig.setExpressionEngine(engine);
        } catch (final org.apache.commons.configuration2.ex.ConfigurationException e) {
            OatLogUtil.traceException(e);
        }
        return xmlconfig;
    }

    private static void initFilter(final OatConfig oatConfig, final XMLConfiguration xmlconfig,
        final OatProject oatProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> fileFilterListCfg = OatCfgUtil.getElements(xmlconfig,
            OatCfgUtil.ROOTNODE, "filefilterlist.filefilter");
        for (final HierarchicalConfiguration<ImmutableNode> fileFilterCfg : fileFilterListCfg) {
            final OatFileFilter oatFileFilter = new OatFileFilter();
            oatFileFilter.setName(OatCfgUtil.getElementAttrValue(fileFilterCfg, "name"));
            oatFileFilter.setDesc(OatCfgUtil.getElementAttrValue(fileFilterCfg, "desc"));
            if (oatProject == null) {
                // Global OAT XML
                oatConfig.addFileFilter(oatFileFilter);
            }
            final List<HierarchicalConfiguration<ImmutableNode>> fileFilterItemListCfg = fileFilterCfg.configurationsAt(
                "filteritem");
            OatCfgUtil.initFilterItems(oatProject, oatFileFilter, fileFilterItemListCfg);

            // Init global OAT filter using command line para, this will work together with OAT.xml in project
            if (oatProject == null && oatFileFilter.getName()
                .equals("defaultFilter")) {//oatProject == null means init global OAT.xml
                final String filterstring = oatConfig.getData("filter");
                if (filterstring != null && filterstring.length() > 0) {
                    final OatFileFilter oatFileFilter1 = OatCommandLineFilterPara.getOatFileFilter(filterstring);
                    oatFileFilter.merge(oatFileFilter1);
                }
            }

            OatCfgUtil.initFilterItems2Policy(oatProject, oatFileFilter);
            OatCfgUtil.initFilterItems2Project(oatProject, oatFileFilter);
        } // end of filefilter
    }

    private static void initFilterItems2Project(final OatProject oatProject, final OatFileFilter oatFileFilter) {
        // Merge filter items to project filter
        if (oatProject != null && oatProject.getFileFilter().equals(oatFileFilter.getName())) {
            final OatFileFilter fileFilter = oatProject.getFileFilterObj();
            if (fileFilter != null) {
                fileFilter.merge(oatFileFilter);
            } else {
                oatProject.setFileFilterObj(oatFileFilter);
            }
        }
    }

    private static void initFilterItems2Policy(final OatProject oatProject, final OatFileFilter oatFileFilter) {
        // Merge filter items to policy filter
        if (!(oatProject != null && oatProject.getOatPolicy() != null)) {
            return;
        }
        for (final OatPolicyItem allPolicyItem : oatProject.getOatPolicy().getAllPolicyItems()) {
            if (allPolicyItem.getFileFilter().equals(oatFileFilter.getName())) {
                final OatFileFilter fileFilter = allPolicyItem.getFileFilterObj();
                if (fileFilter != null) {
                    fileFilter.merge(oatFileFilter);
                } else {
                    allPolicyItem.setFileFilterObj(oatFileFilter);
                }
            }
        }
    }

    private static void initFilterItems(final OatProject oatProject, final OatFileFilter oatFileFilter,
        final List<HierarchicalConfiguration<ImmutableNode>> fileFilterItemListCfg) {
        for (final HierarchicalConfiguration<ImmutableNode> fileFilterItemCfg : fileFilterItemListCfg) {
            final String type = OatCfgUtil.getElementAttrValue(fileFilterItemCfg, "type");
            String name = OatCfgUtil.getElementAttrValue(fileFilterItemCfg, "name");
            final String desc = OatCfgUtil.getElementAttrValue(fileFilterItemCfg, "desc");
            if (!type.equals("filepath")) {
                if (oatProject != null) {
                    oatFileFilter.addFilterItem(oatProject.getPath(), name, desc);
                } else {
                    oatFileFilter.addFilterItem(name, desc);
                }
                if (oatProject != null && (!oatFileFilter.getName().contains("dir name underproject"))) {
                    // Project OAT XML
                    OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                        oatProject.getPath() + "\tFilter\t" + oatFileFilter.getName() + "\t\t" + "\tFileName\t" + name
                            + "\tDesc\t" + desc);
                }
                continue;
            }

            if (oatProject != null) {
                if (name.startsWith("!")) {
                    name = "!" + oatProject.getPath() + name.substring(1);
                } else {
                    name = oatProject.getPath() + name;
                }
            }
            oatFileFilter.addFilePathFilterItem(name, desc);
            if (oatProject != null) {
                // Project OAT XML
                OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                    oatProject.getPath() + "\tFilter\t" + oatFileFilter.getName() + "\t\t" + "\tFilePath\t" + name
                        + "\tDesc\t" + desc);
            }
        } // end of filter items

    }

    private static void initPolicy(final OatConfig oatConfig, final XMLConfiguration xmlconfig,
        final OatProject oatProject) {
        // Init global OAT configuration using command line para, not OAT.xml in project
        if (oatProject == null) {//oatProject == null means init global OAT.xml
            final String policystring = oatConfig.getData("policy");
            if (policystring != null && policystring.length() > 0) {
                final OatPolicy oatPolicy = OatCommandLinePolicyPara.getOatPolicy(policystring);
                oatPolicy.setName("defaultPolicy");
                oatConfig.addPolicy(oatPolicy);

                final OatPolicy thirdOatPolicy = OatCommandLinePolicyPara.getOatPolicy(policystring);
                oatPolicy.setName("3rdDefaultPolicy");
                oatConfig.addPolicy(oatPolicy);

                return;
            }
        }

        final List<HierarchicalConfiguration<ImmutableNode>> policylistCfg = OatCfgUtil.getElements(xmlconfig,
            OatCfgUtil.ROOTNODE, "policylist.policy");
        // Start init policy
        for (final HierarchicalConfiguration<ImmutableNode> policyCfg : policylistCfg) {
            final OatPolicy oatPolicy = new OatPolicy();
            oatPolicy.setName(OatCfgUtil.getElementAttrValue(policyCfg, "name"));
            oatPolicy.setDesc(OatCfgUtil.getElementAttrValue(policyCfg, "desc"));
            if (oatProject == null) {
                // Global OAT XML
                oatConfig.addPolicy(oatPolicy);
            }
            final List<HierarchicalConfiguration<ImmutableNode>> policyitemlistCfg = policyCfg.configurationsAt(
                "policyitem");
            for (final HierarchicalConfiguration<ImmutableNode> policyitemCfg : policyitemlistCfg) {
                final OatPolicyItem oatPolicyItem = new OatPolicyItem();
                oatPolicyItem.setName(OatCfgUtil.getElementAttrValue(policyitemCfg, "name"));
                oatPolicyItem.setType(OatCfgUtil.getElementAttrValue(policyitemCfg, "type"));
                String policyPath = OatCfgUtil.getElementAttrValue(policyitemCfg, "path");
                if (oatProject != null) {
                    policyPath = oatProject.getPath() + policyPath;
                }
                oatPolicyItem.setPath(policyPath);
                oatPolicyItem.setRule(OatCfgUtil.getElementAttrValue(policyitemCfg, "rule", "may"));
                oatPolicyItem.setGroup(OatCfgUtil.getElementAttrValue(policyitemCfg, "group", "defaultGroup"));
                final String policyType = oatPolicyItem.getType();
                final String policyName = oatPolicyItem.getName();

                final String filterName = OatCfgUtil.getFilterName(policyType, policyName);
                oatPolicyItem.setFileFilter(OatCfgUtil.getElementAttrValue(policyitemCfg, "filefilter", filterName));
                oatPolicyItem.setDesc(OatCfgUtil.getElementAttrValue(policyitemCfg, "desc"));
                if (oatProject != null) {
                    // Project OAT XML
                    oatPolicyItem.setFileFilterObj(oatConfig.getOatFileFilter(oatPolicyItem.getFileFilter()));
                    oatProject.getOatPolicy().addPolicyItem(oatPolicyItem);
                    if (oatProject != null) {
                        // Project OAT XML
                        OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                            oatProject.getPath() + "\tPolicyItem\t" + oatPolicyItem.getType() + "Policy\tName\t"
                                + oatPolicyItem.getName() + "\tPath\t" + oatPolicyItem.getPath() + "\tDesc\t"
                                + oatPolicyItem.getDesc());
                    }
                } else {
                    // Global OAT XML
                    oatPolicy.addPolicyItem(oatPolicyItem);
                }
            } // End of policy items
        } // end of policy
    }

    @NotNull
    public static String getFilterName(final String policyType, final String policyName) {
        String tmpFilterName = "defaultPolicyFilter";
        if (policyType.equals("copyright")) {
            tmpFilterName = "copyrightPolicyFilter";
        } else if (policyType.equals("filename") && policyName.equals("LICENSE")) {
            tmpFilterName = "licenseFileNamePolicyFilter";
        } else if (policyType.equals("filename") && policyName.contains("README.OpenSource")) {
            tmpFilterName = "readmeOpenSourcefileNamePolicyFilter";
        } else if (policyType.equals("filename") && policyName.contains("README")) {
            tmpFilterName = "readmeFileNamePolicyFilter";
        } else if (policyType.equals("filetype")) {
            tmpFilterName = "binaryFileTypePolicyFilter";
        }
        return tmpFilterName;
    }

    private static void initLicenseMatcher(final OatConfig oatConfig, final XMLConfiguration xmlconfig,
        final OatProject oatProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> licenseMatcherlistCfg = OatCfgUtil.getElements(xmlconfig,
            OatCfgUtil.ROOTNODE, "licensematcherlist.licensematcher");
        for (final HierarchicalConfiguration<ImmutableNode> licenseMatcherCfg : licenseMatcherlistCfg) {
            final String licenseName = OatCfgUtil.getElementAttrValue(licenseMatcherCfg, "name");

            final List<HierarchicalConfiguration<ImmutableNode>> licenseTextListCfg
                = licenseMatcherCfg.configurationsAt("licensetext");
            for (final HierarchicalConfiguration<ImmutableNode> licenseTextCfg : licenseTextListCfg) {
                final String licenseText = OatCfgUtil.getElementAttrValue(licenseTextCfg, "name");
                if (oatProject == null) {
                    oatConfig.addLicenseText(licenseName, licenseText);
                    OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                        "GlobalConfig" + "\taddGlobalLicenseText\t" + "customizedLicenseConfig\tName\t" + licenseName
                            + "\t \t \tLicenseText\t" + licenseText);
                } else {
                    oatProject.addPrjLicenseText(licenseName, licenseText);
                    OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                        oatProject.getPath() + "\taddPrjLicenseText\t" + "customizedLicenseConfig\tName\t" + licenseName
                            + "\t \t \tLicenseText\t" + licenseText);
                }
            }
        }
    }

    private static void initCompatibilityLicense(final OatConfig oatConfig, final XMLConfiguration xmlconfig,
        final OatProject oatProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> compatibilityCfg = OatCfgUtil.getElements(xmlconfig,
            OatCfgUtil.ROOTNODE, "licensecompatibilitylist.license");
        for (final HierarchicalConfiguration<ImmutableNode> license : compatibilityCfg) {
            final String licenseName = OatCfgUtil.getElementAttrValue(license, "name");

            final List<HierarchicalConfiguration<ImmutableNode>> compatibilityLicenseCfg = license.configurationsAt(
                "compatibilitylicense");
            for (final HierarchicalConfiguration<ImmutableNode> compatibilitylicense : compatibilityLicenseCfg) {
                final String compatibilitylicenseName = OatCfgUtil.getElementAttrValue(compatibilitylicense, "name");
                if (oatProject == null) {
                    oatConfig.addCompatibilityLicense(licenseName, compatibilitylicenseName);
                    OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                        "GlobalConfig" + "\taddCompatibilityLicense\t" + "compatibilityLicenseConfig\tName\t"
                            + licenseName + "\t \t \tLicenseText\t" + compatibilitylicenseName);
                } else {
                    oatProject.addPrjCompatibilityLicense(licenseName, compatibilitylicenseName);
                    OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
                        oatProject.getPath() + "\taddPrjCompatibilityLicense\t" + "compatibilityLicenseConfig\tName\t"
                            + licenseName + "\t \t \tLicenseText\t" + compatibilitylicenseName);
                }
            }
        }
    }

    private static void initTask(final OatConfig oatConfig, final XMLConfiguration xmlconfig) {
        final List<HierarchicalConfiguration<ImmutableNode>> tasklistCfg = OatCfgUtil.getElements(xmlconfig,
            OatCfgUtil.ROOTNODE, "tasklist.task");
        OatTask defaultOatTask = null;
        for (final HierarchicalConfiguration<ImmutableNode> taskCfg : tasklistCfg) {
            final OatTask oatTask = new OatTask();
            oatTask.setNamne(OatCfgUtil.getElementAttrValue(taskCfg, "name"));
            oatTask.setPolicy(OatCfgUtil.getElementAttrValue(taskCfg, "policy"));
            oatTask.setDesc(OatCfgUtil.getElementAttrValue(taskCfg, "desc"));
            oatTask.setFileFilter(OatCfgUtil.getElementAttrValue(taskCfg, "filefilter"));

            final List<HierarchicalConfiguration<ImmutableNode>> projectlistCfg = taskCfg.configurationsAt("project");
            boolean containReps = false;
            for (final HierarchicalConfiguration<ImmutableNode> projectCfg : projectlistCfg) {
                final OatProject oatProject = new OatProject();
                final String prjName = OatCfgUtil.getElementAttrValue(projectCfg, "name");
                OatCfgUtil.initProjectBasicinfo(projectCfg, oatProject, prjName);
                if (!oatConfig.isPluginMode()) {
                    oatTask.addProject(oatProject);
                    continue;
                }

                if (oatConfig.getRepositoryName().equals(prjName)) {
                    containReps = true;
                    oatTask.addProject(oatProject);
                    break;
                }
                if ((!(oatConfig.getRepositoryName().equals(prjName))) && oatTask.getNamne().equals("defaultTask")) {
                    oatProject.setName(oatConfig.getRepositoryName());
                    oatProject.setPath(oatConfig.getRepositoryName() + "/");
                    OatCfgUtil.initProjectDefaultPolicy(oatConfig, oatProject);
                    oatTask.addProject(oatProject);
                }
            } // end of project list

            if (oatConfig.isPluginMode()) {
                if (containReps) {
                    oatConfig.addTask(oatTask);
                    break;
                }
                if (oatTask.getNamne().equals("defaultTask")) {
                    defaultOatTask = oatTask;
                }

            }
            if (!(oatConfig.isPluginMode()) && (!oatTask.getNamne().equals("defaultTask"))) {
                oatConfig.addTask(oatTask);
            }
        } // end of tasklist
        if (oatConfig.isPluginMode() && oatConfig.getTaskList().size() <= 0) {
            oatConfig.addTask(defaultOatTask);
        }
    }

    private static void initProjectBasicinfo(final HierarchicalConfiguration<ImmutableNode> projectCfg,
        final OatProject oatProject, final String prjName) {
        oatProject.setName(prjName);
        String tmpPath = OatCfgUtil.getElementAttrValue(projectCfg, "path");
        if (!tmpPath.endsWith("/")) {
            tmpPath += "/";
        }
        if (tmpPath.startsWith("/")) {
            tmpPath = tmpPath.substring(1);
        }
        oatProject.setPath(tmpPath);
        oatProject.setPolicy(OatCfgUtil.getElementAttrValue(projectCfg, "policy"));
        oatProject.setFileFilter(OatCfgUtil.getElementAttrValue(projectCfg, "filefilter"));
        final String[] licenselist = OatCfgUtil.getSplitStrings(
            OatCfgUtil.getElementAttrValue(projectCfg, "licensefile"));
        OatCfgUtil.cleanFileName(licenselist);
        oatProject.setLicenseFiles(licenselist);
    }

    private static void initProjectDefaultPolicy(final OatConfig oatConfig, final OatProject oatProject) {
        if (oatConfig.getRepositoryName().toLowerCase(Locale.ENGLISH).contains("third_party")) {
            oatProject.setPolicy("3rdDefaultPolicy");
            oatProject.setUpstreamPrj(true);
        } else {
            oatProject.setPolicy("defaultPolicy");
        }
    }

    private static void cleanFileName(final String[] licenselist) {
        for (int i = 0; i < licenselist.length; i++) {
            String tmpstr = licenselist[i];
            if (tmpstr.startsWith("/")) {
                tmpstr = tmpstr.substring(1);
                licenselist[i] = tmpstr;
            }
        }
    }

    private static String getElementAttrValue(final HierarchicalConfiguration<ImmutableNode> element, final String attr,
        final String defaultValue) {
        final HierarchicalConfiguration column = element;
        String value = "";
        try {
            value = column.getString("[@" + attr + "]");
        } catch (final Exception e) {
            return defaultValue;
        }
        return value == null ? defaultValue : OatCfgUtil.formatPath(value);
    }

    private static String getElementAttrValue(final HierarchicalConfiguration<ImmutableNode> element,
        final String attr) {
        return OatCfgUtil.getElementAttrValue(element, attr, "");
    }

    private static List<HierarchicalConfiguration<ImmutableNode>> getElements(final XMLConfiguration xmlconfig,
        final String rootnode, final String path) {
        final List<HierarchicalConfiguration<ImmutableNode>> elements = xmlconfig.configurationsAt(rootnode + path);
        return elements;
    }

    private static String getValue(final XMLConfiguration xmlconfig, final String path) {
        final String value = xmlconfig.getString(OatCfgUtil.ROOTNODE + path);
        return value == null ? "" : OatCfgUtil.formatPath(value);
    }

    private static String getAttrValue(final XMLConfiguration xmlconfig, final String rootnode, final String path,
        final String attr) {
        final String value = xmlconfig.getString(rootnode + path + "[@" + attr + "]");
        return value == null ? "" : OatCfgUtil.formatPath(value);
    }
}
