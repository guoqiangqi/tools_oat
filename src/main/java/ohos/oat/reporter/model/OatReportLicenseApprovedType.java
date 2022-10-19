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
 */

package ohos.oat.reporter.model;

/**
 * @author chenyaxun
 * @since 2.0
 */
public enum OatReportLicenseApprovedType {
    OSI("osi approved", "OSI approved", "This license is approved by OSI."),
    FSF("liability", "Liability", "This license is approved by FSF."),
    OH_USER_SPACE("openharmony user space", "Openharmony user space",
        "This license can be used in openharmony user space repo."),
    OH_KERNEL_LITEOS("openharmony liteos kernel", "Openharmony liteos kernel",
        "This license can be used in openharmony user space repo."),
    OH_KERNEL_LINUX("openharmony linux kernel", "Openharmony linux kernel",
        "This license can be used in openharmony user space repo.");

    private String tag;

    private String label;

    private String description;

    OatReportLicenseApprovedType(final String tag, final String label, final String description) {
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
