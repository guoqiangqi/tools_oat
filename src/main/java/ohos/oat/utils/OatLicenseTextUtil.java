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
 * 2021.2 - Extracted from OhosLicenseMain class to support common license text compression
 * Modified by jalenchen
 */

package ohos.oat.utils;

/**
 * Stateless utility class for license text compression
 *
 * @author chenyaxun
 * @since 1.0
 */
public final class OatLicenseTextUtil {
    /**
     * Private constructure to prevent new instance
     */
    private OatLicenseTextUtil() {
    }

    public static String cleanAndLowerCaseLetter(final String licenseStr) {
        if (null == licenseStr || licenseStr.length() <= 0) {
            return licenseStr;
        }
        final int size = licenseStr.length();
        final StringBuilder buffer = new StringBuilder(size);

        for (int i = 0; i < size; ++i) {
            char letter = licenseStr.charAt(i);
            if (Character.isLetterOrDigit(letter)) {
                if (Character.isUpperCase(letter)) {
                    letter = Character.toLowerCase(letter);
                }
                buffer.append(letter);
            }
        }

        return buffer.toString();
    }

    public static String[] cleanAndLowerCaseArray(final String[] patterns) {
        final String[] purePatterns = new String[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            purePatterns[i] = OatLicenseTextUtil.cleanAndLowerCaseLetter(patterns[i]);
        }
        return purePatterns;
    }

}
