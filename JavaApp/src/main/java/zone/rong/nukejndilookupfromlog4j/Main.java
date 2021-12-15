package zone.rong.nukejndilookupfromlog4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static final String JNDI_LOOKUP_PATTERN = "glob:**JndiLookup.class";

    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Jars and Zips", "jar", "zip");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                try {
                    boolean successful = removeJndiLookupZipFS(file);
                    if (successful) {
                        JOptionPane.showMessageDialog(null, "Removed JndiLookup.class from " + file.getName(), "Success", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, file.getName() + " does not include JndiLookup.class.", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, file.getName() + " could not be modified.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private static boolean removeJndiLookupZipFS(File file) throws IOException {
        boolean modified = false;
        try (FileSystem fs = FileSystems.newFileSystem(file.toPath(), null)) {
            PathMatcher jndiLookupMatcher = fs.getPathMatcher(JNDI_LOOKUP_PATTERN);
            for (Path rootDir : fs.getRootDirectories()) {

                List<Path> jndiPathList = Files.walk(rootDir)
                        .filter(jndiLookupMatcher::matches)
                        .collect(Collectors.toList());

                if (!jndiPathList.isEmpty()) {
                    modified = true;
                    for (Path jndiPath : jndiPathList) {
                        Files.delete(jndiPath);
                    }
                }
            }
            return modified;
        }
    }

}
