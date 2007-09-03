package net.sf.freecol.server.control;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NoRouteToServerException;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;

/**
 * Handles the network messages that arrives before the game starts.
 * 
 * @see PreGameController
 */
public final class PreGameInputHandler extends InputHandler {
    private static Logger logger = Logger.getLogger(PreGameInputHandler.class.getName());

    public static final String COPYRIGHT = "Copyright (C) 2003-2005 The FreeCol Team";

    public static final String LICENSE = "http://www.gnu.org/licenses/gpl.html";

    public static final String REVISION = "$Revision$";


    /**
     * The constructor to use.
     * 
     * @param freeColServer The main server object.
     */
    public PreGameInputHandler(FreeColServer freeColServer) {
        super(freeColServer);
        // TODO: move and simplify methods later, for now just delegate
        register("updateGameOptions", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return updateGameOptions(connection, element);
            }
        });
        register("updateMapGeneratorOptions", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return updateMapGeneratorOptions(connection, element);
            }
        });
        register("ready", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return ready(connection, element);
            }
        });
        register("setNation", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return nation(connection, element);
            }
        });
        register("setColor", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return color(connection, element);
            }
        });
        register("requestLaunch", new NetworkRequestHandler() {
            public Element handle(Connection connection, Element element) {
                return requestLaunch(connection, element);
            }
        });
    }

    /**
     * Handles a &quot;updateGameOptions&quot;-message from a client.
     * 
     * @param connection The connection the message came from.
     * @param element The element containing the request.
     * @return The reply.
     */
    private Element updateGameOptions(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (!player.isAdmin()) {
            throw new IllegalStateException();
        }
        getFreeColServer().getGame().getGameOptions().readFromXMLElement((Element) element.getChildNodes().item(0));
        Element updateGameOptionsElement = Message.createNewRootElement("updateGameOptions");
        updateGameOptionsElement.appendChild(getFreeColServer().getGame().getGameOptions().toXMLElement(
                updateGameOptionsElement.getOwnerDocument()));
        getFreeColServer().getServer().sendToAll(updateGameOptionsElement, connection);
        return null;
    }

    /**
     * Handles a "updateMapGeneratorOptions"-message from a client.
     * 
     * @param connection The connection the message came from.
     * @param element The element containing the request.
     * @return The reply.
     */
    private Element updateMapGeneratorOptions(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (!player.isAdmin()) {
            throw new IllegalStateException();
        }
        getFreeColServer().getMapGenerator().getMapGeneratorOptions().readFromXMLElement(
                (Element) element.getChildNodes().item(0));
        Element umge = Message.createNewRootElement("updateMapGeneratorOptions");
        umge.appendChild(getFreeColServer().getMapGenerator().getMapGeneratorOptions().toXMLElement(
                umge.getOwnerDocument()));
        getFreeColServer().getServer().sendToAll(umge, connection);
        return null;
    }

    /**
     * Handles a "ready"-message from a client.
     * 
     * @param connection The connection the message came from.
     * @param element The element containing the request.
     */
    private Element ready(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            boolean ready = (new Boolean(element.getAttribute("value"))).booleanValue();
            player.setReady(ready);
            Element playerReady = Message.createNewRootElement("playerReady");
            playerReady.setAttribute("player", player.getID());
            playerReady.setAttribute("value", Boolean.toString(ready));
            getFreeColServer().getServer().sendToAll(playerReady, player.getConnection());
        } else {
            logger.warning("Ready from unknown connection.");
        }
        return null;
    }

    /**
     * Handles a "nation"-message from a client.
     * 
     * @param connection The connection the message came from.
     * @param element The element containing the request.
     */
    private Element nation(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            NationType nation = FreeCol.getSpecification().getNationType(element.getAttribute("value"));
            player.setNation(nation);
            Element updateNation = Message.createNewRootElement("updateNation");
            updateNation.setAttribute("player", player.getID());
            updateNation.setAttribute("value", nation.getID());
            getFreeColServer().getServer().sendToAll(updateNation, player.getConnection());
        } else {
            logger.warning("Nation from unknown connection.");
        }
        return null;
    }

    /**
     * Handles a "color"-message from a client.
     * 
     * @param connection The connection the message came from.
     * @param element The element containing the request.
     */
    private Element color(Connection connection, Element element) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        if (player != null) {
            String color = element.getAttribute("value");
            player.setColor(color);
            Element updateColor = Message.createNewRootElement("updateColor");
            updateColor.setAttribute("player", player.getID());
            updateColor.setAttribute("value", color);
            getFreeColServer().getServer().sendToAll(updateColor, player.getConnection());
        } else {
            logger.warning("Color from unknown connection.");
        }
        return null;
    }

    /**
     * Handles a "requestLaunch"-message from a client.
     * 
     * @param connection The connection the message came from.
     * @param element The element containing the request.
     */
    private Element requestLaunch(Connection connection, Element element) {
        FreeColServer freeColServer = getFreeColServer();
        ServerPlayer launchingPlayer = freeColServer.getPlayer(connection);
        // Check if launching player is an admin.
        if (!launchingPlayer.isAdmin()) {
            Element reply = Message.createNewRootElement("error");
            reply.setAttribute("message", "Sorry, only the server admin can launch the game.");
            reply.setAttribute("messageID", "server.onlyAdminCanLaunch");
            return reply;
        }
        // Check that no two players have the same color or nation
        Iterator<Player> playerIterator = freeColServer.getGame().getPlayerIterator();
        LinkedList<NationType> nations = new LinkedList<NationType>();
        LinkedList<Color> colors = new LinkedList<Color>();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            final NationType nation = player.getNation();
            final Color color = player.getColor();
            // Check the nation.
            for (int i = 0; i < nations.size(); i++) {
                if (nations.get(i) == nation) {
                    Element reply = Message.createNewRootElement("error");
                    reply
                            .setAttribute("message",
                                    "All players need to pick a unique nation before the game can start.");
                    reply.setAttribute("messageID", "server.invalidPlayerNations");
                    return reply;
                }
            }
            nations.add(nation);
            // Check the color.
            for (int i = 0; i < colors.size(); i++) {
                if (colors.get(i).equals(color)) {
                    Element reply = Message.createNewRootElement("error");
                    reply.setAttribute("message", "All players need to pick a unique color before the game can start.");
                    reply.setAttribute("messageID", "server.invalidPlayerColors");
                    return reply;
                }
            }
            colors.add(color);
        }
        // Check if all players are ready.
        if (!freeColServer.getGame().isAllPlayersReadyToLaunch()) {
            Element reply = Message.createNewRootElement("error");
            reply.setAttribute("message", "Not all players are ready to begin the game!");
            reply.setAttribute("messageID", "server.notAllReady");
            return reply;
        }
        ((PreGameController) freeColServer.getController()).startGame();
        return null;
    }

    /**
     * Handles a &quot;logout&quot;-message.
     * 
     * @param connection The <code>Connection</code> the message was received
     *            on.
     * @param logoutElement The element (root element in a DOM-parsed XML tree)
     *            that holds all the information.
     * @return The reply.
     */
    protected Element logout(Connection connection, Element logoutElement) {
        ServerPlayer player = getFreeColServer().getPlayer(connection);
        logger.info("Logout by: " + connection + ((player != null) ? " (" + player.getName() + ") " : ""));
        Element logoutMessage = Message.createNewRootElement("logout");
        logoutMessage.setAttribute("reason", "User has logged out.");
        logoutMessage.setAttribute("player", player.getID());
        player.setConnected(false);
        getFreeColServer().getGame().removePlayer(player);
        getFreeColServer().getServer().sendToAll(logoutMessage, connection);
        try {
            getFreeColServer().updateMetaServer();
        } catch (NoRouteToServerException e) {}
        
        return null;
    }
}
