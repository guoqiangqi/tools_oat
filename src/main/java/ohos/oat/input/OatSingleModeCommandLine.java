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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatSingleModeCommandLine implements IOatCommandLine {
    private final Options options = new Options();

    private final String cmdLineSyntax = "";

    @Override
    public boolean accept(final String[] args) {
        if (ArrayUtils.isEmpty(args)) {
            return false;
        }
        this.options.addOption("mode", true, "Operating mode, 's' for single project");
        this.options.addOption("h", false, "Help message");
        final CommandLine commandLine = this.parseOptions(args, this.options);
        if (null == commandLine || commandLine.hasOption("h")) {
            return false;
        }
        final String modeValue = commandLine.getOptionValue("mode");
        return modeValue != null && modeValue.equals("s");
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        this.options.addOption("s", true, "Source code repository path");
        this.options.addOption("r", true, "Report file path, must be used together with -s option");
        this.options.addOption("n", true,
            "Name of repository, used to match the default policy, must be used together with -s option");
        this.options.addOption("m", true,
            "Check mode, 0 means full check, 1 means only check the file list, must be used together with -s option");
        this.options.addOption("f", true, "File list to check, separated by |, must be used together with -s option");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("t", false, "Trace project license list only");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false,
            "Ignore project OAT configuration, used to display all the filtered report items");

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
