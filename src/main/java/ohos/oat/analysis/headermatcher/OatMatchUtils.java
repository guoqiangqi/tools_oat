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
 * 2021.5 - Extract from license matcher classes to avoid duplicate code
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLogUtil;

import org.apache.rat.api.MetaData;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Stateless utility class for determining whether to continue matching.
 *
 * @author chenyaxun
 * @since 1.0
 */

public class OatMatchUtils {

    private static final String[] COPY_LEFT_LICENSE_NAME = new String[] {
        "CeCILL", "GPL", "MPL", "EPL", "CDDL", "CPL", "IPL", "NPL", "OSL"
    };

    public OatMatchUtils() {
    }

    /**
     * stop if matched spdx license header
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedSpdx(final String licenseName) {
        return licenseName != null && (licenseName.startsWith("SPDX:"));
    }

    /**
     * stop if matched any license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedAny(final String licenseName) {
        return licenseName != null && (!licenseName.equals("InvalidLicense"));
    }

    /**
     * stop if matched GPL license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedGPL(final String licenseName) {
        return licenseName != null && licenseName.contains("GPL");
    }

    public static boolean needMatchAgain(final String matchedName, final String licenseNameToMatch) {
        if (matchedName == null || matchedName.equals("InvalidLicense")) {
            return true;
        }

        for (final String name : OatMatchUtils.COPY_LEFT_LICENSE_NAME) {
            if (matchedName.contains(name)) {
                return false;
            }
        }

        for (final String name : OatMatchUtils.COPY_LEFT_LICENSE_NAME) {
            if (licenseNameToMatch.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public static void reportGPL(final IOatDocument subject, final String licenseFamilyName) {
        final MetaData metaData = subject.getMetaData();
        final String licenseName = metaData.value(MetaData.RAT_URL_LICENSE_FAMILY_NAME);
        final String newName;
        if (licenseName == null || licenseName.contains("InvalidLicense")) {
            newName = licenseFamilyName;
        } else {
            newName = licenseName + "|" + licenseFamilyName;
        }
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_SAMPLE, ""));
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, newName));
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY, newName));
        metaData.set(new MetaData.Datum(MetaData.RAT_URL_LICENSE_FAMILY_NAME, newName));
    }

    public static Pattern compilePattern(final String patternStr) {
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

    public static boolean matchPattern(final String strToMatch, final Pattern pattern) {
        if (strToMatch == null || strToMatch.trim().length() <= 0) {
            return false;
        }
        return pattern.matcher(strToMatch).matches();
    }
}
