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
 * 2021.6 -  Derived from Apache Creadur Rat FullTextMatchingLicense to support multi-line match first line
 * Modified by jalenchen
 */

package ohos.oat.analysis.matcher;

import ohos.oat.document.IOatDocument;

/**
 * Base full text license matcher class to support multi-line match first line
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatFullTextLicenseMatcher extends AbstractOatLicenseMatcher {

    private final String allLicenseHeaderText;

    private final String firstLicenseHeaderText;

    private boolean firstLineMatched = false;

    private final StringBuilder stringBuilder = new StringBuilder();

    private String preLine = "";

    public OatFullTextLicenseMatcher(final String licenseFamilyCategory, final String licenseFamilyName,
        final String notes, final String licenseText) {
        super(licenseFamilyCategory, licenseFamilyName, notes);
        int offset = licenseText.indexOf(10);
        if (offset == -1) {
            offset = Math.min(20, licenseText.length());
        }
        this.firstLicenseHeaderText = licenseText.substring(0, offset);
        this.allLicenseHeaderText = licenseText;
    }

    @Override
    public boolean match(final IOatDocument subject, final String line) {
        final String inputToMatch = line;
        int offset;
        if (this.firstLineMatched) {
            this.stringBuilder.append(inputToMatch);
        } else {
            offset = (inputToMatch).indexOf(this.firstLicenseHeaderText);
            if (offset < 0) {
                final String tmp = this.preLine + inputToMatch;
                final int offset0 = tmp.indexOf(this.firstLicenseHeaderText);
                if (offset0 < 0) {
                    this.preLine = inputToMatch;
                    return false;
                } else {
                    this.stringBuilder.append(tmp.substring(offset0));
                    this.firstLineMatched = true;
                }
            } else {
                this.stringBuilder.append(inputToMatch.substring(offset));
                this.firstLineMatched = true;
            }
        }

        if (this.stringBuilder.length() >= this.allLicenseHeaderText.length()) {
            if (this.stringBuilder.toString().contains(this.allLicenseHeaderText)) {
                this.reportLicense(subject);
                return true;
            }

            offset = this.stringBuilder.substring(1).indexOf(this.firstLicenseHeaderText);
            if (offset >= 0) {
                this.stringBuilder.delete(0, offset);
            } else {
                this.reset();
            }
        }

        return false;
    }

    @Override
    public void reset() {
        this.stringBuilder.setLength(0);
        this.firstLineMatched = false;
        this.preLine = "";
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }
}
