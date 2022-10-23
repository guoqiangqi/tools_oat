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
import ohos.oat.reporter.model.OatReportInfo;
import ohos.oat.reporter.model.OatReportSummaryInfo;
import ohos.oat.reporter.model.file.OatReportFile;
import ohos.oat.reporter.model.license.OatReportLicense;
import ohos.oat.utils.OatLogUtil;

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

    private final String reportFileName = "DetailPlainReport_";

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
        this.resultFile = new File(reportFolder + "/" + this.reportFileName + filePrefix + ".txt");

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
        this.oatReportSummaryInfo.getReportCreatorInfo().setReportNotes("");
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
        final OatReportFile oatReportFile = new OatReportFile();
        oatReportFile.setFileName(oatFileDocument.getFileName());
        oatReportFile.setFilePath(oatFileDocument.getFile().getPath());

        if (oatFileDocument.getStatus().isFileStatusNormal()) {
            oatReportInfo.getReportFileInfo().addProjectNormalFile(oatReportFile);
        } else if (oatFileDocument.getStatus().isFileStatusFiltered()) {
            oatReportInfo.getReportFileInfo().addProjectFilteredFile(oatReportFile);
            if (oatFileDocument.getStatus().isFileStatusFilteredByHeader()) {
                oatReportInfo.getReportFileInfo().addProjectFilteredByHeaderFile(oatReportFile);
            }
        }

        if (oatFileDocument.getData("Result.LicenseFile").equals("false")) {
            oatReportInfo.getReportFileInfo().setHasLicenseFile(false);
        } else {
            oatReportInfo.getReportFileInfo().setHasLicenseFile(true);
        }

        if (oatFileDocument.getData("Result.ReadmeOpenSource").equals("false")) {
            oatReportInfo.getReportFileInfo().setHasReadmeOpenSourceFile(false);
        } else {
            oatReportInfo.getReportFileInfo().setHasReadmeOpenSourceFile(true);
        }

        if (oatFileDocument.getData("Result.Readme").equals("false")) {
            oatReportInfo.getReportFileInfo().setHasReadmeFile(false);
        } else {
            oatReportInfo.getReportFileInfo().setHasReadmeFile(true);
        }
        if (oatFileDocument.getData("Result.FileType").equals("false")) {
            oatReportInfo.getReportFileInfo().addProjectInvalidTypeFile(oatReportFile);
        }

        final String copyrightOwner = oatFileDocument.getData("CopyrightOwner");

        if (oatFileDocument.getData("Result.Copyright").equals("true")) {
            oatReportInfo.getReportCopyrightInfo().addNormalCopyright(copyrightOwner);
            oatReportInfo.getReportCopyrightInfo().addNormalCopyrightHeaderFile(oatReportFile);
        } else {
            if ("NULL".equals(copyrightOwner)) {
                oatReportInfo.getReportCopyrightInfo().addNoCopyrightHeaderFile(oatReportFile);
            } else {
                oatReportInfo.getReportCopyrightInfo().addAbnormalCopyright(copyrightOwner);
                oatReportInfo.getReportCopyrightInfo().addAbnormalCopyrightHeaderFile(oatReportFile);
            }
        }
        oatReportInfo.getReportCopyrightInfo().addCopyright2File(copyrightOwner, oatReportFile);

        final String licenseName = oatFileDocument.getData("LicenseName");
        final OatReportLicense oatReportlicense = new OatReportLicense();
        oatReportlicense.setLicenseId(licenseName);
        if (oatFileDocument.getData("Result.License").equals("true")) {
            oatReportInfo.getReportLicenseInfo().addNormalLicenseType(oatReportlicense);
        } else {
            if ("NULL".equals(licenseName)) {
                oatReportInfo.getReportLicenseInfo().addNoLicenseHeaderFile(oatReportFile);
            } else {
                oatReportInfo.getReportLicenseInfo().addAbnormalLicenseType(oatReportlicense);
                oatReportInfo.getReportLicenseInfo().addAbnormalLicenseHeaderFile(oatReportFile);
            }
        }
        oatReportInfo.getReportLicenseInfo().addLicenseId2File(licenseName, oatReportFile);

        if (oatFileDocument.getData("Result.Compatibility").equals("false")) {
            oatReportInfo.getReportLicenseInfo().addNotCompatibleLicenseType(oatReportlicense);
            oatReportInfo.getReportLicenseInfo().addNotCompatibleLicenseTypeFile(oatReportFile);
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
            this.writeLine("Report User: \t" + this.oatReportSummaryInfo.getReportCreatorInfo().getReportUser());
            this.writeLine("Report Time: \t" + this.oatReportSummaryInfo.getReportCreatorInfo().getReportTime());
            this.writeLine("Report Tool: \t" + this.oatReportSummaryInfo.getReportCreatorInfo().getReportTool());
            this.writeLine(
                "Report Tool Version: \t" + this.oatReportSummaryInfo.getReportCreatorInfo().getReportToolVersion());
            this.writeLine(
                "Report Command: \t" + this.oatReportSummaryInfo.getReportCreatorInfo().getReportInitCommand());

            final List<OatReportInfo> oatReportInfoList = this.oatReportSummaryInfo.getOatReportInfoList();
            this.writeLine("Report Project Count: \t" + oatReportInfoList.size());

            for (final OatReportInfo oatReportInfo : oatReportInfoList) {
                this.writeLine("Report Project Name: \t" + oatReportInfo.getReportProjectInfo().getProjectName());
                this.writeLine(
                    "Report Project Home Page: \t" + oatReportInfo.getReportProjectInfo().getProjectHomePage());
                this.writeLine("Report Project Branch: \t" + oatReportInfo.getReportProjectInfo().getProjectBranch());
                this.writeLine("Report Project Tag: \t" + oatReportInfo.getReportProjectInfo().getProjectTag());
                this.writeLine("Report Project Version: \t" + oatReportInfo.getReportProjectInfo().getProjectVersion());
                this.writeLine(
                    "Report Project Main License: \t" + oatReportInfo.getReportProjectInfo().getMainLicense());
                this.writeLine(
                    "Report Project Config: \t" + oatReportInfo.getReportConfigInfoInfo().getProjectPolicy());
            }

            this.writer.flush();
            this.writer.close();
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
        OatLogUtil.println("", "Result file path:\t" + this.resultFile);
    }

    private void writeLine(final String desc) throws IOException {
        this.writer.write(desc + "\n");
    }

}
