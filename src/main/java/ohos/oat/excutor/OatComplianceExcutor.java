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
import ohos.oat.report.IOatReport;
import ohos.oat.report.OatDirectoryWalker;
import ohos.oat.report.OatMainReport;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.rat.api.RatException;
import org.apache.rat.report.IReportable;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OAT excutor，used to process tasks passed in by the commander
 *
 * @author chenyaxun
 * @since 2022/08
 */
public class OatComplianceExcutor implements IOatExcutor {
    
    /**
     * Perform inspection tasks based on the contents of the Oat Config data structure
     * @param oatConfig
     */
    @Override
    public void excute(final OatConfig oatConfig) {
        oatCheck(oatConfig);
    }

    /**
     * Output a report in the default style and default license header matcher.
     *
     * @param oatConfig Config in oat.xml
     */
    private static void oatCheck(final OatConfig oatConfig) {
        final List<OatTask> taskList = oatConfig.getTaskList();
        final int size = taskList.size();
        int maxThread = Math.min(size, 100);
        if (maxThread <= 0) {
            maxThread = 1;
        }
        final String reportFile = oatConfig.getData("reportFile");
        final ExecutorService exec = Executors.newFixedThreadPool(maxThread);

        final Date date = new Date();
        final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        final String startTime = simpleDataFormat.format(date);
        final String resultfolder = "./oat_results_" + startTime;
        OatLogUtil.println("", startTime + " Start analyzing....");
        if (reportFile.length() <= 0) {
            final File dir = new File(resultfolder);
            if (!dir.exists()) {
                final boolean success = dir.mkdirs();
                if (!success) {
                    OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), "Create dir failed");
                }
            }
        }

        for (final OatTask oatTask : taskList) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    this.repoort();
                }

                private void repoort() {
                    final File resultFile;
                    if (reportFile.length() > 0) {
                        resultFile = new File(reportFile);
                    } else {
                        resultFile = new File(resultfolder + "/oat_" + oatTask.getNamne() + ".txt");
                    }
                    OatLogUtil.println("", "Result file path:\t" + resultFile);
                    try {
                        final FileWriter fileWriter = new FileWriter(resultFile, false);

                        final IOatReport report = new OatMainReport(oatConfig, fileWriter);
                        report.startReport();
                        OatComplianceExcutor.checkProjects(report, oatTask, oatConfig);
                        report.concurrentReport();
                        report.endReport();
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (final Exception e) {
                        OatLogUtil.traceException(e);
                    }
                }
            });
        }
        exec.shutdown();
    }

    private static void checkProjects(final IOatReport report, final OatTask oatTask, final OatConfig oatConfig)
        throws RatException {
        final List<OatProject> projectList = oatTask.getProjectList();

        for (final OatProject oatProject : projectList) {
            final List<String> filterItems = oatProject.getFileFilterObj().getFileFilterItems();
            final List<String> newItems = new ArrayList<>();
            for (final String filterItem : filterItems) {
                newItems.add(filterItem.replace(oatProject.getPath(), ""));
            }
            final FilenameFilter filenameFilter = parseFileExclusions(newItems);
            final long startTime = System.currentTimeMillis();
            final IReportable base = getDirectoryWalker(oatConfig, oatProject, filenameFilter);
            if (base != null) {
                base.run(report);
            }
            final long costTime = (System.currentTimeMillis() - startTime) / 1000;
            OatLogUtil.warn(OatLicenseMain.class.getSimpleName(), oatProject.getPath() + "\tCostTime\t" + costTime);

        }

    }

    private static IReportable getDirectoryWalker(final OatConfig oatConfig, final OatProject oatProject,
        final FilenameFilter inputFileFilter) {
        final String prjDirectory = getPrjDirectory(oatConfig, oatProject);
        final File base = new File(prjDirectory);
        if (!base.exists()) {
            return null;
        }

        if (base.isDirectory()) {
            return new OatDirectoryWalker(oatConfig, oatProject, base, inputFileFilter);
        }
        return null;
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

    private static FilenameFilter parseFileExclusions(final List<String> excludes) {
        final OrFileFilter orFilter = new OrFileFilter();

        for (final String exclude : excludes) {
            // skip comments
            if (exclude.startsWith("#") || StringUtils.isEmpty(exclude)) {
                continue;
            }
            final String exclusion = exclude.trim();
            orFilter.addFileFilter(new NameFileFilter(exclusion));
            orFilter.addFileFilter(new WildcardFileFilter(exclusion));
        }
        return new NotFileFilter(orFilter);
    }
}
