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

import ohos.oat.config.OatPolicy;
import ohos.oat.config.OatPolicyItem;
import ohos.oat.utils.OatCfgUtil;

/**
 * Used to convert the '-policy' parameter of the command line into an Oat Policy object
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatCommandLinePolicyPara {

    /**
     * @param para Command line '-policy' option string
     * @return OatPolicy
     */
    public static OatPolicy getOatPolicy(final String para) {

        final String tmpPara = para.trim();
        final OatPolicy oatPolicy = new OatPolicy();
        oatPolicy.setName("defaultPolicy");
        oatPolicy.setDesc("Command line policy str: " + tmpPara);
        final String[] strOatPolicyItems = OatCfgUtil.getSplitStrings(tmpPara, ";");
        if (strOatPolicyItems.length <= 0) {
            return null;
        }
        boolean isUpstream = false;
        for (final String strOatPolicyItem : strOatPolicyItems) {
            final String[] kv = OatCfgUtil.getSplitStrings(strOatPolicyItem.trim(), ":");
            if (kv.length < 2) {
                return null;
            }
            final String kstring = kv[0].trim();
            final String vstring = kv[1].trim();
            if (kstring.equals("repotype")) {
                if (vstring.equals("upstream")) {
                    isUpstream = true;
                }
            } else if (kstring.equals("compatibility")) {
                OatCommandLinePolicyPara.fillPolicy(oatPolicy, "compatibility", vstring);
            } else if (kstring.equals("license")) {
                OatCommandLinePolicyPara.fillPolicy(oatPolicy, "license", vstring);
            } else if (kstring.equals("copyright")) {
                OatCommandLinePolicyPara.fillPolicy(oatPolicy, "copyright", vstring);
            } else if (kstring.equals("filename")) {
                OatCommandLinePolicyPara.fillPolicy(oatPolicy, "filename", vstring);
            } else if (kstring.equals("filetype")) {
                OatCommandLinePolicyPara.fillPolicy(oatPolicy, "filetype", vstring);
            } else {
                return null;
            }
        }
        return oatPolicy;
    }

    private static void fillPolicy(final OatPolicy oatPolicy, final String policyType, final String vstring) {

        final String[] nameparts = OatCfgUtil.getSplitStrings(vstring);
        for (final String namepart : nameparts) {

            String tmpName = namepart;
            String tmpPath = ".*";
            final String[] vstringparts = OatCfgUtil.getSplitStrings(namepart, "@");
            if (vstringparts.length == 2) {
                tmpName = vstringparts[0].trim();
                tmpPath = vstringparts[1].trim();
            }

            final OatPolicyItem oatPolicyItem = new OatPolicyItem();
            oatPolicyItem.setType(policyType);
            oatPolicyItem.setGroup("defaultGroup");
            oatPolicyItem.setRule("may");
            oatPolicyItem.setName(tmpName);
            if ("projectroot".equals(tmpPath)) {
                oatPolicyItem.setPath(tmpPath);
            } else {
                oatPolicyItem.setPath("defaultProject/" + tmpPath);
            }
            oatPolicyItem.setDesc("");
            oatPolicyItem.setFileFilter(OatCfgUtil.getFilterName(policyType, ""));
            oatPolicy.addPolicyItem(oatPolicyItem);
        }
    }

}
