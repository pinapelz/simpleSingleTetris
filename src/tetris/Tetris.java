package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Tetris extends JPanel {

    private static int startX = 5;
    private static int startY = 0;
    private static int clearedLines = 0;
    private static String levelUpSound = "19_levelup.wav";
    private static int level = 0;
    private static int levelDelay = 800;
    private static Clip musicClip;
    private static Thread defThread = null;
    private static Thread softDropThread = null;
    private static String[] gameOverButtons = {"Yes", "Play Again", "Quit"};
    private static boolean gameOver = false;
    private static int timeElapsed = 0;
    private static String bgmOne = "bgm_01.wav";
    private static String bgmTwo = "bgm_02.wav";
    private static String bgmThree = "bgm_03.wav";
    private static String oneVoiceSound = "01_single.wav";
    private static String twoVoiceSound = "02_double.wav";
    private static String threeVoiceSound = "03_triple.wav";
    private static String tetrisVoiceSound = "04_tetris.wav";
    private static String twoLineSound = "sfx_double.wav";
    private static String threeLineSound = "sfx_triple.wav";
    private static String tetrisSound = "sfx_tetris.wav";
    private static String oneLineSound = "sfx_single.wav";
    private static String softDropSound = "sfx_softdrop.wav";
    private static String rotateSound = "sfx_rotate.wav";
    private static String gameOverSound = "sfx_gameover.wav";
    private static boolean levelledUp = false;
    private static String hardDropSound = "sfx_harddrop.wav";
    private static boolean isHardDrop = false;
    private static boolean leftPressing = false;
    private static float volume = -5.0f;
    static Tetris game = new Tetris();
    private static boolean rightPressing = false;
    private static boolean downPressing = false;
    private final static Color[] tetraminoColors = {
        Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.pink, Color.red
    };
    private final static Point[][][] Tetraminos = {
        {
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)}
        },
        {
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)}
        },
        {
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
            {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)}
        },
        {
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)}
        },
        {
            {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1)},
            {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)}
        },
        {
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
            {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
            {new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2)}
        },
        {
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)},
            {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
            {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)}
        }
    };

    private static Point pieceOrigin;
    private static int currentPiece;
    private static int rotation;
    private static ArrayList<Integer> nextPieces = new ArrayList<Integer>();

    private static long score;
    private static Color[][] well;

    private void init() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        newPiece();
    }

    private static void reinit() {
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j == 22) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        newPiece();
    }

    public static void newPiece() {
        pieceOrigin = new Point(startX, startY);

        rotation = 0;
        if (nextPieces.isEmpty()) {
            Random r = new Random();
            Collections.addAll(nextPieces, r.nextInt(7), r.nextInt(7), r.nextInt(7), r.nextInt(7), r.nextInt(7), r.nextInt(7), r.nextInt(7));
            //Collections.addAll(nextPieces, 0, 0, 0, 0, 0, 0, 0);
            Collections.shuffle(nextPieces);
        }
        currentPiece = nextPieces.get(0);
        nextPieces.remove(0);
    }

    private static boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }

    public void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    public void move(int i) {
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
        }
        repaint();
    }

    public static void checkDropSound() throws Exception {
        if (isHardDrop) {
            playSound(hardDropSound, 0);
            isHardDrop = false;
        } else {
            playSound(softDropSound, 3.2f);
        }
    }

    public void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {

            fixToWell();
        }
        repaint();
    }

    public static void fixToWell() {
        try {
            checkDropSound();
        } catch (Exception ex) {
            Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pieceOrigin.x <= startX && pieceOrigin.y <= startY) {
            System.out.println("Game Over");
            gameOver = true;
        }
        for (Point p : Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();

        newPiece();
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public void hardDrop() {
        isHardDrop = true;
        int fallen = 0;
        boolean touchedBottom = false;
        while (!touchedBottom) {
            if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
                pieceOrigin.y += 1;
                fallen++;
            } else {

                fixToWell();
                touchedBottom = true;
            }
            repaint();
        }
        score += fallen;
    }

    public static void deleteRow(int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < 11; i++) {
                well[i][j + 1] = well[i][j];
            }
        }
    }

    public static void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {

                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
            case 1:
                score += 100;
                clearedLines++;
                 {
                    try {
                        playSound(oneVoiceSound, -1.0f);
                        playSound(oneLineSound, -2.0f);

                    } catch (Exception ex) {
                        Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

            case 2: {
                clearedLines += 2;
                try {
                    playSound(twoVoiceSound, -1.0f);
                    playSound(twoLineSound, -2.0f);
                } catch (Exception ex) {
                    Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            score += 300;
            break;

            case 3:
                clearedLines += 3;
                try {
                    playSound(threeVoiceSound, -1.0f);
                    playSound(threeLineSound, -2.0f);
                } catch (Exception ex) {
                    Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                }
                score += 500;
                break;
            case 4:
                clearedLines += 4;
                try {
                    playSound(tetrisVoiceSound, -1.0f);
                    playSound(tetrisSound, -2.0f);
                } catch (Exception ex) {
                    Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                }
                score += 800;
                break;
        }
        System.out.println("Lines" + clearedLines);
    }

    public static void clearBoard() {
        reinit();
    }

    private void drawPiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * 26,
                    (p.y + pieceOrigin.y) * 26,
                    25, 25);
        }
    }

    private void queuePiece(Graphics g) {
        g.setColor(tetraminoColors[currentPiece]);

    }

    private static void checkLevel() {
        int prevLevel = level;
        System.out.println(prevLevel);
        if (clearedLines >= 10 && clearedLines < 15) {
            level = 1;
            levelDelay = 750;

        } else if (clearedLines >= 0 && clearedLines < 10) {
            level = 0;
            levelDelay = 800;
        } else if (clearedLines >= 15 && clearedLines < 20) {
            level = 2;
            levelDelay = 650;
        } else if (clearedLines >= 20 && clearedLines < 30) {
            level = 3;
            levelDelay = 500;
        } else if (clearedLines >= 30 && clearedLines < 40) {
            level = 4;
            levelDelay = 300;
        } else if (clearedLines >= 40 && clearedLines < 45) {
            level = 5;
            levelDelay = 200;
        } else if (clearedLines >= 45 && clearedLines < 55) {
            level = 6;
            levelDelay = 120;
        } else if (clearedLines >= 55 && clearedLines < 60) {
            level = 7;
            levelDelay = 80;
        } else if (clearedLines >= 60 && clearedLines < 65) {
            level = 8;
            levelDelay = 60;
        } else {
            level = 9;
            levelDelay = 45;
        }
        if (prevLevel != level) {
            System.out.println("PLAY SOUND");
            try {
                playSound(levelUpSound, -2f);
            } catch (Exception ex) {
                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.fillRect(0, 0, 26 * 12, 26 * 23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26 * i, 26 * j, 25, 25);
                g.setColor(Color.GRAY);
                g.fillRect(322, 0, 322 + 200, 623);
            }
        }

        g.setColor(Color.WHITE);
        Font font = new Font("Helvetica", Font.BOLD, 24);
        g.setFont(font);
        g.drawString("Score: " + score, 330, 55);
        Font timerFont = new Font("Helvetica", Font.BOLD, 12);
        g.setFont(timerFont);
        g.drawString("Time Elapsed: " + convertToTime(timeElapsed), 330, 20);
        drawPiece(g);

        queuePiece(g);
    }

    private static void playSound(String soundFile, float gain) throws Exception {
        Clip clip = AudioSystem.getClip();
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("sounds\\" + soundFile).getAbsoluteFile());
        clip.open(audioInput);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);
        clip.start();

    }

    private static void playBGM(float gain) throws Exception {

        String soundFile = "";
        Random r = new Random();
        switch (r.nextInt(3) + 1) {
            case 1:
                soundFile = bgmOne;
                break;
            case 2:
                soundFile = bgmTwo;
                break;
            case 3:
                soundFile = bgmThree;
                break;

        }
        musicClip = AudioSystem.getClip();
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("sounds\\" + soundFile).getAbsoluteFile());
        musicClip.open(audioInput);
        FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);
        musicClip.loop(999);
    }

    private static void stopBGM() {
        musicClip.stop();
    }

    // open audioInputStream to the clip 
    public static void main(String[] args) {

        JFrame f = new JFrame("Tetris");
        try {
            playBGM(-10f);
        } catch (Exception ex) {
            Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
        }
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(322 + 200, 26 * 23 + 25);
        f.setVisible(true);
        game.init();
        f.add(game);
        f.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                         {
                            try {
                                playSound(rotateSound, -15.0f);

                            } catch (Exception ex) {
                                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        downPressing = true;
                        break;
                    case KeyEvent.VK_LEFT:

                        leftPressing = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressing = true;
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!gameOver) {
                            game.hardDrop();
                        }
                        break;

                }

            }

            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        leftPressing = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        rightPressing = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        downPressing = false;
                        break;

                }
            }
        });
        defThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (gameOver != true) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(levelDelay);

                            game.dropDown();

                        } catch (InterruptedException e) {
                        }
                    }
                    try {
                        playSound(gameOverSound, -6f);
                    } catch (Exception ex) {
                        Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    int response = JOptionPane.showOptionDialog(null, "GAME OVER\n" + "Final Score: " + score, "GAME OVER",
                            JOptionPane.WARNING_MESSAGE, 0, null, gameOverButtons, gameOverButtons[1]);

                    switch (response) {
                        case 0:
                            //Future online leaderboard code goes here
                            break;
                        case 1:
                            clearBoard();
                            gameOver = false;
                            score = 0;
                            stopBGM();
                             {
                                try {
                                    playBGM(-10.0f);
                                } catch (Exception ex) {
                                    Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            break;

                        case 2:
                            //Local leaderboard
                            System.exit(0);
                    }

                }
            }
        };
        defThread.start();
        Thread gameLoop = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(85);
                        if (leftPressing) {
                            game.move(-1);
                        } else if (rightPressing) {
                            game.move(+1);
                        }

                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        gameLoop.start();

        softDropThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(30);
                        if (downPressing && !gameOver) {
                            game.dropDown();
                            game.score += 1;
                        }

                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        Thread gameTimer = new Thread() {
            public void run() {

                while (true) {

                    try {
                        while (!gameOver) {
                            TimeUnit.MILLISECONDS.sleep(1000);
                            timeElapsed++;
                        }

                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        Thread levelChecker = new Thread() {
            public void run() {

                while (true) {

                    try {
                        while (!gameOver) {
                            checkLevel();
                            TimeUnit.MILLISECONDS.sleep(1500);
                        }

                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        softDropThread.start();
        levelChecker.start();
        gameTimer.start();
    }

    public static String convertToTime(int seconds) {
        int minutesDisplay = seconds / 60;
        int secondsDisplay = seconds % 60;
        if (secondsDisplay < 10) {
            return minutesDisplay + ":0" + secondsDisplay;
        }
        return minutesDisplay + ":" + secondsDisplay;
    }

}
