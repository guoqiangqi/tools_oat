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

import java.util.ArrayList;
import java.util.List;

/**
 * OAT detail report data structure, one report corresponds to one OatReportSummaryInfo instance
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportSummaryInfo {
    public List<OatReportInfo> oatReportInfoList = new ArrayList<>();

    private final OatReportCreatorInfo reportCreatorInfo = new OatReportCreatorInfo();

    public List<OatReportInfo> getOatReportInfoList() {
        return this.oatReportInfoList;
    }

    public void addOatReportInfo(final OatReportInfo oatReportInfo) {
        this.oatReportInfoList.add(oatReportInfo);
    }

    public OatReportCreatorInfo getReportCreatorInfo() {
        return this.reportCreatorInfo;
    }

}
