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
public class MITLicense extends OatDefaultFullTextLicenseMatcher {
    private static final String FIRST_LICENSE_LINE =
        "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and"
            + " associated documentation files (the \"Software\"), to deal in the Software without restriction,"
            + " including without limitation the rights to use, copy, modify, merge, publish, distribute,"
            + " sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is"
            + " furnished to do so, subject to the following conditions:";

    private static final String MIDDLE_LICENSE_LINE =
        "The above copyright notice and this permission notice shall be included in all copies or substantial"
            + " portions of the Software.";

    private static final String AS_IS_LICENSE_LINE =
        "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT"
            + " LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND"
            + " NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,"
            + " DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT"
            + " OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.";

    public MITLicense() {
        super("MIT", "MIT", "",
            MITLicense.FIRST_LICENSE_LINE + MITLicense.MIDDLE_LICENSE_LINE + MITLicense.AS_IS_LICENSE_LINE);
    }
}
