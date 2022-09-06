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
import ohos.oat.task.IOatTaskProcessor;

import java.io.File;
import java.io.IOException;

/**
 * OAT file walker，used to process files
 *
 * @author chenyaxun
 * @since 2.0
 */
public interface IOatFileWalker {
 
    /**
     * @param oatConfig OAT configuration data structure
     * @param taskProcessor Task Processor to receive file documments
     */
    IOatFileWalker init(OatConfig oatConfig, IOatTaskProcessor taskProcessor);

    /**
     * @param oatProject Project to walk files
     */
    void walkProject(OatProject oatProject);

    /**
     * Determine whether the file is a soft link file
     *
     * @param file file to check
     * @return whether the file is a soft link file
     */
    static boolean isLink(final File file) {
        String cPath = "";
        try {
            cPath = file.getCanonicalPath();
        } catch (final IOException ex) {

        }
        return !cPath.equals(file.getAbsolutePath());

    }

}
