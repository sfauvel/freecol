package net.sf.freecol.docastest;

import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.common.io.FreeColTcFile;
import net.sf.freecol.common.model.*;
import net.sf.freecol.common.resources.ImageResource;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Before;
import org.junit.Test;
import org.sfvl.docformatter.asciidoc.AsciidocFormatter;
import org.sfvl.docformatter.Formatter;
import org.sfvl.doctesting.utils.Config;
import org.sfvl.doctesting.utils.DocPath;
import org.sfvl.doctesting.writer.ClassDocumentation;
import org.sfvl.doctesting.writer.DocWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class FreeColDocAsTest extends DocAsTest {

    public static final Path IMAGE_PATH = Config.DOC_PATH.resolve("images");
    FreeColTestCase testCase = new FreeColTestCase();
    private FreeColTcFile tcData = null;

    public FreeColDocAsTest() {
        super(new FreeColDocWriter(new FreeColFormatter()));
        tcData = FreeColTcFile.getFreeColTcFile("default");
        ResourceManager.setTcData(tcData);
        ResourceManager.prepare();
    }
    @Before
    public void initGame() {
        getGame();
    }

    public static Specification spec() {
        return FreeColTestCase.spec();
    }

    Game game = null;
    public Game getGame() {
//        return FreeColTestCase.getGame();
        if (game == null) {
            game = FreeColTestCase.getStandardGame();
        }
        return game;
    }

    public Game getStandardGame() {
        return getGame();
//        return testCase.getStandardGame();
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
        return getTestMap(getGame());
    }

    public static Map getTestMap(Game game) {
        FreeColTestCase.MapBuilder builder = new FreeColTestCase.MapBuilder(game);
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

        public FreeColDocWriter(AsciidocFormatter formatter) {
            super(formatter);
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

    private Properties prop = null;
    protected String getProperty(String key) {
        if (prop == null) {
            prop = new Properties();
            try {
//                prop.load(new FileInputStream(tcData.getPath() + "/../default/resources.properties"));
                prop.load(new FileInputStream("data/default/resources.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return prop.getProperty(key);
    }

    public String getImage(FreeColSpecObjectType buildable) {
        String folder = buildable instanceof BuildingType ? "buildingicon" : "unit";
        ImageResource imageResource = null;
        if (buildable instanceof BuildingType) {
            final String buildingTypeKey = ImageLibrary.getBuildingTypeKey((BuildingType) buildable);
            imageResource = ResourceManager.getImageResource(buildingTypeKey, true);
        } else if (buildable instanceof GoodsType) {
            folder = "icon";
            imageResource = ResourceManager.getImageResource("image." + folder + "." + buildable.getId(), true);
        } else {
            imageResource = ResourceManager.getImageResource("image." + folder + "." + buildable.getId(), true);
        }

        final Path imagePath = Paths.get(imageResource.getResourceLocator().getPath());
        final Path relativizeToTcData = Paths.get(tcData.getPath()).toAbsolutePath().relativize(imagePath);
        return Paths.get("{RESOURCES_PATH}").resolve(relativizeToTcData).toString();
    }

    public String includeImage(FreeColSpecObjectType building) {
        return getFormatter().image(getImage(building), building.getId());
    }


}
