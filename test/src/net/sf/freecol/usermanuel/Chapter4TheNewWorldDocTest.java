package net.sf.freecol.usermanuel;

import net.sf.freecol.client.gui.label.ProductionLabel;
import net.sf.freecol.common.model.*;
import net.sf.freecol.docastest.FreeColDocAsTest;
import net.sf.freecol.docastest.gui.DocGenerator;
import net.sf.freecol.docastest.gui.FreeColGuiDocAsTest;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class generate something similar to the https://www.freecol.org/docs/FreeCol.html#x1-500004[chapter 4] of the https://www.freecol.org/docs/FreeCol.html[user manual].
 */
@DisplayName(value = "Chapter 4 - The New World")
public class Chapter4TheNewWorldDocTest extends FreeColGuiDocAsTest {
    @Test
    public void introduction() {

        final EuropeanNationType defaultNation = (EuropeanNationType) FreeColDocAsTest.spec().getDefaultNationType();

        final Map<String, List<AbstractUnit>> startingUnits = defaultNation.getStartingUnits().stream()
                .collect(Collectors.groupingBy(u -> {
                    String id = u.getId();
                    final UnitType unitType = FreeColDocAsTest.spec().getUnitType(id);
                    if (unitType.isNaval()) {
                        return "Vessel";
                    } else if (unitType.isPerson()) {
                        return "Colonist";
                    } else {
                        return id;
                    }
                }));

        String startingUnitsDescription = startingUnits.entrySet().stream().map(e -> {
                    return e.getValue().size()
                            + " "
                            + getLabel(e.getKey(), e.getValue().size() > 1);
//                                    + " (" +
//                                    includeImage(e, FreeColDocAsTest.spec().getUnitType(e.getKey()))
//                                    + ")"
                })
                .collect(Collectors.joining(" and "));

        write("At the beginning of the game, you will start with " + startingUnitsDescription + ".",
                "Your first task will be to discover the New World, which should lie due West, although sailing North West or South West may prove quicker.",
                "As soon as you have discovered land, you can establish your colonies and produce goods to send home to Europe."
        );
    }

    @Test
    public void terrainTypes() {
        writeln(":table-caption!: ");
        writeln("There are many different types of terrain in the New World, each with its own peculiar advantages.",
                "At the beginning of the game you will probably arrive at a High Seas tile (or at the edge of the map).",
                "High Seas tiles (and the map edge) allow you to sail between Europe and the New World.",
                "As you approach land, the High Seas will be replaced by Ocean tiles, which produce Fish.");

        final TileType plainTile = spec().getTileType("model.tile.plains");


        final List<ProductionType> availableProductionTypes = plainTile.getAvailableProductionTypes(false);
        String text = availableProductionTypes.stream().sorted(new Comparator<ProductionType>() {
            @Override
            public int compare(ProductionType o1, ProductionType o2) {
                return o2.getOutputs().map(AbstractGoods::getAmount).findFirst().get().compareTo(o1.getOutputs().map(AbstractGoods::getAmount).findFirst().get());
            }
        }).map(t -> {
            final AbstractGoods x = t.getOutputs().findFirst().get();
            System.out.println(x);

            final String label = getLabel(x.getType().getSuffix(), false);
            if (x.getAmount() > 4) {
                return " a great deal of " + label;
            } else if (x.getAmount() > 1) {
                return "a lesser amount of " + label;
            } else {
                return "and some " + label;
            }
        }).collect(Collectors.joining(", "));

        final String plainDescription = getLabel(plainTile.getSuffix(), false) + ", which produce " + text;
        String productionOriginal = String.join("; ",
                plainDescription,
                getProductionDescription("model.tile.grassland", "{tile}, on which {production} can be cultivated"), // "Plains, which produce a great deal of Grain, a lesser amount of Cotton, and some Ore";
                getProductionDescription("model.tile.prairie", "{tile}, which are suitable for growing {production}"),
                getProductionDescription("model.tile.savannah", "{tile}, which produces {production}"),
                getProductionDescription("model.tile.marsh", "{tile}, where {production} can be cultivated or mined"), // Marsh, where Grain can be cultivated and some Ore can be mined
                getProductionDescription("model.tile.swamp", "{tile}, which yields some {production}"), // Swamp, which yields some Grain, and small amounts of Sugar, Tobacco and Ore
                getProductionDescription("model.tile.desert", "{tile}, which produces {production}"), // Desert, which produce some Food, Cotton and Ore
                getProductionDescription("model.tile.tundra", "{tile}, where {production} can be grown or mined") // as well as Tundra, where Grain can be grown, and some Ore can be mined
        );

        final List<String> tileList = spec().getTileTypeList().stream()
                .filter(tileType -> !tileType.isForested()
                        && !tileType.isWater()
                        && !tileType.isElevation() // Hills + Moutains
                        // There is no filter for Arctic
                )
                .map(TileType::getIdType)
                .collect(Collectors.toList());

        final String productionList = "\n.List of production\n"
                + tileList.stream()
                .map(t -> getProductionDescription(t, "* {tile}: {production}"))
                .collect(Collectors.joining("\n"))
                + "\n";

        final String productionTable = ".Production table\n" + createProductionTable(tileList, "Chapter4_image_terrain_%s.jpg");

        // How to have a specific text ?
        // We can just list values we want to display and write`can` or `can't` following the case the code produce.
        // Here the difficulties is that we want a different output for the same input to avoid repetition.
        // It's not adapted to this approach.
        // Do we need to have a so exhaustive list in a user manuel ?
        // In that case, we may display it as a bullet point list or a table.

        writeln("In the New World, you will also discover " + productionOriginal);
        writeln(productionList);
        writeln(productionTable);

        writeln("Large parts of the New World are covered in forests, all of which yield varying amounts of Grain, Lumber and Furs",
                "The Boreal Forest also produces Ore, the Mixed Forest Cotton, the Conifer Forest Tobacco, the Tropical Forest Sugar, the Rain Forest produces small amounts of Ore, Sugar and Tobacco, the Wetland Forest and the Scrub Forest yield some Ore, and the Broadleaf Forest Cotton");

        spec().getTileTypeList().stream()
                .filter(tileType -> !tileType.isForested()
                        && !tileType.isWater()
                        && !tileType.isElevation() // Hills + Moutains
                )
                .forEach(t -> System.out.println(">>> "+ t.getSuffix()));

        final List<String> tileForestList = spec().getTileTypeList().stream()
                .filter(TileType::isForested)
                .map(FreeColObject::getIdType)
                .collect(Collectors.toList());

        final String productionForestTable = createProductionTable(tileForestList, "Chapter4_image_forest_%s.jpg");
        writeln(".Forest production table", productionForestTable);

        final List<String> otherTile = spec().getTileTypeList().stream()
                .map(FreeColObject::getIdType)
                .filter(t ->
                        !tileForestList.contains(t) && !tileList.contains(t))
                .collect(Collectors.toList());

        final String productionOtherTable = createProductionTable(otherTile, "Chapter4_image_other_%s.jpg");
        writeln(".Other terrain production table", productionOtherTable);


        writeln("The Hills produce a small amount of Grain, and can be mined for Ore and a lesser amount of Silver",
                "The Mountains are unsuitable for agriculture, but yield some Ore and Silver",
                "Arctic tiles are the least useful type of terrain, as they produce nothing at all",
                "Terrain types that produce no Grain, such as the Mountains and Arctic types, can not support colonies");


        writeln("Clearing or plowing a tile, and building a road require spending 20 tools",
                "Therefore, these actions can only be carried out by units carrying at least 20 tools",
                "You can equip your units in your colonies or in Europe");

        writeln("include::sortable-table.adoc[]");
    }

    private String createProductionTable(List<String> tileList, String nameTemplate) {
        boolean unattended = false;
        final Set<GoodsType> goodsHeader = tileList.stream()
                .map(id -> spec().getTileType(id))
                .flatMap(t -> t.getAvailableProductionTypes(unattended).stream()
                        .flatMap(p -> p.getOutputs().map(o -> o.getType())))
                .collect(Collectors.toSet());


        List<String> lines = tileList.stream()
                .map(tileId -> spec().getTileType(tileId))
                .map(tileType -> {
                    String line = goodsHeader.stream()
                            .map(g -> buildProductionCellContent(tileType, g, unattended, nameTemplate))
                            .collect(Collectors.joining(" | "));

                    return ""
                            + "a| " + includeImage(tileType, tileType.getSuffix(), Map.of("width", "40px"))
                            + " a| " + getLabel(tileType, false)
                            + "|" + line;
                }).collect(Collectors.toList());


        String productionTable = "\n\n[%autowidth]\n|====\n" + String.join("\n",
                /*"2+^| Not working with sortable */"^| Tile | a| " + goodsHeader.stream().map(g -> includeImage(g)/*getLabel(g, false)*/).collect(Collectors.joining(" a| ")),
                "",
                lines.stream().collect(Collectors.joining("\n"))
        ) + "\n|====\n";
        return productionTable;
    }

    private String buildProductionCellContent(TileType tileType, GoodsType goodType, boolean unattended, String nameTemplate) {
        return tileType.getAvailableProductionTypes(unattended).stream()
                .flatMap(p -> p.getOutputs())
                .filter(good -> good.getType().equals(goodType))
                .findFirst()
                .map(good -> buildImage(good.getAmount(), good.getType().getSuffix() + good.getAmount(), nameTemplate, good.getType()))
                .orElse("");
    }

    private String buildImage(Integer amount, String name, String nameTemplate, GoodsType goodsType) {
        AbstractGoods ag = new AbstractGoods();
        ag.setType(goodsType);
        ag.setAmount(amount);

        final ProductionLabel productionLabel = new ProductionLabel(client, ag);

        final DocGenerator.ImageFile imageFile = DocGenerator.componentToImage(productionLabel, IMAGE_PATH, String.format(nameTemplate, name));
        return imageFile.imageWithChecksum() + "\n"; //+ String.format("\nWith amount %d", amount);
    }

    private static String getProductionDescription(String tileId, String template) {
        final TileType tile = spec().getTileType(tileId);
        return template.replace("{tile}", getLabel(tile.getSuffix(), false))
                .replace("{production}", getProduction(tile));
    }

    private static String getProduction(TileType grassLand) {
        return grassLand.getAvailableProductionTypes(false).stream()
                .map(x -> {
                    final GoodsType type = x.getOutputs().findFirst().get().getType();
                    return getLabel(type, false);
                })
                .collect(Collectors.joining(" and "));
    }


    private static String getUnitName(Map.Entry<String, List<AbstractUnit>> e, boolean plural) {
        final String suffix = FreeColDocAsTest.spec().getUnitType(e.getKey()).getSuffix();
        return getLabel(suffix, plural);
    }

    private static String getLabel(FreeColObject object, boolean plural) {
        return getLabel(object.getSuffix(), plural);
    }

    private static String getLabel(String suffix, boolean plural) {
        return "`" + suffix.substring(0, 1).toUpperCase()
                + suffix.substring(1).replaceAll("([A-Z])", " $1")
                + (plural ? "s" : "") + "`";
    }

    private String includeImage(Map.Entry<String, List<AbstractUnit>> e, UnitType unitType) {
        return includeImage(unitType, e.getKey());
    }

    private String includeImage(FreeColSpecObjectType unitType, String title) {
        return includeImage(unitType, title, Map.of("width", "15px"));
    }

    private String includeImage(FreeColSpecObjectType tileType, String title, Map<String,String> options) {
        final Map<String, String> optionsMap = new HashMap<>(Map.of("title", title));
        optionsMap.putAll(options);
        String optionsString = optionsMap.entrySet().stream().map(e -> e.getKey() + "=\"" + e.getValue() + "\"").collect(Collectors.joining(", "));
        return String.format("\nimage:%s[%s]\n", getImage(tileType), optionsString);
    }

}
