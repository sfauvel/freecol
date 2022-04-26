/**
 * Copyright (C) 2002-2022  The FreeCol Team
 *
 * This file is part of FreeCol.
 *
 * FreeCol is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * FreeCol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.model;

import net.sf.freecol.common.io.FreeColTcFile;
import net.sf.freecol.docastest.FreeColDocAsTest;
import net.sf.freecol.docastest.FreeColFormatter;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Test;
import org.sfvl.codeextraction.CodeExtractor;
import org.sfvl.codeextraction.MethodReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ColonyDocTest extends FreeColDocAsTest {

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

    private static final BuildingType carpenterHouseType
            = spec().getBuildingType("model.building.carpenterHouse");
    private static final BuildingType churchType
            = spec().getBuildingType("model.building.church");
    private static final BuildingType depotType
            = spec().getBuildingType("model.building.depot");
    private static final BuildingType lumberMillType
            = spec().getBuildingType("model.building.lumberMill");
    private static final BuildingType townHallType
            = spec().getBuildingType("model.building.townHall");
    private static final BuildingType warehouseType
            = spec().getBuildingType("model.building.warehouse");
    private static final BuildingType warehouseExpansionType
            = spec().getBuildingType("model.building.warehouseExpansion");
    private static final BuildingType weaversHouseType
            = spec().getBuildingType("model.building.weaverHouse");

    private static final GoodsType bellsGoodsType
            = spec().getGoodsType("model.goods.bells");
    private static final GoodsType clothGoodsType
            = spec().getGoodsType("model.goods.cloth");
    private static final GoodsType cottonGoodsType
            = spec().getGoodsType("model.goods.cotton");
    private static final GoodsType foodGoodsType
            = spec().getPrimaryFoodType();
    private static final GoodsType grainGoodsType
            = spec().getGoodsType("model.goods.grain");
    private static final GoodsType hammerGoodsType
            = spec().getGoodsType("model.goods.hammers");
    private static final GoodsType lumberGoodsType
            = spec().getGoodsType("model.goods.lumber");

    private static final Role soldierRole
            = spec().getRole("model.role.soldier");

    private static final TileType arcticTileType
            = spec().getTileType("model.tile.arctic");
    private static final TileType plainsTileType
            = spec().getTileType("model.tile.plains");

    private static final UnitType cottonPlanterType
            = spec().getUnitType("model.unit.masterCottonPlanter");
    private static final UnitType elderStatesmanType
            = spec().getUnitType("model.unit.elderStatesman");
    private static final UnitType freeColonistType
            = spec().getUnitType("model.unit.freeColonist");
    private static final UnitType masterWeaverType
            = spec().getUnitType("model.unit.masterWeaver");
    private static final UnitType wagonTrainType
            = spec().getUnitType("model.unit.wagonTrain");
    private static final UnitType braveType
            = spec().getUnitType("model.unit.brave");

    private FreeColFormatter formatter = new FreeColFormatter();

    FreeColTcFile tcData = FreeColTcFile.getFreeColTcFile("classic");
    Properties prop = null;

    private String getProperty(String key) {
        if (prop == null) {
            prop = new Properties();
            try {
                prop.load(new FileInputStream(tcData.getPath() + "/resources.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return prop.getProperty(key);
    }


    public String getImage(BuildableType buildable) {
        String folder = buildable instanceof BuildingType ? "buildingicon" : "unit";
        return Paths.get("{RESOURCES_PATH}").resolve(getProperty("image." + folder + "." + buildable.getId())).toString();
    }

    public String includeImage(BuildableType building) {
        return formatter.image(getImage(building), building.getId());
    }


    private String formatAfterSettingBuildingTypeImage(Colony colony, List<BuildableType> buildingToSet) {
        return formatAfterSettingBuildingType(colony, buildingToSet,
                this::includeImage,
                this::displayQueue);
    }

    private String formatAfterSettingBuildingTypeText(Colony colony, List<BuildableType> buildingToSet) {
        return formatAfterSettingBuildingType(colony, buildingToSet,
                type -> type.getId(),
                c -> Integer.toString(c.getBuildQueue().size()));
    }

    private String formatAfterSettingBuildingType(Colony colony, List<BuildableType> buildingToSet,
                                                  Function<BuildableType, String> key, Function<Colony, String> queue) {
        final String table = buildingToSet.stream()
                .map(type -> {
                    colony.setCurrentlyBuilding(type);
                    return String.format("a| %s\na| %s", key.apply(type), queue.apply(colony));
                }).collect(Collectors.joining("\n\n", "|====\n| Building asked | Queue size\n\n", "\n|===="));
        return table;
    }

    private String displayQueue(Colony colony) {
        return colony.getBuildQueue().stream()
                .map(type -> includeImage(type))
                .collect(Collectors.joining("\n"));
    }

    private String displayColony(Colony colony) {
        return String.join("\n",
                ".Standard colony",
                "[%collapsible]",
                "====",
                "Name : " + colony.getName(),
                "",
                "Current building : " + includeImage(colony.getCurrentlyBuilding()),
                "====");
    }


    // //////////////////////////////////////

    @Test
    public void testCurrentlyBuilding() throws IOException {
        Game game = getGame();
        game.changeMap(getTestMap(true));

        Colony colony = getStandardColony();
        write("Initial currently building: *" + colony.getCurrentlyBuilding() + "* +", "");

        final BuildingType newBuilding = ColonyDocTest.churchType;
        colony.setCurrentlyBuilding(newBuilding);
        write("When setting building with *" + newBuilding + "* +",
                "The new currently building is: *" + colony.getCurrentlyBuilding() + "*");

    }

    @Test
    public void testBuildQueueDoesNotAcceptBuildingDoubles_Original() {
        final String code = CodeExtractor.extractMethodBody(MethodReference.getMethod(ColonyTest::testBuildQueueDoesNotAcceptBuildingDoubles));

        write(formatter.sourceCode(code));
    }

    @Test
    public void testBuildQueueDoesNotAcceptBuildingDoubles_Basic() {
        Game game = getGame();
        game.changeMap(getTestMap(true));

        Colony colony = getStandardColony();

        // We don't need to assert after each action.
        // We can have the same treatment for each value.
        final List<BuildableType> buildingToSet = Arrays.asList(
                warehouseType,
                warehouseType,
                churchType,
                warehouseType);

        write("Build queue does not accept building doubles.",
                "The queue size is incremented only with a new building.",
                "",
                formatAfterSettingBuildingTypeText(colony, buildingToSet));
    }

    @Test
    public void testBuildQueueDoesNotAcceptBuildingDoubles() {
        Game game = getGame();
        game.changeMap(getTestMap(true));

        Colony colony = getStandardColony();

        // We don't need to assert after each action.
        // We can have the same treatment for each value.
        final List<BuildableType> buildingToSet = Arrays.asList(
                warehouseType,
                warehouseType,
                churchType,
                warehouseType);

        write("Build queue does not accept building doubles.",
                "The queue size is incremented only with a new building.",
                "",
                displayColony(colony),
                "",
                formatAfterSettingBuildingType(colony, buildingToSet,
                        type -> includeImage(type),
                        this::displayQueue));

    }

    @Test
    public void testBuildQueueAcceptsUnitDoubles_Original() {
        final String code = CodeExtractor.extractMethodBody(MethodReference.getMethod(ColonyTest::testBuildQueueAcceptsUnitDoubles));

        write(formatter.sourceCode(code));
    }

    @Test
    public void testBuildQueueAcceptsUnitDoubles_Basic() {
        Game game = getGame();
        game.changeMap(getTestMap(true));

        Colony colony = getStandardColony();

        final List<BuildableType> buildingToSet = Arrays.asList(
                wagonTrainType,
                wagonTrainType);

        // We can reuse table printer.
        write("Build queue is incremented when building an other item.",
                "",
                "Initial currently building: *" + colony.getCurrentlyBuilding() + "* +",
                "",
                formatAfterSettingBuildingTypeText(colony, buildingToSet));
    }

    @Test
    public void testBuildQueueAcceptsUnitDoubles() {
        Game game = getGame();
        game.changeMap(getTestMap(true));

        Colony colony = getStandardColony();

        final List<BuildableType> buildingToSet = Arrays.asList(
                wagonTrainType,
                wagonTrainType);

        // We can reuse table printer.
        write("Build queue is incremented when building an other item.",
                "",
                displayColony(colony),
                "",
                formatAfterSettingBuildingTypeImage(colony, buildingToSet));
    }
//
//    public void testOccupationWithFood() {
//        Game game = getGame();
//        game.changeMap(getTestMap(true));
//
//        int population = 1;
//        Colony colony = getStandardColony(population);
//
//        assertTrue("colony should produce enough food",
//                   colony.getFoodProduction() > colony.getFoodConsumption()
//                   + freeColonistType.getConsumptionOf(grainGoodsType));
//
//        // colonist with no skill or experience will produce food
//        Unit colonist = new ServerUnit(game, colony.getTile(),
//                                       colony.getOwner(), freeColonistType);
//        nonServerJoinColony(colonist, colony);
//        assertTrue(colonist.getLocation() instanceof ColonyTile);
//        assertEquals(grainGoodsType, colonist.getWorkType());
//
//        // colonist with experience in producing farmed goods will
//        // produce that type of goods
//        colonist.setLocation(colony.getTile());
//        colonist.changeWorkType(cottonGoodsType);
//        colonist.modifyExperience(100);
//        nonServerJoinColony(colonist, colony);
//        assertTrue(colonist.getLocation() instanceof ColonyTile);
//        assertEquals(cottonGoodsType, colonist.getWorkType());
//
//        // expert will produce expert goods
//        colonist.setLocation(colony.getTile());
//        colonist.setType(cottonPlanterType);
//        colonist.changeWorkType(null);
//        nonServerJoinColony(colonist, colony);
//        assertTrue(colonist.getLocation() instanceof ColonyTile);
//        assertEquals(cottonGoodsType, colonist.getWorkType());
//
//        // expert will produce expert goods
//        colonist.setLocation(colony.getTile());
//        colonist.setType(elderStatesmanType);
//        colonist.changeWorkType(null);
//        nonServerJoinColony(colonist, colony);
//        assertTrue(colonist.getLocation() instanceof Building);
//        assertEquals(townHallType,
//                     ((Building) colonist.getLocation()).getType());
//        assertEquals(bellsGoodsType, colonist.getWorkType());
//    }
//
//    private int countParties(Colony colony) {
//        return count(colony.getModifiers("model.goods.bells"),
//            m -> Specification.COLONY_GOODS_PARTY_SOURCE.equals(m.getSource()));
//    }
//
//    public void testTeaParty() {
//        Game game = getGame();
//        game.changeMap(getTestMap(true));
//
//        Colony colony = getStandardColony(5);
//        Player player = colony.getOwner();
//
//        assertEquals(0, countParties(colony));
//        colony.addModifier(((ServerPlayer)player).makeTeaPartyModifier());
//        assertEquals(1, countParties(colony));
//        Modifier m = ((ServerPlayer)player).makeTeaPartyModifier();
//        m.setFirstTurn(new Turn(game.getTurn().getNumber() + 1));
//        colony.addModifier(m);
//        assertEquals(2, countParties(colony));
//    }
//
//    public void testAddUnitToColony() {
//        Game game = getGame();
//        game.changeMap(getTestMap(arcticTileType, true));
//
//        int population = 1;
//        Colony colony = getStandardColony(population);
//        assertTrue("colony should produce less food than it consumes",
//            colony.getFoodProduction() < colony.getFoodConsumption()
//            + freeColonistType.getConsumptionOf(foodGoodsType));
//
//        assertEquals(2, freeColonistType.getConsumptionOf(foodGoodsType));
//        assertEquals(2, masterWeaverType.getConsumptionOf(foodGoodsType));
//
//        // colonist produces bells, the colony needs them
//        Unit colonist = colony.getUnitList().get(0);
//        Building townHall = colony.getBuilding(townHallType);
//        assertEquals(townHall, colonist.getLocation());
//        assertEquals(townHall, colony.getWorkLocationFor(colonist));
//        assertEquals(bellsGoodsType, colonist.getWorkType());
//
//        // colonist might have experience, but the colony still needs bells
//        colonist.setLocation(colony.getTile());
//        colonist.changeWorkType(cottonGoodsType);
//        colonist.modifyExperience(100);
//        nonServerJoinColony(colonist, colony);
//        assertEquals(townHall, colony.getWorkLocationFor(colonist));
//        assertEquals(townHall, (Building)colonist.getLocation());
//        assertEquals(bellsGoodsType, colonist.getWorkType());
//
//        // Add a statesman and have it deal with the bells problem
//        colonist.setLocation(colony.getTile());
//        Unit statesman = new ServerUnit(game, colony.getTile(),
//                                        colony.getOwner(), elderStatesmanType);
//        nonServerJoinColony(statesman, colony);
//        assertEquals(townHall, statesman.getLocation());
//        assertEquals(bellsGoodsType, statesman.getWorkType());
//
//        // Add one plains tile
//        Tile plainsTile = colony.getTile().getNeighbourOrNull(Direction.S);
//        plainsTile.setType(plainsTileType);
//        ColonyTile colonyTile = colony.getColonyTile(plainsTile);
//        assertTrue(colonyTile.isEmpty());
//
//        // colonist experience might be cotton, but the colony needs food
//        colonist.setLocation(colony.getTile());
//        colonist.changeWorkType(cottonGoodsType);
//        colonist.modifyExperience(100);
//        nonServerJoinColony(colonist, colony);
//        assertEquals(colonyTile, colonist.getLocation());
//        assertEquals(grainGoodsType, colonist.getWorkType());
//
//        // Change the center tile to plains to improve the food situation
//        colony.getTile().changeType(plainsTileType);
//        colony.invalidateCache();
//        assertTrue("colony should produce more food than it consumes",
//            colony.getFoodProduction() >= colony.getFoodConsumption()
//            + freeColonistType.getConsumptionOf(foodGoodsType));
//
//        // colonist experience will now encourage cotton production
//        colonist.setLocation(colony.getTile());
//        colonist.changeWorkType(cottonGoodsType);
//        colonist.modifyExperience(100);
//        nonServerJoinColony(colonist, colony);
//        assertEquals(colonyTile, colonist.getLocation());
//        assertEquals(cottonGoodsType, colonist.getWorkType());
//
//        // colonist should still make cotton due to expertise
//        colonist.setLocation(colony.getTile());
//        colonist.changeWorkType(null);
//        colonist.setType(cottonPlanterType);
//        nonServerJoinColony(colonist, colony);
//        assertEquals(colonyTile, colonist.getLocation());
//        assertEquals(cottonGoodsType, colonist.getWorkType());
//
//        // colonist produces cloth, because there is cotton now
//        colonist.setLocation(colony.getTile());
//        colonist.changeWorkType(null);
//        colonist.setType(masterWeaverType);
//        colony.addGoods(cottonGoodsType, 100);
//        nonServerJoinColony(colonist, colony);
//        assertTrue(colonist.getLocation() instanceof Building);
//        Building weaversHouse = colony.getBuilding(weaversHouseType);
//        assertEquals(weaversHouse, (Building)colonist.getLocation());
//        assertEquals(clothGoodsType, colonist.getWorkType());
//    }
//
//    public void testFoundColony() {
//        Game game = getGame();
//        Map map = getCoastTestMap(plainsTileType);
//        game.changeMap(map);
//
//        Colony colony = getStandardColony(3, 1, 8);
//        Player dutch = colony.getOwner();
//        Tile colonyTile = colony.getTile();
//        assertEquals(colonyTile.getType(), plainsTileType);
//
//        Unit colonist = first(colony.getUnits());
//        Tile workedTile = null;
//        for (ColonyTile ct : colony.getColonyTiles()) {
//            if (ct.isColonyCenterTile()) continue;
//            workedTile = ct.getWorkTile();
//            if (workedTile.isInUse()) {
//                break;
//            } else if (workedTile.getType() == plainsTileType) {
//                colonist.changeWorkType(spec().getPrimaryFoodType());
//                colonist.setLocation(ct);
//                break;
//            }
//        }
//        assertTrue(workedTile.isInUse());
//
//        IndianSettlementBuilder builder
//            = new IndianSettlementBuilder(game);
//        IndianSettlement camp = builder.build();
//        Player indianPlayer = camp.getOwner();
//        Tile campTile = camp.getTile();
//        assertEquals(campTile.getType(), plainsTileType);
//
//        Tile nativeTile = null;
//        for (Tile t : camp.getTile().getSurroundingTiles(1)) {
//            if (t.getType() == plainsTileType) {
//                nativeTile = t;
//                break;
//            }
//        }
//        assertEquals(nativeTile.getOwner(), indianPlayer);
//
//        Player french = game.getPlayerByNationId("model.nation.french");
//        Colony frenchColony = getStandardColony(3, 9, 8);
//        frenchColony.changeOwner(french);
//        assertEquals(frenchColony.getTile().getType(), plainsTileType);
//        Tile frenchTile = null;
//        for (Tile t : frenchColony.getTile().getSurroundingTiles(1)) {
//            if (t.getType() == plainsTileType) {
//                frenchTile = t;
//                break;
//            }
//        }
//        assertEquals(frenchTile.getOwner(), french);
//
//        Tile landTile = map.getTile(1, 1);
//        Tile lcrTile = map.getTile(2, 2);
//        lcrTile.add(new LostCityRumour(game, lcrTile,
//                LostCityRumour.RumourType.NO_SUCH_RUMOUR, "fake"));
//        Tile waterTile = map.getTile(12, 12);
//        assertTrue(!waterTile.isLand());
//
//        assertTrue("Can own empty tile",
//                   dutch.canOwnTile(landTile));
//        assertFalse("Europeans can not own tile with an LCR on it",
//                    dutch.canOwnTile(lcrTile));
//        assertTrue("Natives can own tile with an LCR on it",
//                   indianPlayer.canOwnTile(campTile));
//        assertTrue("Europeans can own water tile",
//                   dutch.canOwnTile(waterTile));
//        assertFalse("Natives can not own water tile",
//                   indianPlayer.canOwnTile(waterTile));
//
//        assertEquals("Can found on land",
//                     NoClaimReason.NONE,
//                     dutch.canClaimToFoundSettlementReason(landTile));
//        assertEquals("Can found on unsettleable tile",
//                     NoClaimReason.TERRAIN,
//                     dutch.canClaimToFoundSettlementReason(waterTile));
//        assertEquals("Can not found on LCR",
//                     NoClaimReason.RUMOUR,
//                     dutch.canClaimToFoundSettlementReason(lcrTile));
//        assertEquals("Can not found on water",
//                     NoClaimReason.TERRAIN,
//                     indianPlayer.canClaimToFoundSettlementReason(waterTile));
//        assertEquals("Can not found on settlement",
//                     NoClaimReason.SETTLEMENT,
//                     dutch.canClaimToFoundSettlementReason(campTile));
//        assertEquals("Can not found on tile in use",
//                     NoClaimReason.WORKED,
//                     dutch.canClaimToFoundSettlementReason(workedTile));
//        assertEquals("Can not found on European tile",
//                     NoClaimReason.EUROPEANS,
//                     dutch.canClaimToFoundSettlementReason(frenchTile));
//        assertEquals("Might be able to found on native settlement tile",
//                     NoClaimReason.NATIVES,
//                     dutch.canClaimToFoundSettlementReason(nativeTile));
//        landTile.setOwner(indianPlayer);
//        assertEquals("Might be able to found on loose native tile",
//                     NoClaimReason.NATIVES,
//                     dutch.canClaimToFoundSettlementReason(landTile));
//        landTile.setOwner(null);
//
//        assertEquals("Can use land",
//                     NoClaimReason.NONE,
//                     dutch.canClaimForSettlementReason(landTile));
//        assertEquals("Can use unsettleable tile",
//                     NoClaimReason.NONE,
//                     dutch.canClaimForSettlementReason(waterTile));
//        assertEquals("Europeans can not use LCR",
//                     NoClaimReason.RUMOUR,
//                     dutch.canClaimForSettlementReason(lcrTile));
//        assertEquals("Natives can use LCR",
//                     NoClaimReason.NONE,
//                     indianPlayer.canClaimForSettlementReason(lcrTile));
//        assertEquals("Europeans can use water",
//                     NoClaimReason.NONE,
//                     dutch.canClaimForSettlementReason(waterTile));
//        assertEquals("Natives can not use water",
//                     NoClaimReason.WATER,
//                     indianPlayer.canClaimForSettlementReason(waterTile));
//        assertEquals("Can not use on settlement",
//                     NoClaimReason.SETTLEMENT,
//                     dutch.canClaimForSettlementReason(campTile));
//        assertEquals("Can not use tile in use",
//                     NoClaimReason.WORKED,
//                     dutch.canClaimForSettlementReason(workedTile));
//        assertEquals("Can not use European tile",
//                     NoClaimReason.EUROPEANS,
//                     dutch.canClaimForSettlementReason(frenchTile));
//        assertEquals("Can not use native tile",
//                     NoClaimReason.NATIVES,
//                     dutch.canClaimForSettlementReason(nativeTile));
//    }
//
//    public void testUnderSiege() {
//        Game game = getGame();
//        game.changeMap(getTestMap(true));
//
//        Colony colony = getStandardColony(5);
//        Tile tile = colony.getTile().getNeighbourOrNull(Direction.N);
//        Player iroquois = game.getPlayerByNationId("model.nation.iroquois");
//
//        assertFalse("No enemy units present.", colony.isUnderSiege());
//
//        Unit brave = new ServerUnit(game, tile, iroquois, braveType);
//        assertFalse("Not at war with the Iroquois.", colony.isUnderSiege());
//
//        // declare war
//        colony.getOwner().setStance(iroquois, Stance.WAR);
//        iroquois.setStance(colony.getOwner(), Stance.WAR);
//
//        assertTrue("At war with the Iroquois.", colony.isUnderSiege());
//
//        Role soldierRole = spec().getRole("model.role.soldier");
//        Unit soldier = new ServerUnit(game, colony.getTile(), colony.getOwner(),
//                                      freeColonistType, soldierRole);
//        assertFalse("Equal number of friendly and enemy combat units.",
//                    colony.isUnderSiege());
//
//        Unit brave2 = new ServerUnit(game, tile, iroquois, braveType);
//        assertTrue("Enemy combat units outnumber friendly combat units.",
//                   colony.isUnderSiege());
//
//        Unit colonist = new ServerUnit(game, colony.getTile(), colony.getOwner(),
//                                       freeColonistType);
//        assertTrue("Enemy combat units outnumber friendly combat units.",
//                   colony.isUnderSiege());
//
//        colonist.changeRole(soldierRole, 1);
//        assertFalse("Equal number of friendly and enemy combat units.",
//                    colony.isUnderSiege());
//    }
//
//
//    public void testUpkeep() {
//        Game game = getGame();
//        game.changeMap(getTestMap(true));
//
//        Colony colony = getStandardColony(5);
//
//        assertEquals("New colonies should not require upkeep.",
//                     0, colony.getUpkeep());
//
//        int churchUpkeep = churchType.getUpkeep();
//        colony.getBuilding(churchType).upgrade();
//        assertEquals(churchUpkeep, colony.getUpkeep());
//
//        int lumberMillUpkeep = lumberMillType.getUpkeep();
//        colony.getBuilding(carpenterHouseType).upgrade();
//        assertEquals(churchUpkeep + lumberMillUpkeep, colony.getUpkeep());
//    }
//
//    public void testCopyColony() {
//        Game game = getGame();
//        game.changeMap(getTestMap(true));
//
//        Colony colony = getStandardColony(2);
//        Player player = colony.getOwner();
//
//        Colony copied = colony.copyColony();
//        assertNotNull(copied);
//        assertFalse(colony == copied);
//        // Note: The following is true because it uses FCGO.equals().
//        assertTrue(colony.equals(copied));
//        assertEquals(colony.getId(), copied.getId());
//        assertEquals(colony.getName(), copied.getName());
//        // Note: we can not check that player.hasSettlement(copied) is false
//        // because it too will use FCGO.equals().
//        for (Settlement s : player.getSettlementList()) {
//            assertFalse(s == copied);
//        }
//
//        Tile ct = colony.getTile();
//        Tile oct = copied.getTile();
//        assertFalse(ct == oct);
//        assertEquals(ct.getId(), oct.getId());
//        assertEquals(ct.getUnitCount(), oct.getUnitCount());
//        assertEquals(ct.getType(), oct.getType());
//
//        assertEquals(oct.getColony(), copied);
//        assertEquals(oct.getOwningSettlement(), copied);
//
//        for (WorkLocation wl : colony.getAllWorkLocationsList()) {
//            WorkLocation owl = copied.getCorresponding(wl);
//            assertNotNull(owl);
//            assertFalse(wl == owl);
//            assertEquals(wl.getId(), owl.getId());
//            Tile wt = wl.getWorkTile();
//            if (wt != null) {
//                Tile owt = owl.getWorkTile();
//                assertFalse(wt == owt);
//                assertEquals(wt.getId(), owt.getId());
//                assertEquals(wt.getType(), owt.getType());
//                assertEquals(owt.getOwningSettlement(), copied);
//            }
//            assertEquals(wl.getUnitCount(), owl.getUnitCount());
//            for (Unit u : wl.getUnitList()) {
//                Unit ou = copied.getCorresponding(u);
//                assertNotNull(ou);
//                assertFalse(u == ou);
//                assertEquals(u.getId(), ou.getId());
//                assertEquals(u.getType(), ou.getType());
//                assertEquals(u.getRole(), ou.getRole());
//                assertEquals(game.getFreeColGameObject(u.getId()), u);
//                assertEquals(u.getOwner(), ou.getOwner());
//            }
//        }
//    }
}
