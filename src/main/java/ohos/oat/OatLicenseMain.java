/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Derived from Apache Creadur Rat, the original license and notice text is at the end of the LICENSE file of this
 * project.
 *
 * ChangeLog:
 * 2021.1 - Add the following capabilities to support OpenHarmony:
 * 1. Task, project, processfilter, policy, and reportfilter  customization capability.
 * 2. Parameters for pipleline ingetration.
 * 3. SPDX license analysis capability.
 * 4. Special license header used by OpenHarmony analysis capability.
 * 5. Support batch and single project mode.
 * 6. List all the missed files not define in the OAT config file.
 * 7. Concurrent processing capability for each task.
 * 2021.3 -  Add program parameters to support integration with pipleline tools
 * Modified by jalenchen
 * 2021.5 - Support Scan files of all projects concurrently in one task:
 * 1. Add report.concurrentReport() method, all time-consuming code analysis processing takes place in this function.
 * 2. Delete createReport method and replaced by new OhosMainReport in every task.
 * 3. Modify run options, delete para of -l option.
 * 2021.6 - Support ignore project OAT configuration.
 * Modified by jalenchen
 */

package ohos.oat;

import ohos.oat.input.OatCommandLineMgr;
import ohos.oat.utils.OatLogUtil;

/**
 * Main class of the license and copyright analyser
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatLicenseMain {
    /**
     * Prompt message when the program started
     */
    private static final String PROMPT_MESSAGE_SEPARATOR
        = "--------------------------------------------------------------------------";

    private static final String PROMPT_MESSAGE_NAME = "OpenHarmony OSS Audit Tool";

    private static final String PROMPT_MESSAGE_COPY = "Copyright (C) 2021-2022 Huawei Device Co., Ltd.";

    private static final String PROMPT_MESSAGE_FEEDBACK =
        "If you have any questions or concerns, please create issue at https://gitee"
            + ".com/openharmony-sig/tools_oat/issues";

    /**
     * Private constructure to prevent new instance
     */
    private OatLicenseMain() {
    }

    /**
     * Main for OAT
     *
     * @param args Command line paras
     */
    public static void main(final String[] args) {
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_SEPARATOR);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_NAME);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_COPY);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_FEEDBACK);
        OatLogUtil.println("", OatLicenseMain.PROMPT_MESSAGE_SEPARATOR);

        OatCommandLineMgr.runCommand(args);

    }

}
