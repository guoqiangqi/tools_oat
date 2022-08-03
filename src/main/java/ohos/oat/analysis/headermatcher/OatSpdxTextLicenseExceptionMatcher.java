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
 * 2021.6 - Add this class to support match all spdx license exception texts
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Header matcher class to match spdx license exception texts
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatSpdxTextLicenseExceptionMatcher extends OatFullTextLicenseMatcher {
    private final List<String> urls = new ArrayList<>();

    public OatSpdxTextLicenseExceptionMatcher(final String licenseName, final String licenseText,
        final List<String> urls) {
        super(licenseName, licenseName, "", licenseText);
        if (urls != null) {
            this.urls.addAll(urls);
        }
    }

    @Override
    public boolean match(final Document subject, final String line) throws RatHeaderAnalysisException {
//        final String licenseName = subject.getMetaData().value(MetaData.RAT_URL_LICENSE_FAMILY_NAME);
        final boolean matchResult = super.match(subject, line);
        if (!matchResult) {
            for (final String url : this.urls) {
                if (line.contains(url)) {
                    this.reportLicense(subject);
                    return true;
                }
            }
        }
        return matchResult;

    }

    @Override
    protected void reportLicense(final Document subject) {
        final MetaData metaData = subject.getMetaData();
        final String licenseName = metaData.value(MetaData.RAT_URL_LICENSE_FAMILY_NAME);
        final String tmpStr = licenseName == null ? "" : licenseName;
        final String newName = tmpStr + "-with-" + this.getLicenseFamilyName();
        if (tmpStr.toLowerCase(Locale.ENGLISH).contains("with")) {
            return;
        }
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_SAMPLE, ""));
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, newName));
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY, newName));
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_LICENSE_FAMILY_NAME, newName));
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName() + this.getLicenseFamilyName();
    }

}
