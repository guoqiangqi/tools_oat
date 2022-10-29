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
public class MITLicense5 extends OatDefaultFullTextLicenseMatcher {
    private static final String FIRST_LICENSE_LINE =
        "Permission is hereby granted, without written agreement and without license or royalty fees, to use,"
            + " copy, modify, and distribute this software and its documentation for any purpose, provided that"
            + " the above copyright notice and the following two paragraphs appear in all copies of this"
            + " software.";

    private static final String MIDDLE_LICENSE_LINE =
        "IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,"
            + " OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF"
            + " THE COPYRIGHT HOLDER HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";

    private static final String AS_IS_LICENSE_LINE =
        "THE COPYRIGHT HOLDER SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED"
            + " WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED"
            + " HEREUNDER IS ON AN \"AS IS\" BASIS, AND THE COPYRIGHT HOLDER HAS NO OBLIGATION TO PROVIDE"
            + " MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.";

    public MITLicense5() {
        super("MIT", "MIT", "",
            MITLicense5.FIRST_LICENSE_LINE + MITLicense5.MIDDLE_LICENSE_LINE + MITLicense5.AS_IS_LICENSE_LINE);
    }
}
