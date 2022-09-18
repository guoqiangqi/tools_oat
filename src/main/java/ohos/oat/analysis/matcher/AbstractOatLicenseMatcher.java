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

package ohos.oat.analysis.matcher;

import ohos.oat.document.IOatDocument;

/**
 * Default IOatHeaderMatcher method implementations
 *
 * @author chenyaxun
 * @since 2.0
 */
public abstract class AbstractOatLicenseMatcher implements IOatMatcher {
    private String licenseFamilyCategory;

    private String licenseFamilyName;

    private String notes;

    public AbstractOatLicenseMatcher() {

    }

    public AbstractOatLicenseMatcher(final String licenseFamilyCategory, final String licenseFamilyName,
        final String notes) {
        this.setLicenseFamilyCategory(licenseFamilyCategory);
        this.setLicenseFamilyName(licenseFamilyName);
        this.setNotes(notes);

    }

    protected String getLicenseFamilyCategory() {
        return this.licenseFamilyCategory;
    }

    protected void setLicenseFamilyCategory(final String pDocumentCategory) {
        this.licenseFamilyCategory = pDocumentCategory;
    }

    protected String getLicenseFamilyName() {
        return this.licenseFamilyName;
    }

    protected void setLicenseFamilyName(final String pLicenseFamilyCategory) {
        this.licenseFamilyName = pLicenseFamilyCategory;
    }

    protected String getNotes() {
        return this.notes;
    }

    protected void setNotes(final String pNotes) {
        this.notes = pNotes;
    }

    protected void reportLicense(final IOatDocument subject) {
        subject.putData("LicenseHeaderText", this.getNotes());
        subject.putData("LicenseCategory", this.getLicenseFamilyCategory());
        subject.putData("LicenseName", this.getLicenseFamilyName());

    }

}
