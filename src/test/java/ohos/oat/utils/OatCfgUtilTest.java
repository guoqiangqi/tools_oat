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
 */

package ohos.oat.utils;

import ohos.oat.config.OatConfig;

import org.junit.Assert;

/**
 * @author chenyaxun
 * @since 1.0
 */
public class OatCfgUtilTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void getShortPath() {
        final OatConfig oatConfig = new OatConfig();
        oatConfig.setBasedir("C:/_chen/TestFiles/");
        final String filePath = "C:/_chen/TestFiles/ApacheLicense.txt";
        final String result = OatCfgUtil.getShortPath(oatConfig, filePath);
        Assert.assertEquals(result, "ApacheLicense.txt");
    }

    @org.junit.Test
    public void formatPath() {
    }

    @org.junit.Test
    public void testGetShortPath() {
    }

    @org.junit.Test
    public void initOatConfig() {
    }

    @org.junit.Test
    public void getSplitStrings() {
    }

    @org.junit.Test
    public void testGetSplitStrings() {
    }
}