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

import java.io.FilenameFilter;

/**
 * OAT file walker filter，used to filter files while walking files
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatFileFilter {
    /**
     * Init instance
     *
     * @param oatProject OAT project data structure
     */
    IOatFileFilter init(OatProject oatProject);

    /**
     * @return FilenameFilter
     */
    FilenameFilter getFilter();
}
