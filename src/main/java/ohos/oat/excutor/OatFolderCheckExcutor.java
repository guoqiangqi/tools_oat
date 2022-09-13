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
import ohos.oat.utils.OatLogUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        final String defaultpolicystr =
            "#-policy#\"repotype:dev; license:Apache-2.0@!.*LICENSE |ApacheStyleLicense@.*LICENSE| Apache-2.0@"
                + ".*LICENSE;"
                + "copyright:Huawei Device Co., Ltd.;filename:LICENSE@projectroot|README.md@projectroot|README_zh"
                + ".md@projectroot;filetype:!binary~must|!archive~must;\"";

        final ExecutorService exec = Executors.newFixedThreadPool(16);
        int count = 0;
        for (final OatProject subProject : subProjects) {

            String cmdLine = assembleCmdline(sourceCodeRepoPath, subProject);
            final String cmd = cmdLine + postStr;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    OatCommandLineMgr.runCommand(cmd.split("#"));
                }
            });
            cmdLine += defaultpolicystr;
            final String cmd2 = cmdLine + postStr;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    OatCommandLineMgr.runCommand(cmd2.split("#"));
                }
            });

            //Limit the number of concurrency to prevent OOM
            count++;
            if (count >= 16) {
                count = 0;
                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        try {
            // wait the pool until finish
            while (!exec.awaitTermination(3, TimeUnit.SECONDS)) {
            }
        } catch (final InterruptedException e) {
            OatLogUtil.traceException(e);
        }
    }

    @NotNull
    private static String assembleCmdline(final String sourceCodeRepoPath, final OatProject subProject) {
        String cmdLine = "-mode#";
        cmdLine += "s#-s#" + sourceCodeRepoPath + (subProject.getPath().length() > 0 ? "/" + subProject.getPath() : "");
        // cmdLine += " -r ./";
        cmdLine += "#-n#" + (subProject.getName().length() > 0
            ? subProject.getName()
            : (subProject.isUpstreamPrj() ? "third_party_root" : "root"));
        String filterString = "";
        if (subProject.getIncludedPrjList().size() > 0) {
            filterString += "#-filter#filepath:";
            int index = 0;
            for (final OatProject oatProject : subProject.getIncludedPrjList()) {
                if (index == 0) {
                    filterString += "projectroot/" + oatProject.getPath();
                } else {
                    filterString += "|projectroot/" + oatProject.getPath();
                }
                index++;
            }
        }
        cmdLine += filterString;
        return cmdLine;
    }

}
