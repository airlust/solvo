package com.curvedpin.solver.image;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;


public class OCR {

    public static Map<Integer,String> getTileLetters(Map<Integer,BufferedImage> tileImages) {

        ArrayList<CompletableFuture<String>> futures = new ArrayList<>();
        Map<Integer, String> collector = new TreeMap<>();

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

                    //Put in fix for confusion between 0 and O
                    if(s.equals("0")) { s = "O";}

                    //TODO this could be more efficient of a regex.
                    if(s.length() > 1 || s.isEmpty() || s.matches("[^a-zA-Z]")) {
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
}
