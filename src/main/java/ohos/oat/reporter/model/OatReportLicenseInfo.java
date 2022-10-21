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

import ohos.oat.reporter.model.license.OatReportLicense;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaxun
 * @since 2.0
 */
public class OatReportLicenseInfo {

    private final int ossProjectLicenseTypeCount = 0;

    private final int ossProjectNormalLicenseTypeCount = 0;

    private final int ossProjectAbnormalLicenseTypeCount = 0;

    private final int ossProjectHasLicenseHeaderFileCount = 0;

    private final int ossProjectMultiLicenseHeaderFileCount = 0;

    private final int ossProjectNoLicenseHeaderFileCount = 0;

    private final int ossProjectNormalLicenseTypeFileCount = 0;

    private final int ossProjectAbnormalLicenseTypeFileCount = 0;

    private final List<OatReportLicense> ossProjectLicenseTypeList = new ArrayList<>();

}
