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

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.config.OatTask;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * OAT excutor，Used to collect all projects in the directory specified by the command line
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatCollectSubProjectsExcutor extends AbstractOatExcutor {

    /**
     * Execute the specified task on the command line
     */
    @Override
    public void excute() {
        OatCollectSubProjectsExcutor.logSubProjects(this.oatConfig);
    }

    /**
     * @param oatConfig OAT configuration data structure
     */
    private static void logSubProjects(final OatConfig oatConfig) {
        if (oatConfig.isPluginMode()) {
            return;
        }
        final List<OatTask> taskList = oatConfig.getTaskList();
        if (taskList == null || taskList.size() <= 0) {
            return;
        }
        final OatTask task = taskList.get(0);
        final List<OatProject> projectList = task.getProjectList();
        if (projectList == null || projectList.size() <= 0) {
            return;
        }
        final OatProject oatProject = projectList.get(0);
        final String prjDirectory = OatCollectSubProjectsExcutor.getPrjDirectory(oatConfig, oatProject);
        final List<OatProject> subProjects = OatCollectSubProjectsExcutor.getSubProjects(prjDirectory);
        if (subProjects == null) {
            return;
        }
        for (final OatProject subProject : subProjects) {
            OatLogUtil.warn("",
                "<project name=\"" + subProject.getName() + "\" path=\"" + subProject.getPath() + "\"/>");
        }
    }

    @Nullable
    public static List<OatProject> getSubProjects(final String prjDirectory) {
        final File prjFile = new File(prjDirectory);
        if (!prjFile.exists() || prjFile.isFile()) {
            return null;
        }
        final String prjPath = OatCfgUtil.formatPath(OatFileUtils.getFileCanonicalPath(prjFile)) + "/";
        final List<OatProject> subProjects = new ArrayList<>();

        final File[] files = prjFile.listFiles();
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                if (file.getName().equals(".git") || file.getName().equals(".repo") || file.getName().equals(".svn")) {

                    continue;
                }
                OatCollectSubProjectsExcutor.collectSubPrjects(subProjects, prjPath, file, 1);
            }
        }
        OatCollectSubProjectsExcutor.fillProject(subProjects, prjPath, prjFile, true);

        return subProjects;
    }

    /**
     * @param subProjects
     * @param prjPath
     * @param file
     * @param depth
     */
    private static void collectSubPrjects(final List<OatProject> subProjects, final String prjPath, final File file,
        final int depth) {
        if (depth > 6) {
            return;
        }
        final int nextDepth = depth + 1;
        final File[] subFiles = file.listFiles();
        if (subFiles != null && subFiles.length > 0) {
            for (final File subFile : subFiles) {
                if (!subFile.isDirectory()) {
                    continue;
                }
                if (subFile.getName().equals(".git") || subFile.getName().equals(".repo") || subFile.getName()
                    .equals(".svn")) {
                    OatCollectSubProjectsExcutor.fillProject(subProjects, prjPath, file, false);
                    continue;
                }
                OatCollectSubProjectsExcutor.collectSubPrjects(subProjects, prjPath, subFile, nextDepth);
            }
        }
    }

    private static void fillProject(final List<OatProject> subProjects, final String prjPath, final File file,
        final boolean isRootPrj) {
        final String subPath = OatCfgUtil.formatPath(OatFileUtils.getFileCanonicalPath(file)) + "/";
        final String subPrjPath = subPath.replace(prjPath, "");
        final String subPrjName = subPrjPath;

        for (final OatProject subProject : subProjects) {
            if (subProject.getName().equals(subPrjName)) {
                return;
            }
        }
        final OatProject oatProject = new OatProject(subPrjName, subPrjPath);
        if (file.getPath().contains("third_party")) {
            oatProject.setUpstreamPrj(true);
        }
        if (subPrjName.length() <= 0) {
            oatProject.setName(file.getName());
        }
        subProjects.add(oatProject);
        if (isRootPrj) {
            OatCollectSubProjectsExcutor.reArrangeProject(subProjects);
        }
    }

    private static void reArrangeProject(final List<OatProject> oatProjects) {
        Collections.sort(oatProjects, new Comparator<OatProject>() {
            @Override
            public int compare(final OatProject o1, final OatProject o2) {
                return o1.getPath().compareTo(o2.getPath()); // up
            }
        });
        for (int i = 0; i < oatProjects.size(); i++) {
            final OatProject oatProject = oatProjects.get(i);
            for (int i1 = i + 1; i1 < oatProjects.size(); i1++) {
                final OatProject oatProject1 = oatProjects.get(i1);
                if (oatProject1.getPath().startsWith(oatProject.getPath())) {
                    oatProject.addIncludedPrj(oatProject1);
                }
            }
        }
    }

    /**
     * @param oatConfig OAT configuration data structure
     * @param oatProject
     * @return
     */
    private static String getPrjDirectory(final OatConfig oatConfig, final OatProject oatProject) {
        final String prjDirectory;
        if (oatConfig.isPluginMode()) {
            // 如果是插件模式，直接扫描根目录下所有
            prjDirectory = oatConfig.getBasedir();
        } else {
            final String prjPath = oatProject.getPath();
            prjDirectory = oatConfig.getBasedir() + prjPath;
        }
        return prjDirectory;
    }

}
