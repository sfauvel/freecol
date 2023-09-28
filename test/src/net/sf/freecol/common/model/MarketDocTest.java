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

import net.sf.freecol.common.FreeColException;
import net.sf.freecol.docastest.FreeColDocAsTest;
import net.sf.freecol.util.test.FreeColTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class MarketDocTest extends FreeColDocAsTest {

    /**
     * Make sure that the initial prices are correctly taken from the
     * specification
     */
    @Test
    public void testInitialMarket() {

        Game g = getStandardGame();

        Player p = g.getPlayerByNationId("model.nation.dutch");

        Market dm = p.getMarket();

        Specification s = spec();
        

        for (GoodsType good : s.getStorableGoodsTypeList()) {
            write(good.toString(), Integer.toString(good.getInitialBuyPrice()), Integer.toString(dm.getCostToBuy(good)));
            write(good.toString(), Integer.toString(good.getInitialSellPrice()), Integer.toString(dm.getPaidForSale(good)));
//            assertEquals(good.toString(), good.getInitialBuyPrice(), dm.getCostToBuy(good));
//            assertEquals(good.toString(), good.getInitialSellPrice(), dm.getPaidForSale(good));
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(dm.addGoodsToMarket(s.getStorableGoodsTypeList().get(5), 1));
        }
    }

    @Test
    public void testEuropeMarketPricing() {
        final Game g = getStandardGame();
        final Player p = g.getPlayerByNationId("model.nation.dutch");
        final Specification s = spec();
        final Europe eu = p.getEurope();

        for (GoodsType good : s.getGoodsTypeList()) {
            List<AbstractGoods> goods = new ArrayList<AbstractGoods>();
            write(Integer.toString(p.getMarket().getSalePrice(good, 1)), Integer.toString(eu.getOwner().getMarket().getSalePrice(good, 1)));
//            assertEquals(p.getMarket().getSalePrice(good, 1), eu.getOwner().getMarket().getSalePrice(good, 1));
            goods.add(new AbstractGoods(good, 1));
            int bidPrice = p.getMarket().getBidPrice(good, 1);
            int buyCost = p.getMarket().getCostToBuy(good);
            write(Integer.toString(buyCost), Integer.toString(bidPrice));
//            assertEquals(buyCost, bidPrice);
            try {
                int priceGoods = eu.priceGoods(goods);
                write(Integer.toString(buyCost), Integer.toString(priceGoods));
//                assertEquals(buyCost, priceGoods);
            } catch (FreeColException fce) {
                write(fce.getMessage());
//                fail(fce.getMessage());
            }
        }
    }

    /**
     * Serialization and deserialization?
     */

    @Test
    public void testSerialization() {
        //fail();
    }

    /**
     * Do the transaction listeners work?
     */
    @Test
    public void testTransactionListeners() {
        //fail("Not yet implemented");
    }
}
