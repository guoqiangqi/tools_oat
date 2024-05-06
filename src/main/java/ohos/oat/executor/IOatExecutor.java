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

package ohos.oat.executor;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.task.IOatTaskProcessor;

import java.io.IOException;
import java.util.List;

/**
 * OAT executor，used to process tasks passed in by OatCommandLine
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatExecutor {

    /**
     * init instance
     *
     * @param oatConfig OAT configuration data structure
     */
    IOatExecutor init(OatConfig oatConfig);

    /**
     * Execute the specified task on the command line
     */
    void execute();

    /**
     * Pass the task to the IOatTaskProcessor handler for execution
     *
     * @param oatTask OAT task
     */
    static void transmit2TaskProcessor(final OatTask oatTask, final OatConfig oatConfig,
        final List<IOatTaskProcessor> oatTaskProcessors) {
        oatTaskProcessors.forEach(oatTaskProcessor -> {
            oatTaskProcessor.init(oatConfig, oatTask);
            try {
                oatTaskProcessor.process();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            oatTaskProcessor.transmit2Analyser();
            oatTaskProcessor.transmit2Reporter();
        });
    }

}
