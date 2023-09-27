package net.sf.freecol.docastest;

import net.sf.freecol.common.io.FreeColTcFile;
import net.sf.freecol.common.model.*;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Before;
import org.junit.Test;
import org.sfvl.docformatter.asciidoc.AsciidocFormatter;
import org.sfvl.doctesting.utils.Config;
import org.sfvl.doctesting.utils.DocPath;
import org.sfvl.doctesting.writer.ClassDocumentation;
import org.sfvl.doctesting.writer.DocWriter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FreeColDocAsTest extends DocAsTest {

    public static final Path IMAGE_PATH = Config.DOC_PATH.resolve("images");
    FreeColTestCase testCase = new FreeColTestCase();

    @Before
    public void initGame() {
        getGame();
    }

    public static Specification spec() {
        return FreeColTestCase.spec();
    }

    public Game getGame() {
//        return FreeColTestCase.getGame();
        return FreeColTestCase.getStandardGame();
    }

    public Game getStandardGame() {
        return testCase.getStandardGame();
    }
    public Colony getStandardColony() {
        return testCase.getStandardColony();
    }

    public Colony getStandardColony(int numberOfSetllers) {
        return testCase.getStandardColony(numberOfSetllers);
    }

    /**
     * Creates a standardized map on which all fields have the plains type.
     *
     * Uses the getGame() method to access the currently running game.
     *
     * Does not call Game.setMap(Map) with the returned map. The map
     * is unexplored.
     *
     * @return The map created as described above.
     */
    public Map getTestMap() {
        FreeColTestCase.MapBuilder builder = new FreeColTestCase.MapBuilder(getGame());
        return builder.build();
    }

    public Map getTestMap(boolean explored) {
        return testCase.getTestMap(explored);
    }
    public Map getTestMap(TileType plains) {
        return testCase.getTestMap(plains);
    }

    private static class FreeColDocWriter extends DocWriter<AsciidocFormatter> {

        FreeColTcFile tcData = FreeColTcFile.getFreeColTcFile("rules/classic");
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
            FreeColTcFile tcData = FreeColTcFile.getFreeColTcFile("default");
            final Path relativizedPath = Config.DOC_PATH.relativize(Paths.get("."))
                    .resolve(tcData.getPath());
            return "ifndef::RESOURCES_PATH[:RESOURCES_PATH: {" + Config.DOC_PATH_TAG + "}/" + relativizedPath + "]";
        }
    }

    public FreeColDocAsTest() {
        super(new FreeColDocWriter());
    }

}
