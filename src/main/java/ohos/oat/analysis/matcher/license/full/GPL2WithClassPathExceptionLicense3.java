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
public class GPL2WithClassPathExceptionLicense3 extends OatDefaultFullTextLicenseMatcher {
    private static final String FIRST_LICENSE_LINE =
        "This code is free software; you can redistribute it and/or modify it "
            + " under the terms of the GNU General Public License version 2 only, as "
            + " published by the Free Software Foundation.  Oracle designates this "
            + " particular file as subject to the \"Classpath\" exception as provided "
            + " by Oracle in the LICENSE file that accompanied this code.";

    public GPL2WithClassPathExceptionLicense3() {
        super("GPL-2.0-with-classpath-exception", "GPL-2.0-with-classpath-exception", "",
            GPL2WithClassPathExceptionLicense3.FIRST_LICENSE_LINE);
    }

    @Override
    protected boolean stopWhileMatched(final String licenseName) {
        return false;
    }
}
