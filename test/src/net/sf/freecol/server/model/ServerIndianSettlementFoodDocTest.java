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
import net.sf.freecol.server.ServerTestHelper;
import net.sf.freecol.util.test.FreeColTestCase.IndianSettlementBuilder;
import org.junit.Test;
import org.sfvl.printer.SvgGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ServerIndianSettlementFoodDocTest extends FreeColDocAsTest {

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


    @Test
    public void testFoodConsumption() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        assertEquals(1, camp.getUnitCount());
        assertEquals(0, camp.getGoodsCount(foodType));

        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();
        assertTrue("Food Produced should be more the food consumed", foodProduced > foodConsumed);

        ServerTestHelper.newTurn();

        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);
        assertEquals("Unexpected value for remaining food, ", foodRemaining, camp.getGoodsCount(foodType));
    }

    @Test
    public void testFoodConsumption_1() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        assertEquals(1, camp.getUnitCount());
        assertEquals(0, camp.getGoodsCount(foodType));

        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();
        assertTrue("Food Produced should be more the food consumed", foodProduced > foodConsumed);

        ServerTestHelper.newTurn();

        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);
        assertEquals("Unexpected value for remaining food, ", foodRemaining, camp.getGoodsCount(foodType));

        write(Integer.toString(camp.getGoodsCount(foodType)));
    }

    /**
     * Ecriture de l'équivalent de chaque assertion.
     */
    @Test
    public void testFoodConsumption_2() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        assertEquals(1, camp.getUnitCount());
        assertEquals(0, camp.getGoodsCount(foodType));

        writeln("UnitCount:" + camp.getUnitCount());
        writeln("GoodsCount for " + foodType + ": " + camp.getGoodsCount(foodType));

        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();

        writeln("foodProduced:" + foodProduced);
        writeln("foodConsumed:" + foodConsumed);
        assertTrue("Food Produced should be more the food consumed", foodProduced > foodConsumed);
        String comparaison = comparaison_sign(foodProduced, foodConsumed);
        writeln("foodProduced " + comparaison + " foodConsumed");

        ServerTestHelper.newTurn();
        writeln("new turn");

        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);
        assertEquals("Unexpected value for remaining food, ", foodRemaining, camp.getGoodsCount(foodType));
        writeln("foodRemaining: " + foodRemaining);
        writeln("GoodsCount for " + foodType + ": " + camp.getGoodsCount(foodType));

    }

    /**
     * Mise en forme.
     */
    @Test
    public void testFoodConsumption_3() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        assertEquals(1, camp.getUnitCount());
        assertEquals(0, camp.getGoodsCount(foodType));

        writeln("UnitCount:" + camp.getUnitCount());
        writeln("GoodsCount for *" + foodType.getSuffix() + "*: " + camp.getGoodsCount(foodType));

        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();
        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);

        writeln("[%autowidth]",
                getFormatter().tableWithHeader(Arrays.asList(
                        Arrays.asList("Produced", "Consumed", "Remaining"),
                        Arrays.asList(foodProduced, foodConsumed, foodRemaining)
                )));
        assertTrue("Food Produced should be more the food consumed", foodProduced > foodConsumed);
        String comparaison = comparaison_sign(foodProduced, foodConsumed);
        writeln("foodProduced " + comparaison + " foodConsumed");

        ServerTestHelper.newTurn();
        writeln("new turn");

        assertEquals("Unexpected value for remaining food, ", foodRemaining, camp.getGoodsCount(foodType));
        writeln("GoodsCount for *" + foodType.getSuffix() + "*: " + camp.getGoodsCount(foodType));

    }

    /**
     * Suppression des assertions.
     */
    @Test
    public void testFoodConsumption_4() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        writeln("UnitCount:" + camp.getUnitCount());
        writeln("GoodsCount for *" + foodType.getSuffix() + "*: " + camp.getGoodsCount(foodType));

        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();
        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);

        writeln("[%autowidth]",
                getFormatter().tableWithHeader(Arrays.asList(
                        Arrays.asList("Produced", "Consumed", "Remaining"),
                        Arrays.asList(foodProduced, foodConsumed, foodRemaining)
                )));

        String comparaison = comparaison_sign(foodProduced, foodConsumed);
        writeln("foodProduced " + comparaison + " foodConsumed");

        ServerTestHelper.newTurn();
        writeln("new turn");
        writeln("GoodsCount for *" + foodType.getSuffix() + "*: " + camp.getGoodsCount(foodType));

    }

    /**
     * Ajouter des images.
     */
    @Test
    public void testFoodConsumption_5() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        writeln("UnitCount:" + camp.getUnitCount());
        writeln("GoodsCount for " + getImage(foodType) + ": " + camp.getGoodsCount(foodType));


        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();
        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);

        writeln("[%autowidth]",
                getFormatter().tableWithHeader(Arrays.asList(
                        Arrays.asList("Produced", "Consumed", "Remaining"),
                        Arrays.asList(foodProduced, foodConsumed, foodRemaining)
                )));

        String comparaison = comparaison_sign(foodProduced, foodConsumed);
        writeln("foodProduced " + comparaison + " foodConsumed");

        ServerTestHelper.newTurn();
        writeln("new turn");
        writeln("GoodsCount for " + getImage(foodType) + ": " + camp.getGoodsCount(foodType));

    }


    /**
     * Ajouter d'un graph sur la production et la consommation en fonction du nombre d'unités.
     */
    @Test
    public void testFoodConsumption_6() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        final List<Integer> lineProduced = new ArrayList<>();
        final List<Integer> lineConsumed = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
            IndianSettlement camp = builder.initialBravesInCamp(i).build();
            lineProduced.add(camp.getTotalProductionOf(grainType));
            lineConsumed.add(camp.getFoodConsumption());
        }
        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        writeln("++++",
                new SvgGraph()
                        .withLine("Produced", lineProduced)
                        .withLine("Consumed", lineConsumed)
                        .generate(),
                "++++",
                "");

    }

    /**
     * Ajouter d'un graph quantité nourriture / nombre d'unités
     */
    @Test
    public void testFoodConsumption_7() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        final List<Integer> lineGoodsCount = new ArrayList<>();
        final List<Integer> lineUnitCount = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ServerTestHelper.newTurn();
            lineGoodsCount.add(camp.getGoodsCount(foodType));
            lineUnitCount.add(camp.getOwnedUnitList().size());
        }


        writeln("++++",
                new SvgGraph()
                        .withLine("Food", lineGoodsCount)
                        .withLine("Unit", lineUnitCount)
                        .generate(),
                "++++",
                "");

    }

    /**
     * Ajouter d'un graph quantité nourriture / nombre d'unités
     */
    @Test
    public void testFoodConsumption_8() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        final MyGraph myGraph = new MyGraph();
        for (int i = 0; i < 100; i++) {
            ServerTestHelper.newTurn();
            myGraph.addData(
                    camp.getGoodsCount(foodType),
                    camp.getOwnedUnitList().size()
            );
        }

        writeln("++++",
                myGraph.generate(),
                "++++",
                "");
    }

    class MyGraph extends SvgGraph {
        List<List> lines = new ArrayList<>();

        public void addData(Object... datas) {
            if (lines.isEmpty()) {
                for (int i = 0; i < datas.length; i++) {
                    lines.add(new ArrayList());
                }
            }

            for (int i = 0; i < datas.length; i++) {
                lines.get(i).add(datas[i]);
            }
        }

        @Override
        public String generate() {
            for (int i = 0; i < lines.size(); i++) {
                withLine("", lines.get(i));
            }
            return super.generate();
        }
    }


    private String comparaison_sign(int foodProduced, int foodConsumed) {
        return foodProduced < foodConsumed
                ? "<"
                : foodProduced > foodConsumed
                ? ">" : "=";
    }

    private String getImage(GoodsType goods) {
        return getFormatter().image(
                "{ROOT_PATH}/../../data/default/resources/images/goods/" + goods.getSuffix() + ".png"
                , goods.getSuffix());
    }
}
