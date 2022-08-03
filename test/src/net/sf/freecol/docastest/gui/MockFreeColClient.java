package net.sf.freecol.docastest.gui;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.control.*;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.action.ActionManager;
import net.sf.freecol.common.model.*;
import net.sf.freecol.common.networking.ServerAPI;
import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.resources.ImageCache;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.util.test.FreeColTestCase;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

class MockFreeColClient implements FreeColClient {
    ClientOptions options = new ClientOptions();

    float scaleFactor = ImageLibrary.NORMAL_SCALE;
    ImageCache imageCache = new ImageCache();
    ImageLibrary scaledImageLibrary = new ImageLibrary(scaleFactor, imageCache);
    ImageLibrary fixedImageLibrary = new ImageLibrary(scaleFactor, imageCache);

    Game game = FreeColTestCase.getStandardGame();
    Player myPlayer = new Player(game, "me");
    {
        myPlayer.setNationType(new EuropeanNationType("NationType_0", game.getSpecification()));
        final Nation newNation = new Nation("model.nation.spanishREF", game.getSpecification());
        myPlayer.setNation(newNation);
        myPlayer.changePlayerType(Player.PlayerType.ROYAL);
      //  myPlayer.setSpecification(game.getSpecification());
    }

    {
        game.changeMap(new Map(game, 10, 10));
    }

    {
        final IntegerOption integerOption = new IntegerOption(new Specification());
        integerOption.setId(ClientOptions.MAX_NUMBER_OF_GOODS_IMAGES);
        options.add(integerOption);
    }

    {
        final IntegerOption integerOption = new IntegerOption(new Specification());
        integerOption.setId(ClientOptions.MIN_NUMBER_FOR_DISPLAYING_GOODS_COUNT);
        options.add(integerOption);
    }

    {
        final IntegerOption integerOption = new IntegerOption(new Specification());
        integerOption.setId(ClientOptions.MIN_NUMBER_FOR_DISPLAYING_GOODS);
        options.add(integerOption);
    }
    {
        final IntegerOption integerOption = new IntegerOption(new Specification());
        integerOption.setId(ClientOptions.DISPLAY_COLONY_LABELS);
        options.add(integerOption);
    }
    @Override
    public Player getMyPlayer() {
        return myPlayer;
    }

    @Override
    public void setMyPlayer(Player player) {

    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public boolean currentPlayerIsMyPlayer() {
        return false;
    }

    @Override
    public boolean getSinglePlayer() {
        return false;
    }

    @Override
    public List<String> getVacantPlayerNames() {
        return null;
    }

    @Override
    public void setVacantPlayerNames(List<String> vacant) {

    }

    @Override
    public ClientOptions getClientOptions() {
        return options;
    }

    @Override
    public void toggleClientOption(String op) {

    }

    @Override
    public boolean tutorialMode() {
        return false;
    }

    @Override
    public ConnectController getConnectController() {
        return null;
    }

    @Override
    public PreGameController getPreGameController() {
        return null;
    }

    @Override
    public InGameController getInGameController() {
        return null;
    }

    @Override
    public MapEditorController getMapEditorController() {
        return null;
    }

    @Override
    public FreeColServer getFreeColServer() {
        return null;
    }

    @Override
    public GUI getGUI() {

        return new GUI(null) {
            @Override
            public ImageLibrary getFixedImageLibrary() {
                return fixedImageLibrary;
            }
        };
    }

    @Override
    public int getAnimationSpeed(Player owner) {
        return 0;
    }

    @Override
    public ActionManager getActionManager() {
        return null;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public boolean isInGame() {
        return false;
    }

    @Override
    public void quit() {

    }

    @Override
    public boolean canSaveCurrentGame() {
        return false;
    }

    @Override
    public boolean isReadyToStart() {
        return false;
    }

    @Override
    public void skipTurns(int debugRunTurns) {

    }

    @Override
    public void setGame(Game game) {

    }

    @Override
    public void retire() {

    }

    @Override
    public boolean isMapEditor() {
        return false;
    }

    @Override
    public void changeClientState(boolean b) {

    }

    @Override
    public void restoreGUI(Player player) {

    }

    @Override
    public void addSpecificationActions(Specification specification) {

    }

    @Override
    public void askToQuit() {

    }

    @Override
    public void setMapEditor(boolean b) {

    }

    @Override
    public void login(boolean b, Game game, Player player, boolean single) {

    }

    @Override
    public void setSinglePlayer(boolean b) {

    }

    @Override
    public SoundController getSoundController() {
        return null;
    }

    @Override
    public void updateActions() {

    }


    @Override
    public ServerAPI askServer() {
        return null;
    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void logout(boolean b) {

    }

    @Override
    public boolean unblockServer(int serverPort) {
        return false;
    }

    @Override
    public FreeColServer startServer(boolean publicServer,
                                     boolean singlePlayer, Specification spec,
                                     InetAddress address,
                                     int port) {
        return null;
    }

    @Override
    public FreeColServer startServer(boolean publicServer,
                                     boolean singlePlayer,
                                     File saveFile, InetAddress address,
                                     int port, String name) {
        return null;
    }

    @Override
    public void setFreeColServer(FreeColServer fcs) {

    }

    @Override
    public FreeColServer.ServerState getServerState() {
        return null;
    }

    @Override
    public void stopServer() {

    }

    @Override
    public void setServerState(FreeColServer.ServerState state) {

    }
}
