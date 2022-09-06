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
 * 2021.2 - Change the file analyse logic to OhosProcessor
 * Modified by jalenchen
 */

package ohos.oat.analysis;

import static org.apache.rat.api.MetaData.RAT_URL_DOCUMENT_CATEGORY;

import ohos.oat.utils.OatFileUtils;

import org.apache.commons.io.IOUtils;
import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.api.MetaData;

import java.io.IOException;
import java.io.Reader;

/**
 * Main analyser of oat
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatHeaderMatchAnalyser extends AbstraceOatAnalyser {

    @Override
    public void analyse() {

        final MetaData.Datum documentCategory;

        if (OatFileUtils.isArchiveFile(this.oatFileDocument) || OatFileUtils.isBinaryFile(this.oatFileDocument)) {
            return;
        } else {
            if (this.oatFileDocument.isDirectory()) {
                documentCategory = new MetaData.Datum(RAT_URL_DOCUMENT_CATEGORY, "Directory");
            } else {
                documentCategory = MetaData.RAT_DOCUMENT_CATEGORY_DATUM_STANDARD;

                Reader reader = null;
                try {
                    reader = this.oatFileDocument.reader();
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
        this.oatFileDocument.getMetaData().set(documentCategory);
    }
}
