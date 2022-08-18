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
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatMainCommandLine implements IOatCommandLine {
    private final Options options;

    private final String cmdLineSyntax;

    public OatMainCommandLine() {
        this.options = null;
        this.cmdLineSyntax = "";
    }

    @Override
    public boolean accept(final String[] args) {
        return false;
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        final CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(this.options, args);
        } catch (final ParseException e) {
            OatLogUtil.traceException(e);
        }
        if (ArrayUtils.isEmpty(args) || commandLine == null || commandLine.hasOption("h")) {
            this.printUsage();
        }
        return false;
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

    public static void main(final String[] args) {
        final OatMainCommandLine oatMainCommandLine = new OatMainCommandLine();
        oatMainCommandLine.printUsage();
    }
}
