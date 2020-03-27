package io.github.glott.NavDataUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

@SuppressWarnings("UnusedDeclaration")
public class ErrorDialog
{
    private static JFrame frame;
    private JPanel panel;
    private JLabel outputText;
    private JButton closeButton;

    public ErrorDialog()
    {
        closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    public void displayError(String text)
    {
        frame = new JFrame("Error");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        outputText.setBackground(panel.getBackground());
        outputText.setText(text);
        outputText.setFont(new Font(closeButton.getFont().getName(), Font.BOLD, 14));
        outputText.setBorder(null);
    }
}
