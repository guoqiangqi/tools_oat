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

import ohos.oat.analysis.headermatcher.OatLicense;

import org.apache.rat.api.Document;
import org.apache.rat.document.impl.guesser.BinaryGuesser;
import org.apache.rat.document.impl.guesser.GuessUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        "ods", "odt", "har", "sar", "wsr"
    };

    /**
     * API for identify files which donot need copyright headers
     *
     * @param document file to identify
     * @return files which donot need copyright headers
     */
    public static boolean isNote(final Document document) {
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

    public static boolean isArchiveFile(final Document document) {
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

    public static boolean isBinaryFile(final Document document) {
        final String fileName = GuessUtils.normalise(document.getName());
        final boolean isNotBinary = BinaryGuesser.isNonBinary(fileName);
        if (isNotBinary) {
            return false;
        }
        return BinaryGuesser.isBinary(document);
    }

    public static void saveJson2File(final String jsonString, final String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException e) {
                OatLogUtil.traceException(e);
            }
        }
        try (final BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));) {
            writer.write("");
            writer.write(jsonString);
        } catch (final IOException e) {
            OatLogUtil.traceException(e);
        }
    }

    public static <T> List<T> readJsonFromFile(final String filePath, final Class<OatLicense> t) {
        final File file = new File(filePath);
        // if (!file.exists()) {
        //     return null;
        // }
        final URL url = OatFileUtils.class.getResource(filePath);
        if (null == url) {
            return null;
        }

        String readJson = "";
        try (final InputStream fileInputStream = OatFileUtils.class.getResourceAsStream(filePath);
            final InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            final BufferedReader reader = new BufferedReader(inputStreamReader);) {
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                readJson += tempString;
            }
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

}
