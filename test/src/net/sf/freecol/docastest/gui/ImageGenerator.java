package net.sf.freecol.docastest.gui;

import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.mapviewer.TileViewer;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.resources.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ImageGenerator {

    private final Path imagePath;
    private final GuiWindows windows;

    private static final Predicate<Tile> ALL_TILE = t -> true;

    float scaleFactor = ImageLibrary.NORMAL_SCALE;
    ImageCache imageCache = new ImageCache();
    ImageLibrary scaledImageLibrary = new ImageLibrary(scaleFactor, imageCache);
    ImageLibrary fixedImageLibrary = new ImageLibrary(scaleFactor, imageCache);
    ImageLibrary lib = fixedImageLibrary;

    Dimension tileSize = this.lib.getTileSize();

    final TileViewer tileViewer = new TileViewer(null, lib);

    public ImageGenerator(Path imagePath, GuiWindows windows) {
        this.imagePath = imagePath;
        this.windows = windows;
    }

    private java.util.List<Tile> pathToTiles(PathNode firstNode) {
        java.util.List<PathNode> paths = pathToList(firstNode);

        for (PathNode pathNode : paths) {
            Tile tile = pathNode.getTile();


            System.out.println(String.format("%d / %d, cost %d, turns %d, direction %s",
                    tile.getX(), tile.getY(), pathNode.getCost(), pathNode.getTurns(), pathNode.getDirection()));
        }
        return paths.stream()
                .map(PathNode::getTile)
                .collect(Collectors.toList());
    }

    public java.util.List<PathNode> pathToList(PathNode path) {
        java.util.List<PathNode> paths = new ArrayList<>();
        for (; path != null; path = path.next) {
            final Tile tile = path.getTile();
            paths.add(path);
        }
        return paths;
    }

    final BiConsumer<Graphics2D, Tile> drawer = (g2d, tile) -> {
        tileViewer.displayTileWithBeach(g2d, tile);
        BufferedImage overlayImage = lib.getScaledOverlayImage(tile);
        tileViewer.displayTileItems(g2d, tile, null, overlayImage);

//        System.out.println(String.format("%d / %d: %s", tile.getX(), tile.getY(), tile.getTile().getType().getId()));
        final String text = String.format("%d / %d", tile.getX(), tile.getY());
        g2d.drawString(text, tileSize.width / 2 - g2d.getFontMetrics().stringWidth(text) / 2, tileSize.height / 2);
    };

    final BiConsumer<Graphics2D, Tile> drawerPath = (g2d, tile) -> {
        g2d.fillOval(tileSize.width / 2, tileSize.height / 2, 10, 10);
    };

    static class DrawTiles {
        BiConsumer<Graphics2D, Tile> drawer;
        java.util.List<Tile> tiles;

        public DrawTiles(BiConsumer<Graphics2D, Tile> drawer, java.util.List<Tile> tiles) {
            this.drawer = drawer;
            this.tiles = tiles;
        }
    }

    public DocGenerator.ImageFile generateImageWith(Map map, PathNode path, String imageName) throws InterruptedException {
        final java.util.List<DrawTiles> drawers = Arrays.asList(
                new DrawTiles(drawer, map.getTileList(ALL_TILE)),
                new DrawTiles(drawerPath, pathToTiles(path))
        );
        return generateImageWith(drawers, imageName);
    }

    private DocGenerator.ImageFile generateImageWith(Map map, BiConsumer<Graphics2D, Tile> drawer, java.util.List<Map.Position> positionStream, String imageName) throws InterruptedException {
        return generateImageWith(drawer, positionStream.stream().map(map::getTile).collect(Collectors.toList()), imageName);
    }

    private DocGenerator.ImageFile generateImageWith(BiConsumer<Graphics2D, Tile> drawer, java.util.List<Tile> tiles, String imageName) throws InterruptedException {
        return generateImageWith(Arrays.asList(new DrawTiles(drawer, tiles)), imageName);
    }

    private DocGenerator.ImageFile generateImageWith(java.util.List<DrawTiles> drawTilesList, String imageName) throws InterruptedException {

        final Integer maxX = drawTilesList.stream().flatMap(d -> d.tiles.stream()).map(Tile::getX).max(Integer::compareTo).get();
        final Integer maxY = drawTilesList.stream().flatMap(d -> d.tiles.stream()).map(Tile::getY).max(Integer::compareTo).get();

        final Container panel = show(g2d -> {
            for (DrawTiles drawTiles : drawTilesList) {
                drawTiles.tiles.forEach(tile -> display(g2d, tile, drawTiles.drawer));
            }
        }, maxX + 1, (maxY + 2) / 2);

        final DocGenerator.ImageFile imageFile = DocGenerator.takeScreenshot(panel, imagePath.resolve(imageName));
        return imageFile;
    }

    public static class Coordinate {
        private final int x;
        private final int y;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private void display(Graphics2D g2d, Tile tile, BiConsumer<Graphics2D, Tile> tileDrawer) {
        Coordinate coordinate = getCoordinate(tile);
        final int xt = coordinate.getX();
        final int yt = coordinate.getY();

        g2d.translate(xt, yt);

        tileDrawer.accept(g2d, tile);

        g2d.translate(-xt, -yt);
    }

    private Coordinate getCoordinate(Tile tile) {
        final int x = tile.getX();
        final int y = tile.getY();
        final int xt = x * tileSize.width
                + (y & 1) * tileSize.width / 2;
        final int yt = y * tileSize.height / 2;

        Coordinate coordinate = new Coordinate(xt, yt);
        return coordinate;
    }

    public JPanel show(java.util.function.Consumer<Graphics2D> displayMethod) throws InterruptedException, IOException {
        return show(displayMethod, 1, 1);
    }

    private JPanel show(java.util.function.Consumer<Graphics2D> displayMethod, int nbTileWidth, int nbTileHeight) throws InterruptedException {
        final JPanel jPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.translate(0, 20);
                displayMethod.accept((Graphics2D) g);

            }
        };
        jPanel.setPreferredSize(new Dimension(nbTileWidth * tileSize.width + tileSize.width / 2, nbTileHeight * tileSize.height + 20));

        windows.displayInFrame(jPanel);
        return jPanel;
    }

}
