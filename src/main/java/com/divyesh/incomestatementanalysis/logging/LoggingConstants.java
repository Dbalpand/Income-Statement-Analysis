package com.divyesh.incomestatementanalysis.logging;

public final class LoggingConstants {

    private LoggingConstants() {
        // Prevent instantiation
    }

    public static final String POINTCUT_CONTROLLERS = "execution(* com.divyesh.incomestatementanalysis.controller..*.*(..))";
    public static final String POINTCUT_SERVICES = "execution(* com.divyesh.incomestatementanalysis.service..*.*(..))";
    public static final String POINTCUT_AI_AND_OCR = "execution(* com.divyesh.incomestatementanalysis.ocr..*.*(..)) || execution(* com.divyesh.incomestatementanalysis.ai..*.*(..))";

    public static final String LOG_ENTRY = "==> [ENTER] {}.{}() | Args: {}";
    public static final String LOG_EXIT = "<== [EXIT]  {}.{}() | Time Taken: {} ms";
    public static final String LOG_ERROR = "!!! [ERROR] {}.{}() | Exception: {} | Message: {}";
}