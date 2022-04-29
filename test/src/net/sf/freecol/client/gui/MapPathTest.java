package net.sf.freecol.client.gui;

import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Role;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.util.LogBuilder;
import net.sf.freecol.docastest.gui.DocGenerator;
import net.sf.freecol.docastest.gui.FreeColGuiDocAsTest;
import net.sf.freecol.util.test.FreeColTestCase.MapBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapPathTest extends FreeColGuiDocAsTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        getGame().changeMap(new Map(getGame(), 10, 10));
    }

    @Test
    public void testMapFindPath() throws InterruptedException, IOException {

        getGame().changeMap(new MapBuilder(getGame())
                .setDimensions(5, 9)
                .setBaseTileType(RESOURCES.prairie)
                .build());

        final PathNode path = getGame().getMap().findPath(getUnit(),
                getGame().getMap().getTile(0, 1),
                getGame().getMap().getTile(4, 8),
                null, null, null);

        final DocGenerator.ImageFile imageFile = imageGenerator.generateImageWith(getGame().getMap(), path, "findPath.jpg");

        write("", imageFile.imageWithChecksum(),
                "", pathToTable(path));
    }

    @Test
    public void testMapFindPathWithObstacle() throws InterruptedException, IOException {

        getGame().changeMap(new MapBuilder(getGame())
                .setDimensions(5, 9)
                .setBaseTileType(RESOURCES.prairie)
                .setTileType(1, 7, RESOURCES.highSeas)
                .setTileType(2, 6, RESOURCES.highSeas)
                .setTileType(2, 7, RESOURCES.highSeas)
                .setTileType(2, 8, RESOURCES.highSeas)
                .build());


        LogBuilder log = new LogBuilder(500);
        final PathNode path = getGame().getMap().findPath(getUnit(),
                getGame().getMap().getTile(0, 1),
                getGame().getMap().getTile(4, 8),
                null, null, log);

        System.out.println(log.toString());

        final DocGenerator.ImageFile imageFile = imageGenerator.generateImageWith(getGame().getMap(), path, "findPathWithObstacle.jpg");

        write("", imageFile.imageWithChecksum(),
                "", pathToTable(path));

    }

    @Test
    public void testMapFindComplexePath() throws InterruptedException, IOException {

        getGame().changeMap(new MapBuilder(getGame())
                .setDimensions(5, 9)
                .setBaseTileType(RESOURCES.prairie)
                .setTileType(0, 1, RESOURCES.highSeas)
                .setTileType(2, 0, RESOURCES.highSeas)
                .setTileType(1, 1, RESOURCES.highSeas)
                .setTileType(0, 2, RESOURCES.highSeas)
                .setTileType(2, 2, RESOURCES.highSeas)
                .setTileType(1, 3, RESOURCES.highSeas)
                .setTileType(1, 4, RESOURCES.highSeas)
                .setTileType(0, 5, RESOURCES.highSeas)
                .setTileType(2, 7, RESOURCES.highSeas)
                .setTileType(2, 8, RESOURCES.highSeas)
                .setTileType(3, 6, RESOURCES.highSeas)
                .setTileType(3, 5, RESOURCES.highSeas)
                .setTileType(3, 4, RESOURCES.highSeas)
                .setTileType(3, 3, RESOURCES.highSeas)
                .setTileType(4, 4, RESOURCES.highSeas)
                .build());


        final PathNode path = getGame().getMap().findPath(getUnit(),
                getGame().getMap().getTile(0, 0),
                getGame().getMap().getTile(4, 8),
                null, null, null);

        final DocGenerator.ImageFile imageFile = imageGenerator.generateImageWith(getGame().getMap(), path, "findComplexePath.jpg");

        write("", imageFile.imageWithChecksum(),
                "", pathToTable(path));

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
