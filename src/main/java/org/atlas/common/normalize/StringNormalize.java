package org.atlas.common.normalize;

public class StringNormalize {


    public static String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }


    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    public static String normalizeCpf(String cpf) {
        return cpf.trim().replaceAll("[^0-9]", "");
    }

}