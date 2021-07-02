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
 * 2021.3 -  Add filter object field and bind the filter object to ohos policy item during program initialization.
 * Modified by jalenchen
 */

package ohos.oat.config;

import java.util.Objects;

/**
 * Data Structure of policy items defined in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatPolicyItem {
    private String name;

    private String type;

    private String path;

    private String rule;

    private String group;

    private String desc;

    private OatFileFilter fileFilterObj;

    private String fileFilter;

    public OatPolicyItem() {

    }

    public OatPolicyItem(final String type, final String name, final String path, final String rule, final String group,
        final String fileFilter, final String desc) {
        this.setType(type);
        this.setName(name);
        this.setPath(path);
        this.setRule(rule);
        this.setGroup(group);
        this.setFileFilter(fileFilter);
        this.setDesc(desc);
    }

    public OatPolicyItem(final String type, final String name, final String path, final String rule, final String group,
        final String fileFilter, final String desc, final OatFileFilter fileFilterObj) {
        this(type, name, path, rule, group, fileFilter, desc);
        this.setFileFilterObj(fileFilterObj);
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(final String rule) {
        this.rule = rule;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public OatFileFilter getFileFilterObj() {
        return this.fileFilterObj;
    }

    public void setFileFilterObj(final OatFileFilter fileFilterObj) {
        this.fileFilterObj = fileFilterObj;
    }

    public String getFileFilter() {
        return this.fileFilter;
    }

    public void setFileFilter(final String fileFilter) {
        this.fileFilter = fileFilter;
    }

    @Override
    public String toString() {
        return "OatPolicyItem{" + "name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", path='" + this.path
            + '\'' + ", rule='" + this.rule + '\'' + ", group='" + this.group + '\'' + ", desc='" + this.desc + '\''
            + ", fileFilterObj=" + this.fileFilterObj + ", fileFilter='" + this.fileFilter + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final OatPolicyItem that = (OatPolicyItem) o;
        return this.name.equals(that.name) && this.type.equals(that.type) && this.path.equals(that.path)
            && this.rule.equals(that.rule) && this.group.equals(that.group) && this.desc.equals(that.desc)
            && this.fileFilterObj.equals(that.fileFilterObj) && this.fileFilter.equals(that.fileFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.type, this.path, this.rule, this.group, this.desc, this.fileFilterObj,
            this.fileFilter);
    }
}
