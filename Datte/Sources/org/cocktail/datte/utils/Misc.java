package org.cocktail.datte.utils;

import org.cocktail.scolarix.serveur.metier.eos.EOPreCandidat;

public class Misc {

	public static String nomPrenomAffichage(EOPreCandidat selectedCandidat) {
		return selectedCandidat.candNom() +  " " + selectedCandidat.candPrenom();
	}
}
