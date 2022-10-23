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

package ohos.oat.reporter.model;

/**
 * OAT detail report data structure, one project corresponds to one OatReportInfo instance
 * summary
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportInfo {

    private OatReportConfigInfo reportConfigInfoInfo = new OatReportConfigInfo();

    private OatReportProjectInfo reportProjectInfo = new OatReportProjectInfo();

    private OatReportFileInfo reportFileInfo = new OatReportFileInfo();

    private OatReportLicenseInfo reportLicenseInfo = new OatReportLicenseInfo();

    private OatReportCopyrightInfo reportCopyrightInfo = new OatReportCopyrightInfo();

    public OatReportConfigInfo getReportConfigInfoInfo() {
        return this.reportConfigInfoInfo;
    }

    public void setReportConfigInfoInfo(final OatReportConfigInfo reportConfigInfoInfo) {
        this.reportConfigInfoInfo = reportConfigInfoInfo;
    }

    public OatReportProjectInfo getReportProjectInfo() {
        return this.reportProjectInfo;
    }

    public void setReportProjectInfo(final OatReportProjectInfo reportProjectInfo) {
        this.reportProjectInfo = reportProjectInfo;
    }

    public OatReportFileInfo getReportFileInfo() {
        return this.reportFileInfo;
    }

    public void setReportFileInfo(final OatReportFileInfo reportFileInfo) {
        this.reportFileInfo = reportFileInfo;
    }

    public OatReportLicenseInfo getReportLicenseInfo() {
        return this.reportLicenseInfo;
    }

    public void setReportLicenseInfo(final OatReportLicenseInfo reportLicenseInfo) {
        this.reportLicenseInfo = reportLicenseInfo;
    }

    public OatReportCopyrightInfo getReportCopyrightInfo() {
        return this.reportCopyrightInfo;
    }

    public void setReportCopyrightInfo(final OatReportCopyrightInfo reportCopyrightInfo) {
        this.reportCopyrightInfo = reportCopyrightInfo;
    }

}
