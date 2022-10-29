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

package ohos.oat.analysis.matcher.license.custom;

import ohos.oat.analysis.matcher.OatFullTextLicenseMatcher;
import ohos.oat.document.IOatDocument;

/**
 * Header matcher class for matching user customized license text in OAT.xml.
 *
 * @author chenyaxun
 * @since 1.0
 */

public class OatCustomizedTextLicenseMatcher extends OatFullTextLicenseMatcher {
    public OatCustomizedTextLicenseMatcher(final String licenseName, final String licenseText) {
        super(licenseName, licenseName, "", licenseText);
    }

    @Override
    public boolean match(final IOatDocument subject, final String line) {
        final String licenseName = subject.getData("LicenseName");
        if (licenseName.length() == 0 || licenseName.equals("InvalidLicense")) {
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
