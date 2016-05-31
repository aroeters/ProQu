/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collections;

import objects.Protein;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author arne
 */
public class ProteinCollection {
    /**
     * All proteins in the database.
     */
    private final HashMap<String, Protein> proteins = new HashMap<>();
    /**
     * Order of the samples
     */
    private LinkedList<String> sampleOrder = null;

    public HashMap<String, Protein> getProteins() {
        return proteins;
    }

    public void addProtein(String proteinName, Protein proteinObject) {
        if (!proteins.containsKey(proteinName)) {
            this.proteins.put(proteinName, proteinObject);
        } else {
            proteins.get(proteinName).mergePeptides(proteinObject.getPeptides());
        }
    }

    public final Protein getProtein(final String protName) {
        return proteins.get(protName);
    }

    public final LinkedList<String> getSampleOrder() {
        return sampleOrder;
    }

    public void setSampleOrder(final LinkedList<String> sampleOrder) {
        this.sampleOrder = sampleOrder;
    }

    public final String samplesToString() {
        StringBuilder sb = new StringBuilder();
        for (String sample : sampleOrder) {
            sb.append("\t" + sample);
        }
        return sb.toString();
    }

    public final void mergeProteins(final String geneName, final Protein protein) {
        if (proteins.containsKey(geneName)) {
            proteins.get(geneName).mergeWithGeneName(protein);
        } else {
            protein.setProteinID(geneName);
            proteins.put(geneName, protein);
        }
    }
}
