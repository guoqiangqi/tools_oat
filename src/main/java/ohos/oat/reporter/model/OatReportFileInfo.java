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

package ohos.oat.reporter.model;

import ohos.oat.reporter.model.file.OatReportFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportFileInfo {

    private final List<OatReportFile> projectNormalFileList = new ArrayList<>();

    private final List<OatReportFile> projectFilteredFileList = new ArrayList<>();

    private final List<OatReportFile> projectFilteredByHeaderFileList = new ArrayList<>();

    private final List<OatReportFile> projectInvalidTypeFileList = new ArrayList<>();

    private int projectFileCount = 0;

    private int projectNormalFileCount = 0;

    private int projectFilteredFileCount = 0;

    private int projectFilteredByHeaderFileCount = 0;

    private int projectInvalidTypeFileCount = 0;

    private boolean hasLicenseFile;

    private boolean hasReadmeFile;

    private boolean hasReadmeOpenSourceFile;

    public int getProjectFileCount() {
        return this.projectFileCount;
    }

    public void setProjectFileCount(final int projectFileCount) {
        this.projectFileCount = projectFileCount;
    }

    public int getProjectNormalFileCount() {
        return this.projectNormalFileCount;
    }

    public void setProjectNormalFileCount(final int projectNormalFileCount) {
        this.projectNormalFileCount = projectNormalFileCount;
    }

    public int getProjectFilteredFileCount() {
        return this.projectFilteredFileCount;
    }

    public void setProjectFilteredFileCount(final int projectFilteredFileCount) {
        this.projectFilteredFileCount = projectFilteredFileCount;
    }

    public int getProjectFilteredByHeaderFileCount() {
        return this.projectFilteredByHeaderFileCount;
    }

    public void setProjectFilteredByHeaderFileCount(final int projectFilteredByHeaderFileCount) {
        this.projectFilteredByHeaderFileCount = projectFilteredByHeaderFileCount;
    }

    public List<OatReportFile> getProjectNormalFileList() {
        return this.projectNormalFileList;
    }

    public void addProjectNormalFile(final OatReportFile file) {
        this.projectNormalFileList.add(file);
        this.projectNormalFileCount++;
        this.projectFileCount++;
    }

    public List<OatReportFile> getProjectFilteredFileList() {
        return this.projectFilteredFileList;
    }

    public void addProjectFilteredFile(final OatReportFile file) {
        this.projectFilteredFileList.add(file);
        this.projectFilteredFileCount++;
        this.projectFileCount++;
    }

    public List<OatReportFile> getProjectFilteredByHeaderFileList() {
        return this.projectFilteredByHeaderFileList;
    }

    public void addProjectFilteredByHeaderFile(final OatReportFile file) {
        this.projectFilteredByHeaderFileList.add(file);
        this.projectFilteredByHeaderFileCount++;
    }

    public int getProjectInvalidTypeFileCount() {
        return this.projectInvalidTypeFileCount;
    }

    public void addProjectInvalidTypeFile(final OatReportFile file) {
        this.projectInvalidTypeFileList.add(file);
        this.projectInvalidTypeFileCount++;
    }

    public boolean isHasLicenseFile() {
        return this.hasLicenseFile;
    }

    public void setHasLicenseFile(final boolean hasLicenseFile) {
        this.hasLicenseFile = hasLicenseFile;
    }

    public boolean isHasReadmeFile() {
        return this.hasReadmeFile;
    }

    public void setHasReadmeFile(final boolean hasReadmeFile) {
        this.hasReadmeFile = hasReadmeFile;
    }

    public boolean isHasReadmeOpenSourceFile() {
        return this.hasReadmeOpenSourceFile;
    }

    public void setHasReadmeOpenSourceFile(final boolean hasReadmeOpenSourceFile) {
        this.hasReadmeOpenSourceFile = hasReadmeOpenSourceFile;
    }
}
