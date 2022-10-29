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
 * 2021.4 - Override method match, only check if the text is contained in source file
 * Modified by jalenchen
 */

package ohos.oat.analysis.matcher.license.full;

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.analysis.matcher.OatFullTextLicenseMatcher;
import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLicenseTextUtil;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatDefaultFullTextLicenseMatcher extends OatFullTextLicenseMatcher {
    protected OatDefaultFullTextLicenseMatcher(final String licenseFamilyCategory, final String licenseFamilyName,
        final String notes, final String fullText) {
        super(licenseFamilyCategory, licenseFamilyName, notes, OatLicenseTextUtil.cleanAndLowerCaseLetter(fullText));
    }

    @Override
    public boolean match(final IOatDocument subject, final String line) {
        final String licenseName = subject.getData("LicenseName");
        if (IOatMatcher.needMatchAgain(licenseName, this.getLicenseFamilyName())) {
            // copyleft need match again
            if (this.stopWhileMatched(licenseName)) {
                return true;
            }
            return super.match(subject, line);
        }
        return true;
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }

    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
