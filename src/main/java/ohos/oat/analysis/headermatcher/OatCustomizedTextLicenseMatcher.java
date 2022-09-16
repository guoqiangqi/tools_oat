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
 * 2021.5 - Add this class to support user defined license match rules
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY;
import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_NAME;

import ohos.oat.document.IOatDocument;

import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching user customized license text in OAT.xml.
 *
 * @author chenyaxun
 * @since 1.0
 */

public class OatCustomizedTextLicenseMatcher extends OatFullTextLicenseMatcher {
    public OatCustomizedTextLicenseMatcher(final String licenseName, final String licenseText) {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, licenseName),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, licenseName), "", licenseText);
    }

    @Override
    public boolean match(final IOatDocument subject, final String line) {
        final String licenseName = subject.getMetaData().value(MetaData.RAT_URL_LICENSE_FAMILY_NAME);
        if (licenseName == null || licenseName.equals("InvalidLicense")) {
            return super.match(subject, line);
        } else {
            return true;
        }
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName() + this.getLicenseFamilyName();
    }
}
