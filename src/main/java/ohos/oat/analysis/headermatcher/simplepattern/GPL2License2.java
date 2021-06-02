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
public class GPL2License2 extends OhosSimplePatternLicense {
    public GPL2License2() {
        super(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_CATEGORY, "GPL-2.0+"),
            new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "GPL-2.0+"), "", new String[] {
                "Open source license information is in the zlib.ads file",
                "See linux/lib/crc32.c for license and changes", "GNU Public License, Version 2",
                "Licensed under the GPL-2", "Licensed under the GPL2", "Licensed under GPL2", "Licensed under GPL-2",
                "Licensed under the GPLv2", "licensed under GPLv2"
            });
    }

    @Override
    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
