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

package ohos.oat.reporter.model.license;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportLicense {

    private String licenseName = "";

    private String licenseId = "";

    private String licenseText = "";

    private String licenseDesc = "";

    private List<OatReportLicensePermission> permissions = new ArrayList<>();

    private List<OatReportLicenseCondition> conditions = new ArrayList<>();

    private List<OatReportLicenseLimitation> limitations = new ArrayList<>();

    private List<OatReportLicenseApprovedType> approvedTypes = new ArrayList<>();

    public String getLicenseName() {
        return this.licenseName;
    }

    public void setLicenseName(final String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseId() {
        return this.licenseId;
    }

    public void setLicenseId(final String licenseId) {
        this.licenseId = licenseId;
    }

    public String getLicenseText() {
        return this.licenseText;
    }

    public void setLicenseText(final String licenseText) {
        this.licenseText = licenseText;
    }

    public String getLicenseDesc() {
        return this.licenseDesc;
    }

    public void setLicenseDesc(final String licenseDesc) {
        this.licenseDesc = licenseDesc;
    }

    public List<OatReportLicensePermission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(final List<OatReportLicensePermission> permissions) {
        this.permissions = permissions;
    }

    public List<OatReportLicenseCondition> getConditions() {
        return this.conditions;
    }

    public void setConditions(final List<OatReportLicenseCondition> conditions) {
        this.conditions = conditions;
    }

    public List<OatReportLicenseLimitation> getLimitations() {
        return this.limitations;
    }

    public void setLimitations(final List<OatReportLicenseLimitation> limitations) {
        this.limitations = limitations;
    }

    public List<OatReportLicenseApprovedType> getApprovedTypes() {
        return this.approvedTypes;
    }

    public void setApprovedTypes(final List<OatReportLicenseApprovedType> approvedTypes) {
        this.approvedTypes = approvedTypes;
    }

}
