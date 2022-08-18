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
public class OatCollectSubPrjectsCommandLine implements IOatCommandLine {
    private final Options options = new Options();

    private final String cmdLineSyntax = "java -jar ohos_ossaudittool-VERSION.jar [options] \n";

    @Override
    public boolean accept(final String[] args) {
        return this.accept(args, this.options, "c");
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        this.options.addOption("s", true, "Source code repository path, eg: c:/test/");
        this.options.addOption("r", true, "Report file path, eg: c:/oatresult.txt");

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
