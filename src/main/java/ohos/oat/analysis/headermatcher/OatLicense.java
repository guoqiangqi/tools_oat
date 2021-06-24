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
 * 2021.4 - Add this class to match spdx license texts
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * License data structure.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatLicense {
    private final List<String> urls = new ArrayList<>();

    private String licenseName;

    private String licenseId;

    private String licenseText;

    private String licenseHeaderText;

    public int getLicenseHeaderTextLength() {
        return this.licenseHeaderTextLength;
    }

    private int licenseHeaderTextLength;

    public void addUrls(final String url) {
        this.urls.add(url);
    }

    public String getLicenseHeaderText() {
        return this.licenseHeaderText;
    }

    public void setLicenseHeaderText(final String licenseHeaderText) {
        this.licenseHeaderText = licenseHeaderText;
        if (null != licenseHeaderText) {
            this.licenseHeaderTextLength = licenseHeaderText.length();
        }
    }

    public String getLicenseId() {
        return this.licenseId;
    }

    public void setLicenseId(final String licenseId) {
        this.licenseId = licenseId;
    }

    public String getLicenseName() {
        return this.licenseName;
    }

    public void setLicenseName(final String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseText() {
        return this.licenseText;
    }

    public void setLicenseText(final String licenseText) {
        this.licenseText = licenseText;
    }

    public List<String> getUrls() {
        return this.urls;
    }

    @Override
    public String toString() {
        return "OatLicense{" + "licenseId='" + this.licenseId + '\'' + ", licenseHeaderTextLength="
            + this.licenseHeaderTextLength + '}';
    }
}
