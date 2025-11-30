package com.mycompany.huynhvantam;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private GameState gameState;
    private GamePanel gamePanel;
    private JLabel infoLabel;
    private JPanel pauseGlassPane;
    private Timer updateTimer;

    public GameFrame(GameState gameState) {
        this.gameState = gameState;

        setTitle("Maze Game - Level " + gameState.getCurrentLevel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(30, 30, 50),
                        0, getHeight(), new Color(40, 40, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setPreferredSize(new Dimension(700, 40));

        infoLabel = new JLabel();
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        updateInfo();
        topPanel.add(infoLabel);
        add(topPanel, BorderLayout.NORTH);

        gamePanel = new GamePanel(gameState, this);
        gamePanel.setPreferredSize(new Dimension(700, 700));
        add(gamePanel, BorderLayout.CENTER);

        pauseGlassPane = createPauseGlassPane();
        setGlassPane(pauseGlassPane);

        updateTimer = new Timer(1000, e -> {
            if (!gamePanel.isPaused()) {
                updateInfo();
            }
        });
        updateTimer.start();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    private JPanel createPauseGlassPane() {
        JPanel glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isVisible()) {
                    return;
                }

                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                int boxW = 320;
                int boxH = 280;
                int boxX = (getWidth() - boxW) / 2;
                int boxY = (getHeight() - boxH) / 2;

                g2d.setColor(new Color(50, 50, 70, 250));
                g2d.fillRoundRect(boxX, boxY, boxW, boxH, 25, 25);

                g2d.setColor(new Color(70, 130, 180));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(boxX, boxY, boxW, boxH, 25, 25);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 42));
                String title = "PAUSED";
                FontMetrics fm = g2d.getFontMetrics();
                int titleX = (getWidth() - fm.stringWidth(title)) / 2;
                g2d.drawString(title, titleX, boxY + 70);
            }
        };

        glassPane.setOpaque(false);
        glassPane.setLayout(null);

        JButton resumeBtn = createStyledButton("Tiếp Tục");
        JButton menuBtn = createStyledButton("Menu Chính");

        resumeBtn.addActionListener(e -> setPauseVisible(false));
        menuBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn lưu tiến trình trước khi thoát?",
                    "Xác nhận",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                gameState.saveGame();
                dispose();
                updateTimer.stop();
                new MainMenu();
            } else if (choice == JOptionPane.NO_OPTION) {
                dispose();
                updateTimer.stop();
                new MainMenu();
            }
        });

        glassPane.add(resumeBtn);
        glassPane.add(menuBtn);

        glassPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = glassPane.getWidth();
                int h = glassPane.getHeight();
                int btnW = 200;
                int btnH = 45;

                resumeBtn.setBounds(w / 2 - btnW / 2, h / 2 + 30, btnW, btnH);
                menuBtn.setBounds(w / 2 - btnW / 2, h / 2 + 90, btnW, btnH);
            }
        });

        return glassPane;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hover = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color1 = hover ? new Color(90, 150, 200) : new Color(70, 130, 180);
                Color color2 = hover ? new Color(110, 170, 220) : new Color(90, 150, 200);

                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(50, 100, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);

                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        hover = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        hover = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }
                });
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);

        return btn;
    }

    public void updateInfo() {
        String timeStr = GameState.formatTime(gameState.getPlayTime());
        infoLabel.setText("Người chơi: " + gameState.getPlayerName()
                + " | Level: " + gameState.getCurrentLevel()
                + " | Thời gian: " + timeStr);
        setTitle("Maze Game - Level " + gameState.getCurrentLevel());
    }

    public void setPauseVisible(boolean visible) {
        pauseGlassPane.setVisible(visible);
        if (!visible) {
            gamePanel.requestFocusInWindow();
        }
    }

    public void stopTimer() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}
