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

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Default IOatCommandLine method implementations
 *
 * @author chenyaxun
 * @since 2.0
 */
public abstract class AbstractOatCommandLine implements IOatCommandLine {
    protected final Options options = new Options();

    protected final String cmdLineSyntax = "java -jar ohos_ossaudittool-VERSION.jar ";

    /**
     * Print usage of this commandline，defined as static to avoid instantiation
     */
    @Override
    public void printUsage() {
        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(null);
        helpFormatter.setWidth(140);
        helpFormatter.printHelp(this.getCmdLineSyntax(), IOatCommandLine.PROMPT_MESSAGE_HEADER, this.getOptions(),
            IOatCommandLine.PROMPT_MESSAGE_SEPARATOR, true);
    }

    /**
     * @return Options
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

    /**
     * @return cmdLineSyntax
     */
    @Override
    public String getCmdLineSyntax() {
        return this.cmdLineSyntax;
    }

}
