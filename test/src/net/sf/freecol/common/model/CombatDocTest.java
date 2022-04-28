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

import net.sf.freecol.docastest.FreeColDocAsTest;
import net.sf.freecol.server.model.ServerUnit;
import org.junit.Test;
import org.sfvl.codeextraction.CodeExtractor;
import org.sfvl.codeextraction.MethodReference;
import org.sfvl.docformatter.asciidoc.AsciidocFormatter;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CombatDocTest extends FreeColDocAsTest {

    private static final Role armedBraveRole
            = spec().getRole("model.role.armedBrave");
    private static final Role cavalryRole
            = spec().getRole("model.role.cavalry");
    private static final Role dragoonRole
            = spec().getRole("model.role.dragoon");
    private static final Role infantryRole
            = spec().getRole("model.role.infantry");
    private static final Role missionaryRole
            = spec().getRole("model.role.missionary");
    private static final Role nativeDragoonRole
            = spec().getRole("model.role.nativeDragoon");
    private static final Role soldierRole
            = spec().getRole("model.role.soldier");

    private static final TileType hills
            = spec().getTileType("model.tile.hills");
    private static final TileType ocean
            = spec().getTileType("model.tile.ocean");
    private static final TileType plains
            = spec().getTileType("model.tile.plains");

    private static final UnitType artilleryType
            = spec().getUnitType("model.unit.artillery");
    private static final UnitType braveType
            = spec().getUnitType("model.unit.brave");
    private static final UnitType colonialRegularType
            = spec().getUnitType("model.unit.colonialRegular");
    private static final UnitType colonistType
            = spec().getUnitType("model.unit.freeColonist");
    private static final UnitType damagedArtilleryType
            = spec().getUnitType("model.unit.damagedArtillery");
    private static final UnitType galleonType
            = spec().getUnitType("model.unit.galleon");
    private static final UnitType indenturedServantType
            = spec().getUnitType("model.unit.indenturedServant");
    private static final UnitType indianConvertType
            = spec().getUnitType("model.unit.indianConvert");
    private static final UnitType jesuitMissionaryType
            = spec().getUnitType("model.unit.jesuitMissionary");
    private static final UnitType kingsRegularType
            = spec().getUnitType("model.unit.kingsRegular");
    private static final UnitType pettyCriminalType
            = spec().getUnitType("model.unit.pettyCriminal");
    private static final UnitType privateerType
            = spec().getUnitType("model.unit.privateer");
    private static final UnitType veteranType
            = spec().getUnitType("model.unit.veteranSoldier");

    class ServerUnitDoc extends ServerUnit {

        private Location location;
        private Unit template;
        private Player owner;
        private UnitType type;
        private Role role;
        private UnitState unitState;

        public ServerUnitDoc(Game game, String id) {
            super(game, id);
        }

        public ServerUnitDoc(Game game, Location location, Player owner, UnitType type) {
            super(game, location, owner, type);
            this.location = location;
            this.owner = owner;
            this.type = type;
        }

        public ServerUnitDoc(Game game, Location location, Unit template) {
            super(game, location, template);
            this.location = location;
            this.template = template;
        }

        public ServerUnitDoc(Game game, Location location, Player owner, UnitType type, Role role) {
            super(game, location, owner, type, role);
            this.location = location;
            this.owner = owner;
            this.type = type;
            this.role = role;
        }

        @Override
        protected void setStateUnchecked(UnitState unitState) {
            this.unitState = unitState;
            super.setStateUnchecked(unitState);
        }

        public String toDoc() {
            return String.join(" +\n",
                    "* Location:" + Optional.ofNullable(location).map(Object::toString).orElse(""),
                    "* Template:" + Optional.ofNullable(template).map(Object::toString).orElse(""),
                    "* Owner:" + Optional.ofNullable(owner).map(Object::toString).orElse(""),
                    "* Type:" + Optional.ofNullable(type).map(Object::toString).orElse(""),
                    "* Role:" + Optional.ofNullable(role).map(Object::toString).orElse(""));

        }

        public String toDoc(AsciidocFormatter formatter) {


            return String.join("\n",
                    "[%autowidth]",
                    formatter.tableWithHeader(Arrays.asList(
                            Arrays.asList("Location", "Type", "Role", "UnitState"),
                            Arrays.asList(Optional.ofNullable(location)
                                            .map(__ -> __.getTile().getType())
                                            .map(FreeColObject::getSuffix).orElse(""),
                                    Optional.ofNullable(type).map(FreeColObject::getSuffix).orElse(""),
                                    Optional.ofNullable(role).map(FreeColObject::getSuffix).orElse(""),
                                    Optional.ofNullable(unitState).map(UnitState::name).orElse(""))
                    )),
                    "Abilities:",
                    getSortedAbilities().stream()
                            .map(__ -> __.getSuffix() + (__.getValue() ? "" : ": " + __.getValue()))
                            .collect(Collectors.joining(", "))
            );
        }
    }

    private void writeColonist(Player dutch, ServerUnitDoc colonist) {

        write("[.unit]",
                "--",
                dutch.getNation().getSuffix() + " colonist"
                        + " on " + (colonist.getLocation().getTile().isExplored() ? "explored" : "not explored") + " " + colonist.getLocation().getTile().getType().getSuffix(),
                "",
                colonist.toDoc(getFormatter()),
                "",
                "[%autowidth]",
                getFormatter().tableWithHeader(Arrays.asList(
                        Arrays.asList("Work left", "Move left"),
                        Arrays.asList(colonist.getWorkLeft(), colonist.getMovesLeft())
                )),
                "--", "");
    }

    private void writeModifiers(Set<Modifier> modifiers) {
        for (Modifier modifier : modifiers) {
            write("* " + modifier.getSuffix() + " " + modifier.getModifierIndex() + " " + modifier.getType() + ": " + modifier.getValue() + "\n");
        }
    }

    private String docModifiers(AsciidocFormatter formatter, Set<Modifier> modifiers) {
        final String table = formatter.tableWithHeader(
                Stream.concat(
                        Stream.of(Arrays.asList("Modifier", "Source", "Index", "Type", "Value")),
                        modifiers.stream()
                                .map(modifier -> Arrays.asList(
                                        modifier.getSuffix(),
                                        modifier.getSource().getSuffix(),
                                        modifier.getModifierIndex(),
                                        modifier.getType(),
                                        modifier.getValue()))
                ).collect(Collectors.toList())
        );
        return table;
    }

    @Test
    public void testColonistAttackedByVeteran() throws Exception {

        write(".Initial test code",
                getFormatter().sourceCode(CodeExtractor.methodSource(
                        CombatTest.class.getMethod("testColonistAttackedByVeteran")
        )));
    }

    @Test
    public void colonist_attacked_by_veteran() throws Exception {
        write(String.join("\n",
                "++++",
                "<style>",
                ".unit {",
                "    padding: 1em;",
                "    margin: 1em;",
                "    background-color:#EEE;",
                "    box-shadow: 10px 5px 5px #AAA;",
                "    border: 1px solid #444;",
                "}",
                "</style>",
                "++++\n\n"));

        Game game = getStandardGame();
        Map map = getTestMap(plains);
        game.changeMap(map);

        CombatModel combatModel = game.getCombatModel();
        Player dutch = game.getPlayerByNationId("model.nation.dutch");
        Player french = game.getPlayerByNationId("model.nation.french");

        Tile tile1 = map.getTile(5, 8);
        tile1.setType(hills);
        tile1.setExplored(dutch, true);
        tile1.setExplored(french, true);

        Tile tile2 = map.getTile(4, 8);
        tile2.setExplored(dutch, true);
        tile2.setExplored(french, true);

        ServerUnitDoc colonist = new ServerUnitDoc(game, tile1, dutch, colonistType);
        colonist.setStateUnchecked(Unit.UnitState.FORTIFIED);

        ServerUnitDoc soldier = new ServerUnitDoc(game, tile2, french,
                veteranType, dragoonRole);
        soldier.setMovesLeft(1);

        writeColonist(dutch, colonist);
        writeColonist(french, soldier);

        Set<Modifier> offenceModifiers
                = combatModel.getOffensiveModifiers(soldier, colonist);

        write("", "",
                ".Offence modifiers:",
                docModifiers(getFormatter(), offenceModifiers));


        Set<Modifier> defenceModifiers
                = combatModel.getDefensiveModifiers(soldier, colonist);

        write("", "",
                ".Defence modifiers:",
                docModifiers(getFormatter(), defenceModifiers));
    }

//
//    public void testGalleonAttackedByPrivateer() throws Exception {
//        Game game = getStandardGame();
//        Map map = getTestMap(ocean);
//        game.changeMap(map);
//
//        CombatModel combatModel = game.getCombatModel();
//        Player dutch = game.getPlayerByNationId("model.nation.dutch");
//        Player french = game.getPlayerByNationId("model.nation.french");
//        FreeColTestCase.spec();
//        Tile tile1 = map.getTile(5, 8);
//        tile1.setExplored(dutch, true);
//        tile1.setExplored(french, true);
//        Tile tile2 = map.getTile(4, 8);
//        tile2.setExplored(dutch, true);
//        tile2.setExplored(french, true);
//
//        Unit galleon = new ServerUnit(game, tile1, dutch, galleonType);
//        Unit privateer = new ServerUnit(game, tile2, french, privateerType);
//
//        /**
//         * Only base modifiers should apply.
//         */
//        Set<Modifier> offenceModifiers = combatModel.getOffensiveModifiers(privateer, galleon);
//        assertEquals(2, offenceModifiers.size());
//        int n = 0;
//        for (Modifier m : offenceModifiers) {
//            if (m.getSource() == Specification.ATTACK_BONUS_SOURCE) n += 1;
//            if (m.getSource() == Specification.BASE_OFFENCE_SOURCE) n += 2;
//        }
//        assertEquals(n, 3);
//
//        Set<Modifier> defenceModifiers = combatModel.getDefensiveModifiers(privateer, galleon);
//        assertEquals(1, defenceModifiers.size());
//        assertEquals(Specification.BASE_DEFENCE_SOURCE,
//                     first(defenceModifiers).getSource());
//
//        /**
//         * Fortification should have no effect.
//         */
//        galleon.setStateUnchecked(Unit.UnitState.FORTIFIED);
//        defenceModifiers = combatModel.getDefensiveModifiers(privateer, galleon);
//        assertEquals(1, defenceModifiers.size());
//        assertEquals(Specification.BASE_DEFENCE_SOURCE,
//                     first(defenceModifiers).getSource());
//
//        /**
//         * Penalties due to cargo.
//         */
//        GoodsType lumberType = spec().getGoodsType("model.goods.lumber");
//        Goods goods1 = new Goods(game, null, lumberType, 50);
//        privateer.add(goods1);
//        offenceModifiers = combatModel.getOffensiveModifiers(privateer, galleon);
//        assertEquals(3, offenceModifiers.size());
//        n = 0;
//        for (Modifier m : offenceModifiers) {
//            if (m.getSource() == Specification.BASE_OFFENCE_SOURCE) n += 1;
//            if (m.getSource() == Specification.ATTACK_BONUS_SOURCE) n += 2;
//            if (m.getSource() == Specification.CARGO_PENALTY_SOURCE) n += 4;
//        }
//        assertEquals(7, n);
//
//        Goods goods2 = new Goods(game, null, lumberType, 150);
//        galleon.add(goods2);
//        assertEquals(2, galleon.getVisibleGoodsCount());
//        defenceModifiers = combatModel.getDefensiveModifiers(privateer, galleon);
//        n = 0;
//        assertEquals(2, defenceModifiers.size());
//        for (Modifier m : defenceModifiers) {
//            if (m.getSource() == Specification.BASE_DEFENCE_SOURCE) n += 1;
//            if (m.getSource() == Specification.CARGO_PENALTY_SOURCE) {
//                n += 2;
//                assertEquals(-25f, m.getValue());
//            }
//        }
//        assertEquals(3, n);
//
//        // Francis Drake
//        FoundingFather drake = spec().getFoundingFather("model.foundingFather.francisDrake");
//        Modifier drakeModifier = first(drake.getModifiers(Modifier.OFFENCE, privateerType));
//        assertNotNull(drakeModifier);
//        french.addFather(drake);
//        assertEquals(drakeModifier, first(french.getModifiers(Modifier.OFFENCE, privateerType)));
//
//        offenceModifiers = combatModel.getOffensiveModifiers(privateer, galleon);
//        assertEquals(4, offenceModifiers.size());
//        n = 0;
//        for (Modifier m : offenceModifiers) {
//            if (m.getSource() == Specification.BASE_OFFENCE_SOURCE) n += 1;
//            if (m.getSource() == drake) n += 2;
//            if (m.getSource() == Specification.ATTACK_BONUS_SOURCE) n += 4;
//            if (m.getSource() == Specification.CARGO_PENALTY_SOURCE) {
//                n += 8;
//                assertEquals(-12.5f, m.getValue());
//            }
//        }
//        assertEquals(15, n);
//
//        // Verify that the move is correctly interpreted
//        assertEquals("Wrong move type", MoveType.ATTACK_UNIT,
//                     privateer.getMoveType(tile1));
//    }
//
//    public void testDefendColonyWithUnarmedColonist() {
//        Game game = getGame();
//        Map map = getTestMap(true);
//        game.changeMap(map);
//
//        Colony colony = getStandardColony();
//        Player dutch = game.getPlayerByNationId("model.nation.dutch");
//        Player inca = game.getPlayerByNationId("model.nation.inca");
//
//        Tile tile2 = map.getTile(4, 8);
//        tile2.setExplored(dutch, true);
//
//        Unit colonist = first(colony.getUnitList());
//        Unit attacker = new ServerUnit(getGame(), tile2, inca, braveType,
//                                       armedBraveRole);
//
//        assertEquals(colonist, colony.getDefendingUnit(attacker));
//        assertEquals(colonist, colony.getTile().getDefendingUnit(attacker));
//
//        Unit defender = new ServerUnit(getGame(), colony.getTile(), dutch,
//                                       colonistType);
//        assertFalse("Colonist should not be defensive unit",
//                    defender.isDefensiveUnit());
//        assertEquals(defender, colony.getTile().getDefendingUnit(attacker));
//    }
//
//    public void testDefendColonyWithRevere() {
//        final Game game = getGame();
//        final Map map = getTestMap(true);
//        game.changeMap(map);
//
//        final SimpleCombatModel combatModel = new SimpleCombatModel();
//        Player dutch = game.getPlayerByNationId("model.nation.dutch");
//        Player inca = game.getPlayerByNationId("model.nation.inca");
//        Colony colony = getStandardColony();
//        Tile tile2 = map.getTile(4, 8);
//        tile2.setExplored(dutch, true);
//        Unit colonist = first(colony.getUnitList());
//        Unit attacker = new ServerUnit(getGame(), tile2, inca, braveType,
//                                       armedBraveRole);
//
//        // Colonist will defend
//        assertEquals(colonist, colony.getDefendingUnit(attacker));
//
//        // Set up for auto-equip
//        dutch.addFather(spec().getFoundingFather("model.foundingFather.paulRevere"));
//        for (AbstractGoods ag : soldierRole.getRequiredGoodsList()) {
//            colony.addGoods(ag);
//        }
//
//        // Colonist will auto arm
//        assertEquals(soldierRole, colonist.getAutomaticRole());
//
//        Set<Modifier> defenceModifiers = combatModel
//            .getDefensiveModifiers(attacker, colonist);
//        forEach(soldierRole.getModifiers(Modifier.DEFENCE),
//                m -> assertTrue(defenceModifiers.contains(m)));
//    }
//
//    public void testDefendSettlement() {
//        Game game = getStandardGame();
//        Map map = getTestMap();
//        game.changeMap(map);
//
//        SimpleCombatModel combatModel = new SimpleCombatModel();
//        Player dutch = game.getPlayerByNationId("model.nation.dutch");
//        Player inca = game.getPlayerByNationId("model.nation.inca");
//
//        Tile tile1 = map.getTile(5, 8);
//        tile1.setExplored(dutch, true);
//
//        Tile tile2 = map.getTile(4, 8);
//        tile2.setExplored(dutch, true);
//
//        IndianSettlementBuilder builder
//            = new IndianSettlementBuilder(game);
//        IndianSettlement is = builder.player(inca)
//            .settlementTile(tile1).skillToTeach(null).capital(true).build();
//
//        Unit defender = new ServerUnit(game, is, inca, braveType,
//                                       nativeDragoonRole);
//        Unit attacker = new ServerUnit(game, tile2, dutch, colonistType,
//                                       dragoonRole);
//        for (AbstractGoods ag : nativeDragoonRole.getRequiredGoodsList()) {
//            is.addGoods(ag);
//        }
//
//        Set<Modifier> defenceModifiers = combatModel
//            .getDefensiveModifiers(attacker, defender);
//        forEach(nativeDragoonRole.getModifiers(Modifier.DEFENCE),
//                m -> assertTrue(defenceModifiers.contains(m)));
//    }
//
//    public void testAttackIgnoresMovementPoints() throws Exception {
//        Game game = getStandardGame();
//        Map map = getTestMap(plains, true);
//        game.changeMap(map);
//
//        Player dutch = game.getPlayerByNationId("model.nation.dutch");
//        Player french = game.getPlayerByNationId("model.nation.french");
//        Tile tile1 = map.getTile(5, 8);
//        Tile tile2 = map.getTile(4, 8);
//        tile1.setType(hills);
//        assertEquals(hills, tile1.getType());
//
//        dutch.setStance(french, Stance.WAR);
//        french.setStance(dutch, Stance.WAR);
//
//        Unit colonist = new ServerUnit(game, tile1, dutch, colonistType);
//        colonist.setStateUnchecked(Unit.UnitState.FORTIFIED);
//        Unit soldier = new ServerUnit(game, tile2, french, veteranType,
//                                      dragoonRole);
//        soldier.setStateUnchecked(Unit.UnitState.FORTIFIED);
//
//        assertEquals(tile1, colonist.getLocation());
//        assertEquals(tile2, soldier.getLocation());
//
//        assertEquals(MoveType.ATTACK_UNIT,
//                     soldier.getMoveType(tile2, tile1, 9));
//        assertEquals(MoveType.ATTACK_UNIT,
//                     soldier.getMoveType(tile2, tile1, 1));
//        assertEquals(MoveType.MOVE_NO_MOVES,
//                     soldier.getMoveType(tile2, tile1, 0));
//    }
//
//    public void testSpanishAgainstNatives() throws Exception {
//        final Game game = getStandardGame();
//        Map map = getTestMap(plains, true);
//        game.changeMap(map);
//
//        final Player spanish = game.getPlayerByNationId("model.nation.spanish");
//        final Player dutch = game.getPlayerByNationId("model.nation.dutch");
//        Player tupi = game.getPlayerByNationId("model.nation.tupi");
//
//        SimpleCombatModel combatModel = new SimpleCombatModel();
//
//        Tile tile1 = map.getTile(5, 8);
//        Tile tile2 = map.getTile(4, 8);
//        tile1.setType(hills);
//        assertEquals(hills, tile1.getType());
//
//        spanish.setStance(tupi, Stance.WAR);
//        tupi.setStance(spanish, Stance.WAR);
//
//        Unit soldier = new ServerUnit(game, tile1, spanish, colonistType,
//                                      soldierRole);
//        Unit brave = new ServerUnit(game, tile2, tupi, braveType);
//
//        assertEquals(tile1, soldier.getLocation());
//        assertEquals(tile2, brave.getLocation());
//
//        // Spanish should have special bonus v natives...
//        Set<Modifier> offenceModifiers
//            = combatModel.getOffensiveModifiers(soldier, brave);
//        Modifier offenceAgainst = null;
//        for (Modifier modifier : offenceModifiers) {
//            if (Modifier.OFFENCE_AGAINST.equals(modifier.getId())) {
//                offenceAgainst = modifier;
//                break;
//            }
//        }
//        assertNotNull(offenceAgainst);
//        assertEquals(50, (int) offenceAgainst.getValue());
//
//        // but not against Europeans.
//        Tile tile3 = map.getTile(6, 8);
//        Unit dutchSoldier = new ServerUnit(game, tile3, dutch, colonistType,
//                                           soldierRole);
//        offenceAgainst = null;
//        for (Modifier modifier
//                 : combatModel.getOffensiveModifiers(soldier, dutchSoldier)) {
//            if (Modifier.OFFENCE_AGAINST.equals(modifier.getId())) {
//                offenceAgainst = modifier;
//                break;
//            }
//        }
//        assertNull(offenceAgainst);
//    }
//
//    public void testAttackShipWithLandUnit() {
//        Game game = getStandardGame();
//        Map map = getTestMap(plains, true);
//        game.changeMap(map);
//
//        Player spanish = game.getPlayerByNationId("model.nation.spanish");
//        Player tupi = game.getPlayerByNationId("model.nation.tupi");
//        SimpleCombatModel combatModel = new SimpleCombatModel();
//        Tile tile1 = map.getTile(5, 8);
//        Tile tile2 = map.getTile(4, 8);
//
//        tile1.setType(hills);
//        assertEquals(hills, tile1.getType());
//        tile2.setType(ocean);
//        assertEquals(ocean, tile2.getType());
//
//        spanish.setStance(tupi, Stance.WAR);
//        tupi.setStance(spanish, Stance.WAR);
//
//        Unit galleon = new ServerUnit(game, tile2, spanish, galleonType);
//        Unit brave = new ServerUnit(game, tile1, tupi, braveType);
//
//        assertEquals(tile1, brave.getLocation());
//        assertEquals(tile2, galleon.getLocation());
//
//        assertEquals(MoveType.MOVE_NO_ACCESS_LAND,
//                     galleon.getMoveType(tile2, tile1, 3));
//
//        assertEquals(MoveType.MOVE_NO_ACCESS_EMBARK,
//                     brave.getMoveType(tile1, tile2, 3));
//
//    }
//
//    public void testRegulars() {
//        Game game = ServerTestHelper.startServerGame(getTestMap(plains, true));
//        InGameController igc = ServerTestHelper.getInGameController();
//
//        ServerPlayer french = getServerPlayer(game, "model.nation.french");
//        french.addAbility(new Ability(Ability.INDEPENDENCE_DECLARED));
//        ServerPlayer refPlayer = igc.createREFPlayer(french);
//
//        SimpleCombatModel combatModel = new SimpleCombatModel();
//
//        Map map = game.getMap();
//        Tile tile1 = map.getTile(5, 8);
//        Tile tile2 = map.getTile(4, 8);
//
//        Unit regular = new ServerUnit(game, tile2, refPlayer, kingsRegularType,
//                                      cavalryRole);
//        Unit colonial = new ServerUnit(game, tile1, french, colonialRegularType,
//                                       dragoonRole);
//
//        MockPseudoRandom loseRandom = new MockPseudoRandom();
//        List<Integer> i1 = new ArrayList<>();
//        i1.add((int)(Integer.MAX_VALUE * 0.85));
//        loseRandom.setNextNumbers(i1, true);
//        MockPseudoRandom winRandom = new MockPseudoRandom();
//        List<Integer> i2 = new ArrayList<>();
//        i2.add((int)(Integer.MAX_VALUE * 0.20));
//        winRandom.setNextNumbers(i2, true);
//
//        // colonist + regular + cavalry * attack bonus
//        double offence = (0 + 4 + 3) * 1.5;
//        assertEquals(offence, combatModel.getOffencePower(regular, colonial));
//        // colonist + colonial + dragoon
//        double defence = 0 + 3 + 3;
//        assertEquals(defence, combatModel.getDefencePower(regular, colonial));
//
//        List<CombatResult> crs
//            = combatModel.generateAttackResult(loseRandom, regular, colonial);
//        checkCombat("Regular v Colonial", crs,
//            CombatResult.LOSE, CombatResult.LOSE_EQUIP);
//        refPlayer.csCombat(regular, colonial, crs, loseRandom, new ChangeSet());
//        assertEquals(infantryRole, regular.getRole());
//
//        // (colonist + regular + infantry) * attack bonus
//        offence = (0 + 4 + 2) * 1.5;
//        assertEquals(offence, combatModel.getOffencePower(regular, colonial));
//
//        // slaughter King's Regular
//        crs = combatModel.generateAttackResult(winRandom, colonial, regular);
//        checkCombat("Regular should be slaughtered upon losing all equipment",
//            crs, CombatResult.WIN, CombatResult.SLAUGHTER_UNIT);
//
//        regular = new ServerUnit(game, tile2, french, kingsRegularType,
//                                 cavalryRole);
//
//        crs = combatModel.generateAttackResult(winRandom, regular, colonial);
//        checkCombat("Regular v Colonial (2)", crs,
//            CombatResult.WIN, CombatResult.LOSE_EQUIP);
//        refPlayer.csCombat(regular, colonial, crs, winRandom, new ChangeSet());
//        assertEquals(soldierRole, colonial.getRole());
//
//        crs = combatModel.generateAttackResult(winRandom, regular, colonial);
//        checkCombat("Regular v Colonial (3)", crs,
//            CombatResult.WIN, CombatResult.LOSE_EQUIP, CombatResult.DEMOTE_UNIT);
//        refPlayer.csCombat(regular, colonial, crs, winRandom, new ChangeSet());
//        assertFalse(colonial.isArmed());
//        assertEquals(veteranType, colonial.getType());
//        assertEquals(spec().getDefaultRole(), colonial.getRole());
//
//        crs = combatModel.generateAttackResult(winRandom, regular, colonial);
//        checkCombat("Regular v Colonial (4)", crs,
//            CombatResult.WIN, CombatResult.CAPTURE_UNIT);
//        refPlayer.csCombat(regular, colonial, crs, winRandom, new ChangeSet());
//    }
//
//    public void testCaptureConvert() {
//        Map map = getTestMap(plains, true);
//        Game game = ServerTestHelper.startServerGame(map);
//        CombatModel combatModel = game.getCombatModel();
//        InGameController igc = ServerTestHelper.getInGameController();
//
//        ServerPlayer dutch = getServerPlayer(game, "model.nation.dutch");
//        ServerPlayer inca = getServerPlayer(game, "model.nation.inca");
//        dutch.setStance(inca, Stance.WAR);
//        inca.setStance(dutch, Stance.WAR);
//
//        Tile tile1 = map.getTile(5, 8);
//        tile1.setExplored(dutch, true);
//        Tile tile2 = map.getTile(4, 8);
//        tile2.setExplored(dutch, true);
//        Unit missionary = new ServerUnit(game, null, dutch,
//                                         jesuitMissionaryType, missionaryRole);
//        IndianSettlementBuilder builder
//            = new IndianSettlementBuilder(game);
//        IndianSettlement is = builder.player(inca)
//            .settlementTile(tile1).skillToTeach(null).capital(true)
//            .initialBravesInCamp(8).missionary(missionary).build();
//
//        spec().setInteger(GameOptions.NATIVE_CONVERT_PROBABILITY, 100);
//
//        Unit soldier = new ServerUnit(game, tile2, dutch, veteranType,
//                                      soldierRole);
//        Unit defender = is.getDefendingUnit(soldier);
//        assertNotNull(defender);
//        assertTrue(defender.getOwner().isIndian());
//
//        MockPseudoRandom random = new MockPseudoRandom();
//        List<Integer> il = new ArrayList<>();
//        il.add(0);
//        random.setNextNumbers(il, true);
//
//        List<CombatResult> crs = combatModel.generateAttackResult(random,
//            soldier, defender);
//        checkCombat("Capture convert", crs,
//                    CombatResult.WIN, CombatResult.SLAUGHTER_UNIT,
//                    CombatResult.CAPTURE_CONVERT);
//        assertEquals("One unit on tile", 1, tile2.getUnitList().size());
//        dutch.csCombat(soldier, defender, crs, new Random(),
//                       new ChangeSet());
//        assertEquals("Two units on tile", 2, tile2.getUnitList().size());
//        assertEquals("Convert on tile", indianConvertType,
//                     tile2.getUnitList().get(1).getType());
//    }
}
