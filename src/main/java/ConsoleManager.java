import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, отвечающий за создание окна консоли
 *
 * @author Pablo
 * @created 20.02.2020
 */
public class ConsoleManager extends JFrame {

    /**
     * Console window
     */
    private JTextPane console;
    private JTextField inputLine;

    private StyledDocument document;

    boolean trace = false;

    List<String> recentUsed = new ArrayList<>();
    int recentUsedId = 0;
    int recentUsedMaximum = 10;


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        new ConsoleManager();
    }

    ConsoleManager() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


        JFrame jFrame = new JFrame();
        jFrame.setTitle("Pablo SM");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        console = new JTextPane();
        console.setEditable(false);
        console.setOpaque(false);
        console.setFont(new Font("Courier New", Font.PLAIN, 14));

        inputLine = new JTextField();
        inputLine.setEditable(true);
        inputLine.setCaretColor(Color.BLUE);

        document = console.getStyledDocument();

        inputLine.addActionListener(e -> {
            String text = inputLine.getText();

            if (!text.isEmpty()) {
                recentUsed.add(text);
                recentUsedId = 0;

                doCommand(text);
                scrollToBottom();
                inputLine.selectAll();
            }
        });

        inputLine.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (recentUsedId < (recentUsedMaximum - 1) && recentUsedId < (recentUsed.size() - 1)) {
                        recentUsedId++;
                    }
                    inputLine.setText(recentUsed.get(recentUsed.size() - 1 - recentUsedId));
                } else {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (recentUsedId > 0) {
                            recentUsedId--;
                        }
                        inputLine.setText(recentUsed.get(recentUsed.size() - 1 - recentUsedId));
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        jFrame.add(inputLine, BorderLayout.SOUTH);
        jFrame.add(scrollPane, BorderLayout.CENTER);

        jFrame.getContentPane().setBackground(new Color(192, 192, 192));

        jFrame.setSize(500, 377);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);

        Image imageIcon = new ImageIcon("data/img/console-icon.png").getImage();

        jFrame.setIconImage(imageIcon);
        jFrame.setVisible(true);

    }

    public void scrollToTop() {
        console.setCaretPosition(0);
    }

    public void scrollToBottom() {
        console.setCaretPosition(console.getDocument().getLength());
    }

    private void print(String text, boolean trace) {
        print(text, true, new Color(255, 255, 255));
    }

    private void print(String text, boolean trace, Color color) {
        Style style = console.addStyle("Style", null);
        StyleConstants.setForeground(style, color);

        if (trace) {
            Throwable throwable = new Throwable();
            StackTraceElement[] stackTraceElement = throwable.getStackTrace();
            String caller = stackTraceElement[0].getClassName();

            text = caller + "->" + text;
        }

        try {
            document.insertString(document.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void println(String text, boolean trace) {
        println(text, trace, new Color(255, 255, 255));
    }

    public void println(String text, boolean trace, Color c) {
        print(text + "\n", trace, c);
    }

    private void clear() {
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void doCommand(String text) {
        final String[] commands = text.split(" ");

        String mainCommand = commands[0];
        if (commands[0].equalsIgnoreCase("clear")) {
            clear();
        } else if (commands[0].equalsIgnoreCase("start")) {
            String message = "";

            for (int i = 0; i < commands.length; i++) {
                message += commands[i];

                if (i != commands.length - 1) {
                    message += " ";
                }
                JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            println(text, trace, new Color(255, 255, 255));
        }
    }
}
