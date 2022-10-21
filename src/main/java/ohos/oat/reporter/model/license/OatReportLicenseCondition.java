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
public enum OatReportLicenseCondition {
    INCLUDE_COPYRIGHT("include-copyright", "License and copyright notice",
        "A copy of the license and copyright notice must be included with the licensed material."),
    INCLUDE_COPYRIGHT_SOURCE("include-copyright--source", "License and copyright notice for source",
        " A copy of the license and copyright notice must be included with the licensed material in source form, but "
            + "is not required for binaries."),
    DOCUMENT_CHANGES("document-changes", "State changes", "Changes made to the licensed material must be documented."),
    DISCLOSE_SOURCE("disclose-source", "Disclose source",
        "Source code must be made available when the licensed material is distributed."),
    NETWORK_USE_DISCLOSE("network-use-disclose", "Network use is distribution",
        "Users who interact with the licensed material via network are given the right to receive a copy of the "
            + "source code."),
    SAME_LICENSE("same-license", "Same license",
        "Modifications must be released under the same license when distributing the licensed material. In some cases"
            + " a similar or related license may be used."),
    SAME_LICENSE_FILE("same-license--file", "Same license (file)",
        "Modifications of existing files must be released under the same license when distributing the licensed "
            + "material. In some cases a similar or related license may be used."),
    SAME_LICENSE_LIBRARY("same-license--library", "Same license (library)",
        "Modifications must be released under the same license when distributing the licensed material. In some cases"
            + " a similar or related license may be used, or this condition may not apply to works that use the "
            + "licensed material as a library.");

    private String tag;

    private String label;

    private String description;

    OatReportLicenseCondition(final String tag, final String label, final String description) {
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
