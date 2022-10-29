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
 */

package ohos.oat.analysis.matcher.license.simple;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class BSDStyleLicense2 extends OatDefaultSimplePatternLicenseMatcher {
    public BSDStyleLicense2() {
        super("BSDStyleLicense", "BSDStyleLicense", "", new String[] {
            "under BSD 3-Clause license", "opensource.org/licenses/BSD-3-Clause", "under the terms of the BSD license",
            "under the terms of BSD", "Licensed under the 2-clause BSD", "Licensed under the 3-clause BSD",
            "under the terms of the Modified BSD License", "under a open-source 3-clause BSD license",
            "This header is BSD licensed", "This is a heavily cut-down \"BSD license\"", "BSD 2-Clause license"
        });
    }
}
