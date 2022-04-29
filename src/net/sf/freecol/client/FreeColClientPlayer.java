package net.sf.freecol.client;

import net.sf.freecol.common.model.Player;

import java.util.List;

public interface FreeColClientPlayer {
    Player getMyPlayer();

    void setMyPlayer(Player player);

    boolean isAdmin();

    boolean currentPlayerIsMyPlayer();

    boolean getSinglePlayer();

    List<String> getVacantPlayerNames();

    void setVacantPlayerNames(List<String> vacant);
}
