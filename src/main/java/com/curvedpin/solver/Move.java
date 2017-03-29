package com.curvedpin.solver;

import com.curvedpin.solver.wordgraph.WordGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ben on 3/21/17.
 */
public class Move {

    private final String remainingRackLetters;
    private final String word;
    private final TileCell startTile;
    private final WWFClassicBoard.Direction direction;
    private final TileCell anchor;
    private final List<MoveElement> moveElements;
    private final String originalMatch;
    private Integer score = null;


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

    //fixme some of this we can just compute, so we shouldn't pass it in.
    public Move(List<MoveElement> rawMoveElements, TileCell anchor, WWFClassicBoard.Direction direction, String remainingRackLetters) {TileCell startTileTmp;

        this.direction = direction;
        this.remainingRackLetters = remainingRackLetters;
        this.anchor = anchor;

        this.originalMatch = rawMoveElements.stream().map(moveElement -> moveElement.letter).collect(Collectors.joining());
        this.moveElements = arrangeMoveElements(rawMoveElements);
        this.word = this.moveElements.stream().map(moveElement -> moveElement.letter).collect(Collectors.joining());

        int breakOffset = originalMatch.indexOf(WordGraph.Node.BREAK) - 1;

        //meh - could do away with this temp variable if we don't care about having all the properties final in this class.
        startTileTmp = anchor;
        if(direction == WWFClassicBoard.Direction.RIGHT) {
            for(int i = 0; i < breakOffset; i++) {
                startTileTmp = startTileTmp.getLeftCell();
            }
        } else if (direction == WWFClassicBoard.Direction.DOWN) {
            for(int i = 0; i < breakOffset; i++) {
                startTileTmp = startTileTmp.getUpCell();
            }
        }

        this.startTile = startTileTmp;
    }

    @Override
    public String toString() {
        int col = startTile.getPos() % WWFClassicBoard.ROWS + 1;
        int row = startTile.getPos() / WWFClassicBoard.COLS + 1;
        return String.format("%3d %-8s %-8s starting on %3d (%2dx%-2d) %-5s; anchor was %3d - remaining rack: %s", getScore(), word, "(" +originalMatch+")", startTile.getPos(),row, col, direction, anchor.getPos(), remainingRackLetters);
    }

    public TileCell getStartTile() { return startTile;}

    public TileCell getAnchorTile() {
        return anchor;
    }

    public List<MoveElement> getMoveElements() {
        return moveElements;
    }

    public WWFClassicBoard.Direction getDirection() {
        return direction;
    }

    public String getWord() {
        return word;
    }

    public int getScore() {
        return getScore(true);
    }

    public int getScore(boolean includeCrosswords) {
        if(score != null) { return score;}

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
            currentScore += WWFClassicBoard.LETTER_VALUES.get(me.letter) * multiplier;
            if(me.isPlayedFromRack() && includeCrosswords) {
                switch (direction) {
                    case UP: case DOWN:
                        if(me.tile.hasLeftFilled()|| me.tile.hasRightFilled()) {
                            currentScore +=  me.tile.getAcrossCrossScore(me.letter);
                        }
                        break;
                    case LEFT: case RIGHT:
                        if(me.tile.hasUpFilled()|| me.tile.hasDownFilled()) {
                            currentScore +=  me.tile.getDownCrossScore(me.letter);
                        }
                        break;
                }
            }
        }
        for(TileCell.TileBonus tb: bonuses) {currentScore *= tb.getMultiplier();}
        return score = currentScore;
    }

    //FIXME rename this method.
    private static List<Move.MoveElement> arrangeMoveElements(List<Move.MoveElement> moves) {

        List<Move.MoveElement> orderedMoves = new ArrayList<Move.MoveElement>();

        StringBuffer letters = new StringBuffer();
        for(Move.MoveElement m : moves) {
            letters.append(m.letter);
        }

        for(int i = letters.indexOf(WordGraph.Node.BREAK) - 1; i >= 0; i-- ) {
            orderedMoves.add(moves.get(i));
        }
        for(int i = letters.indexOf(WordGraph.Node.BREAK) + 1; i < letters.length(); i++) {
            orderedMoves.add(moves.get(i));
        }
        return orderedMoves;
    }
}
