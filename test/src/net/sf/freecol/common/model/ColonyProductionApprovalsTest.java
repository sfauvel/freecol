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

import net.sf.freecol.common.option.GameOptions;
import net.sf.freecol.util.test.FreeColTestCase;
import org.approvaltests.Approvals;


import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;


public class ColonyProductionApprovalsTest extends FreeColTestCase {

    private static final BuildingType countryType
            = spec().getBuildingType("model.building.country");
    private static final BuildingType depotType
            = spec().getBuildingType("model.building.depot");
    private static final BuildingType townHallType
            = spec().getBuildingType("model.building.townHall");

    private static final GoodsType bellsType
            = spec().getGoodsType("model.goods.bells");
    private static final GoodsType clothType
            = spec().getGoodsType("model.goods.cloth");
    private static final GoodsType cottonType
            = spec().getGoodsType("model.goods.cotton");
    private static final GoodsType crossesType
            = spec().getGoodsType("model.goods.crosses");
    private static final GoodsType foodType
            = spec().getGoodsType("model.goods.food");
    private static final GoodsType grainType
            = spec().getGoodsType("model.goods.grain");
    private static final GoodsType horsesType
            = spec().getGoodsType("model.goods.horses");

    private static final ResourceType grainResource
            = spec().getResourceType("model.resource.grain");

    private static final TileType plainsType
            = spec().getTileType("model.tile.plains");

    private static final UnitType freeColonistType
            = spec().getUnitType("model.unit.freeColonist");
    private static final UnitType pioneerType
            = spec().getUnitType("model.unit.hardyPioneer");
    private static final UnitType veteranSoldierType
            = spec().getUnitType("model.unit.veteranSoldier");


    private StringBuffer buffer = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        buffer = new StringBuffer();
    }

    public void writeln(Object... objects) {
        buffer.append(Arrays.stream(objects).map(o -> o.toString()).collect(Collectors.joining(" ")));
        buffer.append("\n");
    }

    public void testGetPotentialProduction() {
        Game game = getGame();
        FreeColTestCase.MapBuilder builder = new FreeColTestCase.MapBuilder(getGame());
        game.changeMap(builder.build());

        Colony colony = getStandardColony(1);
        ColonyTile colonyTile = colony.getColonyTile(colony.getTile());

        Building townHall = colony.getBuilding(townHallType);

        for (WorkLocation building : Arrays.asList(townHall, colonyTile)) {
            for (GoodsType goodsType : Arrays.asList(cottonType, bellsType)) {
                for (UnitType unitType : spec().getUnitTypeList()) {
                    for (String governementLimit : Arrays.asList(GameOptions.GOOD_GOVERNMENT_LIMIT, GameOptions.VERY_GOOD_GOVERNMENT_LIMIT)) {
                        final int libertyAmount = setLiberty(game, colony, governementLimit);

                        writeln(((building instanceof Building) ? ((Building) building).getType().getSuffix() : "Colony tile"),
                                goodsType.getSuffix(),
                                Optional.ofNullable(unitType).map(UnitType::getSuffix).orElse(""),
                                libertyAmount,
                                building.getPotentialProduction(goodsType, unitType));
                    }
                }

            }
        }

        Approvals.verify(buffer.toString());
    }

    private int setLiberty(Game game, Colony colony, String governementLimit) {
        final int libertyAmount = game.getSpecification().getInteger(governementLimit);
        colony.modifyLiberty(-colony.getLiberty());
        colony.modifyLiberty(libertyAmount);
        return libertyAmount;
    }

}
