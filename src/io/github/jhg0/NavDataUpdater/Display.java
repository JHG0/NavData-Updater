package io.github.jhg0.NavDataUpdater;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class Display
{

    private static JFrame frame;
    private JPanel panel;
    private JButton closeButton;
    private JButton updateNavDataButton;
    private JTextField outputMS;

    private ErrorDialog ed = new ErrorDialog();

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
                DataHandler dh = new DataHandler(getExceptions());
                long out = dh.updateData();
                if (out > 0)
                    outputMS.setText("" + out);
                else
                {
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

    private List<String> getExceptions()
    {
        try
        {
            File vSTARS = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vSTARS\\NavData\\NavDataExceptions.txt");
            File vERAM = new File(System.getProperty("user.home") + "\\AppData\\Local\\vERAM\\NavDataExceptions.txt");
            File f;
            if (vSTARS.exists()) f = vSTARS;
            else if (vERAM.exists()) f = vERAM;
            else return new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            String s = "";
            while (line != null)
            {
                s += line + "\n";
                line = br.readLine();
            }
            br.close();
            return Arrays.asList(s.split("\n"));
        } catch (Exception ignored)
        {
        }
        return new ArrayList<String>();
    }
}
