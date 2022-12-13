/*
 *
 * Copyright (c) 2022 Huawei Device Co., Ltd.
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
 * 2022.12 -  init filefilterItem, add ref info into Item.
 * Modified by gao liang
 */
package ohos.oat.config;

import java.util.Objects;

public class OatFileFilterItem {
    private String type;
    private String name;
    private String desc;
    private String ref;


    public OatFileFilterItem(String type, String name, String desc, String ref) {
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.ref = ref;
    }


    public String getName() {
        return this.name;
    }
    public String getRef() {
        return this.ref;
    }
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final OatFileFilterItem oatFileFilterItem = (OatFileFilterItem) o;
        return   this.name.equals(oatFileFilterItem.name) && this.type.equals(oatFileFilterItem.type)
                && this.desc.equals(oatFileFilterItem.desc) && this.ref.equals(oatFileFilterItem.ref) ;


    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.name, this.desc,this.ref);
    }
}