package org.atlas.common.normalize;

public class StringNormalize {


    public static String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }


    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

}