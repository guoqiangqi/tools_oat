/*
 * Copyright (c) 2022 Huawei Device Co., Ltd.
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
 */

package ohos.oat.reporter;

import static ohos.oat.utils.IOatCommonUtils.getTaskDefaultPrjName;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.document.IOatDocument;
import ohos.oat.reporter.model.OatReportConfigInfo;
import ohos.oat.reporter.model.OatReportCopyrightInfo;
import ohos.oat.reporter.model.OatReportCreatorInfo;
import ohos.oat.reporter.model.OatReportFileInfo;
import ohos.oat.reporter.model.OatReportFileNameInfo;
import ohos.oat.reporter.model.OatReportFileTypeInfo;
import ohos.oat.reporter.model.OatReportInfo;
import ohos.oat.reporter.model.OatReportLicenseInfo;
import ohos.oat.reporter.model.OatReportProjectInfo;
import ohos.oat.reporter.model.OatReportSummaryInfo;
import ohos.oat.reporter.model.file.OatReportFile;
import ohos.oat.reporter.model.license.OatReportLicense;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collect the detail analysis result to output files
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatDetailPlainReporter extends AbstractOatReporter {

    private final String reportFileName = "PlainReport_";

    private final OatReportSummaryInfo oatReportSummaryInfo = new OatReportSummaryInfo();

    private final Map<String, OatReportInfo> prjName2ReportInfo = new HashMap<>();

    private FileWriter writer;

    private File resultFile;

    /**
     * @param oatConfig OAT configuration data structure
     * @param oatTask Task
     */
    @Override
    public IOatReporter init(final OatConfig oatConfig, final OatTask oatTask) {
        super.init(oatConfig, oatTask);

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
        this.resultFile = new File(reportFolder + "/" + this.reportFileName + filePrefix + "_Detail.txt");

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(this.resultFile, false);
        } catch (final IOException e) {
            e.printStackTrace();
            return this;
        }
        this.writer = fileWriter;
        this.oatReportSummaryInfo.getReportCreatorInfo().setReportUser(System.getenv().get("USERNAME"));
        final Date date = new Date();
        final String strDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(date);
        this.oatReportSummaryInfo.getReportCreatorInfo().setReportTime(strDate);
        this.oatReportSummaryInfo.getReportCreatorInfo()
            .setReportNotes(
                "Generated by OAT," + "If you have any questions or concerns, please create issue at https://gitee"
                    + ".com/openharmony-sig/tools_oat/issues");
        this.oatReportSummaryInfo.getReportCreatorInfo().setReportInitCommand(oatConfig.getData("initCommand"));

        return this;
    }

    /**
     * Report document
     *
     * @param oatFileDocument OatFileDocument
     */
    @Override
    public void report(final IOatDocument oatFileDocument) {
        final String prjName = oatFileDocument.getOatProject().getName();
        OatReportInfo oatReportInfo = this.prjName2ReportInfo.get(prjName);
        if (null == oatReportInfo) {
            oatReportInfo = new OatReportInfo();
            this.prjName2ReportInfo.put(prjName, oatReportInfo);
            this.oatReportSummaryInfo.addOatReportInfo(oatReportInfo);
            oatReportInfo.getReportProjectInfo().setProjectName(prjName);
            oatReportInfo.getReportProjectInfo().setMainLicense(oatFileDocument.getOatProject().getData("MainLicense"));
            oatReportInfo.getReportProjectInfo().setProjectHomePage(prjName);
            oatReportInfo.getReportConfigInfoInfo()
                .setProjectPolicy(oatFileDocument.getOatProject().getData("ProjectOAT"));

        }
        final OatReportFile oatReportFile = new OatReportFile(oatFileDocument);
        final IOatDocument.Status status = oatFileDocument.getStatus();
        final OatReportFileInfo oatReportFileInfo = oatReportInfo.getReportFileInfo();
        final String fileExt = FilenameUtils.getExtension(oatFileDocument.getFile().getName());
        if (status.isFileStatusNormal()) {
            oatReportFileInfo.addProjectNormalFile(oatReportFile);
        } else if (status.isFileStatusFiltered()) {
            oatReportFileInfo.addProjectFilteredFile(
                oatReportFile.copy("Project Filtered File", fileExt, status.getFileStatusRule(),
                    status.getFileStatusDesc()));
        } else if (status.isFileStatusFilteredByHeader()) {
            oatReportFileInfo.addProjectFilteredByHeaderFile(
                oatReportFile.copy("Project Filtered By Header File", fileExt, status.getFileStatusRule(),
                    status.getFileStatusDesc()));
        }

        final OatReportFileNameInfo oatReportFileNameInfo = oatReportInfo.getReportFileNameInfo();
        final OatReportFileTypeInfo oatReportFileTypeInfo = oatReportInfo.getReportFileTypeInfo();
        final OatReportLicenseInfo oatReportLicenseInfo = oatReportInfo.getReportLicenseInfo();
        final OatReportCopyrightInfo oatReportCopyrightInfo = oatReportInfo.getReportCopyrightInfo();
        for (final IOatDocument.FilteredRule filteredRule : oatFileDocument.getStatus()
            .getPolicyStatusFilteredMap()
            .values()) {
            filteredRule.setFilePath(oatReportFile.getFilePath());
            oatReportFileNameInfo.addFilteredRule(filteredRule);
            oatReportFileTypeInfo.addFilteredRule(filteredRule);
            oatReportLicenseInfo.addLicenseFilteredRule(filteredRule);
            oatReportLicenseInfo.addCompatibleFilteredRule(filteredRule);
            oatReportCopyrightInfo.addFilteredRule(filteredRule);
        }

        if (oatFileDocument.isProjectRoot()) {
            if (oatFileDocument.getData("Result.LicenseFile").equals("false")) {
                oatReportFileNameInfo.setHasLicenseFile(false);
            } else {
                oatReportFileNameInfo.setHasLicenseFile(true);
            }

            if (oatFileDocument.getData("Result.ReadmeOpenSource").equals("false")) {
                oatReportFileNameInfo.setHasReadmeOpenSourceFile(false);
            } else {
                oatReportFileNameInfo.setHasReadmeOpenSourceFile(true);
            }

            if (oatFileDocument.getData("Result.Readme").equals("false")) {
                oatReportFileNameInfo.setHasReadmeFile(false);
            } else {
                oatReportFileNameInfo.setHasReadmeFile(true);
            }
        }

        if (oatFileDocument.getData("Result.FileType").equals("false")) {
            oatReportFileTypeInfo.addProjectInvalidTypeFile(oatReportFile.copy("Policy Not Passed-FileType"));
        }

        final String copyrightOwner = oatFileDocument.getData("CopyrightOwner").replace("\t", "");

        if (oatFileDocument.getData("Result.Copyright").equals("true")) {
            oatReportCopyrightInfo.addNormalCopyright(copyrightOwner);
            oatReportCopyrightInfo.addNormalCopyrightHeaderFile(
                oatReportFile.copy("Policy Passed-NormalCopyright", copyrightOwner));
        } else if (oatFileDocument.getData("Result.Copyright").equals("false")) {
            if ("NULL".equals(copyrightOwner)) {
                oatReportCopyrightInfo.addNoCopyrightHeaderFile(
                    oatReportFile.copy("Policy Not Passed-NoCopyright", copyrightOwner));
            } else {
                oatReportCopyrightInfo.addAbnormalCopyright(copyrightOwner);
                oatReportCopyrightInfo.addAbnormalCopyrightHeaderFile(
                    oatReportFile.copy("Policy Not Passed-WrongCopyright", copyrightOwner));
            }
        }
        oatReportCopyrightInfo.addCopyright2File(copyrightOwner, oatReportFile);

        final String licenseName = oatFileDocument.getData("LicenseName");
        final OatReportLicense oatReportlicense = new OatReportLicense();
        oatReportlicense.setLicenseId(licenseName);
        if (oatFileDocument.getData("Result.License").equals("true")) {
            oatReportLicenseInfo.addNormalLicenseType(oatReportlicense);
        } else if (oatFileDocument.getData("Result.License").equals("false")) {
            if ("NULL".equals(licenseName) || "NoLicenseHeader".equals(licenseName)) {
                oatReportLicenseInfo.addNoLicenseHeaderFile(
                    oatReportFile.copy("Policy Not Passed-NoLicenseHeader", licenseName));
            } else {
                oatReportLicenseInfo.addAbnormalLicenseType(oatReportlicense);
                oatReportLicenseInfo.addAbnormalLicenseHeaderFile(
                    oatReportFile.copy("Policy Not Passed-WrongLicenseHeader", licenseName));
            }
        }
        oatReportLicenseInfo.addLicenseId2File(licenseName, oatReportFile);

        if (oatFileDocument.getData("Result.Compatibility").equals("false")) {
            oatReportLicenseInfo.addNotCompatibleLicenseType(oatReportlicense);
            oatReportLicenseInfo.addNotCompatibleLicenseTypeFile(
                oatReportFile.copy("Policy Not Passed-Compatibility", licenseName));
        } else {
            if (licenseName.length() > 0 && !"NULL".equals(licenseName) && !"NoLicenseHeader".equals(licenseName)
                && !"InvalidLicense".equals(licenseName)) {
                oatReportLicenseInfo.addCompatibleLicenseType(oatReportlicense);

            }
        }

    }

    /**
     * Write report to file
     */
    @Override
    public void writeReport() {
        if (this.writer == null) {
            OatLogUtil.println("", "Writer is null, file path:\t" + this.resultFile);
            return;
        }
        try {
            final OatReportCreatorInfo oatReportCreatorInfo = this.oatReportSummaryInfo.getReportCreatorInfo();
            this.writeLine("========Report Basic Information========");
            this.writeLine("");
            this.writeLine("Report User: ", oatReportCreatorInfo.getReportUser());
            this.writeLine("Report Time: ", oatReportCreatorInfo.getReportTime());
            this.writeLine("Report Tool: ", oatReportCreatorInfo.getReportTool());
            final String toolVersion = oatReportCreatorInfo.getReportToolVersion();
            this.writeLine("Report Tool Version: ", toolVersion);
            this.writeLine("Report Notes: ", oatReportCreatorInfo.getReportNotes());
            this.writeLine("Report Command: ", "java -jar ohos_ossaudittool-" + toolVersion + ".jar "
                + this.oatReportSummaryInfo.getReportCreatorInfo().getReportInitCommand());
            this.writeLine("");

            this.writeLine("========Report Project List========");
            this.writeLine("");
            final List<OatReportInfo> oatReportInfoList = this.oatReportSummaryInfo.getOatReportInfoList();
            this.writeLine("Report Project Count: ", "" + oatReportInfoList.size());

            int index = 1;
            for (final OatReportInfo oatReportInfo : oatReportInfoList) {
                this.writeLine("Project Begin " + index + "/" + oatReportInfoList.size() + ": \t");
                final OatReportProjectInfo oatReportProjectInfo = oatReportInfo.getReportProjectInfo();
                final OatReportFileNameInfo oatReportFileNameInfo = oatReportInfo.getReportFileNameInfo();
                final OatReportFileInfo oatReportFileInfo = oatReportInfo.getReportFileInfo();
                final OatReportFileTypeInfo oatReportFileTypeInfo = oatReportInfo.getReportFileTypeInfo();
                final OatReportLicenseInfo oatReportLicenseInfo = oatReportInfo.getReportLicenseInfo();
                final OatReportCopyrightInfo oatReportCopyrightInfo = oatReportInfo.getReportCopyrightInfo();
                final OatReportConfigInfo oatReportConfigInfo = oatReportInfo.getReportConfigInfoInfo();
                this.writeLine("");

                this.writeLine("========Project Basic Information========");
                this.writeLine("");
                this.writeLine("Project Name: ", oatReportProjectInfo.getProjectName());
                this.writeLine("Project Home Page: ", oatReportProjectInfo.getProjectHomePage());
                this.writeLine("Project Branch: ", oatReportProjectInfo.getProjectBranch());
                this.writeLine("Project Commit Id: ", oatReportProjectInfo.getProjectCommitId());
                this.writeLine("Project Version: ", oatReportProjectInfo.getProjectVersion());
                this.writeLine("Project Main License: ", oatReportProjectInfo.getMainLicense());

                this.writeLine("");

                this.writeLine("========Project Summary Information========");
                this.writeLine("");
                this.writeLine("Project File Count: ", "" + oatReportFileInfo.getProjectFileCount());
                this.writeLine("Project Normal File Count: ", "" + oatReportFileInfo.getProjectNormalFileCount());
                this.writeLine("Project Filtered File Count: ", "" + oatReportFileInfo.getProjectFilteredFileCount());
                this.writeLine("Project Filtered By Header File Count: ",
                    "" + oatReportFileInfo.getProjectFilteredByHeaderFileCount());

                this.writeLine("");
                this.writeLine("Policy Filtered-FileName Count: " + "" + oatReportFileNameInfo.getFilteredRuleCount());
                this.writeLine("Policy Filtered-FileType Count: " + "" + oatReportFileTypeInfo.getFilteredRuleCount());
                this.writeLine(
                    "Policy Filtered-LicenseHeader Count: " + "" + oatReportLicenseInfo.getLicenseFilteredRuleCount());
                this.writeLine("Policy Filtered-Compatibility Count: " + ""
                    + oatReportLicenseInfo.getCompatibleFilteredRuleCount());
                this.writeLine(
                    "Policy Filtered-Copyright Count: " + "" + oatReportCopyrightInfo.getFilteredRuleCount());

                this.writeLine("");
                this.writeLine("Project License Count: ", "" + oatReportLicenseInfo.getLicenseTypeCount());
                this.writeLine("Project Normal License Count: ", "" + oatReportLicenseInfo.getNormalLicenseTypeCount());
                this.writeLine("Project Abnormal License Count: ",
                    "" + oatReportLicenseInfo.getAbnormalLicenseTypeCount());
                this.writeLine("Project Compatible License Count: ",
                    "" + oatReportLicenseInfo.getCompatibleLicenseTypeCount());
                this.writeLine("Project Not Compatible License Count: ",
                    "" + oatReportLicenseInfo.getNotCompatibleLicenseTypeCount());
                this.writeLine("Project Copyright Count: ", "" + oatReportCopyrightInfo.getCopyrightCount());
                this.writeLine("Project Normal Copyright Count: ",
                    "" + oatReportCopyrightInfo.getNormalCopyrightCount());
                this.writeLine("Project Abnormal Copyright Count: ",
                    "" + oatReportCopyrightInfo.getAbnormalCopyrightCount());

                this.writeLine("");
                this.writeLine("Policy Not Passed-FileType Count: ",
                    "" + oatReportFileTypeInfo.getInvalidTypeFileCount());
                this.writeLine("Policy Not Passed-Compatibility Count: ",
                    "" + oatReportLicenseInfo.getNotCompatibleLicenseFileCount());
                this.writeLine("Policy Not Passed-WrongLicenseHeader Count: ",
                    "" + oatReportLicenseInfo.getAbnormalLicenseHeaderFileCount());
                this.writeLine("Policy Not Passed-NoLicenseHeader Count: ",
                    "" + oatReportLicenseInfo.getNoLicenseHeaderFileCount());
                this.writeLine("Policy Not Passed-WrongCopyright Count: ",
                    "" + oatReportCopyrightInfo.getAbnormalCopyrightHeaderFileCount());
                this.writeLine("Policy Not Passed-NoCopyright Count: ",
                    "" + oatReportCopyrightInfo.getNoCopyrightHeaderFileCount());
                this.writeLine("Project License File: ", (oatReportFileNameInfo.isHasLicenseFile() ? "TRUE" : "FALSE"));
                this.writeLine("Project README: ", (oatReportFileNameInfo.isHasReadmeFile() ? "TRUE" : "FALSE"));
                this.writeLine("Project README.OpenSource: ",
                    (oatReportFileNameInfo.isHasReadmeOpenSourceFile() ? "TRUE" : "FALSE"));
                this.writeLine("");

                this.writeLine("========Project Filtered File Information========");
                this.writeLine("");
                final List<OatReportFile> projectFilteredFileList = oatReportFileInfo.getProjectFilteredFileList();
                this.writeFileList(projectFilteredFileList);

                final List<OatReportFile> projectFilteredByHeaderFileList
                    = oatReportFileInfo.getProjectFilteredByHeaderFileList();
                this.writeFileList(projectFilteredByHeaderFileList);

                this.writeLine("========Project Filtered Policy Information========");
                this.writeLine("");

                final List<IOatDocument.FilteredRule> filteredRuleList = oatReportFileNameInfo.getFilteredRules();
                for (final IOatDocument.FilteredRule filteredRule : filteredRuleList) {
                    this.writeLine("Policy Filtered-FileName-" + filteredRule.getPolicyName(), "",
                        filteredRule.getFilePath(), filteredRule.getFilterItem(), filteredRule.getDesc());
                }
                this.writeEmptyLine(filteredRuleList.size());

                final List<IOatDocument.FilteredRule> fileTypeFilteredRuleList
                    = oatReportFileTypeInfo.getFilteredRules();
                for (final IOatDocument.FilteredRule filteredRule : fileTypeFilteredRuleList) {
                    this.writeLine("Policy Filtered-FileType-" + filteredRule.getPolicyName(),
                        filteredRule.getOatDocument().getData("FileType"), filteredRule.getFilePath(),
                        filteredRule.getFilterItem(), filteredRule.getDesc());
                }
                this.writeEmptyLine(fileTypeFilteredRuleList.size());

                final List<IOatDocument.FilteredRule> licenseFilteredRuleList
                    = oatReportLicenseInfo.getLicenseFilteredRules();
                for (final IOatDocument.FilteredRule filteredRule : licenseFilteredRuleList) {
                    this.writeLine("Policy Filtered-LicenseHeader",
                        filteredRule.getOatDocument().getData("LicenseName"), filteredRule.getFilePath(),
                        filteredRule.getFilterItem(), filteredRule.getDesc());
                }
                this.writeEmptyLine(licenseFilteredRuleList.size());

                final List<IOatDocument.FilteredRule> compatibleFilteredRuleList
                    = oatReportLicenseInfo.getCompatibleFilteredRules();
                for (final IOatDocument.FilteredRule filteredRule : compatibleFilteredRuleList) {
                    this.writeLine("Policy Filtered-Compatibility",
                        filteredRule.getOatDocument().getData("LicenseName"), filteredRule.getFilePath(),
                        filteredRule.getFilterItem(), filteredRule.getDesc());
                }
                this.writeEmptyLine(compatibleFilteredRuleList.size());

                final List<IOatDocument.FilteredRule> copyrightFilteredRuleList
                    = oatReportCopyrightInfo.getFilteredRules();
                for (final IOatDocument.FilteredRule filteredRule : copyrightFilteredRuleList) {
                    this.writeLine("Policy Filtered-Copyright", filteredRule.getOatDocument().getData("CopyrightOwner"),
                        filteredRule.getFilePath(), filteredRule.getFilterItem(), filteredRule.getDesc());
                }
                this.writeEmptyLine(copyrightFilteredRuleList.size());

                this.writeLine("========Project License Information========");
                this.writeLine("");

                final List<OatReportLicense> oatReportNormalLicenseList
                    = oatReportLicenseInfo.getNormalLicenseTypeList();
                for (final OatReportLicense oatReportLicense : oatReportNormalLicenseList) {
                    this.writeLine("Project Normal License", oatReportLicense.getLicenseId());
                }
                this.writeEmptyLine(oatReportNormalLicenseList.size());

                final List<OatReportLicense> oatReportAbnormalLicenseList
                    = oatReportLicenseInfo.getAbnormalLicenseTypeList();
                for (final OatReportLicense oatReportLicense : oatReportAbnormalLicenseList) {
                    this.writeLine("Project Abnormal License", oatReportLicense.getLicenseId());
                }
                this.writeEmptyLine(oatReportAbnormalLicenseList.size());

                final List<OatReportLicense> oatNotCompatibleLicenseList
                    = oatReportLicenseInfo.getNotCompatibleLicenseTypeList();
                for (final OatReportLicense oatReportLicense : oatNotCompatibleLicenseList) {
                    this.writeLine("Project Not Compatible License", oatReportLicense.getLicenseId());
                }
                this.writeEmptyLine(oatNotCompatibleLicenseList.size());

                final List<OatReportLicense> oatcompatibleLicenseList
                    = oatReportLicenseInfo.getCompatibleLicenseTypeList();
                for (final OatReportLicense oatReportLicense : oatcompatibleLicenseList) {
                    this.writeLine("Project Compatible License", oatReportLicense.getLicenseId());
                }
                this.writeEmptyLine(oatcompatibleLicenseList.size());

                this.writeLine("========Project Copyright Information========");
                this.writeLine("");
                for (final String copyright : oatReportCopyrightInfo.getNormalCopyrightList()) {
                    this.writeLine("Project Normal Copyright", copyright);
                }
                this.writeEmptyLine(oatReportCopyrightInfo.getNormalCopyrightList().size());

                for (final String copyright : oatReportCopyrightInfo.getAbnormalCopyrightList()) {
                    this.writeLine("Project Abnormal Copyright", copyright);
                }
                this.writeEmptyLine(oatReportCopyrightInfo.getAbnormalCopyrightList().size());

                this.writeLine("");

                this.writeLine("XXXXXXXXX Policy Not Passed Begin XXXXXXXXX");
                this.writeLine("");
                final List<OatReportFile> invalidTypeFileList = oatReportFileTypeInfo.getInvalidTypeFileList();
                this.writeFileList(invalidTypeFileList);

                final List<OatReportFile> notCompatibleLicenseFileList
                    = oatReportLicenseInfo.getNotCompatibleLicenseFileList();
                this.writeFileList(notCompatibleLicenseFileList);
                final List<OatReportFile> abnormalLicenseHeaderFile
                    = oatReportLicenseInfo.getAbnormalLicenseHeaderFileList();
                this.writeFileList(abnormalLicenseHeaderFile);
                final List<OatReportFile> noLicenseHeaderFileList = oatReportLicenseInfo.getNoLicenseHeaderFileList();
                this.writeFileList(noLicenseHeaderFileList);
                final List<OatReportFile> abnormalCopyrightFileList
                    = oatReportCopyrightInfo.getAbnormalCopyrightHeaderFileList();
                this.writeFileList(abnormalCopyrightFileList);
                final List<OatReportFile> noCopyrightFileList = oatReportCopyrightInfo.getNoCopyrightHeaderFileList();
                this.writeFileList(noCopyrightFileList);

                this.writeLine("XXXXXXXXX Policy Not Passed End   XXXXXXXXX");

                this.writeLine("");
                this.writeLine("========Project OAT.xml========");
                this.writeLine("Project Config: \t");
                this.writeLine(oatReportConfigInfo.getProjectPolicy());

                this.writeLine("");
                this.writeLine("Project End " + index + "/" + oatReportInfoList.size() + ": \t");
                index++;
            }

            this.writer.flush();
            this.writer.close();
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
        OatLogUtil.println("", "Result file path:\t" + this.resultFile);
    }

    private void writeEmptyLine(final int filteredRuleList) throws IOException {
        if (filteredRuleList > 0) {
            this.writeLine("");
        }
    }

    private void writeFileList(final List<OatReportFile> invalidTypeFileList) throws IOException {
        for (final OatReportFile oatReportFile : invalidTypeFileList) {
            this.writeLine(oatReportFile.getTitle(), oatReportFile.getContent(), oatReportFile.getFilePath(),
                oatReportFile.getRule(), oatReportFile.getDesc());
        }
        if (invalidTypeFileList.size() > 0) {
            this.writeLine("");
        }
    }

    private void writeLine(final String desc) throws IOException {
        this.writer.write(desc + "\n");
    }

    private void writeLine(final String... desc) throws IOException {
        String tmp = "";
        for (final String s : desc) {
            tmp = tmp + s + "\t";
        }
        this.writer.write(tmp + "\n");
    }

}
