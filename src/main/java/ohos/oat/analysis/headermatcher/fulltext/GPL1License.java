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
public class GPL1License extends OhosFullTextMatchingLicense {
    private static final String LICENSE_LINE_1 =
        "This program is free software; you can redistribute it and/or modify\n"
            + " it under the terms of the GNU General Public License as published by\n"
            + " the Free Software Foundation; either version 1, or (at your option)\n" + " any later version.";

    public GPL1License() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "GPL-1.0+"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "GPL-1.0+"), "", LICENSE_LINE_1);
    }

    @Override
    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
