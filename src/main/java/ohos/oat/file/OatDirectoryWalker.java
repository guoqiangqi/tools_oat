/*
 * Copyright (c) 2021-2022 Huawei Device Co., Ltd.
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
 * ChangeLog:
 * 2021.1 -  Extend from Apache Rat and enhanced a lot of capabilities to support OpenHarmony:
 * 1. Support nested git projects, every file can mapped to the most intimate project.
 * 2. Support directory analysis, such as LICENSE and README file in the root directory.
 * Modified by jalenchen
 * 2021.5 - Support Scan files of all projects concurrently in one task:
 * 1. Modify the report(final RatReport report, final File file) and delete setProjectFileDocument(document) calling
 * to fix bugs.
 * Modified by jalenchen
 */


package ohos.oat.file;

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.document.IOatDocument;
import ohos.oat.document.OatFileDocument;
import ohos.oat.task.IOatTaskProcessor;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLogUtil;

import org.apache.rat.api.RatException;
import org.apache.rat.report.RatReport;
import org.apache.rat.walker.Walker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Scan the specified project directory and trigger the analysis processes.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatDirectoryWalker extends Walker {
    // FileComparator to sort projects
    private final FileNameComparator comparator = new FileNameComparator();

    // project of this walker
    private final OatProject oatProject;

    // config of this process
    private final OatConfig oatConfig;

    private final IOatTaskProcessor taskProcessor;

    private final Map<String, IOatDocument> filteredFilePath2Document = new HashMap<>();

    /**
     * Constructor method
     *
     * @param oatConfig Oat config of this process
     * @param oatProject Oat project of this walker
     * @param projectRootDir Root dir of current scanning project
     * @param filter Filter information
     * @param taskProcessor Task Processor
     */
    public OatDirectoryWalker(final OatConfig oatConfig, final OatProject oatProject, final File projectRootDir,
        final FilenameFilter filter, final IOatTaskProcessor taskProcessor) {
        super(projectRootDir.getPath(), projectRootDir, filter);
        this.oatProject = oatProject;
        this.oatConfig = oatConfig;
        this.taskProcessor = taskProcessor;
    }

    /**
     * Not used
     *
     * @param ratReport RatReport
     * @throws RatException to pass errors
     */
    @Override
    public void run(final RatReport ratReport) throws RatException {
    }

    /**
     * The main fuction to process all file in current project
     */
    public void walkProjectFiles() {
        final File[] prj1LevelFiles = this.file.listFiles();
        if (prj1LevelFiles == null || prj1LevelFiles.length == 0) {
            return;
        }
        boolean needprocess = false;
        for (final File prj1LevelFile : prj1LevelFiles) {
            if (prj1LevelFile.getName().endsWith(".git") || prj1LevelFile.getName().endsWith(".repo")
                || prj1LevelFile.getName().endsWith(".svn")) {
                continue;
            }

            final IOatDocument prj1LevelDocument = new OatFileDocument(prj1LevelFile);
            prj1LevelDocument.setOatProject(this.oatProject);

            final boolean notFilteredFile = this.notFilteredFile(prj1LevelDocument);
            if (!notFilteredFile) {
                this.filteredFilePath2Document.put(prj1LevelDocument.getFile().getPath(), prj1LevelDocument);
                prj1LevelDocument.getStatus().setFileStatusFilteredByCommon();
            }
            final boolean notFilteredPath = this.notFilteredPath(prj1LevelDocument);
            final boolean isNotSubPrj = this.isNotSubPrj(prj1LevelDocument);

            if (notFilteredPath && notFilteredFile && isNotSubPrj) {
                needprocess = true;
            }
        }
        if (needprocess) {
            final IOatDocument prjectDocument = new OatFileDocument(this.file);
            prjectDocument.setOatProject(this.oatProject);
            this.process(prjectDocument);
        }
    }

    private boolean isNotSubPrj(final IOatDocument file) {
        final List<OatProject> includedPrjList = this.oatProject.getIncludedPrjList();
        final String shortPath = OatCfgUtil.getShortPath(this.oatConfig, file.getFile());
        for (final OatProject project : includedPrjList) {
            if (shortPath.startsWith(project.getPath())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Identify ignorable files
     *
     * @param oatDocument File to process
     * @return Whether ignore this file
     */
    private boolean notFilteredPath(final IOatDocument oatDocument) {

        String shortPath = OatCfgUtil.getShortPath(this.oatConfig, oatDocument.getFile());
        if (!this.oatConfig.needCheck(oatDocument.getFile())) {
            return false;
        }

        if (this.oatConfig.isPluginMode()) {
            shortPath = this.oatProject.getPath() + shortPath;
        }

        final List<String> oatFilePathFilterItems = this.oatProject.getFileFilterObj().getOatFilePathFilterItems();
        for (final String oatFilePathFilterItem : oatFilePathFilterItems) {
            String piPath = oatFilePathFilterItem;
            if (piPath.startsWith("projectroot/")) {
                piPath = piPath.replace("projectroot/", this.oatProject.getPath());
            }
            if (piPath.equals("projectroot")) {
                piPath = this.oatProject.getPath();
            }
            if (shortPath.startsWith(piPath)) {
                if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                    OatLogUtil.warn(this.getClass().getSimpleName(),
                        this.oatProject.getPath() + "\t:" + "\tstartsWith-skip\t" + this.name + "\t:"
                            + oatDocument.getFile().getPath());
                }
                return false;
            }
            final Pattern pattern = IOatMatcher.compilePattern(piPath);
            if (pattern == null) {
                return true;
            }
            final boolean needFilter = IOatMatcher.matchPattern(shortPath, pattern);
            if (needFilter) {
                if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                    OatLogUtil.warn(this.getClass().getSimpleName(),
                        this.oatProject.getPath() + "\t:" + "\tmatcher-skip\t" + this.name + "\t:"
                            + oatDocument.getFile().getPath());
                }

                return false;
            }
        }
        return true;
    }

    private boolean notFilteredFile(final IOatDocument oatDocument) {
        final String fileName = oatDocument.getFile().getName().toLowerCase(Locale.ENGLISH);
        final String filePath = oatDocument.getFile().getPath().toLowerCase(Locale.ENGLISH).replace("\\", "/");

        if ((fileName.contains("license") && (fileName.contains(".txt") || fileName.contains(".md")
            || fileName.contains(".htm"))) || fileName.equals("build.gn") || fileName.equals("license")
            || filePath.contains("/license/") || filePath.contains("/licenses/")) {
            return true;
        }

        final boolean notIgnored = this.isNotIgnored(oatDocument.getFile());
        if (!notIgnored) {
            if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                OatLogUtil.warn(this.getClass().getSimpleName(),
                    this.oatProject.getPath() + "\tIgnoredFile\t" + oatDocument.getFile().getPath());
            }
        }
        return notIgnored;
    }

    /**
     * Process a directory, ignoring any files/directories set to be ignored.
     *
     * @param file File to process
     */
    private void process(final IOatDocument file) {
        if (this.notFilteredPath(file)) {
            final File[] files = file.getFile().listFiles();
            if (files != null) {
                Arrays.sort(files, this.comparator);
                // breadth first traversal
                this.processNonDirectories(files);
                this.processDirectories(files);
            }
            // Also process folder
            this.report(file);
        }
    }

    /**
     * Process all files in a directory
     *
     * @param files the files to analyse
     */
    private void processNonDirectories(final File[] files) {
        for (final File file : files) {
            final IOatDocument document = new OatFileDocument(file);
            document.setOatProject(this.oatProject);
            if (this.notFilteredPath(document) && this.notFilteredFile(document) && !file.isDirectory()) {
                this.report(document);
            }
        }
    }

    /**
     * Process all directories
     *
     * @param files the directories to analyse
     */
    private void processDirectories(final File[] files) {
        for (final File file : files) {
            final IOatDocument document = new OatFileDocument(file);
            document.setOatProject(this.oatProject);
            if (this.notFilteredPath(document) && this.notFilteredFile(document) && file.isDirectory()) {
                if (!this.isRestricted(file)) {
                    this.process(document);
                }
            }
        }
    }

    /**
     * Report on the given file.
     */
    private void report(final IOatDocument document) {
        if (document == null) {
            return;
        }
        // final IOatDocument document = new OatFileDocument(file);
        // document.setOatProject(this.oatProject);
        if (this.file.getPath().equals(document.getFile().getPath())) {
            document.setProjectRoot(true);
            this.oatProject.setProjectFileDocument(document);
        }
        final boolean isDirectory = document.getFile().isDirectory();
        document.setDirectory(isDirectory);
        if (isDirectory) {
            final File[] files = document.getFile().listFiles();
            if (files != null && files.length > 0) {
                this.collectFileNames(document.getFile(), document, files);
                this.collectLicenseFileNames(document.getFile(), document);
            }
        }
        this.taskProcessor.addFileDocument(document);
    }

    private void collectLicenseFileNames(final File file, final IOatDocument document) {
        if (!document.isProjectRoot()) {
            return;
        }
        final String[] licenseFiles = this.oatProject.getLicenseFiles();
        for (final String licenseFile : licenseFiles) {
            final String licensepath = file.getPath() + licenseFile;
            final File file1 = new File(licensepath);
            if (file1.exists() && this.notFilteredPath(document) && this.notFilteredFile(document)) {
                final String licensefilename = OatCfgUtil.getShortPath(this.oatConfig, file1);
                if (!document.getOatProject()
                    .getProjectFileDocument()
                    .getListData("LICENSEFILE")
                    .contains(licensefilename)) {
                    document.getOatProject().getProjectFileDocument().addListData("LICENSEFILE", licensefilename);
                }
            }
        }
    }

    private void collectFileNames(final File file, final IOatDocument document, final File[] files) {
        for (final File file1 : files) {
            if ((!file1.isDirectory()) && this.notFilteredPath(document) && this.notFilteredFile(document)) {
                final String fileName = file1.getName();
                final String shotFileName = fileName.toLowerCase(Locale.ENGLISH).replace(" ", "");
                if (fileName.startsWith("LICENSE") || fileName.startsWith("LICENCE") || (fileName.startsWith("NOTICE"))
                    || (fileName.startsWith("COPYING")) || (fileName.startsWith("COPYRIGHT")) || (shotFileName.contains(
                    "licenseagreement")) || (shotFileName.contains("licenceagreement"))) {
                    document.getOatProject()
                        .getProjectFileDocument()
                        .addListData("LICENSEFILE", OatCfgUtil.getShortPath(this.oatConfig, file1));
                } else if (fileName.equals("README.OpenSource")) {
                    document.getOatProject()
                        .getProjectFileDocument()
                        .addListData("README.OpenSource", OatCfgUtil.getShortPath(this.oatConfig, file1));
                } else if (fileName.equals("README") || fileName.equals("README.md") || fileName.equals(
                    "README_zh.md")) {
                    document.getOatProject()
                        .getProjectFileDocument()
                        .addListData("README", OatCfgUtil.getShortPath(this.oatConfig, file1));
                } else {
                    //do nothing
                }
            }
        }
    }

    private static class FileNameComparator implements Comparator<File> {
        /**
         * Compare file names to sort files
         *
         * @param file1 File1 to sort
         * @param file2 File2 to sort
         * @return Sort result
         */
        @Override
        public int compare(final File file1, final File file2) {
            int result = 0;
            if (file1 == null) {
                if (file2 != null) {
                    result = 1;
                }
                return result;
            }
            if (file2 == null) {
                result = -1;
            } else {
                result = file1.getName().compareTo(file2.getName());
            }
            return result;
        }
    }
}
