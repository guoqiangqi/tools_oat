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

package ohos.oat.analysis.headermatcher;

import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.analysis.license.BaseLicense;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosSimplePatternLicenseMatcher extends BaseLicense implements IOhosHeaderMatcher {
    private final String[] purePatterns;

    protected OhosSimplePatternLicenseMatcher(final MetaData.Datum pLicenseFamilyCategory,
        final MetaData.Datum pLicenseFamilyName, final String notes, final String[] patterns) {
        super(pLicenseFamilyCategory, pLicenseFamilyName, notes);
        this.purePatterns = patterns;
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }

    protected boolean matches(final String pLine) {
        if (pLine == null) {
            return false;
        }

        if (this.purePatterns != null) {
            for (final String pttrn : this.purePatterns) {
                if (pLine.contains(pttrn)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean match(final Document subject, final String line) throws RatHeaderAnalysisException {
        final boolean result = this.matches(line);
        if (result) {
            this.reportLicense(subject);
        }
        return result;
    }

    protected void reportLicense(final Document subject) {
        super.reportOnLicense(subject);
    }
}
