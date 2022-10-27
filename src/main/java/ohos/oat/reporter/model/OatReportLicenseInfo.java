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

import ohos.oat.document.IOatDocument;
import ohos.oat.reporter.model.file.OatReportFile;
import ohos.oat.reporter.model.license.OatReportLicense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportLicenseInfo {

    private final List<IOatDocument.FilteredRule> licenseFilteredRules = new ArrayList<>();

    private final List<IOatDocument.FilteredRule> compatibleFilteredRules = new ArrayList<>();

    private final List<OatReportLicense> licenseTypeList = new ArrayList<>();

    private final List<OatReportLicense> normalLicenseTypeList = new ArrayList<>();

    private final List<OatReportLicense> abnormalLicenseTypeList = new ArrayList<>();

    private final List<OatReportLicense> notCompatibleLicenseTypeList = new ArrayList<>();

    private final Map<String, List<OatReportFile>> licenseId2FileList = new HashMap<>();

    private final Map<String, String> license2Flag = new HashMap<>();

    private final List<OatReportFile> noLicenseHeaderFileList = new ArrayList<>();

    private final List<OatReportFile> abnormalLicenseHeaderFileList = new ArrayList<>();

    private final List<OatReportFile> notCompatibleLicenseTypeFileList = new ArrayList<>();

    private final int hasLicenseHeaderFileCount = 0;

    private final int normalLicenseHeaderFileCount = 0;

    private int licenseFilteredRuleCount = 0;

    private int compatibleFilteredRuleCount = 0;

    private int licenseTypeCount = 0;

    private int normalLicenseTypeCount = 0;

    private int abnormalLicenseTypeCount = 0;

    private int notCompatibleLicenseTypeCount = 0;

    private int noLicenseHeaderFileCount = 0;

    private int abnormalLicenseHeaderFileCount = 0;

    private int notCompatibleLicenseTypeFileCount = 0;

    public List<OatReportLicense> getNotCompatibleLicenseTypeList() {
        return this.notCompatibleLicenseTypeList;
    }

    public int getNotCompatibleLicenseTypeCount() {
        return this.notCompatibleLicenseTypeCount;
    }

    public void addNotCompatibleLicenseType(final OatReportLicense oatReportLicense) {
        if (this.license2Flag.put(oatReportLicense.getLicenseId(), "true") != null) {
            return;
        }
        this.notCompatibleLicenseTypeList.add(oatReportLicense);
        this.notCompatibleLicenseTypeCount++;
    }

    public List<OatReportFile> getNotCompatibleLicenseTypeFileList() {
        return this.notCompatibleLicenseTypeFileList;
    }

    public void addNotCompatibleLicenseTypeFile(final OatReportFile oatReportFile) {
        this.notCompatibleLicenseTypeFileList.add(oatReportFile);
        this.notCompatibleLicenseTypeFileCount++;
    }
    // private int multiLicenseHeaderFileCount = 0;

    public int getLicenseTypeCount() {
        return this.licenseTypeCount;
    }

    public int getNormalLicenseTypeCount() {
        return this.normalLicenseTypeCount;
    }

    public int getAbnormalLicenseTypeCount() {
        return this.abnormalLicenseTypeCount;
    }

    public int getNotCompatibleLicenseTypeFileCount() {
        return this.notCompatibleLicenseTypeFileCount;
    }

    public List<OatReportLicense> getLicenseTypeList() {
        return this.licenseTypeList;
    }

    public void addLicenseType(final OatReportLicense oatReportLicense) {
        if (this.license2Flag.put(oatReportLicense.getLicenseId(), "true") != null) {
            return;
        }
        this.licenseTypeList.add(oatReportLicense);
        this.licenseTypeCount++;
    }

    public List<OatReportLicense> getNormalLicenseTypeList() {
        return this.normalLicenseTypeList;
    }

    public void addNormalLicenseType(final OatReportLicense oatReportLicense) {
        if (this.license2Flag.put(oatReportLicense.getLicenseId(), "true") != null) {
            return;
        }
        this.normalLicenseTypeList.add(oatReportLicense);
        this.normalLicenseTypeCount++;
        this.licenseTypeCount++;
    }

    public List<OatReportLicense> getAbnormalLicenseTypeList() {
        return this.abnormalLicenseTypeList;
    }

    public void addAbnormalLicenseType(final OatReportLicense oatReportLicense) {
        if (this.license2Flag.put(oatReportLicense.getLicenseId(), "true") != null) {
            return;
        }
        this.abnormalLicenseTypeList.add(oatReportLicense);
        this.abnormalLicenseTypeCount++;
        this.licenseTypeCount++;
    }

    public Map<String, List<OatReportFile>> getLicenseId2FileList() {
        return this.licenseId2FileList;
    }

    public void addLicenseId2File(final String licenseId, final OatReportFile file) {
        List filelist = this.licenseId2FileList.get(licenseId);
        if (null == filelist) {
            filelist = new ArrayList();
            this.licenseId2FileList.put(licenseId, filelist);
        }
        filelist.add(file);
    }

    public List<OatReportFile> getNoLicenseHeaderFileList() {
        return this.noLicenseHeaderFileList;
    }

    public void addNoLicenseHeaderFile(final OatReportFile file) {
        this.noLicenseHeaderFileList.add(file);
        this.noLicenseHeaderFileCount++;
    }

    public int getHasLicenseHeaderFileCount() {
        return this.hasLicenseHeaderFileCount;
    }

    public int getNoLicenseHeaderFileCount() {
        return this.noLicenseHeaderFileCount;
    }

    public int getNormalLicenseHeaderFileCount() {
        return this.normalLicenseHeaderFileCount;
    }

    public int getAbnormalLicenseHeaderFileCount() {
        return this.abnormalLicenseHeaderFileCount;
    }

    public List<OatReportFile> getAbnormalLicenseHeaderFileList() {
        return this.abnormalLicenseHeaderFileList;
    }

    public void addAbnormalLicenseHeaderFile(final OatReportFile file) {
        this.abnormalLicenseHeaderFileList.add(file);
        this.abnormalLicenseHeaderFileCount++;
    }

    public int getLicenseFilteredRuleCount() {
        return this.licenseFilteredRuleCount;
    }

    public List<IOatDocument.FilteredRule> getLicenseFilteredRules() {
        return this.licenseFilteredRules;
    }

    public void addLicenseFilteredRule(final IOatDocument.FilteredRule licenseFilteredRule) {
        if (licenseFilteredRule.getPolicyType().equals("license")) {
            this.licenseFilteredRules.add(licenseFilteredRule);
            this.licenseFilteredRuleCount++;
        }
    }

    public int getCompatibleFilteredRuleCount() {
        return this.compatibleFilteredRuleCount;
    }

    public List<IOatDocument.FilteredRule> getCompatibleFilteredRules() {
        return this.compatibleFilteredRules;
    }

    public void addCompatibleFilteredRule(final IOatDocument.FilteredRule compatibleFilteredRule) {
        if (compatibleFilteredRule.getPolicyType().equals("compatibility")) {
            this.compatibleFilteredRules.add(compatibleFilteredRule);
            this.compatibleFilteredRuleCount++;
        }
    }
}
