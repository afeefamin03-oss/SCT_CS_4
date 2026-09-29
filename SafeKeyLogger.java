import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class SafeKeyLogger extends JFrame implements KeyListener {

    private JTextArea textArea;
    private FileWriter logFile;
    private boolean isLogging = false;

    public SafeKeyLogger() {
        setTitle("Safe Key Logger - Demo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
        textArea.setLineWrap(true);

        // Block default typing behavior while maintaining focus
        ((AbstractDocument) textArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (isLogging) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (isLogging) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        add(new JScrollPane(textArea));
        initializeKeyListener();

        try {
            logFile = new FileWriter("keystrokes.txt", true);
            logText("\n--- New Session ---\n");
        } catch (IOException e) {
            System.err.println("Unable to create log file.");
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeKeyListener() {
        textArea.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();

        if (key == '\n' || key == '\r' || key == ' ' || key == '\b') {
            return;
        }

        if (!Character.isISOControl(key)) {
            logText(String.valueOf(key));
        }
    }

    @Override
public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
        case KeyEvent.VK_ENTER -> logText("[ENTER]\n"); // Moves subsequent typing to the next line
        case KeyEvent.VK_BACK_SPACE -> logText("[BACKSPACE]");
        case KeyEvent.VK_SPACE -> logText("[SPACE]");
    }
}

    @Override
    public void keyReleased(KeyEvent e) {
        // No action required
    }

    private void logText(String text) {
        isLogging = true;
        textArea.append(text);
        isLogging = false;

        try {
            if (logFile != null) {
                logFile.write(text);
                logFile.flush();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file.");
        }
    }

    @Override
    public void dispose() {
        try {
            if (logFile != null) {
                logText("\n--- Session Ended ---\n");
                logFile.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing log file: " + e.getMessage());
        }

        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SafeKeyLogger::new);
    }
}