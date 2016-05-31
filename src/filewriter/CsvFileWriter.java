/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filewriter;

import collections.ProteinCollection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author arne
 */
public class CsvFileWriter {

    /**
     * Contains All samples and their results.
     */
    private final ProteinCollection protCol;

    /**
     * Constructor of the CsvFileWriter class.
     * @param protCollection allProteins
     * @throws java.io.IOException when an I/O error occurs
     */
    public CsvFileWriter(final ProteinCollection protCollection) throws IOException {
        System.out.println("Start writing");
        this.protCol = protCollection;
    }

    /**
     * Writes all results to the correct result file.
     * @param writeOption The file option to write the results to
     * @param resultFileName name of the file that should contain the results
     * @throws IOException When an I/O error occurs
     */
    public void writeToCsvFile(final String resultFileName, final Integer writeOption) throws IOException {
        // initialize the keyset (containing genenames) to proceed
        File file = new File(resultFileName);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Gene_Protein" + protCol.samplesToString() + "\n");
            for (String soName : protCol.getProteins().keySet()) {
                switch (writeOption) {
                    case 1: {
                        bw.write(protCol.getProtein(soName).uniqueToString() + "\n");
                        break;
                    }
                    case 2: {
                        bw.write(protCol.getProtein(soName).totalToString() + "\n");
                        break;
                    }
                }
            }
        }
        System.out.println("done writing to: " + resultFileName);
    }
}
