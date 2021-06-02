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
public class OhosTask {
    private String namne;

    private String policy;

    private OhosPolicy policyData;

    private String desc;

    private String fileFilter;

    private OhosFileFilter fileFilterObj;

    private List<OhosProject> projectList;

    public OhosTask() {
        this.projectList = new ArrayList<>();
    }

    public String getFileFilter() {
        return this.fileFilter;
    }

    public void setFileFilter(final String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public OhosFileFilter getFileFilterObj() {
        return this.fileFilterObj;
    }

    public void setFileFilterObj(final OhosFileFilter fileFilterObj) {
        this.fileFilterObj = fileFilterObj;
    }

    public OhosPolicy getPolicyData() {
        return this.policyData;
    }

    public void setPolicyData(final OhosPolicy policyData) {
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

    public List<OhosProject> getProjectList() {
        return this.projectList;
    }

    public void setProjectList(final List<OhosProject> projectList) {
        this.projectList = projectList;
    }

    public void addProject(final OhosProject ohosProject) {
        this.projectList.add(ohosProject);
    }

    public void reArrangeProject() {
        Collections.sort(this.projectList, new Comparator<OhosProject>() {
            @Override
            public int compare(final OhosProject o1, final OhosProject o2) {
                return o1.getPath().compareTo(o2.getPath()); // up
            }
        });
        for (int i = 0; i < this.projectList.size(); i++) {
            final OhosProject ohosProject = this.projectList.get(i);
            for (int i1 = i + 1; i1 < this.projectList.size(); i1++) {
                final OhosProject ohosProject1 = this.projectList.get(i1);
                if (ohosProject1.getPath().startsWith(ohosProject.getPath())) {
                    ohosProject.addIncludedPrj(ohosProject1);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "OhosTask{" + "namne='" + this.namne + '\'' + ", policy='" + this.policy + '\'' + ", desc='" + this.desc
            + '\'' + '}';
    }
}
