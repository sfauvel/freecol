package net.sf.freecol.client;

import net.sf.freecol.client.control.*;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.action.ActionManager;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.networking.ServerAPI;

import net.sf.freecol.server.FreeColServer;

import java.io.File;

public interface FreeColClient extends
        FreeColClientPlayer,
        FreeColClientOptions,
        FreeColClientGui,
        FreeColClientGame,
        FreeColClientServer {

    ConnectController getConnectController();

    PreGameController getPreGameController();

    InGameController getInGameController();

    MapEditorController getMapEditorController();

    FreeColServer getFreeColServer();

    ActionManager getActionManager();

    boolean isMapEditor();

    void changeClientState(boolean b);

    void restoreGUI(Player player);

    void addSpecificationActions(Specification specification);

    void askToQuit();

    void setMapEditor(boolean b);

    void login(boolean b, Game game, Player player, boolean single);

    void setSinglePlayer(boolean b);

    SoundController getSoundController();

    void updateActions();
}
