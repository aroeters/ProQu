/*
 *Author: Arne Roeters
 */
package cmdparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 *
 * @author arne
 */
public class CMDArgumentParser {

    /**
     * Contains the number of threads to use.
     */
    private Integer Threads = 2;
    /**
     * Directory to write the results to.
     */
    private String resultDir = "./";
    /**
     * The type of digestion enzyme to use.
     */
    private Integer digestion = 0;
    /**
     * The minimal length of a peptide.
     */
    private Integer minPeptideLength = 6;
    /**
     * The number of miscleavages to use.
     */
    private Integer miscleavages = 0;
    /**
     * Path + filename of the ENSEMBL id file.
     */
    private String ensembl = null;
    /**
     * Path + filename to the protein database.
     */
    private String database = null;
    /**
     * Path + filename to the ensembl protein database.
     */
    private String ensembl_database = null;
    /**
     * The path to the directory that contains all samples.
     */
    private String sampleDir = null;
    /**
     * The path + filename to the TAPP output file.
     */
    private String sampleFile = null;
    /**
     * True if protein quantification has to be done for genes.
     */
    private boolean geneSet = false;

    /**
     * Getter of the threads.
     *
     * @return Integer threads
     */
    public final Integer getThreads() {
        return Threads;
    }

    /**
     * Getter of the result directory.
     *
     * @return String with result directory
     */
    public final String getResultDir() {
        return resultDir;
    }

    /**
     * Getter of the digestion method.
     *
     * @return Integer digestion method
     */
    public final Integer getDigestion() {
        return digestion;
    }

    /**
     * Getter of the minimal peptide length.
     *
     * @return Integer minimal peptide length
     */
    public final Integer getMinPeptideLength() {
        return minPeptideLength;
    }

    /**
     * Getter of the miscleavages to use.
     *
     * @return Integer number of miscleavages
     */
    public final Integer getMiscleavages() {
        return miscleavages;
    }

    /**
     * Getter of the ensembl id file.
     *
     * @return String with the path and name of the ensembl id file
     */
    public final String getEnsembl() {
        return ensembl;
    }

    /**
     * Getter of the database file.
     *
     * @return String with the path and filename of the protein database file
     */
    public final String getDatabase() {
        return database;
    }

    /**
     * Getter of the database file.
     *
     * @return String with the path and filename of the protein database file
     */
    public final String getEnsemblDatabase() {
        return ensembl_database;
    }

    /**
     * Getter of the sample directory.
     *
     * @return String with the path of the directory that contains all samples
     */
    public final String getSampleDir() {
        return sampleDir;
    }

    /**
     * Checks if the protein quantification has to be done on gene level.
     *
     * @return True if genes have to be used
     */
    public final boolean isGeneSet() {
        return geneSet;
    }

    /**
     * Getter of the TAPPFile.
     *
     * @return path + filename to the TAPP output file
     */
    public final String getTAPPFile() {
        return sampleFile;
    }
    /**
     * constructor of the class.
     * @param args
     * @throws ParseException
     * @throws IOException
     * @throws UnrecognizedOptionException 
     */
    public CMDArgumentParser(final String[] args) throws ParseException, IOException, UnrecognizedOptionException, MissingArgumentException {
        setCMDArguments(args);
    }
    /**
     * Gets all the arguments from the CMD to know the file locations.
     *
     * @param args CMD arguments
     * @throws ParseException when something goes wrong with parsing
     * @throws FileNotFoundException if the specified file is not found.
     * @throws IOException when an error occurs in the handling of I/O
     */
    private final void setCMDArguments(final String[] args)
            throws ParseException, FileNotFoundException, IOException {
        // Create options object
        Options options = new Options();
        // Add all options
        options.addOption(OptionBuilder.withLongOpt("threads")
                .withDescription("The number of threads to use.\n"
                        + "Default = 4 (influences processing speed)")
                .hasArg()
                .create("a"));
        options.addOption(OptionBuilder.withLongOpt("result_dir")
                .withDescription("The path to the directory to place the result file in.\n"
                        + "Default is ./")
                .hasArg()
                .create("c"));
        options.addOption(OptionBuilder.withLongOpt("digestion")
                .withDescription("The type of peptide digestion that is wanted:\n"
                        + "0:no digestion\n"
                        + "1:Trypsin conservative (only digest at Arg unless Pro after)\n"
                        + "\t\t\t\t\t2:Trypsin\n"
                        + "3:Pepsin High PH (PH > 2)\n"
                        + "4:Pepsin (PH 1.3)\n"
                        + "5:Chemotrypsin high specificity\n"
                        + "6:Chemotrypsin Low specificity\n"
                        + "Default = 0 (no digestion)")
                .hasArg()
                .create("d"));
        options.addOption(OptionBuilder.withLongOpt("min_peptide_length")
                .withDescription("Minimal length of the peptide to be used.\n"
                        + "Default = 6")
                .hasArg()
                .create("e"));
        options.addOption(OptionBuilder.withLongOpt("ensembl")
                .withDescription("The path + file that is needed to convert protein ids to ensembl gene ids.")
                .hasArg()
                .create("f"));
        options.addOption(OptionBuilder.withLongOpt("geneSet")
                .withDescription("Get protein quantification per gene.")
                .create("g"));
        options.addOption(OptionBuilder.withLongOpt("help")
                .withDescription("Prints the help if used")
                .create("h"));
        options.addOption(OptionBuilder.withLongOpt("ensembl_database")
                .withDescription("The ensembl database file to use as a second database.\nThis is highly recommended when checking against a database with many NEWP in it."
                        + "\nThis database will be checked first.")
                .hasArgs()
                .create("i"));
        options.addOption(OptionBuilder.withLongOpt("miscleavages")
                .withDescription("The number of miscleavages to use. (default = 0)")
                .hasArgs()
                .create("m"));
        options.addOption(OptionBuilder.withLongOpt("database")
                .withDescription("The protein database to match the proteins against.")
                .hasArg()
                .create("p"));
        options.addOption(OptionBuilder.withLongOpt("sample_dir")
                .withDescription("The directory that contains the directory per sample.\n"
                        + "The name of the sample directory should be the name of the sample.")
                .hasArg()
                .create("s"));
        options.addOption(OptionBuilder.withLongOpt("TAPPFile")
                .withDescription("The number of threads to use.\n"
                        + "Default = 4 (influences processing speed)")
                .hasArg()
                .create("t"));
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        // Check help option
        if (cmd.hasOption("h") || cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Help", options);
            System.exit(0);
        }
        // checks the threads option
        if (cmd.hasOption("c") || cmd.hasOption("threads")) {
            if (checkValidInteger(cmd.getOptionValue("a"), options, "Please enter a valid number of threads.")) {
                this.Threads = Integer.parseInt(cmd.getOptionValue("a"));
            }
        }
        // Checks the result dir option
        if (cmd.hasOption("c") || cmd.hasOption("result_dir")) {
            if (checkValidDirectory(cmd.getOptionValue("c"), options, "The directory for the final results is not a valid directory.")) {
                this.resultDir = cmd.getOptionValue("c");
            }
        }
        // Checks the peptideFile option
        if (!cmd.hasOption("p") && !cmd.hasOption("database")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("The protein database to match the proteins against.", options);
            System.exit(0);
        } else {
            if (checkValidDatabaseFile(cmd.getOptionValue("p"), options, "Provide a valid protein database in fasta format to continue.")) {
                this.database = cmd.getOptionValue("p");
            }
        }
        // checks the sample dir option
        if (cmd.hasOption("s") && cmd.hasOption("t")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("You should provide atleast only one of the two options to do the\n"
                    + "protein quantification for.This can be -t for TAPP output file or -s for\n"
                    + "PEAKS output directory where each sample has its own directory in", options);
            System.exit(0);

        } else if (cmd.hasOption("s") || cmd.hasOption("sample_dir")) {
            if (checkValidDirectory(cmd.getOptionValue("s"), options, "You should provide an existing directory with all the sample directories in it.")) {
                this.sampleDir = cmd.getOptionValue("s");
            }
        } else if (cmd.hasOption("t") || cmd.hasOption("TAPPFile")) {
            if (checkValidTAPPFile(cmd.getOptionValue("t"), options, "You should provide an existing TAPP output file.\n"
                    + "This should be peptide_sequence.txt from the TAPP output.")) {
                this.sampleFile = cmd.getOptionValue("t");
            }
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("You should provide atleast one of the two options to do the\n"
                    + "protein quantification for.This can be -t for TAPP output file or -s for\n"
                    + "PEAKS output directory where each sample has its own directory in", options);
            System.exit(0);
        }
        // checks the geneSet option
        if (cmd.hasOption("g") || cmd.hasOption("geneSet")) {
            this.geneSet = true;
        }
        // checks the number of miscleavages allowed for the digestion
        if (cmd.hasOption("m") || cmd.hasOption("miscleavages")) {
            if (checkMC(cmd.getOptionValue("m"), options, "The number of miscleavages used (max = 3)")) {
                this.miscleavages = Integer.parseInt(cmd.getOptionValue("m"));
            }
        }
        // checks the digestion option
        if (cmd.hasOption("d") || cmd.hasOption("digestion")) {
            if (checkValiddigestion(cmd.getOptionValue("d"), options, "The digestion you chose is not correct.\nPlease try again with a valid digestion method.")) {
                this.digestion = Integer.parseInt(cmd.getOptionValue("d"));
            }
        }
        // Checks the minimal peptide length option
        if (cmd.hasOption("e") || cmd.hasOption("min_peptide_length")) {
            if (checkValidInteger(cmd.getOptionValue("e"), options, "You should enter a valid number for the minimal length of a peptide.")) {
                this.minPeptideLength = Integer.parseInt(cmd.getOptionValue("e"));
            }
        }
        // Checks the peptideFile option
        if (!cmd.hasOption("f") && !cmd.hasOption("ensembl")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("You should provide a valid file with the ENSG and protein ID", options);
            System.exit(0);
        } else {
            if (checkValidEnsemblFile(cmd.getOptionValue("f"), options, "You should provide a valid file with the ENSG and protein ID")) {
                this.ensembl = cmd.getOptionValue("f");
            }
        }
        if (cmd.hasOption("i") || cmd.hasOption("ensembl_database")) {
            if (checkValidDatabaseFile(cmd.getOptionValue("i"), options, "Please provide a correct database file that can be downloaded from theftp server of ensembl.")) {
                ensembl_database = cmd.getOptionValue("i");
            }
        }
    }

    /**
     * Checks if the option value is a valid number.
     *
     * @param optionValue the value of the option
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if option a/threads is a valid value
     */
    public final boolean checkValidInteger(final String optionValue, final Options options, final String errorMessage) {
        try {
            Integer check = Integer.parseInt(optionValue);
        } catch (IllegalArgumentException IAE) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(errorMessage, options);
            System.exit(0);
        }
        return true;
    }

    /**
     * Checks if the option value is a valid number in the range 0 to 6.
     *
     * @param optionValue the value of the option
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if option a/threads is a valid value
     */
    public final boolean checkValiddigestion(final String optionValue, final Options options, final String errorMessage) {
        try {
            Integer check = Integer.parseInt(optionValue);
            if (0 <= check && check <= 6) {
                return true;
            }
        } catch (IllegalArgumentException iae) {
        }
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(errorMessage, options);
        System.exit(0);
        return false;
    }

    /**
     * Checks if the option value is a valid directory.
     *
     * @param optionValue the value of option
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if option value is a valid directory
     */
    public final boolean checkValidDirectory(final String optionValue, final Options options, final String errorMessage) {
        File file = new File(optionValue);
        if (file.isDirectory()) {
            return true;
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(errorMessage, options);
            System.exit(0);
        }
        return false;
    }

    /**
     * Checks if the option value is a valid database file. Checks if the file
     * extension is .fa or .fasta and if it is a valid file. When that passes
     * checks if the first or second line starts with an ">" which is typical
     * for a fa and fasta file.
     *
     * @param optionValue the value of option
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if option value is a valid database in .fa or .fasta format
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if an error occurs during in or output handling
     */
    public final boolean checkValidDatabaseFile(final String optionValue, final Options options, final String errorMessage)
            throws FileNotFoundException, IOException {
        File file = new File(optionValue);
        if (file.isFile() && optionValue.endsWith(".fa") || optionValue.endsWith(".fasta") || optionValue.endsWith(".faa")) {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String line;
            Integer lineCount = 0;
            boolean validFasta = false;
            while ((line = br.readLine()) != null && lineCount < 2) {
                lineCount++;
                if (line.startsWith(">")) {
                    validFasta = true;
                }
            }
            if (validFasta) {
                return true;
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(errorMessage, options);
                System.exit(0);
            }
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(errorMessage, options);
            System.exit(0);
        }
        return false;
    }

    /**
     * Checks if the number of miscleavages is allowed.
     *
     * @param mc the number of miscleavages
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if the file is a valid file
     */
    public final boolean checkMC(final String mc, final Options options, final String errorMessage) {
        HashSet<Integer> values = new HashSet<>();
        values.add(0);
        values.add(1);
        values.add(2);
        values.add(3);
        if (!values.contains(Integer.parseInt(mc))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(errorMessage, options);
            System.exit(0);
        }
        return true;
    }

    /**
     * Checks if the option value is a valid ENSEMBL ID file. Checks if the file
     * extension is .fa or .fasta and if it is a valid file. When that passes
     * checks if the first or second line starts with an ">" which is typical
     * for a fa and fasta file.
     *
     * @param optionValue the value of option
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if option value is a valid database in .fa or .fasta format
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if an error occurs during in or output handling
     */
    public final boolean checkValidEnsemblFile(final String optionValue, final Options options, final String errorMessage)
            throws FileNotFoundException, IOException {
        File file = new File(optionValue);
        if (file.isFile()) {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String[] line = br.readLine().split("\t");
            if (line[0].startsWith("ENSG") || line[1].startsWith("ENSG")) {
                return true;
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(errorMessage, options);
                System.exit(0);
            }
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(errorMessage, options);
            System.exit(0);
        }
        return false;
    }

    /**
     * Checks if the option value is a valid TAPP output file.
     *
     * @param optionValue the value of option
     * @param options the options object
     * @param errorMessage The message it should display if not a valid value
     * @return true if option value is a TAPP output file
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if an error occurs during in or output handling
     */
    public final boolean checkValidTAPPFile(final String optionValue, final Options options, final String errorMessage)
            throws FileNotFoundException, IOException {
        File file = new File(optionValue);
        if (file.isFile()) {
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String[] line = br.readLine().split("\\s");
            if (line[0].toLowerCase().contains("meta")) {
                return true;
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(errorMessage, options);
                System.exit(0);
            }
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(errorMessage, options);
            System.exit(0);
        }
        return false;
    }
}
