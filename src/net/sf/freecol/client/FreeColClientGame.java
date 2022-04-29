package net.sf.freecol.client;

import net.sf.freecol.common.model.Game;
import net.sf.freecol.server.model.ServerGame;

public interface FreeColClientGame {
    Game getGame();

    boolean isInGame();

    void quit();

    boolean canSaveCurrentGame();

    boolean isReadyToStart();

    void skipTurns(int debugRunTurns);

    void setGame(Game game);

    void retire();
}
