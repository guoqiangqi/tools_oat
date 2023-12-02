/*
 * Copyright (c) 2023 Huawei Device Co., Ltd.
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

import ohos.oat.analysis.matcher.AbstractOatLicenseMatcher;
import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ license text matcher
 *
 * @author chenyaxun
 * @since 1.0
 */
public class AtLicenseMatcher extends AbstractOatLicenseMatcher {
    private final Pattern pattern = IOatMatcher.compilePattern("(@license(.*))");

    private int line;

    public AtLicenseMatcher() {
    }

    @Override
    public boolean match(final IOatDocument pSubject, final String licenseTxt) {
        final String licenseName = pSubject.getData("LicenseName");
        if (licenseName != null && licenseName.length() != 0 && !licenseName.equals("InvalidLicense")) {
            return true;
        }
        this.line++;
        if (this.line > 50) {
            return true;
        }

        String licensHeaderText = licenseTxt;
        final int index = licensHeaderText.indexOf("@license");

        if (index < 0) {
            return false;
        }

        try {
            licensHeaderText = licensHeaderText.substring(index);
            licensHeaderText = licensHeaderText.replace("*", "");
            licensHeaderText = licensHeaderText.replace("/", "");
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
            return false;
        }

        final Matcher matcher = this.pattern.matcher(licensHeaderText);
        if (matcher.find()) {
            if (matcher.groupCount() >= 2) {
                final String cp = matcher.group(2).trim().replace(":","");
                final String familyCategory = this.getLicenseFamilyCategory();
                final String familyName = this.getLicenseFamilyName();
                this.setLicenseFamilyCategory((familyCategory == null ? cp : familyCategory));
                this.setLicenseFamilyName((familyName == null ? cp : familyName));
                this.reportLicense(pSubject);
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void reset() {
        // Nothing to do
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }
}
