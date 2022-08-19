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
import ohos.oat.excutor.IOatExcutor;
import ohos.oat.excutor.OatComplianceExcutor;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;
import ohos.oat.utils.OatSpdxLicenseUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatSingleModeCommandLine implements IOatCommandLine {
    private final Options options = new Options();

    private final String cmdLineSyntax = "java -jar ohos_ossaudittool-VERSION.jar [options] \n";

    private final List<IOatExcutor> lstOatExcutors = new ArrayList<>();

    @Override
    public boolean accept(final String[] args) {
        this.options.addOption("mode", true, "Operating mode, 's' for check single project");
        this.options.addOption("h", false, "Help message");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("s", true, "Source code repository path, eg: c:/test/");
        this.options.addOption("r", true, "Report file path, eg: c:/oatresult.txt");
        this.options.addOption("n", true, "Name of repository, used to match the default policy");
        this.options.addOption("w", true, "Check way, 0 means full check, 1 means only check the file list");
        this.options.addOption("f", true, "File list to check, separated by |");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false, "Ignore project OAT configuration");
        return this.accept(args, this.options, "s");
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        final CommandLine commandLine = this.parseOptions(args, this.options);
        final String optionValue_s = commandLine.getOptionValue("s");
        if (null == commandLine || null == optionValue_s) {
            return false;
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
            return false;
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
            return false;
        }
        final String jarOatPkgPath = oatResource.toString();
        final String jarRootPath = jarOatPkgPath.substring(0, jarOatPkgPath.length() - 8);
        OatLogUtil.warn(this.getClass().getSimpleName(), "jarRoot:\t" + jarRootPath);
        oatConfig.putData("JarRootPath", jarRootPath);
        final String initOATCfgFile = jarRootPath + "OAT-Default.xml";
        oatConfig.putData("initOATCfgFile", initOATCfgFile);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tinitOATCfgFile\t" + initOATCfgFile);

        // Init report file path
        String reportFile = "./OATResult.txt";
        final String optionValue_r = commandLine.getOptionValue("r");
        if (null != optionValue_r) {
            reportFile = OatCfgUtil.formatPath(optionValue_r);
        }
        oatConfig.putData("reportFile", reportFile);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\treportFile\t" + reportFile);

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

        OatCfgUtil.initOatConfig(oatConfig, sourceCodeRepoPath);
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
