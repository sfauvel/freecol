package net.sf.freecol;

import com.github.javaparser.utils.SourceRoot;
import javassist.bytecode.ClassFile;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocTestRunner {

    public static List<? extends Class<?>> docClasses() {
        SourceRoot sourceRoot = new SourceRoot(Paths.get("test"));

        final Scanner scanner = new TypeElementsScanner() {
            @Override
            public boolean acceptsInput(String file) {
                return file.endsWith("DocTest.class");
            }

            @Override
            public List<Map.Entry<String, String>> scan(ClassFile classFile) {
                String className = classFile.getName();
                return Arrays.asList(this.entry(className, className));
            }
        };
        List<String> startingPackage = Arrays.asList("net", "sf", "freecol");
        final String package_string = String.join(".", startingPackage);
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(package_string)
                        .setScanners(scanner));

        return reflections.getAll(scanner).stream()
                .map(file -> {
                    System.out.println(file);
                    try {
                        return Class.forName(file);
                    } catch (ClassNotFoundException err) {
                        System.err.println(err.getMessage());
                    }
                    return null;

                })
                .collect(Collectors.toList());



    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {

        final List<? extends Class<?>> classes = docClasses();
        Result result = JUnitCore.runClasses(classes.toArray(new Class[0]));

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        String color = (result.wasSuccessful()) ?ANSI_GREEN: ANSI_RED;
        if (!result.wasSuccessful()) {
            System.out.println("There was " + result.getFailures() + " failures");
        }
        System.out.println("=================================================");
        System.out.println("Time " + result.getRunTime() / 1000.0);
        System.out.println("TOTAL: " + result.getRunCount());
        System.out.println(color + "Tests run: " + (result.getRunCount() - result.getFailureCount() - result.getIgnoreCount())
                + ", Failures: " + result.getFailureCount()
                + ", Ignores: " + result.getIgnoreCount()
                + ANSI_RESET);
        System.exit(0);
    }
}