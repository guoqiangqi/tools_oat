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

import ohos.oat.analysis.headermatcher.OhosLicense;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.license.ListedLicenses;
import org.spdx.library.model.license.SpdxListedLicense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
     * @return spdx license list
     */
    public static List<OhosLicense> createSpdxLicenseList() {
        // List for store spdx licenses
        final List<OhosLicense> licenseList = new ArrayList<>();

        // Just use local license files
        System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
        System.setProperty("log4j2.error", "true");

        final List<String> standardLicenseIds = ListedLicenses.getListedLicenses().getSpdxListedLicenseIds();

        try {
            for (final String standardLicenseId : standardLicenseIds) {
                final SpdxListedLicense spdxListedLicense = ListedLicenses.getListedLicenses()
                    .getListedLicenseById(standardLicenseId);
                final OhosLicense ohosLicense = new OhosLicense();
                ohosLicense.setLicenseName(spdxListedLicense.getName());
                ohosLicense.setLicenseId(spdxListedLicense.getLicenseId());
                ohosLicense.setSpdxListedLicense(spdxListedLicense);

                final Collection<String> seealso = spdxListedLicense.getSeeAlso();
                for (final String seealsoUrl : seealso) {
                    ohosLicense.addUrls(OhosLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(seealsoUrl, false));
                }
                final String licenseText = spdxListedLicense.getLicenseText().toLowerCase();
                final String licenseHeaderText = spdxListedLicense.getStandardLicenseHeader().toLowerCase();
                processLicenseList(licenseList, licenseText, licenseHeaderText, ohosLicense);
            }

        } catch (final InvalidSPDXAnalysisException e) {
            OhosLogUtil.traceException(e);
        }
        return Collections.unmodifiableList(licenseList);
    }

    private static void processLicenseList(final List<OhosLicense> licenseList, final String licenseText,
        final String licenseHeaderText, final OhosLicense ohosLicense) {
        if (licenseHeaderText.trim().length() <= 9) {
            // use full license text
            String tmpString = OhosLicenseTextUtil.cleanCopyrightLines(licenseText);
            final int length = tmpString.length();
            if (length > 2000) {
                tmpString = tmpString.substring(0, 2000);
            }
            if (length > 9) {
                ohosLicense.setLicenseHeaderText(tmpString);
                licenseList.add(ohosLicense);
            }

        } else {
            // use license header text
            final String tmp = OhosLicenseTextUtil.cleanCopyrightString(licenseHeaderText);
            final int length = tmp.length();
            if (length > 9) {
                ohosLicense.setLicenseHeaderText(tmp);
                licenseList.add(ohosLicense);
            }
        }
    }
}
