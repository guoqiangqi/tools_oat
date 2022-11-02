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
 * 2021.6 - Add this class to support match all spdx license texts
 * 2021.6 - Change super class FullTextMatchingLicense to OhosFullTextLicenseMatcher to support multi-line match
 * first line
 * Modified by jalenchen
 */

package ohos.oat.analysis.matcher.license.spdx;

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.analysis.matcher.OatFullTextLicenseMatcher;
import ohos.oat.document.IOatDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * Header matcher class to match spdx license texts
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatSpdxTextLicenseMatcher extends OatFullTextLicenseMatcher {
    private final List<String> urls = new ArrayList<>();

    public OatSpdxTextLicenseMatcher(final String licenseName, final String licenseText, final List<String> urls) {
        super(licenseName, licenseName, "", licenseText);
        if (urls != null) {
            this.urls.addAll(urls);
        }
    }

    @Override
    public boolean match(final IOatDocument subject, final String line) {
        final String licenseName = subject.getData("LicenseName");
        // Don't match again if doc is matched,because the longest license is matched first.
        if (IOatMatcher.needMatchAgain(licenseName, this.getLicenseFamilyName())) {
            final boolean result = super.match(subject, line);
            if (!result) {
                for (final String url : this.urls) {
                    if (line.contains(url)) {
                        this.reportLicense(subject);
                        return true;
                    }
                }
            }
            return result;
        } else {
            return true;
        }
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName() + this.getLicenseFamilyName();
    }
}
