package net.sf.freecol.docastest.gui;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.mapviewer.TileViewer;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.resources.ImageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
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

    final TileViewer tileViewer;

    public ImageGenerator(Path imagePath, GuiWindows windows, FreeColClient client) {
        this.imagePath = imagePath;
        this.windows = windows;
        this.tileViewer = new TileViewer(client, lib);
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


    final void drawer(Graphics2D g2d, Tile tile) {
        tileViewer.displayTileWithBeach(g2d, tile);
        BufferedImage overlayImage = lib.getScaledOverlayImage(tile);
        tileViewer.displayTileItems(g2d, tile, null, overlayImage);
        final RescaleOp standardRescale
                = new RescaleOp(new float[] { 0.8f, 0.8f, 0.8f, 1f },
                new float[] { 0, 0, 0, 0 },
                null);
        RescaleOp rop = standardRescale;
//        displayTileItems(g2d, tile, rop, overlayImage);
//        displaySettlementWithChipsOrPopulationNumber(g2d, tile, false, rop);

        tileViewer.displaySettlementWithChipsOrPopulationNumber(g2d, tile,  false, rop);

//        System.out.println(String.format("%d / %d: %s - %s", tile.getX(), tile.getY(), tile.getTile().getType().getId(), ((tile.getColony()==null)?"":tile.getColony().getName())));
        final String text = String.format("%d / %d", tile.getX(), tile.getY());

        g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, g2d.getFont().getSize()));
        g2d.drawString(text, tileSize.width / 2 - g2d.getFontMetrics().stringWidth(text) / 2, tileSize.height / 2);
    };

    final void drawerPath(Graphics2D g2d, Tile tile) {
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
        return generateImageWith(map, 0, 0, path, imageName);
    }

    public DocGenerator.ImageFile generateImageWith(Map map, int nbTileToTranslateX, int nbTileToTranslateY, PathNode path, String imageName) throws InterruptedException {
//        final java.util.List<DrawTiles> drawers = Arrays.asList(
//                new DrawTiles(this::drawer, map.getTileList(ALL_TILE)),
//                new DrawTiles(this::drawerPath, pathToTiles(path))
//        );
        final java.util.List<DrawTiles> drawers = new ArrayList<>();
        drawers.add(new DrawTiles(this::drawer, map.getTileList(ALL_TILE)));
        if (path != null) {
            drawers.add(new DrawTiles(this::drawerPath, pathToTiles(path)));
        }
        return generateImageWith(drawers, imageName, nbTileToTranslateX, nbTileToTranslateY);
    }

    private DocGenerator.ImageFile generateImageWith(Map map, BiConsumer<Graphics2D, Tile> drawer, java.util.List<Map.Position> positionStream, String imageName) throws InterruptedException {
        return generateImageWith(drawer, positionStream.stream().map(map::getTile).collect(Collectors.toList()), imageName);
    }

    private DocGenerator.ImageFile generateImageWith(BiConsumer<Graphics2D, Tile> drawer, java.util.List<Tile> tiles, String imageName) throws InterruptedException {
        return generateImageWith(Arrays.asList(new DrawTiles(drawer, tiles)), imageName);
    }

    private DocGenerator.ImageFile generateImageWith(java.util.List<DrawTiles> drawTilesList, String imageName) throws InterruptedException {
        return generateImageWith(drawTilesList, imageName, 0, 0);
    }

    private DocGenerator.ImageFile generateImageWith(java.util.List<DrawTiles> drawTilesList, String imageName, int nbTileToTranslateX, int nbTileToTranslateY) throws InterruptedException {
        final Integer maxX = drawTilesList.stream().flatMap(d -> d.tiles.stream()).map(Tile::getX).max(Integer::compareTo).get();
        final Integer maxY = drawTilesList.stream().flatMap(d -> d.tiles.stream()).map(Tile::getY).max(Integer::compareTo).get();

        final Container panel = show(g2d -> {
            g2d.translate(-nbTileToTranslateX *tileSize.width, -nbTileToTranslateY *tileSize.height+20);
            for (DrawTiles drawTiles : drawTilesList) {
                drawTiles.tiles.forEach(tile -> display(g2d, tile, drawTiles.drawer));
            }
            g2d.translate(nbTileToTranslateX *tileSize.width, nbTileToTranslateY *tileSize.height+20);
        }, maxX + 1, maxY + 1/*(maxY + 2) / 2*/);

        final DocGenerator.ImageFile imageFile = DocGenerator.takeScreenshot(panel, imagePath.resolve(imageName));
        windows.closeWindows();
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
                displayMethod.accept((Graphics2D) g);

            }
        };
        System.out.println("nbTileHeight:" + nbTileHeight);
        System.out.println("nbTileHeight / 2.0:" + (nbTileHeight / 2.0));
        System.out.println("nbTileHeight / 2.0:" + (int)Math.ceil(nbTileHeight / 2.0));
        System.out.println("tileSize.height:" + tileSize.height);
        System.out.println((int)Math.ceil(nbTileHeight / 2.0) * tileSize.height);
        jPanel.setPreferredSize(new Dimension(
                nbTileWidth * tileSize.width + tileSize.width / 2,
                (nbTileHeight + 1) * (tileSize.height / 2) + (20*2) /*+ 20*/));
//                (int)Math.ceil(nbTileHeight / 2.0) * tileSize.height + (20*2) /*+ 20*/));
//                nbTileHeight * tileSize.height + (int)Math.ceil(tileSize.height / 2.0) /*20*/));
/**
        1 => 2 demi
            2 => 3 demi
 3 => 4
 4 => 5
 */
        windows.displayInFrame(jPanel);
        return jPanel;
    }

}
