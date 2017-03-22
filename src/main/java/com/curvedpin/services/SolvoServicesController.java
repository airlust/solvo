package com.curvedpin.services;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class SolvoServicesController {

    private final BoardPositionalConfig boardConfig = new BoardPositionalConfig(15, 15, (float) (15 / 3), (float) (15 / 3), 14, 344, 48, 48, 3, 14, 36, 30);
    private final BoardPositionalConfig rackConfig = new BoardPositionalConfig(1, 7, (float) (15 / 1), (float) (15 / 1), 3, 1094, 107, 0, 12, 25, 78, 70);


    @PostMapping(value = "/chopshop/board", produces = "application/json")
    public Map<Integer,String> getBoard(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());
        return getBoardLetters(bf);
    }

    @PostMapping(value = "/chopshop/rack", produces = "application/json")
    public Map<Integer,String> getRack(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());
        return getRackLetters(bf);
    }

    @PostMapping(value = "/chopshop", produces = "application/json")
    public ArrayList<Map<Integer,String>> getRackAndBoard(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());
        Map<Integer, String> boardLetters = getBoardLetters(bf);
        Map<Integer, String> rackLetters = getRackLetters(bf);
        return new ArrayList<>(Arrays.asList(boardLetters,rackLetters));
    }

    public Map<Integer,String> getBoardLetters(BufferedImage bf) {
        Map<Integer, BufferedImage> boardTileImages = getBoardTileImages(bf);
        return getTileLetters(boardTileImages);
    }

    public Map<Integer,String> getRackLetters(BufferedImage bf) {
        Map<Integer, BufferedImage> rackTileImages = getRackTileImages(bf);
        return getTileLetters(rackTileImages);
    }

    public Map<Integer,BufferedImage> getBoardTileImages(BufferedImage bf) {
        Map<Integer, BufferedImage> tilesFromImage = getTilesFromImage(bf, boardConfig);
        return tilesFromImage;
    }

    public Map<Integer,BufferedImage> getRackTileImages(BufferedImage bf) {
        Map<Integer, BufferedImage> tilesFromImage = getTilesFromImage(bf, rackConfig);
        return tilesFromImage;
    }

    public Map<Integer,String> getTileLetters(Map<Integer,BufferedImage> tileImages) {

        ArrayList<CompletableFuture<String>> futures = new ArrayList<>();
        Map<Integer, String> collector = new HashMap<>();

        for(Map.Entry<Integer,BufferedImage> entry: tileImages.entrySet()) {

            int cellNumber = entry.getKey();
            BufferedImage bf = entry.getValue();

            futures.add(CompletableFuture.supplyAsync(() -> {
                String s;
                try {
                    Tesseract tess = new Tesseract();
                    tess.setPageSegMode(13);
                    //TODO refactor this so it lives in some kind of deployment variable.
                    if(new File("/usr/local/share/tessdata").exists()) tess.setDatapath("/usr/local/share/");
                    if(new File("/usr/share/tesseract/tessdata").exists()) tess.setDatapath("/usr/share/tesseract/");
                    s = tess.doOCR(bf).trim().toUpperCase();
                    if(s.length() > 1 || s.isEmpty()) {
                        System.out.println(String.format("Ignoring cell %d, content %s", cellNumber, s, Thread.currentThread().getName()));
                    } else {
                        System.out.println(String.format("cell %d, content %s", cellNumber, s, Thread.currentThread().getName()));
                        collector.put(cellNumber, s);
                    }
                } catch (TesseractException e) {
                    throw new IllegalStateException("Unable to do OCR on this image", e);
                }
                return s;
            }));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return collector;
    }

    public Map<Integer,BufferedImage> getTilesFromImage(BufferedImage bf, BoardPositionalConfig boardPositionalConfig) {

        Map<Integer, BufferedImage> retVal = new HashMap<>();
        for (int i = 0; i < boardPositionalConfig.getRows(); i++) {
            for (int j = 0; j < boardPositionalConfig.getCols(); j++) {
                int x_fudge = Math.round(j / boardPositionalConfig.getxFudge());
                int y_fudge = Math.round(i / boardPositionalConfig.getyFudge());
                int x = boardPositionalConfig.getxStart() + j * boardPositionalConfig.getCellXDist() + x_fudge + boardPositionalConfig.getCellXBorder();
                int y = boardPositionalConfig.getyStart() + i * boardPositionalConfig.getCellYDist() + y_fudge + boardPositionalConfig.getCellYBorder();

                BufferedImage subimage = toBinaryImage(bf.getSubimage(x, y, boardPositionalConfig.getCellWidth(), boardPositionalConfig.getCellHeight()));

                int cellNumber = i * boardPositionalConfig.getRows() + j;
                int curCol = j;
                if (subimage != null) retVal.put(cellNumber,subimage);
            }
        }
        return retVal;
    }

    BufferedImage toBinaryImage(final BufferedImage image) {
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

}