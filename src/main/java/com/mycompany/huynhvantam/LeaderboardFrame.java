package com.mycompany.huynhvantam;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class LeaderboardFrame extends JFrame {

    public LeaderboardFrame(JFrame parent) {
        setTitle("Bảng Xếp Hạng");
        setSize(650, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(25, 25, 45),
                        0, getHeight(), new Color(45, 45, 75)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));

        JLabel title = new JLabel("BẢNG XẾP HẠNG");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Xếp hạng theo thời gian hoàn thành 5 level");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitle.setForeground(new Color(200, 200, 220));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(title);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(subtitle);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Hạng", "Tên Nguời Chơi", "Thời Gian"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<LeaderboardEntry> entries = GameState.loadLeaderboard();

        if (entries.isEmpty()) {
            model.addRow(new Object[]{"", "Chưa có dữ liệu", ""});
        } else {
            int rank = 1;
            for (int i = 0; i < Math.min(10, entries.size()); i++) {
                LeaderboardEntry entry = entries.get(i);

                String rankIcon = "";
                if (rank == 1) {
                    rankIcon = "[1st]";
                } else if (rank == 2) {
                    rankIcon = "[2nd]";
                } else if (rank == 3) {
                    rankIcon = "[3rd]";
                } else {
                    rankIcon = "[" + rank + "]";
                }

                model.addRow(new Object[]{
                    rankIcon,
                    entry.name != null ? entry.name : "Unknown",
                    GameState.formatTime(entry.time)
                });
                rank++;
            }
        }

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    if (row == 0 && entries.size() > 0) {
                        c.setBackground(new Color(255, 215, 0, 100));
                    } else if (row == 1 && entries.size() > 1) {
                        c.setBackground(new Color(192, 192, 192, 100));
                    } else if (row == 2 && entries.size() > 2) {
                        c.setBackground(new Color(205, 127, 50, 100));
                    } else if (row % 2 == 0) {
                        c.setBackground(new Color(240, 240, 250));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                return c;
            }
        };

        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(45);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 220));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(350);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);

        JButton viewFileBtn = createStyledButton("Xem File");
        JButton refreshBtn = createStyledButton("Làm Mới");
        JButton closeBtn = createStyledButton("Đóng");

        viewFileBtn.addActionListener(e -> showFileLocation());
        refreshBtn.addActionListener(e -> {
            dispose();
            new LeaderboardFrame(parent);
        });
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(viewFileBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hover = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color color1 = new Color(70, 130, 180);
                Color color2 = new Color(90, 150, 200);

                if (hover) {
                    color1 = new Color(90, 150, 200);
                    color2 = new Color(110, 170, 220);
                }

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

        btn.setPreferredSize(new Dimension(140, 45));
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);

        return btn;
    }

    private void showFileLocation() {
        try {
            String path = new java.io.File("leaderboard.txt").getAbsolutePath();
            JTextArea textArea = new JTextArea(12, 55);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            StringBuilder content = new StringBuilder();
            content.append("Vi tri file:");
            content.append(path).append("");
            content.append("Nội dung file (Format: Tên|Thời gian):");
            content.append("=".repeat(60)).append("");

            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.FileReader("leaderboard.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("");
                }
            } catch (Exception ex) {
                content.append("Khong the doc file!");
            }

            textArea.setText(content.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane,
                    "Thong Tin File Leaderboard", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
