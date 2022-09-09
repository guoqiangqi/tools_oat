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
 * @author chenyaxun
 * @since 2.0
 */
public class OatCommandLinePolicyPara {
    //repotype:upstream|dev; license:Apache-2.0@.*|MIT|BSD;copyright:Huawei Device Co., Ltd.;filename:README.md@root
    // List<OatPolicy>
    public static OatPolicy getOatPolicy(final String para) {

        final OatPolicy oatPolicy = new OatPolicy();
        oatPolicy.setName("defaultPolicy");
        oatPolicy.setDesc("Command line policy str: " + para);
        final String[] strOatPolicyItems = OatCfgUtil.getSplitStrings(para, ";");
        if (strOatPolicyItems.length <= 0) {
            return null;
        }
        boolean isUpstream = false;
        for (final String strOatPolicyItem : strOatPolicyItems) {
            final String[] kv = OatCfgUtil.getSplitStrings(strOatPolicyItem, ":");
            if (kv.length < 2) {
                return null;
            }
            final String kstring = kv[0];
            final String vstring = kv[1];
            if (kstring.equals("repotype")) {
                if (vstring.equals("upstream")) {
                    isUpstream = true;
                }
            } else if (kstring.equals("license")) {
                final OatPolicyItem oatPolicyItem = new OatPolicyItem();
                oatPolicyItem.setType("license");
                oatPolicyItem.setGroup("defaultGroup");
                oatPolicyItem.setRule("may");
                oatPolicyItem.setName("");
                oatPolicyItem.setPath("");
                oatPolicyItem.setPath("");
            } else if (kstring.equals("copyright")) {

            } else if (kstring.equals("filename")) {

            } else {
                return null;
            }
        }

        // {
        //     final OatPolicyItem oatPolicyItem = new OatPolicyItem();
        //     oatPolicyItem.setName(OatCfgUtil.getElementAttrValue(policyitemCfg, "name"));
        //     oatPolicyItem.setType(OatCfgUtil.getElementAttrValue(policyitemCfg, "type"));
        //     String policyPath = OatCfgUtil.getElementAttrValue(policyitemCfg, "path");
        //     if (oatProject != null) {
        //         policyPath = oatProject.getPath() + policyPath;
        //     }
        //     oatPolicyItem.setPath(policyPath);
        //     oatPolicyItem.setRule(OatCfgUtil.getElementAttrValue(policyitemCfg, "rule", "may"));
        //     oatPolicyItem.setGroup(OatCfgUtil.getElementAttrValue(policyitemCfg, "group", "defaultGroup"));
        //     final String policyType = oatPolicyItem.getType();
        //     final String policyName = oatPolicyItem.getName();
        //
        //     final String filterName = getFilterName(policyType, policyName);
        //     oatPolicyItem.setFileFilter(OatCfgUtil.getElementAttrValue(policyitemCfg, "filefilter", filterName));
        //     oatPolicyItem.setDesc(OatCfgUtil.getElementAttrValue(policyitemCfg, "desc"));
        //     if (oatProject != null) {
        //         // Project OAT XML
        //         oatPolicyItem.setFileFilterObj(oatConfig.getOatFileFilter(oatPolicyItem.getFileFilter()));
        //         oatProject.getOatPolicy().addPolicyItem(oatPolicyItem);
        //         if (oatProject != null) {
        //             // Project OAT XML
        //             OatLogUtil.logOatConfig(OatCfgUtil.class.getSimpleName(),
        //                 oatProject.getPath() + "\tPolicyItem\t" + oatPolicyItem.getType() + "Policy\tName\t"
        //                     + oatPolicyItem.getName() + "\tPath\t" + oatPolicyItem.getPath() + "\tDesc\t"
        //                     + oatPolicyItem.getDesc());
        //         }
        //     } else {
        //         // Global OAT XML
        //         oatPolicy.addPolicyItem(oatPolicyItem);
        //     }
        // }
        //

        return null;
    }

}
