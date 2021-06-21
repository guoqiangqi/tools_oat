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

import ohos.oat.analysis.headermatcher.OatLicense;
import ohos.oat.config.OatConfig;

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
public final class OatSpdxLicenseUtil {
    /**
     * Private constructure to prevent new instance
     */
    private OatSpdxLicenseUtil() {
    }

    /**
     * Use spdx library apis to get all license texts and store them in a unmodifiableList and return
     *
     * @param oatConfig Ohos config data structure
     */
    public static void initSpdxLicenseList(final OatConfig oatConfig) {
        // List for store spdx licenses
        List<OatLicense> licenseList = OatFileUtils.readJsonFromFile("/licenses.json", OatLicense.class);
        List<OatLicense> exceptionLicenseList = OatFileUtils.readJsonFromFile("/licenses-exception.json",
            OatLicense.class);
        // Just use local license files
        // System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
        // System.setProperty("log4j2.error", "true");
        if (licenseList != null) {
            oatConfig.setLicenseList(Collections.unmodifiableList(licenseList));
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
                OatLogUtil.traceException(e);
            }
            for (final OatLicense oatLicense : licenseList) {
                OatLogUtil.warn(OatSpdxLicenseUtil.class.getSimpleName(), oatLicense.getLicenseHeaderText());
            }
            Collections.sort(licenseList, new Comparator<OatLicense>() {
                @Override
                public int compare(final OatLicense o1, final OatLicense o2) {
                    return o2.getLicenseHeaderTextLength() - o1.getLicenseHeaderTextLength();
                }
            });
            final String licenseListJsonString = JSON.toJSONString(licenseList);
            OatFileUtils.saveJson2File(licenseListJsonString, "licenses.json");
            oatConfig.setLicenseList(Collections.unmodifiableList(licenseList));
        }

        if (exceptionLicenseList != null) {
            oatConfig.setExceptionLicenseList(Collections.unmodifiableList(exceptionLicenseList));
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
                OatLogUtil.traceException(e);
            }

            for (final OatLicense oatLicense : exceptionLicenseList) {
                OatLogUtil.warn(OatSpdxLicenseUtil.class.getSimpleName(), oatLicense.getLicenseHeaderText());
            }
            Collections.sort(exceptionLicenseList, new Comparator<OatLicense>() {
                @Override
                public int compare(final OatLicense o1, final OatLicense o2) {
                    return o2.getLicenseHeaderTextLength() - o1.getLicenseHeaderTextLength();
                }
            });

            final String exceptionLicenseListJsonString = JSON.toJSONString(exceptionLicenseList);
            OatFileUtils.saveJson2File(exceptionLicenseListJsonString, "licenses-exception.json");
            oatConfig.setExceptionLicenseList(Collections.unmodifiableList(exceptionLicenseList));
        }

        int count = 0;
        for (final OatLicense oatLicense : licenseList) {
            for (final OatLicense license : licenseList) {

                if ((!oatLicense.getLicenseId().equals(license.getLicenseId())) && oatLicense.getLicenseHeaderText()
                    .equals(license.getLicenseHeaderText())) {
                    count++;
                    OatLogUtil.println("", oatLicense.getLicenseId() + "\t" + license.getLicenseId());
                }
            }
        }
        OatLogUtil.println("", count + "");

    }

    private static void convertSpdx2OhoslicenseList(final List<OatLicense> licenseList,
        final SpdxListedLicense spdxListedLicense) throws InvalidSPDXAnalysisException {
        final OatLicense oatLicense = new OatLicense();
        oatLicense.setLicenseName(spdxListedLicense.getName());
        oatLicense.setLicenseId(spdxListedLicense.getLicenseId());

        final Collection<String> seealso = spdxListedLicense.getSeeAlso();
        for (final String seealsoUrl : seealso) {
            oatLicense.addUrls(OatLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(seealsoUrl, false));
        }
        final String licenseText = spdxListedLicense.getLicenseText().toLowerCase(Locale.ENGLISH);
        final String licenseHeaderText = spdxListedLicense.getStandardLicenseHeader().toLowerCase(Locale.ENGLISH);
        oatLicense.setLicenseText(licenseText);
        oatLicense.setLicenseHeaderText(licenseHeaderText);
        simplifyOhosLicense(licenseList, oatLicense);
    }

    private static void convertSpdxException2OhoslicenseList(final List<OatLicense> licenseList,
        final ListedLicenseException spdxListedLicense) throws InvalidSPDXAnalysisException {
        final OatLicense oatLicense = new OatLicense();
        oatLicense.setLicenseName(spdxListedLicense.getName());
        oatLicense.setLicenseId(spdxListedLicense.getLicenseExceptionId());

        final Collection<String> seealso = spdxListedLicense.getSeeAlso();
        for (final String seealsoUrl : seealso) {
            oatLicense.addUrls(OatLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(seealsoUrl, false));
        }
        final String licenseText = spdxListedLicense.getLicenseExceptionText().toLowerCase(Locale.ENGLISH);
        final String licenseHeaderText = spdxListedLicense.getLicenseExceptionText().toLowerCase(Locale.ENGLISH);
        oatLicense.setLicenseText(licenseText);
        oatLicense.setLicenseHeaderText(licenseHeaderText);
        simplifyOhosLicense(licenseList, oatLicense);
    }

    private static void simplifyOhosLicense(final List<OatLicense> licenseList, final OatLicense oatLicense) {
        final String licenseText = oatLicense.getLicenseText();
        final String licenseHeaderText = oatLicense.getLicenseHeaderText();
        if (licenseHeaderText.trim().length() <= 9) {
            // use full license text
            String tmpString = OatLicenseTextUtil.cleanCopyrightLines(licenseText);
            final int length = tmpString.length();
            final int maxLength = 4000;
            if (length > maxLength) {
                tmpString = tmpString.substring(0, maxLength);
            }
            if (length > 9) {
                oatLicense.setLicenseHeaderText(tmpString);
                licenseList.add(oatLicense);
            } else {
                OatLogUtil.println("licenseTextNull:\t", oatLicense.getLicenseId());
            }

        } else {
            // use license header text
            final String tmp = OatLicenseTextUtil.cleanCopyrightString(licenseHeaderText);
            final int length = tmp.length();
            if (length > 9) {
                oatLicense.setLicenseHeaderText(tmp);
                licenseList.add(oatLicense);
            } else {
                OatLogUtil.println("licenseHeaderTextNull:\t", oatLicense.getLicenseId());
            }
        }
        final String tmpLicenseString = OatLicenseTextUtil.cleanCopyrightLines(licenseText);
        oatLicense.setLicenseText(tmpLicenseString);
    }
}
