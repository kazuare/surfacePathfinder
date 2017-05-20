package org.educationalProject.surfacePathfinder;

import javax.swing.*;
import java.io.File;

/**
 * Created by merlin on 18.05.17.
 */
public class FileChooser {
    public static String getFilePath() {
        String path = "";
        if (System.getProperty("os.name").equals("Linux"))
            path = "/home/merlin/DigDes/";
        else
            path = "C:\\digdes\\";
        JFileChooser jfc = new JFileChooser(path);
        jfc.setDialogTitle("Choose your map-file");
        int returnValue = jfc.showOpenDialog(null);


        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            path = selectedFile.getAbsolutePath();
        }
        return path;
    }
}
