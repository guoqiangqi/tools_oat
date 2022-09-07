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

package ohos.oat.excutor;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.task.IOatTaskProcessor;
import ohos.oat.task.OatDefaultTaskProcessor;
import ohos.oat.utils.OatLogUtil;
import ohos.oat.utils.OatSpdxLicenseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OAT excutor，used to check code compatibility
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatComplianceExcutor extends AbstractOatExcutor {

    /**
     * Execute the specified task on the command line
     */
    @Override
    public void excute() {
        OatSpdxLicenseUtil.initSpdxLicenseList(this.oatConfig);
        this.excuteTasks(this.oatConfig);
    }

    /**
     * Output a report in the default style and default license header matcher.
     *
     * @param oatConfig OAT configuration data structure Config in oat.xml
     */
    private void excuteTasks(final OatConfig oatConfig) {
        final List<OatTask> taskList = oatConfig.getTaskList();
        final int size = taskList.size();
        int maxThread = Math.min(size, 100);
        if (maxThread <= 0) {
            maxThread = 1;
        }
        final ExecutorService exec = Executors.newFixedThreadPool(maxThread);
        
        final Date date = new Date();
        final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        final String startTime = simpleDataFormat.format(date);
        OatLogUtil.println("", startTime + " Start analyzing....");

        for (final OatTask oatTask : taskList) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    this.repoort();
                }

                private void repoort() {
                    try {
                        final List<IOatTaskProcessor> taskProcessors = new ArrayList<>();
                        taskProcessors.add(new OatDefaultTaskProcessor());
                        IOatExcutor.transmit2TaskProcessor(oatTask, oatConfig, taskProcessors);
                    } catch (final Exception e) {
                        OatLogUtil.traceException(e);
                    }
                }
            });
        }
        exec.shutdown();
    }

}
