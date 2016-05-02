package io.github.jhg0.NavDataUpdater;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Display
{
    @SuppressWarnings("All")
    private static JFrame frame;
    private JPanel panel;
    private JButton closeButton;
    private JButton updateNavDataButton;
    private JButton saveExceptionsButton;
    private JButton importExceptionsButton;
    private JButton setExceptionsButton;
    private JTextField outputMS;

    public Display()
    {
        closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        updateNavDataButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ArrayList<String> exceptions = new ArrayList<String>();
                exceptions.add("KMIA");
                exceptions.add("AR*|Airport");
                exceptions.add("WWWWW|12.32|44.21");
                DataHandler dh = new DataHandler(exceptions);
                long out = dh.updateData();
                if (out > 0)
                    outputMS.setText("" + out);
                else
                {
                    ErrorDialog ed = new ErrorDialog();
                    if (out == -1)
                        ed.displayError("There was an error parsing NavData.");
                    else if (out == -2)
                        ed.displayError("There was an error fetching NavData from the internet.");
                    else if (out == -3)
                        ed.displayError("There was an error locating vSTARS/vERAM files.");
                    outputMS.setText("-1");
                }
            }
        });

    }

    public void run()
    {
        frame = new JFrame("NavData Updater");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
