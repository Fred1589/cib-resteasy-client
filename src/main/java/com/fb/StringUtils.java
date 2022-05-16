package com.fb;

import org.jboss.logging.Logger;

/**
 * Utils class for String operations
 */
public final class StringUtils {

    private static final Logger LOG = Logger.getLogger(StringUtils.class);

    private StringUtils() {
        // private constructor to hide the public one
    }

    /**
     * Checks whether given {@link String} is null or {@link String#isBlank()}.
     *
     * @param value value to check
     * @return true if null or blank, otherwise false
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Checks whether given {@link String} is null or {@link String#isBlank()}. If null or blank, default value is returned.
     *
     * @param value        value to check
     * @param defaultValue default value
     * @return value if not blank, otherwise default value
     */
    public static String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    /**
     * Parse given value to {@link Integer}
     *
     * @param value value to parse
     * @return Integer, otherwise if not parseable null
     */
    public static Integer parseToInt(String value) {
        if (!isBlank(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOG.trace(String.format("%s is not a numeric value", value), e);
            }
        }
        return null;
    }

    /**
     * Pads left side of input with given character until String has desired length
     *
     * @param length      length the String should have
     * @param original    original String
     * @param paddingChar Character for padding
     * @return padded String or original if original is already equal or longer than given length
     */
    public static String leftPadString(final int length, final String original, final Character paddingChar) {
        return String.format("%1$" + length + "s", original).replace(' ', paddingChar);
    }
}
