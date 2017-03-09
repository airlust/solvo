package com.curvedpin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by ben on 3/3/17.
 */
public class ImageToTextRequestTest {

    @Test
    public void testToJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BufferedImage blackAndWhiteImage = ImageIO.read(ClassLoader.getSystemResourceAsStream("Sample-string.png"));

        ImageToTextRequest imageToTextRequest = new ImageToTextRequest(blackAndWhiteImage);
        System.out.println(mapper.writeValueAsString(imageToTextRequest));

    }

    @Test
    public void testCloudOCR() throws IOException {

        BufferedImage image = ImageIO.read(ClassLoader.getSystemResourceAsStream("Sample-E.png"));
        String json = GreetingController.callOCR(image);
        //System.out.println(json);


        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(json);
        System.out.println(jsonNode.at("/responses/0/fullTextAnnotation/text"));

    }

}