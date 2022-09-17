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
 * 2021.5 -  Add new interface to support return the header matcher id.
 * Modified by jalenchen
 */

package ohos.oat.analysis.matcher;

import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLogUtil;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * New oat matcher interface to support return the header matcher id.
 *
 * @author chenyaxun
 * @since 1.0
 */
public interface IOatMatcher {

    String[] COPY_LEFT_LICENSE_NAME = new String[] {
        "CeCILL", "GPL", "MPL", "EPL", "CDDL", "CPL", "IPL", "NPL", "OSL"
    };

    String getMatcherId();

    void reset();

    boolean match(IOatDocument oatDocument, String lineString);

    /**
     * stop if matched spdx license header
     *
     * @param licenseName license name
     * @return stop match or not
     */
    static boolean stopWhileMatchedSpdx(final String licenseName) {
        return licenseName != null && (licenseName.startsWith("SPDX:"));
    }

    /**
     * stop if matched any license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    static boolean stopWhileMatchedAny(final String licenseName) {
        return licenseName != null && (!licenseName.equals("InvalidLicense"));
    }

    /**
     * stop if matched GPL license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    static boolean stopWhileMatchedGPL(final String licenseName) {
        return licenseName != null && licenseName.contains("GPL");
    }

    static boolean needMatchAgain(final String matchedName, final String licenseNameToMatch) {
        if (matchedName == null || matchedName.length() == 0 || matchedName.equals("InvalidLicense")) {
            return true;
        }

        for (final String name : IOatMatcher.COPY_LEFT_LICENSE_NAME) {
            if (matchedName.contains(name)) {
                return false;
            }
        }

        for (final String name : IOatMatcher.COPY_LEFT_LICENSE_NAME) {
            if (licenseNameToMatch.contains(name)) {
                return true;
            }
        }
        return false;
    }

    static void reportGPL(final IOatDocument subject, final String licenseFamilyName) {
        // final MetaData metaData = subject.getMetaData();
        // final String licenseName = metaData.value(MetaData.RAT_URL_LICENSE_FAMILY_NAME);
        final String licenseName = subject.getData("LicenseName");
        final String newName;
        if (licenseName.length() == 0 || licenseName.contains("InvalidLicense")) {
            newName = licenseFamilyName;
        } else {
            newName = licenseName + "|" + licenseFamilyName;
        }
        // metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_SAMPLE, ""));
        // metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, newName));
        // metaData.set(new MetaData.Datum(MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY, newName));
        // metaData.set(new MetaData.Datum(MetaData.RAT_URL_LICENSE_FAMILY_NAME, newName));
        subject.putData("LicenseHeaderText", "");
        subject.putData("LicenseCategory", newName);
        subject.putData("LicenseName", newName);
    }

    static Pattern compilePattern(final String patternStr) {
        if (null == patternStr || patternStr.trim().length() <= 0) {
            return null;
        }
        String tmppatternStr = patternStr;
        if (tmppatternStr.contains("**")) {
            tmppatternStr = tmppatternStr.replaceAll("\\*\\*", ".*");
        }
        final Pattern pattern;
        try {
            pattern = Pattern.compile(tmppatternStr, Pattern.CASE_INSENSITIVE);
        } catch (final PatternSyntaxException e) {
            OatLogUtil.traceException(e);
            return null;
        }
        return pattern;
    }

    static boolean matchPattern(final String strToMatch, final Pattern pattern) {
        if (strToMatch == null || strToMatch.trim().length() <= 0) {
            return false;
        }
        return pattern.matcher(strToMatch).matches();
    }
}
