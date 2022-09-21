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

import ohos.oat.analysis.matcher.IOatMatcher;
import ohos.oat.analysis.matcher.copyright.OatCopyrightMatcher;
import ohos.oat.analysis.matcher.dependency.OatImportMatcher;
import ohos.oat.analysis.matcher.license.custom.OatCustomizedTextLicenseMatcher;
import ohos.oat.analysis.matcher.license.full.Apache2License;
import ohos.oat.analysis.matcher.license.full.BSD0ClauseLicense;
import ohos.oat.analysis.matcher.license.full.BSD1ClauseLicense;
import ohos.oat.analysis.matcher.license.full.BSD1ClauseLicense2;
import ohos.oat.analysis.matcher.license.full.BSD2ClauseLicense;
import ohos.oat.analysis.matcher.license.full.BSD2ClauseLicense2;
import ohos.oat.analysis.matcher.license.full.BSD3ClauseLicense;
import ohos.oat.analysis.matcher.license.full.BSD3ClauseLicense2;
import ohos.oat.analysis.matcher.license.full.BSD3ClauseLicense3;
import ohos.oat.analysis.matcher.license.full.BSD3ClauseLicense4;
import ohos.oat.analysis.matcher.license.full.BSD3ClauseLicense5;
import ohos.oat.analysis.matcher.license.full.BSD3ClauseLicense6;
import ohos.oat.analysis.matcher.license.full.BSD4ClauseLicense;
import ohos.oat.analysis.matcher.license.full.BSDStyleLicense;
import ohos.oat.analysis.matcher.license.full.CDDL1License;
import ohos.oat.analysis.matcher.license.full.CDDL1License2;
import ohos.oat.analysis.matcher.license.full.FreeBSDLicense;
import ohos.oat.analysis.matcher.license.full.GPL1License;
import ohos.oat.analysis.matcher.license.full.GPL2License;
import ohos.oat.analysis.matcher.license.full.GPL2WithClassPathExceptionLicense;
import ohos.oat.analysis.matcher.license.full.GPL3License;
import ohos.oat.analysis.matcher.license.full.GPLStyleLicense2;
import ohos.oat.analysis.matcher.license.full.LGPLLicense;
import ohos.oat.analysis.matcher.license.full.LGPLStyleLicense2;
import ohos.oat.analysis.matcher.license.full.LGPLStyleLicense3;
import ohos.oat.analysis.matcher.license.full.LibertyLicense2;
import ohos.oat.analysis.matcher.license.full.MITLicense;
import ohos.oat.analysis.matcher.license.full.MITLicense2;
import ohos.oat.analysis.matcher.license.full.MITLicense3;
import ohos.oat.analysis.matcher.license.full.MITLicense5;
import ohos.oat.analysis.matcher.license.full.NuttXBSDLicense;
import ohos.oat.analysis.matcher.license.full.OpenSSLLicense;
import ohos.oat.analysis.matcher.license.full.OriginalBSDLicense;
import ohos.oat.analysis.matcher.license.full.ZlibLicense;
import ohos.oat.analysis.matcher.license.full.ZopePublicLicense;
import ohos.oat.analysis.matcher.license.simple.Apache2License2;
import ohos.oat.analysis.matcher.license.simple.ApacheStyleLicense;
import ohos.oat.analysis.matcher.license.simple.ApplePublicSourceLicense;
import ohos.oat.analysis.matcher.license.simple.BSDStyleLicense2;
import ohos.oat.analysis.matcher.license.simple.BSLLicense;
import ohos.oat.analysis.matcher.license.simple.CreativeCommonsAttribution4InternationalPublicLicense;
import ohos.oat.analysis.matcher.license.simple.CurlLicense;
import ohos.oat.analysis.matcher.license.simple.DerivedLicense;
import ohos.oat.analysis.matcher.license.simple.DojoLicense;
import ohos.oat.analysis.matcher.license.simple.EndUserLicenseAgreement;
import ohos.oat.analysis.matcher.license.simple.FreeTypeProjectLicense;
import ohos.oat.analysis.matcher.license.simple.GPL2License2;
import ohos.oat.analysis.matcher.license.simple.GPLStyleLicense;
import ohos.oat.analysis.matcher.license.simple.InvalidLicense;
import ohos.oat.analysis.matcher.license.simple.LGPLStyleLicense;
import ohos.oat.analysis.matcher.license.simple.LibpngLicense;
import ohos.oat.analysis.matcher.license.simple.MITLicense1;
import ohos.oat.analysis.matcher.license.simple.MulanLicense;
import ohos.oat.analysis.matcher.license.simple.OpenSSLLicense2;
import ohos.oat.analysis.matcher.license.simple.PublicDomainLicense;
import ohos.oat.analysis.matcher.license.simple.TMF854License;
import ohos.oat.analysis.matcher.license.simple.ThirdPartyNotice;
import ohos.oat.analysis.matcher.license.simple.W3CDocLicense;
import ohos.oat.analysis.matcher.license.simple.W3CLicense;
import ohos.oat.analysis.matcher.license.simple.XConsortiumLicense;
import ohos.oat.analysis.matcher.license.simple.ZlibLibpngLicense2;
import ohos.oat.analysis.matcher.license.spdx.OatLicense;
import ohos.oat.analysis.matcher.license.spdx.OatSpdxLabelLicenseMatcher;
import ohos.oat.analysis.matcher.license.spdx.OatSpdxTextLicenseExceptionMatcher;
import ohos.oat.analysis.matcher.license.spdx.OatSpdxTextLicenseMatcher;
import ohos.oat.config.OatConfig;
import ohos.oat.document.IOatDocument;
import ohos.oat.utils.OatCfgUtil;
import ohos.oat.utils.OatLicenseTextUtil;
import ohos.oat.utils.OatLogUtil;

import org.apache.commons.io.IOUtils;
import org.apache.rat.analysis.RatHeaderAnalysisException;

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
public class OatFileAnalyser {
    private int processLineCount = 150;

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
        "doxyfile"

    };

    private final List<String> oriHeaderTextList = new ArrayList<>();

    private final StringBuffer oriHeaderText = new StringBuffer();

    private final List<String> cleanHeaderTextList = new ArrayList<>();

    private final StringBuffer cleanHeaderText = new StringBuffer();

    private final List<IOatMatcher> defaultHeaderMatchersOri = new ArrayList<>();

    private final List<IOatMatcher> defaultHeaderMatchersClean = new ArrayList<>();

    private final List<IOatMatcher> defaultHeaderMatchersExceptionClean = new ArrayList<>();

    private final List<IOatMatcher> definedHeaderMatchersOri = new ArrayList<>();

    private final List<IOatMatcher> definedHeaderMatchersClean = new ArrayList<>();

    private final List<IOatMatcher> definedHeaderMatchersCleanGpl = new ArrayList<>();

    private final List<IOatMatcher> customizedHeaderMatchersClean = new ArrayList<>();

    private final BufferedReader fileReader;

    private final OatConfig oatConfig;

    private final IOatDocument fileDocument;

    private int headerLinesToRead;

    private String repoDisplayName = "";

    private boolean firstLineEnough = false;

    /**
     * Constructor
     *
     * @param fileReader File reader of the fileDocument, not null
     * @param fileDocument FileDocument of the file to be checked, not null
     */
    public OatFileAnalyser(final Reader fileReader, final IOatDocument fileDocument, final OatConfig oatConfig) {
        this.fileReader = new BufferedReader(fileReader);
        this.fileDocument = fileDocument;

        this.oatConfig = oatConfig;
        if (this.fileDocument != null) {
            this.repoDisplayName = this.fileDocument.getOatProject().getPath();
        }
        if (this.repoDisplayName.endsWith("/")) {
            this.repoDisplayName = this.repoDisplayName.substring(0, this.repoDisplayName.length() - 1);
        }
        this.repoDisplayName = this.repoDisplayName.replace("/", "_");
        final String licensefilename = OatCfgUtil.getShortPath(this.oatConfig, fileDocument.getName());
        if (this.fileDocument != null) {
            if (this.fileDocument.getOatProject()
                .getProjectFileDocument()
                .getListData("LICENSEFILE")
                .contains(licensefilename)) {
                this.processLineCount = 3000;
            }
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
        this.defaultHeaderMatchersOri.add(new OatCopyrightMatcher());
        this.defaultHeaderMatchersOri.add(new OatImportMatcher());
        this.defaultHeaderMatchersOri.add(new InvalidLicense());
        this.defaultHeaderMatchersOri.add(new OatSpdxLabelLicenseMatcher());

        // matchers only need the cleaned and lowercase texts
        final List<OatLicense> spdxLicenseList = this.oatConfig.getLicenseList();
        for (final OatLicense oatLicense : spdxLicenseList) {
            this.defaultHeaderMatchersClean.add(
                new OatSpdxTextLicenseMatcher(oatLicense.getLicenseId(), oatLicense.getLicenseHeaderText(),
                    oatLicense.getUrls()));
        }
        final List<OatLicense> spdxLicenseExceptionList = this.oatConfig.getExceptionLicenseList();
        for (final OatLicense oatLicense : spdxLicenseExceptionList) {
            this.defaultHeaderMatchersExceptionClean.add(
                new OatSpdxTextLicenseExceptionMatcher(oatLicense.getLicenseId(), oatLicense.getLicenseHeaderText(),
                    oatLicense.getUrls()));
        }

    }

    private void initDefinedMatchers() {
        this.definedHeaderMatchersClean.add(new Apache2License());
        this.definedHeaderMatchersClean.add(new MITLicense());
        this.definedHeaderMatchersClean.add(new MITLicense2());
        this.definedHeaderMatchersClean.add(new MITLicense3());
        this.definedHeaderMatchersClean.add(new MITLicense5());
        this.definedHeaderMatchersClean.add(new W3CLicense());
        this.definedHeaderMatchersClean.add(new W3CDocLicense());
        this.definedHeaderMatchersClean.add(new DojoLicense());
        this.definedHeaderMatchersClean.add(new TMF854License());
        this.definedHeaderMatchersClean.add(new CDDL1License());
        this.definedHeaderMatchersClean.add(new CDDL1License2());
        this.definedHeaderMatchersClean.add(new BSD2ClauseLicense());
        this.definedHeaderMatchersClean.add(new BSD2ClauseLicense2());
        this.definedHeaderMatchersClean.add(new BSD3ClauseLicense());
        this.definedHeaderMatchersClean.add(new BSD3ClauseLicense2());
        this.definedHeaderMatchersClean.add(new BSD3ClauseLicense3());
        this.definedHeaderMatchersClean.add(new BSD3ClauseLicense4());
        this.definedHeaderMatchersClean.add(new BSD3ClauseLicense5());
        this.definedHeaderMatchersClean.add(new BSD3ClauseLicense6());
        this.definedHeaderMatchersClean.add(new BSD4ClauseLicense());
        this.definedHeaderMatchersClean.add(new OriginalBSDLicense());
        this.definedHeaderMatchersClean.add(new ZopePublicLicense());
        this.definedHeaderMatchersClean.add(new FreeBSDLicense());
        this.definedHeaderMatchersClean.add(new BSD1ClauseLicense());
        this.definedHeaderMatchersClean.add(new BSD1ClauseLicense2());
        this.definedHeaderMatchersClean.add(new BSD0ClauseLicense());
        this.definedHeaderMatchersClean.add(new NuttXBSDLicense());
        this.definedHeaderMatchersClean.add(new ZlibLicense());
        this.definedHeaderMatchersClean.add(new ZlibLibpngLicense2());
        this.definedHeaderMatchersClean.add(new MITLicense1());
        this.definedHeaderMatchersClean.add(new MulanLicense());
        this.definedHeaderMatchersClean.add(new LibertyLicense2());
        this.definedHeaderMatchersClean.add(new XConsortiumLicense());
        this.definedHeaderMatchersClean.add(new PublicDomainLicense());
        this.definedHeaderMatchersClean.add(new CreativeCommonsAttribution4InternationalPublicLicense());
        this.definedHeaderMatchersClean.add(new CurlLicense());
        this.definedHeaderMatchersClean.add(new FreeTypeProjectLicense());
        this.definedHeaderMatchersClean.add(new LibpngLicense());
        this.definedHeaderMatchersClean.add(new ApplePublicSourceLicense());
        this.definedHeaderMatchersClean.add(new OpenSSLLicense());
        this.definedHeaderMatchersClean.add(new OpenSSLLicense2());
        this.definedHeaderMatchersClean.add(new Apache2License2());
        this.definedHeaderMatchersClean.add(new EndUserLicenseAgreement());
        this.definedHeaderMatchersClean.add(new ThirdPartyNotice());
        this.definedHeaderMatchersClean.add(new BSLLicense());
        this.definedHeaderMatchersClean.add(new DerivedLicense());
        this.definedHeaderMatchersClean.add(new BSDStyleLicense());
        this.definedHeaderMatchersClean.add(new BSDStyleLicense2());
        this.definedHeaderMatchersClean.add(new ApacheStyleLicense());
        this.definedHeaderMatchersClean.add(new GPL2WithClassPathExceptionLicense());
        this.definedHeaderMatchersClean.add(new GPL1License());
        this.definedHeaderMatchersClean.add(new GPL2License());
        this.definedHeaderMatchersClean.add(new GPL2License2());
        this.definedHeaderMatchersClean.add(new GPL3License());
        this.definedHeaderMatchersClean.add(new LGPLLicense());
        this.definedHeaderMatchersCleanGpl.add(new GPLStyleLicense2());
        this.definedHeaderMatchersCleanGpl.add(new LGPLStyleLicense2());
        this.definedHeaderMatchersCleanGpl.add(new LGPLStyleLicense3());
        this.definedHeaderMatchersOri.add(new LGPLStyleLicense());
        this.definedHeaderMatchersOri.add(new GPLStyleLicense());

    }

    private void initCustomizedMatchers() {
        for (final Map.Entry<String, List<String>> stringListEntry : this.oatConfig.getLicenseText2NameMap()
            .entrySet()) {
            for (final String licenseTxt : stringListEntry.getValue()) {
                this.customizedHeaderMatchersClean.add(new OatCustomizedTextLicenseMatcher(stringListEntry.getKey(),
                    OatLicenseTextUtil.cleanAndLowerCaseLetter(licenseTxt)));
            }
        }
        if (this.fileDocument != null) {
            for (final Map.Entry<String, List<String>> stringListEntry : this.fileDocument.getOatProject()
                .getPrjLicenseText2NameMap()
                .entrySet()) {
                for (final String licenseTxt : stringListEntry.getValue()) {
                    this.customizedHeaderMatchersClean.add(new OatCustomizedTextLicenseMatcher(stringListEntry.getKey(),
                        OatLicenseTextUtil.cleanAndLowerCaseLetter(licenseTxt)));
                }
            }
        }
    }

    public void read() throws RatHeaderAnalysisException {

        this.headerLinesToRead = this.processLineCount;
        try {
            while (this.readLine()) {
                // do nothing
            }
        } catch (final IOException e) {
            throw new RatHeaderAnalysisException("Read file failed: " + this.fileDocument, e);
        }
        IOUtils.closeQuietly(this.fileReader);
        if (this.processLineCount - this.headerLinesToRead < 3) {
            this.fileDocument.putData("isSkipedFile", "true");
        }
        final String tmp = this.fileDocument.getData("isSkipedFile");
        if (tmp.equals("true")) {
            return;
        }

        // match header text with header matchers
        this.match();
    }

    private void match() throws RatHeaderAnalysisException {
        for (final IOatMatcher iOatMatcher : this.defaultHeaderMatchersOri) {
            for (final String line : this.oriHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }
        String licenseName = this.fileDocument.getData("LicenseName");
        if (IOatMatcher.stopWhileMatchedSpdx(licenseName)) {
            return;
        }
        for (final IOatMatcher iOatMatcher : this.defaultHeaderMatchersClean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }
        for (final IOatMatcher iOatMatcher : this.definedHeaderMatchersClean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }
        for (final IOatMatcher iOatMatcher : this.definedHeaderMatchersOri) {
            for (final String line : this.oriHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }
        for (final IOatMatcher iOatMatcher : this.definedHeaderMatchersCleanGpl) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }

        for (final IOatMatcher iOatMatcher : this.customizedHeaderMatchersClean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }
        for (final IOatMatcher iOatMatcher : this.defaultHeaderMatchersExceptionClean) {
            for (final String line : this.cleanHeaderTextList) {
                if (this.matchLine(line, iOatMatcher)) {
                    break;
                }
            }
        }

        licenseName = this.fileDocument.getData("LicenseName");
        if (licenseName.length() <= 0 || (licenseName.equals("InvalidLicense"))) {
            this.fileDocument.putData("LicenseHeaderText", this.oriHeaderText.toString().replace("\n", " "));
        }
    }

    private boolean matchLine(final String line, final IOatMatcher matcher) throws RatHeaderAnalysisException {
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
                fileDocument.addListData("README.OpenSource.LicenseName", tmpLicenseStr);
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
            for (final String skipString : OatFileAnalyser.SKIP_STRINGS) {
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
        if (this.processLineCount - this.headerLinesToRead > lineToProcess) {
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
                return !line.startsWith("import ") && !line.startsWith("export ") && !line.startsWith("class ")
                    && !line.startsWith("export ");
            }
        }
        return true;
    }

}
