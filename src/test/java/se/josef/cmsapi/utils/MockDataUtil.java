package se.josef.cmsapi.utils;

import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.document.TemplateField;
import se.josef.cmsapi.model.document.contentField.ImageField;
import se.josef.cmsapi.model.document.contentField.StringField;

import java.util.*;
import java.util.stream.IntStream;


/**
 * Utility class for creating random data for tests
 */
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

    private static char getRandomChar(String src) {
        return src.charAt((int) (src.length() * Math.random()));
    }

    public static String getRandomAlphaNumeric(int len) {
        var src = uppercase + number + lowercase;
        return getRandom(src, len);
    }

    public static String getRandomUppercase(int len) {
        var src = uppercase;
        return getRandom(src, len);
    }

    public static String getRandomLowercase(int len) {
        var src = lowercase;
        return getRandom(src, len);
    }

    public static String getRandomUppercaseNumeric(int len) {
        var src = uppercase + number;
        return getRandom(src, len);
    }

    public static String getRandomLowercaseNumeric(int len) {
        var src = lowercase + number;
        return getRandom(src, len);
    }

    public static String getRandomAlphabets(int len) {
        var src = uppercase + lowercase;
        return getRandom(src, len);
    }

    public static String getRandomAlphaNumericAndSymbols(int len) {
        var src = uppercase + number + lowercase + symbol;
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


    public static Template getNewTemplate(String templateId, String projectId,String name) {
        List<TemplateField> tfs = new ArrayList<>();
        tfs.add(new TemplateField(getRandomAlphabets(15), StringField.class.getSimpleName()));
        tfs.add(new TemplateField(getRandomAlphabets(15), ImageField.class.getSimpleName()));

        return Template.builder()
                .name(name)
                .description(getRandomAlphaNumericAndSymbols(100))
                .projectId(projectId)
                .created(new Date())
                .updated(new Date())
                .isPublic(true)
                .templateFields(tfs)
                .id(templateId)
                .build();

    }

    public static Project getNewProject(String projectId, String ownerId) {
        return Project.builder()
                .id(projectId)
                .memberIds(Collections.singletonList(ownerId))
                .name(getRandomAlphabets(15))
                .description(getRandomAlphaNumericAndSymbols(100))
                .build();
    }
}