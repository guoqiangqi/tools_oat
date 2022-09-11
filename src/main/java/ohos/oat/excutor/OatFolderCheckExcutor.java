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

package ohos.oat.excutor;

import ohos.oat.config.OatProject;
import ohos.oat.input.OatCommandLineMgr;

import java.util.List;

/**
 * OAT excutor，Used to collect all projects in the directory specified by the command line and check projects
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatFolderCheckExcutor extends AbstractOatExcutor {

    /**
     * Execute the specified task on the command line
     */
    @Override
    public void excute() {
        final String sourceCodeRepoPath = this.oatConfig.getBasedir();
        final List<OatProject> subProjects = OatCollectSubProjectsExcutor.getSubProjects(sourceCodeRepoPath);
        String postStr = "";
        if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
            postStr += "#-k";
        }
        if (this.oatConfig.getData("IgnoreProjectOAT").equals("true")) {
            postStr += "#-g";
        }
        if (this.oatConfig.getData("IgnoreProjectPolicy").equals("true")) {
            postStr += "#-p";
        }
        final String policystr = this.oatConfig.getData("policy");
        if (policystr.length() > 0) {
            postStr += "#-policy#" + policystr;
        }

        final String defaultpolicystr =
            "#-policy#\"repotype:dev; license:Apache-2.0@!.*LICENSE |ApacheStyleLicense@.*LICENSE| Apache-2.0@"
                + ".*LICENSE;"
                + "copyright:Huawei Device Co., Ltd.;filename:LICENSE@projectroot|README.md@projectroot|README_zh"
                + ".md@projectroot;filetype:!binary|!archive;\"";

        for (final OatProject subProject : subProjects) {

            String cmdLine = "-mode#";
            cmdLine += "s#-s#" + sourceCodeRepoPath + (subProject.getPath().length() > 0
                ? "/" + subProject.getPath()
                : "");
            // cmdLine += " -r ./";
            cmdLine += "#-n#" + (subProject.getName().length() > 0 ? subProject.getName() : "Default");
            cmdLine += defaultpolicystr;
            OatCommandLineMgr.runCommand(cmdLine.split("#"));
        }

    }

}
