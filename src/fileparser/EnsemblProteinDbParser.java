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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import objects.Protein;
import proteindigesters.Digester;

/**
 *
 * @author arne
 */
public class EnsemblProteinDbParser {

    /**
     * The peptide collection to fill.
     */
    private final PeptideCollection pepCol;
    /**
     * The proteinCollection to fill.
     */
    private final ProteinCollection protCol;
    /**
     * The database file.
     */
    private final File dbFile;
    /**
     * The digester to use to get the peptides.
     */
    private final Digester digester;

    /**
     * Constructor of the class.
     *
     * @param dbFile the database file to parse
     * @param proteinCol the protein collection to fill
     * @param peps the peptide collection to fill
     * @param dg the digestion method top use
     * @throws java.io.IOException
     */
    public EnsemblProteinDbParser(final String dbFile, final ProteinCollection proteinCol, final PeptideCollection peps, final Digester dg) throws IOException {
        this.protCol = proteinCol;
        this.digester = dg;
        this.dbFile = new File(dbFile);
        this.pepCol = peps;

    }

    /**
     *
     * @param directories
     * @param ensembl
     * @throws FileNotFoundException
     * @throws IOException
     */
    public final void parseEnsemblDbFile(final LinkedList<String> directories, final String ensembl) throws FileNotFoundException, IOException {
        EnsemblFileParser epf = new EnsemblFileParser();
        epf.getEnsemblIDs(ensembl);
        BufferedReader br = new BufferedReader(new FileReader(dbFile.getPath()));
        String line;
        String name = "";
        String[] splitLine;
        Protein protein = new Protein(directories);
        while ((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                if (line.startsWith(">")) {
                    if (protein.getProteinID() != null) {
                        for (String peptide : this.digester.digest(protein.getAASeq())) {
                            protein.addPeptide(pepCol.addPeptide(peptide));
                        }
                        protein.clearAASeq(); // sequence is no longer needed and to save space.
                        protCol.addProtein(name, protein);
                        protein = new Protein(directories);
                    }
                    splitLine = line.split("\\s");
                    for (String element : splitLine) {
                        if (element.toLowerCase().startsWith("transcript:")) {
                            name = element.split(":")[1].split("\\.")[0];
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
            for (String peptide : this.digester.digest(protein.getAASeq())) {
            protein.addPeptide(pepCol.addPeptide(peptide));
        }
        protein.clearAASeq(); // sequence is no longer needed and to save space.
        protCol.addProtein(name, protein);
    }
}
