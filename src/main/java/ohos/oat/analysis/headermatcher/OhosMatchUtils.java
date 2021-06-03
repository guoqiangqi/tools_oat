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
 * ChangeLog:
 * 2021.5 - Extract from license matcher classes to avoid duplicate code
 * Modified by jalenchen
 */

package ohos.oat.analysis.headermatcher;

/**
 * Stateless utility class for determining whether to continue matching.
 *
 * @author chenyaxun
 * @since 1.0
 */

public class OhosMatchUtils {
    public OhosMatchUtils() {
    }

    /**
     * stop if matched spdx license header
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedSpdx(final String licenseName) {
        return licenseName != null && (licenseName.startsWith("SPDX:"));
    }

    /**
     * stop if matched any license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedAny(final String licenseName) {
        return licenseName != null && (!licenseName.equals("InvalidLicense"));
    }

    /**
     * stop if matched GPL license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedGPL(final String licenseName) {
        return licenseName != null && licenseName.contains("GPL");
    }

    /**
     * stop if matched MPL&EPL license
     *
     * @param licenseName license name
     * @return stop match or not
     */
    public static boolean stopWhileMatchedMPLEPL(final String licenseName) {
        return licenseName != null && (licenseName.contains("MPL") || licenseName.contains("EPL"));
    }

}
