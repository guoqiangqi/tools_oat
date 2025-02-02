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
 * 2021.3 -  Add file path filter type to support filter specified directory.
 * 2021.4 -  Add merge method to support merge filter obj with another to support merge project OAT.xml to default
 * OAT.xml.
 * Modified by jalenchen
 */

package ohos.oat.config;

import ohos.oat.utils.OatLogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data Structure of file filter defined in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatFileFilter {
    private final List<OatFileFilterItem> oatFileFilterItems = new ArrayList<>();

    private final List<OatFileFilterItem> oatFilePathFilterItems = new ArrayList<>();

    private final Map<String, String> fileFilter2Desc = new HashMap<>();

    private final Map<String, String> filePathFilter2Desc = new HashMap<>();

    private String name;

    private String desc;

    public OatFileFilter() {

    }

    private List<String> getOatFileFilterNameItems( List<OatFileFilterItem> oatFileFilterItems)
    {
        List<String> oatFileFilterNameItem = new ArrayList<>();
        for (final OatFileFilterItem oatFilePathFilterItem : oatFileFilterItems) {
            oatFileFilterNameItem.add(oatFilePathFilterItem.getName());
        }
        return oatFileFilterNameItem;
    }

    public OatFileFilter(final String name, final String desc) {
        this.setName(name);
        this.setDesc(desc);
    }

    /**
     * Merge a filter to another
     *
     * @param fileFilter filter
     */
    public void merge(final OatFileFilter fileFilter) {
        if (fileFilter == null) {
            return;
        }
        for (final OatFileFilterItem oatFileFilterItem : fileFilter.oatFileFilterItems) {
            if (!this.oatFileFilterItems.contains(oatFileFilterItem)) {
                this.oatFileFilterItems.add(oatFileFilterItem);
            }
        }
        for (final OatFileFilterItem oatFilePathFilterItem : fileFilter.oatFilePathFilterItems) {
            if (!this.oatFilePathFilterItems.contains(oatFilePathFilterItem)) {
                this.oatFilePathFilterItems.add(oatFilePathFilterItem);
            }
        }
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public List<String> getOatFilePathFilterNameItems() {
        return this.getOatFileFilterNameItems(this.oatFilePathFilterItems);
    }

    public List<OatFileFilterItem> getOatFilePathFilterItems() {
        return this.oatFilePathFilterItems;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void addFilterItem(final String filterstr, final String desc) {
        this.addFilterItem("", filterstr, desc,"");
    }

    public void addFilterItem(String type, String fileName, String desc, String ref) {
        this.addFilterItem("",type,fileName,desc,ref);
    }

    public void addFilterItem(final String projectPath, final String type, final String filterStr, final String desc, final String ref) {
        if (!(filterStr != null && filterStr.trim().length() > 0)) {
            return;
        }
        try {
            final String[] filter = filterStr.split("\\|");
            for (final String filterTxt : filter) {
                if (filterTxt != null && filterTxt.trim().length() > 0) {
                    this.oatFileFilterItems.add(new OatFileFilterItem(type, projectPath + filterTxt, desc, ref));
                    this.fileFilter2Desc.put(projectPath + filterTxt, desc);
                }
            }
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
        }
    }

    public void addFilePathFilterItem(final String filterstr, final String desc) {
            this.addFilePathFilterItem("",filterstr,desc,"");
        }
    public void addFilePathFilterItem(final String type, final String filterStr, final String desc, final String ref) {
        if (!(filterStr != null && filterStr.trim().length() > 0)) {
            return;
        }
        try {
            final String[] filter = filterStr.split("\\|");
            for (final String filterTxt : filter) {
                if (filterTxt != null && filterTxt.trim().length() > 0) {
                    this.oatFilePathFilterItems.add(new OatFileFilterItem(type,filterTxt,desc,ref));
                    this.filePathFilter2Desc.put(filterTxt, desc);
                }
            }
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
        }
    }

    public List<String> getFileFilterNameItems() {
        return this.getOatFileFilterNameItems(this.oatFileFilterItems);
    }

    public List<OatFileFilterItem> getFileFilterItems() {
        return this.oatFileFilterItems;
    }

    public String getFileFilterDesc(final String fileFilter) {
        if (fileFilter.length() == 0) {
            return "";
        }
        final String desc = this.fileFilter2Desc.get(fileFilter);
        return desc == null ? "" : desc;
    }

    public boolean IsRefInfoInvalidWhenBinaryFileFilter(OatFileFilterItem fileFilterItem) {
        if ( this.getName().equals("binaryFileTypePolicyFilter") ) {
            if (fileFilterItem.getRef().equals("")) {
                return true;
            }
        }
        return false;
    }

    public String getFilePathFilterDesc(final String filePathFilter) {
        if (filePathFilter.length() == 0) {
            return this.filePathFilter2Desc.toString();
        }
        final String desc = this.filePathFilter2Desc.get(filePathFilter);
        return desc == null ? "" : desc;
    }

    @Override
    public String toString() {
        return "OatFileFilter{" + "oatFileFilterItems=" + this.oatFileFilterItems + ", oatFilePathFilterItems="
            + this.oatFilePathFilterItems + ", name='" + this.name + '\'' + ", desc='" + this.desc + '\'' + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final OatFileFilter that = (OatFileFilter) o;
        return this.oatFileFilterItems.equals(that.oatFileFilterItems) && this.oatFilePathFilterItems.equals(
            that.oatFilePathFilterItems) && this.name.equals(that.name) && this.desc.equals(that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.oatFileFilterItems, this.oatFilePathFilterItems, this.name, this.desc);
    }
}
