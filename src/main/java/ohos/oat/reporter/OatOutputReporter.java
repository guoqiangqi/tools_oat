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
 * 2021.2 - Transfor all the analysis result to readable text and write to the specified file
 * Modified by jalenchen
 * 2021.3 - Add key-value style output to integrate with pipleline tools
 * Modified by jalenchen
 */

package ohos.oat.reporter;

import static ohos.oat.utils.IOatCommonUtils.getTaskDefaultPrjName;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLogUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Transform all the analysis result to output files
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatOutputReporter extends AbstractOatReporter {
    private final Map<String, List<ReportItem>> resultMap = new HashMap<>();

    private final String reportFileName = "PlainReport_";

    private FileWriter writer;

    private File resultFile;

    private List<ReportItem> archiveList;

    private List<ReportItem> compatibilityList;

    private List<ReportItem> licenseHeaderList;

    private List<ReportItem> copyrightHeaderList;

    private List<ReportItem> licenseFileList;

    private List<ReportItem> readmeOpenSourceList;

    private List<ReportItem> readmeList;

    private List<ReportItem> importList;

    private List<ReportItem> redundantLicenseList;

    /**
     * @param oatConfig OAT configuration data structure
     * @param oatTask Task
     */
    @Override
    public IOatReporter init(final OatConfig oatConfig, final OatTask oatTask) {
        super.init(oatConfig, oatTask);

        this.resultMap.put("Archive", new ArrayList());
        this.resultMap.put("Compatibility", new ArrayList());
        this.resultMap.put("LicenseHeader", new ArrayList());
        this.resultMap.put("CopyrightHeader", new ArrayList());
        this.resultMap.put("LicenseFile", new ArrayList());
        this.resultMap.put("ReadmeOpenSource", new ArrayList());
        this.resultMap.put("Readme", new ArrayList());
        this.resultMap.put("Import", new ArrayList());
        this.resultMap.put("RedundantLicense", new ArrayList());
        this.archiveList = this.resultMap.get("Archive");
        this.compatibilityList = this.resultMap.get("Compatibility");
        this.licenseHeaderList = this.resultMap.get("LicenseHeader");
        this.copyrightHeaderList = this.resultMap.get("CopyrightHeader");
        this.licenseFileList = this.resultMap.get("LicenseFile");
        this.readmeOpenSourceList = this.resultMap.get("ReadmeOpenSource");
        this.readmeList = this.resultMap.get("Readme");
        this.importList = this.resultMap.get("Import");
        this.redundantLicenseList = this.resultMap.get("RedundantLicense");

        String reportFolder = oatConfig.getData("reportFolder");

        if (reportFolder.length() <= 0) {
            reportFolder = "./";
        }
        final File dir = new File(reportFolder);
        if (!dir.exists()) {
            final boolean success = dir.mkdirs();
            if (!success) {
                OatLogUtil.warn(this.getClass().getSimpleName(), "Create dir failed");
            }
        }

        String filePrefix = getTaskDefaultPrjName(oatTask);
        filePrefix = filePrefix.replace("/", "_");
        if (filePrefix.length() > 1 && filePrefix.endsWith("_")) {
            filePrefix = filePrefix.substring(0, filePrefix.length() - 1);
        }
        this.resultFile = new File(reportFolder + "/" + this.reportFileName + filePrefix + ".txt");

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(this.resultFile, false);
        } catch (final IOException e) {
            e.printStackTrace();
            return this;
        }
        this.writer = fileWriter;
        return this;
    }

    /**
     * Report document
     *
     * @param oatFileDocument OatFileDocument
     */
    @Override
    public void report(final IOatDocument oatFileDocument) {
        String tmpString = oatFileDocument.getData("Result.License");
        if (tmpString != null && tmpString.equals("false")) {
            this.licenseHeaderList.add(
                new ReportItem("License Header Invalid", oatFileDocument.getData("LicenseName"), 0, oatFileDocument));
        }
        tmpString = oatFileDocument.getData("Result.Copyright");
        if (tmpString != null && tmpString.equals("false")) {

            this.copyrightHeaderList.add(
                new ReportItem("Copyright Header Invalid", oatFileDocument.getData("CopyrightOwner"), 0,
                    oatFileDocument));
        }
        tmpString = oatFileDocument.getData("Result.LicenseFile");
        if (tmpString != null && tmpString.equals("false")) {
            this.licenseFileList.add(new ReportItem("No License File", "", 0, oatFileDocument));
        }
        tmpString = oatFileDocument.getData("Result.ReadmeOpenSource");
        if (tmpString != null && tmpString.equals("false")) {
            this.readmeOpenSourceList.add(new ReportItem("No Readme.OpenSource", "", 0, oatFileDocument));
        }
        tmpString = oatFileDocument.getData("Result.Compatibility");
        if (tmpString != null && tmpString.equals("false")) {
            this.compatibilityList.add(
                new ReportItem("License Not Compatible", oatFileDocument.getData("LicenseName"), 0, oatFileDocument));
        }
        tmpString = oatFileDocument.getData("Result.Readme");
        if (tmpString != null && tmpString.equals("false")) {
            this.readmeList.add(new ReportItem("No Readme", "", 0, oatFileDocument));
        }
        tmpString = oatFileDocument.getData("Result.Import");
        if (tmpString != null && tmpString.equals("false")) {
            this.importList.add(
                new ReportItem("Import Invalid", oatFileDocument.getData("ImportName"), 0, oatFileDocument));
        }
        // tmpString = metaData.value("NoRedundantLicenseFile");
        // if (tmpString != null && tmpString.equals("false")) {
        //     this.redundantLicenseList.add(
        //         new ReportItem("Redundant License File", metaData.value("RedundantLicenseFile"), 0,
        //         oatFileDocument));
        // }
        tmpString = oatFileDocument.getData("Result.FileType");
        if (tmpString != null && tmpString.equals("false")) {
            this.archiveList.add(
                new ReportItem("Invalid File Type", oatFileDocument.getData("FileType"), 0, oatFileDocument));
        }
    }

    /**
     * Write report to file
     */
    @Override
    public void writeReport() {
        try {
            this.writer.write("\nInvalid File Type Total Count: " + this.archiveList.size() + "\n");
            this.writeReport(this.archiveList);
            this.writer.write("\nLicense Not Compatible Total Count: " + this.compatibilityList.size() + "\n");
            this.writeReport(this.compatibilityList);
            this.writer.write("\nLicense Header Invalid Total Count: " + this.licenseHeaderList.size() + "\n");
            this.writeReport(this.licenseHeaderList);
            this.writer.write("\nCopyright Header Invalid Total Count: " + this.copyrightHeaderList.size() + "\n");
            this.writeReport(this.copyrightHeaderList);
            this.writer.write("\nNo License File Total Count: " + this.licenseFileList.size() + "\n");
            this.writeReport(this.licenseFileList);
            this.writer.write("\nNo Readme.OpenSource Total Count: " + this.readmeOpenSourceList.size() + "\n");
            this.writeReport(this.readmeOpenSourceList);
            this.writer.write("\nNo Readme Total Count: " + this.readmeList.size() + "\n");
            this.writeReport(this.readmeList);
            this.writer.write("\nImport Invalid Total Count: " + this.importList.size() + "\n");
            this.writeReport(this.importList);
            this.writer.write("\nRedundant License File Total Count: " + this.redundantLicenseList.size() + "\n");
            this.writeReport(this.redundantLicenseList);
            this.writer.flush();
            this.writer.close();
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
        OatLogUtil.println("", "Result file path:\t" + this.resultFile);
    }

    private void writeReport(final List<ReportItem> archiveList) throws IOException {
        for (final ReportItem reportItem : archiveList) {
            this.writer.write(reportItem.toString());
        }

    }

    /**
     * @return To string
     */
    @Override
    public String toString() {
        return "OatOutputReport{" + "archiveList=" + this.archiveList + ", compatibilityList=" + this.compatibilityList
            + ", licenseHeaderList=" + this.licenseHeaderList + ", copyrightHeaderList=" + this.copyrightHeaderList
            + ", licenseFileList=" + this.licenseFileList + ", readmeOpenSourceList=" + this.readmeOpenSourceList
            + ", readmeList=" + this.readmeList + ", importList=" + this.importList + '}';
    }

    private class ReportItem {
        private final String name;

        private final String content;

        private final String file;

        private final String project;

        private final int line;

        private ReportItem(final String name, final String content, final int line,
            final IOatDocument oatFileDocument) {
            this.name = name;
            this.content = content;
            this.line = line;
            this.file = oatFileDocument.getName();
            this.project = oatFileDocument.getOatProject().getName();
        }

        @Override
        public String toString() {
            String string = "";
            if (OatOutputReporter.this.oatConfig.isPluginMode()) {
                string = "Name:\t" + this.name + "\tContent:\t" + this.content + "\tLine:\t" + this.line
                    + "\tProject:\t" + this.project + "\tFile:\t" + this.file + "\n";
            } else {
                string = this.name + "\t" + this.content + "\t" + this.line + "\t" + this.project + "\t" + this.file
                    + "\n";
            }
            //            final String[] sss = string.split("\t");
            return string;
        }

        private String getName() {
            return this.name;
        }

        private String getContent() {
            return this.content;
        }

        private int getLine() {
            return this.line;
        }

        private String getFile() {
            return this.file;
        }

        private String getProject() {
            return this.project;
        }
    }
}
