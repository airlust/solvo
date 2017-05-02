package com.curvedpin.solver;

import com.curvedpin.solver.wordgraph.WordGraph;
import org.junit.*;

import java.util.*;

/**
 * Created by ben on 3/15/17.
 */
public class WordBoardTest {


    static WordGraph gaddagLoader;

    //TODO parameterize this test class.

    WWFClassicBoard blankBoard = new WWFClassicBoard(new HashMap<>());
    WWFClassicBoard simpleBoard;
    Map<Integer,TileCell> simpleAnchorSquares = new HashMap<>();

    WWFClassicBoard anOtherBoard;

    Map<Integer,TileCell> anOtherAnchorSquares = new HashMap<>();

    @BeforeClass
    public static void setupShared() {
        gaddagLoader = new WordGraph();
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


    private WWFClassicBoard setupBoardAndAnchor(String boardValues, Map<Integer,TileCell> anchorSquares) {
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
        return new WWFClassicBoard(boardByCellNumber);
    }


    @Test
    @Ignore
    public void checkSimpleBoardContent() {
//        Iterator<List<TileCell>> lineIterator = new I simpleBoard.getAllTiles();
//        int i = 0;
//        while (lineIterator.hasNext()) {
//            List<TileCell> line = lineIterator.next();
//            if(i == 7) {
//                Assert.assertEquals("",line.get(6).getLetter());
//                Assert.assertEquals("h",line.get(7).getLetter());
//                Assert.assertEquals("e",line.get(8).getLetter());
//                Assert.assertEquals("l",line.get(9).getLetter());
//                Assert.assertEquals("l",line.get(10).getLetter());
//                Assert.assertEquals("o",line.get(11).getLetter());
//            } else if (i == 22) {
//                Assert.assertEquals("",line.get(6).getLetter());
//                Assert.assertEquals("h",line.get(7).getLetter());
//                Assert.assertEquals("",line.get(8).getLetter());
//            }
//            i++;
//        }
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
        ScrabbleSolver.generateCrossSets(simpleBoard, gaddagLoader.getRootNode());

        //This board just returns one anchor square so we can narrow down the test.
        WWFClassicBoard decoratedBoard = new WWFClassicBoard(simpleBoard.getTheBoard()) {
            @Override
            public Map<Integer, TileCell> getAnchorSquares() {
                Map<Integer, TileCell> retVal = new HashMap<>();
                retVal.put(97,super.getAnchorSquares().get(97));
                return retVal;
            }
        };

        List<Move> helpo = ScrabbleSolver.wordSearch(decoratedBoard, "HELPO", gaddagLoader.getRootNode());
        for (Move m : helpo) {
            System.out.println(m);
        }

    }

    @Test
    public void wordSearchWholeBoard() {
        //simpleBoard.generateCrossSets(anchorSquares.values(), gaddagLoader.getRootNode());
        List<Move> moves = ScrabbleSolver.wordSearch(simpleBoard, "HELPO", gaddagLoader.getRootNode());

        for(Move m: moves) {
            System.out.println(m);
        }
        System.out.println("Number of possible moves: " + moves.size());
    }

    @Test
    public void testSimpleCrossSets() {
        List<Move> moves = ScrabbleSolver.generateCrossSets(simpleBoard, gaddagLoader.getRootNode());
        for(Move m: moves) {
            System.out.println(m);
        }
    }

    @Test
    public void testComplexCrossSets() {
        List<Move> moves = ScrabbleSolver.generateCrossSets(anOtherBoard, gaddagLoader.getRootNode());
        for(Move m: moves) {
            if(m.getAnchorTile().getPos() == 58) {System.out.println(m);};
        }
    }

    @Test
    public void testComplexBoardSingleWordDictionary() {

        WordGraph filteredGADDAG = new WordGraph(s -> s.equals("ear"));
        ScrabbleSolver.generateCrossSets(anOtherBoard, gaddagLoader.getRootNode());
        List<Move> moves = ScrabbleSolver._wordSearch(anOtherBoard, "IXREWAJ", filteredGADDAG.getRootNode(),true);
        Assert.assertEquals(moves.get(0).getAnchorTile().getPos(), 161);
        Assert.assertEquals(moves.get(0).getWord(), "ear");
        Assert.assertEquals(moves.size(),1);
    }

    @Test
    public void testComplexBoard() {

        List<Move> moves = ScrabbleSolver.wordSearch(anOtherBoard, "IXREWAJ", gaddagLoader.getRootNode());

        moves.sort(Comparator.<Move>comparingInt(Move::getScore).reversed());
        for(Move m: moves)
            System.out.println(m);
        System.out.println("Number of possible moves: " + moves.size());
    }

    @Test
    public void testBlankBoardHasCenterAnchor() {
        WWFClassicBoard myBlankBoard = new WWFClassicBoard(new HashMap<>());
        Map<Integer, TileCell> anchorSquares = myBlankBoard.getAnchorSquares();
        Assert.assertEquals(1,anchorSquares.size());
        Assert.assertEquals(112,anchorSquares.get(112).getPos());
    }

    @Test
    public void testBlankBoardFirstMove() {
        Map<Integer, TileCell> anchorSquares = blankBoard.getAnchorSquares();
        Assert.assertEquals(1,anchorSquares.size());
        Assert.assertEquals(112,anchorSquares.get(112).getPos());

        List<Move> moves = ScrabbleSolver.wordSearch(blankBoard, "JOUKER", gaddagLoader.getRootNode());

        moves.sort(Comparator.<Move>comparingInt(Move::getScore).reversed());
        for(Move m: moves)
            System.out.println(m);
        System.out.println("Number of possible moves: " + moves.size());

    }

}