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
import ohos.oat.excutor.IOatExcutor;
import ohos.oat.excutor.OatComplianceExcutor;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLogUtil;
import ohos.oat.utils.OatSpdxLicenseUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatMultiModeCommandLine implements IOatCommandLine {
    private final Options options = new Options();

    private final String cmdLineSyntax = "[Multiple Projects Mode]: java -jar ohos_ossaudittool-VERSION.jar";

    private final List<IOatExcutor> lstOatExcutors = new ArrayList<>();

    @Override
    public boolean accept(final String[] args) {
        this.options.addOption("mode", true, "Operating mode, 'm' for check multiple projects");
        this.options.addOption("i", true, "OAT.xml file path, default vaule is OAT.xml in the running path");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false, "Ignore project OAT configuration");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("h", false, "Help message");
        return this.accept(args, this.options, "m");
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        final CommandLine commandLine = this.parseOptions(args, this.options);
        final String optionValue_i = commandLine.getOptionValue("i");
        if (null == commandLine || null == optionValue_i) {
            return false;
        }

        String initOATCfgFile = "OAT.xml";
        initOATCfgFile = OatCfgUtil.formatPath(optionValue_i);

        oatConfig.putData("initOATCfgFile", initOATCfgFile);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tinitOATCfgFile\t" + initOATCfgFile);

        // To be deleted
        oatConfig.setRepositoryName("defaultProject");
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine\tnameOfRepository\tdefaultProject");
        oatConfig.setPluginCheckMode("0");
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tmode\t" + "0");
        final String fileList = "";
        oatConfig.setSrcFileList(fileList);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tfileList\t" + fileList);

        if (commandLine.hasOption("k")) {
            oatConfig.putData("TraceSkippedAndIgnoredFiles", "true");
        }
        if (commandLine.hasOption("g")) {
            oatConfig.putData("IgnoreProjectOAT", "true");
        }

        OatCfgUtil.initOatConfig(oatConfig, "");
        OatSpdxLicenseUtil.initSpdxLicenseList(oatConfig);
        this.lstOatExcutors.add(new OatComplianceExcutor());
        this.transmit(oatConfig, this.lstOatExcutors);

        return true;
    }

    /**
     * @return Options
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

    /**
     * @return cmdLineSyntax
     */
    @Override
    public String getCmdLineSyntax() {
        return this.cmdLineSyntax;
    }

}
