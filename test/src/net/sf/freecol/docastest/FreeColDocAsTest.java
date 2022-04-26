package net.sf.freecol.docastest;

import net.sf.freecol.common.io.FreeColTcFile;
import org.junit.Test;
import org.sfvl.docformatter.asciidoc.AsciidocFormatter;
import org.sfvl.doctesting.utils.Config;
import org.sfvl.doctesting.utils.DocPath;
import org.sfvl.doctesting.writer.ClassDocumentation;
import org.sfvl.doctesting.writer.DocWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FreeColDocAsTest extends DocAsTest {

    private static class FreeColDocWriter extends DocWriter<AsciidocFormatter> {

        FreeColTcFile tcData = FreeColTcFile.getFreeColTcFile("classic");
        private final ClassDocumentation classDocumentation = new ClassDocumentation(
                getFormatter(),
                o -> Paths.get(o.filename()),
                m -> m.isAnnotationPresent(Test.class),
                m -> false // Do not include inner class
        );

        public FreeColDocWriter() {
            super(new AsciidocFormatter());
        }


        public String defineDocPath(Path relativePathToRoot) {
            return super.defineDocPath(relativePathToRoot)
                    + "\n"
                    + getResourcesPath();
        }

        public String formatOutput(Class<?> clazz) {
            return String.join("\n",
                    defineDocPath(clazz),
                    getResourcesPath(),
                    "",
                    classDocumentation.getClassDocumentation(clazz)
            );
        }

        private String getResourcesPath() {
            final DocPath docPath = new DocPath(this.getClass());
            FreeColTcFile tcData = FreeColTcFile.getFreeColTcFile("classic");
            final Path relativizedPath = Config.DOC_PATH.relativize(Paths.get("."))
                    .resolve(tcData.getPath());
            return "ifndef::RESOURCES_PATH[:RESOURCES_PATH: {" + Config.DOC_PATH_TAG + "}/" + relativizedPath + "]";
        }
    }

    public FreeColDocAsTest() {
        super(new FreeColDocWriter());
    }

}
