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

package ohos.oat.document;

import ohos.oat.config.OatProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Document, used to represent the file to be scanned
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatDocument {

    String getName();

    File getFile();

    boolean isProjectRoot();

    void setProjectRoot(boolean projectRoot);

    boolean isDirectory();

    void setDirectory(boolean directory);

    OatProject getOatProject();

    void setOatProject(OatProject oatProject);

    String getFileName();

    String getData(String key);

    void putData(String key, String value);

    List<String> getListData(String key);

    void addListData(String key, String value);

    void copyData(IOatDocument fileDocument);

    InputStream inputStream() throws IOException;

    boolean isArchive();

    void setArchive(boolean archive);

    boolean isBinary();

    void setBinary(boolean binary);

    boolean isReadable();

    void setReadable(boolean readable);

    boolean isLicenseNotes();

    void setLicenseNotes(boolean licenseNotes);

    Status getStatus();

    void setStatus(Status status);

    /**
     * Document status data structure, used to store process details
     *
     * @author chenyaxun
     * @since 2.0
     */
    static class Status {
        public static final String STATUS_NORMAL = "Status.Normal";

        public static final String STATUS_FILTERED = "Status.Filtered";

        public static final String STATUS_SKIPPED = "Status.Skipped";

        private String type = Status.STATUS_NORMAL;

        public String getType() {
            return this.type;
        }

        public void setType(final String type) {
            this.type = type;
        }

    }
}
