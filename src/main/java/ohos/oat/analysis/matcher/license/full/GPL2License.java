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
public class GPL2License extends OatDefaultFullTextLicenseMatcher {
    private static final String LICENSE_LINE_1 = "This program is free software; you can redistribute it and/or\n"
        + " modify it under the terms of the GNU General Public License\n"
        + " as published by the Free Software Foundation; either version 2\n"
        + " of the License, or (at your option) any later version.";

    public GPL2License() {
        super("GPL-2.0+", "GPL-2.0+", "", GPL2License.LICENSE_LINE_1);
    }

    @Override
    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
