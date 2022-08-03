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
 * 2021.2 - Add OhosLogUtil and replace System.out
 * Modified by jalenchen
 */

package ohos.oat.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for log
 *
 * @author chenyaxun
 * @since 1.0
 */
public final class OatLogUtil {
    private static final Logger LOGGER = LogManager.getLogger("Log");

    private static final Logger LICENSE_LOGGER = LogManager.getLogger("Log_License");

    private static final Logger LICENSE_FILE_LOGGER = LogManager.getLogger("Log_License_File");

    private static final Logger OAT_CONFIG_LOGGER = LogManager.getLogger("Log_OAT_Config");

    private static boolean isDebugMode = false;

    /**
     * Private constructure to prevent new instance
     */
    private OatLogUtil() {
    }

    /**
     * set run mode
     *
     * @param debugMode run mode
     */
    public static void setDebugMode(final boolean debugMode) {
        isDebugMode = debugMode;
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void println(final String className, final String logtext) {
        System.out.println(className + logtext);
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void info(final String className, final String logtext) {
        if (isDebugMode) {
            LOGGER.info(className + "\t" + logtext);
        }
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void warn(final String className, final String logtext) {
        if (isDebugMode) {
            LOGGER.warn(className + "\t" + logtext);
        }
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void error(final String className, final String logtext) {
        if (isDebugMode) {
            LOGGER.error(className + "\t" + logtext);
        }
    }

    /**
     * write log message
     *
     * @param exception log text
     */
    public static void traceException(final Exception exception) {
        exception.printStackTrace();
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void logLicense(final String className, final String logtext) {
        LICENSE_LOGGER.warn(className + "\t" + logtext);
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void logLicenseFile(final String className, final String logtext) {
        LICENSE_FILE_LOGGER.warn(className + "\t" + logtext);
    }

    /**
     * write log message
     *
     * @param className class name to write log
     * @param logtext log text
     */
    public static void logOatConfig(final String className, final String logtext) {
        OAT_CONFIG_LOGGER.warn(className + "\t" + logtext);
    }

}
