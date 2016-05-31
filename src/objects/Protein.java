/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 *
 * @author arne
 */
public class Protein {
    /**
     * The ID of the protein.
     */
    private String proteinID;
    /**
     * The gene ID from ENSEMBL.
     */
    private String geneID = null;
    /**
     * The Amino Acid sequence.
     */
    private String aminoSeq;
    /**
     * The unique number of spectra.
     */
    private final LinkedHashMap<String, Integer> spectraUnique = new LinkedHashMap<>();
    /**
     * contains the total number of Spectra.
     */
    private final LinkedHashMap<String, Integer> spectraTotal = new LinkedHashMap<>();
    /**
     * All peptides for the protein.
     */
    private HashSet<Integer> peptides = new HashSet<>();
    private Integer seqLength = 0;
    /**
     * checks if the peptide is present in the peptides of the protein.
     *
     * @param peptide String with AA seq
     * @return true if peptide in peptides of protein
     */
    public final boolean checkPeptide(final Integer peptide) {
        return this.peptides.contains(peptide);
    }

    /**
     * Setter of the peptide set.
     *
     * @param peptideSet the set with peptides.
     */
    public final void setPeptides(final HashSet<Integer> peptideSet) {
        this.peptides = peptideSet;
    }
    /**
     * Adds a peptide index to the set.
     * @param peptide index of a peptide
     */
    public final void addPeptide(final Integer peptide) {
        this.peptides.add(peptide);
    }
    /**
     * Getter of the peptides set.
     *
     * @return HashSet with peptides
     */
    public final HashSet<Integer> getPeptides() {
        return this.peptides;
    }

    /**
     * Merges the sets of peptides together.
     *
     * @param otherPeptides HashSet with peptides
     */
    public final void mergePeptides(final HashSet<Integer> otherPeptides) {
        this.peptides.addAll(otherPeptides);
    }

    /**
     * Returns the protein ID.
     *
     * @return protein ID
     */
    public final String getProteinID() {
        return proteinID;
    }
    public final void clearAASeq() {
        this.aminoSeq = null;
    }
    /**
     * Sets the protein ID.
     *
     * @param proteinID String of the protein ID.
     */
    public final void setProteinID(final String proteinID) {
        this.proteinID = proteinID;
    }
    /**
     * Getter of the gene ID.
     *
     * @return String of the gene ID.
     */
    public final String getGeneID() {
        return geneID;
    }

    /**
     * Setter of the gene ID.
     *
     * @param geneID String of the gene ID.
     */
    public final void setGeneID(final String geneID) {
        this.geneID = geneID;
    }

    /**
     * Getter of the amino acid sequence.
     *
     * @return the AA sequence
     */
    public final String getAASeq() {
        return aminoSeq;
    }

    /**
     * Setter of the AA sequence.
     *
     * @param AASeq String of the AA sequence
     */
    public final void setAASeq(final String AASeq) {
        if (this.aminoSeq == null) {
            this.aminoSeq = AASeq;
        } else {
            this.aminoSeq += AASeq.replaceAll("[\n]\\s+", "");
        }
       this.seqLength = this.aminoSeq.length();
    }

    /**
     * Returns the spectral count.
     *
     * @param name name of the sample
     * @return Integer spectral count.
     */
    public final Integer getUniqueSpectra(final String name) {
        return spectraUnique.get(name);
    }
    /**
     * Getter of the length of the AA sequence.
     * @return Integer AA seq length
     */
    public final Integer getAALength() {
        return this.seqLength;
    }
    /**
     * Adds to the spectra_unique of the protein.
     *
     * @param newSpectra spectra_unique to add
     * @param name name of the sample
     */
    public final void addUniqueSpectra(final Integer newSpectra, final String name) {
        if (spectraUnique.containsKey(name)) {
            spectraUnique.put(name, spectraUnique.get(name) + newSpectra);
        } else {
            spectraUnique.put(name, newSpectra);
        }
        if (spectraTotal.containsKey(name)) {
            spectraTotal.put(name, spectraTotal.get(name) + newSpectra);
        } else {
            spectraTotal.put(name, newSpectra);
        }
    }

    /**
     * Returns the spectral count.
     *
     * @param name name of the sample
     * @return Integer spectral count.
     */
    public Integer getTotalSpectra(final String name) {
        return spectraUnique.get(name);
    }

    /**
     * Adds to the spectra_unique of the protein.
     *
     * @param newSpectra spectra_unique to add
     * @param name name of the sample
     */
    public void addTotalSpectra(final Integer newSpectra, final String name) {
        if (spectraTotal.containsKey(name)) {
            spectraTotal.put(name, spectraTotal.get(name) + newSpectra);
        } else {
            spectraTotal.put(name, newSpectra);
        }
    }

    /**
     * Initiate the class with the name given.
     *
     * @param samples names of all samples
     */
    public Protein(final LinkedList<String> samples) {
        for (String sample : samples) {
            spectraTotal.put(sample.intern(), 0);
            spectraUnique.put(sample.intern(), 0);
        }
    }

    /**
     * To string function of the total spectra.
     *
     * @return String with all total spectra
     */
    public final String totalToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(proteinID);
        for (String nameKey : spectraTotal.keySet()) {
            if (spectraTotal.get(nameKey) != null) {
                sb.append("\t" + spectraTotal.get(nameKey));
            } else {
                sb.append("\t0");
            }
        }
        return sb.toString();
    }

    /**
     * to string function of the unique spectra.
     *
     * @return String with all unique spectra
     */
    public final String uniqueToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(proteinID);

        for (String nameKey : spectraUnique.keySet()) {
            if (spectraUnique.get(nameKey) != null) {
                sb.append("\t" + spectraUnique.get(nameKey));
            }
        }
        return sb.toString();
    }

    /**
     * Merges with another protein.
     *
     * @param protein protein object
     */
    public final void mergeWithGeneName(final Protein protein) {
        for (String sampleName : spectraTotal.keySet()) {
            spectraTotal.put(sampleName, spectraTotal.get(sampleName) + protein.getTotalSpectra(sampleName));
            spectraTotal.put(sampleName, spectraUnique.get(sampleName) + protein.getUniqueSpectra(sampleName));
        }
        mergePeptides(protein.getPeptides());
    }
}
