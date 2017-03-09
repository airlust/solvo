package com.curvedpin;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

@RestController
public class GreetingController {

    static final int START_X = 14;
    static final int START_Y = 344;
    static final int CELL_HEIGHT = 47;
    static final int CELL_WIDTH = 47;
    static final int CELL_BORDER_X = 0;
    static final int CELL_BORDER_Y = 0;
    static final int CELL_X_DIST = 48;
    static final int CELL_Y_DIST = 48;

    static final int ROWS = 15;
    static final int COLS = 15;

    static final float X_FUDGE = ROWS / 3;
    static final float Y_FUDGE = COLS / 3;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private final AtomicInteger pos = new AtomicInteger();

    Tesseract1 tess = new Tesseract1();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @PostMapping(value = "/chopshop", produces = "image/png")
    public byte[] chopShop(@RequestParam("file") MultipartFile file, @RequestParam(value = "col", required = false) int col, @RequestParam(value = "row", required = false) int row) throws IOException, TesseractException {
        ArrayList<BufferedImage> choppedCells = new ArrayList<>();
        BufferedImage bf = ImageIO.read(file.getInputStream());
        tess.setPageSegMode(13);
        tess.setDatapath("/usr/local/share/");
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {

                int x_fudge = Math.round(j / X_FUDGE);
                int y_fudge = Math.round(i / Y_FUDGE);
                int x = START_X + j * CELL_X_DIST + x_fudge + CELL_BORDER_X;
                int y = START_Y + i * CELL_Y_DIST + y_fudge + CELL_BORDER_Y;

                BufferedImage subimage = bf.getSubimage(x, y, CELL_WIDTH, CELL_HEIGHT);
                //subimage = subimage.getSubimage(3,13,36,29);
                subimage = toBinaryImage(subimage);
                choppedCells.add(subimage);
//                String ocrd = callOCR(subimage);
//                if (ocrd == null || ocrd.isEmpty()) {
//                    System.out.print(" ");
//                } else {
//                    ocrd = StringEscapeUtils.unescapeJava(ocrd).trim();
//                    if(ocrd.length() < 2) {
//                        System.out.print(ocrd);
//                    } else {
//                        System.out.print(" ");
//                    }
//                }
            }
            System.out.println();
        }

        for (int i = 0; i < choppedCells.size(); i++) {

            BufferedImage bi = choppedCells.get(i);

            if (bi == null) continue;

            String s = tess.doOCR(bi).trim();
            System.out.println(String.format("cell %d, content %s", i, s));

            FileOutputStream fos = new FileOutputStream(String.format("Cell-%d-(%s).png", i, s));
            ImageIO.write(bi, "png", fos);
            fos.close();

        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (row != 0 || col != 0) {
            ImageIO.write(choppedCells.get(row * COLS + col), "png", baos);
        } else {
            ImageIO.write(choppedCells.get(pos.incrementAndGet()), "png", baos);
        }
        return baos.toByteArray();
    }

    @PostMapping(value = "/filterimage", produces = "image/png")
    public byte[] filterImage(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("content type: " + file.getContentType());
        BufferedImage bf = ImageIO.read(file.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(toBinaryImage(bf), "png", baos);
        return baos.toByteArray();
    }

    BufferedImage toBinaryImage(final BufferedImage image) {
        BufferedImage blackAndWhiteImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = (Graphics2D) blackAndWhiteImage.getGraphics();
        g.drawImage(image, 0, 0, null);

        blackAndWhiteImage = blackAndWhiteImage.getSubimage(3, 14, 36, 30);

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

    static String callOCR(BufferedImage characterImage) throws IOException {

        final String URL = "https://vision.googleapis.com/v1/images:annotate?key=REDACTED";

        ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        ImageToTextRequest imageReqeust = new ImageToTextRequest(characterImage);
        String s = restTemplate.postForObject(URL, imageReqeust, String.class);
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(s);
        return jsonNode.at("/responses/0/fullTextAnnotation/text").asText();
    }

    static private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(config)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}