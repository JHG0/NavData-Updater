package io.github.glott.NavDataUpdater;

import javax.naming.ConfigurationException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
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
    private SwingWorker worker;

    private ErrorDialog ed = new ErrorDialog();

    public Display()
    {
        closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (worker != null) worker.cancel(true);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        updateNavDataButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                worker = new SwingWorker<Void, Void>()
                {
                    @Override
                    protected Void doInBackground() throws Exception
                    {
                        updateNavDataButton.setEnabled(false);
                        updateNavData();
                        return null;
                    }

                    @Override
                    protected void done()
                    {
                        updateNavDataButton.setEnabled(true);
                    }
                };
                worker.execute();
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

    public List<String> getExceptions()
    {
        try
        {
            File vSTARS = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\vSTARS\\NavDataExceptions.txt");
            File vERAM = new File(System.getProperty("user.home") + "\\AppData\\Local\\vERAM\\NavDataExceptions.txt");
            File f;
            if (vSTARS.exists()) f = vSTARS;
            else if (vERAM.exists()) f = vERAM;
            else return new ArrayList<>();
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
        return new ArrayList<>();
    }

    public void updateNavData()
    {
        ArrayList<String> exceptions = new ArrayList<>();
        DataHandler dh = new DataHandler(getExceptions());
        long a = -1;
        try
        {
            a = dh.updateAirports();
        } catch (Exception ex)
        {
            if (ex instanceof FileNotFoundException)
                ed.displayError("There was an error locating vSTARS/vERAM files.");
            else if (ex instanceof MalformedURLException)
                ed.displayError("There was an error fetching NavData from the internet.");
            else if (ex instanceof ConfigurationException)
                ed.displayError("There was an error writing to vSTARS/vERAM files.");
            else
                ed.displayError("There was an error parsing NavData.");
        }

        long w = -1;
        try
        {
            w = dh.updateWaypoints();
        } catch (Exception ex)
        {
            if (ex instanceof FileNotFoundException)
                ed.displayError("There was an error locating vSTARS/vERAM files.");
            else if (ex instanceof MalformedURLException)
                ed.displayError("There was an error fetching NavData from the internet.");
            else if (ex instanceof ConfigurationException)
                ed.displayError("There was an error writing to vSTARS/vERAM files.");
            else
                ed.displayError("There was an error parsing NavData.");
        }
        if (a == -1 || w == -1)
            outputMS.setText("-1");
        else
            outputMS.setText("" + (a + w));
    }
}
