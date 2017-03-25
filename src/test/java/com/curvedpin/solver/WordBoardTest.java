package com.curvedpin.solver;

import com.curvedpin.solver.gaddag.GADDAG;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

/**
 * Created by ben on 3/15/17.
 */
public class WordBoardTest {


    static GADDAG gaddagLoader;

    //TODO parameterize this test class.

    WordBoard blankBoard = new WordBoard(new HashMap<>());
    WordBoard simpleBoard;
    Map<Integer,TileCell> simpleAnchorSquares = new HashMap<>();

    WordBoard anOtherBoard;

    Map<Integer,TileCell> anOtherAnchorSquares = new HashMap<>();

    @BeforeClass
    public static void setupShared() {
        gaddagLoader = new GADDAG();
    }

    @Before
    public void setup() {

        String simpleBoardValues = "" +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , ,+,+,+,+,+, , , ," +
                " , , , , , ,+,H,E,L,L,O,+, , ," +
                " , , , , , , ,+,+,+,+,+, , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ";
        simpleBoard = setupBoardAndAnchor(simpleBoardValues,simpleAnchorSquares);

        String anOtherBoardValues = "" +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , ,+," +
                " , , , , , , , , , ,+,+,+,+,M," +
                " , , , , , , , , ,+,S,H,O,T,E," +
                " , , , , , , , , ,+,U,+,+,+,N," +
                " , , , , , , ,+,+,+,I,+,F,+,+," +
                " , , , , , ,+,P,L,O,T,+,A,+, ," +
                " , , , , , , ,+,+,+,O,+,D,+, ," +
                " , , , , , , , ,+,G,R,E,E,K,+," +
                " , , , , , , , , ,+,+,+,+,+, ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ," +
                " , , , , , , , , , , , , , , ";
        anOtherBoard = setupBoardAndAnchor(anOtherBoardValues,anOtherAnchorSquares);

    }


    private WordBoard setupBoardAndAnchor(String boardValues, Map<Integer,TileCell> anchorSquares) {
        HashMap<Integer, String> boardByCellNumber = new HashMap<>();
        String split[] = boardValues.split(",");
        int i=0;
        for(String cur: split) {
            if(cur.isEmpty() || cur.equals(" ")) {
                i++;
            } else if (cur.matches("^[a-zA-Z]+")) {
                boardByCellNumber.put(i++, cur);
            } else {
                anchorSquares.put(i, new TileCell(i++));
            }
        }
        return new WordBoard(boardByCellNumber);
    }


    @Test
    public void getLineIterator() throws Exception {
        Iterator<List<TileCell>> lineIterator = blankBoard.getLineIterator();
        int numberOfIterations = 0;
        while(lineIterator.hasNext()) {
            lineIterator.next();
            numberOfIterations++;
        }
        Assert.assertEquals(WordBoard.COLS + WordBoard.ROWS,numberOfIterations);
    }

    @Test
    public void checkSimpleBoardContent() {
        Iterator<List<TileCell>> lineIterator = simpleBoard.getLineIterator();
        int i = 0;
        while (lineIterator.hasNext()) {
            List<TileCell> line = lineIterator.next();
            if(i == 7) {
                Assert.assertEquals("",line.get(6).getLetter());
                Assert.assertEquals("h",line.get(7).getLetter());
                Assert.assertEquals("e",line.get(8).getLetter());
                Assert.assertEquals("l",line.get(9).getLetter());
                Assert.assertEquals("l",line.get(10).getLetter());
                Assert.assertEquals("o",line.get(11).getLetter());
            } else if (i == 22) {
                Assert.assertEquals("",line.get(6).getLetter());
                Assert.assertEquals("h",line.get(7).getLetter());
                Assert.assertEquals("",line.get(8).getLetter());
            }
            i++;
        }
    }

    @Test
    public void computeSimpleBoardAnchorSquares() {
        Map<Integer, TileCell> anchorSquares = simpleBoard.getAnchorSquares();
        Assert.assertEquals(simpleAnchorSquares,anchorSquares);
    }

    @Test
    public void computeAnOtherAnchorSquares() {
        Map<Integer, TileCell> anchorSquares = anOtherBoard.getAnchorSquares();
        Assert.assertEquals(anOtherAnchorSquares,anchorSquares);
    }

    @Test
    public void initateWordSearch() {
        Map<Integer, TileCell> anchorSquares = simpleBoard.getAnchorSquares();
        System.out.println("Testing");
        simpleBoard.generateCrossSets(anchorSquares.values(), gaddagLoader.getRootState());
        List<Move> helpo = simpleBoard.initiateWordSearch(Arrays.asList(new TileCell[]{anchorSquares.get(97)}), "HELPO", gaddagLoader.getRootState());
        for (Move m : helpo) {
            System.out.println(m);
        }

    }

    @Test
    public void wordSearchWholeBoard() {
        Map<Integer, TileCell> anchorSquares = simpleBoard.getAnchorSquares();
        //simpleBoard.generateCrossSets(anchorSquares.values(), gaddagLoader.getRootState());
        List<Move> moves = simpleBoard.initiateWordSearch(anchorSquares.values(), "HELPO", gaddagLoader.getRootState());

        for(Move m: moves) {
            System.out.println(m);
        }
        System.out.println("Number of possible moves: " + moves.size());
    }

    @Test
    public void testSimpleCrossSets() {
        Map<Integer, TileCell> anchorSquares = simpleBoard.getAnchorSquares();
        List<Move> moves = simpleBoard.generateCrossSets(anchorSquares.values(), gaddagLoader.getRootState());
        for(Move m: moves) {
            System.out.println(m);
        }
    }

    @Test
    public void testComplexCrossSets() {
        Map<Integer, TileCell> anchorSquares = anOtherBoard.getAnchorSquares();
        List<Move> moves = anOtherBoard.generateCrossSets(anchorSquares.values(), gaddagLoader.getRootState());
        for(Move m: moves) {
            if(m.getAnchorTile().getPos() == 58) {System.out.println(m);};
        }
    }

    @Test
    public void testComplexBoardSingleWordDictionary() {

        GADDAG filteredGADDAG = new GADDAG(s -> s.equals("ear"));
        Map<Integer, TileCell> anchorSquares = anOtherBoard.getAnchorSquares();
        anOtherBoard.generateCrossSets(anchorSquares.values(), gaddagLoader.getRootState());
        List<Move> moves = anOtherBoard._initiateWordSearch(anchorSquares.values(), "IXREWAJ", filteredGADDAG.getRootState(),true);
        Assert.assertEquals(moves.get(0).getAnchorTile().getPos(), 161);
        Assert.assertEquals(moves.get(0).getWord(), "ear");
        Assert.assertEquals(moves.size(),1);
    }

    @Test
    public void testComplexBoard() {

        Map<Integer, TileCell> anchorSquares = anOtherBoard.getAnchorSquares();
        List<Move> moves = anOtherBoard.initiateWordSearch(anchorSquares.values(), "IXREWAJ", gaddagLoader.getRootState());

        moves.sort(Comparator.comparingInt(Move::getScore).reversed());
        for(Move m: moves)
            System.out.println(m);
        System.out.println("Number of possible moves: " + moves.size());
    }

}