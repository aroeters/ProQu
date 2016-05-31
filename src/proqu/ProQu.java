/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proqu;

import java.io.IOException;
import java.util.HashMap;
import org.apache.commons.cli.ParseException;
import java.util.LinkedList;
import cmdparser.CMDArgumentParser;
import collections.PeptideCollection;
import collections.ProteinCollection;
import fileparser.EnsemblFileParser;
import fileparser.EnsemblProteinDbParser;
import fileparser.PeptideUniquenessCollector;
import filewriter.CsvFileWriter;
import fileparser.ProteinPeptidesFileParser;
import fileparser.ProteinDatabaseParser;
import fileparser.TAPPFileParser;
import filewriter.PeptideUniquenessWriter;
import java.util.HashSet;
import tools.DigestionTypeGetter;
import tools.ProteinToGeneConverter;
import tools.SubDirectoryGetter;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.cli.MissingArgumentException;
/**
 *
 * @author arne
 */
public class ProQu {

    /**
     * @param args the command line arguments
     * @throws org.apache.commons.cli.ParseException
     * @throws java.io.IOException
     */
    public static void main(final String[] args) throws ParseException, IOException, Exception {
        long start = System.currentTimeMillis();
        CMDArgumentParser cmd = null;
        try {
        cmd = new CMDArgumentParser(args);
        } catch (UnrecognizedOptionException e) {
            System.out.println("You have given an option that is not possible,\nplease use -h to see which options are available.");
            System.exit(0);
        } catch (MissingArgumentException e) {
            System.out.println("One of your flags doesn't have an argument.\nPlease check the given flags");
            System.exit(0);
        }
// Get all arguments
        LinkedList<String> directories;                                         // Get sub directories
        if (cmd.getTAPPFile() == null) {
            directories = new SubDirectoryGetter().getSubDirs(cmd.getSampleDir());
        } else {
            directories = new SubDirectoryGetter().getTAPPSamples(cmd.getTAPPFile());
        }
        EnsemblFileParser epf = new EnsemblFileParser();

        ProteinCollection protCol = new ProteinCollection();                    // Create the protein collection
        PeptideCollection pepCol = new PeptideCollection();                     // and the peptide collection
        if (cmd.getEnsemblDatabase() != null) {
            EnsemblProteinDbParser epdp = new EnsemblProteinDbParser(
                    cmd.getEnsemblDatabase(),
                    protCol,
                    pepCol,
                    new DigestionTypeGetter().getDigester(
                            cmd.getDigestion(),
                            cmd.getMinPeptideLength(),
                            cmd.getMiscleavages()));
            epdp.parseEnsemblDbFile(directories, cmd.getEnsembl());
        }
        ProteinDatabaseParser proteinFileParser = new ProteinDatabaseParser(    // Get the protein collection database to match against
                cmd.getDatabase(), 
                protCol,                                       
                pepCol,
                new DigestionTypeGetter().getDigester(
                        cmd.getDigestion(),
                        cmd.getMinPeptideLength(),
                        cmd.getMiscleavages()));
        proteinFileParser.getProteinCollection(directories, cmd.getEnsembl());
        protCol.setSampleOrder(directories);                                    // Set the order of the samples for later
        ProteinPeptidesFileParser proteinPeptidesFileParser;
        HashMap<String, HashSet<String>> peptideUniqueness;
        PeptideUniquenessCollector cpu = new PeptideUniquenessCollector();
        if (cmd.getTAPPFile() == null) {
            for (String subdir : directories) {
                String fileToParse = cmd.getSampleDir() + subdir + "/protein-peptides.csv";
                cpu.addPeptides(fileToParse);
            }
            peptideUniqueness = cpu.getPeptideMatches(protCol, cmd.getThreads(), cmd.isGeneSet(), proteinFileParser.getPeptideCollection(), null);
        } else {
            TAPPFileParser tfp = new TAPPFileParser(cmd.getTAPPFile());
            peptideUniqueness = cpu.getPeptideMatches(protCol, cmd.getThreads(), cmd.isGeneSet(), proteinFileParser.getPeptideCollection(), tfp.getUniquePeptides());
            PeptideUniquenessWriter pw = new PeptideUniquenessWriter();
            pw.writePeptideUniqueness(peptideUniqueness, cmd.getResultDir());
        }
        // Do the protein quantification
        if (cmd.isGeneSet()) { // parse every file with gene info
            ProteinToGeneConverter proteinTogeneConverter = new ProteinToGeneConverter(protCol);
            protCol = proteinTogeneConverter.getGeneSet();
            if (cmd.getTAPPFile() != null) {
                TAPPFileParser tfp = new TAPPFileParser(cmd.getTAPPFile());
                tfp.getProteinQuantification(protCol, peptideUniqueness);
            } else {
                for (String subdir : directories) {
                    String fileToParse = cmd.getSampleDir() + subdir + "/protein-peptides.csv";
                    proteinPeptidesFileParser = new ProteinPeptidesFileParser();
                    proteinPeptidesFileParser.getProteinQuantification(fileToParse, subdir, protCol, peptideUniqueness);
                }
            }
            CsvFileWriter csvFileWriter = new CsvFileWriter(protCol);
            csvFileWriter.writeToCsvFile((cmd.getResultDir() + "unique_gene_spectra.csv"), 1);
            csvFileWriter.writeToCsvFile((cmd.getResultDir() + "total_gene_spectra.csv"), 2);
            
        } else { // parse every file with protein info
            if (cmd.getTAPPFile() != null) {
                TAPPFileParser tfp = new TAPPFileParser(cmd.getTAPPFile());
                tfp.getProteinQuantification(protCol, peptideUniqueness);
            } else {
                for (String subdir : directories) {
                    String fileToParse = cmd.getSampleDir() + subdir + "/protein-peptides.csv";
                    proteinPeptidesFileParser = new ProteinPeptidesFileParser();
                    proteinPeptidesFileParser.getProteinQuantification(fileToParse, subdir, protCol, peptideUniqueness);
                }
            }
            CsvFileWriter csvFileWriter = new CsvFileWriter(protCol);
            csvFileWriter.writeToCsvFile((cmd.getResultDir() + "unique_protein_spectra.csv"), 1);
            csvFileWriter.writeToCsvFile((cmd.getResultDir() + "total_protein_spectra.csv"), 2);
        }
        System.out.println("It took: " + (System.currentTimeMillis() - start) / 1000 + " seconds to complete the program.");
    }
}
