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
public class LGPLLicense extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1 = "This library is free software; you can redistribute it and/or "
        + "modify it under the terms of the GNU Library General Public "
        + "License as published by the Free Software Foundation; either "
        + "version 2 of the License, or (at your option) any later version.";

    private static final String LICENSE_LINE_2 = "This library is distributed in the hope that it will be useful,"
        + "but WITHOUT ANY WARRANTY; without even the implied warranty of"
        + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU"
        + "Library General Public License for more details.";

    private static final String LICENSE_LINE_3 = "You should have received a copy of the GNU Library General Public\n"
        + "License along with this library";

    public LGPLLicense() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "LGPL-2.0+"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "LGPL-2.0+"), "BSD4ClauseLicense",
            LICENSE_LINE_1 + LICENSE_LINE_2 + LICENSE_LINE_3);
    }

    @Override
    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
