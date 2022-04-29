package net.sf.freecol.docastest.gui;

import junit.framework.TestCase;
import org.sfvl.doctesting.utils.Config;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocGenerator {
    public static ImageFile takeScreenshot(Container component, Path path) {
        final Point locationOnScreen = component.getLocationOnScreen();
        final Dimension size = component.getSize();
        final Rectangle rectangle = new Rectangle(locationOnScreen, size);
        return takeScreenshot(rectangle, path.toString());
    }

    public static ImageFile takeScreenshot(Rectangle area, String pathname) {
        final String[] split = pathname.split("\\.");
        final String extension = split[split.length - 1];

        if (Files.exists(Paths.get(pathname))) {
            final String oldChecksum = Checksum.checksum2(pathname);
            System.out.println("Old file: " + pathname);
            System.out.println("Old checksum: " + oldChecksum);
            File tmpfile = null;
            try {
                tmpfile = File.createTempFile("tmpImage", extension);
            } catch (IOException e) {
                e.printStackTrace();
                TestCase.fail(e.getMessage());
            }
            //File tmpfile = new File(Paths.get("target", "tmpImage." + extension).toString());

            System.out.println("tmp image: " + tmpfile.toString());
            final int MAX_WAITING_TIME = 5000;
            final long begin = System.currentTimeMillis();

            String newChecksum = "";
            while (!oldChecksum.equals(newChecksum) && System.currentTimeMillis() < begin + MAX_WAITING_TIME) {

                try {
                    ImageIO.write(getBackgroundImage(area), extension, tmpfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                newChecksum = Checksum.checksum2(tmpfile.toString());
                System.out.println("New checksum: " + newChecksum);
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            File outputfile = new File(pathname);
            ImageIO.write(getBackgroundImage(area), extension, outputfile);
            return new ImageFile(outputfile, Checksum.checksum2(outputfile.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * https://alvinalexander.com/blog/post/jfc-swing/how-take-create-screenshot-java-swing-robot-class/
     * Take a snapshot of the screen using the Java Robot class.
     * Return the screen shot as an Image (BufferedImage).
     *
     */
    public static BufferedImage getBackgroundImage(Rectangle size) {
        try {
            Robot rbt = new Robot();
            return rbt.createScreenCapture(size);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("");
        }
    }

    // https://stackoverflow.com/questions/4028898/create-an-image-from-a-non-visible-awt-component
    public static BufferedImage componentToImage(Component c) {

        // Set it to it's preferred size. (optional)
        c.setSize(c.getPreferredSize());
        layoutComponent(c);

        BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        CellRendererPane crp = new CellRendererPane();
        crp.add(c);

        final Graphics2D graphics = img.createGraphics();

        drawBackground(graphics, c, Color.WHITE);

        crp.paintComponent(graphics, c, crp, c.getBounds());
        return img;
    }

    private static void drawBackground(Graphics2D graphics, Component c, Color color) {
        graphics.setColor(color);
        graphics.fillRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
    }

    // from the example of user489041
    public static void layoutComponent(Component c) {
        synchronized (c.getTreeLock()) {
            c.doLayout();
            if (c instanceof Container)
                for (Component child : ((Container) c).getComponents())
                    layoutComponent(child);
        }
    }

    public static ImageFile componentToImage(Component component, Path path, String name) {
        BufferedImage img = componentToImage(component);
        try {
            if (!path.toFile().exists()) {
                path.toFile().mkdirs();
            }
            final File outputFile = path.resolve(name).toFile();
            ImageIO.write(img, "png", outputFile);
            return new ImageFile(outputFile, Checksum.checksum2(outputFile.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class ImageFile {
        final File file;
        final String checksum;

        public ImageFile(File file, String checksum) {
            this.file = file;
            this.checksum = checksum;
        }

        public String imageWithChecksum() {
            return imageWithChecksum(file.toPath(), checksum);
        }

        public String imageWithChecksum(Path path, String checksum) {
            return imageWithChecksum(path, checksum, "");
        }

        public String imageWithChecksum(Path path, String checksum, String options) {

            return String.format("image:{%s}/%s[%s]\n// Checksum %s=%s",
                    Config.DOC_PATH_TAG,
                    Config.DOC_PATH.relativize(path),
                    options,
                    path.getFileName(),
                    checksum);
        }
    }
}
