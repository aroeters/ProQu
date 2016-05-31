/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filewriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author arne
 */
public class PeptideUniquenessWriter {
    /**
     * Writes the peptide uniqueness to a file for other applications.
     * @param uniqueness the uniqueness per peptide
     * @param resultDir the directory to write the results to
     * @throws IOException
     */
    public final void writePeptideUniqueness(final HashMap<String, HashSet<String>> uniqueness, final String resultDir) throws IOException {
        String dir;
        if (resultDir.endsWith("/") || resultDir.endsWith("\\")) {
            dir = resultDir;
        } else {
            dir = resultDir + "/";
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dir + "/peptideUniqueness.csv")));
        for (String peptide : uniqueness.keySet()) {
            if (uniqueness.get(peptide).size() == 1) {
                for (String protein : uniqueness.get(peptide)) {
                    bw.write(peptide + "," + protein + "\n");
                }
            } else if (!uniqueness.get(peptide).isEmpty()) {
                bw.write(peptide);
                for (String protein : uniqueness.get(peptide)) {
                    bw.write("," + protein);
                }
                bw.write("\n");
            }
        }
    }
}
