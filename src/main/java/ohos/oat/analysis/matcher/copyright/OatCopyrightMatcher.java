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
 * 2021.4 - Add InvalidCopyright flag to the document when the copyright string is not matched
 * Modified by jalenchen
 */

package ohos.oat.analysis.matcher.copyright;

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatLogUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Header matcher class for matching copyright header texts.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatCopyrightMatcher implements IOatMatcher {
    private final ArrayList<Pattern> copyrightPatternList = new ArrayList<>();

    private int line;

    public OatCopyrightMatcher() {
        this.copyrightPatternList.add(IOatMatcher.compilePattern("(Copyright .{0,40}[0-9]{4}(\\-[0-9]{4})? (.*))"));
    }

    @Override
    public boolean match(final IOatDocument subject, final String licensHeaderText) {
        this.line++;
        //        if (this.line > 50) {
        //            // return true;
        //        }
        String tmpText = licensHeaderText;
        // final MetaData metaData = subject.getMetaData();
        // String owner = metaData.value("CopyrightOwner");
        String owner = subject.getData("CopyrightOwner");
        final int index = tmpText.indexOf("Copyright");
        if (index >= 0) {
            try {
                tmpText = tmpText.substring(index); // 获取从Copyright开始的字符串
            } catch (final Exception e) {
                OatLogUtil.traceException(e);
                return false;
            }
        } else {
            // 不含copyright
            return false;
        }
        if (tmpText.trim().length() <= 0) {
            return false;
        }

        for (final Pattern pattern : this.copyrightPatternList) {
            final Matcher matcher = pattern.matcher(tmpText);
            if (!matcher.find()) {
                // If not matched, continue
                continue;
            }
            String cp = tmpText;
            if (matcher.groupCount() >= 3) {
                cp = matcher.group(1);
            }
            if (owner.length() > 0 && !owner.equals("InvalidCopyright")) {
                if (cp.length() < 300) {
                    owner = owner + "|" + cp;
                }
            } else {
                owner = cp;
            }
        }
        if (owner == null || owner.trim().length() == 0) {
            owner = "InvalidCopyright";
        }
        // metaData.set(new MetaData.Datum("CopyrightOwner", owner));
        subject.putData("CopyrightOwner", owner);
        return false;
    }

    @Override
    public void reset() {
        this.copyrightPatternList.clear();
    }

    @Override
    public String getMatcherId() {
        return this.getClass().getSimpleName();
    }
}
