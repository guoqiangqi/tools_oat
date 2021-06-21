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
 * 2021.3 -  Change the owner of policy and filter from task to projects, and set then to projects under a task by
 * default.
 * Modified by jalenchen
 */

package ohos.oat.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Data Structure of oat scanning task defined in OAT.xml
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatTask {
    private String namne;

    private String policy;

    private OatPolicy policyData;

    private String desc;

    private String fileFilter;

    private OatFileFilter fileFilterObj;

    private List<OatProject> projectList;

    public OatTask() {
        this.projectList = new ArrayList<>();
    }

    public String getFileFilter() {
        return this.fileFilter;
    }

    public void setFileFilter(final String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public OatFileFilter getFileFilterObj() {
        return this.fileFilterObj;
    }

    public void setFileFilterObj(final OatFileFilter fileFilterObj) {
        this.fileFilterObj = fileFilterObj;
    }

    public OatPolicy getPolicyData() {
        return this.policyData;
    }

    public void setPolicyData(final OatPolicy policyData) {
        this.policyData = policyData;
    }

    public String getNamne() {
        return this.namne;
    }

    public void setNamne(final String namne) {
        this.namne = namne;
    }

    public String getPolicy() {
        return this.policy;
    }

    public void setPolicy(final String policy) {
        this.policy = policy;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public List<OatProject> getProjectList() {
        return this.projectList;
    }

    public void setProjectList(final List<OatProject> projectList) {
        this.projectList = projectList;
    }

    public void addProject(final OatProject oatProject) {
        this.projectList.add(oatProject);
    }

    public void reArrangeProject() {
        Collections.sort(this.projectList, new Comparator<OatProject>() {
            @Override
            public int compare(final OatProject o1, final OatProject o2) {
                return o1.getPath().compareTo(o2.getPath()); // up
            }
        });
        for (int i = 0; i < this.projectList.size(); i++) {
            final OatProject oatProject = this.projectList.get(i);
            for (int i1 = i + 1; i1 < this.projectList.size(); i1++) {
                final OatProject oatProject1 = this.projectList.get(i1);
                if (oatProject1.getPath().startsWith(oatProject.getPath())) {
                    oatProject.addIncludedPrj(oatProject1);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "OatTask{" + "namne='" + this.namne + '\'' + ", policy='" + this.policy + '\'' + ", desc='" + this.desc
            + '\'' + '}';
    }
}
