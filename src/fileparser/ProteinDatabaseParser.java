/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileparser;

import collections.PeptideCollection;
import collections.ProteinCollection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import objects.Protein;
import proteindigesters.Digester;

/**
 *
 * @author arne
 */
public class ProteinDatabaseParser {

    /**
     * All proteins in a single collection.
     */
    private final ProteinCollection protCol;
    /**
     * The peptide collection with all peptides.
     */
    private final PeptideCollection peptideCollection;
    /**
     * The database file to parse.
     */
    private final String databaseFile;
    /**
     * The type of digestion used.
     */
    private final Digester digesterType;

    /**
     * Constructor of the class.
     *
     * @param proteinDB the protein database
     * @param proteinCol the protein collection to fill
     * @param peps The peptide collection to fill
     * @param dg the digestertype to use
     */
    public ProteinDatabaseParser(final String proteinDB, final ProteinCollection proteinCol, final PeptideCollection peps, final Digester dg) {
        this.databaseFile = proteinDB;
        this.digesterType = dg;
        this.protCol = proteinCol;
        this.peptideCollection = peps;
    }

    public final void getProteinCollection(final LinkedList<String> directories, final String ensembl) throws Exception {
        EnsemblFileParser epf = new EnsemblFileParser();
        epf.getEnsemblIDs(ensembl);
        BufferedReader br = new BufferedReader(new FileReader(new File(this.databaseFile).getPath()));
        String line;
        String name = "";
        Protein protein = new Protein(directories);
        String[] splitLine;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                if (protein.getProteinID() != null) {
                    for (String peptide : this.digesterType.digest(protein.getAASeq())) {
                        protein.addPeptide(peptideCollection.addPeptide(peptide));
                    }
                    // sequence is no longer needed and to save space.
                    protein.clearAASeq();
                    protCol.addProtein(name, protein);
                    protein = new Protein(directories);
                }
                if (line.startsWith(">sp") || line.startsWith(">tr")) {
                    name = line.split("\\|")[1];
                } else if (line.startsWith(">NEWP")) {
                    name = line.split("\\s")[0].replace(">", "");
                } else if (line.startsWith(">EN") || line.startsWith(">GE")) {
                    if (line.contains("transcript")) {
                        splitLine = line.split("\\s");
                        for (String element : splitLine) {
                            if (element.contains("ENST")) {
                                name = element.split(":")[1];
                            }
                        }
                    } else {
                        if (line.contains("_")) {
                            name = line.split("_")[0].replace(">", "");
                        } else {
                            name = line.trim().replace(">", "");
                        }
                    }
                } else {
                    name = line.substring(1).replaceAll("\\s", "");
                }
                protein.setProteinID(name);
                protein.setGeneID(epf.getConvertedId(name));
            } else {
                protein.setAASeq(line);
            }
        }
        // To do the last protein with
        for (String peptide : this.digesterType.digest(protein.getAASeq())) {
            protein.addPeptide(peptideCollection.addPeptide(peptide));
        }
        protein.clearAASeq(); // sequence is no longer needed and to save space.
        protCol.addProtein(name, protein);
        System.out.println("Done...");
    }

    /**
     * Returns the peptide collection
     *
     * @return
     */
    public final PeptideCollection getPeptideCollection() {
        return peptideCollection;
    }
}
