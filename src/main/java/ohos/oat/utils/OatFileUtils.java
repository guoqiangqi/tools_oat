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
 * Derived from Apache Creadur Rat BinaryGuesser, ArchiveGuesser, GuessUtils, the original license and notice text is
 *  at the end of
 * the LICENSE file of this project.
 *
 * ChangeLog:
 * 2021.1 -  Add license file names to discard copyright header matching.
 * 2021.3 -  Change file names and extensions according to the openharmony architecture.
 * 2021.6 -  Add saveJson2File, readJsonFromFile util methods to support store and load spdx license texts.
 * Modified by jalenchen
 */

package ohos.oat.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import ohos.oat.analysis.matcher.license.spdx.OatLicense;
import ohos.oat.document.IOatDocument;

import org.apache.commons.io.IOUtils;
import org.apache.rat.document.impl.guesser.BinaryGuesser;
import org.apache.rat.document.impl.guesser.GuessUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * Stateless utility class for identify files which donot need copyright headers
 *
 * @author chenyaxun
 * @since 1.0
 */
public class OatFileUtils {
    private static final String[] LICENSE_NOTE_FILE_NAMES = {
        "NOTICE", "LICENSE", "NOTICE.md", "LICENSE.md", "LICENSE.TXT", "NOTICE.TXT", "INSTALL", "INSTALL.TXT",
        "INSTALL.md", "README", "README.TXT", "README.md", "NEWS", "NEWS.TXT", "NEWS.md", "AUTHOR", "AUTHOR.TXT",
        "AUTHOR.md", "AUTHORS", "AUTHORS.txt", "AUTHORS.md", "CHANGELOG", "CHANGELOG.TXT", "CHANGELOG.md", "DISCLAIMER",
        "DISCLAIMER.TXT", "DISCLAIMER.md", "KEYS", "KEYS.TXT", "KEYS.md", "RELEASE-NOTES", "RELEASE-NOTES.TXT",
        "RELEASE-NOTES.md", "RELEASE_NOTES", "RELEASE_NOTES.TXT", "RELEASE_NOTES.md", "UPGRADE", "UPGRADE.TXT",
        "UPGRADE.md", "STATUS", "STATUS.TXT", "STATUS.md", "THIRD_PARTY_NOTICES", "THIRD_PARTY_NOTICES.TXT",
        "THIRD_PARTY_NOTICES.md", "COPYRIGHT", "COPYRIGHT.TXT", "COPYRIGHT.md", "COPYING", "COPYING.TXT", "COPYING.md",
        "DEPENDENCIES"
    };

    private static final String[] LICENSE_NOTE_FILE_EXTENSIONS = {
        "LICENSE", "LICENSE.TXT", "NOTICE", "NOTICE.TXT",
    };

    private static final String[] ARCHIVE_FILE_EXTENSION = new String[] {
        "jar", "gz", "zip", "tar", "bz", "bz2", "rar", "war", "ear", "mar", "par", "xar", "odb", "odf", "odg", "odp",
        "ods", "odt", "har", "sar", "wsr", "tgz", "xz", "7z", "lz4", "rpm", "deb", "img"
    };

    private static final String[] PREBUILD_FILE_EXTENSION = new String[] {
        "scr", "elf", "bin", "ocx", "cpl", "drv", "sys", "vxd","jar", "gz", "zip", "tar",
            "bz", "bz2", "rar","tgz", "xz", "7z","lz4", "rpm", "deb", "class","img", "hap",
            "exe", "dll", "lib", "so", "a", "o","exp",
            "CLASS", "PYD", "OBJ", "PYC"
    };

    private static final String[] SOURCE_FILE_EXTENSION = new String[] {
        "js", "ts", "c", "cpp", "java", "py", "rb"
    };

    /**
     * API for identify files which donot need copyright headers
     *
     * @param document file to identify
     * @return files which donot need copyright headers
     */
    public static boolean isNote(final IOatDocument document) {
        return OatFileUtils.isNote(document.getName());
    }

    private static boolean isNote(final String name) {
        if (name == null) {
            return false;
        }

        final List<String> licenseNames = Arrays.asList(OatFileUtils.LICENSE_NOTE_FILE_NAMES);
        final String normalisedName = GuessUtils.normalise(name);

        if (licenseNames.contains(name) || licenseNames.contains(normalisedName)) {
            return true;
        }

        for (int i = 0; i < OatFileUtils.LICENSE_NOTE_FILE_EXTENSIONS.length; i++) {
            if (normalisedName.endsWith("." + OatFileUtils.LICENSE_NOTE_FILE_EXTENSIONS[i])) {
                return true;
            }
        }

        return false;
    }

    public static boolean isArchiveFile(final IOatDocument document) {
        return OatFileUtils.isArchiveFile(document.getName());
    }

    private static boolean isArchiveFile(final String name) {
        if (name == null) {
            return false;
        } else {
            final String nameToLower = name.toLowerCase(Locale.US);

            for (int i = 0; i < OatFileUtils.ARCHIVE_FILE_EXTENSION.length; ++i) {
                if (nameToLower.endsWith("." + OatFileUtils.ARCHIVE_FILE_EXTENSION[i])) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isPreBuildFile(final String name) {
        if (name != null) {
            final String nameToLower = name.toLowerCase(Locale.US);
            for (int i = 0; i < OatFileUtils.PREBUILD_FILE_EXTENSION.length; ++i) {
                if (nameToLower.endsWith("." + OatFileUtils.PREBUILD_FILE_EXTENSION[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isSourceFile(final String name) {
        if (name != null) {
            final String nameToLower = name.toLowerCase(Locale.US);
            for (int i = 0; i < OatFileUtils.SOURCE_FILE_EXTENSION.length; ++i) {
                if (nameToLower.endsWith("." + OatFileUtils.SOURCE_FILE_EXTENSION[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBinaryFile(final IOatDocument document) {
        final String fileName = GuessUtils.normalise(document.getName());
        final boolean isNotBinary = BinaryGuesser.isNonBinary(fileName);
        if (isNotBinary || OatFileUtils.isSourceFile(fileName)) {
            return false;
        }
        return BinaryGuesser.isBinary(document.getName()) || OatFileUtils.isBinaryDocument(document)
            || OatFileUtils.isPreBuildFile(fileName);
    }

    private static boolean isBinaryDocument(final IOatDocument document) {
        boolean result = false;
        InputStream stream = null;

        try {
            stream = document.inputStream();
            result = BinaryGuesser.isBinary(stream);
        } catch (final IOException var7) {
            result = false;
        } finally {
            IOUtils.closeQuietly(stream);
        }

        return result;
    }

    public static void saveJson2File(final String jsonString, final String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            try {
                final boolean success = file.createNewFile();
                if (!success) {
                    OatLogUtil.traceException(new Exception("create file error"));
                }
            } catch (final IOException e) {
                OatLogUtil.traceException(e);
            }
        }
        try (final BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            writer.write("");
            writer.write(jsonString);
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
    }

    public static <T> List<T> readJsonFromFile(final String filePath, final Class<OatLicense> t) {
        //        final File file = new File(filePath);
        // if (!file.exists()) {
        //     return null;
        // }
        final URL url = OatFileUtils.class.getResource(filePath);
        if (null == url) {
            return null;
        }

        String readJson = "";
        try (final InputStream fileInputStream = OatFileUtils.class.getResourceAsStream(filePath);
            final InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            final BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String tempString = null;
            final StringBuffer buffer = new StringBuffer();
            buffer.append(readJson);
            while ((tempString = reader.readLine()) != null) {
                buffer.append(tempString);
            }
            readJson = buffer.toString();
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
        try {
            final List<T> jsonObject = (List<T>) JSONObject.parseArray(readJson, t);
            return jsonObject;
        } catch (final JSONException e) {
            OatLogUtil.traceException(e);
        }
        return null;
    }

    public static String getFileCanonicalPath(final File file) {
        String filepath = "";
        try {
            filepath = file.getCanonicalPath();
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
        return filepath;
    }


    public static void decompressGZipFiles(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             GzipCompressorInputStream gzipInputStream = new GzipCompressorInputStream(fileInputStream);
             TarArchiveInputStream tarInputStream = new TarArchiveInputStream(gzipInputStream);) {
             TarArchiveEntry entry;
             while ((entry = tarInputStream.getNextTarEntry()) != null) {
                 if (entry.isDirectory()) {
                     continue; // 跳过目录
                 }
                 File curfile = new File(file.getParent(), entry.getName());
                 File parent = curfile.getParentFile();
                 if (!parent.exists()) {
                     parent.mkdirs();
                 }
                 IOUtils.copy(tarInputStream, new FileOutputStream(curfile));
            }
        }
    }
}

