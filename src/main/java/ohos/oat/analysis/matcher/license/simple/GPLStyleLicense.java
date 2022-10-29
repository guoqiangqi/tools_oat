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
 */

package ohos.oat.analysis.matcher.license.simple;

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.analysis.matcher.OatSimplePatternLicenseMatcher;
import ohos.oat.document.IOatDocument;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class GPLStyleLicense extends OatSimplePatternLicenseMatcher {
    public GPLStyleLicense() {
        super("GPLStyleLicense", "GPLStyleLicense", "", new String[] {
            " GPL ", " GPL,", " GPL.", " GPLV", " GNU General Public License", " GNU General Public",
            "GNU Public License", "(GPL)"
        });
    }

    @Override
    public boolean match(final IOatDocument pSubject, final String pLine) {
        final String licenseName = pSubject.getData("LicenseName");
        if (IOatMatcher.needMatchAgain(licenseName, this.getLicenseFamilyName())) {
            return super.match(pSubject, pLine);
        }
        return true;
    }

    @Override
    protected void reportLicense(final IOatDocument subject) {
        IOatMatcher.reportGPL(subject, this.getLicenseFamilyName());
    }
}
