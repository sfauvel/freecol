package net.sf.freecol.docastest.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GuiWindows {
    public static final int WAIT_DISPLAY_FRAME = 1000;
    static List<Window> windows = new ArrayList<>();

    public static void displayInFrame(Component comp) throws InterruptedException {
        final JFrame jFrame = createJFrame();

//        jFrame.setUndecorated(true);
        jFrame.setBackground(new Color(100, 100, 100));
        jFrame.add(comp);
        jFrame.pack();
        jFrame.setVisible(true);

        Thread.sleep(WAIT_DISPLAY_FRAME); // Needed to give time to display frame
    }

    public static JFrame createJFrame() {
        return registerWindow(new JFrame());
    }

    public static <T extends Window> T registerWindow(T window) {
        windows.add(window);
        return window;
    }

    public static void closeWindows() {
        for (Window window : windows) {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }
        windows.clear();
    }
}
