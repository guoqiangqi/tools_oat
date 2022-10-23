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

import ohos.oat.reporter.model.file.OatReportFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportCopyrightInfo {

    private final List<String> copyrightList = new ArrayList<>();

    private final List<String> normalCopyrightList = new ArrayList<>();

    private final List<String> abnormalCopyrightList = new ArrayList<>();

    private final Map<String, List<OatReportFile>> copyright2FileList = new HashMap<>();

    private final List<OatReportFile> noCopyrightHeaderFileList = new ArrayList<>();

    private final List<OatReportFile> abnormalCopyrightHeaderFileList = new ArrayList<>();

    private int copyrightCount = 0;

    private int normalCopyrightCount = 0;

    private int abnormalCopyrightCount = 0;

    private int hasCopyrightHeaderFileCount = 0;

    private int noCopyrightHeaderFileCount = 0;

    private int normalCopyrightHeaderFileCount = 0;

    private int abnormalCopyrightHeaderFileCount = 0;

    public int getCopyrightCount() {
        return this.copyrightCount;
    }

    public void setCopyrightCount(final int copyrightCount) {
        this.copyrightCount = copyrightCount;
    }

    public int getNormalCopyrightCount() {
        return this.normalCopyrightCount;
    }

    public void setNormalCopyrightCount(final int normalCopyrightCount) {
        this.normalCopyrightCount = normalCopyrightCount;
    }

    public int getAbnormalCopyrightCount() {
        return this.abnormalCopyrightCount;
    }

    public void setAbnormalCopyrightCount(final int abnormalCopyrightCount) {
        this.abnormalCopyrightCount = abnormalCopyrightCount;
    }

    public int getHasCopyrightHeaderFileCount() {
        return this.hasCopyrightHeaderFileCount;
    }

    public void setHasCopyrightHeaderFileCount(final int hasCopyrightHeaderFileCount) {
        this.hasCopyrightHeaderFileCount = hasCopyrightHeaderFileCount;
    }

    public int getNoCopyrightHeaderFileCount() {
        return this.noCopyrightHeaderFileCount;
    }

    public void setNoCopyrightHeaderFileCount(final int noCopyrightHeaderFileCount) {
        this.noCopyrightHeaderFileCount = noCopyrightHeaderFileCount;
    }

    public int getNormalCopyrightHeaderFileCount() {
        return this.normalCopyrightHeaderFileCount;
    }

    public void setNormalCopyrightHeaderFileCount(final int normalCopyrightHeaderFileCount) {
        this.normalCopyrightHeaderFileCount = normalCopyrightHeaderFileCount;
    }

    public int getAbnormalCopyrightHeaderFileCount() {
        return this.abnormalCopyrightHeaderFileCount;
    }

    public void setAbnormalCopyrightHeaderFileCount(final int abnormalCopyrightHeaderFileCount) {
        this.abnormalCopyrightHeaderFileCount = abnormalCopyrightHeaderFileCount;
    }

    public List<String> getCopyrightList() {
        return this.copyrightList;
    }

    public List<String> getNormalCopyrightList() {
        return this.normalCopyrightList;
    }

    public void addNormalCopyright(final String copyright) {
        this.normalCopyrightList.add(copyright);
        this.normalCopyrightCount++;
        this.copyrightList.add(copyright);
        this.copyrightCount++;
    }

    public List<String> getAbnormalCopyrightList() {
        return this.abnormalCopyrightList;
    }

    public void addAbnormalCopyright(final String copyright) {
        this.abnormalCopyrightList.add(copyright);
        this.abnormalCopyrightCount++;
        this.copyrightList.add(copyright);
        this.copyrightCount++;
    }

    public Map<String, List<OatReportFile>> getCopyright2FileList() {
        return this.copyright2FileList;
    }

    public void addCopyright2File(final String copyright, final OatReportFile file) {
        List filelist = this.copyright2FileList.get(copyright);
        if (null == filelist) {
            filelist = new ArrayList();
            this.copyright2FileList.put(copyright, filelist);
        }
        filelist.add(file);
    }

    public List<OatReportFile> getNoCopyrightHeaderFileList() {
        return this.noCopyrightHeaderFileList;
    }

    public void addNoCopyrightHeaderFile(final OatReportFile file) {
        this.noCopyrightHeaderFileList.add(file);
        this.noCopyrightHeaderFileCount++;
    }

    public List<OatReportFile> getAbnormalCopyrightHeaderFileList() {
        return this.abnormalCopyrightHeaderFileList;
    }

    public void addAbnormalCopyrightHeaderFile(final OatReportFile file) {
        this.abnormalCopyrightHeaderFileList.add(file);
        this.abnormalCopyrightHeaderFileCount++;
        this.hasCopyrightHeaderFileCount++;
    }

    public void addNormalCopyrightHeaderFile(final OatReportFile file) {

        this.hasCopyrightHeaderFileCount++;
    }
}
