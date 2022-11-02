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

package ohos.oat.task;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.document.IOatDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default IOatTaskProcessor method implementations
 *
 * @author chenyaxun
 * @since 2.0
 */
public abstract class AbstractOatTaskProcessor implements IOatTaskProcessor {

    protected OatConfig oatConfig = null;

    protected OatTask oatTask;

    protected final Map<Integer, List<IOatDocument>> docMap = new HashMap<>();

    protected final static int THREAD_POOL_SIZE = 16;

    protected int index = 0;

    /**
     * Init instance
     *
     * @param oatConfig OAT configuration data structure
     * @param oatTask OAT task instance
     */
    @Override
    public IOatTaskProcessor init(final OatConfig oatConfig, final OatTask oatTask) {
        this.oatConfig = oatConfig;
        this.oatTask = oatTask;
        for (int i = 0; i < AbstractOatTaskProcessor.THREAD_POOL_SIZE; i++) {
            this.docMap.put(i, new ArrayList<>());
        }
        return this;
    }

    /**
     * Traverse files into OatFileDocument and stored to support subsequent analysis
     *
     * @param oatFileDocument file document
     */
    @Override
    public void addFileDocument(final IOatDocument oatFileDocument) {
        this.docMap.get(this.index).add(oatFileDocument);
        this.index++;
        if (this.index >= AbstractOatTaskProcessor.THREAD_POOL_SIZE) {
            this.index = 0;
        }
    }

}
