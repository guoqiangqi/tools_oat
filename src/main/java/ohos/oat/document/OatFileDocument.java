/*
 * Copyright (c) 2021-2022 Huawei Device Co., Ltd.
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
 * 2021.2 -  Extends from Apache Rat and some fields to support OpenHarmony:
 * 1. Add matchResult to verify if the matcher return success
 * 2. Add data to store some flags to simplify the document analysis implementation
 * Modified by jalenchen
 * 2021.5 - Support Scan files of all projects concurrently and fix bugs:
 * 1. Add copyData() method, because the sub directory files are processed before the project document,this is for
 * copy the tmp document data to the real project document.
 * 2. Delete ohosProject.setProjectFileDocument in setOhosProject method to fix bugs, because it is not the real
 * project document, while in concurrent process, this problem will exposed.
 * Modified by jalenchen
 */

package ohos.oat.document;

import ohos.oat.config.OatProject;
import ohos.oat.utils.OatCfgUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Source code data structure
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatFileDocument implements IOatDocument {

    private OatProject oatProject;

    private final String name;

    private final File file;

    // private final MetaData metaData = new MetaData();

    private boolean isProjectRoot = false;

    private boolean isDirectory = false;

    private boolean isArchive = false;

    private boolean isBinary = false;

    private final Map<String, String> data = new HashMap<>();

    // This will be used in concurrent threads
    private final Map<String, List<String>> listData = new ConcurrentHashMap<>();

    private boolean isReadable;

    public OatFileDocument(final File file) {
        this.file = file;
        this.name = OatCfgUtil.formatPath(file.getPath());
    }

    @Override
    public String getName() {
        return this.name;
    }

    // @Override
    // public MetaData getMetaData() {
    //     return this.metaData;
    // }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public boolean isProjectRoot() {
        return this.isProjectRoot;
    }

    @Override
    public void setProjectRoot(final boolean projectRoot) {
        this.isProjectRoot = projectRoot;
    }

    @Override
    public boolean isDirectory() {
        return this.isDirectory;
    }

    @Override
    public void setDirectory(final boolean directory) {
        this.isDirectory = directory;
    }

    @Override
    public boolean isArchive() {
        return this.isArchive;
    }

    @Override
    public boolean isBinary() {
        return this.isBinary;
    }

    @Override
    public void setArchive(final boolean archive) {
        this.isArchive = archive;
    }

    @Override
    public void setBinary(final boolean binary) {
        this.isBinary = binary;
    }

    @Override
    public boolean isReadable() {
        return this.isReadable;
    }

    @Override
    public void setReadable(final boolean isReadble) {
        this.isReadable = isReadble;
    }

    @Override
    public Map<String, ? extends List<String>> getListData() {
        return this.listData;
    }

    @Override
    public Map<String, String> getData() {
        return this.data;
    }

    @Override
    public OatProject getOatProject() {
        return this.oatProject;
    }

    @Override
    public void setOatProject(final OatProject oatProject) {
        this.oatProject = oatProject;
        // this.getMetaData().set(new MetaData.Datum("ProjectName", oatProject.getName()));
    }

    @Override
    public String getData(final String key) {
        final String tmp = this.data.get(key);
        return tmp == null ? "" : tmp;
    }

    @Override
    public String getFileName() {
        final String name = this.getName();
        final int index = name.lastIndexOf("/");
        if (index >= 0) {
            return name.substring(index + 1);
        } else {
            return name;
        }
    }

    @Override
    public void putData(final String key, final String value) {
        this.data.put(key, value);
    }

    @Override
    public List<String> getListData(final String key) {
        final List<String> tmpList = this.listData.get(key);
        return tmpList == null ? new ArrayList<>() : tmpList;
    }

    @Override
    public void addListData(final String key, final String value) {
        List<String> tmpList = this.listData.get(key);
        if (tmpList == null) {
            tmpList = new ArrayList<>();
            this.listData.put(key, tmpList);
        }
        tmpList.add(value);
    }

    @Override
    public void copyData(final IOatDocument fileDocument) {
        if (fileDocument == null) {
            return;
        }
        this.listData.putAll(fileDocument.getListData());
        this.data.putAll(fileDocument.getData());
    }

    @Override
    public InputStream inputStream() throws IOException {
        return new FileInputStream(this.file);
    }
}
