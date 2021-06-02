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

package ohos.oat.analysis.headermatcher.fulltext;

import ohos.oat.analysis.headermatcher.OhosMatchUtils;

import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.analysis.license.FullTextMatchingLicense;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosFullTextMatchingLicense extends FullTextMatchingLicense {
    protected OhosFullTextMatchingLicense(final MetaData.Datum licenseFamilyCategory,
        final MetaData.Datum licenseFamilyName, final String notes, final String fullText) {
        super(licenseFamilyCategory, licenseFamilyName, notes, fullText);
    }

    @Override
    public boolean match(final Document subject, final String line) throws RatHeaderAnalysisException {
        final String licenseName = subject.getMetaData().value(MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY);
        if (OhosMatchUtils.stopWhileMatchedSpdx(licenseName)) {
            return false;
        }

        if (this.stopWhileMatched(licenseName)) {
            return false;
        }

        return super.match(subject, line);
    }

    protected boolean stopWhileMatched(final String licenseName) {
        return OhosMatchUtils.stopWhileMatchedGPL(licenseName);
    }
}
