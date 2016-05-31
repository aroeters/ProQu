/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author arne
 */
public class SubDirectoryGetter {

    LinkedList<String> directories = new LinkedList<>();

    /**
     * Getter of all samples in the file.
     *
     * @param dir the directory to look in for all samples
     * @return a linked list with all samples
     */
    public final LinkedList<String> getSubDirs(final String dir) {
        String directory;
        if (dir.endsWith("/")) {
            directory = dir;
        } else {
            directory = dir + "/";
        }
        // Get all subdirectories
        File file = new File(directory);
        String[] dirs = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        for (String sample : dirs) {
            if (new File(directory + sample + "/protein-peptides.csv").isFile()) {
                directories.add(sample);
            }
        }
        return directories;
    }

    /**
     * Getter of the TAPP samples.
     *
     * @param TAPPFile String with the path + filename of the TAPP file
     * @return LinkedList with all sample names
     * @throws FileNotFoundException
     * @throws IOException
     */
    public final LinkedList<String> getTAPPSamples(final String TAPPFile) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(TAPPFile).getPath()));
        for (String element : br.readLine().split("\\s")) {
            if (element.contains("file")) {
                directories.add(element);
            }
        }
        return directories;
    }
}
