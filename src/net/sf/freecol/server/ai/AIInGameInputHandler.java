/**
 *  Copyright (C) 2002-2016   The FreeCol Team
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

package net.sf.freecol.server.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.DiplomaticTrade;
import net.sf.freecol.common.model.DiplomaticTrade.TradeStatus;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.Monarch.MonarchAction;
import net.sf.freecol.common.model.NationSummary;
import net.sf.freecol.common.model.NativeTrade;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.AddPlayerMessage;
import net.sf.freecol.common.networking.AssignTradeRouteMessage;
import net.sf.freecol.common.networking.ChatMessage;
import net.sf.freecol.common.networking.ChooseFoundingFatherMessage;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.DeleteTradeRouteMessage;
import net.sf.freecol.common.networking.DiplomacyMessage;
import net.sf.freecol.common.networking.DisconnectMessage;
import net.sf.freecol.common.networking.ErrorMessage;
import net.sf.freecol.common.networking.FirstContactMessage;
import net.sf.freecol.common.networking.FountainOfYouthMessage;
import net.sf.freecol.common.networking.GameEndedMessage;
import net.sf.freecol.common.networking.IndianDemandMessage;
import net.sf.freecol.common.networking.LogoutMessage;
import net.sf.freecol.common.networking.LootCargoMessage;
import net.sf.freecol.common.networking.MessageHandler;
import net.sf.freecol.common.networking.NativeGiftMessage;
import net.sf.freecol.common.networking.NativeTradeMessage;
import net.sf.freecol.common.networking.NewTurnMessage;
import net.sf.freecol.common.networking.MonarchActionMessage;
import net.sf.freecol.common.networking.MultipleMessage;
import net.sf.freecol.common.networking.NationSummaryMessage;
import net.sf.freecol.common.networking.NewLandNameMessage;
import net.sf.freecol.common.networking.NewRegionNameMessage;
import net.sf.freecol.common.networking.ScoutSpeakToChiefMessage;
import net.sf.freecol.common.networking.SetAIMessage;
import net.sf.freecol.common.networking.SetCurrentPlayerMessage;
import net.sf.freecol.common.networking.SetDeadMessage;
import net.sf.freecol.common.networking.SetStanceMessage;
import net.sf.freecol.common.networking.TrivialMessage;
import net.sf.freecol.common.networking.UpdateMessage;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;
import static net.sf.freecol.common.util.CollectionUtils.*;

import org.w3c.dom.Element;


/**
 * Handles the network messages that arrives while in the game.
 */
public final class AIInGameInputHandler implements MessageHandler {

    private static final Logger logger = Logger.getLogger(AIInGameInputHandler.class.getName());

    /** The player for whom I work. */
    private final ServerPlayer serverPlayer;

    /** The server. */
    private final FreeColServer freeColServer;

    /** The main AI object. */
    private final AIMain aiMain;


    /**
     * The constructor to use.
     *
     * @param freeColServer The main server.
     * @param me The AI {@code ServerPlayer} that is being
     *     managed by this AIInGameInputHandler.
     * @param aiMain The main AI-object.
     */
    public AIInGameInputHandler(FreeColServer freeColServer, ServerPlayer me,
                                AIMain aiMain) {
        if (freeColServer == null) {
            throw new NullPointerException("freeColServer == null");
        } else if (me == null) {
            throw new NullPointerException("me == null");
        } else if (!me.isAI()) {
            throw new RuntimeException("Applying AIInGameInputHandler to a non-AI player!");
        } else if (aiMain == null) {
            throw new NullPointerException("aiMain == null");
        }

        this.freeColServer = freeColServer;
        this.serverPlayer = me;
        this.aiMain = aiMain;
    }


    /**
     * Get the AI player using this {@code AIInGameInputHandler}.
     *
     * @return The {@code AIPlayer}.
     */
    private AIPlayer getAIPlayer() {
        return aiMain.getAIPlayer(serverPlayer);
    }

    /**
     * Gets the AI unit corresponding to a given unit, if any.
     *
     * @param unit The {@code Unit} to look up.
     * @return The corresponding AI unit or null if not found.
     */
    private AIUnit getAIUnit(Unit unit) {
        return aiMain.getAIUnit(unit);
    }


    // Implement MessageHandler

    /**
     * Deals with incoming messages that have just been received.
     *
     * @param connection The {@code Connection} the message was
     *     received on.
     * @param element The root element of the message.
     * @return The reply.
     */
    @Override
    public synchronized Element handle(Connection connection, Element element) {
        if (element == null) return null;
        final String tag = element.getTagName();
        Element reply = null;
        try {
            switch (tag) {
            case TrivialMessage.RECONNECT_TAG:
                logger.warning("Reconnect on illegal operation, refer to any previous error message."); break;
            case ChooseFoundingFatherMessage.TAG:
                reply = chooseFoundingFather(connection, element); break;
            case "diplomacy":
                reply = diplomacy(connection, element); break;
            case FirstContactMessage.TAG:
                reply = firstContact(connection, element); break;
            case FountainOfYouthMessage.TAG:
                reply = fountainOfYouth(connection, element); break;
            case IndianDemandMessage.TAG:
                reply = indianDemand(connection, element); break;
            case "lootCargo":
                reply = lootCargo(connection, element); break;
            case "monarchAction":
                reply = monarchAction(connection, element); break;
            case MultipleMessage.TAG:
                reply = multiple(connection, element); break;
            case NationSummaryMessage.TAG:
                reply = nationSummary(connection, element); break;
            case NativeTradeMessage.TAG:
                reply = nativeTrade(connection, element); break;
            case NewLandNameMessage.TAG:
                reply = newLandName(connection, element); break;
            case NewRegionNameMessage.TAG:
                reply = newRegionName(connection, element); break;
            case SetAIMessage.TAG:
                reply = setAI(connection, element); break;
            case SetCurrentPlayerMessage.TAG:
                reply = setCurrentPlayer(connection, element); break;
                
            // Since we're the server, we can see everything.
            // Therefore most of these messages are useless.  This
            // may change one day.
            case AddPlayerMessage.TAG:
            case "animateMove":
            case "animateAttack":
            case AssignTradeRouteMessage.TAG:
            case ChatMessage.TAG:
            case TrivialMessage.CLOSE_MENUS_TAG:
            case DeleteTradeRouteMessage.TAG:
            case DisconnectMessage.TAG:                
            case ErrorMessage.TAG:
            case "featureChange":
            case GameEndedMessage.TAG:
            case LogoutMessage.TAG:
            case NativeGiftMessage.TAG:
            case NewTurnMessage.TAG:
            case "newTradeRoute":
            case "remove":
            case "removeGoods":
            case ScoutSpeakToChiefMessage.TAG:
            case SetDeadMessage.TAG:
            case SetStanceMessage.TAG:
            case TrivialMessage.START_GAME_TAG:
            case UpdateMessage.TAG:
                break;
            default:
                logger.warning("Unknown message type: " + tag);
                break;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "AI input handler for " + serverPlayer
                + " caught error handling " + tag, e);
        }
        return reply;
    }

    // Individual message handlers

    /**
     * Handles a "chooseFoundingFather"-message.
     * Only meaningful for AIPlayer types that implement selectFoundingFather.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element chooseFoundingFather(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = aiMain.getGame();
        final AIPlayer aiPlayer = getAIPlayer();

        ChooseFoundingFatherMessage message
            = new ChooseFoundingFatherMessage(game, element);
        FoundingFather ff = aiPlayer.selectFoundingFather(message.getFathers(game));
        logger.finest(aiPlayer.getId() + " chose founding father: " + ff);
        if (ff != null) message.setFather(ff);
        return message.toXMLElement();
    }

    /**
     * Handles an "diplomacy"-message.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element diplomacy(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = freeColServer.getGame();
        final DiplomacyMessage message = new DiplomacyMessage(game, element);
        final DiplomaticTrade agreement = message.getAgreement();

        // Shortcut if no negotiation is required
        if (agreement.getStatus() != DiplomaticTrade.TradeStatus.PROPOSE_TRADE)
            return null;
        
        StringBuilder sb = new StringBuilder(256);
        sb.append("AI Diplomacy: ").append(agreement);
        TradeStatus status = getAIPlayer().acceptDiplomaticTrade(agreement);
        agreement.setStatus(status);
        sb.append(" -> ").append(agreement);
        logger.fine(sb.toString());

        return new DiplomacyMessage(message.getOurFCGO(game),
                                    message.getOtherFCGO(game), agreement)
            .toXMLElement();
    }

    /**
     * Replies to a first contact offer.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element firstContact(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = freeColServer.getGame();

        return new FirstContactMessage(game, element).setResult(true)
            .toXMLElement();
    }

    /**
     * Replies to fountain of youth offer.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return Null.
     */
    private Element fountainOfYouth(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = freeColServer.getGame();
        final FountainOfYouthMessage message
            = new FountainOfYouthMessage(game, element);
        final AIPlayer aiPlayer = getAIPlayer();

        int n = message.getMigrants();
        for (int i = 0; i < n; i++) AIMessage.askEmigrate(aiPlayer, 0);
        return null;
    }

    /**
     * Handles an "indianDemand"-message.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return The original message with the acceptance state set if querying
     *     the colony player (result == null), or null if reporting the final
     *     result to the native player (result != null).
     */
    private Element indianDemand(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = aiMain.getGame();
        final AIPlayer aiPlayer = getAIPlayer();

        IndianDemandMessage message = new IndianDemandMessage(game, element);
        Unit unit = message.getUnit(game);
        Colony colony = message.getColony(game);
        GoodsType type = message.getType(game);
        int amount = message.getAmount();
        Boolean result = message.getResult();
        result = aiPlayer.indianDemand(unit, colony, type, amount, result);
        if (result == null) return null;
        message.setResult(result);
        logger.finest("AI handling native demand by " + unit
            + " at " + colony.getName() + " result: " + result);
        return message.toXMLElement();
    }

    /**
     * Replies to loot cargo offer.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return Null.
     */
    private Element lootCargo(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = freeColServer.getGame();
        final Market market = serverPlayer.getMarket();

        LootCargoMessage message = new LootCargoMessage(game, element);
        Unit unit = message.getUnit(game);
        List<Goods> goods = sort(message.getGoods(),
                                 market.getSalePriceComparator());
        List<Goods> loot = new ArrayList<>();
        int space = unit.getSpaceLeft();
        while (!goods.isEmpty()) {
            Goods g = goods.remove(0);
            if (g.getSpaceTaken() > space) continue; // Approximate
            loot.add(g);
            space -= g.getSpaceTaken();
        }
        AIMessage.askLoot(getAIUnit(unit), message.getDefenderId(), loot);
        return null;
    }

    /**
     * Handles a "monarchAction"-message.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element monarchAction(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = aiMain.getGame();

        MonarchActionMessage message = new MonarchActionMessage(game, element);
        MonarchAction action = message.getAction();
        boolean accept;
        switch (action) {
        case RAISE_TAX_WAR: case RAISE_TAX_ACT:
            accept = getAIPlayer().acceptTax(message.getTax());
            message.setResult(accept);
            logger.finest("AI player monarch action " + action
                          + " = " + accept);
            break;

        case MONARCH_MERCENARIES: case HESSIAN_MERCENARIES:
            accept = getAIPlayer().acceptMercenaries();
            message.setResult(accept);
            logger.finest("AI player monarch action " + action
                          + " = " + accept);
            break;

        default:
            logger.finest("AI player ignoring monarch action " + action);
            return null;
        }
        return message.toXMLElement();
    }

    /**
     * Handle all the children of this element.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    public Element multiple(Connection connection, Element element) {
        return new MultipleMessage(element).applyHandler(this, connection);
    }

    /**
     * Handle an incoming nation summary.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return Null.
     */
    private Element nationSummary(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = aiMain.getGame();
        final AIPlayer aiPlayer = getAIPlayer();

        NationSummaryMessage message = new NationSummaryMessage(game, element);
        Player player = aiPlayer.getPlayer();
        Player other = message.getPlayer(game);
        NationSummary ns = message.getNationSummary();
        player.putNationSummary(other, ns);
        logger.info("Updated nation summary of " + other.getSuffix()
            + " for AI " + player.getSuffix());
        return null;
    }

    /**
     * Handle a native trade message.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element nativeTrade(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final NativeTradeMessage message
            = new NativeTradeMessage(aiMain.getGame(), element);
        final NativeTrade nt = message.getNativeTrade();
        NativeTrade.NativeTradeAction action = message.getAction();

        action = getAIPlayer().handleTrade(action, nt);
        return new NativeTradeMessage(action, nt).toXMLElement();
    }

    /**
     * Replies to offer to name the new land.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element newLandName(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        return new NewLandNameMessage(freeColServer.getGame(), element)
            .toXMLElement();
    }

    /**
     * Replies to offer to name a new region name.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return An {@code Element} containing the response/s.
     */
    private Element newRegionName(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        return new NewRegionNameMessage(freeColServer.getGame(), element)
            .toXMLElement();
    }

    /**
     * Handle a "setAI"-message.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return Null.
     */
    private Element setAI(
        @SuppressWarnings("unused") Connection connection,
        Element element) {
        final Game game = freeColServer.getGame();
        final SetAIMessage message = new SetAIMessage(game, element);
        
        final Player p = message.getPlayer(game);
        final boolean ai = message.getAI();
        if (p != null) p.setAI(ai);

        return null;
    }

    /**
     * Handles a "setCurrentPlayer"-message.
     *
     * @param connection The {@code Connection} the element arrived on.
     * @param element The {@code Element} to process.
     * @return Null.
     */
    private Element setCurrentPlayer(
        @SuppressWarnings("unused") Connection connection,
        final Element element) {
        final Game game = freeColServer.getGame();
        final SetCurrentPlayerMessage message
            = new SetCurrentPlayerMessage(game, element);

        final Player currentPlayer = message.getPlayer(game);
        if (currentPlayer != null
            && serverPlayer.getId().equals(currentPlayer.getId())) {
            logger.finest("Starting new Thread for " + serverPlayer.getName());
            String nam = FreeCol.SERVER_THREAD + "AIPlayer ("
                + serverPlayer.getName() + ")";
            new Thread(nam) {
                @Override
                public void run() {
                    try {
                        getAIPlayer().startWorking();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "AI player failed while working!", e);
                    }
                    AIMessage.askEndTurn(getAIPlayer());
                }
            }.start();
        }
        return null;
    }
}
