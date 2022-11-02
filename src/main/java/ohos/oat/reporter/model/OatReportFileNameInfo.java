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

import ohos.oat.document.IOatDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportFileNameInfo {

    private final List<IOatDocument.FilteredRule> filteredRules = new ArrayList<>();

    private int filteredRuleCount = 0;

    private boolean hasReadmeOpenSourceFile;

    private boolean hasLicenseFile;

    private boolean hasReadmeFile;

    public int getFilteredRuleCount() {
        return this.filteredRuleCount;
    }

    public List<IOatDocument.FilteredRule> getFilteredRules() {
        return this.filteredRules;
    }

    public void addFilteredRule(final IOatDocument.FilteredRule filteredRule) {
        if (filteredRule.getPolicyType().equals("filename")) {
            this.filteredRules.add(filteredRule);
            this.filteredRuleCount++;
        }
    }

    public boolean isHasLicenseFile() {
        return this.hasLicenseFile;
    }

    public void setHasLicenseFile(final boolean hasLicenseFile) {
        this.hasLicenseFile = hasLicenseFile;
    }

    public boolean isHasReadmeFile() {
        return this.hasReadmeFile;
    }

    public void setHasReadmeFile(final boolean hasReadmeFile) {
        this.hasReadmeFile = hasReadmeFile;
    }

    public boolean isHasReadmeOpenSourceFile() {
        return this.hasReadmeOpenSourceFile;
    }

    public void setHasReadmeOpenSourceFile(final boolean hasReadmeOpenSourceFile) {
        this.hasReadmeOpenSourceFile = hasReadmeOpenSourceFile;
    }

}
