package com.curvedpin.services;

import com.curvedpin.solver.image.BoardImageUtils;
import com.curvedpin.solver.image.OCR;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by ben on 3/13/17.
 */
public class SolvoServicesControllerTest {

    HashMap<Integer, String> expectedBoardA = new HashMap<>();
    HashMap<Integer, String> expectedRackC = new HashMap<>();

    @Before
    public void setup() {

        String boardAValues = "" +
                " , , , ,O,O,Z,E,S, , ,V, , , ," +
                "G,U,T,T,E,D, ,T,R,O,P,I,C, , ," +
                "A, , , , , , , ,I, , ,N, , , ," +
                "G, , , , , , , , , ,N,E, , , ," +
                "E, , , , , , , , ,H,I,S, , , ," +
                "R, , , , , , , , ,I,D, , , , ," +
                " , , ,W,O, , , , ,J,A,B,S, , ," +
                " , , ,H, , , ,A,N,A,L, ,U, ,O," +
                " , , ,I, , , ,C,A,B, , ,L,O,X," +
                " ,Q, ,R,O,T,T,E,N, , , ,K, ,I," +
                " ,U, ,R, ,U, ,D, , , , , , ,D," +
                "L,E,G,S, ,L, ,I,D,E,A,T,E, ,A," +
                " ,Y, , , ,I, ,A, , , ,H, , ,N," +
                " , ,R,O,M,P, ,S,H,A,V,E,R, ,T," +
                " ,F,E,W,E,S,T, ,M,E, ,Y, , , ";


        int i = 0;
        //String[] split = Arrays.stream(boardAValues.split(",")).filter(s -> i++>0).filter(s -> !s.isEmpty()).map();
        String[] split = boardAValues.split(",");

        for(String cur: split) {
            if(cur.isEmpty() || cur.equals(" ")) {
                i++;
            } else {
                expectedBoardA.put(i++, cur);
            }
        }

        //TODO put this in a quick stream / lambda?
        String rackValues = "I,L,N,I,Q,S,E";
        split = rackValues.split(",");
        i=0;
        for(String cur: split) {
            if(cur.isEmpty()) {
                i++;
            } else {
                expectedRackC.put(i++, cur);
            }
        }

    }


    @Test
    public void processRack() throws IOException, ExecutionException, InterruptedException {

        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardD.png"));

        SolvoServicesController ssc = new SolvoServicesController();
        Map<Integer, BufferedImage> rackImages = BoardImageUtils.getRackTileImages(bf);
        System.out.println(rackImages);
        Map<Integer, String> tileLetters = OCR.getTileLetters(rackImages);
        System.out.println(tileLetters);
    }

    @Test
    public void getBoardLetters() throws IOException {
        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardA.png"));

        SolvoServicesController ssc = new SolvoServicesController();
        Map<Integer, String> boardLetters = BoardImageUtils.getBoardLetters(bf);

    }

    @Test
    public void testWhiteOOnBonus() throws IOException {
        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardF.png"));

        SolvoServicesController ssc = new SolvoServicesController();
        Map<Integer, String> boardLetters = BoardImageUtils.getBoardLetters(bf);
        Assert.assertEquals("O",boardLetters.get(8));

    }

    @Test
    public void getBlankBoard() throws IOException {
        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardE.png"));

        SolvoServicesController ssc = new SolvoServicesController();
        Map<Integer, String> boardLetters = BoardImageUtils.getBoardLetters(bf);
        System.out.println(boardLetters);
        Assert.assertEquals(new HashMap<>(), boardLetters);

    }

    @Test
    public void getRackLetters() throws IOException {
        BufferedImage bf = ImageIO.read(ClassLoader.getSystemResourceAsStream("SampleBoardC.png"));
        SolvoServicesController ssc = new SolvoServicesController();
        Map<Integer, String> rackLetters = BoardImageUtils.getRackLetters(bf);
        Assert.assertEquals(expectedRackC, rackLetters);
    }
}