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

import ohos.oat.analysis.IOatAnalyser;
import ohos.oat.config.OatConfig;
import ohos.oat.config.OatTask;
import ohos.oat.document.IOatDocument;
import ohos.oat.reporter.IOatReporter;

import java.util.List;

/**
 * Task processor, used to execute Task, one Task corresponds to one IOatTaskProcessor instance
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatTaskProcessor {
    /**
     * Init instance
     *
     * @param oatConfig OAT configuration data structure
     * @param oatTask OAT task instance
     */
    IOatTaskProcessor init(OatConfig oatConfig, OatTask oatTask);

    /**
     * Process task
     */
    void process();

    /**
     * Use IOatAnalyser to analyse document, one Document corresponds to one IOatAnalyser instance
     */
    void transmit2Analyser();

    /**
     * Use IOatReporter to report document, one TaskProcessor corresponds to one IOatReporter instance
     */
    void transmit2Reporter();

    /**
     * @param document OatFileDocument
     * @param oatConfig OAT configuration data structure
     * @param oatAnalysers IOatAnalyser to analyse document
     */
    static void transmit2Analyser(final IOatDocument document, final OatConfig oatConfig,
        final List<IOatAnalyser> oatAnalysers) {
        oatAnalysers.forEach(oatAnalyser -> {
            oatAnalyser.analyse();
        });
    }

    /**
     * @param document OatFileDocument
     * @param oatConfig OAT configuration data structure
     * @param oatReporters IOatReporter to report document
     */
    static void transmit2Reporter(final IOatDocument document, final OatConfig oatConfig,
        final List<IOatReporter> oatReporters) {
        oatReporters.forEach(oatReporter -> {
            oatReporter.report(document);
        });
    }

    /**
     * Write report date to file
     *
     * @param oatReporters IOatReporter to report document
     */
    static void writeReport(final List<IOatReporter> oatReporters) {
        oatReporters.forEach(oatReporter -> oatReporter.writeReport());
    }

    void addFileDocument(IOatDocument document);
}
