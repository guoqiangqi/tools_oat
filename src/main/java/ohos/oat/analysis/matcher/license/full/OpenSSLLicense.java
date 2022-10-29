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
public class OpenSSLLicense extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1
        = "Licensed under the OpenSSL license (the \"License\").  You may not use";

    private static final String LICENSE_LINE_2
        = "this file except in compliance with the License.  You can obtain a copy";

    private static final String LICENSE_LINE_3 = "in the file LICENSE in the source distribution or at";

    private static final String LICENSE_LINE_4 = "https://www.openssl.org/source/license.html";

    public OpenSSLLicense() {
        super("OpenSSL", "OpenSSL", "",
            OpenSSLLicense.LICENSE_LINE_1 + OpenSSLLicense.LICENSE_LINE_2 + OpenSSLLicense.LICENSE_LINE_3
                + OpenSSLLicense.LICENSE_LINE_4);
    }
}
