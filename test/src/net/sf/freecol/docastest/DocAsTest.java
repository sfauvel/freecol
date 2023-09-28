package net.sf.freecol.docastest;

import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.approvaltests.namer.ApprovalNamer;
import org.approvaltests.writers.ApprovalTextWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.sfvl.codeextraction.CodeExtractor;
import org.sfvl.docformatter.asciidoc.AsciidocFormatter;
import org.sfvl.doctesting.utils.Config;
import org.sfvl.doctesting.utils.DocPath;
import org.sfvl.doctesting.writer.ClassDocumentation;
import org.sfvl.doctesting.writer.DocWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;

/**
 * Base class for test.
 */
public class DocAsTest {

    static {
        CodeExtractor.init(Config.TEST_PATH, Config.SOURCE_PATH);
    }

    @Rule
    public TestName testName = new TestName();

    protected String getFullTestName() {
        return getClass().getName() + "." + testName.getMethodName();
    }

    private static Class<?> testClass;

    static DocWriter<AsciidocFormatter> docWriter;

    public DocAsTest() {
        this(new DocWriter<>(new AsciidocFormatter()) {
            private final ClassDocumentation classDocumentation = new ClassDocumentation(
                    getFormatter(),
                    o -> Paths.get(o.filename()),
                    m -> m.isAnnotationPresent(Test.class),
                    m -> false // Do not include inner class
            );

            public String formatOutput(Class<?> clazz) {
                return String.join("\n",
                        defineDocPath(clazz),
                        "",
                        classDocumentation.getClassDocumentation(clazz)
                );
            }
        });
    }

    public DocAsTest(DocWriter<AsciidocFormatter> docWriter) {
        DocAsTest.docWriter = docWriter;
    }

    protected void write(String... lines) {
        docWriter.write(lines);
    }

    protected void writeln(String... lines) {
        docWriter.write(getFormatter().paragraph(lines));
    }

    protected AsciidocFormatter getFormatter() {
        return docWriter.getFormatter();
    }

    protected static void approved(DocPath docPath, String content) {
        ApprovalNamer approvalNamer = new ApprovalNamer() {
            @Override
            public String getApprovalName() {
                return "_" + docPath.name();
            }

            @Override
            public String getSourceFilePath() {
                return docPath.approved().folder().toString() + File.separator;
            }

            @Override
            public File getApprovedFile(String extensionWithDot) {
                return new File(this.getSourceFilePath() + "/" + this.getApprovalName() + ".approved" + extensionWithDot);
            }

            @Override
            public File getReceivedFile(String extensionWithDot) {
                return new File(this.getSourceFilePath() + "/" + this.getApprovalName() + ".received" + extensionWithDot);
            }

        };

        final Options options = new Options()
                .forFile().withExtension(".adoc");

        Approvals.verify(
                new ApprovalTextWriter(content, options),
                approvalNamer);
    }

    @After
    public void approvedAfterTest() throws NoSuchMethodException {

        final String methodName = testName.getMethodName();
        final Method testMethod = this.getClass().getDeclaredMethod(methodName);

        testClass = testMethod.getDeclaringClass();
        final String content = docWriter.formatOutput(testMethod);
        docWriter.reset();
        approved(new DocPath(testMethod), content);
    }

    @AfterClass
    public static void writeTestDoc() throws IOException {
        final String content = docWriter.formatOutput(testClass);
        final DocPath path = new DocPath(testClass);

        approved(path, content);
    }

}
