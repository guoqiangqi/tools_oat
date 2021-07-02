/*
 *
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
 * 2021.3 -  Change license file policy type to file name policy to support LICENSE, README, README.OpenSource files
 * checking.
 * 2021.4 -  Add file type policy type to support binary and archive files checking.
 * 2021.5 - Enhance extensibility: Add policyItemListMap and delete all the xxxPolicyItems to support more types of
 * policy in the future.
 * Modified by jalenchen
 *
 */

package ohos.oat.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data Structure of policy defined in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatPolicy {
    private final Map<String, List<OatPolicyItem>> policyItemListMap = new HashMap<>();

    private String name;

    private String desc;

    public OatPolicy() {

    }

    public OatPolicy(final String name, final String desc) {
        this.setName(name);
        this.setDesc(desc);
    }

    public List<OatPolicyItem> getPolicyItems(final String policyType) {
        List<OatPolicyItem> policyItemList = this.policyItemListMap.get(policyType);
        if (null == policyItemList) {
            policyItemList = new ArrayList<>();
        }
        return policyItemList;
    }

    public void addPolicyItem(final OatPolicyItem oatPolicyItem) {
        final String policyType = oatPolicyItem.getType();
        List<OatPolicyItem> policyItemList = this.policyItemListMap.get(policyType);
        if (null == policyItemList) {
            policyItemList = new ArrayList<>();
            this.policyItemListMap.put(policyType, policyItemList);
        }
        policyItemList.add(oatPolicyItem);
    }

    public List<OatPolicyItem> getAllPolicyItems() {
        final List<OatPolicyItem> allPolicyItems = new ArrayList<>();
        for (final Map.Entry<String, List<OatPolicyItem>> stringListEntry : this.policyItemListMap.entrySet()) {
            allPolicyItems.addAll(stringListEntry.getValue());
        }

        return allPolicyItems;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "OatPolicy{" + "policyItemListMap=" + this.policyItemListMap + ", namne='" + this.name + '\''
            + ", desc='" + this.desc + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final OatPolicy oatPolicy = (OatPolicy) o;
        return this.policyItemListMap.equals(oatPolicy.policyItemListMap) && this.name.equals(oatPolicy.name)
            && this.desc.equals(oatPolicy.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.policyItemListMap, this.name, this.desc);
    }
}
