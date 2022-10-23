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

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatPolicy;
import ohos.oat.executor.IOatExecutor;
import ohos.oat.executor.OatFolderCheckExecutor;
import ohos.oat.input.model.OatCommandLinePolicyPara;
import ohos.oat.utils.IOatCommonUtils;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to scan all files in the specified directory
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatFolderModeCommandLine extends AbstractOatCommandLine {

    /**
     * Receive command line parameters and determine whether the command line corresponds to the operating mode
     *
     * @param args command line paras
     * @return Match result
     */
    @Override
    public boolean accept(final String[] args) {
        this.options.addOption("mode", true, "Operating mode, 'f' for check folder");
        this.options.addOption("h", false, "Help message");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("s", true, "Source code repository path, eg: c:/test/");
        this.options.addOption("r", true, "Report file folder, eg: c:/oatresult/");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false, "Ignore project OAT configuration");
        this.options.addOption("p", false, "Ignore project OAT policy");
        this.options.addOption("a", false,
            "Output single mode detection results, include 'single' and 'single_policy' folder");
        return IOatCommandLine.accept(args, this.options, "f");
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
        IOatCommandLine.storeCommand2Config(args, oatConfig);
        final CommandLine commandLine = IOatCommandLine.parseOptions(args, this.options);
        final String optionValue_s = commandLine.getOptionValue("s");
        if (null == commandLine || null == optionValue_s || commandLine.hasOption("h")) {
            return null;
        }

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

        // Init report file path
        String reportFolder = "./" + IOatCommonUtils.getDateTimeString();
        final String optionValue_r = commandLine.getOptionValue("r");
        if (null != optionValue_r) {
            reportFolder = OatCfgUtil.formatPath(optionValue_r);
        }
        oatConfig.putData("reportFolder", reportFolder);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\treportFolder\t" + reportFolder);

        if (commandLine.hasOption("k")) {
            oatConfig.putData("TraceSkippedAndIgnoredFiles", "true");
        }
        if (commandLine.hasOption("g")) {
            oatConfig.putData("IgnoreProjectOAT", "true");
        }
        if (commandLine.hasOption("p")) {
            oatConfig.putData("IgnoreProjectPolicy", "true");
        }
        if (commandLine.hasOption("a")) {
            oatConfig.putData("allreports", "true");
        }

        final String policystring = commandLine.getOptionValue("policy");
        if (policystring != null) {
            final OatPolicy oatPolicy = OatCommandLinePolicyPara.getOatPolicy(policystring);
            if (oatPolicy == null) {
                return null;
            }
            oatConfig.putData("policy", policystring);
        }
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
        oatExecutors.add(new OatFolderCheckExecutor());
        IOatCommandLine.transmit2Executor(oatConfig, oatExecutors);

    }

}
