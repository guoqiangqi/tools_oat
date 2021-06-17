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

import ohos.oat.config.OhosConfig;
import ohos.oat.config.OhosFileFilter;
import ohos.oat.config.OhosPolicy;
import ohos.oat.config.OhosPolicyItem;
import ohos.oat.config.OhosProject;
import ohos.oat.config.OhosTask;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.ImmutableNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Stateless utility class for OAT config file
 *
 * @author chenyaxun
 * @since 1.0
 */
public final class OhosCfgUtil {
    private static final String ROOTNODE = "oatconfig."; // Root node name in OAT config file

    /**
     * Private constructure to prevent new instance
     */
    private OhosCfgUtil() {
    }

    /**
     * Get the short file path without base dir
     *
     * @param ohosConfig Config in oat.xml
     * @param file File
     * @return File path without base dir
     */
    public static String getShortPath(final OhosConfig ohosConfig, final File file) {
        try {
            String filepath = file.getCanonicalPath();
            if (file.isDirectory()) {
                filepath = filepath + "/";
            }
            final String tmpFilepath = OhosCfgUtil.formatPath(filepath);
            return tmpFilepath.replace(ohosConfig.getBasedir(), "");
        } catch (final IOException e) {
            OhosLogUtil.traceException(e);
        }
        return "";
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
     * @param ohosConfig Config in oat.xml
     * @param filepath File path
     * @return File path without base dir
     */
    public static String getShortPath(final OhosConfig ohosConfig, final String filepath) {
        final String tmpFilepath = OhosCfgUtil.formatPath(filepath);
        return tmpFilepath.replace(ohosConfig.getBasedir(), "");
    }

    /**
     * Read OAT config file and init OhosConfig data structure
     *
     * @param ohosConfig Ohosconfig data structure to be initiate
     * @param defaultOatConfigFilePath OAT config file path
     * @param rootDir Root dir, while the run mode is plugin, this parameter is the single project root dir, otherwise
     * it is empty
     */
    public static void initOhosConfig(final OhosConfig ohosConfig, final String defaultOatConfigFilePath,
        final String rootDir) {
        final XMLConfiguration xmlconfig = OhosCfgUtil.getXmlConfiguration(defaultOatConfigFilePath);

        String tmpDir = "";
        if (rootDir.length() > 0) {
            tmpDir = rootDir;
        } else {
            tmpDir = OhosCfgUtil.getValue(xmlconfig, "basedir");
        }
        if (!tmpDir.endsWith("/")) {
            tmpDir += "/";
        }
        ohosConfig.setBasedir(tmpDir);
        OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(), "basedir: " + tmpDir);
        OhosCfgUtil.initLicenseMatcher(ohosConfig, xmlconfig, null);
        OhosCfgUtil.initCompatibilityLicense(ohosConfig, xmlconfig, null);
        OhosCfgUtil.initTask(ohosConfig, xmlconfig);
        OhosCfgUtil.initPolicy(ohosConfig, xmlconfig, null);
        OhosCfgUtil.initFilter(ohosConfig, xmlconfig, null);
        ohosConfig.reArrangeData();

        final String flag = ohosConfig.getData("IgnoreProjectOAT");
        if (flag != null && flag.equals("true")) {
            return;
        }
        // If there is OAT.xml in project dir while in the plugin mode, combine all the configuration together
        if (ohosConfig.isPluginMode()) {
            final OhosTask ohosTask = ohosConfig.getTaskList().get(0);
            final OhosProject ohosProject = ohosTask.getProjectList().get(0);
            OhosCfgUtil.updateProjectConfig(ohosConfig, tmpDir, ohosProject);
        } else {
            final List<OhosTask> taskList = ohosConfig.getTaskList();
            for (final OhosTask ohosTask : taskList) {
                final List<OhosProject> projectList = ohosTask.getProjectList();
                for (final OhosProject ohosProject : projectList) {
                    final String prjDirectory = ohosConfig.getBasedir() + ohosProject.getPath();
                    OhosCfgUtil.updateProjectConfig(ohosConfig, prjDirectory, ohosProject);
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
        return getSplitStrings(configString, "\\|");
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
            OhosLogUtil.traceException(e);
        }

        return strings;
    }

    private static void updateProjectConfig(final OhosConfig ohosConfig, final String projectDir,
        final OhosProject ohosProject) {
        final String prjOatFile = projectDir + "OAT.xml";
        final File oatFile = new File(prjOatFile);

        if (oatFile.exists()) {
            final XMLConfiguration prjXmlconfig = OhosCfgUtil.getXmlConfiguration(prjOatFile);
            final String licensefile = OhosCfgUtil.getValue(prjXmlconfig, "licensefile");
            final String[] licenselist = OhosCfgUtil.getSplitStrings(licensefile);
            if (licenselist != null && licenselist.length > 0) {
                ohosProject.setLicenseFiles(licenselist);
            }
            OhosCfgUtil.initLicenseMatcher(ohosConfig, prjXmlconfig, ohosProject);
            OhosCfgUtil.initCompatibilityLicense(ohosConfig, prjXmlconfig, ohosProject);
            OhosCfgUtil.initPolicy(ohosConfig, prjXmlconfig, ohosProject);
            OhosCfgUtil.initFilter(ohosConfig, prjXmlconfig, ohosProject);
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
            OhosLogUtil.traceException(e);
        }
        return xmlconfig;
    }

    private static void initFilter(final OhosConfig ohosConfig, final XMLConfiguration xmlconfig,
        final OhosProject ohosProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> fileFilterListCfg = OhosCfgUtil.getElements(xmlconfig,
            OhosCfgUtil.ROOTNODE, "filefilterlist.filefilter");
        for (final HierarchicalConfiguration<ImmutableNode> fileFilterCfg : fileFilterListCfg) {
            final OhosFileFilter ohosFileFilter = new OhosFileFilter();
            ohosFileFilter.setName(OhosCfgUtil.getElementAttrValue(fileFilterCfg, "name"));
            ohosFileFilter.setDesc(OhosCfgUtil.getElementAttrValue(fileFilterCfg, "desc"));
            if (ohosProject == null) {
                // Global OAT XML
                ohosConfig.addFileFilter(ohosFileFilter);
            }
            final List<HierarchicalConfiguration<ImmutableNode>> fileFilterItemListCfg = fileFilterCfg.configurationsAt(
                "filteritem");
            initFilterItems(ohosProject, ohosFileFilter, fileFilterItemListCfg);
            initFilterItems2Policy(ohosProject, ohosFileFilter);
            initFilterItems2Project(ohosProject, ohosFileFilter);
        } // end of filefilter
    }

    private static void initFilterItems2Project(final OhosProject ohosProject, final OhosFileFilter ohosFileFilter) {
        // Merge filter items to project filter
        if (ohosProject != null && ohosProject.getFileFilter().equals(ohosFileFilter.getName())) {
            final OhosFileFilter fileFilter = ohosProject.getFileFilterObj();
            if (fileFilter != null) {
                fileFilter.merge(ohosFileFilter);
            } else {
                ohosProject.setFileFilterObj(ohosFileFilter);
            }
        }
    }

    private static void initFilterItems2Policy(final OhosProject ohosProject, final OhosFileFilter ohosFileFilter) {
        // Merge filter items to policy filter
        if (!(ohosProject != null && ohosProject.getOhosPolicy() != null)) {
            return;
        }
        for (final OhosPolicyItem allPolicyItem : ohosProject.getOhosPolicy().getAllPolicyItems()) {
            if (allPolicyItem.getFileFilter().equals(ohosFileFilter.getName())) {
                final OhosFileFilter fileFilter = allPolicyItem.getFileFilterObj();
                if (fileFilter != null) {
                    fileFilter.merge(ohosFileFilter);
                } else {
                    allPolicyItem.setFileFilterObj(ohosFileFilter);
                }
            }
        }
    }

    private static void initFilterItems(final OhosProject ohosProject, final OhosFileFilter ohosFileFilter,
        final List<HierarchicalConfiguration<ImmutableNode>> fileFilterItemListCfg) {
        for (final HierarchicalConfiguration<ImmutableNode> fileFilterItemCfg : fileFilterItemListCfg) {
            final String type = OhosCfgUtil.getElementAttrValue(fileFilterItemCfg, "type");
            String name = OhosCfgUtil.getElementAttrValue(fileFilterItemCfg, "name");
            final String desc = OhosCfgUtil.getElementAttrValue(fileFilterItemCfg, "desc");
            if (!type.equals("filepath")) {
                ohosFileFilter.addFilterItem(name);
                if (ohosProject != null && (!ohosFileFilter.getName().contains("dir name underproject"))) {
                    // Project OAT XML
                    OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                        ohosProject.getPath() + "\tFilter\t" + ohosFileFilter.getName() + "\t\t" + "\tFileName\t" + name
                            + "\tDesc\t" + desc);
                }
                continue;
            }

            if (ohosProject != null) {
                if (name.startsWith("!")) {
                    name = "!" + ohosProject.getPath() + name.substring(1);
                } else {
                    name = ohosProject.getPath() + name;
                }
            }
            ohosFileFilter.addFilePathFilterItem(name);
            if (ohosProject != null) {
                // Project OAT XML
                OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                    ohosProject.getPath() + "\tFilter\t" + ohosFileFilter.getName() + "\t\t" + "\tFilePath\t" + name
                        + "\tDesc\t" + desc);
            }
        } // end of filter items
    }

    private static void initPolicy(final OhosConfig ohosConfig, final XMLConfiguration xmlconfig,
        final OhosProject ohosProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> policylistCfg = OhosCfgUtil.getElements(xmlconfig,
            OhosCfgUtil.ROOTNODE, "policylist.policy");
        for (final HierarchicalConfiguration<ImmutableNode> policyCfg : policylistCfg) {
            final OhosPolicy ohosPolicy = new OhosPolicy();
            ohosPolicy.setNamne(OhosCfgUtil.getElementAttrValue(policyCfg, "name"));
            ohosPolicy.setDesc(OhosCfgUtil.getElementAttrValue(policyCfg, "desc"));
            if (ohosProject == null) {
                // Global OAT XML
                ohosConfig.addPolicy(ohosPolicy);
            }
            final List<HierarchicalConfiguration<ImmutableNode>> policyitemlistCfg = policyCfg.configurationsAt(
                "policyitem");
            for (final HierarchicalConfiguration<ImmutableNode> policyitemCfg : policyitemlistCfg) {
                final OhosPolicyItem ohosPolicyItem = new OhosPolicyItem();
                ohosPolicyItem.setName(OhosCfgUtil.getElementAttrValue(policyitemCfg, "name"));
                ohosPolicyItem.setType(OhosCfgUtil.getElementAttrValue(policyitemCfg, "type"));
                String policyPath = OhosCfgUtil.getElementAttrValue(policyitemCfg, "path");
                if (ohosProject != null) {
                    policyPath = ohosProject.getPath() + policyPath;
                }
                ohosPolicyItem.setPath(policyPath);
                ohosPolicyItem.setRule(OhosCfgUtil.getElementAttrValue(policyitemCfg, "rule", "may"));
                ohosPolicyItem.setGroup(OhosCfgUtil.getElementAttrValue(policyitemCfg, "group", "defaultGroup"));
                ohosPolicyItem.setFileFilter(
                    OhosCfgUtil.getElementAttrValue(policyitemCfg, "filefilter", "defaultPolicyFilter"));
                ohosPolicyItem.setDesc(OhosCfgUtil.getElementAttrValue(policyitemCfg, "desc"));
                if (ohosProject != null) {
                    // Project OAT XML
                    ohosPolicyItem.setFileFilterObj(ohosConfig.getOhosFileFilter(ohosPolicyItem.getFileFilter()));
                    ohosProject.getOhosPolicy().addPolicyItem(ohosPolicyItem);
                    if (ohosProject != null) {
                        // Project OAT XML
                        OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                            ohosProject.getPath() + "\tPolicyItem\t" + ohosPolicyItem.getType() + "Policy\tName\t"
                                + ohosPolicyItem.getName() + "\tPath\t" + ohosPolicyItem.getPath() + "\tDesc\t"
                                + ohosPolicyItem.getDesc());
                    }
                } else {
                    // Global OAT XML
                    ohosPolicy.addPolicyItem(ohosPolicyItem);
                }
            } // End of policy items
        } // end of policy
    }

    private static void initLicenseMatcher(final OhosConfig ohosConfig, final XMLConfiguration xmlconfig,
        final OhosProject ohosProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> licenseMatcherlistCfg = OhosCfgUtil.getElements(xmlconfig,
            OhosCfgUtil.ROOTNODE, "licensematcherlist.licensematcher");
        for (final HierarchicalConfiguration<ImmutableNode> licenseMatcherCfg : licenseMatcherlistCfg) {
            final String licenseName = OhosCfgUtil.getElementAttrValue(licenseMatcherCfg, "name");

            final List<HierarchicalConfiguration<ImmutableNode>> licenseTextListCfg
                = licenseMatcherCfg.configurationsAt("licensetext");
            for (final HierarchicalConfiguration<ImmutableNode> licenseTextCfg : licenseTextListCfg) {
                final String licenseText = OhosCfgUtil.getElementAttrValue(licenseTextCfg, "name");
                if (ohosProject == null) {
                    ohosConfig.addLicenseText(licenseName, licenseText);
                    OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                        "GlobalConfig" + "\taddGlobalLicenseText\t" + "customizedLicenseConfig\tName\t" + licenseName
                            + "\t \t \tLicenseText\t" + licenseText);
                } else {
                    ohosProject.addPrjLicenseText(licenseName, licenseText);
                    OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                        ohosProject.getPath() + "\taddPrjLicenseText\t" + "customizedLicenseConfig\tName\t"
                            + licenseName + "\t \t \tLicenseText\t" + licenseText);
                }
            }
        }
    }

    private static void initCompatibilityLicense(final OhosConfig ohosConfig, final XMLConfiguration xmlconfig,
        final OhosProject ohosProject) {
        final List<HierarchicalConfiguration<ImmutableNode>> compatibilityCfg = OhosCfgUtil.getElements(xmlconfig,
            OhosCfgUtil.ROOTNODE, "licensecompatibilitylist.license");
        for (final HierarchicalConfiguration<ImmutableNode> license : compatibilityCfg) {
            final String licenseName = OhosCfgUtil.getElementAttrValue(license, "name");

            final List<HierarchicalConfiguration<ImmutableNode>> compatibilityLicenseCfg = license.configurationsAt(
                "compatibilitylicense");
            for (final HierarchicalConfiguration<ImmutableNode> compatibilitylicense : compatibilityLicenseCfg) {
                final String compatibilitylicenseName = OhosCfgUtil.getElementAttrValue(compatibilitylicense, "name");
                if (ohosProject == null) {
                    ohosConfig.addCompatibilityLicense(licenseName, compatibilitylicenseName);
                    OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                        "GlobalConfig" + "\taddCompatibilityLicense\t" + "compatibilityLicenseConfig\tName\t"
                            + licenseName + "\t \t \tLicenseText\t" + compatibilitylicenseName);
                } else {
                    ohosProject.addPrjCompatibilityLicense(licenseName, compatibilitylicenseName);
                    OhosLogUtil.logOatConfig(OhosCfgUtil.class.getSimpleName(),
                        ohosProject.getPath() + "\taddPrjCompatibilityLicense\t" + "compatibilityLicenseConfig\tName\t"
                            + licenseName + "\t \t \tLicenseText\t" + compatibilitylicenseName);
                }
            }
        }
    }

    private static void initTask(final OhosConfig ohosConfig, final XMLConfiguration xmlconfig) {
        final List<HierarchicalConfiguration<ImmutableNode>> tasklistCfg = OhosCfgUtil.getElements(xmlconfig,
            OhosCfgUtil.ROOTNODE, "tasklist.task");
        OhosTask defaultOhosTask = null;
        for (final HierarchicalConfiguration<ImmutableNode> taskCfg : tasklistCfg) {
            final OhosTask ohosTask = new OhosTask();
            ohosTask.setNamne(OhosCfgUtil.getElementAttrValue(taskCfg, "name"));
            ohosTask.setPolicy(OhosCfgUtil.getElementAttrValue(taskCfg, "policy"));
            ohosTask.setDesc(OhosCfgUtil.getElementAttrValue(taskCfg, "desc"));
            ohosTask.setFileFilter(OhosCfgUtil.getElementAttrValue(taskCfg, "filefilter"));

            final List<HierarchicalConfiguration<ImmutableNode>> projectlistCfg = taskCfg.configurationsAt("project");
            boolean containReps = false;
            for (final HierarchicalConfiguration<ImmutableNode> projectCfg : projectlistCfg) {
                final OhosProject ohosProject = new OhosProject();
                final String prjName = OhosCfgUtil.getElementAttrValue(projectCfg, "name");
                initProjectBasicinfo(projectCfg, ohosProject, prjName);
                if (!ohosConfig.isPluginMode()) {
                    ohosTask.addProject(ohosProject);
                    continue;
                }

                if (ohosConfig.getRepositoryName().equals(prjName)) {
                    containReps = true;
                    ohosTask.addProject(ohosProject);
                    break;
                }
                if ((!(ohosConfig.getRepositoryName().equals(prjName))) && ohosTask.getNamne().equals("defaultTask")) {
                    ohosProject.setName(ohosConfig.getRepositoryName());
                    ohosProject.setPath(ohosConfig.getRepositoryName() + "/");
                    initProjectDefaultPolicy(ohosConfig, ohosProject);
                    ohosTask.addProject(ohosProject);
                }
            } // end of project list

            if (ohosConfig.isPluginMode()) {
                if (containReps) {
                    ohosConfig.addTask(ohosTask);
                    break;
                }
                if (ohosTask.getNamne().equals("defaultTask")) {
                    defaultOhosTask = ohosTask;
                }

            }
            if (!(ohosConfig.isPluginMode()) && (!ohosTask.getNamne().equals("defaultTask"))) {
                ohosConfig.addTask(ohosTask);
            }
        } // end of tasklist
        if (ohosConfig.isPluginMode() && ohosConfig.getTaskList().size() <= 0) {
            ohosConfig.addTask(defaultOhosTask);
        }
    }

    private static void initProjectBasicinfo(final HierarchicalConfiguration<ImmutableNode> projectCfg,
        final OhosProject ohosProject, final String prjName) {
        ohosProject.setName(prjName);
        String tmpPath = OhosCfgUtil.getElementAttrValue(projectCfg, "path");
        if (!tmpPath.endsWith("/")) {
            tmpPath += "/";
        }
        if (tmpPath.startsWith("/")) {
            tmpPath = tmpPath.substring(1);
        }
        ohosProject.setPath(tmpPath);
        ohosProject.setPolicy(OhosCfgUtil.getElementAttrValue(projectCfg, "policy"));
        ohosProject.setFileFilter(OhosCfgUtil.getElementAttrValue(projectCfg, "filefilter"));
        final String[] licenselist = OhosCfgUtil.getSplitStrings(
            OhosCfgUtil.getElementAttrValue(projectCfg, "licensefile"));
        cleanFileName(licenselist);
        ohosProject.setLicenseFiles(licenselist);
    }

    private static void initProjectDefaultPolicy(final OhosConfig ohosConfig, final OhosProject ohosProject) {
        if (ohosConfig.getRepositoryName().toLowerCase(Locale.ENGLISH).contains("third_party")) {
            ohosProject.setPolicy("3rdDefaultPolicy");
        } else {
            ohosProject.setPolicy("defaultPolicy");
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
        return value == null ? defaultValue : OhosCfgUtil.formatPath(value);
    }

    private static String getElementAttrValue(final HierarchicalConfiguration<ImmutableNode> element,
        final String attr) {
        return OhosCfgUtil.getElementAttrValue(element, attr, "");
    }

    private static List<HierarchicalConfiguration<ImmutableNode>> getElements(final XMLConfiguration xmlconfig,
        final String rootnode, final String path) {
        final List<HierarchicalConfiguration<ImmutableNode>> elements = xmlconfig.configurationsAt(rootnode + path);
        return elements;
    }

    private static String getValue(final XMLConfiguration xmlconfig, final String path) {
        final String value = xmlconfig.getString(OhosCfgUtil.ROOTNODE + path);
        return value == null ? "" : OhosCfgUtil.formatPath(value);
    }

    private static String getAttrValue(final XMLConfiguration xmlconfig, final String rootnode, final String path,
        final String attr) {
        final String value = xmlconfig.getString(rootnode + path + "[@" + attr + "]");
        return value == null ? "" : OhosCfgUtil.formatPath(value);
    }
}
