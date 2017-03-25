package com.curvedpin.solver;

import com.curvedpin.solver.gaddag.GADDAG;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ben on 3/14/17.
 */
public class WordBoard {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    //Refactor this later if we need to support different sized boards.
    final static int ROWS = 15;
    final static int COLS = 15;

    private final static String BONUS_TILES= "3=TW, 6=TL, 8=TL, 11=TW, 17=DL, 20=DW, 24=DW, 27=DL, 31=DL, 34=DL, 40=DL, 43=DL, 45=TW, 48=TL, 52=DW, 56=TL, 59=TW, 62=DL, 66=DL, 68=DL, 72=DL, 76=DW, 80=TL, 84=TL, 88=DW, 90=TL, 94=DL, 100=DL, 104=TL, 108=DW, 116=DW, 120=TL, 124=DL, 130=DL, 134=TL, 136=DW, 140=TL, 144=TL, 148=DW, 152=DL, 156=DL, 158=DL, 162=DL, 165=TW, 168=TL, 172=DW, 176=TL, 179=TW, 181=DL, 184=DL, 190=DL, 193=DL, 197=DL, 200=DW, 204=DW, 207=DL, 213=TW, 216=TL, 218=TL, 221=TW";
    public final static Map<String,Integer> LETTER_VALUES = new HashMap<>();

    //TODO: consider changing this to two lists, rows & cells (obviously with repeated TileCells across them).
    private final Map<Integer,TileCell> theBoard = new HashMap<>();

    public WordBoard(Map<Integer, String> currentLayout) {
        for(int i = 0; i < ROWS * COLS; i++) {
            if(currentLayout.containsKey(i)) theBoard.put(i,new TileCell(i,currentLayout.get(i)));
            else theBoard.put(i, new TileCell(i));
        }

        //Generate the references for each tiles neighbours.
        for(TileCell currentCell: theBoard.values()) {

            int pos = currentCell.getPos();

            //Check not above the board
            int up = pos - COLS;
            if(up > 0) currentCell.setUpCell(theBoard.get(up));

            //Check not below the board
            int down = pos + COLS;
            if( down < ROWS * COLS) currentCell.setDownCell(theBoard.get(down));

            //Check we're not at the left edge
            int left = pos - 1;
            if(pos % ROWS != 0) currentCell.setLeftCell(theBoard.get(left));

            //Check we're not at the right edge
            int right = pos + 1;
            if(pos % ROWS != 0) currentCell.setRightCell(theBoard.get(right));
        }

        for(String bounsDef: BONUS_TILES.split(",")) {
            //FIXME this is lazy, inefficient, fragile etc.. Although, it does just kind of work.
            int tilePos = Integer.parseInt(bounsDef.split("=")[0].trim());
            TileCell.TileBonus bonusType = TileCell.TileBonus.valueOf(bounsDef.split("=")[1]);
            theBoard.get(tilePos).setBonus(bonusType);
        }

        //Oh Java - you suck.
        LETTER_VALUES.put("a",1);
        LETTER_VALUES.put("b",4);
        LETTER_VALUES.put("c",4);
        LETTER_VALUES.put("d",2);
        LETTER_VALUES.put("e",1);
        LETTER_VALUES.put("f",4);
        LETTER_VALUES.put("g",3);
        LETTER_VALUES.put("h",3);
        LETTER_VALUES.put("i",1);
        LETTER_VALUES.put("j",10);
        LETTER_VALUES.put("k",5);
        LETTER_VALUES.put("l",2);
        LETTER_VALUES.put("m",4);
        LETTER_VALUES.put("n",2);
        LETTER_VALUES.put("o",1);
        LETTER_VALUES.put("p",4);
        LETTER_VALUES.put("q",10);
        LETTER_VALUES.put("r",1);
        LETTER_VALUES.put("s",1);
        LETTER_VALUES.put("t",1);
        LETTER_VALUES.put("u",2);
        LETTER_VALUES.put("v",5);
        LETTER_VALUES.put("w",4);
        LETTER_VALUES.put("x",8);
        LETTER_VALUES.put("y",3);
        LETTER_VALUES.put("z",10);
        LETTER_VALUES.put(" ",0);
    }

    /**
     * Get's a iterator for this board which returns a line on the board. A line is either a Row on the board or a column on the board.
     * This iterator will return n number of times, where n = number of rows + number of columns. You'll end up getting each cell twice.
     * @return
     */
    public Iterator<List<TileCell>> getLineIterator() {

        return new Iterator<List<TileCell>>() {

            int currentRow = 0;
            int currentCol = 0;

            @Override
            public boolean hasNext() {
                return currentRow < ROWS || currentCol < COLS;
            }

            @Override
            public List<TileCell> next() {
                List<TileCell> retVal = new ArrayList<>();
                if(currentRow < ROWS) {
                    for(int i = currentRow * COLS; i < (currentRow+1) * COLS; i++) {
                        retVal.add(theBoard.get(i));
                    }
                    currentRow++;
                } else if (currentCol < COLS) {
                    for(int i = currentCol; i < (ROWS-1) * COLS + currentCol; i += COLS) {
                        retVal.add(theBoard.get(i));
                    }
                    currentCol++;
                } else {
                    throw new NoSuchElementException();
                }
                return retVal;
            }
        };
    }

    public Map<Integer,TileCell> getAnchorSquares() {
        Map<Integer, TileCell> retVal = new HashMap<>();

        for(Map.Entry<Integer,TileCell> entry : theBoard.entrySet()) {

            if(entry.getValue().isAnchorTile()) {
                retVal.put(entry.getKey(), entry.getValue());
            }
        }

        return retVal;
    }

    public List<Move> generateCrossSets(Collection<TileCell> anchorCells,GADDAG.State initialState) {
        //for each anchor, generate the cross sets

        //FIXME Reset all the crossSets for now
        for(TileCell t : theBoard.values()) {
            t.resetCrossSets();
        }

        List<Move> retVal = new ArrayList<>();
        String letters = "abcdefghijklmnopqrst";

        for(String singleLetterRack: letters.split("")) {

            List<Move> moves = _initiateWordSearch(anchorCells, singleLetterRack, initialState, false);
            for(Move m: moves) {
                for (Move.MoveElement e : m.getMoveElements()) {

                    if(e.tile.isEmpty()) {
                        switch (m.getDirection()) {
                            case RIGHT:
                                e.tile.addAccrossCrossSet(e.letter);
                                break;
                            case DOWN:
                                e.tile.addDownCrosSet(e.letter);
                                break;
                        }
                    }
                }
            }
            retVal.addAll(moves);
        }
        return retVal;
    }

    public List<Move> initiateWordSearch(Collection<TileCell> anchorCells, String rackLetters, GADDAG.State initialState) {
        generateCrossSets(anchorCells,initialState);
        return _initiateWordSearch(anchorCells,rackLetters,initialState, true);
    }

        //FIXME this uses the anchor squares to traverse the board. This is fine, but means you're searching the board the anchor squares came from, not this board instance.
    public List<Move> _initiateWordSearch(Collection<TileCell> anchorCells, String rackLetters, GADDAG.State initialState, boolean crossSetFiltering) {

        rackLetters = rackLetters.toLowerCase();
        List<Move> candidateMoves = new ArrayList<>();

        for(TileCell currentAnchor: anchorCells) {
            if (currentAnchor.isEmpty() != true) {
                throw new IllegalStateException("Current cell isn't empty!");
            }

            //Go across and left first if we can.
            if (currentAnchor.getLeftCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialState, rackLetters, null, Direction.LEFT, candidateMoves, crossSetFiltering);
            } else if (currentAnchor.getRightCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialState, rackLetters, null, Direction.RIGHT, candidateMoves,crossSetFiltering);
            }

            //Go up first if we can down if not (top of the board)
            if (currentAnchor.getUpCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialState, rackLetters, null, Direction.UP, candidateMoves,crossSetFiltering);
            } else if (currentAnchor.getDownCell() != null) {
                findWordForAnchor(currentAnchor, currentAnchor, initialState, rackLetters, null, Direction.DOWN, candidateMoves,crossSetFiltering);
            }
        }

        return candidateMoves;
    }

    public void findWordForAnchor(TileCell anchor, TileCell currentCell, GADDAG.State state, String rackLetters, List<Move.MoveElement> currentMoves, Direction direction, List<Move> candidateMoves, boolean crossSetFiltering) {

        if (currentCell == null) {
            return;
        } else if (state == null) {
            //We have a word.
            String originalMatch = currentMoves.stream().map(moveElement -> moveElement.letter).collect(Collectors.joining());
            List<Move.MoveElement> sortedMoves = orderMoves(currentMoves);
            String word = sortedMoves.stream().map(moveElement -> moveElement.letter).collect(Collectors.joining());


            int breakOffset = originalMatch.indexOf(GADDAG.State.BREAK) - 1;

            TileCell startTile = anchor;
            if(direction == Direction.RIGHT) {
                for(int i = 0; i < breakOffset; i++) {
                    startTile = startTile.getLeftCell();
                }
            } else if (direction == Direction.DOWN) {
                for(int i = 0; i < breakOffset; i++) {
                    startTile = startTile.getUpCell();
                }
            }

            Move move = new Move(word, originalMatch, sortedMoves, startTile, anchor, direction, rackLetters);
            candidateMoves.add(move);

        } else if(currentCell.isEmpty()) {
            //For each letter left on the rack to which we can navigate (from this state)
            //Remote the letter from the rack (and place it on the board)
            state.getChildren().keySet().stream().filter(s ->  rackLetters.contains(s) || s.equals(GADDAG.State.EOW) || s.equals(GADDAG.State.BREAK)).filter(s -> {
                if(crossSetFiltering) {
                    if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) {
                        return currentCell.checkAcrossCrossSet(s);
                    } else {
                        return currentCell.checkDownCrossSet(s);
                    }
                }
                return true;
            }).forEach(s -> {

                //Remove letter from rack if it's not a BREAK or EOW
                String newRackLetters = (s.equals(GADDAG.State.BREAK) || s.equals(GADDAG.State.EOW)) ? rackLetters :  rackLetters.replaceFirst(s,"");

                Direction newDirection = direction;
                TileCell newCell = currentCell;

                if(s.equals(GADDAG.State.BREAK)) {
                    //we need to swtich direction:
                    newCell = anchor;
                    switch (direction) {
                        case LEFT:
                            newDirection = Direction.RIGHT;
                            break;
                        case RIGHT:
                            newDirection = Direction.LEFT;
                            break;
                        case UP:
                            newDirection = Direction.DOWN;
                            break;
                        case DOWN:
                            newDirection = Direction.UP;
                            break;
                    }
                }

                //Get the next tile for the now current direction
                newCell = newCell.getCellForDirection(newDirection);

                List<Move.MoveElement> newMoves = new ArrayList<>();
                if(currentMoves != null) {newMoves.addAll(currentMoves);}
                //pop the current letter into our letters holder (if we're not the root state)
                //if(state.getLetter() != State.ROOT) { newMoves.add(new Move.MoveElement(state.getLetter(),currentCell)); };
                if(!s.equals(GADDAG.State.EOW)) { newMoves.add(new Move.MoveElement(s,currentCell,true));}

                //recurse, also getting the new state (from the current state).
                findWordForAnchor(anchor,newCell,state.getChildState(s),newRackLetters, newMoves,newDirection,candidateMoves,crossSetFiltering);
            });

        } else {
            // current cell isn't empty, must be something on the board already - can we navigate to it from our current state?
            GADDAG.State nextState = state.getChildState(currentCell.getLetter());
            if(nextState != null) {
                List<Move.MoveElement> newMoves = new ArrayList<>();
                if(currentMoves != null) {newMoves.addAll(currentMoves);}
                newMoves.add(new Move.MoveElement(currentCell.getLetter(),currentCell, false));
                findWordForAnchor(anchor,currentCell.getCellForDirection(direction),state.getChildState(currentCell.getLetter()),rackLetters,newMoves,direction,candidateMoves,crossSetFiltering);
            }
        }
    }

    //FIXME rename this method.
    private List<Move.MoveElement> orderMoves(List<Move.MoveElement> moves) {

        List<Move.MoveElement> orderedMoves = new ArrayList<Move.MoveElement>();

        StringBuffer letters = new StringBuffer();
        for(Move.MoveElement m : moves) {
            letters.append(m.letter);
        }

        for(int i = letters.indexOf(GADDAG.State.BREAK) - 1; i >= 0; i-- ) {
            //sb.append(letters.charAt(i));
            orderedMoves.add(moves.get(i));
        }
        for(int i = letters.indexOf(GADDAG.State.BREAK) + 1; i < letters.length(); i++) {
            orderedMoves.add(moves.get(i));
        }
        return orderedMoves;
    }

}
