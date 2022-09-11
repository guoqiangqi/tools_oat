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
import ohos.oat.excutor.IOatExcutor;
import ohos.oat.excutor.OatComplianceExcutor;
import ohos.oat.input.model.OatCommandLinePolicyPara;
import ohos.oat.utils.IOatCommonUtils;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple project scan mode, specify the batch project definition file through the -i parameter to scan all projects
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatMultiModeCommandLine extends AbstractOatCommandLine {

    /**
     * Receive command line parameters and determine whether the command line corresponds to the operating mode
     *
     * @param args command line paras
     * @return Match result
     */
    @Override
    public boolean accept(final String[] args) {
        this.options.addOption("mode", true, "Operating mode, 'm' for check multiple projects");
        this.options.addOption("h", false, "Help message");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("i", true, "OAT.xml file path, default vaule is OAT.xml in the running path");
        this.options.addOption("r", true, "Report file folder, eg: c:/oatresult/");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false, "Ignore project OAT configuration");
        this.options.addOption("p", false, "Ignore project OAT policy");
        this.options.addOption("policy", true, "Specify check policy rules to replace the tool's default rules, \n"
            + "eg:repotype:upstream; license:Apache-2.0@dirA/.*|MIT@dirB/.*|BSD@dirC/.*;copyright:Huawei Device Co"
            + "., Ltd.@dirA/.*;filename:README.md@projectroot;filetype:!binary|!archive;compatibility:Apache-2.0 \n"
            + "Note:\n" + "repotype:'upstreaam' means 3rd software, 'dev' means self developed \n"
            + "license: used to check license header \n copyright: used to check copyright header \n filename: used "
            + "to check whether there is the specified file in the specified directory \n filetype: used to check "
            + "where there are some binary or archive files \n compatibility: used to check license compatibility");
        return IOatCommandLine.accept(args, this.options, "m");
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
        final String optionValue_i = commandLine.getOptionValue("i");
        if (null == commandLine || null == optionValue_i || commandLine.hasOption("h")) {
            return null;
        }

        String initOATCfgFile = "OAT.xml";
        initOATCfgFile = OatCfgUtil.formatPath(optionValue_i);

        oatConfig.putData("initOATCfgFile", initOATCfgFile);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tinitOATCfgFile\t" + initOATCfgFile);

        // Init report file path
        String reportFolder = "./" + IOatCommonUtils.getDateTimeString();
        final String optionValue_r = commandLine.getOptionValue("r");
        if (null != optionValue_r) {
            reportFolder = OatCfgUtil.formatPath(optionValue_r);
        }
        reportFolder = reportFolder + "/multi";
        oatConfig.putData("reportFolder", reportFolder);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\treportFolder\t" + reportFolder);

        // To be deleted
        oatConfig.setRepositoryName("defaultProject");
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine\tnameOfRepository\tdefaultProject");

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
        OatCfgUtil.initOatConfig(oatConfig, "");
        return oatConfig;
    }

    /**
     * Perform tasks
     *
     * @param oatConfig OAT configuration data structure
     */
    @Override
    public void transmit2Excutor(final OatConfig oatConfig) {
        final List<IOatExcutor> oatExcutors = new ArrayList<>();
        oatExcutors.add(new OatComplianceExcutor());
        IOatCommandLine.transmit2Excutor(oatConfig, oatExcutors);

    }
}
