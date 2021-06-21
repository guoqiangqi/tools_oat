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
 * 2021.3 - Add repository name and run mode to support integration with pipleline tools.
 * 2021.4 - Add license text map to Support support user defined license match rules.
 * 2021.5 - Enhance extensibility: Change the calling policy.getXXXPolicyItems() to policy.getAllPolicyItems.
 * Modified by jalenchen
 *
 */

package ohos.oat.config;

import ohos.oat.analysis.headermatcher.OatLicense;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLogUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Structure of oat tool configuration defined in OAT.xml, this contains all information in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatConfig {
    private final Map<String, OatFileFilter> fileFilterMap;

    private final Map<String, OatPolicy> policyMap;

    private final List<OatTask> taskList;

    private final Map<String, List<String>> licenseText2NameMap;

    private final Map<String, List<String>> licenseCompatibilityMap;

    private String basedir;

    private String repositoryName;

    private List<OatLicense> licenseList;

    private List<OatLicense> exceptionLicenseList;

    private boolean isPluginMode = false;

    private final Map<String, String> data = new HashMap<>();

    private String pluginCheckMode = "0";

    private final List<String> srcFileList;

    public OatConfig() {
        this.policyMap = new HashMap<>();
        this.fileFilterMap = new HashMap<>();
        this.taskList = new ArrayList<>();
        this.licenseText2NameMap = new HashMap<>();
        this.licenseCompatibilityMap = new HashMap<>();
        this.srcFileList = new ArrayList<>();
    }

    public void setPluginCheckMode(final String pluginCheckMode) {
        this.pluginCheckMode = pluginCheckMode;
    }

    public String getData(final String key) {
        final String tmp = this.data.get(key);
        return tmp == null ? "" : tmp;
    }

    public void putData(final String key, final String value) {
        this.data.put(key, value);
    }

    public void setSrcFileList(final String srcFileList) {
        if (srcFileList == null || srcFileList.length() <= 0) {
            return;
        }
        final String[] list = OatCfgUtil.getSplitStrings(srcFileList);
        for (final String filePath : list) {
            if (null == filePath || filePath.trim().equals("")) {
                continue;
            }
            String tmpStr = filePath;

            tmpStr = tmpStr.replace("\\", "/");
            tmpStr = tmpStr.replace("//", "/");
            this.srcFileList.add(tmpStr);
            OatLogUtil.warn(this.getClass().getSimpleName(), "CommandLine" + "\taddSrcListItem\t" + tmpStr);
        }
    }

    public boolean needCheck(final File file) {
        if (!this.pluginCheckMode.equals("1")) {
            return true;
        }
        String absolutePath = "";
        try {
            absolutePath = file.getCanonicalPath();
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
        final String formatedPath = absolutePath.replace("\\", "/");
        for (final String srcFile : this.srcFileList) {
            if (srcFile.startsWith(formatedPath)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, List<String>> getLicenseText2NameMap() {
        return this.licenseText2NameMap;
    }

    public Map<String, List<String>> getLicenseCompatibilityMap() {
        return this.licenseCompatibilityMap;
    }

    public OatFileFilter getOatFileFilter(final String filterName) {
        return this.fileFilterMap.get(filterName);
    }

    public boolean isPluginMode() {
        return this.isPluginMode;
    }

    public void setPluginMode(final boolean pluginMode) {
        this.isPluginMode = pluginMode;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public void setRepositoryName(final String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public void addFileFilter(final OatFileFilter fileFilter) {
        final OatFileFilter oatFileFilter = this.fileFilterMap.get(fileFilter.getName());
        if (oatFileFilter == null) {
            this.fileFilterMap.put(fileFilter.getName(), fileFilter);
        }
    }

    public void addPolicy(final OatPolicy oatPolicy) {
        final OatPolicy oatPolicy1 = this.policyMap.get(oatPolicy.getNamne());
        if (oatPolicy1 == null) {
            this.policyMap.put(oatPolicy.getNamne(), oatPolicy);
        }
    }

    public void addTask(final OatTask oatTask) {
        this.taskList.add(oatTask);
    }

    public void addLicenseText(final String licenseName, final String licenseText) {
        List<String> licenseTextList = this.licenseText2NameMap.get(licenseName);
        if (licenseTextList == null) {
            licenseTextList = new ArrayList<>();
            licenseTextList.add(licenseText);
            this.licenseText2NameMap.put(licenseName, licenseTextList);
        } else {
            licenseTextList.add(licenseText);
        }
    }

    public void addCompatibilityLicense(final String licenseName, final String compatibilityLicense) {
        List<String> compatibilityLicenseList = this.licenseCompatibilityMap.get(licenseName);
        if (compatibilityLicenseList == null) {
            compatibilityLicenseList = new ArrayList<>();
            compatibilityLicenseList.add(compatibilityLicense);
            this.licenseCompatibilityMap.put(licenseName, compatibilityLicenseList);
        } else {
            compatibilityLicenseList.add(compatibilityLicense);
        }
    }

    public String getBasedir() {
        return this.basedir;
    }

    public void setBasedir(final String basedir) {
        String tmpDir = basedir.replace('\\', '/');
        if (!tmpDir.endsWith("/")) {
            tmpDir = tmpDir + "/";
        }
        this.basedir = tmpDir;
    }

    public List<OatLicense> getLicenseList() {
        return this.licenseList;
    }

    public void setLicenseList(final List<OatLicense> licenseList) {
        this.licenseList = licenseList;
    }

    public List<OatLicense> getExceptionLicenseList() {
        return this.exceptionLicenseList;
    }

    public void setExceptionLicenseList(final List<OatLicense> exceptionLicenseList) {
        this.exceptionLicenseList = exceptionLicenseList;
    }

    public List<OatTask> getTaskList() {
        return this.taskList;
    }

    public void reArrangeData() {
        for (final OatTask oatTask : this.taskList) {
            final String policyName = oatTask.getPolicy();
            final OatPolicy policy = this.policyMap.get(policyName);
            if (policy == null) {
                continue;
            }
            oatTask.setPolicyData(policy);
            final String fileFilter = oatTask.getFileFilter();
            if (fileFilter == null) {
                continue;
            }
            final OatFileFilter fileFilterObj = this.fileFilterMap.get(fileFilter);
            oatTask.setFileFilterObj(fileFilterObj);

            final List<OatProject> projectList = oatTask.getProjectList();
            for (int i = 0; i < projectList.size(); i++) {
                final OatProject oatProject = projectList.get(i);
                final String prjFileFilter = oatProject.getFileFilter();
                final String prjPolicy = oatProject.getPolicy();
                if (prjFileFilter == null || prjFileFilter.trim().equals("")) {
                    oatProject.setFileFilterObj(fileFilterObj);
                    oatProject.setFileFilter(fileFilterObj.getName());
                } else {
                    oatProject.setFileFilterObj(this.fileFilterMap.get(prjFileFilter));
                }
                if (prjPolicy == null || prjPolicy.trim().equals("")) {
                    oatProject.setOatPolicy(policy);
                    oatProject.setPolicy(policy.getNamne());
                } else {
                    oatProject.setOatPolicy(this.policyMap.get(prjPolicy));
                }
            }
            oatTask.reArrangeProject();
        }

        for (final OatPolicy policy : this.policyMap.values()) {
            this.bindFilterToPolicyItem(policy.getAllPolicyItems());
        }
    }

    private void bindFilterToPolicyItem(final List<OatPolicyItem> policyItems) {
        for (final OatPolicyItem policyItem : policyItems) {
            final String fileFilter = policyItem.getFileFilter();
            final OatFileFilter fileFilterObj = this.fileFilterMap.get(fileFilter);
            policyItem.setFileFilterObj(fileFilterObj);
        }
    }

}
