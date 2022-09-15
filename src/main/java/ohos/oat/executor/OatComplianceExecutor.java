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
import ohos.oat.task.OatDefaultTaskProcessor;
import ohos.oat.utils.OatLogUtil;
import ohos.oat.utils.OatSpdxLicenseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OAT executor，used to check code compatibility
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatComplianceExecutor extends AbstractOatExecutor {

    /**
     * Execute the specified task on the command line
     */
    @Override
    public void execute() {
        OatSpdxLicenseUtil.initSpdxLicenseList(this.oatConfig);
        OatComplianceExecutor.executeTasks(this.oatConfig);
    }

    /**
     * Output a report in the default style and default license header matcher.
     *
     * @param oatConfig OAT configuration data structure Config in oat.xml
     */
    private static void executeTasks(final OatConfig oatConfig) {
        final List<OatTask> taskList = oatConfig.getTaskList();
        final int size = taskList.size();
        // Single-item checks do not need to start a new thread
        if (size <= 1) {
            OatComplianceExecutor.executeTask(taskList.get(0), oatConfig);
            return;
        }
        int maxThread = Math.min(size, 16);
        if (maxThread <= 0) {
            maxThread = 1;
        }
        final ExecutorService exec = Executors.newFixedThreadPool(maxThread);
        for (final OatTask oatTask : taskList) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    OatComplianceExecutor.executeTask(oatTask, oatConfig);
                }

            });
        }
        exec.shutdown();
    }

    private static void executeTask(final OatTask oatTask, final OatConfig oatConfig) {
        try {
            final List<IOatTaskProcessor> taskProcessors = new ArrayList<>();
            taskProcessors.add(new OatDefaultTaskProcessor());
            IOatExecutor.transmit2TaskProcessor(oatTask, oatConfig, taskProcessors);
        } catch (final Exception e) {
            OatLogUtil.traceException(e);
        }
    }

}
