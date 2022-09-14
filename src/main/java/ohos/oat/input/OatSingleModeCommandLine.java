/*
 * Copyright (c) 2022 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ohos.oat.input;

import ohos.oat.OatLicenseMain;
import ohos.oat.config.OatConfig;
import ohos.oat.config.OatFileFilter;
import ohos.oat.config.OatPolicy;
import ohos.oat.executor.IOatExecutor;
import ohos.oat.executor.OatComplianceExecutor;
import ohos.oat.input.model.OatCommandLineFilterPara;
import ohos.oat.input.model.OatCommandLinePolicyPara;
import ohos.oat.utils.IOatCommonUtils;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Single project scan mode, scan the directory specified by the -s parameter as the project root directory
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatSingleModeCommandLine extends AbstractOatCommandLine {

    /**
     * Receive command line parameters and determine whether the command line corresponds to the operating mode
     *
     * @param args command line paras
     * @return Match result
     */
    @Override
    public boolean accept(final String[] args) {
        this.options.addOption("mode", true, "Operating mode, 's' for check single project");
        this.options.addOption("h", false, "Help message");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("s", true, "Source code repository path, eg: c:/test/");
        this.options.addOption("r", true, "Report file folder, eg: c:/oatresult/");
        this.options.addOption("n", true, "Name of repository, used to match the default policy");
        this.options.addOption("w", true, "Check way, 0 means full check, 1 means only check the file list");
        this.options.addOption("f", true, "File list to check, separated by |");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false, "Ignore project OAT configuration");
        this.options.addOption("p", false, "Ignore project OAT policy");
        this.options.addOption("policy", true, "Specify check policy rules to replace the tool's default rules. \n"
            + "eg:repotype:upstream; license:Apache-2.0@dirA/.*|MIT@dirB/.*|BSD@dirC/.*;copyright:Huawei Device Co"
            + "., Ltd.@dirA/.*;filename:README.md@projectroot;filetype:!binary~must|!archive~must;"
            + "compatibility:Apache-2.0 \n" + "Note:\n"
            + "repotype:'upstreaam' means 3rd software, 'dev' means self developed \n"
            + "license: used to check license header \n copyright: used to check copyright header \n filename: used "
            + "to check whether there is the specified file in the specified directory \n filetype: used to check "
            + "where there are some binary or archive files \n compatibility: used to check license compatibility");
        this.options.addOption("filter", true,
            "Specify filtering rules to filter some files or directories that do not need to be checked. \n"
                + "eg:filename:.*.dat|.*.rar; filepath:projectroot/target/.*");
        return IOatCommandLine.accept(args, this.options, "s");
    }

    /**
     * Parse command line arguments and convert to OatConfig data structure
     *
     * @param args Command line arguments
     * @return OatConfig data structure
     */
    @Override
    public OatConfig parseArgs2Config(final String[] args) {
        final OatConfig oatConfig = new OatConfig();
        final CommandLine commandLine = IOatCommandLine.parseOptions(args, this.options);
        final String optionValue_s = commandLine.getOptionValue("s");
        if (null == commandLine || null == optionValue_s || commandLine.hasOption("h")) {
            return null;
        }

        oatConfig.setPluginMode(true);

        // Init Source code repository path, same with basedir 
        String sourceCodeRepoPath = "";
        sourceCodeRepoPath = OatCfgUtil.formatPath(optionValue_s);
        final File tmpFile = new File(sourceCodeRepoPath);
        if (tmpFile.exists()) {
            sourceCodeRepoPath = OatFileUtils.getFileCanonicalPath(tmpFile);
            sourceCodeRepoPath = OatCfgUtil.formatPath(sourceCodeRepoPath);
        } else {
            OatLogUtil.warn(this.getClass().getSimpleName(), "Source code repository path is not exists!");
            return null;
        }
        if (!sourceCodeRepoPath.endsWith("/")) {
            sourceCodeRepoPath += "/";
        }
        oatConfig.setBasedir(sourceCodeRepoPath);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine\tsourceCodeRepoPath\t" + sourceCodeRepoPath);

        // Init OAT.xml file path
        final URL oatResource = OatLicenseMain.class.getResource("/ohos/oat");
        if (oatResource == null) {
            OatLogUtil.warn(this.getClass().getSimpleName(), "OAT jar path is empty!");
            return null;
        }
        final String jarOatPkgPath = oatResource.toString();
        final String jarRootPath = jarOatPkgPath.substring(0, jarOatPkgPath.length() - 8);
        OatLogUtil.warn(this.getClass().getSimpleName(), "jarRoot:\t" + jarRootPath);
        oatConfig.putData("JarRootPath", jarRootPath);
        final String initOATCfgFile = jarRootPath + "OAT-Default.xml";
        oatConfig.putData("initOATCfgFile", initOATCfgFile);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tinitOATCfgFile\t" + initOATCfgFile);

        // Init report file path
        String reportFolder = "./" + IOatCommonUtils.getDateTimeString();
        final String optionValue_r = commandLine.getOptionValue("r");
        if (null != optionValue_r) {
            reportFolder = OatCfgUtil.formatPath(optionValue_r);
        }
        reportFolder = reportFolder + "/single";
        oatConfig.putData("reportFolder", reportFolder);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\treportFolder\t" + reportFolder);

        // Init repository name
        String nameOfRepository = "defaultProject";
        final String optionValue_n = commandLine.getOptionValue("n");
        if (null != optionValue_n) {
            nameOfRepository = OatCfgUtil.formatPath(optionValue_n);
        }
        oatConfig.setRepositoryName(nameOfRepository);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine\tnameOfRepository\t" + nameOfRepository);

        // Init check way
        String way = "0";
        final String optionValue_w = commandLine.getOptionValue("w");
        if (null != optionValue_w) {
            if (optionValue_w.equals("0") || optionValue_w.equals("1")) {
                way = optionValue_w;
            }
        }
        oatConfig.setPluginCheckMode(way);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tmode\t" + way);

        // Init file list to check
        String fileList = "";
        final String optionValue_f = commandLine.getOptionValue("f");
        if (null != optionValue_f) {
            fileList = OatCfgUtil.formatPath(optionValue_f);
        }
        oatConfig.setSrcFileList(fileList);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tfileList\t" + fileList);

        if (commandLine.hasOption("k")) {
            oatConfig.putData("TraceSkippedAndIgnoredFiles", "true");
        }
        if (commandLine.hasOption("g")) {
            oatConfig.putData("IgnoreProjectOAT", "true");
        }
        if (commandLine.hasOption("p")) {
            oatConfig.putData("IgnoreProjectPolicy", "true");
        }
        final String policystring = commandLine.getOptionValue("policy");
        if (policystring != null) {
            final OatPolicy oatPolicy = OatCommandLinePolicyPara.getOatPolicy(policystring);
            if (oatPolicy == null) {
                return null;
            }
            oatConfig.putData("policy", policystring);
            oatConfig.putData("reportFolder", oatConfig.getData("reportFolder") + "_policy");
        }

        final String filterstring = commandLine.getOptionValue("filter");
        if (filterstring != null) {
            final OatFileFilter oatFileFilter = OatCommandLineFilterPara.getOatFileFilter(filterstring);
            if (oatFileFilter == null) {
                return null;
            }
            oatConfig.putData("filter", filterstring);
        }
        OatCfgUtil.initOatConfig(oatConfig, sourceCodeRepoPath);
        return oatConfig;
    }

    /**
     * Perform tasks
     *
     * @param oatConfig OAT configuration data structure
     */
    @Override
    public void transmit2Executor(final OatConfig oatConfig) {
        final List<IOatExecutor> oatExecutors = new ArrayList<>();
        oatExecutors.add(new OatComplianceExecutor());
        IOatCommandLine.transmit2Executor(oatConfig, oatExecutors);

    }
}
