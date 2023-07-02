package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Game2048 extends JPanel implements KeyListener {

    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private static final int ROWS = 4;
    private static final int COLS = 4;
    private static final int TILE_SIZE = 100;
    private static final int TILE_MARGIN = 15;

    private int[][] board;
    private int score;
    private boolean gameOver;
    private JButton resetButton;

    public Game2048() {
        setPreferredSize(new Dimension(500, 500));
        setBackground(new Color(187, 173, 160));
        setLayout(new BorderLayout());

        board = new int[ROWS][COLS];
        score = 0;
        gameOver = false;

        initialize();

        addKeyListener(this);

        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(new Color(187, 173, 160));
        scorePanel.setLayout(new BorderLayout());

        resetButton = new JButton("Restart");
        resetButton.setFocusable(false);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        scorePanel.add(resetButton, BorderLayout.PAGE_END);
        add(scorePanel, BorderLayout.PAGE_START);

        setFocusable(true);
    }

    private void initialize() {
        addRandomTile();
        addRandomTile();
    }

    private void addRandomTile() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(ROWS);
            col = random.nextInt(COLS);
        } while (board[row][col] != 0);

        int value = random.nextDouble() < 0.75 ? 2 : 4;
        board[row][col] = value;
    }

    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void move(Direction direction) {
        boolean moved = false;

        switch (direction) {
            case LEFT:
                for (int row = 0; row < ROWS; row++) {
                    for (int col = 1; col < COLS; col++) {
                        if (board[row][col] != 0) {
                            int currentCol = col;
                            while (currentCol > 0 && board[row][currentCol - 1] == 0) {
                                board[row][currentCol - 1] = board[row][currentCol];
                                board[row][currentCol] = 0;
                                currentCol--;
                                moved = true;
                            }
                            if (currentCol > 0 && board[row][currentCol - 1] == board[row][currentCol]) {
                                board[row][currentCol - 1] *= 2;
                                score += board[row][currentCol - 1];
                                board[row][currentCol] = 0;
                                moved = true;
                            }
                        }
                    }
                }
                break;

            case RIGHT:
                for (int row = 0; row < ROWS; row++) {
                    for (int col = COLS - 2; col >= 0; col--) {
                        if (board[row][col] != 0) {
                            int currentCol = col;
                            while (currentCol < COLS - 1 && board[row][currentCol + 1] == 0) {
                                board[row][currentCol + 1] = board[row][currentCol];
                                board[row][currentCol] = 0;
                                currentCol++;
                                moved = true;
                            }
                            if (currentCol < COLS - 1 && board[row][currentCol + 1] == board[row][currentCol]) {
                                board[row][currentCol + 1] *= 2;
                                score += board[row][currentCol + 1];
                                board[row][currentCol] = 0;
                                moved = true;
                            }
                        }
                    }
                }
                break;

            case UP:
                for (int col = 0; col < COLS; col++) {
                    for (int row = 1; row < ROWS; row++) {
                        if (board[row][col] != 0) {
                            int currentRow = row;
                            while (currentRow > 0 && board[currentRow - 1][col] == 0) {
                                board[currentRow - 1][col] = board[currentRow][col];
                                board[currentRow][col] = 0;
                                currentRow--;
                                moved = true;
                            }
                            if (currentRow > 0 && board[currentRow - 1][col] == board[currentRow][col]) {
                                board[currentRow - 1][col] *= 2;
                                score += board[currentRow - 1][col];
                                board[currentRow][col] = 0;
                                moved = true;
                            }
                        }
                    }
                }
                break;

            case DOWN:
                for (int col = 0; col < COLS; col++) {
                    for (int row = ROWS - 2; row >= 0; row--) {
                        if (board[row][col] != 0) {
                            int currentRow = row;
                            while (currentRow < ROWS - 1 && board[currentRow + 1][col] == 0) {
                                board[currentRow + 1][col] = board[currentRow][col];
                                board[currentRow][col] = 0;
                                currentRow++;
                                moved = true;
                            }
                            if (currentRow < ROWS - 1 && board[currentRow + 1][col] == board[currentRow][col]) {
                                board[currentRow + 1][col] *= 2;
                                score += board[currentRow + 1][col];
                                board[currentRow][col] = 0;
                                moved = true;
                            }
                        }
                    }
                }
                break;
        }

        if (moved) {
            addRandomTile();
            if (!canMove()) {
                gameOver = true;
            }
             
        }
    }
    
    

    private boolean canMove() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) {
                    return true;
                }
                if (row < ROWS - 1 && board[row][col] == board[row + 1][col]) {
                    return true;
                }
                if (col < COLS - 1 && board[row][col] == board[row][col + 1]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    

    private void drawTile(Graphics2D g2d, int value, int x, int y) {
        g2d.setColor(getTileColor(value));
        g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        g2d.setColor(getTextColor(value));
        String valueStr = String.valueOf(value);
        g2d.setFont(new Font("Arial", Font.BOLD, getFontSize(value)));
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth = fm.stringWidth(valueStr);
        int stringHeight = fm.getAscent() + fm.getDescent();
        int startX = x + (TILE_SIZE - stringWidth) / 2;
        int startY = y + (TILE_SIZE - stringHeight) / 2 + fm.getAscent();
        g2d.drawString(valueStr, startX, startY);
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 2:
                return new Color(238, 228, 218);
            case 4:
                return new Color(237, 224, 200);
            case 8:
                return new Color(242, 177, 121);
            case 16:
                return new Color(245, 149, 99);
            case 32:
                return new Color(246, 124, 95);
            case 64:
                return new Color(246, 94, 59);
            case 128:
                return new Color(237, 207, 114);
            case 256:
                return new Color(237, 204, 97);
            case 512:
                return new Color(237, 200, 80);
            case 1024:
                return new Color(237, 197, 63);
            case 2048:
                return new Color(237, 194, 46);
        }
        return new Color(205, 193, 180);
    }

    private Color getTextColor(int value) {
        if (value < 8) {
            return new Color(119, 110, 101);
        }
        return new Color(249, 246, 242);
    }

    private int getFontSize(int value) {
        if (value < 128) {
            return 36;
        } else if (value < 1024) {
            return 32;
        }
        return 24;
    }

    private void resetGame() {
        board = new int[ROWS][COLS];
        score = 0;
        gameOver = false;
        initialize();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
    	 super.paintComponent(g);
    	    Graphics2D g2d = (Graphics2D) g;

    	    // Draw tiles
    	    for (int row = 0; row < ROWS; row++) {
    	        for (int col = 0; col < COLS; col++) {
    	            int x = col * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
    	            int y = row * (TILE_SIZE + TILE_MARGIN) + TILE_MARGIN;
    	            int value = board[row][col];
    	            drawTile(g2d, value, x, y);
    	        }
    	    }

    	    // Draw score
    	    g2d.setColor(new Color(60, 58, 50));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
//            g2d.drawString("Score: " + score, 20, getHeight() - 18);
    	    FontMetrics fm = g2d.getFontMetrics();
    	    String scoreStr = "Score: " + score;
    	    int stringWidth = fm.stringWidth(scoreStr);
    	    int stringHeight = fm.getHeight();
    	    int scoreX = (getWidth() - stringWidth) / 20;
    	    int scoreY = getHeight() - stringHeight - -15;
    	    g2d.drawString(scoreStr, scoreX, scoreY);

    	    if (gameOver) {
    	        g2d.setColor(new Color(119, 110, 101, 150));
    	        g2d.fillRect(0, 0, getWidth(), getHeight());

    	        g2d.setColor(new Color(249, 246, 242));
    	        g2d.setFont(new Font("Arial", Font.BOLD, 64));
    	        fm = g2d.getFontMetrics();
    	        String gameOverStr = "Game Over!";
    	        stringWidth = fm.stringWidth(gameOverStr);
    	        int gameOverX = (getWidth() - stringWidth) / 2;
    	        int gameOverY = getHeight() / 2;
    	        g2d.drawString(gameOverStr, gameOverX, gameOverY);
    	    }
    	}
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    move(Direction.LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    move(Direction.RIGHT);
                    break;
                case KeyEvent.VK_UP:
                    move(Direction.UP);
                    break;
                case KeyEvent.VK_DOWN:
                    move(Direction.DOWN);
                    break;
            }
        }
        repaint();
    }
    
    

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("2048");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new Game2048(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
