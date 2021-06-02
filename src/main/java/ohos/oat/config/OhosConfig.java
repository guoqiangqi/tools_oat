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

import ohos.oat.analysis.headermatcher.OhosLicense;
import ohos.oat.utils.OhosCfgUtil;
import ohos.oat.utils.OhosLogUtil;

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
public class OhosConfig {
    private final Map<String, OhosFileFilter> fileFilterMap;

    private final Map<String, OhosPolicy> policyMap;

    private final List<OhosTask> taskList;

    private final Map<String, List<String>> licenseText2NameMap;

    private String basedir;

    private String repositoryName;

    private List<OhosLicense> licenseList;

    private boolean isPluginMode = false;

    private final Map<String, String> data = new HashMap<>();

    private String pluginCheckMode = "0";

    private final List<String> srcFileList;

    public OhosConfig() {
        this.policyMap = new HashMap<>();
        this.fileFilterMap = new HashMap<>();
        this.taskList = new ArrayList<>();
        this.licenseText2NameMap = new HashMap<>();
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
        final String[] list = OhosCfgUtil.getSplitStrings(srcFileList);
        for (final String filePath : list) {
            if (null == filePath || filePath.trim().equals("")) {
                continue;
            }
            String tmpStr = filePath;

            tmpStr = tmpStr.replace("\\", "/");
            tmpStr = tmpStr.replace("//", "/");
            this.srcFileList.add(tmpStr);
            OhosLogUtil.warn(this.getClass(), "CommandLine" + "\taddSrcListItem\t" + tmpStr);
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
            OhosLogUtil.traceException(e);
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

    public OhosFileFilter getOhosFileFilter(final String filterName) {
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

    public void addFileFilter(final OhosFileFilter fileFilter) {
        final OhosFileFilter ohosFileFilter = this.fileFilterMap.get(fileFilter.getName());
        if (ohosFileFilter == null) {
            this.fileFilterMap.put(fileFilter.getName(), fileFilter);
        }
    }

    public void addPolicy(final OhosPolicy ohosPolicy) {
        final OhosPolicy ohosPolicy1 = this.policyMap.get(ohosPolicy.getNamne());
        if (ohosPolicy1 == null) {
            this.policyMap.put(ohosPolicy.getNamne(), ohosPolicy);
        }
    }

    public void addTask(final OhosTask ohosTask) {
        this.taskList.add(ohosTask);
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

    public List<OhosLicense> getLicenseList() {
        return this.licenseList;
    }

    public void setLicenseList(final List<OhosLicense> licenseList) {
        this.licenseList = licenseList;
    }

    public List<OhosTask> getTaskList() {
        return this.taskList;
    }

    public void reArrangeData() {
        for (final OhosTask ohosTask : this.taskList) {
            final String policyName = ohosTask.getPolicy();
            final OhosPolicy policy = this.policyMap.get(policyName);
            if (policy == null) {
                continue;
            }
            ohosTask.setPolicyData(policy);
            final String fileFilter = ohosTask.getFileFilter();
            if (fileFilter == null) {
                continue;
            }
            final OhosFileFilter fileFilterObj = this.fileFilterMap.get(fileFilter);
            ohosTask.setFileFilterObj(fileFilterObj);

            final List<OhosProject> projectList = ohosTask.getProjectList();
            for (int i = 0; i < projectList.size(); i++) {
                final OhosProject ohosProject = projectList.get(i);
                final String prjFileFilter = ohosProject.getFileFilter();
                final String prjPolicy = ohosProject.getPolicy();
                if (prjFileFilter == null || prjFileFilter.trim().equals("")) {
                    ohosProject.setFileFilterObj(fileFilterObj);
                    ohosProject.setFileFilter(fileFilterObj.getName());
                } else {
                    ohosProject.setFileFilterObj(this.fileFilterMap.get(prjFileFilter));
                }
                if (prjPolicy == null || prjPolicy.trim().equals("")) {
                    ohosProject.setOhosPolicy(policy);
                    ohosProject.setPolicy(policy.getNamne());
                } else {
                    ohosProject.setOhosPolicy(this.policyMap.get(prjPolicy));
                }
            }
            ohosTask.reArrangeProject();
        }

        for (final OhosPolicy policy : this.policyMap.values()) {
            this.bindFilterToPolicyItem(policy.getAllPolicyItems());
        }
    }

    private void bindFilterToPolicyItem(final List<OhosPolicyItem> policyItems) {
        for (final OhosPolicyItem policyItem : policyItems) {
            final String fileFilter = policyItem.getFileFilter();
            final OhosFileFilter fileFilterObj = this.fileFilterMap.get(fileFilter);
            policyItem.setFileFilterObj(fileFilterObj);
        }
    }

}
