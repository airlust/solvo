package com.curvedpin.solver;

import java.util.*;


public class WWFClassicBoard {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    //Refactor this later if we need to support different sized boards.
    final static int ROWS = 15;
    final static int COLS = 15;

    private final static String BONUS_TILES= "3=TW, 6=TL, 8=TL, 11=TW, 17=DL, 20=DW, 24=DW, 27=DL, 31=DL, 34=DL, 40=DL, 43=DL, 45=TW, 48=TL, 52=DW, 56=TL, 59=TW, 62=DL, 66=DL, 68=DL, 72=DL, 76=DW, 80=TL, 84=TL, 88=DW, 90=TL, 94=DL, 100=DL, 104=TL, 108=DW, 116=DW, 120=TL, 124=DL, 130=DL, 134=TL, 136=DW, 140=TL, 144=TL, 148=DW, 152=DL, 156=DL, 158=DL, 162=DL, 165=TW, 168=TL, 172=DW, 176=TL, 179=TW, 181=DL, 184=DL, 190=DL, 193=DL, 197=DL, 200=DW, 204=DW, 207=DL, 213=TW, 216=TL, 218=TL, 221=TW";
    public final static Map<String,Integer> LETTER_VALUES = new HashMap<>();

    static {
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

    //TODO: consider changing this to two lists, rows & cells (obviously with repeated TileCells across them).
    private final Map<Integer,TileCell> theBoard = new HashMap<>();

    public WWFClassicBoard(Map<Integer, String> currentLayout) {
        for(int i = 0; i < ROWS * COLS; i++) {
            if(currentLayout.containsKey(i)) theBoard.put(i,new TileCell(i,currentLayout.get(i)));
            else theBoard.put(i, new TileCell(i));
        }

        //Special Case for blank board
        if (currentLayout.size() == 0) {
            //Get the center; this works because we it's rounded down and we're indexed at 0
            //It won't work if the COLS or ROWS are even.
            if(COLS % 2 != 1 || ROWS % 2 != 1) {
                throw new IllegalStateException("Can't find the middle of the board rows, cols: " + ROWS + " , " + COLS);
            }
            TileCell center = theBoard.get(ROWS * COLS / 2);
            center.setCenterTile(true);
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

    public void resetAllCrossSets() {
        for(TileCell t : theBoard.values()) {
            t.resetCrossSets();
        }
    }

}
