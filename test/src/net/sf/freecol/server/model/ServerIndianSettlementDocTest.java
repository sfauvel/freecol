/**
 * Copyright (C) 2002-2021  The FreeCol Team
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

package net.sf.freecol.server.model;

import net.sf.freecol.common.model.*;
import net.sf.freecol.docastest.FreeColDocAsTest;
import net.sf.freecol.docastest.FreeColFormatter;
import net.sf.freecol.server.ServerTestHelper;
import net.sf.freecol.util.test.FreeColTestCase.IndianSettlementBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ServerIndianSettlementDocTest extends FreeColDocAsTest {

    private static final GoodsType clothType
            = spec().getGoodsType("model.goods.cloth");
    private static final GoodsType coatsType
            = spec().getGoodsType("model.goods.coats");
    private static final GoodsType foodType
            = spec().getPrimaryFoodType();
    private static final GoodsType grainType
            = spec().getGoodsType("model.goods.grain");
    private static final GoodsType horsesType
            = spec().getGoodsType("model.goods.horses");
    private static final GoodsType rumType
            = spec().getGoodsType("model.goods.rum");
    private static final GoodsType toolsType
            = spec().getGoodsType("model.goods.tools");

    private static final TileType desertType
            = spec().getTileType("model.tile.desert");
    private static final TileType plainsType
            = spec().getTileType("model.tile.plains");

    private static final UnitType brave
            = spec().getUnitType("model.unit.brave");

    private FreeColFormatter formatter = new FreeColFormatter();

//    public void testFoodConsumption() {
//        Game game = ServerTestHelper.startServerGame(getTestMap());
//
//        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
//        IndianSettlement camp = builder.build();
//
//        assertEquals(1, camp.getUnitCount());
//        assertEquals(0, camp.getGoodsCount(foodType));
//
//        int foodProduced = camp.getTotalProductionOf(grainType);
//        int foodConsumed = camp.getFoodConsumption();
//        assertTrue("Food Produced should be more the food consumed",foodProduced > foodConsumed);
//
//        ServerTestHelper.newTurn();
//
//        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);
//        assertEquals("Unexpected value for remaining food, ", foodRemaining,camp.getGoodsCount(foodType));
//    }
//
//    public void testHorseBreeding() {
//        Game game = ServerTestHelper.startServerGame(getTestMap());
//
//        IndianSettlementBuilder builder
//            = new IndianSettlementBuilder(game);
//        IndianSettlement camp = builder.build();
//
//        //verify initial conditions
//        assertEquals(1, camp.getUnitCount());
//        assertEquals(0, camp.getGoodsCount(foodType));
//
//        //add horses
//        int initialHorses = horsesType.getBreedingNumber();
//        camp.addGoods(horsesType, initialHorses);
//
//        // verify that there is food production for the horses.
//        // Using freecol rules where horses eat grain
//        assertEquals("Horses need grain", grainType, horsesType.getInputType());
//        int foodProduced = camp.getTotalProductionOf(grainType);
//        int foodConsumed = camp.getFoodConsumption();
//        int foodAvail = foodProduced - foodConsumed;
//        assertTrue("Food Produced should be more the food consumed",
//                   foodProduced > foodConsumed);
//
//        int expectedHorseProd = Math.min(ServerIndianSettlement.MAX_HORSES_PER_TURN,
//                                         foodAvail);
//        assertTrue("Horses should breed", expectedHorseProd > 0);
//
//        ServerTestHelper.newTurn();
//
//        int horsesBred = camp.getGoodsCount(horsesType) - initialHorses;
//        assertEquals("Wrong number of horses bred",
//                     expectedHorseProd, horsesBred);
//    }
//
//    public void testHorseBreedingNoFoodAvail() {
//        Game game = ServerTestHelper.startServerGame(getTestMap(desertType));
//
//        int initialBravesInCamp = 3;
//        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
//        IndianSettlement camp1 = builder.initialBravesInCamp(initialBravesInCamp).build();
//        IndianSettlement camp2 = builder.reset()
//            .settlementTile(camp1.getTile().getNeighbourOrNull(Direction.N)
//                            .getNeighbourOrNull(Direction.N)).build();
//
//        //////////////////////
//        // Simulate that only the center tile is owned by camp 1
//        // Does not matter where camp 2 is, so we put it in the same tile as camp1
//        for (Tile t: camp1.getTile().getSurroundingTiles(camp1.getRadius())) {
//            t.changeOwnership(camp2.getOwner(), camp2);
//        }
//
//
//        //verify initial conditions
//        assertEquals(initialBravesInCamp, camp1.getUnitCount());
//        assertEquals(0, camp1.getGoodsCount(foodType));
//
//        int foodProduced = camp1.getTotalProductionOf(grainType);
//        int foodConsumed = camp1.getFoodConsumption();
//        assertEquals(2, brave.getConsumptionOf(foodType));
//        assertEquals(2 * camp1.getUnitCount(), foodConsumed);
//        assertTrue("Food Produced should be less the food consumed",foodProduced < foodConsumed);
//
//        //add horses
//        int initialHorses = 2;
//        camp1.addGoods(horsesType, initialHorses);
//
//        ServerTestHelper.newTurn();
//
//        int expectedHorsesBreeded = 0;
//        int horsesBreeded = camp1.getGoodsCount(horsesType) - initialHorses;
//        assertEquals("No horses should be bred",expectedHorsesBreeded,horsesBreeded);
//    }

    @Test
    public void testPricing() {
        write("Inidans give a price when you sell them something. We describe how this price is computed.");

        Game game = ServerTestHelper.startServerGame(getTestMap(plainsType));

        final int braveCount = 4;
        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.initialBravesInCamp(braveCount).build();
        final int topPrice = IndianSettlement.GOODS_BASE_PRICE + camp.getType().getTradeBonus();

        // Clear wanted goods so as not to confuse comparisons
        camp.setWantedGoods(0, null);
        camp.setWantedGoods(1, null);
        camp.setWantedGoods(2, null);

//        assertEquals(braveCount, camp.getUnitCount());

        write("", "Indians count in camp: " + camp.getUnitCount(), "");

        write("", "The top price for the camp: " + topPrice, "");

        write("", "Wanted goods: ",
                camp.getWantedGoods().stream()
                        .map(good -> "* " + (good == null ? "NULL" : good.getNameKey()))
                        .collect(Collectors.joining("\n")),
                ""
        );

        {
            int unitNb = 1;
            final GoodsType type = ServerIndianSettlementDocTest.horsesType;
            List<List<?>> tableData = new ArrayList<>();
            tableData.add(Arrays.asList("Amount in camp", String.format("Price for %d unit", unitNb)));
            for (int i = 0; i < 3; i++) {
                tableData.add(Arrays.asList(
                        camp.getGoodsCount(type),
                        camp.getPriceToBuy(type, unitNb)
                ));
                camp.addGoods(type, 50);
            }

            write("",
                    "Should initially value military goods highly.",
                    "But once there are enough for all the braves, the price should fall",
                    "below half of the high price.",
                    "",
                    "[%autowidth]",
                    ".Price for " + type.toString(),
                    formatter.tableWithHeader(tableData),
                    "");
        }
//        // Should initially value military goods highly
//        assertEquals("High price for horses", topPrice,
//                     camp.getPriceToBuy(ServerIndianSettlementDocTest.horsesType, 1));

//        // But once there are enough for all the braves, the price should fall
//        camp.addGoods(ServerIndianSettlementDocTest.horsesType, 50);
//        assertEquals("Still high price for horses", topPrice,
//                camp.getPriceToBuy(ServerIndianSettlementDocTest.horsesType, 1));
//        camp.addGoods(ServerIndianSettlementDocTest.horsesType, 50);
//        assertTrue("Commercial price for horses",
//                camp.getPriceToBuy(ServerIndianSettlementDocTest.horsesType, 1) <= topPrice / 2);

        {
            int unitNb = 100;
            List<List<?>> tableData = new ArrayList<>();
            tableData.add(Arrays.asList("Good", "Amount in camp", String.format("Price for %d unit", unitNb)));

            final GoodsType type = ServerIndianSettlementDocTest.grainType;
            tableData.add(Arrays.asList(
                    type,
                    camp.getGoodsCount(type),
                    camp.getPriceToBuy(type, unitNb)
            ));

            write("",
                    "Farmed goods should be much cheaper.",
                    "It should be below half of the high price.",
                    "",
                    "[%autowidth]",
                    ".Price for " + type.toString(),
                    formatter.tableWithHeader(tableData),
                    "");
        }

//        // Farmed goods should be much cheaper
//        assertTrue("Grain is farmed", grainType.isFarmed());
//        assertTrue("Devalue farmed goods",
//                camp.getPriceToBuy(grainType, 100) <= 100 * topPrice / 2);

        {
            int unitNb = 1;
            List<List<?>> tableData = new ArrayList<>();
            tableData.add(Arrays.asList("Good", "Amount in camp", "Unit buy", "Price for x unit"));

            final GoodsType type = ServerIndianSettlementDocTest.rumType;
            tableData.add(Arrays.asList(
                    type,
                    camp.getGoodsCount(type),
                    unitNb,
                    camp.getPriceToBuy(type, unitNb)
            ));
            camp.addGoods(type, 100);
            tableData.add(Arrays.asList(
                    type,
                    camp.getGoodsCount(type),
                    unitNb,
                    camp.getPriceToBuy(type, unitNb)
            ));
            unitNb = 99;
            tableData.add(Arrays.asList(
                    type,
                    camp.getGoodsCount(type),
                    unitNb,
                    camp.getPriceToBuy(type, unitNb)
            ));
            camp.addGoods(rumType, 100);
            unitNb = 1;
            tableData.add(Arrays.asList(
                    type,
                    camp.getGoodsCount(type),
                    unitNb,
                    camp.getPriceToBuy(type, unitNb)
            ));
            write("",
                    "Rum is more interesting but the price falls with amount present.",
                    "",
                    "[%autowidth]",
                    ".Price for " + type.toString(),
                    formatter.tableWithHeader(tableData),
                    "");
        }

//        // Rum is more interesting...
//        assertEquals(0, camp.getGoodsCount(rumType));
//        assertEquals("Buy rum", topPrice,
//                camp.getPriceToBuy(rumType, 1));
//
//        // ...but the price falls with amount present
//        camp.addGoods(rumType, 100);
//        assertTrue("Add rum",
//                camp.getPriceToBuy(rumType, 1) <= topPrice / 2);
//        assertTrue("Add more rum",
//                camp.getPriceToBuy(rumType, 99) <= 99 * topPrice / 2);
//        camp.addGoods(rumType, 100);
//        assertEquals("Do not buy more rum", 0,
//                camp.getPriceToBuy(rumType, 1));

        write("",
                "On plains cotton can be grown, so cloth should be cheaper than coats.",
                "Cloth ("
                        + camp.getPriceToBuy(clothType, 50)
                        + ") cheaper than coats ("
                        + camp.getPriceToBuy(coatsType, 50) + ")",
                "");

        {
            int unitNb = 50;
            List<List<?>> tableData = new ArrayList<>();
            tableData.add(Arrays.asList(
                    "Unit buy",
                    clothType.getId() + " count",
                    "Price for " + clothType.getId(),
                    coatsType.getId() + " count",
                    "Price for " + coatsType.getId()));

            tableData.add(Arrays.asList(
                    unitNb,
                    camp.getGoodsCount(clothType),
                    camp.getPriceToBuy(clothType, unitNb),
                    camp.getGoodsCount(coatsType),
                    camp.getPriceToBuy(coatsType, unitNb)
            ));
            camp.addGoods(clothType, 20);
            camp.addGoods(coatsType, 20);
            tableData.add(Arrays.asList(
                    unitNb,
                    camp.getGoodsCount(clothType),
                    camp.getPriceToBuy(clothType, unitNb),
                    camp.getGoodsCount(coatsType),
                    camp.getPriceToBuy(coatsType, unitNb)
            ));
            camp.addGoods(clothType, 100);
            camp.addGoods(coatsType, 100);

            unitNb = 1;
            tableData.add(Arrays.asList(
                    unitNb,
                    camp.getGoodsCount(clothType),
                    camp.getPriceToBuy(clothType, unitNb),
                    camp.getGoodsCount(coatsType),
                    camp.getPriceToBuy(coatsType, unitNb)
            ));
            write("",
                    "On plains cotton can be grown, so cloth should be cheaper than coats.",
                    "",
                    "[%autowidth]",
                    ".Prices",
                    formatter.tableWithHeader(tableData),
                    "");
        }

//        // On plains cotton can be grown, so cloth should be cheaper than
//        // coats.
//        assertTrue("Cloth ("
//                        + camp.getPriceToBuy(clothType, 50)
//                        + ") cheaper than coats ("
//                        + camp.getPriceToBuy(coatsType, 50) + ")",
//                camp.getPriceToBuy(clothType, 50) < camp.getPriceToBuy(coatsType, 50));
//        camp.addGoods(clothType, 20);
//        camp.addGoods(coatsType, 20);
//        assertTrue("Cloth still ("
//                        + camp.getPriceToBuy(clothType, 50)
//                        + ") cheaper than coats ("
//                        + camp.getPriceToBuy(coatsType, 50) + ")",
//                camp.getPriceToBuy(clothType, 50) < camp.getPriceToBuy(coatsType, 50));
//        camp.addGoods(clothType, 100);
//        camp.addGoods(coatsType, 100);
//        assertEquals("Cloth now ignored", 0,
//                camp.getPriceToBuy(clothType, 1));
//        assertEquals("Coats now ignored", 0,
//                camp.getPriceToBuy(coatsType, 1));

        {
            int unitNb = 50;
            List<List<?>> tableData = new ArrayList<>();
            tableData.add(Arrays.asList(
                    "Wanted position",
                    "Price to buy " + unitNb + " units"));

            final GoodsType type = ServerIndianSettlementDocTest.toolsType;

            Supplier<String> positionOfGood = () -> {
                    int position = camp.getWantedGoods().indexOf(type);
                    return position == -1 ? "None" : Integer.toString(position + 1);
            };

            IntStream.range(0,3).forEach(__ -> camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType));
            tableData.add(Arrays.asList(
                    positionOfGood.get(),
                    camp.getPriceToBuy(ServerIndianSettlementDocTest.toolsType, unitNb)));

            IntStream.range(0,3).forEach(__ -> camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType));
            camp.setWantedGoods(2, toolsType);
            tableData.add(Arrays.asList(
                    positionOfGood.get(),
                    camp.getPriceToBuy(ServerIndianSettlementDocTest.toolsType, unitNb)));

            IntStream.range(0,3).forEach(__ -> camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType));
            camp.setWantedGoods(1, toolsType);
            tableData.add(Arrays.asList(
                    positionOfGood.get(),
                    camp.getPriceToBuy(ServerIndianSettlementDocTest.toolsType, unitNb)));

            IntStream.range(0,3).forEach(__ -> camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType));
            camp.setWantedGoods(0, toolsType);
            tableData.add(Arrays.asList(
                    positionOfGood.get(),
                    camp.getPriceToBuy(ServerIndianSettlementDocTest.toolsType, unitNb)));

            write("",
                    "Check that wanted goods at least increases the price.",
                    "",
                    "[%autowidth]",
                    ".Prices",
                    formatter.tableWithHeader(tableData),
                    "");
        }

//        // Check that wanted goods at least increases the price
//        camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType);
//        camp.setWantedGoods(1, ServerIndianSettlementDocTest.horsesType);
//        camp.setWantedGoods(0, ServerIndianSettlementDocTest.horsesType);
//        int p3 = camp.getPriceToBuy(toolsType, 50);
//        camp.setWantedGoods(2, toolsType);
//        camp.setWantedGoods(1, ServerIndianSettlementDocTest.horsesType);
//        camp.setWantedGoods(0, ServerIndianSettlementDocTest.horsesType);
//        int p2 = camp.getPriceToBuy(toolsType, 50);
//        assertTrue("Wanted 2: (" + p2 + " > " + p3 + ")", p2 > p3);
//        camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType);
//        camp.setWantedGoods(1, toolsType);
//        camp.setWantedGoods(0, ServerIndianSettlementDocTest.horsesType);
//        int p1 = camp.getPriceToBuy(toolsType, 50);
//        assertTrue("Wanted 1: (" + p1 + " > " + p2 + ")", p1 > p2);
//        camp.setWantedGoods(2, ServerIndianSettlementDocTest.horsesType);
//        camp.setWantedGoods(1, ServerIndianSettlementDocTest.horsesType);
//        camp.setWantedGoods(0, toolsType);
//        int p0 = camp.getPriceToBuy(toolsType, 50);
//        assertTrue("Wanted 0: (" + p0 + " > " + p1 + ")", p0 > p1);
    }
}
