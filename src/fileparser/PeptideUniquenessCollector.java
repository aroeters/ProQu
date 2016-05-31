/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileparser;

import callable.CallablePeptideMatcher;
import collections.PeptideCollection;
import collections.ProteinCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

/**
 *
 * @author arne
 */
public class PeptideUniquenessCollector {

    /**
     * All unique peptides that have to be searched for.
     */
    private final HashSet<String> peptides = new HashSet<>();

    /**
     * Adds all peptides to the list.
     *
     * @param fileName name of the file to parse
     * @throws FileNotFoundException when the file is not found
     * @throws IOException when an I/O error occurs
     */
    public final void addPeptides(final String fileName) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName).getPath()));
        br.readLine();  // skip first line
        String line;    // store the line
        String peptide; // store the peptide
        while ((line = br.readLine()) != null) {
            peptide = line.split(",")[3].replaceAll("\\(\\+\\d+\\.\\d+\\)", "");
            if (peptide.contains(".")) {
                peptides.add(peptide.split("\\.")[1]);
            } else {
                peptides.add(peptide);
            }
        }
    }

    /**
     * Getter of the peptide matches.
     *
     * @param protCol protein collection to match against
     * @param threads the number of threads to use
     * @param geneSet True if searching for genes
     * @param pepCol the peptide collection
     * @param peptideSet if a TAPP output file is used this will be filled, else
     * this is null
     * @return a HashMap with the peptide as a key and the proteins or genes in
     * a HashSet in a the value
     * @throws Exception
     */
    public final HashMap<String, HashSet<String>> getPeptideMatches(final ProteinCollection protCol, final int threads,
            final boolean geneSet, final PeptideCollection pepCol, final HashSet<String> peptideSet) throws Exception {
        Set<Future<HashMap<String, HashSet<String>>>> peptideToProtein;
        if (peptideSet != null) {
            System.out.println("Collected " + peptideSet.size() + " unique peptides.");
            CallablePeptideMatcher cpm = new CallablePeptideMatcher(protCol);
            peptideToProtein = cpm.matchPeptides(threads, peptideSet, geneSet, pepCol);

        } else {
            System.out.println("Collected " + peptides.size() + " unique peptides.");
            CallablePeptideMatcher cpm = new CallablePeptideMatcher(protCol);
            peptideToProtein = cpm.matchPeptides(threads, peptides, geneSet, pepCol);
        }
        HashMap<String, HashSet<String>> finalPeptides = new HashMap<>();
        for (Future<HashMap<String, HashSet<String>>> future : peptideToProtein) {
            finalPeptides.putAll(future.get());
        }
        return finalPeptides;
    }
}
