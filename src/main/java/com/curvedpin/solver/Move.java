package com.curvedpin.solver;

import java.util.ArrayList;
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
    private final Integer score = null;

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

        return String.format("%3d %-8s %-8s starting on %3d (%2dx%-2d) %-5s; anchor was %3d - remaining rack: %s", getScore(), word, "(" +originalMatch+")", startTile.getPos(),row, col, direction, anchor.getPos(), remainingRackLetters);
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
        boolean playedFromRack;

        public MoveElement(String letter, TileCell tile, boolean playedFromRack) {
            this.letter = letter;
            this.tile = tile;
            this.playedFromRack = playedFromRack;
        }

        public boolean isPlayedFromRack() {
            return playedFromRack;
        }

    }

    public int getScore() {
        if(score != null) { return score;}

        List<TileCell> crossWords = new ArrayList<>();
        List<TileCell.TileBonus> bonuses = new ArrayList<>();
        int currentScore = 0 ;
        for (MoveElement me : moveElements) {
            int multiplier = 1;
            if (me.tile.hasBonus() && me.isPlayedFromRack()) {
                if (me.tile.getBonus().isWordBonus()) {
                    bonuses.add(me.tile.getBonus());
                } else {
                    multiplier = me.tile.getBonus().getMultiplier();
                }
            }
            currentScore += WordBoard.LETTER_VALUES.get(me.letter) * multiplier;
            switch (direction) {
                case UP: case DOWN:
                    if(me.tile.hasLeftFilled()|| me.tile.hasRightFilled()) {
                        crossWords.add(me.tile);
                    }
                    break;
                case LEFT: case RIGHT:
                    if(me.tile.hasUpFilled()|| me.tile.hasDownFilled()) {
                        crossWords.add(me.tile);
                    }
                    break;
            }
        }

        //TODO calculate crossScores.

        return currentScore;
    }
}
