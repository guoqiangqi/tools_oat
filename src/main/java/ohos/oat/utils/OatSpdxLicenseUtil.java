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

import ohos.oat.analysis.headermatcher.OatLicense;
import ohos.oat.config.OatConfig;

import java.util.Collections;
import java.util.List;

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
     * @param oatConfig Oat config data structure
     */
    public static void initSpdxLicenseList(final OatConfig oatConfig) {
        // List for store spdx licenses
        final List<OatLicense> licenseList = OatFileUtils.readJsonFromFile("/licenses.json", OatLicense.class);
        final List<OatLicense> exceptionLicenseList = OatFileUtils.readJsonFromFile("/licenses-exception.json",
            OatLicense.class);
        // Just use local license files
        // System.setProperty("SPDXParser.OnlyUseLocalLicenses", "true");
        // System.setProperty("log4j2.error", "true");
        if (licenseList != null) {
            oatConfig.setLicenseList(Collections.unmodifiableList(licenseList));
        } else {
            OatLogUtil.warn(OatSpdxLicenseUtil.class.getSimpleName(), "SPDX license list is null");
        }

        if (exceptionLicenseList != null) {
            oatConfig.setExceptionLicenseList(Collections.unmodifiableList(exceptionLicenseList));
        } else {
            OatLogUtil.warn(OatSpdxLicenseUtil.class.getSimpleName(), "SPDX exception license list is null");
        }

        int count = 0;
        for (final OatLicense oatLicense : licenseList) {
            for (final OatLicense license : licenseList) {

                if ((!oatLicense.getLicenseId().equals(license.getLicenseId())) && oatLicense.getLicenseHeaderText()
                    .equals(license.getLicenseHeaderText())) {
                    count++;
                    OatLogUtil.warn(OatSpdxLicenseUtil.class.getSimpleName(),
                        oatLicense.getLicenseId() + "\t" + license.getLicenseId());
                }
            }
        }
        OatLogUtil.warn(OatSpdxLicenseUtil.class.getSimpleName(), count + "");

    }

}
