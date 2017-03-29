package com.curvedpin.solver.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ben on 3/29/17.
 */
public class BoardImageUtils {

    public static BufferedImage toBinaryImage(final BufferedImage image) {
        BufferedImage blackAndWhiteImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D) blackAndWhiteImage.getGraphics();
        g.drawImage(image, 0, 0, null);

        WritableRaster raster = blackAndWhiteImage.getRaster();
        int[] pixels = new int[blackAndWhiteImage.getWidth()];
        boolean allWhite = true;
        for (int y = 0; y < blackAndWhiteImage.getHeight(); y++) {
            raster.getPixels(0, y, blackAndWhiteImage.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] < 90 || pixels[i] > 237) {
                    pixels[i] = 0;
                    allWhite = false;
                } else pixels[i] = 255;
            }
            raster.setPixels(0, y, blackAndWhiteImage.getWidth(), 1, pixels);
        }
        g.dispose();
        if (allWhite) {
            return null;
        }
        return blackAndWhiteImage;
    }

    public static Map<Integer,BufferedImage> getTilesFromImage(BufferedImage bf, BoardGeometryConfig boardPositionalConfig) {

        Map<Integer, BufferedImage> retVal = new HashMap<>();
        for (int i = 0; i < boardPositionalConfig.getRows(); i++) {
            for (int j = 0; j < boardPositionalConfig.getCols(); j++) {
                int x_fudge = Math.round(j / boardPositionalConfig.getxFudge());
                int y_fudge = Math.round(i / boardPositionalConfig.getyFudge());
                int x = boardPositionalConfig.getxStart() + j * boardPositionalConfig.getCellXDist() + x_fudge + boardPositionalConfig.getCellXBorder();
                int y = boardPositionalConfig.getyStart() + i * boardPositionalConfig.getCellYDist() + y_fudge + boardPositionalConfig.getCellYBorder();

                BufferedImage subimage = toBinaryImage(bf.getSubimage(x, y, boardPositionalConfig.getCellWidth(), boardPositionalConfig.getCellHeight()));

                int cellNumber = i * boardPositionalConfig.getRows() + j;
                if (subimage != null) retVal.put(cellNumber,subimage);
            }
        }
        return retVal;
    }

    public static Map<Integer,String> getBoardLetters(BufferedImage bf) {
        Map<Integer, BufferedImage> boardTileImages = getBoardTileImages(bf);
        return OCR.getTileLetters(boardTileImages);
    }

    public static Map<Integer,String> getRackLetters(BufferedImage bf) {
        Map<Integer, BufferedImage> rackTileImages = getRackTileImages(bf);
        return OCR.getTileLetters(rackTileImages);
    }

    public static Map<Integer,BufferedImage> getBoardTileImages(BufferedImage bf) {
        Map<Integer, BufferedImage> tilesFromImage = getTilesFromImage(bf, BoardGeometryConfig.getBoardConfig());
        return tilesFromImage;
    }

    public static Map<Integer,BufferedImage> getRackTileImages(BufferedImage bf) {
        Map<Integer, BufferedImage> tilesFromImage = getTilesFromImage(bf, BoardGeometryConfig.getRackConfig());
        return tilesFromImage;
    }

}
