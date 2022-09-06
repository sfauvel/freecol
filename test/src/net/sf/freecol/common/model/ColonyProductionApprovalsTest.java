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
import org.approvaltests.core.Options;
import org.approvaltests.namer.ApprovalNamer;
import org.junit.Test;

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

    @Test
    public void testGetPotentialProductionInReadableTable() {
        Game game = getGame();
        FreeColTestCase.MapBuilder builder = new FreeColTestCase.MapBuilder(getGame());
        game.changeMap(builder.build());

        Colony colony = getStandardColony(1);
        ColonyTile colonyTile = colony.getColonyTile(colony.getTile());
        Building townHall = colony.getBuilding(townHallType);

        final List<String> governement_limits = Arrays.asList(GameOptions.GOOD_GOVERNMENT_LIMIT, GameOptions.VERY_GOOD_GOVERNMENT_LIMIT);

        writeln("|====");
        writeln("| Location | Type | " + governement_limits.stream()
                        .map(s -> s.substring(s.lastIndexOf('.') + 1))
                        .collect(Collectors.joining(" / ")) + " | Unit ",
                "");
        for (WorkLocation building : Arrays.asList(townHall, colonyTile)) {

            for (GoodsType goodsType : Arrays.asList(cottonType, bellsType)) {
                final Map<String, List<UnitType>> units_keys = new HashMap<>();

                Map<String, Map<String, Map.Entry<Integer, List<UnitType>>>> collectByUnitsForLimit = new HashMap<>();
                for (String governement_limit : governement_limits) {
                    setLiberty(game, colony, governement_limit);

                    final Map<Integer, List<UnitType>> collect = spec().getUnitTypeList().stream()
                            .collect(Collectors.groupingBy(unitType -> building.getPotentialProduction(goodsType, unitType)));

                    final Map<String, Map.Entry<Integer, List<UnitType>>> collectByUnits = collect.entrySet().stream()
                            .collect(Collectors.toMap(
                                    e -> e.getValue().stream().map(UnitType::getSuffix).collect(Collectors.joining(",")),
                                    e -> e));

                    units_keys.putAll(collectByUnits.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue())));
                    collectByUnitsForLimit.put(governement_limit, collectByUnits);
                }
                for (String units_key : units_keys.keySet()) {
                    List<String> datas = new ArrayList<>();
                    for (String governement_limit : governement_limits) {
                        final Map<String, Map.Entry<Integer, List<UnitType>>> collectByUnits = collectByUnitsForLimit.get(governement_limit);
                        datas.add(Optional.ofNullable(collectByUnits.get(units_key)).map(e -> e.getKey().toString()).orElse(""));
                    }

                    writeln("| " + ((building instanceof Building) ? ((Building) building).getType().getSuffix() : "Colony tile"));
                    writeln("| " + goodsType.getSuffix());
                    writeln("| " + datas.stream().collect(Collectors.joining(" / ")));
                    writeln("a| " + Optional.ofNullable(units_keys.get(units_key)).map(e -> writeUnitCell(e)).orElse(""));
                }

            }
        }

        writeln("|====");

        final Options options = new Options().forFile().withExtension(".adoc");
        Approvals.verify(buffer.toString(), options);
    }

    private String writeUnitCell(List<UnitType> units) {
        int NB_BEFORE_COLLAPSE = 2;
        if (units.size() > NB_BEFORE_COLLAPSE) {
            return
                    "[%collapsible]\n" +
                            "." + units.stream()
                            .limit(NB_BEFORE_COLLAPSE)
                            .map(UnitType::getSuffix)
                            .collect(Collectors.joining(", ", "", ", ...")) + "\n" +
                            "====\n" +
                            units.stream()
                                    .skip(NB_BEFORE_COLLAPSE)
                                    .map(UnitType::getSuffix)
                                    .collect(Collectors.joining(", ")) +
                            "\n" +
                            "====";
        } else {
            return units.stream()
                    .map(UnitType::getSuffix)
                    .collect(Collectors.joining(", "));
        }
    }

    private int setLiberty(Game game, Colony colony, String governementLimit) {
        final int libertyAmount = game.getSpecification().getInteger(governementLimit);
        colony.modifyLiberty(-colony.getLiberty());
        colony.modifyLiberty(libertyAmount);
        return libertyAmount;
    }

}
