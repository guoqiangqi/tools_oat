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
import java.util.List;

/**
 * Data Structure of file filter defined in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatFileFilter {
    private final List<String> ohosFileFilterItems = new ArrayList<>();

    private final List<String> ohosFilePathFilterItems = new ArrayList<>();

    private String name;

    private String desc;

    /**
     * Merge a filter to another
     *
     * @param fileFilter filter
     */
    public void merge(final OatFileFilter fileFilter) {
        if (fileFilter == null) {
            return;
        }
        for (final String ohosFileFilterItem : fileFilter.ohosFileFilterItems) {
            if (!this.ohosFileFilterItems.contains(ohosFileFilterItem)) {
                this.ohosFileFilterItems.add(ohosFileFilterItem);
            }
        }
        for (final String ohosFilePathFilterItem : fileFilter.ohosFilePathFilterItems) {
            if (!this.ohosFilePathFilterItems.contains(ohosFilePathFilterItem)) {
                this.ohosFilePathFilterItems.add(ohosFilePathFilterItem);
            }
        }
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public List<String> getOhosFilePathFilterItems() {
        return this.ohosFilePathFilterItems;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void addFilterItem(final String filterstr) {
        if (!(filterstr != null && filterstr.trim().length() > 0)) {
            return;
        }
        try {
            final String[] filter = filterstr.split("\\|");
            for (final String filterTxt : filter) {
                if (filterTxt != null && filterTxt.trim().length() > 0) {
                    this.ohosFileFilterItems.add(filterTxt);
                }
            }
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
        }
    }

    public void addFilePathFilterItem(final String filterstr) {
        if (!(filterstr != null && filterstr.trim().length() > 0)) {
            return;
        }
        try {
            final String[] filter = filterstr.split("\\|");
            for (final String filterTxt : filter) {
                if (filterTxt != null && filterTxt.trim().length() > 0) {
                    this.ohosFilePathFilterItems.add(filterTxt);
                }
            }
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
        }
    }

    public List<String> getFileFilterItems() {
        return this.ohosFileFilterItems;
    }

    @Override
    public String toString() {
        return "OhosFileFilter{" + "ohosFileFilterItems=" + this.ohosFileFilterItems + ", ohosFilePathFilterItems="
            + this.ohosFilePathFilterItems + ", namne='" + this.name + '\'' + ", desc='" + this.desc + '\'' + '}';
    }
}
