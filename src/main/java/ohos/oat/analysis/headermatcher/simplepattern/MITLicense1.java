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

package ohos.oat.analysis.headermatcher.simplepattern;

import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY;
import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_NAME;

import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class MITLicense1 extends OatDefaultSimplePatternLicenseMatcher {
    public MITLicense1() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "MIT"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "MIT"), "", new String[] {
                "under the terms of the MIT license", "under the permissive MIT License", "under MIT License",
                "under standard MIT license", "License: MIT", "under the MIT license"
            });
    }
}
