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

import ohos.oat.analysis.headermatcher.OhosCopyrightMatcher;
import ohos.oat.analysis.headermatcher.OhosCustomizedTextMatchingLicense;
import ohos.oat.analysis.headermatcher.OhosImportMatcher;
import ohos.oat.analysis.headermatcher.OhosLicense;
import ohos.oat.analysis.headermatcher.SPDXLicenseMatcher;
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
import ohos.oat.analysis.headermatcher.fulltext.LibertyLicense2;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense2;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense3;
import ohos.oat.analysis.headermatcher.fulltext.MITLicense5;
import ohos.oat.analysis.headermatcher.fulltext.NuttXBSDLicense;
import ohos.oat.analysis.headermatcher.fulltext.OpenSSLLicense;
import ohos.oat.analysis.headermatcher.fulltext.OriginalBSDLicense;
import ohos.oat.analysis.headermatcher.fulltext.ZlibLibpngLicense;
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
import ohos.oat.analysis.headermatcher.simplepattern.OhosSimplePatternLicense;
import ohos.oat.analysis.headermatcher.simplepattern.OpenSSLLicense2;
import ohos.oat.analysis.headermatcher.simplepattern.PublicDomainLicense;
import ohos.oat.analysis.headermatcher.simplepattern.SameLicense;
import ohos.oat.analysis.headermatcher.simplepattern.TMF854License;
import ohos.oat.analysis.headermatcher.simplepattern.ThirdPartyNotice;
import ohos.oat.analysis.headermatcher.simplepattern.UnicodeLicense;
import ohos.oat.analysis.headermatcher.simplepattern.W3CDocLicense;
import ohos.oat.analysis.headermatcher.simplepattern.W3CLicense;
import ohos.oat.analysis.headermatcher.simplepattern.XConsortiumLicense;
import ohos.oat.analysis.headermatcher.simplepattern.ZlibLibpngLicense2;
import ohos.oat.config.OhosConfig;
import ohos.oat.document.OhosFileDocument;
import ohos.oat.utils.OhosLicenseTextUtil;
import ohos.oat.utils.OhosLogUtil;

import org.apache.commons.io.IOUtils;
import org.apache.rat.analysis.IHeaderMatcher;
import org.apache.rat.analysis.RatHeaderAnalysisException;
import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Main processor of oat, this will read every source files and trigger the header matcher to match source lines.
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OhosProcessor {
    private static final int PROCESS_LINE_COUNT = 150;

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
        "Generated from ", "generated from reading UI file", "Generated by javadoc"
    };

    private final List<IHeaderMatcher> headerMatchers = Collections.unmodifiableList(
        Arrays.asList(new SPDXLicenseMatcher(), new Apache2License(), new GPL1License(), new GPL2License(),
            new GPL2License2(), new GPL3License(), new LGPLLicense(), new MITLicense(), new MITLicense2(),
            new MITLicense3(), new MITLicense5(), new W3CLicense(), new W3CDocLicense(), new DojoLicense(),
            new TMF854License(), new CDDL1License(), new CDDL1License2(), new BSD2ClauseLicense(),
            new BSD2ClauseLicense2(), new BSD3ClauseLicense(), new BSD3ClauseLicense2(), new BSD3ClauseLicense3(),
            new BSD3ClauseLicense4(), new BSD3ClauseLicense5(), new BSD3ClauseLicense6(), new BSD4ClauseLicense(),
            new OriginalBSDLicense(), new ZopePublicLicense(), new FreeBSDLicense(), new BSD1ClauseLicense(),
            new BSD1ClauseLicense2(), new BSD0ClauseLicense(), new NuttXBSDLicense(),
            new GPL2WithClassPathExceptionLicense(), new ZlibLibpngLicense(), new ZlibLibpngLicense2(),
            new OhosCopyrightMatcher(), new MITLicense1(), new MulanLicense(), new LibertyLicense2(),
            new UnicodeLicense(), new XConsortiumLicense(), new PublicDomainLicense(),
            new CreativeCommonsAttribution4InternationalPublicLicense(), new CurlLicense(),
            new FreeTypeProjectLicense(), new LibpngLicense(), new ApplePublicSourceLicense(), new OpenSSLLicense(),
            new OpenSSLLicense2(), new OhosImportMatcher(), new GPLStyleLicense(), new LGPLStyleLicense(),
            new GPLStyleLicense2(), new Apache2License2(), new BSDStyleLicense(), new BSDStyleLicense2(),
            new ApacheStyleLicense(), new EndUserLicenseAgreement(), new ThirdPartyNotice(), new InvalidLicense(),
            new BSLLicense(), new SameLicense(), new DerivedLicense()));

    private final BufferedReader fileReader;

    private final OhosConfig ohosConfig;

    private OhosFileDocument fileDocument;

    private int headerLinesToRead;

    private boolean finished = false;

    private String repoDisplayName = "";

    /**
     * Constructor
     *
     * @param fileReader File reader of the fileDocument, not null
     * @param fileDocument FileDocument of the file to be checked, not null
     */
    public OhosProcessor(final Reader fileReader, final Document fileDocument, final OhosConfig ohosConfig) {
        this.fileReader = new BufferedReader(fileReader);
        if (fileDocument instanceof OhosFileDocument) {
            this.fileDocument = (OhosFileDocument) fileDocument;
        }
        this.ohosConfig = ohosConfig;
        this.repoDisplayName = this.fileDocument.getOhosProject().getPath();
        if (this.repoDisplayName.endsWith("/")) {
            this.repoDisplayName = this.repoDisplayName.substring(0, this.repoDisplayName.length() - 1);
        }
        this.repoDisplayName = this.repoDisplayName.replace("/", "_");
    }

    public void read() throws RatHeaderAnalysisException {
        if (!this.finished) {
            final StringBuffer headers = new StringBuffer();
            this.headerLinesToRead = OhosProcessor.PROCESS_LINE_COUNT;
            try {
                while (this.readLine(headers)) {
                    // do nothing
                }
                if (OhosProcessor.PROCESS_LINE_COUNT - this.headerLinesToRead < 3) {
                    this.fileDocument.putData("isSkipedFile", "true");
                }

                this.matchWithCustomizedAndSpdx(headers);

            } catch (final IOException e) {
                throw new RatHeaderAnalysisException("Read file failed: " + this.fileDocument, e);
            }
            IOUtils.closeQuietly(this.fileReader);
            this.reset();
        }
        this.finished = true;
    }

    private void matchWithCustomizedAndSpdx(final StringBuffer headers) throws RatHeaderAnalysisException, IOException {
        String name = this.fileDocument.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        final String srcText = headers.toString();
        if (name == null || (name.equals("InvalidLicense"))) {
            OhosLogUtil.info(this.getClass().getSimpleName(),
                "InvalidLicense and process customized matching, fileName:" + this.fileDocument.getFileName());
            for (final Map.Entry<String, List<String>> stringListEntry : this.ohosConfig.getLicenseText2NameMap()
                .entrySet()) {
                for (final String licenseTxt : stringListEntry.getValue()) {
                    new OhosCustomizedTextMatchingLicense(stringListEntry.getKey(), licenseTxt).match(this.fileDocument,
                        srcText);
                }
            }
            for (final Map.Entry<String, List<String>> stringListEntry : this.fileDocument.getOhosProject()
                .getPrjLicenseText2NameMap()
                .entrySet()) {
                for (final String licenseTxt : stringListEntry.getValue()) {
                    new OhosCustomizedTextMatchingLicense(stringListEntry.getKey(), licenseTxt).match(this.fileDocument,
                        srcText);
                }
            }

        }
        name = this.fileDocument.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        if (name != null && (name.equals("InvalidLicense") || (name.contains("Style")))) {
            OhosLogUtil.info(this.getClass().getSimpleName(),
                "InvalidLicense and process spdx matching, fileName:" + this.fileDocument.getFileName());
            this.matchWithSpdx(headers);
        }
        name = this.fileDocument.getMetaData().value(RAT_URL_LICENSE_FAMILY_NAME);
        if (name == null || (name.equals("InvalidLicense"))) {
            this.fileDocument.putData("LicenseHeaderText", srcText.replace("\n", " "));
        }

    }

    private void matchWithSpdx(final StringBuffer headers) {
        final String headerstr = OhosLicenseTextUtil.cleanNoLetterAndCutTemplateFlag(headers.toString(), false)
            .toLowerCase();
        final List<OhosLicense> spdxLicenseList = this.ohosConfig.getLicenseList();
        for (final OhosLicense ohosLicense : spdxLicenseList) {
            if (headerstr.contains(ohosLicense.getLicenseHeaderText())) {
                this.fileDocument.getMetaData()
                    .set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, ohosLicense.getLicenseId()));
                this.fileDocument.getMetaData()
                    .set(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, ohosLicense.getLicenseId()));
                break;
            } else {
                this.matchWithSpdxUrl(headerstr, ohosLicense);
            }
        }
    }

    private void matchWithSpdxUrl(final String headerstr, final OhosLicense ohosLicense) {
        for (final String url : ohosLicense.getUrls()) {
            if (headerstr.contains(url)) {
                this.fileDocument.getMetaData()
                    .set(new MetaData.Datum(MetaData.RAT_URL_HEADER_CATEGORY, ohosLicense.getLicenseId()));
                this.fileDocument.getMetaData()
                    .set(new MetaData.Datum(RAT_URL_LICENSE_FAMILY_NAME, ohosLicense.getLicenseId()));
                break;
            }
        }
    }

    private void reset() {
        for (final IHeaderMatcher matcher : this.headerMatchers) {
            matcher.reset();
        }
    }

    boolean readLine(final StringBuffer headers) throws IOException, RatHeaderAnalysisException {
        final String line = this.fileReader.readLine();
        if (this.fileDocument.getFileName().equals("README.OpenSource")) {
            if (line != null && line.contains("\"License\"")) {
                String tmpLicenseStr = line.replace("\"License\"", "");
                tmpLicenseStr = tmpLicenseStr.replace(":", "");
                tmpLicenseStr = tmpLicenseStr.replace("\"", "");
                tmpLicenseStr = tmpLicenseStr.replace(",", "");
                OhosLogUtil.logLicense(this.getClass().getSimpleName(),
                    this.repoDisplayName + "\t" + "README.OpenSource\t" + tmpLicenseStr.trim());
            }
        } else {
            if (this.ohosConfig.getData("TraceLicenseListOnly").equals("true")) {
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

        headers.append(line);
        headers.append('\n');
        if (line.trim().length() > 0) {
            for (final String skipString : OhosProcessor.SKIP_STRINGS) {
                if (line.contains(skipString)) {
                    this.fileDocument.putData("isSkipedFile", "true");
                    return false;
                }
            }
            this.match(this.fileDocument, line);
        }
        int lineToProcess = 3;
        if (this.ohosConfig.isPluginMode()) {
            lineToProcess = 10;
        }
        if (OhosProcessor.PROCESS_LINE_COUNT - this.headerLinesToRead > lineToProcess) {
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
                return !line.startsWith("function ") && !line.startsWith("var ") && !line.startsWith("class ");
            } else if (fileName.endsWith(".ts")) {
                return !line.startsWith("import ") && !line.startsWith("export ") && !line.startsWith("class ");
            }
        }

        return true;
    }

    private void match(final Document subject, final String line) throws RatHeaderAnalysisException {
        OhosFileDocument document = null;
        if (subject instanceof OhosFileDocument) {
            document = (OhosFileDocument) subject;
        }
        document.addMaxline();
        boolean result = false;
        final String lowerCaseLine = OhosLicenseTextUtil.cleanLetter(line);
        for (final IHeaderMatcher matcher : this.headerMatchers) {
            final boolean isFinished = document.getMatchResult(matcher.getClass().getSimpleName());
            if (isFinished) {
                continue; // 如果该匹配器返回True，则表示已匹配结束，不重复匹配此文件
            }
            if (matcher instanceof OhosSimplePatternLicense) {
                result = matcher.match(document, lowerCaseLine);
            } else {
                result = matcher.match(document, line);
            }
            document.putMatchResult(matcher.getClass().getSimpleName(), result);
        }
    }

}
