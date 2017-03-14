package com.curvedpin;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ben on 3/13/17.
 */
public class OCROperation implements ImageOperation<String> {

    private HashMap<Integer,String> collector;

    public OCROperation(HashMap<Integer,String> collector) {
        this.collector = collector;
    }

    @Override
    public void doOp(BufferedImage bf, int currentCol, int cellNumber) {
        String s;
        try {
            Tesseract tess = new Tesseract();
            tess.setPageSegMode(13);
            tess.setDatapath("/usr/local/share/");
            s = tess.doOCR(bf).trim().toUpperCase();
            System.out.println(String.format("cell %d, content %s - Thread: %s", cellNumber, s, Thread.currentThread().getName()));
            collector.put(cellNumber, s);
        } catch (TesseractException e) {
            throw new IllegalStateException("Unable to do OCR on this image", e);
        }
    }
}
