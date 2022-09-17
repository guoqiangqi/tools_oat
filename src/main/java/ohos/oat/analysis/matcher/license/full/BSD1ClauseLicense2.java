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
public class BSD1ClauseLicense2 extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1 =
        "in source and binary forms, with or without modification, are permitted provided that the following"
            + " condition is met:";

    private static final String LICENSE_LINE_2 =
        "1. Redistributions of source code must retain the above copyright notice, this condition and the"
            + " following disclaimer.";

    private static final String LICENSE_LINE_3 = "";

    private static final String LICENSE_LINE_4 =
        "This software is provided by the copyright holder and contributors \"AS IS\" and any warranties related"
            + " to this software are DISCLAIMED. The copyright owner or contributors be NOT LIABLE for any damages"
            + " caused by use of this software.";

    public BSD1ClauseLicense2() {
        super("BSD-1-Clause", "BSD-1-Clause", "",
            BSD1ClauseLicense2.LICENSE_LINE_1 + BSD1ClauseLicense2.LICENSE_LINE_2 + BSD1ClauseLicense2.LICENSE_LINE_3
                + BSD1ClauseLicense2.LICENSE_LINE_4);
    }
}
