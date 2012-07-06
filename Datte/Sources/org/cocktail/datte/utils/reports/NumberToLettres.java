/*
 * Copyright COCKTAIL (www.cocktail.org), 1995, 2011 This software
 * is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or
 * redistribute the software under the terms of the CeCILL license as
 * circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * As a counterpart to the access to the source code and rights to copy, modify
 * and redistribute granted by the license, users are provided only with a
 * limited warranty and the software's author, the holder of the economic
 * rights, and the successive licensors have only limited liability. In this
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally,
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge
 * of the CeCILL license and that you accept its terms.
 */

package org.cocktail.datte.utils.reports;

/* NumberToString.h created by pascal on Wed 19-Nov-1997 */
/* Traduit en Java by Rodolphe 26/11/2003*/

import com.webobjects.foundation.NSArray;
/**
 * Conversion d'une valeur numï¿½rique en lettres.
 * 
 * 
 * @author Rodolphe PRIN - rodolphe.prin at cocktail.org - rodolphe.prin at cocktail.org
 */

public final class NumberToLettres   {
	private String laCentaine;
    private boolean pasDeCentime;

	
	
	public static final String transforme(Number valeur, String stringMonnaie) {
		NumberToLettres myNumberToString = new NumberToLettres();
		return myNumberToString.valeurMonnaie(valeur, stringMonnaie);
	}
	

    private final String valeurMonnaie(Number valeur, String stringMonnaie) {
    	
        // Initialisation des variables
        NSArray myArray;
        String chaineDecimale, chaineFinale, myValeur, chaineEntiere;
        int partieEntiere, partieDecimale;
        pasDeCentime = false;
        chaineEntiere = new String("");
        chaineDecimale = new String("");
        chaineFinale = new String("");
        // Valorisation des variables
        myValeur = String.valueOf(valeur);
        myArray = NSArray.componentsSeparatedByString(myValeur, ".");
        try {
			partieEntiere =  Integer.valueOf(((String)myArray.objectAtIndex(0))).intValue();	
        }
        catch(Exception e) {
			partieEntiere = 0;
        }
		try {
			partieDecimale = Integer.valueOf(((String)myArray.objectAtIndex(1))).intValue();	
		}
		catch(Exception e) {
			partieDecimale = 0;
		}        
        if (partieDecimale == 0) {
            pasDeCentime = true;
        }

        // Traitement des parties entiï¿½re et dï¿½cimale
        chaineEntiere = this.traiterValeur(partieEntiere);
        chaineDecimale = this.traiterValeur(partieDecimale);

        // Traitement des unitï¿½s de monnaie
        chaineEntiere = chaineEntiere.concat(" ").concat(stringMonnaie);
        chaineDecimale = chaineDecimale.concat(" cent");
//        
//        if ("F".equals(stringMonnaie)) {
//            chaineDecimale = chaineDecimale.concat(" centime");
//            chaineEntiere = chaineEntiere.concat(" franc");
//        } else {
//        }

        if (partieEntiere > 1) {
            chaineEntiere = chaineEntiere.concat("s");
        }

        if (partieDecimale > 1) {
            chaineDecimale = chaineDecimale.concat("s");
        }

        // Traitement final
        chaineFinale = chaineEntiere.concat(chaineDecimale);
        return chaineFinale;
    }


    //*** Mï¿½thodes de Traitement ***//
    //*****************************//
    private final String traiterValeur(int valeur) {
        // Initialisation des variables
        String chaineFinale;
        int unite;
        unite = 0;
        chaineFinale = new String("");

        // Traitements
        if (valeur == 0) {
            chaineFinale = chaineFinale.concat(" zero ");
        } else {
            if (valeur >= 1000000) {
                unite = valeur/1000000;
                valeur = valeur-(unite*1000000);
                laCentaine = new String(" cent");
                chaineFinale = chaineFinale.concat(" "+(this.valeur20a999(unite))+" million");

                if (unite > 1) {
                    chaineFinale = chaineFinale.concat("s");
                }

            }

            if (valeur >= 1000) {
                unite = valeur/1000;
                valeur = valeur-(unite*1000);

                if (unite > 1) {
                    laCentaine = new String(" cent");
                    chaineFinale = chaineFinale.concat(" "+(this.valeur20a999(unite)));
                }

                chaineFinale = chaineFinale.concat(" mille");
            }

            if (valeur > 0) {
                laCentaine = this.valeurCent(valeur);
                chaineFinale = chaineFinale.concat(" "+(this.valeur20a999(valeur)));
            }

        }

        return chaineFinale;
    }


    private final String valeur20a999(int valeur) {
        // Initialisation des variables
        String chaineFinale;
        int unite;
        unite = 0;
        chaineFinale = new String("");

        // Traitements
        if (valeur >= 100) {
            unite = valeur/100;
            valeur = valeur-(unite*100);

            if (unite > 1) {
                chaineFinale = chaineFinale.concat(this.valeur0a19(unite));
            }

            chaineFinale = chaineFinale.concat(laCentaine);
        }

        if (valeur >= 80) {
            valeur = valeur-80;
            chaineFinale = chaineFinale.concat(" quatre-vingt");

            if (valeur == 0) {
                chaineFinale = chaineFinale.concat("s");
            } else {
                chaineFinale = chaineFinale.concat(" "+(this.valeur0a19(valeur)));
            }

        } else {
            if (valeur >= 60) {
                valeur = valeur-60;
                chaineFinale = chaineFinale.concat(" soixante");

                if ((valeur == 1) || (valeur == 11)) {
                    chaineFinale = chaineFinale.concat(" et");
                }

                chaineFinale = chaineFinale.concat(" "+(this.valeur0a19(valeur)));
            } else {
                if (valeur >= 20) {
                    unite = valeur/10;
                    valeur = valeur-(unite*10);

                    switch (unite) {
                        case 2 :
                            chaineFinale = chaineFinale.concat(" vingt");
                            break;
                        case 3 :
                            chaineFinale = chaineFinale.concat(" trente");
                            break;
                        case 4 :
                            chaineFinale = chaineFinale.concat(" quarante");
                            break;
                        case 5 :
                            chaineFinale = chaineFinale.concat(" cinquante");
                            break;
                    }

                    if (valeur == 1) {
                        chaineFinale = chaineFinale.concat(" et");
                    }

                    chaineFinale = chaineFinale.concat(" "+(this.valeur0a19(valeur)));
                } else {
                    if (valeur > 0) {
                        chaineFinale = chaineFinale.concat(" "+(this.valeur0a19(valeur)));
                    }

                }

            }

        }

        return chaineFinale;
    }


    private final String valeur0a19(int valeur) {
        // Initialisation des variables
        String chaineFinale;
        chaineFinale = new String("");

        // Traitements
        switch (valeur) {
            case 0 :
                chaineFinale = chaineFinale.concat("");
                break;
            case 1 :
                chaineFinale = chaineFinale.concat("un");
                break;
            case 2 :
                chaineFinale = chaineFinale.concat("deux");
                break;
            case 3 :
                chaineFinale = chaineFinale.concat("trois");
                break;
            case 4 :
                chaineFinale = chaineFinale.concat("quatre");
                break;
            case 5 :
                chaineFinale = chaineFinale.concat("cinq");
                break;
            case 6 :
                chaineFinale = chaineFinale.concat("six");
                break;
            case 7 :
                chaineFinale = chaineFinale.concat("sept");
                break;
            case 8 :
                chaineFinale = chaineFinale.concat("huit");
                break;
            case 9 :
                chaineFinale = chaineFinale.concat("neuf");
                break;
            case 10 :
                chaineFinale = chaineFinale.concat("dix");
                break;
            case 11 :
                chaineFinale = chaineFinale.concat("onze");
                break;
            case 12 :
                chaineFinale = chaineFinale.concat("douze");
                break;
            case 13 :
                chaineFinale = chaineFinale.concat("treize");
                break;
            case 14 :
                chaineFinale = chaineFinale.concat("quatorze");
                break;
            case 15 :
                chaineFinale = chaineFinale.concat("quinze");
                break;
            case 16 :
                chaineFinale = chaineFinale.concat("seize");
                break;
            case 17 :
                chaineFinale = chaineFinale.concat("dix-sept");
                break;
            case 18 :
                chaineFinale = chaineFinale.concat("dix-huit");
                break;
            case 19 :
                chaineFinale = chaineFinale.concat("dix-neuf");
                break;
        }

        return chaineFinale;
    }


    private final String valeurCent(int valeur) {
        // Initialisation des variables
        String chaineFinale;
        int unite;
        unite = 0;
        chaineFinale = new String("");

        // Traitements
        if (pasDeCentime == false) {
            return chaineFinale.concat(" cent");
        }

        unite = valeur/100;
        valeur = valeur-(unite*100);

        if (valeur == 0) {
            return chaineFinale.concat(" cents");
        }

        return chaineFinale = chaineFinale.concat(" cent");
    }


}
