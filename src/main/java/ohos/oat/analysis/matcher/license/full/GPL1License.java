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
public class GPL1License extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1 =
        "This program is free software; you can redistribute it and/or modify\n"
            + " it under the terms of the GNU General Public License as published by\n"
            + " the Free Software Foundation; either version 1, or (at your option)\n" + " any later version.";

    public GPL1License() {
        super("GPL-1.0+", "GPL-1.0+", "", GPL1License.LICENSE_LINE_1);
    }

    @Override
    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
