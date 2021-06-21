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


package ohos.oat.report;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.document.OatFileDocument;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLogUtil;

import org.apache.rat.api.RatException;
import org.apache.rat.report.RatReport;
import org.apache.rat.walker.Walker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Scan the specified project directory and trigger the analysis processes.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatDirectoryWalker extends Walker {
    // FileComparator to sort projects
    private final FileNameComparator COMPARATOR = new FileNameComparator();

    // project of this walker
    private final OatProject oatProject;

    // config of this process
    private final OatConfig oatConfig;

    /**
     * Constructor method
     *
     * @param oatConfig Oat config of this process
     * @param oatProject Oat project of this walker
     * @param projectRootDir Root dir of current scanning project
     * @param filter Filter information
     */
    public OatDirectoryWalker(final OatConfig oatConfig, final OatProject oatProject, final File projectRootDir,
        final FilenameFilter filter) {
        super(projectRootDir.getPath(), projectRootDir, filter);
        this.oatProject = oatProject;
        this.oatConfig = oatConfig;
    }

    /**
     * The main fuction to process all file in current project
     *
     * @param report RatReport to run on this Directory walker.
     * @throws RatException to pass errors
     */
    @Override
    public void run(final RatReport report) throws RatException {
        final File[] files = this.file.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        boolean needprocess = false;
        for (final File file1 : files) {
            final boolean notIgnored = this.notFilteredFile(file1);
            final boolean needCheck = this.needCheck(file1);

            if (needCheck && notIgnored && (!file1.getName().endsWith(".git"))) {
                needprocess = true;
            }
        }
        if (needprocess) {
            this.process(report, this.file);
        }
    }

    /**
     * Identify ignorable files
     *
     * @param file File to process
     * @return Whether ignore this file
     */
    private boolean needCheck(final File file) {

        final List<OatProject> includedPrjList = this.oatProject.getIncludedPrjList();
        String shortPath = OatCfgUtil.getShortPath(this.oatConfig, file);
        if (!this.oatConfig.needCheck(file)) {
            return false;
        }
        for (final OatProject project : includedPrjList) {
            if (shortPath.startsWith(project.getPath())) {
                return false;
            }
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
                        this.oatProject.getPath() + "\t:" + "\tstartsWith-skip\t" + this.name + "\t:" + file.getPath());
                }
                return false;
            }
            final Pattern pattern = Pattern.compile(piPath, Pattern.CASE_INSENSITIVE);
            final boolean needFilter = pattern.matcher(shortPath).matches();
            if (needFilter) {
                if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                    OatLogUtil.warn(this.getClass().getSimpleName(),
                        this.oatProject.getPath() + "\t:" + "\tmatcher-skip\t" + this.name + "\t:" + file.getPath());
                }

                return false;
            }
        }
        return true;
    }

    private boolean notFilteredFile(final File file) {
        final String fileName = file.getName().toLowerCase(Locale.ENGLISH);
        final String filePath = file.getPath().toLowerCase(Locale.ENGLISH).replace("\\", "/");

        if ((fileName.contains("license") && (fileName.contains(".txt") || fileName.contains(".md")
            || fileName.contains(".htm"))) || fileName.equals("build.gn") || fileName.equals("license")
            || filePath.contains("/license/") || filePath.contains("/licenses/")) {
            return true;
        }

        final boolean notIgnored = this.isNotIgnored(file);
        if (!notIgnored) {
            if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                OatLogUtil.warn(this.getClass().getSimpleName(),
                    this.oatProject.getPath() + "\tIgnoredFile\t" + file.getPath());
            }
        }
        return notIgnored;
    }

    /**
     * Process a directory, ignoring any files/directories set to be ignored.
     *
     * @param report RatReport object to analyse this file
     * @param file File to process
     * @throws RatException exception wile process
     */
    private void process(final RatReport report, final File file) throws RatException {
        if (this.needCheck(file)) {
            final File[] files = file.listFiles();
            if (files != null) {
                Arrays.sort(files, this.COMPARATOR);
                // breadth first traversal
                this.processNonDirectories(report, files);
                this.processDirectories(report, files);
            }
            // Also process folder
            this.report(report, file);
        }
    }

    /**
     * Process all files in a directory
     *
     * @param report Rat report to analyse files
     * @param files the files to analyse
     * @throws RatException exception wile process
     */
    private void processNonDirectories(final RatReport report, final File[] files) throws RatException {
        for (final File file : files) {

            if (this.needCheck(file) && this.notFilteredFile(file) && !file.isDirectory()) {
                this.report(report, file);
            }
        }
    }

    /**
     * Process all directories
     *
     * @param report Rat report to analyse files
     * @param files the directories to analyse
     * @throws RatException exception wile process
     */
    private void processDirectories(final RatReport report, final File[] files) throws RatException {
        for (final File file : files) {
            if (this.needCheck(file) && this.notFilteredFile(file) && file.isDirectory()) {
                if (!this.isRestricted(file)) {
                    this.process(report, file);
                }
            }
        }
    }

    /**
     * Report on the given file.
     *
     * @param report Rat report to process the file with
     * @param file the file to be reported on
     * @throws RatException exception wile process
     */
    private void report(final RatReport report, final File file) throws RatException {
        if (file == null) {
            return;
        }
        final OatFileDocument document = new OatFileDocument(file);
        document.setOatProject(this.oatProject);
        if (this.file.getPath().equals(file.getPath())) {
            document.setProjectRoot(true);
            this.oatProject.setProjectFileDocument(document);
        }
        final boolean isDirectory = file.isDirectory();
        document.setDirectory(isDirectory);
        if (isDirectory) {
            final File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                this.collectFileNames(file, document, files);
                this.collectLicenseFileNames(file, document);
            }
        }
        report.report(document);
    }

    private void collectLicenseFileNames(final File file, final OatFileDocument document) {
        if (!document.isProjectRoot()) {
            return;
        }
        final String[] licenseFiles = this.oatProject.getLicenseFiles();
        for (final String licenseFile : licenseFiles) {
            final String licensepath = file.getPath() + licenseFile;
            final File file1 = new File(licensepath);
            if (file1.exists() && this.needCheck(file) && this.notFilteredFile(file)) {
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

    private void collectFileNames(final File file, final OatFileDocument document, final File[] files) {
        for (final File file1 : files) {
            if ((!file1.isDirectory()) && this.needCheck(file) && this.notFilteredFile(file)) {
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
