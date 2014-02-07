package com.victone.vicsweep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/*todo:
	icons & sounds
	//shouldn't be able to right click a clicked cell...
 */

public class MineGUI extends JFrame implements ActionListener, MouseListener {
    public static final int SMALLHEIGHT = 265;
    public static final int SMALLWIDTH = 200;

    public static final int MEDIUMHEIGHT = 480;
    public static final int MEDIUMWIDTH = 400;

    public static final int LARGEHEIGHT = 480;
    public static final int LARGEWIDTH = 750;

    private static final Size defaultSize = Size.SMALL;

    private static final String VERSION = ".99";
    private static final String LABEL = "VicSweep v" + VERSION;

    private Size size;
    private MineBoard gameBoard;

    private JButton newButton;
    private JLabel remainingFlagLabel, timerLabel;
    private JPanel gamePanel;

    private Timer timer;
    private Map<JButton, MineCell> button_cellMap;

    private boolean timerBegun, gameOver;

    private long startTime;

    ActionListener taskPerformer = new ActionListener() {
        // this updates the game timer
        public void actionPerformed(ActionEvent evt) {
            int time = ((int) System.currentTimeMillis() - (int) startTime) / 1000;
            updateTimer(time);
        }
    };

    public MineGUI(Size guiSize) {
        super(LABEL);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false); //keeps buttons pretty
        setLayout(new BorderLayout());

        timer = new Timer(1000, taskPerformer);
        timerBegun = gameOver = false;

        size = guiSize;
        switch (size) {
            case SMALL:
                setSize(SMALLWIDTH, SMALLHEIGHT);
                break;
            case MEDIUM:
                setSize(MEDIUMWIDTH, MEDIUMHEIGHT);
                break;
            case LARGE:
                setSize(LARGEWIDTH, LARGEHEIGHT);
                break;
            case CUSTOM:
                break;
        }

        gameBoard = new MineBoard(size);

        buildGUI();
        setVisible(true);
    }

    private void buildGUI() {
        JPanel controlPanel = new JPanel(new GridLayout(1, 3));
        gamePanel = new JPanel(new GridLayout(gameBoard.getHeight(),
                gameBoard.getWidth()));

        newButton = new JButton("New");
        newButton.setToolTipText("Start a new game");

        remainingFlagLabel = new JLabel("Flags: "
                + gameBoard.getRemainingMines());
        remainingFlagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        remainingFlagLabel.setToolTipText("Remaining Flags");

        timerLabel = new JLabel("Time: 0");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setToolTipText("Elapsed Time");

        initGameButtons();
        addListeners();

        controlPanel.add(remainingFlagLabel);
        controlPanel.add(newButton);
        controlPanel.add(timerLabel);

        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void initGameButtons() {
        button_cellMap = new HashMap<>();

        // deal with the game buttons
        for (int i = 0; i < gameBoard.getHeight(); i++) {
            for (int j = 0; j < gameBoard.getWidth(); j++) {
                // get the button and the cell
                MineCell someCell = gameBoard.getCell(j, i);
                JButton someButton = someCell.getButton();

                // key: someButton, value: someCell
                button_cellMap.put(someButton, someCell);

                // add to the gamepanel
                gamePanel.add(someButton);
            }
        }
    }

    private void addListeners() {
        // add action/mouse listeners
        for (int i = 0; i < gameBoard.getHeight(); i++) {
            for (int j = 0; j < gameBoard.getWidth(); j++) {
                MineCell someCell = gameBoard.getCell(j, i);
                JButton someButton = someCell.getButton();
                someButton.addActionListener(this);
                someButton.addMouseListener(this);
            }
        }
        newButton.addActionListener(this);
    }

    public void newGame() {
        int n;
        // if the game is not over, ask for input
        if (!gameOver) {
            // are you sure? dialog
            n = JOptionPane.showConfirmDialog(this, "Are you sure?",
                    "New Game Confirmation", JOptionPane.YES_NO_OPTION);
            if (n == 0)
                // if the input is affirmative, new game
                newGameSize(); // resetGame();
        } else
            // if the game is over, restart
            newGameSize(); // resetGame();
    }

    @SuppressWarnings("unused")
    private void newGameSize() {
        if (timer.isRunning())
            stopTimer();

        // size dialog
        Object[] options = {"Large (16x30)", "Medium (16x16)", "Small (8x8)"};
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        int n = JOptionPane.showOptionDialog(this,
                "Please select the size of the minefield.", "Size Selection",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[2]);

        // ultra combo. there is probably a more graceful way to do this.
        // this method is slow.
        if (n == -1) {
            MineGUI mmgui = new MineGUI(size);
            dispose();
        } else if (n == 2) {
            MineGUI mmgui = new MineGUI(Size.SMALL);
            dispose();
        } else if (n == 1) {
            MineGUI mmgui = new MineGUI(Size.MEDIUM);
            dispose();
        } else if (n == 0) {
            MineGUI mmgui = new MineGUI(Size.LARGE);
            dispose();
        }
    }

    private void stopTimer() {
        // stop timer and reinit
        if (timer.isRunning())
            timer.stop();
    }

    public void updateTimer(Integer time) {
        timerLabel.setText("Time: " + time.toString());
    }

    private void updateRemainingFlags() {
        remainingFlagLabel.setText("Flags: " + gameBoard.getRemainingMines());
    }

    public void winGame() {
        gameOver = true;
        stopTimer();
        MineCell mc;
        for (int i = 0; i < gameBoard.getHeight(); i++)
            for (int j = 0; j < gameBoard.getWidth(); j++) {
                mc = gameBoard.getCell(j, i);
                if (!mc.hasBeenClicked())
                    gameBoard.leftClick(j, i);
            }
        String dialog = "You won in " + timerLabel.getText().substring(6)
                + " seconds!";
        JOptionPane.showMessageDialog(this, dialog);
    }

    public void loseGame() {
        gameOver = true;
        stopTimer();
        Toolkit.getDefaultToolkit().beep();
        MineCell mc;
        for (int i = 0; i < gameBoard.getHeight(); i++) {
            for (int j = 0; j < gameBoard.getWidth(); j++) {
                mc = gameBoard.getCell(j, i);
                if (!mc.hasBeenClicked()) {
                    gameBoard.leftClick(j, i);
                }
            }
        }
        int t = Integer.parseInt(timerLabel.getText().substring(6));
        String dialog = "You lost in " + t
                + (t == 1 ? " second!" : " seconds!");
        JOptionPane.showMessageDialog(this, dialog);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // this is for left clicks only
        // could i move this to the mouselistener...
        String command = arg0.getActionCommand();
        if (command.equals("New")) {
            newGame();
        } else {
            if (!timerBegun) { // do this only on the first click of a game
                timerBegun = true;
                timer.setInitialDelay(0);
                startTime = System.currentTimeMillis();
                timer.start();
            }
            JButton someButton = (JButton) arg0.getSource();
            someButton.setEnabled(false); //insufficient!
            MineCell someCell = button_cellMap.get(someButton);
            if (!someCell.hasBeenClicked())
                if (gameBoard.leftClick(someCell.getX(), someCell.getY())) { // clicks the cell.
                    if (!gameOver)
                        // if it's a mine, LeftClick() returns true, and you lose.
                        loseGame();
                }
            // have we won the game?
            if (!gameOver) {
                if (gameBoard.getRemainingClicks() == 0) {
                    Toolkit.getDefaultToolkit().beep();
                    winGame();
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        if (arg0.getClickCount() == 2) {
            JButton someButton = (JButton) arg0.getComponent();
            MineCell someCell = button_cellMap.get(someButton);
            if (gameBoard.bothClick(someCell.getX(), someCell.getY()))
                loseGame();
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        JButton jb = (JButton) arg0.getComponent();
        if (SwingUtilities.isRightMouseButton(arg0)) {
            if (jb.isEnabled()) {
                MineCell someCell = button_cellMap.get(jb);
                gameBoard.rightClick(someCell.getX(), someCell.getY());
                updateRemainingFlags();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override    // funny animation here on rightclick?
    public void mousePressed(MouseEvent arg0) {
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        MineGUI mmgui = new MineGUI(defaultSize);
    }
}