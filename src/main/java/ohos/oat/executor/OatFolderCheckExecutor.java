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

package ohos.oat.executor;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.input.OatCommandLineMgr;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OAT executor，Used to collect all projects in the directory specified by the command line and check projects
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatFolderCheckExecutor extends AbstractOatExecutor {
    private final Map<Integer, List<OatProject>> prjectMap = new HashMap<>();

    private final static int THREAD_POOL_SIZE = 16;

    private int index = 0;

    private String postStr = "";

    private String defaultpolicystr = "";

    private String upstreampolicystr = "";

    private boolean outputallreports = false;

    /**
     * Init instance with paras
     *
     * @param oatConfig OAT configuration data structure
     * @return
     */
    @Override
    public IOatExecutor init(final OatConfig oatConfig) {
        super.init(oatConfig);
        final List<OatProject> subProjects = OatCollectSubProjectsExecutor.getSubProjects(this.oatConfig.getBasedir());
        this.initProjectMap();
        this.allocateProject2List(subProjects);

        if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
            this.postStr += "#-k";
        }
        if (this.oatConfig.getData("IgnoreProjectOAT").equals("true")) {
            this.postStr += "#-g";
        }
        if (this.oatConfig.getData("IgnoreProjectPolicy").equals("true")) {
            this.postStr += "#-p";
        }
        final String reportFolder = this.oatConfig.getData("reportFolder");
        if (reportFolder.length() > 0) {
            this.postStr += "#-r#" + reportFolder;
        }

        this.defaultpolicystr =
            "#-policy#\"repotype:dev; license:Apache-2.0@!.*LICENSE |ApacheStyleLicense@.*LICENSE| Apache-2.0@"
                + ".*LICENSE;"
                + "copyright:Huawei Device Co., Ltd.;filename:LICENSE@projectroot|README.md@projectroot|README_zh"
                + ".md@projectroot;filetype:!binary~must|!archive~must;\"";

        this.upstreampolicystr =
            "#-policy#\"repotype:upstream; compatibility:Apache|BSD|MIT|FSFULLR|MulanPSL|!APSL-1.0~must;"
                + "filename:LICENSE@projectroot|README.OpenSource@projectroot;filetype:!binary~must|!archive~must;\"";

        if (this.oatConfig.getData("allreports").equals("true")) {
            this.outputallreports = true;
        }

        return this;
    }

    /**
     * Execute the specified task on the command line
     */
    @Override
    public void execute() {

        final ExecutorService exec = Executors.newFixedThreadPool(OatFolderCheckExecutor.THREAD_POOL_SIZE);
        for (int i = 0; i < OatFolderCheckExecutor.THREAD_POOL_SIZE; i++) {
            final List<OatProject> oatProjects = this.prjectMap.get(i);
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    for (final OatProject oatProject : oatProjects) {
                        String cmdLine = OatFolderCheckExecutor.assembleCmdline(
                            OatFolderCheckExecutor.this.oatConfig.getBasedir(), oatProject);
                        final String cmd = cmdLine + OatFolderCheckExecutor.this.postStr;

                        OatCommandLineMgr.runCommand(cmd.split("#"));

                        if (OatFolderCheckExecutor.this.outputallreports) {
                            // Check with policy options
                            if (oatProject.isUpstreamPrj()) {
                                cmdLine += OatFolderCheckExecutor.this.upstreampolicystr;
                            } else {
                                cmdLine += OatFolderCheckExecutor.this.defaultpolicystr;
                            }
                            final String cmd2 = cmdLine + OatFolderCheckExecutor.this.postStr;

                            OatCommandLineMgr.runCommand(cmd2.split("#"));

                        }
                    }
                }
            });

        }

        exec.shutdown();
    }

    private void allocateProject2List(final List<OatProject> subProjects) {
        for (final OatProject subProject : subProjects) {
            this.prjectMap.get(this.index).add(subProject);
            this.index++;
            if (this.index >= OatFolderCheckExecutor.THREAD_POOL_SIZE) {
                this.index = 0;
            }
        }
    }

    private void initProjectMap() {
        for (int i = 0; i < OatFolderCheckExecutor.THREAD_POOL_SIZE; i++) {
            this.prjectMap.put(i, new ArrayList<>());
        }
    }

    @NotNull
    private static String assembleCmdline(final String sourceCodeRepoPath, final OatProject subProject) {
        String cmdLine = "-mode#";
        cmdLine += "s#-s#" + sourceCodeRepoPath + (subProject.getPath().length() > 0 ? subProject.getPath() : "");
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
