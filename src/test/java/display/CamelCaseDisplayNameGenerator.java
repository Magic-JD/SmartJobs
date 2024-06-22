package display;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CamelCaseDisplayNameGenerator implements DisplayNameGenerator {
    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        String[] name = testClass.getName().split("\\.");
        return name[name.length - 1].replace("Test", "");
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        String[] name = nestedClass.getName().split("\\.");
        return name[name.length - 1].replace("Test", "");

    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return Arrays.stream(testMethod.getName().split("(?=\\p{Lu})")).filter(s -> !s.equals("test")).collect(Collectors.joining(" "));
    }
}
