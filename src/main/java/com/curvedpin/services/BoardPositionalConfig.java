package com.curvedpin.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ben on 3/13/17.
 */
class BoardPositionalConfig {

    final static Map<String,BoardPositionalConfig> boardMap = new HashMap<>();
    final static Map<String,BoardPositionalConfig> rackMap = new HashMap<>();

    static {

        //TODO: add the config for boards and racks for a given size of board.
        //IPHONE6_BOARD(15, 15, (float) (15 / 3), (float) (15 / 3), 14, 344, 48, 48, 3, 14, 36, 30),
        //        IPHONE6_RACK(1, 7, (float) (15 / 1), (float) (15 / 1), 3, 1094, 107, 0, 20, 25, 70, 70);
    }


    private final int rows;
    private final int cols;
    private final float xFudge;
    private final float yFudge;
    private final int xStart;
    private final int yStart;
    private final int cellXDist;
    private final int cellYDist;
    private final int cellXBorder;
    private final int cellYBorder;
    private final int cellWidth;
    private final int cellHeight;

    BoardPositionalConfig(int rows, int cols, float xFudge, float yFudge, int xStart, int yStart, int cellXDist, int cellYDist, int cellXBorder, int cellYBorder, int cellWidth, int cellHeight) {
        this.rows = rows;
        this.cols = cols;
        this.xFudge = xFudge;
        this.yFudge = yFudge;
        this.xStart = xStart;
        this.yStart = yStart;
        this.cellXDist = cellXDist;
        this.cellYDist = cellYDist;
        this.cellXBorder = cellXBorder;
        this.cellYBorder = cellYBorder;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public float getxFudge() {
        return xFudge;
    }

    public float getyFudge() {
        return yFudge;
    }

    public int getxStart() {
        return xStart;
    }

    public int getyStart() {
        return yStart;
    }

    public int getCellXDist() {
        return cellXDist;
    }

    public int getCellYDist() {
        return cellYDist;
    }

    public int getCellXBorder() {
        return cellXBorder;
    }

    public int getCellYBorder() {
        return cellYBorder;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }
}
