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

package ohos.oat.analysis.headermatcher;

import ohos.oat.utils.OhosLogUtil;

import org.apache.rat.analysis.IHeaderMatcher;
import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Header matcher class for matching copyright header texts.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosCopyrightMatcher implements IHeaderMatcher {
    private final ArrayList<Pattern> copyrightPatternList = new ArrayList<>();

    public OhosCopyrightMatcher() {
        this.copyrightPatternList.add(
            Pattern.compile("(Copyright .{0,40}[0-9]{4}(\\-[0-9]{4})? (.*))", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public boolean match(final Document subject, final String licensHeaderText) throws RatHeaderAnalysisException {
        String tmpText = licensHeaderText;
        final MetaData metaData = subject.getMetaData();
        String owner = metaData.value("copyright-owner");
        final int index = tmpText.indexOf("Copyright");
        if (index >= 0) {
            try {
                tmpText = tmpText.substring(index); // 获取从Copyright开始的字符串
            } catch (final Exception e) {
                OhosLogUtil.traceException(e);
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
            if (owner != null && !owner.equals("InvalidCopyright")) {
                if (cp.length() < 300) {
                    owner = owner + "|" + cp;
                }
            } else {
                owner = cp;
            }
        }
        if (owner == null || owner.trim().length() <= 0) {
            owner = "InvalidCopyright";
        }
        metaData.set(new MetaData.Datum("copyright-owner", owner));
        return false;
    }

    @Override
    public void reset() {
        this.copyrightPatternList.clear();
    }
}
