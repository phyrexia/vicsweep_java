package com.victone.vicsweep;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;


public class MineBoard {

	/*
     * There are three sizes: Beginner: 8 × 8 field with 10 mines Intermediate:
	 * 16 × 16 field with 40 mines Expert: 30 × 16 field with 99 mines Custom:
	 * Any values from 8 × 8 to 30 × 24 field, with 10 to 667 mines [the maximum
	 * number of mines allowed for a field of size A × B is [(A − 1) × (B − 1)].
	 */

    Size size;

    MineCell[][] mineArray;

    private int height, width, numMines, remainingMines, remainingClicks;

    public MineBoard(Size size) { // enum constructor
        this.size = size;

        switch (size) {
            case SMALL:
                height = width = 8;
                numMines = remainingMines = 10;
                break;
            case MEDIUM:
                height = width = 16;
                numMines = remainingMines = 40;
                break;
            case LARGE:
                height = 16;
                width = 30;
                numMines = remainingMines = 99;
                break;
            case CUSTOM:
                break;
        }
        remainingClicks = (height * width) - remainingMines;
        initArray();
    }

    public MineBoard(int height, int width, int numMines) {
        // custom board constructor
        this.height = height;
        this.width = width;
        // number of mines allowed for a field of size A × B is
        //[(A − 1) × (B - 1)]
        if (numMines > (height - 1) * (width - 1)) {
            numMines = (height - 1) * (width - 1);
        }
        this.numMines = numMines;

        initArray();
    }

    public void initArray() {
        // initialize the array and randomly distribute mines
        mineArray = new MineCell[height][width];

        // int odds = numMines / (height * width);

        // randomization routine
        // this fills a collection with Integers between 0 and (numMines)
        // then it increments 'counter' as it creates MineCells.
        // if current value of 'counter' is a member of the the list,
        // this particular cell has been chosen to house a mine.
        // tastes great, less filling.
        Random r = new Random();
        Collection<Integer> mineList = new HashSet<>(numMines);
        while (mineList.size() < numMines)
            mineList.add(r.nextInt(height * width));
        int counter = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mineList.contains(counter))
                    mineArray[i][j] = new MineCell(j, i, true);
                else
                    mineArray[i][j] = new MineCell(j, i, false);
                counter++;
            }
        }
    }

    private int getNumMineNeighbors(int xCoord, int yCoord) {
        // this method is just like the Life getNumNeighbors()
        // x x x the xs are the neighbors of o
        // x o x
        // x x x
        int numNeighbors = 0;
        MineCell cell;
        for (int neighborYpos = yCoord - 1; neighborYpos <= yCoord + 1; neighborYpos++) {
            for (int neighborXpos = xCoord - 1; neighborXpos <= xCoord + 1; neighborXpos++) {
                cell = getCell(neighborXpos, neighborYpos);
                if (cell != null) {  // so we don't go off board and throw a null pointer
                    if (neighborXpos != xCoord || neighborYpos != yCoord) {
                        if (cell.isMine()) {
                            numNeighbors++;
                        }
                    }
                }
            }
        }
        return numNeighbors;
    }

    private int getNumFlaggedNeighbors(int xCoord, int yCoord) {
        // returns number of neighbors that have been flagged
        int flags = 0;
        for (int neighborYpos = yCoord - 1; neighborYpos <= yCoord + 1; neighborYpos++) {
            for (int neighborXpos = xCoord - 1; neighborXpos <= xCoord + 1; neighborXpos++) {
                MineCell cell = getCell(neighborXpos, neighborYpos);
                if (cell != null) {
                    if (neighborXpos == xCoord && neighborYpos == yCoord)
                        continue;
                    if (cell.getState() == MineCellState.FLAGGED) {
                        flags++;
                    }
                }
            }
        }
        return flags;
    }

    private ArrayList<Point> getUnClickedNeighbors(int xCoord, int yCoord) {
        // returns list of unclicked neighbor cells
        ArrayList<Point> list = new ArrayList<>();
        for (int neighborYpos = yCoord - 1; neighborYpos <= yCoord + 1; neighborYpos++) {
            for (int neighborXpos = xCoord - 1; neighborXpos <= xCoord + 1; neighborXpos++) {
                MineCell cell = getCell(neighborXpos, neighborYpos);
                if (cell != null) {
                    // so we don't go off board and throw a null pointer
                    if (neighborXpos == xCoord && neighborYpos == yCoord)
                        continue;
                    if (!cell.hasBeenClicked()) {
                        list.add(new Point(neighborXpos, neighborYpos));
                    }
                }
            }
        }
        return list;
    }

    public boolean leftClick(int xCoord, int yCoord) {
        // returns true if you click a mine
        MineCell mc = getCell(xCoord, yCoord);
        boolean flag = false;
        mc.click();
        if (mc.isMine()) {// endgame routine
            mc.getButton().setForeground(Color.red);
            mc.getButton().setText("<html><b><font size=6>@</b></html>");
            flag = true;
        } else {
            remainingClicks--; // so we know if we've won
            int n = getNumMineNeighbors(xCoord, yCoord);
            switch (n) {
                case 8:
                case 7:
                case 6:
                case 5:
                case 4:
                    mc.getButton().setForeground(Color.decode("#8b008b"));
                    break;
                case 3:
                    mc.getButton().setForeground(Color.red);
                    break;
                case 2:
                    mc.getButton().setForeground(Color.decode("#006400"));
                    break;
                case 1:
                    mc.getButton().setForeground(Color.blue);
                    break;
                case 0:
                    bothClick(xCoord, yCoord);
                    break;
            }
            mc.getButton()
                    .setText(
                            "<html><b><font size = 6>"
                                    + Integer.toString(getNumMineNeighbors(xCoord,
                                    yCoord))
                                    + "</font></b></html>");
        }
        return flag;
    }

    public boolean bothClick(int xCoord, int yCoord) { // also doubleclick
        //returns the value of leftClick (so we know if we click a mine)
        boolean flag = false;
        if (getNumFlaggedNeighbors(xCoord, yCoord) == getNumMineNeighbors(
                xCoord, yCoord)) {
            // click all neighboring cells
            for (Point p : getUnClickedNeighbors(xCoord, yCoord)) {
                MineCell someCell = getCell(p.x, p.y);
                if (!someCell.hasBeenClicked())
                    if (getCell(p.x, p.y).getState() == MineCellState.EMPTY)
                        if (leftClick(p.x, p.y))
                            flag = true;
            }
        }
        return flag;
    }

    public void rightClick(int xCoord, int yCoord) {
        //change to flagged or ?'d or unmarked cell
        MineCell mc = getCell(xCoord, yCoord);
        switch (mc.getState()) {
            case EMPTY:
                mc.setState(MineCellState.FLAGGED);
                mc.getButton()
                        .setText(
                                "<html><center><font size = 6 color = red><b>F</b></font></center></html>");
                decrementRemainingMines();
                break;
            case FLAGGED:
                mc.setState(MineCellState.QUESTION);
                mc.getButton().setText(
                        "<html><center><font size = 6><b>?</b></center></html>");
                incrementRemainingMines();
                break;
            case QUESTION:
                mc.setState(MineCellState.EMPTY);
                mc.getButton().setText("");
                break;
        }
    }

    public MineCell getCell(int xCoord, int yCoord) {
        if (xCoord < 0 || yCoord < 0 || xCoord >= width || yCoord >= height) {
            return null;
        } else
            return mineArray[yCoord][xCoord];
    }

    public Integer getRemainingMines() {
        return remainingMines;
    }

    public void incrementRemainingMines() {
        remainingMines++;
    }

    public void decrementRemainingMines() {
        remainingMines--;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getRemainingClicks() {
        return remainingClicks;
    }
}