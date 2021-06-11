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

import ohos.oat.config.OhosConfig;
import ohos.oat.config.OhosFileFilter;
import ohos.oat.config.OhosMetaData;
import ohos.oat.config.OhosPolicy;
import ohos.oat.config.OhosPolicyItem;
import ohos.oat.config.OhosProject;
import ohos.oat.document.OhosFileDocument;
import ohos.oat.utils.OhosCfgUtil;
import ohos.oat.utils.OhosFileUtils;
import ohos.oat.utils.OhosLogUtil;

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;
import org.apache.rat.document.IDocumentAnalyser;
import org.apache.rat.document.RatDocumentAnalysisException;

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
public class OhosPostAnalyser4Output implements IDocumentAnalyser {
    private final OhosConfig ohosConfig;

    /**
     * Constructor
     *
     * @param ohosConfig Data structure with OAT.xml information
     */
    public OhosPostAnalyser4Output(final OhosConfig ohosConfig) {
        this.ohosConfig = ohosConfig;
    }

    @Override
    public void analyse(final Document subject) throws RatDocumentAnalysisException {
        if (subject == null) {
            return;
        }

        OhosFileDocument document = null;
        if (subject instanceof OhosFileDocument) {
            document = (OhosFileDocument) subject;
        } else {
            return;
        }

        final MetaData metaData = document.getMetaData();
        final String baseDir = this.ohosConfig.getBasedir();
        final OhosProject ohosProject = document.getOhosProject();
        final OhosPolicy ohosPolicy = ohosProject.getOhosPolicy();
        String prjPath = ohosProject.getPath();
        if (this.ohosConfig.isPluginMode()) {
            prjPath = "";
        }
        String shortFileUnderProject = document.getName().replace(baseDir + prjPath, "");
        if (document.isDirectory()) {
            // If the doc is directory, add "/"
            shortFileUnderProject = (document.getName() + "/").replace(baseDir + prjPath, "");
            this.verifyFileName(document, ohosPolicy, shortFileUnderProject);
        }

        final String[] defLicenseFiles = ohosProject.getLicenseFiles();
        final String name = document.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        final String LicenseHeaderText = document.getData("LicenseHeaderText");
        boolean findIt = false;
        if (!document.isDirectory()) {
            if (shortFileUnderProject.equals("LICENSE")) {
                findIt = true;
                OhosLogUtil.logLicenseFile(this.getClass().getSimpleName(),
                    ohosProject.getPath() + "\tLICENSEFILE\t" + shortFileUnderProject + "\t" + name + "\t"
                        + LicenseHeaderText);
            }
            for (final String defLicenseFile : defLicenseFiles) {
                if (shortFileUnderProject.endsWith(defLicenseFile)) {
                    findIt = true;
                    OhosLogUtil.logLicenseFile(this.getClass().getSimpleName(),
                        ohosProject.getPath() + "\tDEFLICENSEFILE\t" + shortFileUnderProject + "\t" + name + "\t"
                            + LicenseHeaderText);
                }
            }
        }

        if (!findIt && !document.isDirectory()) {
            final String fName = document.getFileName().toLowerCase(Locale.ENGLISH);
            if (!fName.contains(".") || fName.endsWith(".md") || fName.endsWith(".txt") || fName.endsWith(".html")
                || fName.endsWith(".htm") || fName.endsWith(".pdf")) {
                if (fName.contains("license") || fName.contains("licence") || fName.contains("copying")
                    || fName.contains("copyright") || fName.contains("licenseagreement") || fName.contains(
                    "licenceagreement")) {
                    OhosLogUtil.logLicenseFile(this.getClass().getSimpleName(),
                        ohosProject.getPath() + "\tOTHERLICENSEFILE\t" + shortFileUnderProject + "\t" + name + "\t"
                            + LicenseHeaderText);
                }
            }
        }
        OhosPostAnalyser4Output.analyseProjectRoot(document, metaData, ohosProject, prjPath);
        if (document.isDirectory()) {
            return;
        }

        // need readfile readme.opensource and check the software version future
        // SkipedFile is only for files
        final String isSkiped = document.getData("isSkipedFile");
        if (isSkiped.equals("true")) {
            if (this.ohosConfig.getData("TraceSkippedAndIgnoredFiles").equals("true")) {
                OhosLogUtil.warn(this.getClass().getSimpleName(),
                    ohosProject.getPath() + "\tSkipedFile\t" + shortFileUnderProject);

            }
            return;
        }

        MetaData.Datum documentCategory = null;
        if (OhosFileUtils.isArchiveFile(document)) {
            documentCategory = MetaData.RAT_DOCUMENT_CATEGORY_DATUM_ARCHIVE;
            document.getMetaData().set(documentCategory);
        } else if (OhosFileUtils.isBinaryFile(document)) {
            documentCategory = MetaData.RAT_DOCUMENT_CATEGORY_DATUM_BINARY;
            document.getMetaData().set(documentCategory);
        }

        final String docType = metaData.value(RAT_URL_DOCUMENT_CATEGORY);
        if (docType != null && (docType.equals("archive") || docType.equals("binary"))) {
            this.verifyFileType(document, ohosPolicy, shortFileUnderProject);
        }

        if (!docType.equals("standard")) {
            return;
        }

        this.verifyLicenseHeader(document, ohosPolicy, shortFileUnderProject);
        this.verifyCompatibility(document, ohosPolicy, shortFileUnderProject);
        if (!OhosFileUtils.isNote(document)) {
            this.verifyImport(document, ohosPolicy, shortFileUnderProject);
            this.verifyCopyright(document, ohosPolicy, shortFileUnderProject);
        }
    }

    private static void analyseProjectRoot(final OhosFileDocument document, final MetaData metaData,
        final OhosProject ohosProject, final String prjPath) {
        if (!document.isProjectRoot()) {
            return;
        }
        if (!ohosProject.getPath().contains("third_party")) {
            final List<String> licenseFiles = document.getListData("LICENSEFILE");
            OhosMetaData.setMetaData(metaData, "LicenseFile", "false");
            for (final String licenseFile : licenseFiles) {
                final String shortFileName = licenseFile.replace(prjPath, "");
                if (shortFileName.equals("LICENSE")) {
                    OhosMetaData.setMetaData(metaData, "LicenseFile", "true");
                }
            }
        }

        final String[] defLicenseFiles = ohosProject.getLicenseFiles();
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
            OhosMetaData.setMetaData(metaData, "LicenseFile", "true");
        } else {
            OhosMetaData.setMetaData(metaData, "LicenseFile", "false");
        }
    }

    private static ValidResult isValid(final Document subject, final String name, final OhosPolicyItem policyItem) {
        final int tmp = 0; // 0:init,1:true,2:false
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
                OhosLogUtil.traceException(e);
                return null;
            }
            if (name.contains(piName)) {
                validResult.valid = 2;
            } else {
                validResult.valid = 1;
            }
            return validResult;
        }

        if (name.contains(piName)) {
            validResult.valid = 1;
        } else {
            // not contains piName true false
            validResult.valid = 2;
        }
        return validResult;
    }

    private static boolean isFiltered(final OhosFileFilter fileFilter, final String fullPathFromBasedir,
        final String fileName, final OhosFileDocument subject) {
        for (String fileFilterItem : fileFilter.getFileFilterItems()) {
            // 用文件名匹配，如果匹配成功，则本策略要忽略此文件，故返回false
            Pattern pattern = null;
            try {
                fileFilterItem = fileFilterItem.replace("*", ".*");
                pattern = Pattern.compile(fileFilterItem, Pattern.CASE_INSENSITIVE);
            } catch (final Exception e) {
                OhosLogUtil.traceException(e);
            }
            final boolean needFilter = pattern.matcher(fileName).matches();
            if (needFilter) {
                // need add reason desc to print all message in output file future
                return true;
            }
        }

        for (final String filePathFilterItem : fileFilter.getOhosFilePathFilterItems()) {
            // 用从根目录开始的路径匹配，如果匹配成功，则本策略要忽略此文件，故返回false
            final Pattern pattern = Pattern.compile(filePathFilterItem, Pattern.CASE_INSENSITIVE);
            final boolean needFilter = pattern.matcher(fullPathFromBasedir).matches();
            if (needFilter) {
                // need add reason desc to print all message in output file future
                return true;
            }
        }
        return false;
    }

    private void verifySpecialWorld(final OhosFileDocument subject, final OhosPolicy ohosPolicy, final String filePath,
        final String line) {
        final List<OhosPolicyItem> specialWorldPolicyItems = ohosPolicy.getPolicyItems("specialworld");
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

    private boolean verify(final OhosFileDocument subject, final String filePath, final String nameToCheck,
        final List<OhosPolicyItem> policyItems) {
        final String[] names = OhosCfgUtil.getSplitStrings(nameToCheck);
        final List<OhosPolicyItem> mustList = new ArrayList<>();
        final List<OhosPolicyItem> mayList = new ArrayList<>();
        for (final OhosPolicyItem policyItem : policyItems) {
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

    private boolean isApproved(final OhosFileDocument subject, final String filePath, final String name,
        final List<OhosPolicyItem> ohosPolicyItemList) {
        final boolean isApproved;
        final ValidResultSet validResultSet = new ValidResultSet();
        for (final OhosPolicyItem policyItem : ohosPolicyItemList) {
            final boolean matched = this.isMatched(subject, filePath, policyItem);

            if (matched) {
                final ValidResult validResult = OhosPostAnalyser4Output.isValid(subject, name, policyItem);
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

    private boolean isMatched(final OhosFileDocument subject, final String shortFilePathUnderPrj,
        final OhosPolicyItem policyItem) {
        String piPath = policyItem.getPath();
        if ("projectroot".equals(piPath)) {
            // in default OAT.xml
            piPath = subject.getOhosProject().getPath();
        }

        final boolean canusepath = !piPath.startsWith("!");
        final OhosFileFilter fileFilter = policyItem.getFileFilterObj();
        String fullPathFromBasedir = OhosCfgUtil.getShortPath(this.ohosConfig, subject.getName());
        if (subject.isDirectory()) {
            fullPathFromBasedir = fullPathFromBasedir + "/";
        }
        if (this.ohosConfig.isPluginMode()) {
            fullPathFromBasedir = subject.getOhosProject().getPath() + fullPathFromBasedir;
        }
        String fileName = shortFilePathUnderPrj;
        if (shortFilePathUnderPrj.indexOf("/") >= 0) {
            fileName = shortFilePathUnderPrj.substring(shortFilePathUnderPrj.lastIndexOf("/") + 1);
        }

        // process filter operations
        if (fileFilter != null) {
            if (OhosPostAnalyser4Output.isFiltered(fileFilter, fullPathFromBasedir, fileName, subject)) {
                return false;
            }
        }

        boolean mached = false;
        if (!canusepath) {
            try {
                piPath = piPath.substring(1);
            } catch (final Exception e) {
                OhosLogUtil.warn(this.getClass().getSimpleName(),
                    subject.getOhosProject().getPath() + "\tisMatched failed\t" + shortFilePathUnderPrj);
                OhosLogUtil.traceException(e);
            }

            final Pattern pattern = Pattern.compile(piPath, Pattern.CASE_INSENSITIVE);
            mached = !pattern.matcher(fullPathFromBasedir).matches();
        } else {
            final Pattern pattern = Pattern.compile(piPath, Pattern.CASE_INSENSITIVE);
            mached = pattern.matcher(fullPathFromBasedir).matches();
        }
        return mached;
    }

    private void verifyFileName(final OhosFileDocument subject, final OhosPolicy ohosPolicy, final String filePath) {
        final List<OhosPolicyItem> fileNamePolicyItems = ohosPolicy.getPolicyItems("filename");
        final List<OhosPolicyItem> licenseFilePolicyItems = new ArrayList<>();
        final List<OhosPolicyItem> readmeFilePolicyItems = new ArrayList<>();
        final List<OhosPolicyItem> readmeopensourceFilePolicyItems = new ArrayList<>();
        for (final OhosPolicyItem fileNamePolicyItem : fileNamePolicyItems) {
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

    private void checkFileInDir(final OhosFileDocument subject, final String filePath,
        final List<OhosPolicyItem> fileNamePolicyItems, final String policyFileName, final String outputName) {
        final List<String> list = subject.getOhosProject().getProjectFileDocument().getListData(policyFileName);
        String name = "";
        if (list != null && list.size() > 0) {
            for (final String fileName : list) {
                final String thisDir = OhosCfgUtil.getShortPath(this.ohosConfig, subject.getName()) + "/";
                final String tmpStr = fileName.replace(thisDir, "");
                if (!tmpStr.contains("/")) {
                    // only check files in this dir layer
                    name = name + " " + fileName;
                }
            }
        }
        if (name.equals("")) {
            name = "NULL";
        }
        final boolean isApproved = this.verify(subject, filePath, name, fileNamePolicyItems);
        subject.getMetaData().set(new MetaData.Datum(outputName, "" + isApproved));
    }

    private void verifyFileType(final OhosFileDocument subject, final OhosPolicy ohosPolicy, final String filePath) {
        final String name = subject.getMetaData().value(RAT_URL_DOCUMENT_CATEGORY);
        if (name == null) {
            return;
        }
        final List<OhosPolicyItem> fileTypePolicyItems = ohosPolicy.getPolicyItems("filetype");
        boolean isApproved = false;
        isApproved = this.verify(subject, filePath, name, fileTypePolicyItems);
        subject.getMetaData().set(new MetaData.Datum("fileType", "" + isApproved));
    }

    private void verifyLicenseHeader(final OhosFileDocument subject, final OhosPolicy ohosPolicy,
        final String filePath) {
        String name = subject.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);

        if (name == null) {
            subject.getMetaData().set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, "NoLicenseHeader"));
            subject.getMetaData().set(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, "NoLicenseHeader"));
            name = subject.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        }
        final List<OhosPolicyItem> licensePolicyItems = ohosPolicy.getPolicyItems("license");
        boolean isApproved = false;
        isApproved = this.verify(subject, filePath, name, licensePolicyItems);
        subject.getMetaData().set( //
            isApproved ? MetaData.RAT_APPROVED_LICENSE_DATIM_TRUE : MetaData.RAT_APPROVED_LICENSE_DATIM_FALSE);
    }

    private void verifyCompatibility(final OhosFileDocument subject, final OhosPolicy ohosPolicy,
        final String filePath) {
        final String name = subject.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);

        if (name == null || name.contains("?") || name.equals("SameLicense") || name.equals("NoLicenseHeader")) {
            return;
        }
        final List<OhosPolicyItem> compatibilityPolicyItems = ohosPolicy.getPolicyItems("compatibility");
        boolean isApproved = false;
        isApproved = this.verify(subject, filePath, name, compatibilityPolicyItems);
        subject.getMetaData().set(new MetaData.Datum("compatibility", "" + isApproved));
    }

    private void verifyImport(final OhosFileDocument subject, final OhosPolicy ohosPolicy, final String filePath) {
        final String name = subject.getMetaData().value("import-name");
        final List<OhosPolicyItem> importPolicyItems = ohosPolicy.getPolicyItems("import");
        boolean isApproved = false;
        if (name == null) {
            return;
        }
        isApproved = this.verify(subject, filePath, name, importPolicyItems);
        if (!isApproved) {
            final String importname = subject.getMetaData().value("import-name");
            if (null != importname) {
                final StringBuffer strbuilder = new StringBuffer();
                final String[] imports = OhosCfgUtil.getSplitStrings(importname);
                if (imports != null) {
                    this.fillImportName(subject, importPolicyItems, strbuilder, imports);
                }
            }
        }
        subject.getMetaData().set(new MetaData.Datum("import-name-type", "import/include invalid"));
        subject.getMetaData().set(new MetaData.Datum("import-name-approval", "" + isApproved));
    }

    private void fillImportName(final OhosFileDocument subject, final List<OhosPolicyItem> importPolicyItems,
        final StringBuffer strbuilder, final String[] imports) {
        for (final String anImport : imports) {
            for (final OhosPolicyItem importPolicyItem : importPolicyItems) {
                if (anImport.contains(importPolicyItem.getName().replace("!", ""))) {
                    strbuilder.append(anImport).append("|");
                    subject.getMetaData().set(new MetaData.Datum("import-name", strbuilder.toString()));
                }
            }
        }
    }

    private void verifyCopyright(final OhosFileDocument subject, final OhosPolicy ohosPolicy, final String filePath) {
        String name = subject.getMetaData().value("copyright-owner");
        final List<OhosPolicyItem> copyrightPolicyItems = ohosPolicy.getPolicyItems("copyright");
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

        private ValidResult(final OhosPolicyItem policyItem) {
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
            for (final String groupkey : map.keySet()) {
                final List<ValidResult> lst = map.get(groupkey);
                // 只处理必须的组
                if (groupkey.equals("notRequired")) {
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
                if (isvalid == false) {
                    endValid = false;
                }
            }

            return endValid;
        }

        private void addValidResult(final ValidResult validResult) {
            this.resultList.add(validResult);
        }
    }
}
