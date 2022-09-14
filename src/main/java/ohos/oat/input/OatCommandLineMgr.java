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

package ohos.oat.input;

import ohos.oat.config.OatConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Tool class for initializing command line mode
 *
 * @author chenyaxun
 * @since 2.0
 */
public class OatCommandLineMgr {
    /**
     * Private constructor,prevent create instance
     */
    private OatCommandLineMgr() {
    }

    /**
     * init oat config from program command para
     *
     * @param args command line paras
     */
    public static void runCommand(final String[] args) {
        final List<IOatCommandLine> lstOatCommandLine = new ArrayList<>();
        lstOatCommandLine.add(new OatSingleModeCommandLine());
        lstOatCommandLine.add(new OatMultiModeCommandLine());
        lstOatCommandLine.add(new OatFolderModeCommandLine());
        lstOatCommandLine.add(new OatCollectSubPrjectsCommandLine());

        boolean bMatched = false;
        for (final IOatCommandLine iOatCommandLine : lstOatCommandLine) {
            if (iOatCommandLine.accept(args)) {
                final OatConfig oatConfig = iOatCommandLine.parseArgs2Config(args);
                if (null != oatConfig) {
                    bMatched = true;
                    iOatCommandLine.transmit2Executor(oatConfig);
                    return;
                } else {
                    // Print command options of this mode only
                    iOatCommandLine.printUsage();
                    System.exit(0);
                }
                break;
            }
        }
        if (!bMatched) {
            // Print all command options
            lstOatCommandLine.forEach(k -> k.printUsage());
            System.exit(0);
        }

    }
}
