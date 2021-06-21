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

package ohos.oat.report;

import ohos.oat.analysis.OatMainAnalyser;
import ohos.oat.analysis.OatPostAnalyser4Output;
import ohos.oat.config.OatConfig;
import ohos.oat.utils.OatLogUtil;

import org.apache.rat.api.Document;
import org.apache.rat.api.RatException;
import org.apache.rat.document.IDocumentAnalyser;
import org.apache.rat.document.RatDocumentAnalysisException;
import org.apache.rat.report.RatReport;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Analyser and Reporter wrapper class
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatMainReport implements IOatReport {

    // RatReports to out put the analysis result
    private final List<RatReport> ourputReporters;

    private final Map<Integer, List<Document>> docMap = new HashMap<>();

    private final OatConfig oatConfig;

    private final static int threadPoolSize = 16;

    private int index = 0;

    /**
     * Constructor
     *
     * @param oatConfig ohos config information
     * @param writer filewriter to write report
     */
    public OatMainReport(final OatConfig oatConfig, final FileWriter writer) {
        this.oatConfig = oatConfig;
        for (int i = 0; i < threadPoolSize; i++) {
            this.docMap.put(i, new ArrayList<>());
        }

        // Reporters is for output the analysis result
        this.ourputReporters = new ArrayList<>();
        this.ourputReporters.add(new OatOutputReport(writer, oatConfig));
    }

    @Override
    public void startReport() throws RatException {
        for (final RatReport outputReport : this.ourputReporters) {
            outputReport.startReport();
        }
    }

    /**
     * Process report with all analysers and reports
     *
     * @param document file document
     * @throws RatException Exception
     */
    @Override
    public void report(final Document document) throws RatException {
        this.docMap.get(this.index).add(document);
        this.index++;
        if (this.index >= threadPoolSize) {
            this.index = 0;
        }
    }

    @Override
    public void concurrentReport() throws RatException {
        // process in thread pool and the task main thread will wait until the pool finished
        final ExecutorService exec = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            final List<Document> documentList = this.docMap.get(i);
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    for (final Document document : documentList) {
                        OatMainReport.this.processAnalyse(document);
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

        // process in one thread of the task
        for (int i = 0; i < threadPoolSize; i++) {
            final List<Document> documentList = this.docMap.get(i);
            for (final Document document : documentList) {
                this.processReport(document);
            }
        }
    }

    private void processAnalyse(final Document document) {

        // Analyser to analyse the source code
        final IDocumentAnalyser[] analysers = new IDocumentAnalyser[] {
            new OatMainAnalyser(this.oatConfig), new OatPostAnalyser4Output(this.oatConfig)
        };
        try {
            for (final IDocumentAnalyser analyser : analysers) {
                analyser.analyse(document);
            }
        } catch (final RatDocumentAnalysisException e) {
            return;
        }

    }

    private void processReport(final Document document) throws RatException {

        for (final RatReport outputReport : this.ourputReporters) {
            outputReport.report(document);
        }
    }

    @Override
    public void endReport() throws RatException {
        for (final RatReport outputReport : this.ourputReporters) {
            outputReport.endReport();
        }
    }

}
