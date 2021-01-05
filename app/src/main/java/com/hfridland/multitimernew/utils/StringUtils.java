package com.hfridland.multitimernew.utils;

public class StringUtils {
    public static String duration2String(long duration) {
        long h = duration / 3600;
        duration %= 3600;
        long m = duration / 60;
        long s = duration % 60;
        if (h > 0) {
            return String.format("%02d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }
}
