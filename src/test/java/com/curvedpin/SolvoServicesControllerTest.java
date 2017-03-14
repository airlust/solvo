package com.curvedpin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by ben on 3/13/17.
 */
public class SolvoServicesControllerTest {

    HashMap<Integer, String> expectedBoardA = new HashMap<>();

    @Before
    public void setup() {

        String boardAValues = ",,,TW,O,O,Z,E,S,,,V,,,," +
                "G,U,T,T,E,D,,T,R,O,P,I,C,,," +
                "A,DL,,,DL,,,,I,,DL,N,,DL,," +
                "G,,,TL,,,,DW,,,N,E,,,TW," +
                "E,,DL,,,,DL,,DL,H,I,S,DL,,," +
                "R,DW,,,,TL,,,,I,D,,,DW,," +
                "TL,,,W,O,,,,,J,A,B,S,,TL," +
                ",,,H,,,,A,N,A,L,DW,U,,O," +
                "TL,,,I,DL,,,C,A,B,DL,,L,O,X," +
                ",Q,,R,O,T,T,E,N,TL,,,K,DW,I," +
                ",U,DL,R,,U,DL,D,DL,,,,DL,,D," +
                "L,E,G,S,,L,,I,D,E,A,T,E,,A," +
                ",Y,,,DL,I,,A,,,DL,H,,DL,N," +
                ",,R,O,M,P,,S,H,A,V,E,R,,T," +
                ",F,E,W,E,S,T,,M,E,,Y,,,";

        String[] split = boardAValues.split(",");
        int i = 0;
        for(String cur: split) {
            if(cur.isEmpty()) {
                i++;
            } else {
                expectedBoardA.put(i++, cur);
            }
        }

    }

    @Test
    public void getBoardFromImage() throws Exception {

        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardA.png"));

        SolvoServicesController ssc = new SolvoServicesController();
        HashMap<Integer,String> boardValues = new HashMap<>();
        ssc.processMainBoard(bf,new OCROperation(boardValues));
        Assert.assertEquals(expectedBoardA, boardValues);
    }

    @Test
    public void processRack() throws IOException, ExecutionException, InterruptedException {

        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardC.png"));

        SolvoServicesController ssc = new SolvoServicesController();
        List<BufferedImage> rackImages = ssc.processRack(bf);
        System.out.println(rackImages);
    }
}