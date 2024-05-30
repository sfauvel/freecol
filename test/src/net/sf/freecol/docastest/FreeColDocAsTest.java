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
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.sf.freecol.common.util.CollectionUtils.*;
import static net.sf.freecol.common.util.CollectionUtils.alwaysTrue;

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
        String folder = null;
        String suffix = "";
        if (buildable instanceof BuildingType) {
            folder = "buildingicon";
        } else if (buildable instanceof GoodsType) {
            folder = "icon";
        } else if (buildable instanceof TileType) {
            TileType tileType = ((TileType) buildable);
            if (tileType.isForested()) {
                folder = "tileforest";
                //image.tileforest.model.tile.borealForest
            } else {
                folder = "tile";
                suffix = ".center";
//                image.tile.model.tile.grassland.center
            }
        } else {
            folder = "unit";
        }
        final String image_id = "image." + folder + "." + buildable.getId() + suffix;

        try  {
            getImage(image_id);
        } catch (Exception e) {
            System.out.println("FreeColDocAsTest.getImage " + e.getMessage());
        }
        return getImage(image_id);
    }

    public String getImage(Unit unit) {

        final String roleSuffix = Optional.ofNullable(unit.getAutomaticRole())
                .map(Role::getRoleSuffix)
                .map(suffix -> "." + suffix)
                .orElse("");
        String folder = "unit";
        final String image_id = "image." + folder + "." + unit.getType() + roleSuffix;

        return getImage(image_id);
    }

    private String getImage(String image_id) {
        ImageResource imageResource = ResourceManager.getImageResource(image_id, true);
        if (imageResource == null ) {
            ResourceManager.getImageResource(image_id, true);
            System.out.println("No image " + image_id);
        }
        final Path imagePath = Paths.get(imageResource.getResourceLocator().getPath());
        final Path relativizeToTcData = Paths.get(tcData.getPath()).toAbsolutePath().relativize(imagePath);
        return Paths.get("{RESOURCES_PATH}").resolve(relativizeToTcData).toString();
    }

    public String includeImage(FreeColSpecObjectType building) {
        return getFormatter().image(getImage(building), building.getId());
    }

    // SEE ImageLibrary.getUnitTypeImageKey
    public String includeImage(Unit unit) {

        return getFormatter().image(getImage(unit), unit.getId());
    }
}
