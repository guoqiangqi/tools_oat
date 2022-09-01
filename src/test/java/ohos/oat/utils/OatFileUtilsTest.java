/*
 * Copyright (c) 2022 Huawei Device Co., Ltd.
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


import ohos.oat.document.OatFileDocument;
import org.junit.Assert;
import java.io.File;


/**
 * @author kubigao
 * @since 1.0
 */
public class OatFileUtilsTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void test_isBinaryFileWhenFileTypeIsDrv() {
        OatFileDocument document = new OatFileDocument(new File("test.drv")) ;
        boolean result =  OatFileUtils.isBinaryFile(document);
        Assert.assertEquals(result, true);
    }

    @org.junit.Test
    public void test_isBinaryFileWhenFileTypeIsXXX() {
        OatFileDocument document = new OatFileDocument(new File("test.XXX")) ;
        boolean result =  OatFileUtils.isBinaryFile(document);
        Assert.assertEquals(result, false);
    }

    @org.junit.Test
    public void test_isArchiveFileWhenFileTypeIsImg() {
        OatFileDocument document = new OatFileDocument(new File("test.img")) ;
        boolean result = OatFileUtils.isArchiveFile(document);
        Assert.assertEquals(result, true);
    }

    @org.junit.Test
    public void test_isArchiveFileWhenFileTypeIsXXX() {
        OatFileDocument document = new OatFileDocument(new File("test.XXX")) ;
        boolean result = OatFileUtils.isArchiveFile(document);
        Assert.assertEquals(result, false);
    }
}
