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
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportCreatorInfo {

    private String reportTool = "OAT";

    private String reportInitCommand = "";

    private String reportToolVersion = "2.0.0-beta.1";

    private String reportUser = "";

    private String reportTime = "";

    private String reportNotes = "Commemorate D";

    public String getReportTool() {
        return this.reportTool;
    }

    public void setReportTool(final String reportTool) {
        this.reportTool = reportTool;
    }

    public String getReportInitCommand() {
        return this.reportInitCommand;
    }

    public void setReportInitCommand(final String reportInitCommand) {
        this.reportInitCommand = reportInitCommand;
    }

    public String getReportToolVersion() {
        return this.reportToolVersion;
    }

    public void setReportToolVersion(final String reportToolVersion) {
        this.reportToolVersion = reportToolVersion;
    }

    public String getReportUser() {
        return this.reportUser;
    }

    public void setReportUser(final String reportUser) {
        this.reportUser = reportUser;
    }

    public String getReportTime() {
        return this.reportTime;
    }

    public void setReportTime(final String reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportNotes() {
        return this.reportNotes;
    }

    public void setReportNotes(final String reportNotes) {
        this.reportNotes = reportNotes;
    }

}
