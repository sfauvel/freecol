package net.sf.freecol.architecture;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import javassist.bytecode.ClassFile;
import net.sf.freecol.docastest.FreeColDocAsTest;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.TypeElementsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.sfvl.codeextraction.CodePath;
import org.sfvl.doctesting.utils.Config;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * The goal of this page, is to document some information about project architecture.
 * @see https://github.com/tweerlei/java-dependency-graph
 */
public class ArchitectureDocTest extends FreeColDocAsTest {

    @Test
    public void server() throws ClassNotFoundException {
        package_dependencies(Arrays.asList("net", "sf", "freecol", "server"));
    }

    @Test
    public void client() throws ClassNotFoundException {
        package_dependencies(Arrays.asList("net", "sf", "freecol", "client"));
    }

    @Test
    public void common() throws ClassNotFoundException {
        package_dependencies(Arrays.asList("net", "sf", "freecol", "common"));
    }


    public void package_dependencies(List<String> startingPackage) throws ClassNotFoundException {
        SourceRoot sourceRoot = new SourceRoot(Paths.get("src"));

        final Scanner scanner = new TypeElementsScanner() {
            @Override
            public boolean acceptsInput(String file) {
                return file.endsWith(".class");
            }

            @Override
            public List<Map.Entry<String, String>> scan(ClassFile classFile) {
                String className = classFile.getName();
                return Arrays.asList(this.entry(className, className));
            }
        };
        final String package_string = String.join(".", startingPackage);
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(package_string)
                        .setScanners(scanner));

        final List<Class> classesToAnalysis =
                reflections.getAll(scanner).stream()
                        .map(this::toClass)
                        .filter(c -> c.getPackageName().startsWith(package_string))
//                        .filter(c -> c.getPackage() != DemoDocumentation.class.getPackage())
//                        .filter(c -> c.getPackage() != ApprovalsBase.class.getPackage())
                        .filter(this::isTopLevelClass)
                        .filter(c -> toSourceFile(c).isFile())
                        .collect(Collectors.toList());

        final Map<String, List<String>> importsByClasses = classesToAnalysis.stream()
                .collect(Collectors.toMap(Class::getName,
                        clazz -> extractImports(sourceRoot, clazz, imp -> imp.startsWith(package_string))));

        GraphvizGenerator graphvizGenerator = new GraphvizGenerator()
                .rankDir(GraphvizGenerator.RankDir.TopDown);

        importsByClasses.entrySet().stream()
                .flatMap(classListEntry -> {
                    Stream<String> importsInClass = classListEntry.getValue().stream()
                            .filter(Objects::nonNull);

                    return importsInClass.map(e1 ->
                            new GraphvizGenerator.Link(
                                    toKeyGraphNode(classListEntry.getKey()),
                                    toKeyGraphNode(e1)
                            )
                    );
                }).forEach(graphvizGenerator::addLink);

        final Set<Package> packages = classesToAnalysis.stream().map(Class::getPackage).collect(Collectors.toSet());
        final Map<String, List<Package>> grouped_packages = packages.stream().collect(Collectors.groupingBy(p -> {
//            System.out.println(p.getName());
//            System.out.println(startingPackage);
//            System.out.println(startingPackage.size());
            final String[] split = p.getName().split("\\.");
            if (split.length > startingPackage.size()) {
                return split[startingPackage.size()];
            } else {
                return "ALL";
            }
        }));

        String clusters = "";
        for (Map.Entry<String, List<Package>> packageEntry : grouped_packages.entrySet()) {
            clusters += String.join("\n",
                    "subgraph cluster_" + packageEntry.getKey() + " {",
                    "    bgcolor=\"#05fdCC\";",
                    packageEntry.getValue().stream()
                            .map(Package::getName)
                            .map(name -> "    \"" + name + "\"")
                            .collect(Collectors.joining("\n")),
                    "}",
                    "");
        }

        final String graph = String.join("\n",
                "The graph below shows dependencies between packages in the project.",

                graphvizGenerator.generate("node [margin=0.1 fontcolor=black fontsize=16 width=0.5 shape=rect style=filled fillcolor=\"#0fd289\"]\n" +
                                clusters,
                        "")
        );

        write(graph);

    }

    private Class<?> toClass(String className) {
        try {
            return Class.forName(className, false, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            System.err.println("ProjectOrganization.toClass ClassNotFoundException:" + className);
            return null;
        }
    }

    private boolean isSourceClass(Class<?> aClass) {
        return toSourceFile(aClass).isFile();
    }

    private File toSourceFile(Class<?> aClass) {
        return Config.SOURCE_PATH.resolve(CodePath.toPath(aClass)).toFile();
    }

    private GenericVisitorAdapter<Object, List<String>> importVisitor = new GenericVisitorAdapter<Object, List<String>>() {
        @Override
        public Object visit(ImportDeclaration n, List<String> imports) {
            imports.add(n.getName().asString());
            return super.visit(n, imports);
        }
    };

    private String keepImportsOfClass(Set<String> importsToShow, String classImports) {
        return importsToShow.stream()
                .filter(i -> classImports.startsWith(i))
                .findFirst()
                .orElse(null);
    }

    private boolean isTopLevelClass(Class<?> c) {
        return !c.isAnonymousClass() && !c.isMemberClass() && !c.isLocalClass();
    }

    private List<String> extractImports(SourceRoot sourceRoot, Class clazz, Predicate<String> importFilter) {
        return extractImports(sourceRoot, clazz).stream().filter(importFilter).collect(Collectors.toList());
    }

    private List<String> extractImports(SourceRoot sourceRoot, Class clazz) {
        final Package aPackage = clazz.getPackage();
        final File file = toSourceFile(clazz);
        CompilationUnit cu = sourceRoot.parse(aPackage.getName(), file.getName());

        final List<String> imports = new ArrayList<>();
        cu.accept(importVisitor, imports);
        return imports;
    }

    private String toKeyGraphNode(String e) {
        return "\"" + e.replaceAll("\\.[A-Z].*", "") + "\"";
    }

}
