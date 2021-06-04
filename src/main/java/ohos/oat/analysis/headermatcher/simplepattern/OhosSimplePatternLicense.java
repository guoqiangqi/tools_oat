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

import ohos.oat.analysis.headermatcher.OhosMatchUtils;
import ohos.oat.utils.OhosLicenseTextUtil;

import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.analysis.license.SimplePatternBasedLicense;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosSimplePatternLicense extends SimplePatternBasedLicense {
    private String[] purePatterns;

    protected OhosSimplePatternLicense(final MetaData.Datum pLicenseFamilyCategory,
        final MetaData.Datum pLicenseFamilyName, final String pNotes, final String[] pPatterns) {
        super(pLicenseFamilyCategory, pLicenseFamilyName, pNotes, pPatterns);
        this.purePatterns = new String[pPatterns.length];
        for (int i = 0; i < pPatterns.length; i++) {
            this.purePatterns[i] = OhosLicenseTextUtil.cleanLetter(pPatterns[i]);
        }
    }

    @Override
    protected boolean matches(final String pLine) {
        if (pLine == null) {
            return false;
        }

        final String[] pttrns = this.getPatterns();
        if (pttrns != null) {
            for (final String pttrn : this.purePatterns) {
                if (pLine.contains(pttrn)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean match(final Document pSubject, final String pLine) throws RatHeaderAnalysisException {
        final String licenseName = pSubject.getMetaData().value(MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY);
        if (OhosMatchUtils.stopWhileMatchedSpdx(licenseName)) {
            return false;
        }
        if (this.stopWhileMatched(licenseName)) {
            return false;
        }
        return super.match(pSubject, pLine);
    }

    protected boolean stopWhileMatched(final String licenseName) {
        return OhosMatchUtils.stopWhileMatchedAny(licenseName);
    }
}
