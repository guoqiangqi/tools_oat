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

package ohos.oat.file;

import ohos.oat.config.OatConfig;
import ohos.oat.config.OatProject;
import ohos.oat.document.OatFileDocument;
import ohos.oat.file.filter.IOatFileFilter;
import ohos.oat.file.filter.OatDefaultFileFilter;
import ohos.oat.utils.OatLogUtil;

import org.apache.rat.api.Document;
import org.apache.rat.api.RatException;
import org.apache.rat.report.IReportable;
import org.apache.rat.report.RatReport;

import java.io.File;

/**
 * OAT project walker，used to process project files
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatProjectWalker extends AbstractOatFileWalker {

    /**
     * walk project
     *
     * @param oatProject
     */
    @Override
    public void walkProject(final OatProject oatProject) {

        final long startTime = System.currentTimeMillis();
        final IReportable directoryWalker = OatProjectWalker.getDirectoryWalker(this.oatConfig, oatProject);
        final RatReport report = new RatReport() {
            @Override
            public void startReport() throws RatException {

            }

            @Override
            public void report(final Document document) throws RatException {
                OatProjectWalker.this.taskProcessor.addFileDocument((OatFileDocument) document);
            }

            @Override
            public void endReport() throws RatException {

            }
        };
        if (directoryWalker != null) {
            try {
                directoryWalker.run(report);
            } catch (final RatException e) {
                throw new RuntimeException(e);
            }
        }
        final long costTime = (System.currentTimeMillis() - startTime) / 1000;
        OatLogUtil.warn(this.getClass().getSimpleName(),
            oatProject.getPath() + "\tWalker project costTime\t" + costTime);
    }

    private static IReportable getDirectoryWalker(final OatConfig oatConfig, final OatProject oatProject) {
        final String prjDirectory = OatProjectWalker.getPrjDirectory(oatConfig, oatProject);
        final File base = new File(prjDirectory);
        if (!base.exists()) {
            return null;
        }

        if (base.isDirectory()) {
            final IOatFileFilter oatFileFilter = new OatDefaultFileFilter().init(oatProject);
            return new OatDirectoryWalker(oatConfig, oatProject, base, oatFileFilter.getFilter());
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

}
