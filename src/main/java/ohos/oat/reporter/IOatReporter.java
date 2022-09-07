/*
 * Copyright (c) 2021-2022 Huawei Device Co., Ltd.
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
 * 2021.5 - Add new interface to support concurrent reporting process.
 * Modified by jalenchen
 * 2022.8 - Decouple the analyzer from the report display part, this interface is dedicated to reporting
 */

package ohos.oat.reporter;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.document.OatFileDocument;

/**
 * IOatReporter, used to generate a report from the analysis results
 * one Task corresponds to one IOatReporter instance
 *
 * @author chenyaxun
 * @since 1.0
 */
public interface IOatReporter {

    /**
     * @param oatConfig OAT configuration data structure
     * @param oatTask Task
     */

    IOatReporter init(final OatConfig oatConfig, final OatTask oatTask);

    /**
     * Report document
     *
     * @param oatFileDocument OatFileDocument
     */
    void report(OatFileDocument oatFileDocument);

    /**
     * Write report to file
     */
    void writeReport();

}
