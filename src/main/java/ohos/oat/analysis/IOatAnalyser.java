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

package ohos.oat.analysis;

import ohos.oat.config.OatConfig;
import ohos.oat.document.OatFileDocument;

/**
 * IOatAnalyser, used to analyse a file, one file corresponds to one IOatAnalyser instance
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatAnalyser {
 
    /**
     * Init instance
     *
     * @param oatConfig OAT configuration data structure
     * @param oatFileDocument FileDocument to analyse
     */
    IOatAnalyser init(OatConfig oatConfig, OatFileDocument oatFileDocument);

    /**
     * Analyse document
     */
    void analyse();
}
