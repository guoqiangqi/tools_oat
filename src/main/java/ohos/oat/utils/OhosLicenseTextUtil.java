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
public final class OhosLicenseTextUtil {
    /**
     * Private constructure to prevent new instance
     */
    private OhosLicenseTextUtil() {
    }

    /**
     * Compress text, discard copyright characters
     *
     * @param text License text to compress
     * @return Compressed license text without copyright string
     */
    public static String cleanCopyrightLines(String text) {
        if (text == null || text.length() <= 0) {
            return "";
        }
        final int length = text.length();
        final StringBuilder buffer = new StringBuilder(length);
        int lastindex = -1;

        for (int i = 0; i < length; ++i) {
            final char at = text.charAt(i);
            if (at == '\n') {
                final String tmpStr = buffer.toString();
                lastindex = getLastindex(buffer, lastindex, at, tmpStr);

            } else {
                buffer.append(at);
            }
        }
        text = buffer.toString();
        text = OhosLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(text, true);
        return text;
    }

    private static int getLastindex(final StringBuilder buffer, int lastindex, final char at, final String tmpStr) {
        if (lastindex > -1) {
            // not first line
            final int index = tmpStr.substring(lastindex).indexOf("copyright (c)");
            if (index >= 0 && index < 20) {
                buffer.delete(0, tmpStr.length());
                lastindex = -1;
            } else {
                buffer.append(at);
                lastindex = tmpStr.length() + 1;
            }
        } else {
            // first line
            final int index = tmpStr.indexOf("copyright");
            if (index >= 0 && index < 20) {
                buffer.delete(0, tmpStr.length());
                lastindex = -1;
            } else {
                buffer.append(at);
                lastindex = tmpStr.length() + 1;
            }
        }
        return lastindex;
    }

    /**
     * Compress text, discard non-alphanumeric characters
     *
     * @param licenseText License text to compress
     * @param needCut Cut text or not while underscore character is present
     * @return Compressed license text without non-alphanumeric characters
     */
    public static String cleanNoLetterAndCutTemplateFlag(final String licenseText, final boolean needCut) {
        if (licenseText == null || licenseText.length() <= 0) {
            return "";
        }
        final int length = licenseText.length();
        final StringBuilder buffer = new StringBuilder(length);

        for (int i = 0; i < length; ++i) {
            final char at = licenseText.charAt(i);

            if (needCut && i < length - 1 && at == '_' && licenseText.charAt(i + 1) == '_') {
                break;
            }
            if (Character.isLetterOrDigit(at)) {
                buffer.append(at);
            }
        }

        return buffer.toString();
    }

    /**
     * Compress text, discard copyright characters
     *
     * @param licenseText License text to compress
     * @return Compressed license text without copyright string
     */
    public static String cleanCopyrightString(final String licenseText) {
        String compresssedLicenseText = licenseText;
        if (compresssedLicenseText == null || compresssedLicenseText.length() <= 0) {
            return "";
        }
        compresssedLicenseText = OhosLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(compresssedLicenseText, true);
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "copyrightcyearnameofauthor");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "copyrightcyearyourname");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText,
            "copyrightcyearcopyrightholders");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "copyrightc19xxnameofauthor");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "copyrightcyyyynameofauthor");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "copyright2001myname");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "pigstycopyright2001myname");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText,
            "copyrightyyyynameofcopyrightowner");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText,
            "copyrightc2019nameofcopyrightholder");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText,
            "copyrightc2019nameofcopyrightholder");
        compresssedLicenseText = OhosLicenseTextUtil.getString(compresssedLicenseText, "copyright2003myname");
        return compresssedLicenseText;
    }

    /**
     * Compress text, discard string before the copyrightText
     *
     * @param text License text to compress
     * @param copyrightText copyright string
     * @return Compressed license text without copyright string
     */
    public static String getString(String text, final String copyrightText) {
        final String cpstr;
        final int index;
        cpstr = copyrightText;
        index = text.indexOf(cpstr);
        if (index >= 0) {
            text = text.substring(index + cpstr.length());
        }
        return text;
    }

    public static final String cleanLetter(final String licenseStr) {
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

}
