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

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatMultiModeCommandLine implements IOatCommandLine {
    private final Options options = new Options();

    private final String cmdLineSyntax = "java -jar ohos_ossaudittool-VERSION.jar [options] \n";

    @Override
    public boolean accept(final String[] args) {
        return this.accept(args, this.options, "m");
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        this.options.addOption("i", true, "OAT.xml file path, default vaule is OAT.xml in the running path");
        this.options.addOption("l", false, "Log switch, used to enable the logger");
        this.options.addOption("c", false, "Collect and log sub projects only, must be used together with -s option");
        this.options.addOption("t", false, "Trace project license list only");
        this.options.addOption("k", false, "Trace skipped files and ignored files");
        this.options.addOption("g", false, "Ignore project OAT configuration, used to display all the filtered report items");
        final CommandLine commandLine = this.parseOptions(args, this.options);
        if (null == commandLine || commandLine.hasOption("h")) {
            return false;
        }
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
