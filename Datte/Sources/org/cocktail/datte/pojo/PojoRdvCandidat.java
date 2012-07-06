package org.cocktail.datte.pojo;

import java.io.Serializable;

public class PojoRdvCandidat implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nomPrenomAffichage;
	private int candKey;
	
	public PojoRdvCandidat(String nomPrenomAffichage, int candKey) {
		this.nomPrenomAffichage = nomPrenomAffichage;
		this.candKey = candKey;
	}
	
	public String getNomPrenomAffichage() {
		return nomPrenomAffichage;
	}
	public void setNomPrenomAffichage(String nomPrenomAffichage) {
		this.nomPrenomAffichage = nomPrenomAffichage;
	}
	public int getCandKey() {
		return candKey;
	}
	public void setCandKey(int candKey) {
		this.candKey = candKey;
	}
	
	
	
}
