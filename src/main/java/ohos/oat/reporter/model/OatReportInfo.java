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

/**
 * OAT detail report data structure, one report corresponds to one OatReportInfo instance
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportInfo {

    private OatReportCreatorInfo reportCreatorInfo;

    private OatReportConfigInfo reportConfigInfoInfo;

    private OatReportProjectInfo reportProjectInfo;

    private OatReportFileInfo reportFileInfo;

    private OatReportLicenseInfo reportLicenseInfo;

    private OatReportCopyrightInfo reportCopyrightInfo;

}
