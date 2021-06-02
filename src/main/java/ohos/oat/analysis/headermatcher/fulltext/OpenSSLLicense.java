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

package ohos.oat.analysis.headermatcher.fulltext;

import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_CATEGORY;
import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_NAME;

import org.apache.rat.api.MetaData;

/**
 * Header matcher class for matching source code license headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OpenSSLLicense extends OhosFullTextMatchingLicense {
    private static final String LICENSE_LINE_1
        = "Licensed under the OpenSSL license (the \"License\").  You may not use";

    private static final String LICENSE_LINE_2
        = "this file except in compliance with the License.  You can obtain a copy";

    private static final String LICENSE_LINE_3 = "in the file LICENSE in the source distribution or at";

    private static final String LICENSE_LINE_4 = "https://www.openssl.org/source/license.html";

    public OpenSSLLicense() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "OpenSSL"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "OpenSSL"), "",
            LICENSE_LINE_1 + LICENSE_LINE_2 + LICENSE_LINE_3 + LICENSE_LINE_4);
    }
}
