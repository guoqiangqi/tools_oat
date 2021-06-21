/*
 *
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
 * 2021.3 -  Add policy and filter field and change the owner of policy and filter from task to projects.
 * Modified by jalenchen
 * 2021.5 - Support Scan files of all projects concurrently and fix bugs:
 * 1. Modify getProjectFileDocument() method, add a temp doc to the project, because the sub directory files are
 * processed before the project document,this tmp document is used to store project license file data.
 * 2. Modify setProjectFileDocument() method, add calling projectFileDocument.copyData(this.projectFileDocument) to
 * merge the tmp document data to the real project document.
 * Modified by jalenchen
 */

package ohos.oat.config;

import ohos.oat.document.OatFileDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Structure of oat scanning project defined in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatProject {
    private final Map<String, List<String>> prjLicenseText2NameMap = new HashMap<>();

    private final Map<String, List<String>> prjLicenseCompatibilityMap = new HashMap<>();

    private final List<OatProject> includedPrjList = new ArrayList<>();

    private OatPolicy oatPolicy;

    private String name;

    private String[] licenseFiles;

    private String path;

    private OatFileFilter fileFilterObj;

    private String fileFilter;

    private String policy;

    private OatFileDocument projectFileDocument;

    public OatFileDocument getProjectFileDocument() {
        if (this.projectFileDocument == null) {
            // Because the sub directory files are processed before the project file,this is tmp object
            this.projectFileDocument = new OatFileDocument(new File("temp.txt"));
        }
        return this.projectFileDocument;
    }

    public void setProjectFileDocument(final OatFileDocument projectFileDocument) {
        if (this.projectFileDocument != null) {
            projectFileDocument.copyData(this.projectFileDocument);
        }
        this.projectFileDocument = projectFileDocument;
    }

    public String[] getLicenseFiles() {
        return this.licenseFiles;
    }

    public void setLicenseFiles(final String[] licenseFiles) {
        this.licenseFiles = licenseFiles;
    }

    public OatFileFilter getFileFilterObj() {
        return this.fileFilterObj;
    }

    public void setFileFilterObj(final OatFileFilter fileFilterObj) {
        this.fileFilterObj = fileFilterObj;
    }

    public OatPolicy getOhosPolicy() {
        return this.oatPolicy;
    }

    public void setOhosPolicy(final OatPolicy oatPolicy) {
        this.oatPolicy = oatPolicy;
    }

    public List<OatProject> getIncludedPrjList() {
        return this.includedPrjList;
    }

    public void addIncludedPrj(final OatProject includedPrj) {
        this.includedPrjList.add(includedPrj);
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getFileFilter() {
        return this.fileFilter;
    }

    public void setFileFilter(final String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public String getPolicy() {
        return this.policy;
    }

    public void setPolicy(final String policy) {
        this.policy = policy;
    }

    public Map<String, List<String>> getPrjLicenseText2NameMap() {
        return this.prjLicenseText2NameMap;
    }

    public void addPrjLicenseText(final String licenseName, final String licenseText) {
        List<String> licenseTextList = this.prjLicenseText2NameMap.get(licenseName);
        if (licenseTextList == null) {
            licenseTextList = new ArrayList<>();
            licenseTextList.add(licenseText);
            this.prjLicenseText2NameMap.put(licenseName, licenseTextList);
        } else {
            licenseTextList.add(licenseText);
        }
    }

    public void addPrjCompatibilityLicense(final String licenseName, final String compatibilityLicense) {
        List<String> compatibilityLicenseList = this.prjLicenseCompatibilityMap.get(licenseName);
        if (compatibilityLicenseList == null) {
            compatibilityLicenseList = new ArrayList<>();
            compatibilityLicenseList.add(compatibilityLicense);
            this.prjLicenseCompatibilityMap.put(licenseName, compatibilityLicenseList);
        } else {
            compatibilityLicenseList.add(compatibilityLicense);
        }
    }

    public Map<String, List<String>> getPrjLicenseCompatibilityMap() {
        return this.prjLicenseCompatibilityMap;
    }

    @Override
    public String toString() {
        return "OhosProject{" + "namne='" + this.name + '\'' + ", licenseFiles=" + Arrays.toString(this.licenseFiles)
            + ", path='" + this.path + '\'' + ", fileFilter='" + this.fileFilter + '\'' + ", policy='" + this.policy
            + '\'' + '}';
    }
}
