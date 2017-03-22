package com.curvedpin.solver;

import java.util.List;

/**
 * Created by ben on 3/21/17.
 */
public class Move {

    private final String remainingRackLetters;
    private final String word;
    private final TileCell startTile;
    private final WordBoard.Direction direction;
    private final TileCell anchor;
    private final List<MoveElement> moveElements;
    private final String originalMatch;

    //fixme some of this we can just compute, so we shouldn't pass it in.
    public Move(String word, String originalMatch, List<MoveElement> moveElements, TileCell startTile, TileCell anchor, WordBoard.Direction direction, String remainingRackLetters) {
        this.word = word;
        this.startTile = startTile;
        this.direction = direction;
        this.remainingRackLetters = remainingRackLetters;
        this.moveElements = moveElements;
        this.anchor = anchor;
        this.originalMatch = originalMatch;
    }

    @Override
    public String toString() {
        //FIXME this relies on the board being static
        int col = startTile.getPos() % WordBoard.ROWS + 1;
        int row = startTile.getPos() / WordBoard.COLS + 1;

        return String.format("%-8s %-8s starting on %3d (%2dx%2-d) %-5s; anchor was %3d - remaining rack: %s", word, "(" +originalMatch+")", startTile.getPos(),row, col, direction, anchor.getPos(), remainingRackLetters);
    }

    public TileCell getStartTile() { return startTile;}

    public TileCell getAnchorTile() {
        return anchor;
    }

    public List<MoveElement> getMoveElements() {
        return moveElements;
    }

    public WordBoard.Direction getDirection() {
        return direction;
    }

    public String getWord() {
        return word;
    }

    public static class MoveElement {
        String letter;
        TileCell tile;

        public MoveElement(String letter, TileCell tile) {
            this.letter = letter;
            this.tile = tile;
        }
    }
}
