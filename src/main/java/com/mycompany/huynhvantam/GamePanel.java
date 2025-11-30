package com.mycompany.huynhvantam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class GamePanel extends JPanel {

    private int[][] maze;
    private int playerX, playerY;
    private int cellSize;
    private GameState gameState;
    private GameFrame gameFrame;
    private boolean isPaused = false;

    private static final int PANEL_SIZE = 700;

    public GamePanel(GameState gameState, GameFrame gameFrame) {
        this.gameState = gameState;
        this.gameFrame = gameFrame;
        loadLevel(gameState.getCurrentLevel());

        setFocusable(true);
        setBackground(new Color(245, 245, 245));
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setMinimumSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setMaximumSize(new Dimension(PANEL_SIZE, PANEL_SIZE));

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                    return;
                }
                if (!isPaused) {
                    handleMovement(e.getKeyCode());
                }
            }
        });
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void loadLevel(int level) {
        System.out.println("=== Tao maze ngau nhien cho Level " + level + " ===");
        maze = MazeMap.getMap(level);

        int maxDimension = Math.max(maze.length, maze[0].length);
        cellSize = PANEL_SIZE / maxDimension;

        System.out.println("Maze size: " + maze.length + "x" + maze[0].length);
        System.out.println("Cell size: " + cellSize);
        System.out.println("Actual draw size: " + (maze[0].length * cellSize) + "x" + (maze.length * cellSize));

        boolean foundStart = false;
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (maze[i][j] == MazeMap.START) {
                    playerX = j;
                    playerY = i;
                    maze[i][j] = MazeMap.PATH;
                    foundStart = true;
                    break;
                }
            }
            if (foundStart) {
                break;
            }
        }

        if (!foundStart) {
            playerX = 1;
            playerY = 1;
            maze[1][1] = MazeMap.PATH;
        }

        revalidate();
        repaint();
    }

    private void handleMovement(int keyCode) {
        int newX = playerX, newY = playerY;

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newY--;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newY++;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newX--;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newX++;
                break;
            default:
                return;
        }

        if (newY >= 0 && newY < maze.length && newX >= 0 && newX < maze[0].length) {
            if (maze[newY][newX] != MazeMap.WALL) {
                playerX = newX;
                playerY = newY;

                if (maze[newY][newX] == MazeMap.END) {
                    levelComplete();
                }
                repaint();
            }
        }
    }

    private void levelComplete() {
        if (gameState.getCurrentLevel() < 5) {
            String message = String.format(
                    "HOÀN THÀNH LEVEL %d!"
                    + "Thời gian hiện tại: %s"
                    + "Chuyển sang Level %d?",
                    gameState.getCurrentLevel(),
                    GameState.formatTime(gameState.getPlayTime()),
                    gameState.getCurrentLevel() + 1
            );

            int choice = JOptionPane.showConfirmDialog(this,
                    message,
                    "Level Complete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                gameState.nextLevel();
                loadLevel(gameState.getCurrentLevel());
                gameFrame.updateInfo();
                repaint();
                requestFocusInWindow();
            } else {
                gameState.saveGame();
                gameFrame.stopTimer();
                gameFrame.dispose();
                new MainMenu();
            }
        } else {
            long completionTime = gameState.getPlayTime();

            gameState.nextLevel();
            gameState.saveToLeaderboard();

            String message = String.format(
                    "CHÚC MỪNG!"
                    + "Người chơi: %s"
                    + "Bạn đã hoàn thành tất cả 5 level!"
                    + "THỜI GIAN HOÀN THÀNH:"
                    + "%s"
                    + "Kết quả đã được lưu vào bảng xếp hạng!", gameState.getPlayerName(), GameState.formatTime(completionTime)
            );

            JOptionPane.showMessageDialog(this,
                    message,
                    "Hoàn Thành Game",
                    JOptionPane.INFORMATION_MESSAGE);

            File saveFile = new File("maze_save.dat");
            if (saveFile.exists()) {
                saveFile.delete();
            }

            gameFrame.stopTimer();
            gameFrame.dispose();
            new MainMenu();
        }
    }

    private void togglePause() {
        isPaused = !isPaused;
        gameFrame.setPauseVisible(isPaused);
        repaint();
        if (!isPaused) {
            requestFocusInWindow();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int mazeWidth = maze[0].length * cellSize;
        int mazeHeight = maze.length * cellSize;
        int offsetX = (PANEL_SIZE - mazeWidth) / 2;
        int offsetY = (PANEL_SIZE - mazeHeight) / 2;

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                int x = offsetX + j * cellSize;
                int y = offsetY + i * cellSize;

                if (maze[i][j] == MazeMap.WALL) {
                    GradientPaint gradient = new GradientPaint(
                            x, y, new Color(40, 40, 40),
                            x, y + cellSize, new Color(60, 60, 60)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(x, y, cellSize, cellSize, 5, 5);

                    g2d.setColor(new Color(80, 80, 80));
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(x, y, cellSize, cellSize, 5, 5);

                } else if (maze[i][j] == MazeMap.END) {
                    GradientPaint gradient = new GradientPaint(
                            x, y, new Color(76, 175, 80),
                            x, y + cellSize, new Color(56, 142, 60)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(x, y, cellSize, cellSize, 8, 8);

                    g2d.setColor(new Color(139, 195, 74));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(x + 1, y + 1, cellSize - 2, cellSize - 2, 8, 8);

                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, Math.max(8, cellSize / 3)));
                    String endText = "END";
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = x + (cellSize - fm.stringWidth(endText)) / 2;
                    int textY = y + (cellSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(endText, textX, textY);

                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x, y, cellSize, cellSize);
                    g2d.setColor(new Color(230, 230, 230));
                    g2d.drawRect(x, y, cellSize, cellSize);
                }
            }
        }

        int playerCenterX = offsetX + playerX * cellSize + cellSize / 2;
        int playerCenterY = offsetY + playerY * cellSize + cellSize / 2;
        int playerRadius = Math.max(3, cellSize / 2 - 4);

        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(playerCenterX - playerRadius + 3, playerCenterY - playerRadius + 3,
                playerRadius * 2, playerRadius * 2);

        GradientPaint playerGradient = new GradientPaint(
                playerCenterX, playerCenterY - playerRadius, new Color(66, 165, 245),
                playerCenterX, playerCenterY + playerRadius, new Color(33, 150, 243)
        );
        g2d.setPaint(playerGradient);
        g2d.fillOval(playerCenterX - playerRadius, playerCenterY - playerRadius,
                playerRadius * 2, playerRadius * 2);

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillOval(playerCenterX - playerRadius / 2, playerCenterY - playerRadius / 2,
                playerRadius, playerRadius);

        g2d.setColor(new Color(25, 118, 210));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(playerCenterX - playerRadius, playerCenterY - playerRadius,
                playerRadius * 2, playerRadius * 2);

        if (isPaused) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 70));
            String pauseText = "PAUSED";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(pauseText);

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(pauseText, (getWidth() - textWidth) / 2 + 3, getHeight() / 2 - 17);

            g2d.setColor(Color.WHITE);
            g2d.drawString(pauseText, (getWidth() - textWidth) / 2, getHeight() / 2 - 20);

            g2d.setFont(new Font("Arial", Font.PLAIN, 22));
            String hint = "Nhấn ESC để tiếp tục";
            textWidth = g2d.getFontMetrics().stringWidth(hint);
            g2d.drawString(hint, (getWidth() - textWidth) / 2, getHeight() / 2 + 40);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PANEL_SIZE, PANEL_SIZE);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
