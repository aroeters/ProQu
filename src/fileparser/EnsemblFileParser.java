/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author arne
 */
public class EnsemblFileParser {
    /**
     * All id conversions in id conversion file.
     */
    private final HashMap<String, String> idConversion = new HashMap<>();
    /**
     * Fills the id conversiion HashMap with its entries.
     * @param filename name of the ensembl id file
     * @throws FileNotFoundException when the file is not found
     * @throws IOException when an I/O error occurs
     */
    public final void getEnsemblIDs(final String filename) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(filename).getPath()));
        String line = br.readLine();
        String[] splitLine = line.split("\t");
        Integer ENSG;
        Integer otherID;
        if (splitLine[1].startsWith("ENSG")) ENSG = 1; else ENSG = 0;
        if (splitLine[1].startsWith("ENSG")) otherID = 0; else otherID = 1;
        while ((line=br.readLine()) != null) {
            splitLine = line.split("\t");
            idConversion.put(splitLine[otherID], splitLine[ENSG]);
        }
    }
    /**
     * Getter of a single id to convert.
     * @param id id to convert
     * @return String converted id
     */
    public final String getConvertedId(final String id) {
        if (idConversion.containsKey(id)) {
            return idConversion.get(id);
        } else {
            return id;
        }
    }
}
