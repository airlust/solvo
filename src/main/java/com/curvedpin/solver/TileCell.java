package com.curvedpin.solver;

import com.curvedpin.solver.gaddag.GADDAG;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ben on 3/15/17.
 */
public class TileCell {


    public boolean hasBonus() {
        return bonus != null;
    }

    public enum TileBonus {
        TL(false,3),TW(true,3),DL(false,2),DW(true,2);

        private final int multiplier;
        private boolean wordBonus;
        TileBonus(boolean wordBonus,int multiplier) {
            this.wordBonus = wordBonus;
            this.multiplier = multiplier;
        }
        public boolean isWordBonus() {
            return wordBonus;
        }
        public int getMultiplier() {
            return multiplier;
        }
    }

    private final int pos;

    private TileBonus bonus;
    private String tileLetter = null;
    private TileCell upCell;
    private TileCell downCell;
    private TileCell leftCell;
    private TileCell rightCell;

    private Set<String> acrossCrossSet = new TreeSet<>();
    private Set<String> downCrossSet = new TreeSet<>();


    public TileCell(int pos, String tileLetter) {
        this.tileLetter = tileLetter;
        this.pos = pos;
    }

    public TileCell(int pos) {
        tileLetter = "";
        this.pos = pos;
    }

    public void setBonus(TileBonus tb) {
        this.bonus = tb;
    }

    public TileBonus getBonus() {
        return bonus;
    }

    public String getLetter() {
        return tileLetter.toLowerCase();
    }
    public int getPos() { return pos; }

    public boolean isEmpty() { return tileLetter.isEmpty();}

    public TileCell getUpCell() {
        return upCell;
    }

    public TileCell getDownCell() {
        return downCell;
    }

    public TileCell getLeftCell() {
        return leftCell;
    }

    public TileCell getRightCell() {
        return rightCell;
    }

    public void setUpCell(TileCell upCell) {
        this.upCell = upCell;
    }

    public void setDownCell(TileCell downCell) {
        this.downCell = downCell;
    }

    public void setLeftCell(TileCell leftCell) {
        this.leftCell = leftCell;
    }

    public void setRightCell(TileCell rightCell) {
        this.rightCell = rightCell;
    }

    public boolean hasRightFilled() { return rightCell != null && !rightCell.isEmpty();}
    public boolean hasLeftFilled() { return leftCell != null && !leftCell.isEmpty();}
    public boolean hasUpFilled() { return upCell != null && !upCell.isEmpty();}
    public boolean hasDownFilled() { return downCell != null && !downCell.isEmpty();}

    public boolean isAnchorTile() {
        //Would be nice here if we could use the .? syntax of groovy or swift
        //upCell?.isEmpty() etc..
        return tileLetter.isEmpty() && ((upCell != null && !upCell.isEmpty()) || (downCell != null && !downCell.isEmpty()) || (leftCell != null && !leftCell.isEmpty()) || (rightCell != null && !rightCell.isEmpty())) ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TileCell tileCell = (TileCell) o;

        if (pos != tileCell.pos) return false;
        return tileLetter.equals(tileCell.tileLetter);
    }

    @Override
    public int hashCode() {
        int result = pos;
        result = 31 * result + tileLetter.hashCode();
        return result;
    }

    public TileCell getCellForDirection(WordBoard.Direction direction) {

        switch (direction) {
            case LEFT:
                return leftCell;
            case RIGHT:
                return rightCell;
            case UP:
                return upCell;
            case DOWN:
                return downCell;
        }
        return null;
    }

    public void addAccrossCrossSet(String letter) {
        acrossCrossSet.add(letter);
    }

    public void addDownCrosSet(String letter) {
        downCrossSet.add(letter);
    }

    public void resetCrossSets() {
        acrossCrossSet = new TreeSet<>();
    }

    public boolean checkDownCrossSet(String s) {
        return !(hasUpFilled() || hasDownFilled()) || downCrossSet.contains(s) || s.equals(GADDAG.State.BREAK) || s.equals(GADDAG.State.EOW);
    }

    public boolean checkAcrossCrossSet(String s) {
        return !(hasRightFilled() || hasLeftFilled()) || acrossCrossSet.contains(s) || s.equals(GADDAG.State.BREAK) || s.equals(GADDAG.State.EOW);
    }

}
