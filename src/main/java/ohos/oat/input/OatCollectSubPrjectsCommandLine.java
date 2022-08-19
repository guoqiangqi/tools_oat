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
import ohos.oat.excutor.OatCollectSubProjectsExcutor;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatCollectSubPrjectsCommandLine implements IOatCommandLine {
    private final Options options = new Options();

    private final String cmdLineSyntax = "[Collect Sub Projects Mode]: java -jar ohos_ossaudittool-VERSION.jar";

    private final List<IOatExcutor> lstOatExcutors = new ArrayList<>();

    @Override
    public boolean accept(final String[] args) {
        this.options.addOption("mode", true, "Operating mode, 'c' for collecting sub projects only");
        this.options.addOption("s", true, "Source code repository path, eg: c:/test/");
        this.options.addOption("r", true, "Report file path, eg: c:/oatresult.txt");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("h", false, "Help message");
        return this.accept(args, this.options, "c");
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        final CommandLine commandLine = this.parseOptions(args, this.options);
        final String optionValue_s = commandLine.getOptionValue("s");
        if (null == commandLine || null == optionValue_s) {
            return false;
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
            return false;
        }
        if (!sourceCodeRepoPath.endsWith("/")) {
            sourceCodeRepoPath += "/";
        }
        oatConfig.setBasedir(sourceCodeRepoPath);
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine\tsourceCodeRepoPath\t" + sourceCodeRepoPath);

        this.lstOatExcutors.add(new OatCollectSubProjectsExcutor());
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
