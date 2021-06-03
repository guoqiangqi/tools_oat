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

import ohos.oat.analysis.headermatcher.OhosLicense;
import ohos.oat.config.OhosConfig;
import ohos.oat.config.OhosProject;
import ohos.oat.config.OhosTask;
import ohos.oat.report.IOhosReport;
import ohos.oat.report.OhosDirectoryWalker;
import ohos.oat.report.OhosMainReport;
import ohos.oat.utils.OhosCfgUtil;
import ohos.oat.utils.OhosLogUtil;
import ohos.oat.utils.OhosSpdxLicenseUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
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
public class OhosLicenseMain {
    /**
     * Prompt message when the program started
     */
    private static final String PROMPT_MESSAGE =
        "\n-------------------------------------------------------------------------------------------\n"
            + "OpenHarmony OSS Audit Tool \n" + "Copyright (C) 2021 Huawei Device Co., Ltd.\n"
            + "if you have any questions or concerns, please create issue in OpenHarmony-SIG/tools_oat "
            + " @jalenchen or chenyaxun.\n"
            + "-------------------------------------------------------------------------------------------\n";

    /**
     * Private constructure to prevent new instance
     */
    private OhosLicenseMain() {
    }

    /**
     * Main for OAT
     *
     * @param args Use -i to pass OAT config file path, the default file name is OAT.xml while in plugin mode, the first
     * para must be the project root dir, the second para must be the output file dir
     * @throws Exception exception wile process
     */
    public static void main(final String[] args) throws Exception {
        OhosLogUtil.print(OhosLicenseMain.class, OhosLicenseMain.PROMPT_MESSAGE);
        final Options options = new Options();
        final CommandLine cmd = parseCommandLine(options, args);
        final OhosConfig ohosConfig = new OhosConfig();

        String initOATCfgFile = "OAT.xml";
        if (cmd.hasOption("i")) {
            initOATCfgFile = cmd.getOptionValue("i");
        }
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\tinitOATCfgFile\t" + initOATCfgFile);
        boolean logSwitch = false;
        if (cmd.hasOption("l")) {
            logSwitch = true;
        }
        OhosLogUtil.setDebugMode(logSwitch);
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\tlogSwitch\t" + logSwitch);

        // The following para is used for pipleline plugin integration
        String sourceCodeRepoPath = "";
        if (cmd.hasOption("s")) {
            ohosConfig.setPluginMode(true);
            sourceCodeRepoPath = OhosCfgUtil.formatPath(cmd.getOptionValue("s"));
            if (!sourceCodeRepoPath.endsWith("/")) {
                sourceCodeRepoPath += "/";
            }
            ohosConfig.setBasedir(sourceCodeRepoPath);
            initOATCfgFile = OhosLicenseMain.class.getResource("/OAT-Default.xml").toString();
        }
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\tsourceCodeRepoPath\t" + sourceCodeRepoPath);
        String reportFile = "";
        if (cmd.hasOption("s") && cmd.hasOption("r")) {
            reportFile = OhosCfgUtil.formatPath(cmd.getOptionValue("r"));
        }
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\treportFile\t" + reportFile);
        String nameOfRepository = "";
        if (cmd.hasOption("s") && cmd.hasOption("n")) {
            nameOfRepository = OhosCfgUtil.formatPath(cmd.getOptionValue("n"));
        }
        if (nameOfRepository.trim().length() <= 0) {
            nameOfRepository = "defaultProject";
        }
        ohosConfig.setRepositoryName(nameOfRepository);
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\tnameOfRepository\t" + nameOfRepository);
        String mode = "0";
        if (cmd.hasOption("s") && cmd.hasOption("m")) {
            final String tmpMode = cmd.getOptionValue("m");
            if (tmpMode.equals("0") || tmpMode.equals("1")) {
                mode = tmpMode;
            }
        }
        ohosConfig.setPluginCheckMode(mode);
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\tmode\t" + mode);
        String fileList = "";
        if (cmd.hasOption("s") && cmd.hasOption("f")) {
            fileList = OhosCfgUtil.formatPath(cmd.getOptionValue("f"));
        }
        ohosConfig.setSrcFileList(fileList);
        OhosLogUtil.warn(OhosLicenseMain.class, "CommandLine" + "\tfileList\t" + fileList);
        if (cmd.hasOption("s")) {
            if (!cmd.hasOption("r") || !cmd.hasOption("n")) {
                OhosLogUtil.print(OhosLicenseMain.class, "Args invalid, the valid args is: [-s sourceCodeRepoPath -r "
                    + "reportFilePath -n nameOfRepo -m 0] or [-s sourceCodeRepoPath -r reportFilePath -n nameOfRepo -m "
                    + "1 -f filelistSeparatedBy|]" + " ");
                System.exit(0);
            }
        }
        if (cmd.hasOption("t")) {
            ohosConfig.putData("TraceLicenseListOnly", "true");
        }
        if (cmd.hasOption("k")) {
            ohosConfig.putData("TraceSkippedAndIgnoredFiles", "true");
        }
        if (cmd.hasOption("g")) {
            ohosConfig.putData("IgnoreProjectOAT", "true");
        }
        OhosCfgUtil.initOhosConfig(ohosConfig, initOATCfgFile, sourceCodeRepoPath);
        final List<OhosLicense> spdxLicenseList = OhosSpdxLicenseUtil.createSpdxLicenseList();
        ohosConfig.setLicenseList(spdxLicenseList);
        OhosLicenseMain.oatCheck(reportFile, ohosConfig);
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
            OhosLogUtil.traceException(e);
        }
        if (ArrayUtils.isEmpty(args) || cmd.hasOption("h")) {
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
     * @param ohosConfig Config in oat.xml
     * @throws Exception in case of errors.
     */
    private static void oatCheck(final String reportFile, final OhosConfig ohosConfig) throws Exception {
        final List<OhosTask> taskList = ohosConfig.getTaskList();
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
        OhosLogUtil.print(OhosLicenseMain.class, startTime + " Start analyzing....");
        if (reportFile.length() <= 0) {
            final File dir = new File(resultfolder);
            if (!dir.exists()) {
                final boolean success = dir.mkdirs();
                if (!success) {
                    OhosLogUtil.warn(OhosLicenseMain.class, "Create dir failed");
                }
            }
        }

        for (final OhosTask ohosTask : taskList) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    this.repoort();
                }

                private void repoort() {
                    File resultFile = null;
                    if (reportFile.length() > 0) {
                        resultFile = new File(reportFile);
                    } else {
                        resultFile = new File(resultfolder + "/oat_" + ohosTask.getNamne() + ".txt");
                    }
                    OhosLogUtil.print(OhosLicenseMain.class, "resultfilepath:" + resultFile);
                    try {
                        final FileWriter fileWriter = new FileWriter(resultFile, false);

                        final IOhosReport report = new OhosMainReport(ohosConfig, fileWriter);
                        report.startReport();
                        OhosLicenseMain.checkProjects(report, ohosTask, ohosConfig);
                        report.concurrentReport();
                        report.endReport();
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (final Exception e) {
                        OhosLogUtil.traceException(e);
                    }
                }
            });
        }
        exec.shutdown();
    }

    private static void checkProjects(final IOhosReport report, final OhosTask ohosTask, final OhosConfig ohosConfig)
        throws IOException, RatException {
        final List<OhosProject> projectList = ohosTask.getProjectList();

        for (final OhosProject ohosProject : projectList) {
            final FilenameFilter filenameFilter = OhosLicenseMain.parseFileExclusions(
                ohosProject.getFileFilterObj().getFileFilterItems());
            final long startTime = System.currentTimeMillis();
            final IReportable base = OhosLicenseMain.getDirectoryWalker(ohosConfig, ohosProject, filenameFilter);
            if (base != null) {
                base.run(report);
            }
            final long costTime = (System.currentTimeMillis() - startTime) / 1000;
            OhosLogUtil.warn(OhosLicenseMain.class, ohosProject.getPath() + "\tCostTime\t" + costTime);

        }

    }

    private static IReportable getDirectoryWalker(final OhosConfig ohosConfig, final OhosProject ohosProject,
        final FilenameFilter inputFileFilter) {
        final String prjDirectory;
        if (ohosConfig.isPluginMode()) {
            // 如果是插件模式，直接扫描根目录下所有
            prjDirectory = ohosConfig.getBasedir();
        } else {
            final String prjPath = ohosProject.getPath();
            prjDirectory = ohosConfig.getBasedir() + prjPath;
        }
        final File base = new File(prjDirectory);
        if (!base.exists()) {
            return null;
        }

        if (base.isDirectory()) {
            return new OhosDirectoryWalker(ohosConfig, ohosProject, base, inputFileFilter);
        }
        return null;
    }

    private static FilenameFilter parseFileExclusions(final List<String> excludes) throws IOException {
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
                orFilter.addFileFilter(new RegexFileFilter(exclusion));
            } catch (final PatternSyntaxException e) {
                continue;
            }
        }
        return new NotFileFilter(orFilter);
    }

    private static void printMissedFiles(final OhosConfig ohosConfig, final List<OhosTask> taskList,
        final String resultfolder) throws IOException {
        if (ohosConfig.isPluginMode()) {
            return;
        }

        // Files defined in tasklist in oat config file
        final List<String> definedfiles = new ArrayList<>();
        for (final OhosTask ohosTask : taskList) {
            for (final OhosProject ohosProject : ohosTask.getProjectList()) {
                final String path = ohosConfig.getBasedir() + ohosProject.getPath();
                definedfiles.add(path);
            }
        }

        final List<String> allfiles = new ArrayList<>();
        final File rootfile = new File(ohosConfig.getBasedir());

        // add files in the base dir but not defined in tasklist in oat config file to allfiles list
        OhosLicenseMain.readFiles(rootfile, allfiles, definedfiles);

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
                OhosLogUtil.traceException(e);
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
                OhosLicenseMain.readFiles(file1, allfiles, definedFiles);
            } else {
                allfiles.add(filePath);
            }
        }
    }
}
