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

import ohos.oat.reporter.model.relation.OatReportRelation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportProjectInfo {
    private final List<OatReportRelation> dependencyRelationList = new ArrayList<>();

    private final List<OatReportRelation> consumerRelationList = new ArrayList<>();

    private String projectName = "";

    private String projectHomePage = "";

    private String projectVersion = "";

    private String projectBranch = "";

    private String projectCommitId = "";

    private String mainLicense = "";

    public List<OatReportRelation> getDependencyRelationList() {
        return this.dependencyRelationList;
    }

    public void addDependencyRelation(final OatReportRelation oatReportRelation) {
        this.dependencyRelationList.add(oatReportRelation);
    }

    public List<OatReportRelation> getConsumerRelationList() {
        return this.consumerRelationList;
    }

    public void addConsumerRelation(final OatReportRelation oatReportRelation) {
        this.consumerRelationList.add(oatReportRelation);
    }

    public String getMainLicense() {
        return this.mainLicense;
    }

    public void setMainLicense(final String mainLicense) {
        this.mainLicense = mainLicense;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectHomePage() {
        return this.projectHomePage;
    }

    public void setProjectHomePage(final String projectHomePage) {
        this.projectHomePage = projectHomePage;
    }

    public String getProjectVersion() {
        return this.projectVersion;
    }

    public void setProjectVersion(final String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public String getProjectBranch() {
        return this.projectBranch;
    }

    public void setProjectBranch(final String projectBranch) {
        this.projectBranch = projectBranch;
    }

    public String getProjectCommitId() {
        return this.projectCommitId;
    }

    public void setProjectCommitId(final String projectCommitId) {
        this.projectCommitId = projectCommitId;
    }

}
