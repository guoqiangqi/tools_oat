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

        public static final String FILE_STATUS_FILTERED_BY_COMMON = "FileFilteredByCommon";

        public static final String FILE_STATUS_FILTERED_BY_PROJECT = "FileFilteredByProject";

        public static final String FILE_STATUS_FILTERED_BY_HEADER = "FileFilteredByFileHeader";

        public static final String POLICY_STATUS_PASSED_BY_COMMON = "PolicyPassedByCommon";

        public static final String POLICY_STATUS_PASSED_BY_PROJECT = "PolicyPassedByProject";

        public static final String POLICY_STATUS_PASSED_BY_FILTER = "PolicyPassedByFilter";

        public static final String POLICY_STATUS_NO_PASSED = "PolicyNoPassed";

        private String fileStatus = Status.FILE_STATUS_NORMAL;

        private String fileStatusRule = "";

        private String fileStatusDesc = "";

        private final Map<String, String> policyStatusMap = new HashMap<>();

        public String getFileStatus() {
            return this.fileStatus;
        }

        public void setFileStatusNormal() {
            this.fileStatus = Status.FILE_STATUS_NORMAL;
        }

        public boolean isFileStatusNormal() {
            return Status.FILE_STATUS_NORMAL.equals(this.fileStatus);
        }

        public void setFileStatusFiltered() {
            this.fileStatus = Status.FILE_STATUS_FILTERED;
        }

        public boolean isFileStatusFiltered() {
            return Status.FILE_STATUS_FILTERED.equals(this.fileStatus);
        }

        public void setFileStatusFilteredByCommon() {
            this.fileStatus = Status.FILE_STATUS_FILTERED_BY_COMMON;
        }

        public boolean isFileStatusFilteredByCommon() {
            return Status.FILE_STATUS_FILTERED_BY_COMMON.equals(this.fileStatus);
        }

        public void setFileStatusFilteredByProject() {
            this.fileStatus = Status.FILE_STATUS_FILTERED_BY_PROJECT;
        }

        public boolean isFileStatusFilteredByProject() {
            return Status.FILE_STATUS_FILTERED_BY_PROJECT.equals(this.fileStatus);
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

        public String getPolicyStatus(final String policyId) {
            final String status = this.policyStatusMap.get(policyId);
            return status == null ? "" : status;
        }

        public void setPolicyStatusData(final String policyIdDataKey, final String value) {
            this.policyStatusMap.put(policyIdDataKey, value);
        }

        public void setPolicyStatusPassedByCommon(final String policyId) {
            this.policyStatusMap.put(policyId, Status.POLICY_STATUS_PASSED_BY_COMMON);
        }

        public void setPolicyStatusPassedByProject(final String policyId) {
            this.policyStatusMap.put(policyId, Status.POLICY_STATUS_PASSED_BY_PROJECT);
        }

        public void setPolicyStatusPassedByFilter(final String policyId) {
            this.policyStatusMap.put(policyId, Status.POLICY_STATUS_PASSED_BY_FILTER);
        }

        public void setPolicyStatusNoPassed(final String policyId) {
            this.policyStatusMap.put(policyId, Status.POLICY_STATUS_NO_PASSED);
        }

    }
}
