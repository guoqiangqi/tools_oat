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
public class ZlibLicense extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1 =
        "This software is provided 'as-is', without any express or implied warranty. In no event will "
            + "the authors be "
            + "held liable for any damages arising from the use of this software.Permission is granted to anyone to "
            + "use this software for any purpose, including commercial applications,"
            + " and to alter it and redistribute it freely, subject to the following restrictions:";

    private static final String LICENSE_LINE_2 =
        "1. The origin of this software must not be misrepresented; you must not claim that you wrote the original"
            + " software. If you use this software in a product, an acknowledgment in the product documentation"
            + " would be appreciated but is not required.";

    private static final String LICENSE_LINE_3 =
        "2. Altered source versions must be plainly marked as such, and must not be misrepresented as being the"
            + " original software.";

    private static final String LICENSE_LINE_4
        = "3. This notice may not be removed or altered from any source distribution.";

    public ZlibLicense() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "Zlib"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "Zlib"), "",
            LICENSE_LINE_1 + LICENSE_LINE_2 + LICENSE_LINE_3 + LICENSE_LINE_4);
    }
}
