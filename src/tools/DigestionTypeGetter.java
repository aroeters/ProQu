/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import proteindigesters.ChemotrypsinDigesterHighSpecific;
import proteindigesters.ChemotrypsinDigesterLowSpecific;
import proteindigesters.Digester;
import proteindigesters.NoDigester;
import proteindigesters.PepsinDigesterHigherPH;
import proteindigesters.PepsinDigesterLowPH;
import proteindigesters.TrypsinDigester;
import proteindigesters.TrypsinDigesterConservative;

/**
 *
 * @author arne
 */
public class DigestionTypeGetter {
        /**
     * Get the enzyme for digestion to use.
     *
     * @param digestionType method for digesting the peptides
     * @param minPepLen minimal peptide length
     * @return the Digester
     */
    public Digester getDigester(final Integer digestionType, final Integer minPepLen, final Integer mc) {
        switch (digestionType) {
            case 0: {
                return new NoDigester(minPepLen, mc);
            }
            case 1: {
                return new TrypsinDigesterConservative(minPepLen, mc);
            }
            case 2: {
                return new TrypsinDigester(minPepLen, mc);
            }
            case 3: {
                return new PepsinDigesterHigherPH(minPepLen, mc);
            }
            case 4: {
                return new PepsinDigesterLowPH(minPepLen, mc);
            }
            case 5: {
                return new ChemotrypsinDigesterHighSpecific(minPepLen, mc);
            }
            case 6: {
                return new ChemotrypsinDigesterLowSpecific(minPepLen, mc);
            }
            default: {
                return new NoDigester(minPepLen, mc);
            }
        }
    }
}
