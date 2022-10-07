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

package ohos.oat.file.filter;

import ohos.oat.config.OatProject;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * OAT file walker filter，used to filter files while walking files
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatDefaultFileFilter implements IOatFileFilter {

    protected OatProject oatProject;

    /**
     * Init instance
     *
     * @param oatProject OAT project data structure
     */
    @Override
    public IOatFileFilter init(OatProject oatProject) {
        this.oatProject = oatProject;
        return this;
    }

    /**
     * @return FilenameFilter
     */
    @Override
    public FilenameFilter getFilter() {
        final List<String> filterItems = oatProject.getFileFilterObj().getFileFilterNameItems();
        final List<String> excludes = new ArrayList<>();
        for (final String filterItem : filterItems) {
            excludes.add(filterItem.replace(oatProject.getPath(), ""));
        }
        final OrFileFilter orFilter = new OrFileFilter();
 
        for (final String exclude : excludes) {
            // skip comments
            if (exclude.startsWith("#") || StringUtils.isEmpty(exclude)) {
                continue;
            }
            final String exclusion = exclude.trim();
            orFilter.addFileFilter(new NameFileFilter(exclusion));
            orFilter.addFileFilter(new WildcardFileFilter(exclusion));
        }
        return new NotFileFilter(orFilter);
    }
}
