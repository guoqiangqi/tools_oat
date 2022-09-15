/*
 * Copyright (c) 2022 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ohos.oat.input.model;

import ohos.oat.config.OatFileFilter;
import ohos.oat.utils.OatCfgUtil;

/**
 * Used to convert the '-filter' parameter of the command line into an OatFileFilter object
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatCommandLineFilterPara {

    /**
     * @param para Command line '-filter' option string
     * @return OatPolicy
     */
    public static OatFileFilter getOatFileFilter(final String para) {
        
        final String tmpPara = para.trim();
        final OatFileFilter oatFileFilter = new OatFileFilter();
        oatFileFilter.setDesc("Command line filter str: " + tmpPara);
        oatFileFilter.setName("defaultFilter");
        final String[] strOatFilterItems = OatCfgUtil.getSplitStrings(tmpPara, ";");
        if (strOatFilterItems.length <= 0) {
            return null;
        }
        for (final String strOatFilterItem : strOatFilterItems) {
            final String[] kv = OatCfgUtil.getSplitStrings(strOatFilterItem.trim(), ":");
            if (kv.length < 2) {
                return null;
            }
            final String kstring = kv[0].trim();
            final String vstring = kv[1].trim();
            if (kstring.equals("filename")) {
                final String[] valueparts = OatCfgUtil.getSplitStrings(vstring); // part split by |
                for (final String valuepart : valueparts) {
                    oatFileFilter.addFilterItem(valuepart);
                }
            } else if (kstring.equals("filepath")) {
                final String[] valueparts = OatCfgUtil.getSplitStrings(vstring); // part split by |
                for (final String valuepart : valueparts) {
                    oatFileFilter.addFilePathFilterItem(valuepart);
                }
            } else {
                return null;
            }
        }
        return oatFileFilter;
    }

}
