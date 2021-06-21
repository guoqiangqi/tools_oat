/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Derived from Apache Creadur Rat, the original license and notice text is at the end of the LICENSE file of this
 * project.
 *
 * ChangeLog:
 * 2021.1 - Add the following capabilities to support OpenHarmony:
 * 1. Task, project, processfilter, policy, and reportfilter  customization capability.
 * 2. Parameters for pipleline ingetration.
 * 3. SPDX license analysis capability.
 * 4. Special license header used by OpenHarmony analysis capability.
 * 5. Support batch and single project mode.
 * 6. List all the missed files not define in the OAT config file.
 * 7. Concurrent processing capability for each task.
 * 2021.3 -  Add program parameters to support integration with pipleline tools
 * Modified by jalenchen
 * 2021.5 - Support Scan files of all projects concurrently in one task:
 * 1. Add report.concurrentReport() method, all time-consuming code analysis processing takes place in this function.
 * 2. Delete createReport method and replaced by new OhosMainReport in every task.
 * 3. Modify run options, delete para of -l option.
 * 2021.6 - Support ignore project OAT configuration.
 * Modified by jalenchen
 */

package ohos.oat;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.config.OatTask;
import ohos.oat.report.IOatReport;
import ohos.oat.report.OatDirectoryWalker;
import ohos.oat.report.OatMainReport;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLogUtil;
import ohos.oat.utils.OatSpdxLicenseUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.rat.api.RatException;
import org.apache.rat.report.IReportable;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.PatternSyntaxException;

/**
 * Main class of the license and copyright analyser
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatLicenseMain {
    /**
     * Prompt message when the program started
     */
    private static final String PROMPT_MESSAGE_SEPARATOR = "----------------------------------------------------------";

    private static final String PROMPT_MESSAGE_NAME = "OpenHarmony OSS Audit Tool";

    private static final String PROMPT_MESSAGE_COPY = "Copyright (C) 2021 Huawei Device Co., Ltd.";

    private static final String PROMPT_MESSAGE_FEEDBACK =
        "If you have any questions or concerns, please create issue at https://gitee"
            + ".com/openharmony-sig/tools_oat/issues";

    /**
     * Private constructure to prevent new instance
     */
    private OatLicenseMain() {
    }

    /**
     * Main for OAT
     *
     * @param args Use -i to pass OAT config file path, the default file name is OAT.xml while in plugin mode, the first
     * para must be the project root dir, the second para must be the output file dir
     * @throws Exception exception wile process
     */
    public static void main(final String[] args) throws Exception {
        OatLogUtil.println("", System.getProperty("file.encoding"));
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_SEPARATOR);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_NAME);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_COPY);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_FEEDBACK);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_SEPARATOR);
        final Options options = new Options();
        final CommandLine cmd = parseCommandLine(options, args);
        final OatConfig oatConfig = new OatConfig();

        String initOATCfgFile = "OAT.xml";
        if (cmd.hasOption("i")) {
            initOATCfgFile = cmd.getOptionValue("i");
        }
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "CommandLine" + "\tinitOATCfgFile\t" + initOATCfgFile);
        boolean logSwitch = false;
        if (cmd.hasOption("l")) {
            logSwitch = true;
        }
        OatLogUtil.setDebugMode(logSwitch);
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "CommandLine" + "\tlogSwitch\t" + logSwitch);

        // The following para is used for pipleline plugin integration
        String sourceCodeRepoPath = "";
        if (cmd.hasOption("s")) {
            oatConfig.setPluginMode(true);
            sourceCodeRepoPath = OatCfgUtil.formatPath(cmd.getOptionValue("s"));
            if (!sourceCodeRepoPath.endsWith("/")) {
                sourceCodeRepoPath += "/";
            }
            final String jarOatPkgPath = OatLicenseMain.class.getResource("/ohos/oat").toString();
            final String jarRootPath = jarOatPkgPath.substring(0, jarOatPkgPath.length() - 8);
            oatConfig.setBasedir(sourceCodeRepoPath);
            OatLogUtil.println("", "jarRoot:\t" + jarRootPath);
            oatConfig.putData("JarRootPath", jarRootPath);
            initOATCfgFile = jarRootPath + "OAT-Default.xml";
        }
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(),
            "CommandLine" + "\tsourceCodeRepoPath\t" + sourceCodeRepoPath);
        String reportFile = "";
        if (cmd.hasOption("s") && cmd.hasOption("r")) {
            reportFile = OatCfgUtil.formatPath(cmd.getOptionValue("r"));
        }
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "CommandLine" + "\treportFile\t" + reportFile);
        String nameOfRepository = "";
        if (cmd.hasOption("s") && cmd.hasOption("n")) {
            nameOfRepository = OatCfgUtil.formatPath(cmd.getOptionValue("n"));
        }
        if (nameOfRepository.trim().length() <= 0) {
            nameOfRepository = "defaultProject";
        }
        oatConfig.setRepositoryName(nameOfRepository);
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(),
            "CommandLine" + "\tnameOfRepository\t" + nameOfRepository);
        String mode = "0";
        if (cmd.hasOption("s") && cmd.hasOption("m")) {
            final String tmpMode = cmd.getOptionValue("m");
            if (tmpMode.equals("0") || tmpMode.equals("1")) {
                mode = tmpMode;
            }
        }
        oatConfig.setPluginCheckMode(mode);
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "CommandLine" + "\tmode\t" + mode);
        String fileList = "";
        if (cmd.hasOption("s") && cmd.hasOption("f")) {
            fileList = OatCfgUtil.formatPath(cmd.getOptionValue("f"));
        }
        oatConfig.setSrcFileList(fileList);
        OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "CommandLine" + "\tfileList\t" + fileList);
        if (cmd.hasOption("s")) {
            if (!cmd.hasOption("r") || !cmd.hasOption("n")) {
                OatLogUtil.println("", "Args invalid, the valid args is: [-s sourceCodeRepoPath -r "
                    + "reportFilePath -n nameOfRepo -m 0] or [-s sourceCodeRepoPath -r reportFilePath -n nameOfRepo -m "
                    + "1 -f filelistSeparatedBy|]" + " ");
                System.exit(0);
            }
        }
        if (cmd.hasOption("t")) {
            oatConfig.putData("TraceLicenseListOnly", "true");
        }
        if (cmd.hasOption("k")) {
            oatConfig.putData("TraceSkippedAndIgnoredFiles", "true");
        }
        if (cmd.hasOption("g")) {
            oatConfig.putData("IgnoreProjectOAT", "true");
        }
        OatCfgUtil.initOatConfig(oatConfig, initOATCfgFile, sourceCodeRepoPath);
        OatSpdxLicenseUtil.initSpdxLicenseList(oatConfig);

        OatLicenseMain.oatCheck(reportFile, oatConfig);
    }

    private static CommandLine parseCommandLine(final Options options, final String[] args) {

        options.addOption("i", true, "OAT.xml file path, default vaule is OAT.xml in the running path");
        options.addOption("s", true, "Source code repository path");
        options.addOption("r", true, "Report file path, must be used together with -s option");
        options.addOption("n", true,
            "Name of repository, used to match the default policy, must be used together with -s option");
        options.addOption("m", true,
            "Check mode, 0 means full check, 1 means only check the file list, must be used together with -s option");
        options.addOption("f", true, "File list to check, separated by |, must be used together with -s option");
        options.addOption("h", false, "Help message");
        options.addOption("l", false, "Log switch, used to enable the logger");
        options.addOption("t", false, "Trace project license list only");
        options.addOption("k", false, "Trace skipped files and ignored files");
        options.addOption("g", false,
            "Ignore project OAT configuration, used to display all the filtered report items");
        final CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            OatLogUtil.traceException(e);
        }
        if (ArrayUtils.isEmpty(args) || (cmd != null && cmd.hasOption("h"))) {
            printUsage(options);
        }
        return cmd;
    }

    private static void printUsage(final Options opts) {
        final HelpFormatter helpFormatter = new HelpFormatter();
        final String messageHeader = "\nAvailable options";

        helpFormatter.printHelp("java -jar ohos_ossaudittool-VERSION.jar [options] ", messageHeader, opts,
            "---------------------------------------------------------------------", false);
        System.exit(0);
    }

    /**
     * Output a report in the default style and default license header matcher.
     *
     * @param reportFile File to report results
     * @param oatConfig Config in oat.xml
     */
    private static void oatCheck(final String reportFile, final OatConfig oatConfig) {
        final List<OatTask> taskList = oatConfig.getTaskList();
        final int size = taskList.size();
        int maxThread = Math.min(size, 100);
        if (maxThread <= 0) {
            maxThread = 1;
        }
        final ExecutorService exec = Executors.newFixedThreadPool(maxThread);

        final Date date = new Date();
        final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        final String startTime = simpleDataFormat.format(date);
        final String resultfolder = "./oat_results_" + startTime;
        OatLogUtil.println("", startTime + " Start analyzing....");
        if (reportFile.length() <= 0) {
            final File dir = new File(resultfolder);
            if (!dir.exists()) {
                final boolean success = dir.mkdirs();
                if (!success) {
                    OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "Create dir failed");
                }
            }
        }

        for (final OatTask oatTask : taskList) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    this.repoort();
                }

                private void repoort() {
                    final File resultFile;
                    if (reportFile.length() > 0) {
                        resultFile = new File(reportFile);
                    } else {
                        resultFile = new File(resultfolder + "/oat_" + oatTask.getNamne() + ".txt");
                    }
                    OatLogUtil.println("", "Result file path:\t" + resultFile);
                    try {
                        final FileWriter fileWriter = new FileWriter(resultFile, false);

                        final IOatReport report = new OatMainReport(oatConfig, fileWriter);
                        report.startReport();
                        OatLicenseMain.checkProjects(report, oatTask, oatConfig);
                        report.concurrentReport();
                        report.endReport();
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (final Exception e) {
                        OatLogUtil.traceException(e);
                    }
                }
            });
        }
        exec.shutdown();
    }

    private static void checkProjects(final IOatReport report, final OatTask oatTask, final OatConfig oatConfig)
        throws RatException {
        final List<OatProject> projectList = oatTask.getProjectList();

        for (final OatProject oatProject : projectList) {
            final FilenameFilter filenameFilter = OatLicenseMain.parseFileExclusions(
                oatProject.getFileFilterObj().getFileFilterItems());
            final long startTime = System.currentTimeMillis();
            final IReportable base = OatLicenseMain.getDirectoryWalker(oatConfig, oatProject, filenameFilter);
            if (base != null) {
                base.run(report);
            }
            final long costTime = (System.currentTimeMillis() - startTime) / 1000;
            OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), oatProject.getPath() + "\tCostTime\t" + costTime);

        }

    }

    private static IReportable getDirectoryWalker(final OatConfig oatConfig, final OatProject oatProject,
        final FilenameFilter inputFileFilter) {
        final String prjDirectory;
        if (oatConfig.isPluginMode()) {
            // 如果是插件模式，直接扫描根目录下所有
            prjDirectory = oatConfig.getBasedir();
        } else {
            final String prjPath = oatProject.getPath();
            prjDirectory = oatConfig.getBasedir() + prjPath;
        }
        final File base = new File(prjDirectory);
        if (!base.exists()) {
            return null;
        }

        if (base.isDirectory()) {
            return new OatDirectoryWalker(oatConfig, oatProject, base, inputFileFilter);
        }
        return null;
    }

    private static FilenameFilter parseFileExclusions(final List<String> excludes) {
        final OrFileFilter orFilter = new OrFileFilter();

        for (final String exclude : excludes) {
            try {
                // skip comments
                if (exclude.startsWith("#") || StringUtils.isEmpty(exclude)) {
                    continue;
                }

                final String exclusion = exclude.trim();
                orFilter.addFileFilter(new NameFileFilter(exclusion));
                orFilter.addFileFilter(new WildcardFileFilter(exclusion));
            } catch (final PatternSyntaxException e) {
                continue;
            }
        }
        return new NotFileFilter(orFilter);
    }

    private static void printMissedFiles(final OatConfig oatConfig, final List<OatTask> taskList,
        final String resultfolder) throws IOException {
        if (oatConfig.isPluginMode()) {
            return;
        }

        // Files defined in tasklist in oat config file
        final List<String> definedfiles = new ArrayList<>();
        for (final OatTask oatTask : taskList) {
            for (final OatProject oatProject : oatTask.getProjectList()) {
                final String path = oatConfig.getBasedir() + oatProject.getPath();
                definedfiles.add(path);
            }
        }

        final List<String> allfiles = new ArrayList<>();
        final File rootfile = new File(oatConfig.getBasedir());

        // add files in the base dir but not defined in tasklist in oat config file to allfiles list
        OatLicenseMain.readFiles(rootfile, allfiles, definedfiles);

        final File missedFiles = new File(resultfolder + "/oat_missed_files.txt");
        final FileWriter fileWriter = new FileWriter(missedFiles);
        for (final String allfile : allfiles) {
            fileWriter.write(allfile + "\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    /**
     * Recursive method to gather all files in the para allfiles folder
     *
     * @param file file folder
     * @param allfiles all files in folder
     * @param definedFiles all files defined in OAT config file
     */
    private static void readFiles(final File file, final List<String> allfiles, final List<String> definedFiles) {
        if (null == file) {
            return;
        }
        final File[] files = file.listFiles();

        if (files == null) {
            return;
        }

        for (final File file1 : files) {
            String filePath = "";
            try {
                filePath = file1.getCanonicalPath().replace('\\', '/');
            } catch (final IOException e) {
                OatLogUtil.traceException(e);
            }
            if (filePath.contains(".repo") || filePath.contains(".git")) {
                continue;
            }
            boolean matched = false;
            for (final String readfile : definedFiles) {
                if (filePath.startsWith(readfile)) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                continue;
            }

            if (file1.isDirectory()) {
                OatLicenseMain.readFiles(file1, allfiles, definedFiles);
            } else {
                allfiles.add(filePath);
            }
        }
    }
}
