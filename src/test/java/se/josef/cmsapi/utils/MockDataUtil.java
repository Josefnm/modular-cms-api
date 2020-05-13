package se.josef.cmsapi.utils;

import se.josef.cmsapi.model.document.Project;
import se.josef.cmsapi.model.document.Template;
import se.josef.cmsapi.model.document.TemplateField;
import se.josef.cmsapi.model.document.User;
import se.josef.cmsapi.model.document.contentField.ImageField;
import se.josef.cmsapi.model.document.contentField.StringField;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAscii;


/**
 * Utility class for creating data for tests
 */
public class MockDataUtil {

    public static long randomLong(int count) {
        var value = count > 18 ? Long.MAX_VALUE : (long) Math.pow(10, count);
        return ThreadLocalRandom.current().nextLong(value);
    }

    public static String randomEmail() {
        return String.format("%s@%s.%s",
                randomAlphabetic(10),
                randomAlphabetic(7),
                randomAlphabetic(3))
                .toLowerCase();
    }

    public static Date getPastDate(int minusDays) {
        return java.sql.Timestamp.valueOf(LocalDateTime.now().minusDays(minusDays));
    }

    public static Date getFutureDate(int plusDays) {
        return java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(plusDays));
    }

    public static Template getNewTemplate(String templateId, String projectId, String name) {
        List<TemplateField> tfs = new ArrayList<>();
        tfs.add(new TemplateField(randomAlphabetic(15), StringField.class.getSimpleName()));
        tfs.add(new TemplateField(randomAlphabetic(15), ImageField.class.getSimpleName()));

        return Template.builder()
                .name(name)
                .description(randomAscii(100))
                .projectId(projectId)
                .created(new Date())
                .updated(new Date())
                .templateFields(tfs)
                .id(templateId)
                .build();

    }

    public static Project getNewProject(String projectId, String... memberIds) {
        return Project.builder()
                .id(projectId)
                .ownerId(memberIds[0])
                .memberIds(Arrays.asList(memberIds))
                .name(randomAlphabetic(15))
                .description(randomAscii(100))
                .build();
    }

    public static User getNewUser(String userId, String name, String email) {
        return User.builder()
                .id(userId)
                .userName(name)
                .email(email)
                .build();
    }
}