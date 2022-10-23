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
import ohos.oat.executor.IOatExecutor;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * Command line parameter Interface for converting command line parameters into OAT configuration data structure
 * extract this Interface to support more detection scenarios
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatCommandLine {
    static final String PROMPT_MESSAGE_SEPARATOR
        = "--------------------------------------------------------------------------";
    static final String PROMPT_MESSAGE_HEADER = "options:";

    /**
     * Receive command line parameters and determine whether the command line corresponds to the operating mode
     *
     * @param args command line paras
     * @return Match result
     */
    boolean accept(String[] args);

    /**
     * Parse command line arguments and convert to OatConfig data structure
     *
     * @param args Command line arguments
     * @return OAT configuration data structure
     */
    OatConfig parseArgs2Config(String[] args);

    /**
     * Perform tasks
     *
     * @param oatConfig OAT configuration data structure OAT configuration data structure
     */
    void transmit2Executor(OatConfig oatConfig);

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
     *
     */
    void printUsage();

    /**
     * Tool function, defined as static to avoid instantiation
     *
     * @param args Command line arguments
     * @param options Command line options
     * @param mode
     * @return
     */
    static boolean accept(final String[] args, final Options options, final String mode) {

        if (ArrayUtils.isEmpty(args)) {
            return false;
        }

        final CommandLine commandLine = IOatCommandLine.parseOptions(args, options);
        if (null == commandLine) {
            return false;
        }
        if (commandLine.hasOption("l")) {
            OatLogUtil.setDebugMode(true);
        }
        OatLogUtil.warn(IOatCommandLine.class.getSimpleName(),
            "CommandLine" + "\tlogSwitch\t" + commandLine.hasOption("l"));
        final String modeValue = commandLine.getOptionValue("mode");
        return modeValue != null && modeValue.equals(mode);
    }

    /**
     * Tool function, defined as static to avoid instantiation
     *
     * @param args Command line arguments
     * @param options Command line options
     * @return CommandLine
     */
    static CommandLine parseOptions(final String[] args, final Options options) {
        final CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            return null;
        }
        return cmd;
    }

    /**
     * Tool function, defined as static to avoid instantiation
     *
     * @param oatConfig OAT configuration data structure
     * @param oatExecutors OAT Executors
     */
    static void transmit2Executor(final OatConfig oatConfig, final List<IOatExecutor> oatExecutors) {
        oatExecutors.forEach(oatExecutor -> {

            oatExecutor.init(oatConfig).execute();

        });
    }

    static void storeCommand2Config(final String[] args, final OatConfig oatConfig) {
        String tmpargs = "";
        if (args != null) {
            for (final String arg : args) {
                tmpargs += " " + arg;
            }
        }
        oatConfig.putData("initCommand", tmpargs);
    }
}
