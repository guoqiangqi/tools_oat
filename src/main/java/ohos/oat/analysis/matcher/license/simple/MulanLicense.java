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
public class MulanLicense extends OatDefaultSimplePatternLicenseMatcher {
    public MulanLicense() {
        super("MulanPSL-2.0", "MulanPSL-2.0", "", new String[] {
            "Licensed under Mulan PSL v2", "http://license.coscl.org.cn/MulanPSL2",
            "according to the terms and conditions of the Mulan PSL v2"
        });
    }

    // @Override
    // protected boolean stopWhileMatched(final String licenseName) {
    //     return false;
    // }
}
