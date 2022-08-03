package net.sf.freecol.client;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.io.FreeColSavegameFile;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.networking.ServerAPI;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.FreeColServer.ServerState;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;

public interface FreeColClientServer {
    ServerAPI askServer();

    boolean isLoggedIn();

    void logout(boolean b);

    boolean unblockServer(int serverPort);

    /**
     * Start a server.
     *
     * @param publicServer If true, add to the meta-server.
     * @param singlePlayer True if this is a single player game.
     * @param spec The {@code Specification} to use in this game.
     * @param address The address to use for the public socket.
     * @param port The TCP port to use for the public socket. If null, try
     *      ports until
     * @return A new {@code FreeColServer} or null on error.
     */
    public FreeColServer startServer(boolean publicServer,
                                     boolean singlePlayer, Specification spec,
                                     InetAddress address,
                                     int port);

    public FreeColServer startServer(boolean publicServer,
                                     boolean singlePlayer,
                                     File saveFile, InetAddress address,
                                     int port, String name);

    void setFreeColServer(FreeColServer fcs);

    ServerState getServerState();

    void stopServer();

    void setServerState(ServerState state);
}
