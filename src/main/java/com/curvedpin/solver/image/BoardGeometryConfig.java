package com.curvedpin.solver.image;

/**
 * Created by ben on 3/13/17.
 */
public class BoardGeometryConfig {

    //TODO: this is currently hardcoded for regular iPhone 6/7
    private static final BoardGeometryConfig boardConfig = new BoardGeometryConfig(15, 15, (float) (15 / 3), (float) (15 / 3), 14, 344, 48, 48, 3, 14, 36, 30);
    private static final BoardGeometryConfig rackConfig = new BoardGeometryConfig(1, 7, (float) (15 / 1), (float) (15 / 1), 3, 1094, 107, 0, 12, 25, 78, 70);

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

    public BoardGeometryConfig(int rows, int cols, float xFudge, float yFudge, int xStart, int yStart, int cellXDist, int cellYDist, int cellXBorder, int cellYBorder, int cellWidth, int cellHeight) {
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

    public static BoardGeometryConfig getRackConfig() {
        return rackConfig;
    }

    public static BoardGeometryConfig getBoardConfig() {
        return boardConfig;
    }
}
