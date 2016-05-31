/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import collections.ProteinCollection;
import objects.Protein;

/**
 *
 * @author arne
 */
public class ProteinToGeneConverter {
    /**
     * The protein collection to convert to gene set.
     */
    private final ProteinCollection protCol;
    /**
     * Constructor of the class.
     * @param protCollection the protein collection to convert
     */
    public ProteinToGeneConverter(final ProteinCollection protCollection) {
        this.protCol = protCollection;
    }
    /**
     * Creates a geneSet from the given protein collection.
     * @return ProteinCollection converted to gene names
     */
    public final ProteinCollection getGeneSet() {
        ProteinCollection geneSet = new ProteinCollection();
        geneSet.setSampleOrder(protCol.getSampleOrder());
        for (Protein protein : protCol.getProteins().values()) {
            geneSet.mergeProteins(protein.getGeneID(), protein);
        }
        return geneSet;
    }
}
