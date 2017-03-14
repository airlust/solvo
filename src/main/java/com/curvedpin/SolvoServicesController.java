package com.curvedpin;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class SolvoServicesController {

    BoardPositionalConfig mainConfig = new BoardPositionalConfig(15, 15, (float) (15 / 3), (float) (15 / 3), 14, 344, 48, 48, 3, 14, 36, 30);



    @PostMapping(value = "/chopshop", produces = "application/json")
    public Map<Integer,String> chopShop(@RequestParam("file") MultipartFile file) throws IOException, TesseractException, ExecutionException, InterruptedException {
        BufferedImage bf = ImageIO.read(file.getInputStream());

        HashMap<Integer,String> boardCellMap = new HashMap<>();
        ImageOperation<String> imageOp = new OCROperation(boardCellMap);
        processMainBoard(bf, imageOp);
        return boardCellMap;
    }

    void processMainBoard(BufferedImage bf, ImageOperation<String> imageOp ) throws ExecutionException, InterruptedException {
        processBoardFromImage(bf, imageOp, mainConfig);
    }

    List<BufferedImage> processRack(BufferedImage bf) throws ExecutionException, InterruptedException {

        BoardPositionalConfig boardConfig = new BoardPositionalConfig(1, 7, (float) (15 / 1), (float) (15 / 1), 3, 1094, 107, 0, 20, 25, 70, 70);
        ArrayList<BufferedImage> flattenedBoard = new ArrayList<>(Collections.nCopies(boardConfig.getCols() * boardConfig.getRows(), null));
        ImageOperation<String> imageOp = (img, curCol, cellNumber) -> {
            System.out.println(String.format("cell %d - Thread: %s", cellNumber, Thread.currentThread().getName()));
            flattenedBoard.set(cellNumber, img);
        };

        processBoardFromImage(bf, imageOp, boardConfig);
        return flattenedBoard;
    }

    void processBoardFromImage(BufferedImage bf, ImageOperation<String> imageOp, BoardPositionalConfig boardPositionalConfig) throws InterruptedException, ExecutionException {

        ArrayList<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < boardPositionalConfig.getRows(); i++) {
            for (int j = 0; j < boardPositionalConfig.getCols(); j++) {
                int x_fudge = Math.round(j / boardPositionalConfig.getxFudge());
                int y_fudge = Math.round(i / boardPositionalConfig.getyFudge());
                int x = boardPositionalConfig.getxStart() + j * boardPositionalConfig.getCellXDist() + x_fudge + boardPositionalConfig.getCellXBorder();
                int y = boardPositionalConfig.getyStart() + i * boardPositionalConfig.getCellYDist() + y_fudge + boardPositionalConfig.getCellYBorder();

                BufferedImage subimage = toBinaryImage(bf.getSubimage(x, y, boardPositionalConfig.getCellWidth(), boardPositionalConfig.getCellHeight()));

                int cellNumber = i * boardPositionalConfig.getRows() + j;
                int curCol = j;
                if (subimage != null) {
                    futures.add(CompletableFuture.supplyAsync(() -> {
                        imageOp.doOp(subimage, curCol, cellNumber);
                        return null;
                    }));
                }
            }
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @PostMapping(value = "/filterimage", produces = "image/png")
    public byte[] filterImage(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("content type: " + file.getContentType());
        BufferedImage bf = ImageIO.read(file.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(toBinaryImage(bf), "png", baos);
        return baos.toByteArray();
    }

    @PostMapping(value = "chopimage", produces = "image/png")
    public byte[] chopImage(@RequestParam("file") MultipartFile file) throws IOException {

        //ROWS = 1, COLS=8(?)
        //X=3, Y=1094
        //Height=102, Width= 101, 6 is the spacing

        //BoardPositionalConfig(1 , 7, 0, 0, 3, 1094, 107, 0, 0, 0, 101, 102)
        BufferedImage bf = ImageIO.read(file.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(toBinaryImage(bf), "png", baos);
        return baos.toByteArray();
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