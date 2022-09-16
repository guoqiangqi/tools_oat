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
 * 2021.4 - Override method matches to ignore case during matching
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher.simplepattern;

import ohos.oat.analysis.headermatcher.OatMatchUtils;
import ohos.oat.analysis.headermatcher.OatSimplePatternLicenseMatcher;
import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLicenseTextUtil;

import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatDefaultSimplePatternLicenseMatcher extends OatSimplePatternLicenseMatcher {

    protected OatDefaultSimplePatternLicenseMatcher(final MetaData.Datum pLicenseFamilyCategory,
        final MetaData.Datum pLicenseFamilyName, final String pNotes, final String[] pPatterns) {
        super(pLicenseFamilyCategory, pLicenseFamilyName, pNotes, OatLicenseTextUtil.cleanAndLowerCaseArray(pPatterns));
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean match(final IOatDocument subject, final String line) {
        final String licenseName = subject.getMetaData().value(MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY);
        if (OatMatchUtils.needMatchAgain(licenseName, this.getLicenseFamilyName())) {
            // copyleft need match again
            if (this.stopWhileMatched(licenseName)) {
                return true;
            }
            return super.match(subject, line);
        }
        return true;
    }

    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
