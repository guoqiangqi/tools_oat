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

import ohos.oat.config.OhosConfig;
import ohos.oat.document.OhosFileDocument;
import ohos.oat.utils.OhosFileUtils;

import org.apache.commons.io.IOUtils;
import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;
import org.apache.rat.document.IDocumentAnalyser;
import org.apache.rat.document.RatDocumentAnalysisException;

import java.io.IOException;
import java.io.Reader;

/**
 * Main analyser of oat
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosMainAnalyser implements IDocumentAnalyser {
    private final OhosConfig ohosConfig;

    public OhosMainAnalyser(final OhosConfig ohosConfig) {
        super();
        this.ohosConfig = ohosConfig;
    }

    @Override
    public void analyse(final Document subject) throws RatDocumentAnalysisException {
        OhosFileDocument document = null;
        if (subject instanceof OhosFileDocument) {
            document = (OhosFileDocument) subject;
        }
        final MetaData.Datum documentCategory;

        if (OhosFileUtils.isArchiveFile(subject) || OhosFileUtils.isBinaryFile(subject)) {
            return;
        } else {
            if (document.isDirectory()) {
                documentCategory = new MetaData.Datum(RAT_URL_DOCUMENT_CATEGORY, "Directory");
            } else {
                documentCategory = MetaData.RAT_DOCUMENT_CATEGORY_DATUM_STANDARD;

                Reader reader = null;
                try {
                    reader = subject.reader();
                    final OhosProcessor worker = new OhosProcessor(reader, subject, this.ohosConfig);
                    worker.read();
                } catch (final IOException e) {
                    throw new RatDocumentAnalysisException("Cannot read header", e);
                } catch (final RatHeaderAnalysisException e) {
                    throw new RatDocumentAnalysisException("Cannot analyse header", e);
                } finally {
                    IOUtils.closeQuietly(reader);
                }
            }
        }
        subject.getMetaData().set(documentCategory);
    }
}
