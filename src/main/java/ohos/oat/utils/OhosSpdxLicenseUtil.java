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
 * 2021.2 - Use spdx library to collect license templates and headers defined in spdx
 * Modified by jalenchen
 */

package ohos.oat.utils;

import com.alibaba.fastjson.JSON;

import ohos.oat.analysis.headermatcher.OhosLicense;
import ohos.oat.config.OhosConfig;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.ListedLicenseException;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.library.model.license.SpdxListedLicense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Stateless utility class for spdx license collection
 *
 * @author chenyaxun
 * @since 1.0
 */
public final class OhosSpdxLicenseUtil {
    /**
     * Private constructure to prevent new instance
     */
    private OhosSpdxLicenseUtil() {
    }

    /**
     * Use spdx library apis to get all license texts and store them in a unmodifiableList and return
     *
     * @param ohosConfig Ohos config data structure
     */
    public static void initSpdxLicenseList(final OhosConfig ohosConfig) {
        // List for store spdx licenses
        List<OhosLicense> licenseList = OhosFileUtils.readJsonFromFile("/licenses.json", OhosLicense.class);
        List<OhosLicense> exceptionLicenseList = OhosFileUtils.readJsonFromFile("/licenses-exception.json",
            OhosLicense.class);
        // Just use local license files
        // System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
        // System.setProperty("log4j2.error", "true");
        if (licenseList != null) {
            ohosConfig.setLicenseList(Collections.unmodifiableList(licenseList));
        } else {
            licenseList = new ArrayList<>();
            final List<String> standardLicenseIds = ListedLicenses.getListedLicenses().getSpdxListedLicenseIds();
            try {
                for (final String standardLicenseId : standardLicenseIds) {
                    final SpdxListedLicense spdxListedLicense = ListedLicenses.getListedLicenses()
                        .getListedLicenseById(standardLicenseId);
                    convertSpdx2OhoslicenseList(licenseList, spdxListedLicense);
                }
            } catch (final InvalidSPDXAnalysisException e) {
                OhosLogUtil.traceException(e);
            }
            for (final OhosLicense ohosLicense : licenseList) {
                OhosLogUtil.warn(OhosSpdxLicenseUtil.class.getSimpleName(), ohosLicense.getLicenseHeaderText());
            }
            Collections.sort(licenseList, new Comparator<OhosLicense>() {
                @Override
                public int compare(final OhosLicense o1, final OhosLicense o2) {
                    return o2.getLicenseHeaderTextLength() - o1.getLicenseHeaderTextLength();
                }
            });
            final String licenseListJsonString = JSON.toJSONString(licenseList);
            OhosFileUtils.saveJson2File(licenseListJsonString, "licenses.json");
            ohosConfig.setLicenseList(Collections.unmodifiableList(licenseList));
        }

        if (exceptionLicenseList != null) {
            ohosConfig.setExceptionLicenseList(Collections.unmodifiableList(exceptionLicenseList));
        } else {
            exceptionLicenseList = new ArrayList<>();
            final List<String> standardExceptionLicenseIds = ListedLicenses.getListedLicenses()
                .getSpdxListedExceptionIds();
            try {
                for (final String standardExceptionLicenseId : standardExceptionLicenseIds) {
                    final ListedLicenseException spdxListedException = ListedLicenses.getListedLicenses()
                        .getListedExceptionById(standardExceptionLicenseId);
                    convertSpdxException2OhoslicenseList(exceptionLicenseList, spdxListedException);
                }
            } catch (final InvalidSPDXAnalysisException e) {
                OhosLogUtil.traceException(e);
            }

            for (final OhosLicense ohosLicense : exceptionLicenseList) {
                OhosLogUtil.warn(OhosSpdxLicenseUtil.class.getSimpleName(), ohosLicense.getLicenseHeaderText());
            }
            Collections.sort(exceptionLicenseList, new Comparator<OhosLicense>() {
                @Override
                public int compare(final OhosLicense o1, final OhosLicense o2) {
                    return o2.getLicenseHeaderTextLength() - o1.getLicenseHeaderTextLength();
                }
            });

            final String exceptionLicenseListJsonString = JSON.toJSONString(exceptionLicenseList);
            OhosFileUtils.saveJson2File(exceptionLicenseListJsonString, "licenses-exception.json");
            ohosConfig.setExceptionLicenseList(Collections.unmodifiableList(exceptionLicenseList));
        }

        int count = 0;
        for (final OhosLicense ohosLicense : licenseList) {
            for (final OhosLicense license : licenseList) {

                if ((!ohosLicense.getLicenseId().equals(license.getLicenseId())) && ohosLicense.getLicenseHeaderText()
                    .equals(license.getLicenseHeaderText())) {
                    count++;
                    OhosLogUtil.println("", ohosLicense.getLicenseId() + "\t" + license.getLicenseId());
                }
            }
        }
        OhosLogUtil.println("", count + "");

    }

    private static void convertSpdx2OhoslicenseList(final List<OhosLicense> licenseList,
        final SpdxListedLicense spdxListedLicense) throws InvalidSPDXAnalysisException {
        final OhosLicense ohosLicense = new OhosLicense();
        ohosLicense.setLicenseName(spdxListedLicense.getName());
        ohosLicense.setLicenseId(spdxListedLicense.getLicenseId());

        final Collection<String> seealso = spdxListedLicense.getSeeAlso();
        for (final String seealsoUrl : seealso) {
            ohosLicense.addUrls(OhosLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(seealsoUrl, false));
        }
        final String licenseText = spdxListedLicense.getLicenseText().toLowerCase(Locale.ENGLISH);
        final String licenseHeaderText = spdxListedLicense.getStandardLicenseHeader().toLowerCase(Locale.ENGLISH);
        ohosLicense.setLicenseText(licenseText);
        ohosLicense.setLicenseHeaderText(licenseHeaderText);
        simplifyOhosLicense(licenseList, ohosLicense);
    }

    private static void convertSpdxException2OhoslicenseList(final List<OhosLicense> licenseList,
        final ListedLicenseException spdxListedLicense) throws InvalidSPDXAnalysisException {
        final OhosLicense ohosLicense = new OhosLicense();
        ohosLicense.setLicenseName(spdxListedLicense.getName());
        ohosLicense.setLicenseId(spdxListedLicense.getLicenseExceptionId());

        final Collection<String> seealso = spdxListedLicense.getSeeAlso();
        for (final String seealsoUrl : seealso) {
            ohosLicense.addUrls(OhosLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(seealsoUrl, false));
        }
        final String licenseText = spdxListedLicense.getLicenseExceptionText().toLowerCase(Locale.ENGLISH);
        final String licenseHeaderText = spdxListedLicense.getLicenseExceptionText().toLowerCase(Locale.ENGLISH);
        ohosLicense.setLicenseText(licenseText);
        ohosLicense.setLicenseHeaderText(licenseHeaderText);
        simplifyOhosLicense(licenseList, ohosLicense);
    }

    private static void simplifyOhosLicense(final List<OhosLicense> licenseList, final OhosLicense ohosLicense) {
        final String licenseText = ohosLicense.getLicenseText();
        final String licenseHeaderText = ohosLicense.getLicenseHeaderText();
        if (licenseHeaderText.trim().length() <= 9) {
            // use full license text
            String tmpString = OhosLicenseTextUtil.cleanCopyrightLines(licenseText);
            final int length = tmpString.length();
            final int maxLength = 4000;
            if (length > maxLength) {
                tmpString = tmpString.substring(0, maxLength);
            }
            if (length > 9) {
                ohosLicense.setLicenseHeaderText(tmpString);
                licenseList.add(ohosLicense);
            } else {
                OhosLogUtil.println("licenseTextNull:\t", ohosLicense.getLicenseId());
            }

        } else {
            // use license header text
            final String tmp = OhosLicenseTextUtil.cleanCopyrightString(licenseHeaderText);
            final int length = tmp.length();
            if (length > 9) {
                ohosLicense.setLicenseHeaderText(tmp);
                licenseList.add(ohosLicense);
            } else {
                OhosLogUtil.println("licenseHeaderTextNull:\t", ohosLicense.getLicenseId());
            }
        }
        final String tmpLicenseString = OhosLicenseTextUtil.cleanCopyrightLines(licenseText);
        ohosLicense.setLicenseText(tmpLicenseString);
    }
}
