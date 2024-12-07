import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Pomodoro {
    private JFrame f;
    private JLabel tLbl;
    private JTextField tInp;
    private DefaultListModel<String> tListModel;
    private JList<String> tList;
    private Timer t;
    private int tRem = 25 * 60;
    private int tTot = 25 * 60;
    private CirclePanel cPanel;
    private JButton pBtn;
    private boolean isPaused = false;

    public Pomodoro() {
        f = new JFrame("Pomodoro");
        f.setSize(400, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());

        JPanel tPanel = new JPanel();
        tLbl = new JLabel(fmtTime(tRem), SwingConstants.CENTER);
        tLbl.setFont(new Font("Arial", Font.BOLD, 40));
        tPanel.add(tLbl);

        JButton sBtn = new JButton("Start");
        JButton rBtn = new JButton("Reset");
        pBtn = new JButton("Pause");
        tPanel.add(sBtn);
        tPanel.add(rBtn);
        tPanel.add(pBtn);

        sBtn.addActionListener(e -> startT());
        rBtn.addActionListener(e -> resetT());
        pBtn.addActionListener(e -> toggleP());

        JPanel mPanel = new JPanel();
        JTextField mInp = new JTextField(5);
        mInp.setText("25");
        JButton setBtn = new JButton("Set");
        setBtn.addActionListener(e -> {
            try {
                int m = Integer.parseInt(mInp.getText().trim());
                if (m > 0) {
                    tTot = m * 60;
                    tRem = tTot;
                    tLbl.setText(fmtTime(tRem));
                    cPanel.repaint();
                } else {
                    JOptionPane.showMessageDialog(f, "Enter a positive number.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(f, "Invalid input.");
            }
        });

        mPanel.add(new JLabel("Set Time:"));
        mPanel.add(mInp);
        mPanel.add(setBtn);

        JPanel taskPanel = new JPanel(new BorderLayout());
        tInp = new JTextField();
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addT());

        JPanel inpPanel = new JPanel(new BorderLayout());
        inpPanel.add(tInp, BorderLayout.CENTER);
        inpPanel.add(addBtn, BorderLayout.EAST);

        tListModel = new DefaultListModel<>();
        tList = new JList<>(tListModel);

        taskPanel.add(inpPanel, BorderLayout.NORTH);
        taskPanel.add(new JScrollPane(tList), BorderLayout.CENTER);

        JButton compBtn = new JButton("Done");
        compBtn.addActionListener(e -> compT());
        taskPanel.add(compBtn, BorderLayout.SOUTH);

        cPanel = new CirclePanel();
        tPanel.add(cPanel);

        f.add(tPanel, BorderLayout.NORTH);
        f.add(mPanel, BorderLayout.CENTER);
        f.add(taskPanel, BorderLayout.SOUTH);

        f.setVisible(true);
    }

    private void startT() {
        if (t == null) {
            t = new Timer(1000, e -> {
                if (tRem > 0) {
                    tRem--;
                    tLbl.setText(fmtTime(tRem));
                    cPanel.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                    t = null;
                    JOptionPane.showMessageDialog(f, "Time's up!");
                }
            });
            t.start();
            pBtn.setText("Pause");
            isPaused = false;
        }
    }

    private void pauseT() {
        if (t != null) {
            t.stop();
            pBtn.setText("Resume");
            isPaused = true;
        }
    }

    private void resumeT() {
        if (isPaused && t != null) {
            t.start();
            pBtn.setText("Pause");
            isPaused = false;
        }
    }

    private void toggleP() {
        if (isPaused) {
            resumeT();
        } else {
            pauseT();
        }
    }

    private void resetT() {
        if (t != null) {
            t.stop();
            t = null;
        }
        tRem = tTot;
        tLbl.setText(fmtTime(tRem));
        cPanel.repaint();
        pBtn.setText("Pause");
        isPaused = false;
    }

    private void addT() {
        String task = tInp.getText().trim();
        if (!task.isEmpty()) {
            tListModel.addElement(task);
            tInp.setText("");
        }
    }

    private void compT() {
        int idx = tList.getSelectedIndex();
        if (idx != -1) {
            tListModel.remove(idx);
        }
    }

    private String fmtTime(int s) {
        int m = s / 60;
        int sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }

    private class CirclePanel extends JPanel {
        private static final int R = 100;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            Color c1 = new Color(70, 130, 180);
            Color c2 = new Color(0, 0, 139);
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int x = (getWidth() - R * 2) / 2;
            int y = (getHeight() - R * 2) / 2;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float prog = 1.0f - (float) tRem / tTot;
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillArc(x, y, R * 2, R * 2, 90, 360);

            g2d.setColor(Color.GREEN);
            g2d.fillArc(x, y, R * 2, R * 2, 90, (int) (prog * 360));

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(5));
            g2d.drawArc(x, y, R * 2, R * 2, 90, 360);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Pomodoro::new);
    }
}
