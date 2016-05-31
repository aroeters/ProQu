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
import java.util.LinkedList;

/**
 *
 * @author arne
 */
public class TAPPFileParser {

    /**
     * The file of TAPP to parse.
     */
    private final File TAPPFile;
    /**
     * All unique peptides of the TAPP file.
     */
    private final HashSet<String> peptides = new HashSet<>();

    /**
     * Constructor of the class.
     *
     * @param TAPPFileInput the TAPP file
     */
    public TAPPFileParser(final String TAPPFileInput) {
        TAPPFile = new File(TAPPFileInput);
    }

    /**
     * Getter of all unique peptides of the TAPP output file.
     *
     * @return a HashSet with all peptides in it
     * @throws FileNotFoundException
     * @throws IOException
     */
    public final HashSet<String> getUniquePeptides() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(TAPPFile.getPath()));
        br.readLine(); // skip first line
        String line;
        String[] splitLine;
        String[] splitElement;
        while ((line = br.readLine()) != null) {
            splitLine = line.split("\\s");
            for (int i = 1; i > 0 && i < splitLine.length; i++) {
                if (splitLine[i].contains("}")) {
                    splitElement = splitLine[i].split("}");
                    for (String element : splitElement) {
                        if (!element.equals("-")) {
                            peptides.add(element);
                        }
                    }
                } else if (!splitLine[i].equals("-") && !splitLine[i].equals("")) {
                    peptides.add(splitLine[i]);
                }
            }
        }
        return peptides;
    }

    /**
     * Gets the protein quantification for the given TAPP file.
     *
     * @param protCol the protein collection to do the protein quantification
     * for
     * @param peptideUniqueness the uniqueness of the peptides
     * @throws FileNotFoundException
     * @throws IOException
     */
    public final void getProteinQuantification(final ProteinCollection protCol, final HashMap<String, HashSet<String>> peptideUniqueness)
            throws FileNotFoundException, IOException {
        System.out.println("Starting TAPP file protein quantification");
        BufferedReader br;
        String line;
        String linePart;
        String[] splitLinePart;
        boolean matches;
        LinkedList<String> samples = protCol.getSampleOrder();
        for (int i = 0; i < protCol.getSampleOrder().size(); i++) {
            String sample = samples.get(i);
            System.out.println("sample: " + sample + " getting parsed");
            br = new BufferedReader(new FileReader(TAPPFile.getPath()));
            br.readLine(); //skip first line
            while ((line = br.readLine()) != null) {
                linePart = line.split("\\s")[i+1];
                if (linePart.contains("}")) {
                    splitLinePart = linePart.split("}");
                    for (String peptide : splitLinePart) {
                        if (!peptide.equals("-")) {
                            matches = peptideUniqueness.get(peptide).size() == 1;
                            for (String proteinName : peptideUniqueness.get(peptide)) {
                                if (matches) {
                                    protCol.getProtein(proteinName).addUniqueSpectra(1, sample);
                                } else {
                                    protCol.getProtein(proteinName).addTotalSpectra(1, sample);
                                }

                            }
                        }
                    }
                } else {
                    if (!linePart.equals("-") && !linePart.equals("")) {
                        matches = peptideUniqueness.get(linePart).size() == 1;
                        for (String proteinName : peptideUniqueness.get(linePart)) {
                            if (matches) {
                                protCol.getProtein(proteinName).addUniqueSpectra(1, sample);
                            } else {
                                protCol.getProtein(proteinName).addTotalSpectra(1, sample);
                            }

                        }
                    }
                }

            }

        }

    }
}
