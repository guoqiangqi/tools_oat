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

package ohos.oat.reporter.model.file;

import ohos.oat.document.IOatDocument;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportFile {

    private IOatDocument oatDocument;

    private String fileName = "";

    private String filePath = "";

    private String fileType = "";

    public IOatDocument getOatDocument() {
        return oatDocument;
    }

    public void setOatDocument(IOatDocument oatDocument) {
        this.oatDocument = oatDocument;
    }
    
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return this.fileType;
    }

    public void setFileType(final String fileType) {
        this.fileType = fileType;
    }

}
