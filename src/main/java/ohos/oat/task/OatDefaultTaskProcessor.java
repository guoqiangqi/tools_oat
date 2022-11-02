/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
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
 * 2021.1 -  Analyser and Reporter wrapper class:
 * 1. Dispatch the document processing to all the analysers and reporters
 * 2. Triggered by OhosDirectoryWalker.report(final RatReport report, final File file)
 * Modified by jalenchen
 * 2021.5 - Support Scan files of all projects concurrently in one task:
 * 1. Implements from IOhosReport, add report.concurrentReport() method, all time-consuming code analysis processing
 * takes place in this function.
 * 2. Modify the Constructor and init the doc arraylist pools to support concurrent processing.
 * 3. Modify the report() method and move all the analysis process to concurrentReport method.
 * Modified by jalenchen
 */

package ohos.oat.task;

import static ohos.oat.utils.IOatCommonUtils.getTaskDefaultPrjName;

import ohos.oat.analysis.IOatAnalyser;
import ohos.oat.analysis.OatFileTypeAnalyser;
import ohos.oat.analysis.OatHeaderMatchAnalyser;
import ohos.oat.analysis.OatPolicyVerifyAnalyser;
import ohos.oat.config.OatProject;
import ohos.oat.document.IOatDocument;
import ohos.oat.file.IOatFileWalker;
import ohos.oat.file.OatProjectWalker;
import ohos.oat.reporter.IOatReporter;
import ohos.oat.reporter.OatDetailPlainReporter;
import ohos.oat.reporter.OatPlainReporter;
import ohos.oat.utils.OatLogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation, by concurrently traversing the files in each Project in the Task and analyzing each file
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatDefaultTaskProcessor extends AbstractOatTaskProcessor {

    private final List<IOatReporter> oatReporters = new ArrayList<>();

    private String costTimeAnalyse = "";

    /**
     * Constructor
     */
    public OatDefaultTaskProcessor() {

    }

    /**
     * Process task
     */
    @Override
    public void process() {
        // init reporter first, ensure report file time is consistent
        final IOatReporter oatReporter = new OatPlainReporter();
        oatReporter.init(this.oatConfig, this.oatTask);
        final IOatReporter oatDetailReporter = new OatDetailPlainReporter();
        oatDetailReporter.init(this.oatConfig, this.oatTask);
        this.oatReporters.add(oatReporter);
        this.oatReporters.add(oatDetailReporter);

        final long startTime = System.currentTimeMillis();

        final List<OatProject> projectList = this.oatTask.getProjectList();
        for (final OatProject oatProject : projectList) {
            final IOatFileWalker fileWalker = new OatProjectWalker();
            fileWalker.init(this.oatConfig, this);
            fileWalker.walkProject(oatProject);

        }

        final long costTime = (System.currentTimeMillis() - startTime) / 1000;
        final String taskDefaultPrjName = getTaskDefaultPrjName(this.oatTask);
        OatLogUtil.warn(this.getClass().getSimpleName(), taskDefaultPrjName + "\tWalker task costTime\t" + costTime);

    }

    /**
     * Use IOatAnalyser to analyse document, one Document corresponds to one IOatAnalyser instance
     */
    @Override
    public void transmit2Analyser() {
        // process in thread pool and the task main thread will wait until the pool finished
        final ExecutorService exec = Executors.newFixedThreadPool(AbstractOatTaskProcessor.THREAD_POOL_SIZE);
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < AbstractOatTaskProcessor.THREAD_POOL_SIZE; i++) {
            final List<IOatDocument> oatFileDocuments = this.docMap.get(i);
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    for (final IOatDocument oatFileDocument : oatFileDocuments) {
                        if (!oatFileDocument.getStatus().isFileStatusNormal()) {
                            continue;
                        }
                        final List<IOatAnalyser> oatAnalysers = new ArrayList<>();
                        final IOatAnalyser oatFileTypeAnalyser = new OatFileTypeAnalyser();
                        oatFileTypeAnalyser.init(OatDefaultTaskProcessor.this.oatConfig, oatFileDocument);
                        final IOatAnalyser oatHeaderMatchAnalyser = new OatHeaderMatchAnalyser();
                        oatHeaderMatchAnalyser.init(OatDefaultTaskProcessor.this.oatConfig, oatFileDocument);
                        final IOatAnalyser oatPolicyVerifyAnalyser = new OatPolicyVerifyAnalyser();
                        oatPolicyVerifyAnalyser.init(OatDefaultTaskProcessor.this.oatConfig, oatFileDocument);
                        oatAnalysers.add(oatFileTypeAnalyser);
                        oatAnalysers.add(oatHeaderMatchAnalyser);
                        oatAnalysers.add(oatPolicyVerifyAnalyser);
                        IOatTaskProcessor.transmit2Analyser(oatFileDocument, OatDefaultTaskProcessor.this.oatConfig,
                            oatAnalysers);
                    }
                }
            });
        }
        exec.shutdown();
        try {
            // wait the pool until finish
            while (!exec.awaitTermination(3, TimeUnit.SECONDS)) {
            }
        } catch (final InterruptedException e) {
            OatLogUtil.traceException(e);
        }
        final long costTime = (System.currentTimeMillis() - startTime) / 1000;
        final String taskDefaultPrjName = getTaskDefaultPrjName(this.oatTask);
        OatLogUtil.warn(this.getClass().getSimpleName(), taskDefaultPrjName + "\tAnalyse task costTime\t" + costTime);
        this.costTimeAnalyse = "" + costTime;
    }

    /**
     * Use IOatReporter to report document, one TaskProcessor corresponds to one IOatReporter instance
     */
    @Override
    public void transmit2Reporter() {
        // process in one thread of the task
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < AbstractOatTaskProcessor.THREAD_POOL_SIZE; i++) {
            final List<IOatDocument> oatFileDocuments = this.docMap.get(i);
            for (final IOatDocument oatFileDocument : oatFileDocuments) {
                IOatTaskProcessor.transmit2Reporter(oatFileDocument, this.oatConfig, this.oatReporters);
            }
        }
        IOatTaskProcessor.writeReport(this.oatReporters);
        final long costTime = (System.currentTimeMillis() - startTime) / 1000;
        final String taskDefaultPrjName = getTaskDefaultPrjName(this.oatTask);
        OatLogUtil.warn(this.getClass().getSimpleName(), taskDefaultPrjName + "\tReport task costTime\t" + costTime);
        OatLogUtil.println("",
            taskDefaultPrjName + " cost time(Analyse|Report):\t" + this.costTimeAnalyse + "|" + costTime);
    }

}
