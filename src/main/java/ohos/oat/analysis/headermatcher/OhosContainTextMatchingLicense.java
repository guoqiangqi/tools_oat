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

package ohos.oat.analysis.headermatcher;

import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.analysis.license.BaseLicense;
import org.apache.rat.analysis.license.FullTextMatchingLicense;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

import java.util.Locale;

/**
 * Header matcher class for "contains" matching logic
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosContainTextMatchingLicense extends FullTextMatchingLicense {
    private final String containText;

    private final StringBuilder stringBuilder = new StringBuilder();

    private String line1 = "";

    private String line2 = "";

    private String line3 = "";

    protected OhosContainTextMatchingLicense(final MetaData.Datum licenseFamilyCategory,
        final MetaData.Datum licenseFamilyName, final String notes, final String fullText) {
        super(licenseFamilyCategory, licenseFamilyName, notes, fullText);
        this.containText = BaseLicense.prune(fullText).toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean match(final Document subject, final String line) throws RatHeaderAnalysisException {
        final String inputToMatch = BaseLicense.prune(line).toLowerCase(Locale.ENGLISH);
        this.line1 = this.line2;
        this.line2 = this.line3;
        this.line3 = inputToMatch;

        if ((this.line1 + this.line2 + this.line3).contains(this.containText)) {
            this.reportOnLicense(subject);
            return true;
        }
        return false;
    }
}
