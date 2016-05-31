/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileparser;

import collections.ProteinCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author arne
 */
public class ProteinPeptidesFileParser {

    /**
     * Constructor of the class.
     *
     * @param inFile the path+filename that has to be parsed.
     * @param sample the name of the currently processed file
     * @param protCol
     * @param finalPeptides
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public final void getProteinQuantification(final String inFile, final String sample, final ProteinCollection protCol,
            final HashMap<String, HashSet<String>> finalPeptides)
            throws FileNotFoundException, IOException, Exception {
        System.out.println("Parsing " + sample);
        // prepare all variables
        String line;
        String[] splitLine;
        String peptide;
        String peptideNext;

        BufferedReader fp = new BufferedReader(new FileReader(new File(inFile).getPath()));
        fp.readLine();      // skip firstline
        Integer spectra;
        boolean uniquePeptide;
        while ((line = fp.readLine()) != null) {
            splitLine = line.split(",");
            peptide = splitLine[3].replaceAll("\\(\\+\\d+\\.\\d+\\)", "");
            if (peptide.contains(".")) {
                peptideNext = peptide.split("\\.")[1];
            } else {
                peptideNext = peptide;
            }
            spectra = Integer.parseInt(splitLine[14]);
            uniquePeptide = finalPeptides.get(peptideNext).size() == 1;
            for (String proteinName : finalPeptides.get(peptideNext)) {
                if (uniquePeptide) {
                    protCol.getProtein(proteinName).addUniqueSpectra(spectra, sample);
                } else {
                    protCol.getProtein(proteinName).addTotalSpectra(spectra, sample);
                }

            }
        }
    }
}
