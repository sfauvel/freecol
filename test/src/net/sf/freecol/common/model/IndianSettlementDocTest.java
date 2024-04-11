/**
 *  Copyright (C) 2002-2022   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.model;

import net.sf.freecol.docastest.FreeColDocAsTest;
import net.sf.freecol.docastest.ModelObjects;
import net.sf.freecol.docastest.gui.FreeColGuiDocAsTest;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class IndianSettlementDocTest extends FreeColGuiDocAsTest {

    private static final GoodsType horsesType
        = spec().getGoodsType("model.goods.horses");
    private static final GoodsType musketsType
        = spec().getGoodsType("model.goods.muskets");

    private static final Role armedBraveRole
        = spec().getRole("model.role.armedBrave");
    private static final Role nativeDragoonRole
        = spec().getRole("model.role.nativeDragoon");

    @Test
    public void testAutomaticEquipBraves() {
        Game game = getStandardGame();
        Map map = getTestMap(game);
        game.changeMap(map);
        {
        FreeColTestCase.IndianSettlementBuilder builder
                = new FreeColTestCase.IndianSettlementBuilder(game);
        IndianSettlement camp = builder.initialBravesInCamp(2).build();

        Unit indianBrave = camp.getUnitList().get(0);

        writeln("Un Brave dans un campement devient un dragon lorsqu'il  y a des chevaux et des mousquets");
        writeln("[%collapsible]\n.Camp\n=====");
        writeln(camp.getUnits()
                .map(unit -> includeImage(unit.getType()))
                .collect(Collectors.joining("\n")));
        writeln("\n=====\n");

        writeln(formatRole(indianBrave));
        {
            final String goods = camp.getGoodsList().isEmpty()
                    ? "rien"
                    : camp.getGoodsList().stream()
                        .map(good -> good.getAmount() + " " + includeImage(good.getType()))
                        .collect(Collectors.joining(" et "));

            writeln("Un " + includeImage(indianBrave.getType()) + " dans un campement devient un " + includeImage(indianBrave) + " lorsqu'il y a " + goods);
        }
//        addGoods(camp, musketsType, 100);
        camp.addGoods(musketsType, 100);
//        writeln(formatRole(indianBrave));
//        writeln(includeImage(indianBrave));
        {
            final String goods = camp.getGoodsList().stream()
                    .map(good -> good.getAmount() + " " + includeImage(good.getType()))
                    .collect(Collectors.joining(" et "));

            writeln("Un " + includeImage(indianBrave.getType()) + " dans un campement devient un " + includeImage(indianBrave) + " lorsqu'il y a " + goods);
        }
//        addGoods(camp, horsesType, 100);
        camp.addGoods(horsesType, 100);
//        writeln(formatRole(indianBrave));
//        writeln(includeImage(indianBrave));
    }
        {
            int nbBraves = 2;
            HashMap<GoodsType, Integer> goods = new HashMap<GoodsType, Integer>();
            goods.put(musketsType, 25);
            goods.put(horsesType, 25);

            FreeColTestCase.IndianSettlementBuilder builder
                    = new FreeColTestCase.IndianSettlementBuilder(game);
            IndianSettlement camp = builder.initialBravesInCamp(nbBraves).build();

            final String unitsBefore = camp.getUnitList().stream().map(unit -> includeImage(unit)).collect(Collectors.joining(" "));

            goods.forEach((good, amount) -> camp.addGoods(good, amount));
            final String goodsImages = camp.getGoodsList().stream()
                    .map(good -> good.getAmount() + " " + includeImage(good.getType()))
                    .collect(Collectors.joining(" et "));
            writeln(unitsBefore
                    + " dans un campement devient "
                    + camp.getUnitList().stream().map(unit -> includeImage(unit)).collect(Collectors.joining(" "))
                    + " lorsqu'il y a "
                    + goodsImages);

        }
    }

    private static String formatRole(Unit indianBrave) {
        if (indianBrave.getAutomaticRole() == null) {
            return "Alors il n'a pas de rôle";
        } else {
            return "Alors le rôle est *" + indianBrave.getAutomaticRole().getRoleSuffix() + "*";
        }
    }

    private void addGoods(IndianSettlement camp, GoodsType good, int amount) {
        writeln("Après avoir ajouté " + amount + " " + includeImage(good));

        camp.addGoods(good, amount);
    }

    /*
     * Test settlement adjacent tiles ownership
     * Per Col1 rules, Indian settlements do not own water tiles
     */
    @Test
    public void testSettlementDoesNotOwnWaterTiles(){
        Game game = getStandardGame();
        Map map = FreeColTestCase.getCoastTestMap(spec().getTileType("model.tile.plains"));
        game.changeMap(map);

        Tile campTile = map.getTile(9, 9);
        Tile landTile = map.getTile(8, 9);
        Tile waterTile = map.getTile(10, 9);

        assertTrue("Setup error, camp tile should be land", campTile.isLand());
        assertTrue("Setup error, tile should be land", landTile.isLand());
        assertFalse("Setup error, tile should be water", waterTile.isLand());
        assertTrue("Setup error, tiles should be adjacent", campTile.isAdjacent(waterTile));
        assertTrue("Setup error, tiles should be adjacent", campTile.isAdjacent(landTile));

        FreeColTestCase.IndianSettlementBuilder builder = new FreeColTestCase.IndianSettlementBuilder(game);

        IndianSettlement camp = builder.settlementTile(campTile).build();

        Player indianPlayer = camp.getOwner();
        assertTrue("Indian player should own camp tile", campTile.getOwner() == indianPlayer);
        assertTrue("Indian player should own land tile", landTile.getOwner() == indianPlayer);
        assertFalse("Indian player should not own water tile", waterTile.getOwner() == indianPlayer);

        writeln("TODO");
    }

    /*
     * Test settlement trade
     */
    @Test
    public void testTradeGoodsWithSetlement(){
        Game game = getStandardGame();
        Map map = getTestMap(game);
        game.changeMap(map);

        Tile camp2Tile = map.getTile(3, 3);
        FreeColTestCase.IndianSettlementBuilder builder = new FreeColTestCase.IndianSettlementBuilder(game);
        IndianSettlement camp1 = builder.build();
        IndianSettlement camp2 = builder.reset().settlementTile(camp2Tile).build();
        final int notEnoughToShare = 50;
        final int enoughToShare = 100;
        final int none = 0;

        camp1.addGoods(horsesType, notEnoughToShare);
        camp1.addGoods(musketsType, enoughToShare);

        String wrongQtyMusketsMsg = "Wrong quantity of muskets";
        String wrongQtyHorsesMsg = "Wrong quantity of horses";

        assertEquals(wrongQtyMusketsMsg,enoughToShare,camp1.getGoodsCount(musketsType));
        assertEquals(wrongQtyHorsesMsg,notEnoughToShare,camp1.getGoodsCount(horsesType));
        assertEquals(wrongQtyMusketsMsg,none,camp2.getGoodsCount(musketsType));
        assertEquals(wrongQtyHorsesMsg,none,camp2.getGoodsCount(horsesType));

        camp1.tradeGoodsWithSettlement(camp2);

        assertEquals(wrongQtyMusketsMsg,enoughToShare / 2,camp1.getGoodsCount(musketsType));
        assertEquals(wrongQtyHorsesMsg,notEnoughToShare,camp1.getGoodsCount(horsesType));
        assertEquals(wrongQtyMusketsMsg,enoughToShare / 2,camp2.getGoodsCount(musketsType));
        assertEquals(wrongQtyHorsesMsg,none,camp2.getGoodsCount(horsesType));

        writeln("TODO");
    }
}
