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

import ohos.oat.OatLicenseMain;
import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.config.OatTask;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * OAT excutor，used to process tasks passed in by the commander
 *
 * @author chenyaxun
 * @since 2022/08
 */
public class OatCollectSubProjectsExcutor implements IOatExcutor {
    /**
     * @param oatConfig
     */
    @Override
    public void excute(final OatConfig oatConfig) {
        logSubProjects(oatConfig);
    }

    private static void logSubProjects(final OatConfig oatConfig) {
        if (!oatConfig.isPluginMode()) {
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
        final String prjDirectory = getPrjDirectory(oatConfig, oatProject);
        final File prjFile = new File(prjDirectory);
        if (!prjFile.exists() || prjFile.isFile()) {
            return;
        }
        final String prjPath = OatCfgUtil.formatPath(OatFileUtils.getFileCanonicalPath(prjFile)) + "/";
        final List<String> subProjects = new ArrayList<>();

        final File[] files = prjFile.listFiles();
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                if (file.getName().equals(".git")) {
                    continue;
                }
                collectSubPrjects(subProjects, prjPath, file, 1);
            }
        }
        for (final String subProject : subProjects) {
            OatLogUtil.warn(OatLicenseMain.class.getSimpleName(),
                oatProject.getPath() + "\tsubProject\t" + "<project name=\"" + subProject + "\" path=\"" + subProject
                    + "\"/>");
        }
    }

    private static void collectSubPrjects(final List<String> subProjects, final String prjPath, final File file,
        final int depth) {
        if (depth > 4) {
            return;
        }
        final int nextDepth = depth + 1;
        final File[] subFiles = file.listFiles();
        if (subFiles != null && subFiles.length > 0) {
            for (final File subFile : subFiles) {
                if (!subFile.isDirectory()) {
                    continue;
                }
                if (subFile.getName().equals(".git")) {
                    final String subPath = OatCfgUtil.formatPath(OatFileUtils.getFileCanonicalPath(file)) + "/";
                    final String subPrjPath = subPath.replace(prjPath, "");
                    subProjects.add(subPrjPath);
                    continue;
                }
                collectSubPrjects(subProjects, prjPath, subFile, nextDepth);
            }
        }
    }

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
