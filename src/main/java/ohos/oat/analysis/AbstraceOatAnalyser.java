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
import ohos.oat.document.IOatDocument;

/**
 * Default IOatAnalyser method implementations
 *
 * @author chenyaxun
 * @since 2.0
 */
public abstract class AbstraceOatAnalyser implements IOatAnalyser {
    protected OatConfig oatConfig;

    protected IOatDocument oatFileDocument;

    /**
     * Init instance
     *
     * @param oatConfig OAT configuration data structure
     * @param oatFileDocument FileDocument to analyse
     */
    @Override
    public IOatAnalyser init(final OatConfig oatConfig, final IOatDocument oatFileDocument) {
        this.oatConfig = oatConfig;
        this.oatFileDocument = oatFileDocument;
        return this;
    }
}
