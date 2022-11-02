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

import ohos.oat.utils.OatFileUtils;

/**
 * File type analyser, used to analyse the file type and stored the result in document data structure.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatFileTypeAnalyser extends AbstraceOatAnalyser {

    @Override
    public void analyse() {

        if (OatFileUtils.isArchiveFile(this.oatFileDocument)) {
            this.oatFileDocument.setArchive(true);
            this.oatFileDocument.putData("FileType", "archive");
        } else if (OatFileUtils.isBinaryFile(this.oatFileDocument)) {
            this.oatFileDocument.setBinary(true);
            this.oatFileDocument.putData("FileType", "binary");
        } else if (this.oatFileDocument.isDirectory()) {
            this.oatFileDocument.putData("FileType", "directory");
        } else {
            this.oatFileDocument.setReadable(true);
            this.oatFileDocument.putData("FileType", "readable");
            if (OatFileUtils.isNote(this.oatFileDocument)) {
                this.oatFileDocument.setLicenseNotes(true);
            }
        }
    }
}
