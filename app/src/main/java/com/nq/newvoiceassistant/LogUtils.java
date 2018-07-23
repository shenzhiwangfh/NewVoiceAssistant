package com.nq.newvoiceassistant;

/**
 * Created by shenzhiwang on 2/13/18.
 */

import android.os.Build;
import android.util.Log;

public class LogUtils {

    /**
     * Default logger used for generic logging, i.eTAG. when a specific log tag isn't specified.
     */
    private final static Logger DEFAULT_LOGGER = new Logger("NewVoiceAssist");

    public static void v(String tag, String message, Object... args) {
        DEFAULT_LOGGER.v(tag, message, args);
    }

    public static void d(String tag, String message, Object... args) {
        DEFAULT_LOGGER.d(tag, message, args);
    }

    public static void i(String tag, String message, Object... args) {
        DEFAULT_LOGGER.i(tag, message, args);
    }

    public static void w(String tag, String message, Object... args) {
        DEFAULT_LOGGER.w(tag, message, args);
    }

    public static void e(String tag, String message, Object... args) {
        DEFAULT_LOGGER.e(tag, message, args);
    }

    public static void e(String tag, String message, Throwable e) {
        DEFAULT_LOGGER.e(tag, message, e);
    }

    public static void e(String tag, Throwable e) {
        DEFAULT_LOGGER.e(tag, e);
    }

    public static void wtf(String tag, String message, Object... args) {
        DEFAULT_LOGGER.wtf(tag, message, args);
    }

    public static void wtf(String tag, Throwable e) {
        DEFAULT_LOGGER.wtf(tag, e);
    }

    public final static class Logger {

        /**
         * Log everything for debug builds or if running on a dev device.
         */
        public final static boolean DEBUG = "eng".equals(Build.TYPE) || "userdebug".equals(Build.TYPE);

        public final String logTag;

        public Logger(String logTag) {
            this.logTag = logTag;
        }

        public boolean isVerboseLoggable() {
            return DEBUG || Log.isLoggable(logTag, Log.VERBOSE);
        }

        public boolean isDebugLoggable() {
            return DEBUG || Log.isLoggable(logTag, Log.DEBUG);
        }

        public boolean isInfoLoggable() {
            return DEBUG || Log.isLoggable(logTag, Log.INFO);
        }

        public boolean isWarnLoggable() {
            return DEBUG || Log.isLoggable(logTag, Log.WARN);
        }

        public boolean isErrorLoggable() {
            return DEBUG || Log.isLoggable(logTag, Log.ERROR);
        }

        public boolean isWtfLoggable() {
            return DEBUG || Log.isLoggable(logTag, Log.ASSERT);
        }

        public void v(String tag, String message, Object... args) {
            if (isVerboseLoggable()) {
                Log.v(logTag, tag + "," + (args == null || args.length == 0
                        ? message : String.format(message, args)));
            }
        }

        public void d(String tag, String message, Object... args) {
            if (isDebugLoggable()) {
                Log.d(logTag, tag + "," + (args == null || args.length == 0 ? message
                        : String.format(message, args)));
            }
        }

        public void i(String tag, String message, Object... args) {
            if (isInfoLoggable()) {
                Log.i(logTag, args == null || args.length == 0 ? message
                        : String.format(message, args));
            }
        }

        public void w(String tag, String message, Object... args) {
            if (isWarnLoggable()) {
                Log.w(logTag, tag + "," + (args == null || args.length == 0 ? message
                        : String.format(message, args)));
            }
        }

        public void e(String tag, String message, Object... args) {
            if (isErrorLoggable()) {
                Log.e(logTag, tag + "," + (args == null || args.length == 0 ? message
                        : String.format(message, args)));
            }
        }

        public void e(String tag, String message, Throwable e) {
            if (isErrorLoggable()) {
                Log.e(logTag, tag + "," + message, e);
            }
        }

        public void e(String tag, Throwable e) {
            if (isErrorLoggable()) {
                Log.e(logTag, tag, e);
            }
        }

        public void wtf(String tag, String message, Object... args) {
            if (isWtfLoggable()) {
                Log.wtf(logTag, tag + "," + (args == null || args.length == 0 ? message
                        : String.format(message, args)));
            }
        }

        public void wtf(String tag, Throwable e) {
            if (isWtfLoggable()) {
                Log.wtf(logTag, tag, e);
            }
        }
    }
}