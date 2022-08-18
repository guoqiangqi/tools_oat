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

import org.apache.commons.cli.Options;

/**
 * @author chenyaxun
 * @since 2022/08
 */
public class OatMultiModeCommandLine implements IOatCommandLine {
    private Options options;

    private String cmdLineSyntax;

    @Override
    public boolean accept(final String[] args) {
        return false;
    }

    @Override
    public boolean parseArgs2Config(final String[] args, final OatConfig oatConfig) {
        return false;
    }

    /**
     * @return Options
     */
    @Override
    public Options getOptions() {
        return null;
    }

    /**
     * @return cmdLineSyntax
     */
    @Override
    public String getCmdLineSyntax() {
        return null;
    }

}
