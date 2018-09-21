package com.dachen.health.file.utils;

import java.text.DecimalFormat;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-06
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class FormatSizeUtil {

    private static double ONE_BYTES = 1;
    private static double ONE_KB = ONE_BYTES * 1024;
    private static double ONE_MB = ONE_KB * 1024;
    private static double ONE_GB = ONE_MB * 1024;
    private static double ONE_TB = ONE_GB * 1024;
    private static double ONE_PB = ONE_TB * 1024;

    public static String setSizeStr(Long size) {
        String sizeStr;
        // 格式化小数
        DecimalFormat df = new DecimalFormat("0.0");
        if (size < ONE_BYTES) {
            sizeStr = "0bytes";
        } else if (ONE_BYTES <= size && size < ONE_KB) {
            sizeStr = df.format(size / ONE_BYTES) + "bytes";
        } else if (ONE_KB <= size && size < ONE_MB) {
            sizeStr = df.format(size / ONE_KB) + "KB";
        } else if (ONE_MB <= size && size < ONE_GB) {
            sizeStr = df.format(size / ONE_MB) + "MB";
        } else if (ONE_GB <= size && size < ONE_TB) {
            sizeStr = df.format(size / ONE_GB) + "GB";
        } else if (ONE_TB <= size && size < ONE_PB) {
            sizeStr = df.format(size / ONE_TB) + "TB";
        } else {
            sizeStr = df.format(size / ONE_PB) + "PB";
        }
        return sizeStr;
    }
}
