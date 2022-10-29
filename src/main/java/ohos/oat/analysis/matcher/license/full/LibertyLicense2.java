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

package ohos.oat.analysis.matcher.license.full;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class LibertyLicense2 extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1
        = "Licensed under MCD-ST Liberty SW License Agreement V2, (the \"License\");";

    private static final String LICENSE_LINE_2 = "You may not use this file except in compliance with the License.";

    private static final String LICENSE_LINE_3 = "You may obtain a copy of the License at:";

    private static final String LICENSE_LINE_4 = "http://www.st.com/software_license_agreement_liberty_v2";

    public LibertyLicense2() {
        super("LibertyLicense2", "LibertyLicense2", "",
            LibertyLicense2.LICENSE_LINE_1 + LibertyLicense2.LICENSE_LINE_2 + LibertyLicense2.LICENSE_LINE_3
                + LibertyLicense2.LICENSE_LINE_4);
    }
}
