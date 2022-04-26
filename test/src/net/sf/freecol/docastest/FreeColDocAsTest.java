package net.sf.freecol.docastest;

import net.sf.freecol.common.io.FreeColTcFile;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Test;
import org.sfvl.docformatter.asciidoc.AsciidocFormatter;
import org.sfvl.doctesting.utils.Config;
import org.sfvl.doctesting.utils.DocPath;
import org.sfvl.doctesting.writer.ClassDocumentation;
import org.sfvl.doctesting.writer.DocWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FreeColDocAsTest extends DocAsTest {

    FreeColTestCase testCase = new FreeColTestCase();

    public static Specification spec() {
        return FreeColTestCase.spec();
    }

    public Game getGame() {
        return FreeColTestCase.getGame();
    }

    public Map getTestMap(boolean x) {
        return FreeColTestCase.getTestMap(x);
    }

    public Colony getStandardColony() {
        return testCase.getStandardColony();
    }

    public Colony getStandardColony(int numberOfSetllers) {
        return testCase.getStandardColony(numberOfSetllers);
    }

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
