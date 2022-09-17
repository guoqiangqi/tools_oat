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

import ohos.oat.document.IOatDocument;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class DerivedLicense extends OatDefaultSimplePatternLicenseMatcher {
    public DerivedLicense() {
        super("DerivedLicense", "DerivedLicense", "", new String[] {
            "is derived from the following"
        });
    }

    @Override
    public boolean match(final IOatDocument pSubject, final String pLine) {
        final String value = pSubject.getData("LicenseName");
        if (value != null && value.trim().length() > 0 && (!value.equals("InvalidLicense"))) {
            // final String licenseName = value + "DerivedLicense";
            // this.setLicenseFamilyCategory(licenseName);
            // this.setLicenseFamilyName(licenseName);
            final boolean matched = super.match(pSubject, pLine);
            return matched;
        } else {
            return true;
        }
    }

    // @Override
    // protected boolean stopWhileMatched(final String licenseName) {
    //     return false;
    // }
}
