package com.jansmerecki.util;

public class MacUtils {

    private MacUtils() {}

    public static String normalize(String input) {
        if (input == null) {
            return null;
        }
        String hex = input.replaceAll("[^0-9A-Fa-f]", "");
        if (hex.length() != 12) {
            throw new IllegalArgumentException("Invalid MAC address format: " + input);
        }
        String upper = hex.toUpperCase();
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < 12; i += 2) {
            if (i > 0) sb.append(':');
            sb.append(upper, i, i + 2);
        }
        return sb.toString();
    }
}
