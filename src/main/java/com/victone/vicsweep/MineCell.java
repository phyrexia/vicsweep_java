package com.victone.vicsweep;

import javax.swing.*;
import java.awt.*;

public class MineCell {

    //we are reading png files as an array of ints
    //private static final int MAXICONSIZE = 4096;

    private MineCellState state;

    private JButton myButton;
    //private ImageIcon mineFlag, questionMark;
    //, smiley;

    // presence of a mine; whether cell's been actuated
    private boolean mine, clicked;

    private int xCoord, yCoord;

    public MineCell(int x, int y, boolean mine) {
        // all a cell needs to know is whether it's a mine,
        // if it's flagged, or if it's marked
        xCoord = x;
        yCoord = y;
        this.mine = mine;
        myButton = new JButton(); // i guess it needs a button too
        state = MineCellState.EMPTY;
        clicked = false;

        //center
        myButton.setMargin(new Insets(0, -30, 0, -30));

//		easy mode
//		if (mine) {
//		myButton.setToolTipText("BOMB");
//      }
    }

    public int getX() {
        return xCoord;
    }

    public int getY() {
        return yCoord;
    }

    public boolean isMine() {
        return mine;
    }

    public MineCellState getState() {
        return state;
    }

    public void setState(MineCellState someState) {
        // corresponds to a right click in the GUI
        state = someState;
    }

    public void click() {
        clicked = true;
    }

    public boolean hasBeenClicked() { // not used yet, need to integrate
        return clicked;
    }

    public JButton getButton() {
        return myButton;
    }

    public String toString() {
        return xCoord + ":" + yCoord;
    }

//	private void loadIcons() {
//		ClassLoader classLoader = Thread.currentThread()
//				.getContextClassLoader();
//
//		mineFlag = new ImageIcon("flag.gif");
//
//		// read questionmark.png
//		Scanner scanner = new Scanner(
//				classLoader.getResourceAsStream("questionmark.png"));
//		byte[] questionByte = new byte[MAXICONSIZE];
//		for (int i = 0; i < MAXICONSIZE; i++) {
//			if (scanner.hasNextByte()) {
//				questionByte[i] = scanner.nextByte();
//			}
//		}
//
//		// read flag.png
//		scanner = new Scanner(
//				classLoader.getResourceAsStream("flag.png"));
//		byte[] flagByte = new byte[MAXICONSIZE];
//		for (int i = 0; i < MAXICONSIZE; i++) {
//			if (scanner.hasNextByte()) {
//				flagByte[i] = scanner.nextByte();
//			}
//		}
//
    // read smiley.png
//		scanner = new Scanner(
//				classLoader.getResourceAsStream("questionmark.png"));
//		byte[] smileyByte = new byte[MAXICONSIZE];
//		for (int i = 0; i < MAXICONSIZE; i++) {
//			if (scanner.hasNextByte()) {
//				smileyByte[i] = scanner.nextByte();
//			}
//		}
//	}
}