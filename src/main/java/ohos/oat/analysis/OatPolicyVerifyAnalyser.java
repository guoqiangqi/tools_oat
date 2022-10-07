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
 * 2021.2 -  Add the following capabilities to support OpenHarmony:
 * 1. Support license header and copyright header policies.
 * 2. Support license compatibility policy to verify 3rd OSS licenses.
 * 3. Extract common methods to support more policies: isMatched, isApproved, isFiltered, isValid
 * 2021.3 - Add policies:
 * 1. Add file name policy to support LICENSE, README, README.OpenSource file check.
 * 2. Add file type policy to support archive and binary file check.
 * 2021.5 - Enhance extensibility: Change the calling policy.getXXXPolicyItems() to policy.getAllPolicyItems.
 * 2021.6 - Support print all license files to log file.
 * Modified by jalenchen
 *
 */

package ohos.oat.analysis;

import static org.apache.rat.api.MetaData.RAT_URL_DOCUMENT_CATEGORY;
import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_NAME;

import ohos.oat.analysis.headermatcher.OatMatchUtils;
import ohos.oat.config.*;
import ohos.oat.document.OatFileDocument;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatFileUtils;
import ohos.oat.utils.OatLogUtil;

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Main oat report generator, all source files will be applied policies and stored the result in document data
 * structure.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatPolicyVerifyAnalyser extends AbstraceOatAnalyser {

    @Override
    public void analyse() {
        final MetaData metaData = this.oatFileDocument.getMetaData();
        final String baseDir = this.oatConfig.getBasedir();
        final OatProject oatProject = this.oatFileDocument.getOatProject();
        final OatPolicy oatPolicy = oatProject.getOatPolicy();
        String prjPath = oatProject.getPath();
        if (this.oatConfig.isPluginMode()) {
            prjPath = "";
        }
        String shortFileUnderProject = this.oatFileDocument.getName().replace(baseDir + prjPath, "");
        if (this.oatFileDocument.isDirectory()) {
            // If the doc is directory, add "/"
            shortFileUnderProject = (this.oatFileDocument.getName() + "/").replace(baseDir + prjPath, "");
            this.verifyFileName(this.oatFileDocument, oatPolicy, shortFileUnderProject);
        }

        final String[] defLicenseFiles = oatProject.getLicenseFiles();
        final String name = this.oatFileDocument.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        final String LicenseHeaderText = this.oatFileDocument.getData("LicenseHeaderText");
        if (LicenseHeaderText != null && name != null && name.equals("InvalidLicense")) {
            OatLogUtil.warn(this.getClass().getSimpleName(),
                oatProject.getPath() + "\tInvalidLicense\t" + shortFileUnderProject + "\t" + name + "\t"
                    + LicenseHeaderText);
        }
        boolean findIt = false;
        if (!this.oatFileDocument.isDirectory()) {
            if (shortFileUnderProject.equals("LICENSE")) {
                findIt = true;
                OatLogUtil.logLicenseFile(this.getClass().getSimpleName(),
                    oatProject.getPath() + "\tLICENSEFILE\t" + shortFileUnderProject + "\t" + name + "\t"
                        + LicenseHeaderText);
            }
            for (final String defLicenseFile : defLicenseFiles) {
                if (shortFileUnderProject.endsWith(defLicenseFile)) {
                    findIt = true;
                    OatLogUtil.logLicenseFile(this.getClass().getSimpleName(),
                        oatProject.getPath() + "\tDEFLICENSEFILE\t" + shortFileUnderProject + "\t" + name + "\t"
                            + LicenseHeaderText);
                }
            }
        }

        if (!findIt && !this.oatFileDocument.isDirectory()) {
            final String fName = this.oatFileDocument.getFileName().toLowerCase(Locale.ENGLISH);
            if (!fName.contains(".") || fName.endsWith(".md") || fName.endsWith(".txt") || fName.endsWith(".html")
                || fName.endsWith(".htm") || fName.endsWith(".pdf")) {
                if (fName.contains("license") || fName.contains("licence") || fName.contains("copying")
                    || fName.contains("copyright") || fName.contains("licenseagreement") || fName.contains(
                    "licenceagreement")) {
                    OatLogUtil.logLicenseFile(this.getClass().getSimpleName(),
                        oatProject.getPath() + "\tOTHERLICENSEFILE\t" + shortFileUnderProject + "\t" + name + "\t"
                            + LicenseHeaderText);
                }
            }
        }
        OatPolicyVerifyAnalyser.analyseProjectRoot(this.oatFileDocument, metaData, oatProject, prjPath);
        if (this.oatFileDocument.isDirectory()) {
            return;
        }

        // need readfile readme.opensource and check the software version future
        // SkipedFile is only for files
        final String isSkiped = this.oatFileDocument.getData("isSkipedFile");
        if (isSkiped.equals("true")) {
            if (this.oatConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                OatLogUtil.warn(this.getClass().getSimpleName(),
                    oatProject.getPath() + "\tSkipedFile\t" + shortFileUnderProject);

            }
            return;
        }

        MetaData.Datum documentCategory = null;
        if (OatFileUtils.isArchiveFile(this.oatFileDocument)) {
            documentCategory = MetaData.RAT_DOCUMENT_CATEGORY_DATUM_ARCHIVE;
            this.oatFileDocument.getMetaData().set(documentCategory);
        } else if (OatFileUtils.isBinaryFile(this.oatFileDocument)) {
            documentCategory = MetaData.RAT_DOCUMENT_CATEGORY_DATUM_BINARY;
            this.oatFileDocument.getMetaData().set(documentCategory);
        }

        final String docType = metaData.value(RAT_URL_DOCUMENT_CATEGORY);
        if (docType != null && (docType.equals("archive") || docType.equals("binary"))) {
            this.verifyFileType(this.oatFileDocument, oatPolicy, shortFileUnderProject);
        }

        if (docType == null || !docType.equals("standard")) {
            return;
        }

        this.verifyLicenseHeader(this.oatFileDocument, oatPolicy, shortFileUnderProject);
        this.verifyCompatibility(this.oatFileDocument, oatPolicy, shortFileUnderProject);
        if (!OatFileUtils.isNote(this.oatFileDocument)) {
            this.verifyImport(this.oatFileDocument, oatPolicy, shortFileUnderProject);
            this.verifyCopyright(this.oatFileDocument, oatPolicy, shortFileUnderProject);
        }
    }

    private static void analyseProjectRoot(final OatFileDocument document, final MetaData metaData,
        final OatProject oatProject, final String prjPath) {
        if (!document.isProjectRoot()) {
            return;
        }
        if (!oatProject.isUpstreamPrj()) {
            final List<String> licenseFiles = document.getListData("LICENSEFILE");
            OatMetaData.setMetaData(metaData, "LicenseFile", "false");
            for (final String licenseFile : licenseFiles) {
                final String shortFileName = licenseFile.replace(prjPath, "");
                if (shortFileName.equals("LICENSE")) {
                    OatMetaData.setMetaData(metaData, "LicenseFile", "true");
                }
            }
        }

        final String[] defLicenseFiles = oatProject.getLicenseFiles();
        if (defLicenseFiles.length <= 0) {
            return;
        }

        boolean hasAllLicense = true;
        for (final String defLicenseFile : defLicenseFiles) {
            if (defLicenseFile != null && defLicenseFile.trim().length() > 0) {
                final String filepath = document.getName() + "/" + defLicenseFile;
                final File file = new File(filepath);
                if (!file.exists()) {
                    hasAllLicense = false;
                }
            }
        }
        if (hasAllLicense) {
            OatMetaData.setMetaData(metaData, "LicenseFile", "true");
        } else {
            OatMetaData.setMetaData(metaData, "LicenseFile", "false");
        }
    }

    private ValidResult isValid(final Document subject, final String name, final OatPolicyItem policyItem) {
        // 0:init,1:true,2:false
        String piName = policyItem.getName();
        final ValidResult validResult = new ValidResult(policyItem);

        // "*" means allow all in this group
        if (piName.equals("*")) {
            validResult.valid = 1;
            return validResult;
        }

        final boolean canusename = !piName.startsWith("!");

        if (!canusename) {
            try {
                piName = piName.substring(1);
            } catch (final Exception e) {
                OatLogUtil.traceException(e);
                return null;
            }
            if (this.isPolicyOk(name, piName, policyItem.getType())) {
                validResult.valid = 2;
            } else {
                validResult.valid = 1;
            }
            return validResult;
        }

        if (this.isPolicyOk(name, piName, policyItem.getType())) {
            validResult.valid = 1;
        } else {
            // not contains piName true false
            validResult.valid = 2;
        }

        // For license and compatibility type

        return validResult;
    }

    private boolean isPolicyOk(final String name, final String piName, final String policyType) {
        final boolean result = name.contains(piName);
        if (result) {
            return true;
        }
        if (!policyType.equals("compatibility")) {
            return false;
        }
        final List<String> compatibilityLicenseList = this.oatConfig.getLicenseCompatibilityMap().get(piName);
        if (compatibilityLicenseList != null) {
            for (final String compatibilityLicense : compatibilityLicenseList) {
                if (name.contains(compatibilityLicense)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isFiltered(final OatFileFilter fileFilter, final String fullPathFromBasedir,
        final String fileName, final OatFileDocument subject) {
        if (fileName != null && fileName.length() > 0) {
            for (OatFileFilterItem fileFilterItem : fileFilter.getFileFilterItems()) {
                // 用文件名匹配，如果匹配成功，则本策略要忽略此文件，故返回false
                Pattern pattern = null;
                try {
                    String fileFilterNameItem = fileFilterItem.getName().replace(subject.getOatProject().getPath(), "");
                    fileFilterNameItem = fileFilterItem.getName().replace("*", ".*");
                    pattern = OatMatchUtils.compilePattern(fileFilterNameItem);
                } catch (final Exception e) {
                    OatLogUtil.traceException(e);
                    return false;
                }
                if (pattern == null) {
                    return false;
                }
                final boolean needFilter = OatMatchUtils.matchPattern(fileName, pattern);
                if (needFilter) {
                    if (fileFilter.IsRefInfoInvaildWhenBinaryFileFilter(fileFilterItem) ) {
                        return false;
                    }
                    // need add reason desc to print all message in output file future
                    return true;
                }
            }
        }

        for (final OatFileFilterItem filePathFilterItem : fileFilter.getOatFilePathFilterItems()) {
            // 用从根目录开始的路径匹配，如果匹配成功，则本策略要忽略此文件，故返回false
            final Pattern pattern = OatMatchUtils.compilePattern(filePathFilterItem.getName());
            if (pattern == null) {
                return false;
            }
            final boolean needFilter = OatMatchUtils.matchPattern(fullPathFromBasedir, pattern);
            if (needFilter) {
                if (fileFilter.IsRefInfoInvaildWhenBinaryFileFilter(filePathFilterItem) ) {
                    return false;
                }
                // need add reason desc to print all message in output file future
                return true;
            }
        }
        return false;
    }

    private void verifySpecialWorld(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath,
        final String line) {
        final List<OatPolicyItem> specialWorldPolicyItems = oatPolicy.getPolicyItems("specialworld");
        boolean isApproved = false;
        if (line == null) {
            return;
        }
        isApproved = this.verify(subject, filePath, line, specialWorldPolicyItems);
        if (!isApproved) {
            subject.getMetaData().set(new MetaData.Datum("specialworld-approval", "" + isApproved));
            String specialline = subject.getMetaData().value("specialworld-line");
            specialline = specialline == null ? line : specialline + "|" + line;
            subject.getMetaData().set(new MetaData.Datum("specialworld-line", specialline));
        }
    }

    private boolean verify(final OatFileDocument subject, final String filePath, final String nameToCheck,
        final List<OatPolicyItem> policyItems) {
        final String[] names = OatCfgUtil.getSplitStrings(nameToCheck);
        final List<OatPolicyItem> mustList = new ArrayList<>();
        final List<OatPolicyItem> mayList = new ArrayList<>();
        for (final OatPolicyItem policyItem : policyItems) {
            if (policyItem.getRule().equals("must")) {
                mustList.add(policyItem);
            } else {
                mayList.add(policyItem);
            }
        }

        // 先校验must
        final boolean mustIsApproved;
        mustIsApproved = this.isApproved(subject, filePath, nameToCheck, mustList);

        if (!mustIsApproved) {
            return false; // must如不满足由直接返回false
        }

        boolean endApproved = true;

        for (final String singleName : names) {
            if (singleName == null || singleName.trim().length() <= 0) {
                continue;
            }
            final boolean mayIsApproved = this.isApproved(subject, filePath, singleName, mayList);

            if (!mayIsApproved) {
                endApproved = false;
                break;
            }
        }

        return endApproved;
    }

    private boolean isApproved(final OatFileDocument subject, final String filePath, final String name,
        final List<OatPolicyItem> oatPolicyItemList) {
        final boolean isApproved;
        final ValidResultSet validResultSet = new ValidResultSet();
        for (final OatPolicyItem policyItem : oatPolicyItemList) {
            final boolean matched = this.isMatched(subject, filePath, policyItem);

            if (matched) {
                final ValidResult validResult = this.isValid(subject, name, policyItem);
                // Add reason desc to print all message in output file
                if (validResult.valid == 1 && (!policyItem.getName().startsWith("!"))) {
                    final String key = policyItem.getType() + "-ApprovedReason";
                    // subject.addListData(key, policyItem.getName() + ":" + policyItem.getDesc());
                }
                validResultSet.addValidResult(validResult);
            }
        }

        if (validResultSet.resultList.size() > 0) {
            isApproved = validResultSet.isValid();
        } else {
            isApproved = true;
        }
        return isApproved;
    }

    private boolean isMatched(final OatFileDocument subject, final String shortFilePathUnderPrj,
        final OatPolicyItem policyItem) {
        String piPath = policyItem.getPath();
        final boolean canusepath = !piPath.startsWith("!");
        String tmpPiPath = piPath;
        if (!canusepath) {
            tmpPiPath = piPath.substring(1);
        }

        if (tmpPiPath.startsWith("projectroot/")) {
            tmpPiPath = tmpPiPath.replace("projectroot/", subject.getOatProject().getPath());
        }
        if (canusepath) {
            piPath = tmpPiPath;
        } else {
            piPath = "!" + tmpPiPath;
        }
        if ("projectroot".equals(piPath)) {
            // in default OAT.xml
            piPath = subject.getOatProject().getPath();
        }

        final OatFileFilter fileFilter = policyItem.getFileFilterObj();
        String subjectname = subject.getName();
        if (subject.isDirectory()) {
            subjectname = subjectname + "/";
        }
        String fullPathFromBasedir = OatCfgUtil.getShortPath(this.oatConfig, subjectname);

        if (this.oatConfig.isPluginMode()) {
            fullPathFromBasedir = subject.getOatProject().getPath() + fullPathFromBasedir;
        }
        String fileName = shortFilePathUnderPrj;
        if (shortFilePathUnderPrj.indexOf("/") >= 0) {
            fileName = shortFilePathUnderPrj.substring(shortFilePathUnderPrj.lastIndexOf("/") + 1);
        }

        // process filter operations
        if (fileFilter != null) {
            final String lastFilterResult = subject.getData("FilterResult:" + fileFilter.getName());
            if (null != lastFilterResult && lastFilterResult.length() > 0) {
                if (lastFilterResult.equals("true")) {
                    return false;
                }
            } else {
                if (OatPolicyVerifyAnalyser.isFiltered(fileFilter, fullPathFromBasedir, fileName, subject)) {
                    subject.putData("FilterResult:" + fileFilter.getName(), "true");
                    return false;
                } else {
                    subject.putData("FilterResult:" + fileFilter.getName(), "false");
                }
            }
        }

        boolean mached = false;
        final String lastMatchResult = subject.getData("MatchResult:" + policyItem.getPath());
        if (null != lastMatchResult && lastMatchResult.length() > 0) {
            mached = lastMatchResult.equals("true");
        } else {
            if (!canusepath) {
                try {
                    piPath = piPath.substring(1);
                } catch (final Exception e) {
                    OatLogUtil.warn(this.getClass().getSimpleName(),
                        subject.getOatProject().getPath() + "\tisMatched failed\t" + shortFilePathUnderPrj);
                    OatLogUtil.traceException(e);
                }

                final Pattern pattern = OatMatchUtils.compilePattern(piPath);
                if (pattern == null) {
                    return false;
                }
                mached = !OatMatchUtils.matchPattern(fullPathFromBasedir, pattern);
            } else {
                final Pattern pattern = OatMatchUtils.compilePattern(piPath);
                if (pattern == null) {
                    return false;
                }
                mached = OatMatchUtils.matchPattern(fullPathFromBasedir, pattern);
            }
            subject.putData("MatchResult:" + policyItem.getPath(), mached ? "true" : "false");
        }

        return mached;
    }

    private void verifyFileName(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath) {
        final List<OatPolicyItem> fileNamePolicyItems = oatPolicy.getPolicyItems("filename");
        final List<OatPolicyItem> licenseFilePolicyItems = new ArrayList<>();
        final List<OatPolicyItem> readmeFilePolicyItems = new ArrayList<>();
        final List<OatPolicyItem> readmeopensourceFilePolicyItems = new ArrayList<>();
        for (final OatPolicyItem fileNamePolicyItem : fileNamePolicyItems) {
            final String name = fileNamePolicyItem.getName();
            if (name.equals("LICENSE")) {
                licenseFilePolicyItems.add(fileNamePolicyItem);
                continue;
            }
            if (name.equals("README.OpenSource")) {
                readmeopensourceFilePolicyItems.add(fileNamePolicyItem);
                continue;
            }
            if (name.startsWith("README")) {
                readmeFilePolicyItems.add(fileNamePolicyItem);
                continue;
            }
        }
        if (licenseFilePolicyItems.size() > 0) {
            this.checkFileInDir(subject, filePath, licenseFilePolicyItems, "LICENSEFILE", "LicenseFile");
        }
        if (readmeFilePolicyItems.size() > 0) {
            this.checkFileInDir(subject, filePath, readmeFilePolicyItems, "README", "Readme");
        }
        if (readmeopensourceFilePolicyItems.size() > 0) {
            this.checkFileInDir(subject, filePath, readmeopensourceFilePolicyItems, "README.OpenSource",
                "ReadmeOpenSource");
        }
    }

    private void checkFileInDir(final OatFileDocument subject, final String filePath,
        final List<OatPolicyItem> fileNamePolicyItems, final String policyFileName, final String outputName) {
        final List<String> list = subject.getOatProject().getProjectFileDocument().getListData(policyFileName);
        final String thisDir = OatCfgUtil.getShortPath(this.oatConfig, subject.getName() + "/");
        String name = "";
        if (list != null && list.size() > 0) {
            final StringBuffer buffer = new StringBuffer();
            for (final String fileName : list) {
                if (!fileName.contains(thisDir)) {
                    continue;
                }
                final String tmpStr = fileName.replace(thisDir, "");
                if (!tmpStr.contains("/")) {
                    // only check files in this dir layer
                    buffer.append(name).append(" ").append(fileName);
                }
            }
            name = buffer.toString();
        }
        if (name.equals("")) {
            name = "NULL";
        }
        final boolean isApproved = this.verify(subject, filePath, name, fileNamePolicyItems);
        subject.getMetaData().set(new MetaData.Datum(outputName, "" + isApproved));
    }

    private void verifyFileType(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath) {
        final String name = subject.getMetaData().value(RAT_URL_DOCUMENT_CATEGORY);
        if (name == null) {
            return;
        }
        final List<OatPolicyItem> fileTypePolicyItems = oatPolicy.getPolicyItems("filetype");
        boolean isApproved = false;
        isApproved = this.verify(subject, filePath, name, fileTypePolicyItems);
        subject.getMetaData().set(new MetaData.Datum("fileType", "" + isApproved));
    }

    private void verifyLicenseHeader(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath) {
        String name = subject.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);

        if (name == null) {
            subject.getMetaData().set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, "NoLicenseHeader"));
            subject.getMetaData().set(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "NoLicenseHeader"));
            name = subject.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        }
        if (name.contains(" AND ")) {
            name = name.replace(" AND ", "|");
        }
        final List<OatPolicyItem> licensePolicyItems = oatPolicy.getPolicyItems("license");
        boolean isApproved = false;
        isApproved = this.verify(subject, filePath, name, licensePolicyItems);
        subject.getMetaData()
            .set(isApproved ? MetaData.RAT_APPROVED_LICENSE_DATIM_TRUE : MetaData.RAT_APPROVED_LICENSE_DATIM_FALSE);
    }

    private void verifyCompatibility(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath) {
        String name = subject.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);

        if (name == null || name.contains("?") || name.equals("SameLicense") || name.equals("NoLicenseHeader")) {
            return;
        }
        if (name.contains(" AND ")) {
            name = name.replace(" AND ", "|");
        }
        final List<OatPolicyItem> compatibilityPolicyItems = oatPolicy.getPolicyItems("compatibility");
        boolean isApproved = false;
        isApproved = this.verify(subject, filePath, name, compatibilityPolicyItems);
        subject.getMetaData().set(new MetaData.Datum("compatibility", "" + isApproved));
    }

    private void verifyImport(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath) {
        final String name = subject.getMetaData().value("import-name");
        final List<OatPolicyItem> importPolicyItems = oatPolicy.getPolicyItems("import");
        boolean isApproved = false;
        if (name == null) {
            return;
        }
        isApproved = this.verify(subject, filePath, name, importPolicyItems);
        if (!isApproved) {
            final String importname = subject.getMetaData().value("import-name");
            if (null != importname) {
                final StringBuffer strbuilder = new StringBuffer();
                final String[] imports = OatCfgUtil.getSplitStrings(importname);
                if (imports != null) {
                    OatPolicyVerifyAnalyser.fillImportName(subject, importPolicyItems, strbuilder, imports);
                }
            }
        }
        subject.getMetaData().set(new MetaData.Datum("import-name-type", "import/include invalid"));
        subject.getMetaData().set(new MetaData.Datum("import-name-approval", "" + isApproved));
    }

    private static void fillImportName(final OatFileDocument subject, final List<OatPolicyItem> importPolicyItems,
        final StringBuffer strbuilder, final String[] imports) {
        for (final String anImport : imports) {
            for (final OatPolicyItem importPolicyItem : importPolicyItems) {
                if (anImport.contains(importPolicyItem.getName().replace("!", ""))) {
                    strbuilder.append(anImport).append("|");
                    subject.getMetaData().set(new MetaData.Datum("import-name", strbuilder.toString()));
                }
            }
        }
    }

    private void verifyCopyright(final OatFileDocument subject, final OatPolicy oatPolicy, final String filePath) {
        String name = subject.getMetaData().value("copyright-owner");
        final List<OatPolicyItem> copyrightPolicyItems = oatPolicy.getPolicyItems("copyright");
        boolean isApproved = false;
        if (name == null) {
            name = "NULL";
            subject.getMetaData().set(new MetaData.Datum("copyright-owner", name));
        }
        isApproved = this.verify(subject, filePath, name, copyrightPolicyItems);
        subject.getMetaData().set(new MetaData.Datum("copyright-owner-approval", "" + isApproved));
        subject.getMetaData().set(new MetaData.Datum("copyright-owner-type", "copyright invalid"));
    }

    private static class ValidResult {
        private static final String RULE_MAY = "may";

        private static final String RULE_MUST = "must";

        private final String rule;

        private final String group;

        private final String desc;

        private int valid; // 0:init,1:true,2:false

        private ValidResult(final OatPolicyItem policyItem) {
            this.rule = policyItem.getRule();
            this.group = policyItem.getGroup();
            this.desc = policyItem.getDesc();
        }

        private boolean isMust() {
            return ValidResult.RULE_MUST.equals(this.rule);
        }

        private boolean isMay() {
            return ValidResult.RULE_MAY.equals(this.rule);
        }

        private String getDesc() {
            return this.desc;
        }
    }

    private static class ValidResultSet {
        private final List<ValidResult> resultList = new ArrayList<>();

        private boolean isValid() {
            // 先匹配Must类型Policy,如果有不满足，则直接False
            for (final ValidResult validResult : this.resultList) {
                if (validResult.isMust()) {
                    if (validResult.valid != 1) {
                        return false;
                    }
                }
            }

            final Map<String, List<ValidResult>> map = new HashMap();
            for (final ValidResult validResult : this.resultList) {
                if (validResult.isMay()) {
                    String group = validResult.group;
                    if (group.trim().length() <= 0) {
                        group = "notRequired";
                    }
                    List<ValidResult> lst = map.get(group);
                    if (lst != null) {
                        lst.add(validResult);
                    } else {
                        lst = new ArrayList<>();
                        lst.add(validResult);
                        map.put(group, lst);
                    }
                }
            }
            boolean endValid = true;
            // 针对各组May进行分析，如果有一个必须的组是False，则结果为False
            for (final Map.Entry<String, List<ValidResult>> entry : map.entrySet()) {
                final List<ValidResult> lst = entry.getValue();
                // 只处理必须的组
                if (entry.getKey().equals("notRequired")) {
                    continue;
                }
                boolean isvalid = false;
                // 针对每一个May的分组，如果有一个是true，则结果为true，否则为false
                for (final ValidResult validResult : lst) {
                    if (validResult.valid == 1) {
                        isvalid = true;
                        break;
                    }
                }
                if (!isvalid) {
                    endValid = false;
                    break;
                }
            }

            return endValid;
        }

        private void addValidResult(final ValidResult validResult) {
            this.resultList.add(validResult);
        }
    }
}
