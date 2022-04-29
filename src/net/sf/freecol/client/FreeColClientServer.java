package net.sf.freecol.client;

import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.networking.ServerAPI;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.FreeColServer.ServerState;

import java.io.File;

public interface FreeColClientServer {
    ServerAPI askServer();

    boolean isLoggedIn();

    void logout(boolean b);

    boolean unblockServer(int serverPort);

    FreeColServer startServer(boolean b, boolean b1, Specification spec, int i);

    FreeColServer startServer(boolean publicServer, boolean singlePlayer, File file, int port, String serverName);

    void setFreeColServer(FreeColServer fcs);

    ServerState getServerState();

    void stopServer();

    void setServerState(ServerState state);
}
