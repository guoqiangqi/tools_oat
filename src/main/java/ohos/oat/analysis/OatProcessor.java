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
 * Derived from Apache Creadur Rat HeaderCheckWorker, the original license and notice text is at the end of the
 * LICENSE file of this project.
 *
 * ChangeLog:
 * 2021.2 - Add the following capabilities to support OpenHarmony:
 * 1. Add lots of license type used by openharmony.
 * 2. Add SPDX license text match capability.
 * 3. Change the matching logic, stop matching when the matcher return true.
 * 4. Mark empty files and support skip them while applying policies.
 * Modified by jalenchen
 */

package ohos.oat.analysis;

import static org.apache.rat.api.MetaData.RAT_URL_LICENSE_FAMILY_NAME;

import ohos.oat.analysis.headermatcher.IOatHeaderMatcher;
import ohos.oat.analysis.headermatcher.OatCopyrightMatcher;
import ohos.oat.analysis.headermatcher.OatCustomizedTextLicenseMatcher;
import ohos.oat.analysis.headermatcher.OatImportMatcher;
import ohos.oat.analysis.headermatcher.OatLicense;
import ohos.oat.analysis.headermatcher.OatMatchUtils;
import ohos.oat.analysis.headermatcher.OatSpdxLabelLicenseMatcher;
import ohos.oat.analysis.headermatcher.OatSpdxTextLicenseExceptionMatcher;
import ohos.oat.analysis.headermatcher.OatSpdxTextLicenseMatcher;
import ohos.oat.analysis.headermatcher.fulltext.Apache2License;
import ohos.oat.analysis.headermatcher.fulltext.BSD0ClauseLicense;
import ohos.oat.analysis.headermatcher.fulltext.BSD1ClauseLicense;
import ohos.oat.analysis.headermatcher.fulltext.BSD1ClauseLicense2;
import ohos.oat.analysis.headermatcher.fulltext.BSD2ClauseLicense;
import ohos.oat.analysis.headermatcher.fulltext.BSD2ClauseLicense2;
import ohos.oat.analysis.headermatcher.fulltext.BSD3ClauseLicense;
import ohos.oat.analysis.headermatcher.fulltext.BSD3ClauseLicense2;
import ohos.oat.analysis.headermatcher.fulltext.BSD3ClauseLicense3;
import ohos.oat.analysis.headermatcher.fulltext.BSD3ClauseLicense4;
import ohos.oat.analysis.headermatcher.fulltext.BSD3ClauseLicense5;
import ohos.oat.analysis.headermatcher.fulltext.BSD3ClauseLicense6;
import ohos.oat.analysis.headermatcher.fulltext.BSD4ClauseLicense;
import ohos.oat.analysis.headermatcher.fulltext.BSDStyleLicense;
import ohos.oat.analysis.headermatcher.fulltext.CDDL1License;
import ohos.oat.analysis.headermatcher.fulltext.CDDL1License2;
import ohos.oat.analysis.headermatcher.fulltext.FreeBSDLicense;
import ohos.oat.analysis.headermatcher.fulltext.GPL1License;
import ohos.oat.analysis.headermatcher.fulltext.GPL2License;
import ohos.oat.analysis.headermatcher.fulltext.GPL2WithClassPathExceptionLicense;
import ohos.oat.analysis.headermatcher.fulltext.GPL3License;
import ohos.oat.analysis.headermatcher.fulltext.GPLStyleLicense2;
import ohos.oat.analysis.headermatcher.fulltext.LGPLLicense;
import ohos.oat.analysis.headermatcher.fulltext.LGPLStyleLicense2;
import ohos.oat.analysis.headermatcher.fulltext.LGPLStyleLicense3;
import ohos.oat.analysis.headermatcher.fulltext.LibertyLicense2;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense2;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense3;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense5;
import ohos.oat.analysis.headermatcher.fulltext.NuttXBSDLicense;
import ohos.oat.analysis.headermatcher.fulltext.OpenSSLLicense;
import ohos.oat.analysis.headermatcher.fulltext.OriginalBSDLicense;
import ohos.oat.analysis.headermatcher.fulltext.ZlibLicense;
import ohos.oat.analysis.headermatcher.fulltext.ZopePublicLicense;
import ohos.oat.analysis.headermatcher.simplepattern.Apache2License2;
import ohos.oat.analysis.headermatcher.simplepattern.ApacheStyleLicense;
import ohos.oat.analysis.headermatcher.simplepattern.ApplePublicSourceLicense;
import ohos.oat.analysis.headermatcher.simplepattern.BSDStyleLicense2;
import ohos.oat.analysis.headermatcher.simplepattern.BSLLicense;
import ohos.oat.analysis.headermatcher.simplepattern.CreativeCommonsAttribution4InternationalPublicLicense;
import ohos.oat.analysis.headermatcher.simplepattern.CurlLicense;
import ohos.oat.analysis.headermatcher.simplepattern.DerivedLicense;
import ohos.oat.analysis.headermatcher.simplepattern.DojoLicense;
import ohos.oat.analysis.headermatcher.simplepattern.EndUserLicenseAgreement;
import ohos.oat.analysis.headermatcher.simplepattern.FreeTypeProjectLicense;
import ohos.oat.analysis.headermatcher.simplepattern.GPL2License2;
import ohos.oat.analysis.headermatcher.simplepattern.GPLStyleLicense;
import ohos.oat.analysis.headermatcher.simplepattern.InvalidLicense;
import ohos.oat.analysis.headermatcher.simplepattern.LGPLStyleLicense;
import ohos.oat.analysis.headermatcher.simplepattern.LibpngLicense;
import ohos.oat.analysis.headermatcher.simplepattern.MITLicense1;
import ohos.oat.analysis.headermatcher.simplepattern.MulanLicense;
import ohos.oat.analysis.headermatcher.simplepattern.OpenSSLLicense2;
import ohos.oat.analysis.headermatcher.simplepattern.PublicDomainLicense;
import ohos.oat.analysis.headermatcher.simplepattern.TMF854License;
import ohos.oat.analysis.headermatcher.simplepattern.ThirdPartyNotice;
import ohos.oat.analysis.headermatcher.simplepattern.W3CDocLicense;
import ohos.oat.analysis.headermatcher.simplepattern.W3CLicense;
import ohos.oat.analysis.headermatcher.simplepattern.XConsortiumLicense;
import ohos.oat.analysis.headermatcher.simplepattern.ZlibLibpngLicense2;
import ohos.oat.config.OatConfig;
import ohos.oat.document.OatFileDocument;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLicenseTextUtil;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.io.IOUtils;
import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.api.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main processor of oat, this will read every source files and trigger the header matcher to match source lines.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatProcessor {
    private int PROCESS_LINE_COUNT = 150;

    private static final String[] SKIP_STRINGS = {

        // To be extracted to the configuration file in the future
        "generated by Cayenne", "Generated By:JJTree", "Generated By:JavaCC", "THIS FILE IS AUTOMATICALLY GENERATED",
        "NOTE: this file is autogenerated by XBeans", "This file was automatically generated by ",
        "# WARNING: DO NOT EDIT OR DELETE THIS WORKSPACE FILE!", "# Microsoft Developer Studio Generated NMAKE File",
        "# Microsoft Developer Studio Generated Build File", "Generated from configure.ac by autoheader",
        "generated automatically by aclocal", "build.xml generated by maven from project.xml",
        "This file was generated by", "This file has been automatically generated.",
        "Automatically generated - do not modify!", "Javadoc style sheet", "SOURCE FILE GENERATATED",
        "Generated by the Batik", "this file is autogenerated", "This class was autogenerated", "Generated by Maven",
        "Autogenerated by Thrift", "DO NOT EDIT THIS FILE - it is machine generated", "This class was generated by",
        "This file is auto-generated", "File generated automatically by", "Automatically generated file",
        "Generated from ", "generated from ", "generated from reading UI file", "Generated by javadoc", "Doxyfile",
        "doxyfile", "generated by"

    };

    private final List<String> oriHeaderTextList = new ArrayList<>();

    private final StringBuffer oriHeaderText = new StringBuffer();

    private final List<String> cleanHeaderTextList = new ArrayList<>();

    private final StringBuffer cleanHeaderText = new StringBuffer();

    private final List<IOatHeaderMatcher> defaultHeaderMatchers_ori = new ArrayList<>();

    private final List<IOatHeaderMatcher> defaultHeaderMatchers_clean = new ArrayList<>();

    private final List<IOatHeaderMatcher> defaultHeaderMatchers_exception_clean = new ArrayList<>();

    private final List<IOatHeaderMatcher> definedHeaderMatchers_ori = new ArrayList<>();

    private final List<IOatHeaderMatcher> definedHeaderMatchers_clean = new ArrayList<>();

    private final List<IOatHeaderMatcher> definedHeaderMatchers_clean_gpl = new ArrayList<>();

    private final List<IOatHeaderMatcher> customizedHeaderMatchers_clean = new ArrayList<>();

    private final BufferedReader fileReader;

    private final OatConfig oatConfig;

    private OatFileDocument fileDocument;

    private int headerLinesToRead;

    private String repoDisplayName = "";

    private boolean firstLineEnough = false;

    /**
     * Constructor
     *
     * @param fileReader File reader of the fileDocument, not null
     * @param fileDocument FileDocument of the file to be checked, not null
     */
    public OatProcessor(final Reader fileReader, final Document fileDocument, final OatConfig oatConfig) {
        this.fileReader = new BufferedReader(fileReader);
        if (fileDocument instanceof OatFileDocument) {
            this.fileDocument = (OatFileDocument) fileDocument;
        } else {
            OatLogUtil.error(this.getClass().getSimpleName(), "Document invalid: " + fileDocument.getClass().getName());
        }
        this.oatConfig = oatConfig;
        this.repoDisplayName = this.fileDocument.getOatProject().getPath();
        if (this.repoDisplayName.endsWith("/")) {
            this.repoDisplayName = this.repoDisplayName.substring(0, this.repoDisplayName.length() - 1);
        }
        this.repoDisplayName = this.repoDisplayName.replace("/", "_");
        final String licensefilename = OatCfgUtil.getShortPath(this.oatConfig, fileDocument.getName());
        if (this.fileDocument.getOatProject()
            .getProjectFileDocument()
            .getListData("LICENSEFILE")
            .contains(licensefilename)) {
            this.PROCESS_LINE_COUNT = 3000;
        }

        this.initMatchers();
    }

    private void initMatchers() {
        this.initDefaultMatchers();

        this.initDefinedMatchers();

        this.initCustomizedMatchers();
    }

    private void initDefaultMatchers() {
        // matchers need the original header texts        
        this.defaultHeaderMatchers_ori.add(new OatCopyrightMatcher());
        this.defaultHeaderMatchers_ori.add(new OatImportMatcher());
        this.defaultHeaderMatchers_ori.add(new InvalidLicense());
        this.defaultHeaderMatchers_ori.add(new OatSpdxLabelLicenseMatcher());

        // matchers only need the cleaned and lowercase texts
        final List<OatLicense> spdxLicenseList = this.oatConfig.getLicenseList();
        for (final OatLicense oatLicense : spdxLicenseList) {
            this.defaultHeaderMatchers_clean.add(
                new OatSpdxTextLicenseMatcher(oatLicense.getLicenseId(), oatLicense.getLicenseHeaderText(),
                    oatLicense.getUrls()));
        }
        final List<OatLicense> spdxLicenseExceptionList = this.oatConfig.getExceptionLicenseList();
        for (final OatLicense oatLicense : spdxLicenseExceptionList) {
            this.defaultHeaderMatchers_exception_clean.add(
                new OatSpdxTextLicenseExceptionMatcher(oatLicense.getLicenseId(), oatLicense.getLicenseHeaderText(),
                    oatLicense.getUrls()));
        }

    }

    private void initDefinedMatchers() {
        this.definedHeaderMatchers_clean.add(new Apache2License());
        this.definedHeaderMatchers_clean.add(new MITLicense());
        this.definedHeaderMatchers_clean.add(new MITLicense2());
        this.definedHeaderMatchers_clean.add(new MITLicense3());
        this.definedHeaderMatchers_clean.add(new MITLicense5());
        this.definedHeaderMatchers_clean.add(new W3CLicense());
        this.definedHeaderMatchers_clean.add(new W3CDocLicense());
        this.definedHeaderMatchers_clean.add(new DojoLicense());
        this.definedHeaderMatchers_clean.add(new TMF854License());
        this.definedHeaderMatchers_clean.add(new CDDL1License());
        this.definedHeaderMatchers_clean.add(new CDDL1License2());
        this.definedHeaderMatchers_clean.add(new BSD2ClauseLicense());
        this.definedHeaderMatchers_clean.add(new BSD2ClauseLicense2());
        this.definedHeaderMatchers_clean.add(new BSD3ClauseLicense());
        this.definedHeaderMatchers_clean.add(new BSD3ClauseLicense2());
        this.definedHeaderMatchers_clean.add(new BSD3ClauseLicense3());
        this.definedHeaderMatchers_clean.add(new BSD3ClauseLicense4());
        this.definedHeaderMatchers_clean.add(new BSD3ClauseLicense5());
        this.definedHeaderMatchers_clean.add(new BSD3ClauseLicense6());
        this.definedHeaderMatchers_clean.add(new BSD4ClauseLicense());
        this.definedHeaderMatchers_clean.add(new OriginalBSDLicense());
        this.definedHeaderMatchers_clean.add(new ZopePublicLicense());
        this.definedHeaderMatchers_clean.add(new FreeBSDLicense());
        this.definedHeaderMatchers_clean.add(new BSD1ClauseLicense());
        this.definedHeaderMatchers_clean.add(new BSD1ClauseLicense2());
        this.definedHeaderMatchers_clean.add(new BSD0ClauseLicense());
        this.definedHeaderMatchers_clean.add(new NuttXBSDLicense());
        this.definedHeaderMatchers_clean.add(new ZlibLicense());
        this.definedHeaderMatchers_clean.add(new ZlibLibpngLicense2());
        this.definedHeaderMatchers_clean.add(new MITLicense1());
        this.definedHeaderMatchers_clean.add(new MulanLicense());
        this.definedHeaderMatchers_clean.add(new LibertyLicense2());
        this.definedHeaderMatchers_clean.add(new XConsortiumLicense());
        this.definedHeaderMatchers_clean.add(new PublicDomainLicense());
        this.definedHeaderMatchers_clean.add(new CreativeCommonsAttribution4InternationalPublicLicense());
        this.definedHeaderMatchers_clean.add(new CurlLicense());
        this.definedHeaderMatchers_clean.add(new FreeTypeProjectLicense());
        this.definedHeaderMatchers_clean.add(new LibpngLicense());
        this.definedHeaderMatchers_clean.add(new ApplePublicSourceLicense());
        this.definedHeaderMatchers_clean.add(new OpenSSLLicense());
        this.definedHeaderMatchers_clean.add(new OpenSSLLicense2());
        this.definedHeaderMatchers_clean.add(new Apache2License2());
        this.definedHeaderMatchers_clean.add(new EndUserLicenseAgreement());
        this.definedHeaderMatchers_clean.add(new ThirdPartyNotice());
        this.definedHeaderMatchers_clean.add(new BSLLicense());
        this.definedHeaderMatchers_clean.add(new DerivedLicense());
        this.definedHeaderMatchers_clean.add(new BSDStyleLicense());
        this.definedHeaderMatchers_clean.add(new BSDStyleLicense2());
        this.definedHeaderMatchers_clean.add(new ApacheStyleLicense());
        this.definedHeaderMatchers_clean.add(new GPL2WithClassPathExceptionLicense());
        this.definedHeaderMatchers_clean.add(new GPL1License());
        this.definedHeaderMatchers_clean.add(new GPL2License());
        this.definedHeaderMatchers_clean.add(new GPL2License2());
        this.definedHeaderMatchers_clean.add(new GPL3License());
        this.definedHeaderMatchers_clean.add(new LGPLLicense());
        this.definedHeaderMatchers_clean_gpl.add(new GPLStyleLicense2());
        this.definedHeaderMatchers_clean_gpl.add(new LGPLStyleLicense2());
        this.definedHeaderMatchers_clean_gpl.add(new LGPLStyleLicense3());
        this.definedHeaderMatchers_ori.add(new LGPLStyleLicense());
        this.definedHeaderMatchers_ori.add(new GPLStyleLicense());

    }

    private void initCustomizedMatchers() {
        for (final Map.Entry<String, List<String>> stringListEntry : this.oatConfig.getLicenseText2NameMap()
            .entrySet()) {
            for (final String licenseTxt : stringListEntry.getValue()) {
                this.customizedHeaderMatchers_clean.add(new OatCustomizedTextLicenseMatcher(stringListEntry.getKey(),
                    OatLicenseTextUtil.cleanAndLowerCaseLetter(licenseTxt)));
            }
        }
        for (final Map.Entry<String, List<String>> stringListEntry : this.fileDocument.getOatProject()
            .getPrjLicenseText2NameMap()
            .entrySet()) {
            for (final String licenseTxt : stringListEntry.getValue()) {
                this.customizedHeaderMatchers_clean.add(new OatCustomizedTextLicenseMatcher(stringListEntry.getKey(),
                    OatLicenseTextUtil.cleanAndLowerCaseLetter(licenseTxt)));
            }
        }
    }

    public void read() throws RatHeaderAnalysisException {

        this.headerLinesToRead = this.PROCESS_LINE_COUNT;
        try {
            while (this.readLine()) {
                // do nothing
            }
        } catch (final IOException e) {
            throw new RatHeaderAnalysisException("Read file failed: " + this.fileDocument, e);
        }
        IOUtils.closeQuietly(this.fileReader);
        if (this.PROCESS_LINE_COUNT - this.headerLinesToRead < 3) {
            this.fileDocument.putData("isSkipedFile", "true");
        }
        final String tmp = this.fileDocument.getData("isSkipedFile");
        if (null != tmp && tmp.equals("true")) {
            return;
        }

        // match header text with header matchers
        this.match();
    }

    private void match() throws RatHeaderAnalysisException {
        for (final IOatHeaderMatcher iOatHeaderMatcher : this.defaultHeaderMatchers_ori) {
            for (final String line : this.oriHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }
        String licenseName = this.fileDocument.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        if (OatMatchUtils.stopWhileMatchedSpdx(licenseName)) {
            return;
        }
        for (final IOatHeaderMatcher iOatHeaderMatcher : this.defaultHeaderMatchers_clean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }
        for (final IOatHeaderMatcher iOatHeaderMatcher : this.definedHeaderMatchers_clean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }
        for (final IOatHeaderMatcher iOatHeaderMatcher : this.definedHeaderMatchers_ori) {
            for (final String line : this.oriHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }
        for (final IOatHeaderMatcher iOatHeaderMatcher : this.definedHeaderMatchers_clean_gpl) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }

        for (final IOatHeaderMatcher iOatHeaderMatcher : this.customizedHeaderMatchers_clean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }
        for (final IOatHeaderMatcher iOatHeaderMatcher : this.defaultHeaderMatchers_exception_clean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatHeaderMatcher)) {
                    break;
                }
            }
        }

        licenseName = this.fileDocument.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        if (licenseName == null || (licenseName.equals("InvalidLicense"))) {
            this.fileDocument.putData("LicenseHeaderText", this.oriHeaderText.toString().replace("\n", " "));
        }
    }

    private boolean matchLine(final String line, final IOatHeaderMatcher matcher) throws RatHeaderAnalysisException {
        final boolean result = matcher.match(this.fileDocument, line);
        return result;
    }

    private boolean readLine() throws IOException {
        final String line = this.fileReader.readLine();
        if (this.fileDocument.getFileName().equals("README.OpenSource")) {
            if (line != null && line.contains("\"License\"")) {
                String tmpLicenseStr = line.replace("\"License\"", "");
                tmpLicenseStr = tmpLicenseStr.replace(":", "");
                tmpLicenseStr = tmpLicenseStr.replace("\"", "");
                tmpLicenseStr = tmpLicenseStr.replace(",", "");
                OatLogUtil.logLicense(this.getClass().getSimpleName(),
                    this.repoDisplayName + "\t" + "README.OpenSource\t" + tmpLicenseStr.trim());
            }
        } else {
            if (this.oatConfig.getData("TraceLicenseListOnly").equals("true")) {
                // If has -t para, only trace license list
                return false;
            }
        }
        final int lineleft = this.headerLinesToRead--;
        final boolean result = line != null;
        if (lineleft <= 0) {
            return false;
        }
        if (!result) {
            return true;
        }

        if (line.trim().length() > 0) {
            for (final String skipString : OatProcessor.SKIP_STRINGS) {
                if (line.contains(skipString)) {
                    this.fileDocument.putData("isSkipedFile", "true");
                    return false;
                }
            }
            this.oriHeaderText.append(line);
            this.oriHeaderText.append('\n');
            final String cleanStr = OatLicenseTextUtil.cleanAndLowerCaseLetter(line);
            this.cleanHeaderText.append(cleanStr);
            if (this.firstLineEnough) {
                this.oriHeaderTextList.add(line);
                this.cleanHeaderTextList.add(cleanStr);
            } else {
                // ensure the length of the first line bigger than 20, because the full matchers need check first line.
                if (this.cleanHeaderText.length() > 20) {
                    this.oriHeaderTextList.add(this.oriHeaderText.toString());
                    this.cleanHeaderTextList.add(this.cleanHeaderText.toString());
                    this.firstLineEnough = true;
                }
            }

        }
        int lineToProcess = 3;
        if (this.oatConfig.isPluginMode()) {
            lineToProcess = 10;
        }
        if (this.PROCESS_LINE_COUNT - this.headerLinesToRead > lineToProcess) {
            final String fileName = this.fileDocument.getName();
            if (fileName.endsWith(".java")) {
                return !line.startsWith("public class ") && !line.startsWith("public final ") && !line.startsWith(
                    "public abstract ") && !line.startsWith("public interface ") && !line.startsWith("protected class ")
                    && !line.startsWith("protected final ") && !line.startsWith("protected abstract ")
                    && !line.startsWith("protected interface ") && !line.startsWith("class ") && !line.startsWith(
                    "final class ") && !line.startsWith("interface ") && !line.startsWith("abstract class ");
            } else if (fileName.endsWith(".c") || fileName.endsWith(".cpp") || fileName.endsWith(".h")) {
                return !line.startsWith("#define ") && !line.startsWith("using namespace") && !line.startsWith(
                    "#ifdef ") && !line.startsWith("#ifndef ") && !line.startsWith("#include ") && !line.startsWith(
                    "struct ") && !line.startsWith("void ") && !line.startsWith("#if ") && !line.startsWith("#elif ");
            } else if (fileName.endsWith(".py")) {
                return !line.startsWith("import ") && !line.startsWith("from ") && !line.startsWith("def ")
                    && !line.startsWith("try: ");
            } else if (fileName.endsWith(".js")) {
                return !line.startsWith("function ") && !line.startsWith("var ") && !line.startsWith("class ")
                    && !line.startsWith("export ") && !line.startsWith("import ");
            } else if (fileName.endsWith(".ts")) {
                return !line.startsWith("import ") && !line.startsWith("export ") && !line.startsWith("class ") && !line
                    .startsWith("export ");
            }
        }
        return true;
    }

}
