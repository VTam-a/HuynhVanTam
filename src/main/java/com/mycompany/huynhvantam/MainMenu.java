package com.mycompany.huynhvantam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainMenu extends JFrame {

    private static final String SAVE_FILE = "maze_save.dat";

    public MainMenu() {
        setTitle("Maze Game - Menu Chính");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 25, 45),
                        0, getHeight(), new Color(45, 45, 75)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        JLabel title = new JLabel("MAZE GAME") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                g2d.drawString(getText(), x + 3, getFont().getSize() + 3);

                g2d.setColor(getForeground());
                g2d.drawString(getText(), x, getFont().getSize());
            }
        };
        title.setFont(new Font("Arial", Font.BOLD, 56));
        title.setForeground(new Color(255, 255, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel subtitle = new JLabel("Cac level có maze ngẫu nhiên mới!");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitle.setForeground(new Color(150, 200, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton continueBtn = createStyledButton("Tiếp Tục");
        JButton newGameBtn = createStyledButton("Chơi Mới");
        JButton leaderboardBtn = createStyledButton("Bảng Xếp Hạng");
        JButton settingsBtn = createStyledButton("Hướng Dẫn");
        JButton exitBtn = createStyledButton("Thoát Game");

        continueBtn.setEnabled(new File(SAVE_FILE).exists());
        if (!continueBtn.isEnabled()) {
            continueBtn.setBackground(new Color(100, 100, 120));
        }

        continueBtn.addActionListener(e -> continueGame());
        newGameBtn.addActionListener(e -> newGame());
        leaderboardBtn.addActionListener(e -> showLeaderboard());
        settingsBtn.addActionListener(e -> showSettings());
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(continueBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(newGameBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(leaderboardBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(settingsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(exitBtn);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        JLabel infoLabel = new JLabel("");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(180, 180, 200));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(infoLabel);

        add(panel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            private float alpha = 1.0f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color1 = isEnabled() ? new Color(70, 130, 180) : new Color(100, 100, 120);
                Color color2 = isEnabled() ? new Color(90, 150, 200) : new Color(120, 120, 140);

                if (hover && isEnabled()) {
                    color1 = new Color(90, 150, 200);
                    color2 = new Color(110, 170, 220);
                }

                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(new Color(50, 100, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);

                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (isEnabled()) {
                            hover = true;
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            repaint();
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }
                });
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 55));
        button.setPreferredSize(new Dimension(350, 55));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);

        return button;
    }

    private void continueGame() {
        try {
            GameState state = GameState.loadGame();
            dispose();
            new GameFrame(state);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Khong the tai game!" + ex.getMessage(),
                    "Loi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void newGame() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Nhập tên người chơi:");
        label.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        panel.add(label, BorderLayout.NORTH);
        panel.add(nameField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Choi Moi", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String playerName = nameField.getText().trim();
            if (!playerName.isEmpty()) {
                dispose();
                new GameFrame(new GameState(playerName));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vui long nhap ten!",
                        "Canh bao",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showLeaderboard() {
        new LeaderboardFrame(this);
    }

    private void showSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("HƯỚNG DẪN GAME");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] infos = {
            "Độ khó tăng theo level (11x11 -> 27x27)",
            "Maze ngẫu nhiên mỗi level",
            "Hoàn thành 5 level nhanh nhất để lên top",
            "Không có điểm số, chỉ tính thời gian",
            "Điều khiển: W/A/S/D hoặc mũi tên | Pause: ESC"
        };

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        for (String info : infos) {
            JLabel label = new JLabel("• " + info);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JOptionPane.showMessageDialog(this, panel, "Hướng Dẫn", JOptionPane.INFORMATION_MESSAGE);
    }
}
