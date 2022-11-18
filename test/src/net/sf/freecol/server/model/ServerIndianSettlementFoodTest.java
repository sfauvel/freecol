/**
 *  Copyright (C) 2002-2021  The FreeCol Team
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

package net.sf.freecol.server.model;

import net.sf.freecol.common.model.*;
import net.sf.freecol.server.ServerTestHelper;
import net.sf.freecol.util.test.FreeColTestCase;


public class ServerIndianSettlementFoodTest extends FreeColTestCase {

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


    public void testFoodConsumption() {
        Game game = ServerTestHelper.startServerGame(getTestMap());

        IndianSettlementBuilder builder = new IndianSettlementBuilder(game);
        IndianSettlement camp = builder.build();

        assertEquals(1, camp.getUnitCount());
        assertEquals(0, camp.getGoodsCount(foodType));

        int foodProduced = camp.getTotalProductionOf(grainType);
        int foodConsumed = camp.getFoodConsumption();
        assertTrue("Food Produced should be more the food consumed",foodProduced > foodConsumed);

        ServerTestHelper.newTurn();

        int foodRemaining = Math.max(foodProduced - foodConsumed, 0);
        assertEquals("Unexpected value for remaining food, ", foodRemaining,camp.getGoodsCount(foodType));
    }

}
