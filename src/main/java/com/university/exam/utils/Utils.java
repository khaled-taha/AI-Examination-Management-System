package com.university.exam.utils;

import java.util.List;
import java.util.Objects;

public class Utils {

    /**
     * Validates if a string is empty or null.
     *
     * @param str The string to validate.
     * @return true if the string is null or empty, false otherwise.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates if a list is empty or null.
     *
     * @param list The list to validate.
     * @return true if the list is null or empty, false otherwise.
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Validates if a list is not empty and not null.
     *
     * @param list The list to validate.
     * @return true if the list is not null and not empty, false otherwise.
     */
    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    /**
     * Validates if a string is not empty and not null.
     *
     * @param str The string to validate.
     * @return true if the string is not null and not empty, false otherwise.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
