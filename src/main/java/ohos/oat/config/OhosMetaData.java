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
 * 2021.1 -  Extends from Apache Rat and abilities to support OpenHarmony:
 * 1. Add set and setMetaData methods to simplify the document analysis implementation
 * Modified by jalenchen
 */

package ohos.oat.config;

import org.apache.rat.api.MetaData;

/**
 * Meta data structure to store scan result of document
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosMetaData extends MetaData {
    public static void setMetaData(final MetaData metaData, final String name, final String value) {
        metaData.clear(name);
        metaData.add(new Datum(name, value));
    }

    /**
     * added by chenyaxun
     *
     * @param name
     * @param value
     */
    public void set(final String name, final String value) {
        this.clear(name);
        this.add(new Datum(name, value));
    }
}
