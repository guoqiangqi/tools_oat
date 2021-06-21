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
 * 2021.1 -  Excract from OhosLicenseMain class to match spdx license text.
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

import ohos.oat.utils.OatLogUtil;

import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.analysis.license.BaseLicense;
import org.apache.rat.api.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SPDX license text matcher
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatSpdxLabelLicenseMatcher extends BaseLicense implements IOatHeaderMatcher {
    private final Pattern pattern = Pattern.compile("(SPDX-License-Identifier:(.*))", Pattern.CASE_INSENSITIVE);

    private int line;

    public OatSpdxLabelLicenseMatcher() {
    }

    @Override
    public boolean match(final Document pSubject, final String licenseTxt) throws RatHeaderAnalysisException {
        this.line++;
        if (this.line > 50) {
            return true;
        }

        String licensHeaderText = licenseTxt;
        final int index = licensHeaderText.indexOf("SPDX-License-Identifier:");

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
            String cp = licensHeaderText;
            if (matcher.groupCount() >= 2) {
                cp = matcher.group(2).trim();
                final String familyCategory = this.getLicenseFamilyCategory();
                final String familyName = this.getLicenseFamilyName();
                this.setLicenseFamilyCategory((familyCategory == null ? "" : familyCategory) + " SPDX:" + cp);
                this.setLicenseFamilyName((familyName == null ? "" : familyName) + " SPDX:" + cp);
                this.reportOnLicense(pSubject);
                return false; // 继续匹配，是否有多行定义SPDX？
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
