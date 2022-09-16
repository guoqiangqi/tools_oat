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

package ohos.oat.analysis.headermatcher.simplepattern;

import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY;
import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_NAME;

import ohos.oat.analysis.headermatcher.OatMatchUtils;
import ohos.oat.analysis.headermatcher.OatSimplePatternLicenseMatcher;
import ohos.oat.document.IOatDocument;

import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class LGPLStyleLicense extends OatSimplePatternLicenseMatcher {
    public LGPLStyleLicense() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "LGPLStyleLicense"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "LGPLStyleLicense"), "", new String[] {
                " LGPL ", " LGPL,", " LGPL.", " LGPLV", "GNU Lesser General Public License", "GNU Lesser General",
                "GNU Library General Public License", "GNU Library General Public", "GNU Library General"
            });
    }

    @Override
    public boolean match(final IOatDocument pSubject, final String pLine) {
        final String licenseName = pSubject.getMetaData().value(MetaData.RAT_URL_LICENSE_FAMILY_NAME);
        if (OatMatchUtils.needMatchAgain(licenseName, this.getLicenseFamilyName())) {
            return super.match(pSubject, pLine);
        }
        return true;
    }

    @Override
    protected void reportLicense(final IOatDocument subject) {
        OatMatchUtils.reportGPL(subject, this.getLicenseFamilyName());
    }

}
