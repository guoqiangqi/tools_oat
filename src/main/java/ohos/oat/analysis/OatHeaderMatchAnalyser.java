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
 * 2021.2 - Change the file analyse logic to OhosProcessor
 * Modified by jalenchen
 */

package ohos.oat.analysis;

import org.apache.commons.io.IOUtils;
import org.apache.rat.analysis.RatHeaderAnalysisException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Header match analyser, all source file content will be analysed and stored the result in document data structure.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatHeaderMatchAnalyser extends AbstraceOatAnalyser {

    @Override
    public void analyse() {

        if (this.oatFileDocument.isReadable()) {
            Reader reader = null;
            try {
                reader = new FileReader(this.oatFileDocument.getFile());
                final OatFileAnalyser worker = new OatFileAnalyser(reader, this.oatFileDocument, this.oatConfig);
                worker.read();
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final RatHeaderAnalysisException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

    }
}
