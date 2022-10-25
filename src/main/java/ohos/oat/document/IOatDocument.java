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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public static final String FILE_STATUS_NORMAL = "Normal";

        public static final String FILE_STATUS_FILTERED = "FileFiltered";

        public static final String FILE_STATUS_FILTERED_BY_HEADER = "FileFilteredByFileHeader";

        private String fileStatus = Status.FILE_STATUS_NORMAL;

        private String fileStatusRule = "";

        private String fileStatusDesc = "";

        private final Map<String, FilteredRule> policyStatusFilteredMap = new HashMap<>();

        public boolean isFileStatusNormal() {
            return Status.FILE_STATUS_NORMAL.equals(this.fileStatus);
        }

        public void setFileStatusFiltered() {
            this.fileStatus = Status.FILE_STATUS_FILTERED;
        }

        public boolean isFileStatusFiltered() {
            return Status.FILE_STATUS_FILTERED.equals(this.fileStatus);
        }

        public void setFileStatusFilteredByHeader() {
            this.fileStatus = Status.FILE_STATUS_FILTERED_BY_HEADER;
        }

        public boolean isFileStatusFilteredByHeader() {
            return Status.FILE_STATUS_FILTERED_BY_HEADER.equals(this.fileStatus);
        }

        public String getFileStatusRule() {
            return this.fileStatusRule;
        }

        public void setFileStatusRule(final String fileStatusRule) {
            this.fileStatusRule = fileStatusRule;
        }

        public String getFileStatusDesc() {
            return this.fileStatusDesc;
        }

        public void setFileStatusDesc(final String fileStatusDesc) {
            this.fileStatusDesc = fileStatusDesc;
        }

        public Map<String, FilteredRule> getPolicyStatusFilteredMap() {
            return this.policyStatusFilteredMap;
        }

        public void addPolicyStatusFilteredRule(final String policyId, final FilteredRule filteredRule) {
            this.policyStatusFilteredMap.put(policyId, filteredRule);
        }

    }

    static class FilteredRule {

        public String getFilePath() {
            return this.filePath;
        }

        public void setFilePath(final String filePath) {
            this.filePath = filePath;
        }

        public String getPolicyType() {
            return this.policyType;
        }

        public void setPolicyType(final String policyType) {
            this.policyType = policyType;
        }

        public String getPolicyName() {
            return this.policyName;
        }

        public void setPolicyName(final String policyName) {
            this.policyName = policyName;
        }

        public String getFilterName() {
            return this.filterName;
        }

        public void setFilterName(final String filterName) {
            this.filterName = filterName;
        }

        public String getFilterItem() {
            return this.filterItem;
        }

        public void setFilterItem(final String filterItem) {
            this.filterItem = filterItem;
        }

        public String getDesc() {
            return this.desc;
        }

        public void setDesc(final String desc) {
            this.desc = desc;
        }

        private String filePath = "";

        private String policyType = "";

        private String policyName = "";

        private String filterName = "";

        private String filterItem = "";

        private String desc = "";

    }
}
