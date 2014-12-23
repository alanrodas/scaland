/*
 * Copyright 2014 Alan Rodas Bonjour
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package com.alanrodas.scaland.logging;

import org.slf4j.Logger;
// import org.slf4j.Marker;

public class LoggerSupport {

    // Error

    public static void error(Logger logger, String message, Object arg) {
        logger.error(message, arg);
    }

    public static void error(Logger logger, org.slf4j.Marker marker, String message, Object arg) {
        logger.error(marker, message, arg);
    }

    public static void error(Logger logger, String message, Object arg1, Object arg2) {
        logger.error(message, arg1, arg2);
    }

    public static void error(Logger logger, org.slf4j.Marker marker, String message, Object arg1, Object arg2) {
        logger.error(marker, message, arg1, arg2);
    }

    // Warn

    public static void warn(Logger logger, String message, Object arg) {
        logger.warn(message, arg);
    }

    public static void warn(Logger logger, org.slf4j.Marker marker, String message, Object arg) {
        logger.warn(marker, message, arg);
    }

    public static void warn(Logger logger, String message, Object arg1, Object arg2) {
        logger.warn(message, arg1, arg2);
    }

    public static void warn(Logger logger, org.slf4j.Marker marker, String message, Object arg1, Object arg2) {
        logger.warn(marker, message, arg1, arg2);
    }

    // Info

    public static void info(Logger logger, String message, Object arg) {
        logger.info(message, arg);
    }

    public static void info(Logger logger, org.slf4j.Marker marker, String message, Object arg) {
        logger.info(marker, message, arg);
    }

    public static void info(Logger logger, String message, Object arg1, Object arg2) {
        logger.info(message, arg1, arg2);
    }

    public static void info(Logger logger, org.slf4j.Marker marker, String message, Object arg1, Object arg2) {
        logger.info(marker, message, arg1, arg2);
    }

    // Debug

    public static void debug(Logger logger, String message, Object arg) {
        logger.debug(message, arg);
    }

    public static void debug(Logger logger, org.slf4j.Marker marker, String message, Object arg) {
        logger.debug(marker, message, arg);
    }

    public static void debug(Logger logger, String message, Object arg1, Object arg2) {
        logger.debug(message, arg1, arg2);
    }

    public static void debug(Logger logger, org.slf4j.Marker marker, String message, Object arg1, Object arg2) {
        logger.debug(marker, message, arg1, arg2);
    }

    // Trace

    public static void trace(Logger logger, String message, Object arg) {
        logger.trace(message, arg);
    }

    public static void trace(Logger logger, org.slf4j.Marker marker, String message, Object arg) {
        logger.trace(marker, message, arg);
    }

    public static void trace(Logger logger, String message, Object arg1, Object arg2) {
        logger.trace(message, arg1, arg2);
    }

    public static void trace(Logger logger, org.slf4j.Marker marker, String message, Object arg1, Object arg2) {
        logger.trace(marker, message, arg1, arg2);
    }
}