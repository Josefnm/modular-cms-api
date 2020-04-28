package se.josef.cmsapi.utils;

import java.util.Random;
import java.util.stream.IntStream;

public class MockDataUtil {
    private static String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String lowercase = "abcdefghijklmnopqrstuvxyz";
    private static String number = "0123456789";
    private static String symbol = "~!@#$%^&*()_+~=-{}\\[]|;':\",./<>?";

    public static String getRandom(String src, int length) {
        return IntStream
                .range(0, length)
                .collect(StringBuilder::new,
                        (sb, i) -> sb.append(getRandomChar(src)),
                        StringBuilder::append)
                .toString();

    }

    private static char getRandomChar(String src){
        return src.charAt((int) (src.length() * Math.random()));
    }

    public static String getRandomAlphaNumeric(int len) {
        String src = uppercase + number + lowercase;
        return getRandom(src, len);
    }

    public static String getRandomUppercase(int len) {
        String src = uppercase;
        return getRandom(src, len);
    }

    public static String getRandomLowercase(int len) {
        String src = lowercase;
        return getRandom(src, len);
    }

    public static String getRandomUppercaseNumeric(int len) {
        String src = uppercase + number;
        return getRandom(src, len);
    }

    public static String getRandomLowercaseNumeric(int len) {
        String src = lowercase + number;
        return getRandom(src, len);
    }

    public static String getRandomAlphabets(int len) {
        String src = uppercase + lowercase;
        return getRandom(src, len);
    }

    public static String getRandomAlphaNumericAndSymbols(int len) {
        String src = uppercase + number + lowercase + symbol;
        return getRandom(src, len);
    }

    public static String getRandomNumbers(int len) {
        return getRandom(number, len);
    }

    public static long getRandomLong(int len) {
        if (len > 18)
            return 0;
        return Long.parseLong(getRandomNumbers(len));
    }

    public static double getRandomDouble() {
        return new Random().nextDouble();
    }
}