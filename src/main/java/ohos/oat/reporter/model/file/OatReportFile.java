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

    private String title = "";

    private String content = "";

    private String filePath = "";

    private String rule = "";

    private String desc = "";

    public OatReportFile(final IOatDocument oatDocument) {
        this.oatDocument = oatDocument;
        this.setFilePath(oatDocument.getName());
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getRule() {
        return this.rule;
    }

    public void setRule(final String rule) {
        this.rule = rule;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public OatReportFile copy(final String title) {
        return this.copy(title, "");
    }

    public OatReportFile copy(final String title, final String content) {
        return this.copy(title, content, this.rule, this.desc);
    }

    public OatReportFile copy(final String title, final String content, final String rule, final String desc) {
        final OatReportFile oatReportFile = new OatReportFile(this.oatDocument);
        oatReportFile.title = title;
        oatReportFile.content = content;
        oatReportFile.rule = rule;
        oatReportFile.desc = desc;
        return oatReportFile;
    }

    public IOatDocument getOatDocument() {
        return this.oatDocument;
    }

    public void setOatDocument(final IOatDocument oatDocument) {
        this.oatDocument = oatDocument;
    }

}
