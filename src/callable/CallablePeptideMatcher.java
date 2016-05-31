/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package callable;

import collections.PeptideCollection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import objects.Protein;
import collections.ProteinCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author arne
 */
public class CallablePeptideMatcher {

    /**
     * All proteins to match the peptides against.
     */
    private final ProteinCollection protCol;

    /**
     * Initiates the class.
     *
     * @param protCol all protein objects to searchPattern against
     * @throws Exception when an Error occurs
     */
    public CallablePeptideMatcher(final ProteinCollection protCol) throws Exception {
        this.protCol = protCol;
    }

    /**
     * Subclass that can be called and used in a thread pool.
     */
    public static class CallableMatcher implements Callable {

        /**
         * The peptide that is searched for in the proteins, does not contain
         * the amino acid before and after.
         */
        private final String peptide;
        /**
         * The total protein database to searchPattern the peptide in.
         */
        private final ProteinCollection protCol;
        /**
         * True if gene set.
         */
        private final boolean geneSet;
        /**
         * PeptideCollection.
         */
        private PeptideCollection pepCol;

        /**
         * Initiates the subclass.
         *
         * @param peptideEntry the peptide to searchPattern for in the proteins.
         * @param protCol the whole protein collection
         * @param geneSet checks if its a gene set
         * @param peptideCol the peptide collection
         */
        public CallableMatcher(final String peptideEntry, final ProteinCollection protCol,
                final Boolean geneSet, final PeptideCollection peptideCol) {
            this.peptide = peptideEntry;
            this.protCol = protCol;
            this.geneSet = geneSet;
            this.pepCol = peptideCol;
        }

        @Override
        public final HashMap<String, HashSet<String>> call() {
            boolean added = false;
            boolean NEWP = true;
            HashSet<String> peptideMatches = new HashSet<>();
            for (Protein protein : this.protCol.getProteins().values()) {
                if (protein.checkPeptide(this.pepCol.getPeptideIndex(peptide))) {
                    if (geneSet) {
                        if (!added) {
                            if (!protein.getGeneID().startsWith("NEWP")) {
                                added = true;
                                NEWP = false;
                            }
                        }
                        peptideMatches.add(protein.getGeneID());
                    } else {
                        if (!added) {
                            if (!protein.getGeneID().startsWith("NEWP")) {
                                added = true;
                                NEWP = false;
                            }
                        }
                        peptideMatches.add(protein.getProteinID());
                    }
                }
            }
            if (added) {
                for (Iterator<String> i = peptideMatches.iterator(); i.hasNext();) {
                    if (i.next().startsWith("NEWP")) {
                        i.remove();
                    }
                }
            } else if (NEWP) {
                ArrayList<String> toRemove = new ArrayList<>();
                Integer length = 0;
                String previousName = "";
                for (String protein : peptideMatches) {
                    if (protCol.getProtein(protein).getAALength() > length) {
                        toRemove.add(previousName);
                        previousName = protein;
                    } else {
                        toRemove.add(protein);
                    }
                    
                }
                peptideMatches.removeAll(toRemove);
            }
            HashMap<String, HashSet<String>> hm = new HashMap<>();
            hm.put(peptide, peptideMatches);
            return hm;
        }
    }

    /**
     * Responsible for the multi threading and parsing.
     *
     * @param threadNumber Threads to use
     * @param peptides peptides to match
     * @param geneSet if its a geneSet or not
     * @param pepCol the peptide collection
     * @return the protein Collection
     * @throws Exception when an Error occurs
     */
    public final Set<Future<HashMap<String, HashSet<String>>>> matchPeptides(final Integer threadNumber,
            final HashSet<String> peptides, final Boolean geneSet, final PeptideCollection pepCol) throws Exception {
        // Creates a thread pool with the max number of threads that can be used.
        ExecutorService pool = Executors.newScheduledThreadPool(threadNumber);
        // creates a set with future objects which can be accessed after the all processes are completed
        Set<Future<HashMap<String, HashSet<String>>>> set = new HashSet<>();
        Callable<HashMap<String, HashSet<String>>> callable;

        for (String peptide : peptides) {
            callable = new CallableMatcher(peptide, this.protCol, geneSet, pepCol);
            Future<HashMap<String, HashSet<String>>> future = pool.submit(callable);
            set.add(future);
        }
        // shuts down the threads or else the will keep running
        pool.shutdown();
        return set;
    }
}
