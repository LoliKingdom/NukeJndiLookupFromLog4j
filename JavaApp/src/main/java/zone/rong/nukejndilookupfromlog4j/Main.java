package zone.rong.nukejndilookupfromlog4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Jars and Zips", "jar", "zip");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                try {
                    Process process = Runtime.getRuntime().exec("zip -d " + file.getAbsolutePath() + " JndiLookup.class");
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String string;
                    boolean successful = true;
                    while ((string = in.readLine()) != null) {
                        if (string.startsWith("I/O error")) {
                            JOptionPane.showMessageDialog(null, "Permission denied while trying to modify " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                            successful = false;
                        } else if (string.startsWith("zip error: Nothing to do!")) {
                            JOptionPane.showMessageDialog(null, file.getName() + " does not include JndiLookup.class.", "Warning", JOptionPane.WARNING_MESSAGE);
                            successful = false;
                        }
                    }
                    if (successful) {
                        JOptionPane.showMessageDialog(null, "Removed JndiLookup.class from " + file.getName(), "Success", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, file.getName() + " could not be modified.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
