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
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * Command line parameter Interface for converting command line parameters into OAT configuration data structures
 * extract this Interface to support more detection scenarios
 *
 * @author chenyaxun
 * @since 2022/08
 */
public interface IOatCommandLine {
    String PROMPT_MESSAGE_SEPARATOR = "--------------------------------------------------------------------------";
    String PROMPT_MESSAGE_HEADER = "Available options:";

    boolean accept(String[] args);

    boolean parseArgs2Config(final String[] args, final OatConfig oatConfig);

    /**
     * Command line options
     *
     * @return Options
     */
    Options getOptions();

    /**
     * Command line syntax explanation
     *
     * @return cmdLineSyntax
     */
    String getCmdLineSyntax();

    /**
     * Print usage of this commandline
     */
    default void printUsage() {
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(null);
        helpFormatter.setWidth(140);
        helpFormatter.printHelp(this.getCmdLineSyntax(), PROMPT_MESSAGE_HEADER, this.getOptions(),
            PROMPT_MESSAGE_SEPARATOR, false);
    }

    default boolean accept(final String[] args, final Options options, final String mode) {

        if (ArrayUtils.isEmpty(args)) {
            return false;
        }

        final CommandLine commandLine = this.parseOptions(args, options);
        if (null == commandLine || commandLine.hasOption("h")) {
            return false;
        }
        OatLogUtil.setDebugMode(commandLine.hasOption("l"));
        OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\tlogSwitch\t" + commandLine.hasOption("l"));
        final String modeValue = commandLine.getOptionValue("mode");
        return modeValue != null && modeValue.equals(mode);
    }

    /**
     * @param args
     * @param options
     * @return
     */
    default CommandLine parseOptions(final String[] args, final Options options) {
        final CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            OatLogUtil.traceException(e);
        }
        return cmd;
    }

    /**
     * @param oatConfig
     * @param oatExcutors
     */
    default void transmit(final OatConfig oatConfig, final List<IOatExcutor> oatExcutors) {
        oatExcutors.forEach(iOatExcutor -> iOatExcutor.excute(oatConfig));
    }
}
