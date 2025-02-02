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
 * ChangeLog:
 * 2021.3 - Add this class to check dependency in source files
 * Modified by jalenchen
 */

package ohos.oat.analysis.matcher.dependency;

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.document.IOatDocument;

/**
 * Header matcher class to check dependency.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatImportMatcher implements IOatMatcher {
    private int line;

    @Override
    public boolean match(final IOatDocument subject, final String licensHeaderText) {
        this.line++;
        if (this.line > 80) {
            return true;
        }
        String tmp = subject.getData("ImportName");
        if (tmp == null || tmp.trim().length() == 0) {
            tmp = "NULL";
        }
        if ((licensHeaderText.startsWith("#include") || licensHeaderText.startsWith("import"))) {
            if (!tmp.equals("NULL") && tmp.length() < 300) {
                tmp = tmp + "|" + licensHeaderText;
            } else {
                tmp = licensHeaderText;
            }
        }

        subject.putData("ImportName", tmp);

        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }
}
