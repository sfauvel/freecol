package net.sf.freecol.docastest;

import net.sf.freecol.common.model.*;
import net.sf.freecol.docastest.ModelObjects.building;
import net.sf.freecol.docastest.ModelObjects.goods;
import net.sf.freecol.docastest.ModelObjects.role;
import net.sf.freecol.docastest.gui.DocGenerator;
import net.sf.freecol.docastest.gui.FreeColGuiDocAsTest;
import net.sf.freecol.server.model.ServerUnit;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Before;
import org.junit.Test;
import org.sfvl.codeextraction.CodeExtractor;
import org.sfvl.codeextraction.MethodReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SampleDocTest extends FreeColGuiDocAsTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
        getGame().changeMap(new Map(getGame(), 10, 10));
    }

    @Test
    public void create_a_map() throws InterruptedException, IOException {

        // >>>
        final Map map = new FreeColTestCase.MapBuilder(getGame(), 5, 6)
                .setBaseTileType(RESOURCES.prairie)
                .setTileType(2, 2, RESOURCES.mountains)
                .setTileType(2, 1, RESOURCES.mountains)
                .setTileType(2, 3, RESOURCES.mountains)
                .setTileType(3, 2, RESOURCES.mountains)
                .build();
        getGame().changeMap(map);
        // <<<

        final DocGenerator.ImageFile imageFile = imageGenerator.generateImageWith(getGame().getMap(), null, getFullTestName() + ".jpg");

        write("",
                "The `" + FreeColTestCase.MapBuilder.class.getSimpleName() + "` is a tool to create a map.",
                "You can specify width and height in the constructor.",
                "Because of the hexagonal representation, for the same value, it seems to have more tile in width than there is in heght.",
                "",
                "You can indicates the default tile type and select another type for specific tiles.",
                getFormatter().sourceCode(CodeExtractor.extractPartOfCurrentMethod()),
                imageFile.imageWithChecksum()
        );
    }

    @Test
    public void display_a_map() throws InterruptedException, IOException {

        // >>>
        getGame().changeMap(new FreeColTestCase.MapBuilder(getGame(), 2, 3).build());

        final DocGenerator.ImageFile imageFile =
                imageGenerator.generateImageWith(
                        getGame().getMap(),
                        null,
                        getFullTestName() + ".jpg");

        final String text_to_write = imageFile.imageWithChecksum();
        // <<<

        write("",
                "It's possible to generate an image of the map using the `imageGenerator`",
                getFormatter().sourceCode(CodeExtractor.extractPartOfCurrentMethod()),
                "You need to give a unique name for the image so that it will not overwritten by another test.",
                "The `" + MethodReference.getName(this::getFullTestName) + "` method generates a name with the full path of the class following by the method name.",
                "It helps you to create a unique name",
                "",
                "From the `" + DocGenerator.ImageFile.class.getSimpleName() + "` generated, you can extract the text to insert in your document with  `" + MethodReference.getName((MethodReference.SerializableRunnable) imageFile::imageWithChecksum) + "` method",
                "It add the link to the image and a checksum in comment to ensure that the image is always the same.",
                getFormatter().sourceCode(text_to_write)
        );
    }

    @Test
    public void display_an_image() throws InterruptedException, IOException {

        write("If you want to display the image of an element, use the following code",
                getFormatter().sourceCode(CodeExtractor.extractPartOfCurrentMethod()));

        //        //        final String defaultIndianPlayer = "model.nation.tupi";
        //        //        final Player indianPlayer = getGame().getPlayerByNationId(defaultIndianPlayer);
                final Player player = new Player(getGame(), "me");
                final ServerUnit serverUnit = new ServerUnit(getGame(), null, player, ModelObjects.unit.brave) {
//                    @Override
//                    public Role getAutomaticRole() {
//                        // AuomaticRole depend on the colony or settlement where the unit is.
//                        // We mocked it to simplify the test.
////                        return ModelObjects.role.nativeDragoon;
//                        return ModelObjects.role.dragoon;
//                    }
                };
        final ServerUnit serverUnit2 = new ServerUnit(getGame(), null, player, ModelObjects.unit.brave) {
                    @Override
                    public Role getAutomaticRole() {
                        // AuomaticRole depend on the colony or settlement where the unit is.
                        // We mocked it to simplify the test.
//                        return ModelObjects.role.nativeDragoon;
                        return ModelObjects.role.nativeDragoon;
                    }
        };
                final Role automaticRole = serverUnit.getAutomaticRole();
        // >>>
        write(
                includeImage(building.church),
                includeImage(goods.muskets),
                includeImage(serverUnit)
        );
        // <<<
    }

    private String pathToTable(PathNode path) {
        return toTable(imageGenerator.pathToList(path.getFirstNode()),
                toTableLine("Position", "Cost", "Turns", "Direction"),
                p -> toTableLine(
                        p.getTile().getX() + " / " + p.getTile().getY(),
                        Integer.toString(p.getCost()),
                        Integer.toString(p.getTurns()),
                        (p.getDirection() == null) ? "" : p.getDirection().toString()));
    }

    private <T> String toTable(java.util.List<T> objectList, String headerLine, Function<T, String> lineMapper) {
        return objectList.stream()
                .map(lineMapper)
                .collect(Collectors.joining("\n", "[%autowidth, options=header]\n|====\n" + headerLine + "\n", "\n|===="));
    }

    public String toTableLine(String... texts) {
        return Arrays.stream(texts).collect(Collectors.joining(" | ", "| ", ""));
    }

    private Unit getUnit() {
        Unit unit = new Unit(getGame(), "AAA");
        unit.setType(RESOURCES.freeColonist);
        unit.setRole(new Role("XXX", getGame().getSpecification()));
        unit.setOwner(getGame().getFirstPlayer());
        return unit;
    }
}