package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Tetris extends JPanel {

    private static int startX = 5;
    private static int startY = 0;
    private static Thread defThread = null;
    private static Thread softDropThread = null;
    private static String[] gameOverButtons = {"Yes", "Play Again", "Quit"};
    private static boolean gameOver = false;
    private static JLabel scoreLabel = new JLabel("Score: 0");
    private static boolean leftPressing = false;
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
            Collections.addAll(nextPieces, 0, 1, 2, 3, 4, 5, 6);

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

    public void dropDown() {
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            System.out.println(pieceOrigin.x + "," + pieceOrigin.y);
            fixToWell();
        }
        repaint();
    }

    public static void fixToWell() {
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
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public void hardDrop() {
        int fallen = 0;
        boolean touchedBottom = false;
        while (!touchedBottom) {
            if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
                pieceOrigin.y += 1;
                fallen++;
            } else {
                System.out.println(pieceOrigin.x + "," + pieceOrigin.y);
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
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
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
        g.drawString("Score: " + score, 330, 35);

        drawPiece(g);

        queuePiece(g);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
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
                            TimeUnit.MILLISECONDS.sleep(800);
                            game.dropDown();

                        } catch (InterruptedException e) {
                        }
                    }
                    int response = JOptionPane.showOptionDialog(null, "GAME OVER\n" + "Final Score: " + score, "GAME OVER",
                            JOptionPane.WARNING_MESSAGE, 0, null, gameOverButtons, gameOverButtons[1]);
                    System.out.println(response);
                    switch (response) {
                        case 0:
                            break;
                        case 1:
                            clearBoard();
                            gameOver = false;
                            score = 0;
                            break;

                        case 2:
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
        softDropThread.start();
    }

}
