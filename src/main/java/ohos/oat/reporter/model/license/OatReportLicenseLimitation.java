/*
 * Copyright (c) 2022 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Derived from choosealicense.com, the original license and notice text is at the end of the LICENSE file of this
 * project.
 */

package ohos.oat.reporter.model.license;

/**
 * @author chenyaxun
 * @since 2.0
 */
public enum OatReportLicenseLimitation {
    TRADEMARK_USE("trademark-use", "Trademark use",
        "This license explicitly states that it does NOT grant trademark rights, even though licenses without such a "
            + "statement probably do not grant any implicit trademark rights."),
    LIABILITY("liability", "Liability", "This license includes a limitation of liability."),
    PATENT_USE("patent-use", "Patent use",
        "This license explicitly states that it does NOT grant any rights in the patents of contributors."),
    WARRANTY("warranty", "Warranty", "This license explicitly states that it does NOT provide any warranty.");

    private String tag;

    private String label;

    private String description;

    OatReportLicenseLimitation(final String tag, final String label, final String description) {
        this.setTag(tag);
        this.setLabel(label);
        this.setDescription(description);
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
