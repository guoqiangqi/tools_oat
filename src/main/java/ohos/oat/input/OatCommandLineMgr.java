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
 * Command line parameter processing class for converting command line parameters into OAT configuration data structures
 * extract this class to support more detection scenarios
 *
 * @author chenyaxun
 * @since 2022/08
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
     * @param args commmand para
     * @param oatConfig oat config
     */
    public static void initConfig(final String[] args, final OatConfig oatConfig) {
        final List<IOatCommandLine> lstOatCommandLine = new ArrayList<>();
        lstOatCommandLine.add(new OatSingleModeCommandLine());
        lstOatCommandLine.add(new OatMultiModeCommandLine());
        lstOatCommandLine.add(new OatFolderModeCommandLine());
        lstOatCommandLine.add(new OatCollectSubPrjectsCommandLine());

        boolean matched = false;
        for (final IOatCommandLine iOatCommandLine : lstOatCommandLine) {
            if (iOatCommandLine.accept(args)) {
                if (iOatCommandLine.parseArgs2Config(args, oatConfig)) {
                    matched = true;
                }
                break;
            }
        }

        if (!matched) {
            lstOatCommandLine.forEach(k -> k.printUsage());
            System.exit(0);
        }
    }
}
