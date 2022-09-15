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

package ohos.oat.utils;

import ohos.oat.config.OatProject;
import ohos.oat.config.OatTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Common tool
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatCommonUtils {

    @NotNull
    static String getDateTimeString() {
        final Date date = new Date();
        final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        final String startTime = simpleDataFormat.format(date);
        return startTime;
    }

    static String getTaskDefaultPrjName(final OatTask oatTask) {
        String filePrefix = oatTask.getNamne();
        final List<OatProject> oatProjects = oatTask.getProjectList();
        if (oatProjects.size() == 1) {
            filePrefix = oatProjects.get(0).getName();
        }
        return filePrefix;
    }

}
