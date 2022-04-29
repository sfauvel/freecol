package net.sf.freecol.client;

public interface FreeColClientOptions {
    ClientOptions getClientOptions();

    void toggleClientOption(String op);

    boolean tutorialMode();
}
