package net.sf.freecol.client;

import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.common.model.Player;

public interface FreeColClientGui {
    GUI getGUI();

    int getAnimationSpeed(Player owner);
}
