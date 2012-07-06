package org.cocktail.datte.components;

import org.cocktail.datte.pojo.PojoRdvCandidat;
import org.cocktail.datte.utils.Misc;
import org.cocktail.scolaritefwk.serveur.metier.eos.EOScolFormationAnnee;
import org.cocktail.scolarix.serveur.metier.eos.EOPreCandidat;
import org.cocktail.scolarix.serveur.metier.eos.EORdvCandidat;
import org.cocktail.scolarix.serveur.metier.eos.EORdvPlanning;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.ajax.AjaxUtils;

public class Inspecteur extends MyWOComponent {

	private static final long serialVersionUID = 1L;
	private EOGenericRecord salle;
	private EOGenericRecord enseignement;

	private EOScolFormationAnnee annee;

	private PojoRdvCandidat participant;
	private EOPreCandidat candidat;
	private EOPreCandidat selectedCandidat;
	private String erreurSaisieMessage;

	public String getErreurSaisieMessage() {
		return erreurSaisieMessage;
	}

	public Inspecteur(WOContext context) {
		super(context);
		annee = new EOScolFormationAnnee();
		annee.setFannKey(application.getFannKey());
	}

	@Override
	public void appendToResponse(WOResponse response, WOContext context) {

		super.appendToResponse(response, context);

		AjaxUtils.addStylesheetResourceInHead(context, response, "FwkCktlThemes.framework", "themes/default.css");
		AjaxUtils.addStylesheetResourceInHead(context, response, "FwkCktlThemes.framework", "themes/alert.css");
		AjaxUtils.addStylesheetResourceInHead(context, response, "FwkCktlThemes.framework", "themes/lighting.css");

		AjaxUtils.addStylesheetResourceInHead(context, response, "FwkCktlThemes.framework", "css/CktlCommon.css");
		// TODO Choisir une css de couleur si necessaire
		// CktlAjaxUtils.addStylesheetResourceInHead(context, response, "FwkCktlThemes.framework", "css/CktlCommonBleu.css");
		AjaxUtils.addStylesheetResourceInHead(context, response, "app", "styles/datte.css");

		AjaxUtils.addScriptResourceInHead(context, response, "prototype.js");
		AjaxUtils.addScriptResourceInHead(context, response, "FwkCktlThemes.framework", "scripts/window.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/strings.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/formatteurs.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/datte.js");

		session.removeObjectForKey("MessageErreur");
	}

	/**
	 * @return the salle
	 */
	public EOGenericRecord salle() {
		return salle;
	}

	/**
	 * @return the candidat
	 */
	public EOPreCandidat candidat() {
		return candidat;
	}

	/**
	 * @return the candidat
	 */
	public EOPreCandidat selectedCandidat() {
		return selectedCandidat;
	}

	/**
	 * @param salle the salle to set
	 */
	public void setSalle(EOGenericRecord salle) {
		this.salle = salle;
	}

	/**
	 * @param candidat the candidat to set
	 */
	public void setCandidat(EOPreCandidat candidat) {
		this.candidat = candidat;
	}

	/**
	 * @return the enseignement
	 */
	public EOGenericRecord enseignement() {
		return enseignement;
	}

	/**
	 * @param enseignement the enseignement to set
	 */
	public void setEnseignement(EOGenericRecord enseignement) {
		this.enseignement = enseignement;
	}


	/**
	 * @return the annee
	 */
	public EOScolFormationAnnee annee() {
		return annee;
	}

	/**
	 * @param annee the annee to set
	 */
	public void setAnnee(EOScolFormationAnnee annee) {
		this.annee = annee;
	}


	/**
	 * @return the participant
	 */
	public PojoRdvCandidat participant() {
		return participant;
	}

	/**
	 * @param participant the participant to set
	 */
	public void setParticipant(PojoRdvCandidat participant) {
		this.participant = participant;
	}

	public WOActionResults deleteParticipant() {
		session.getEvent().getParticipants().remove(participant);

		// On supprime le RdvCandidat
		EOQualifier qualifier = new EOKeyValueQualifier(EORdvCandidat.CAND_KEY_KEY, EOQualifier.QualifierOperatorEqual, participant.getCandKey());

		EORdvCandidat candidat = EORdvCandidat.fetchByQualifier(session().defaultEditingContext(), qualifier);
		session.defaultEditingContext().deleteObject(candidat);

		// On ne met pas à jour le RdvPlanning : ce champs est réservé au RéInscription par le web
		// session.getEvent().setNbRdvRest(session.getEvent().getNbRdvRest()+1);
		// EORdvPlanning rdvPlan = EORdvPlanning.fetchByQualifier(session().defaultEditingContext(), qualifier2);
		// rdvPlan.setNbRdvRest(session.getEvent().getNbRdvRest());
		// On récupère le planning pour faire les modifications de nombre.
//		NSArray<EOQualifier> quals = new NSMutableArray<EOQualifier>();
//		quals.add(new EOKeyValueQualifier(EORdvPlanning.DEB_PERIODE_KEY, EOQualifier.QualifierOperatorEqual, session.getEvent().getConvocHeure()));
//		quals.add(new EOKeyValueQualifier(EORdvPlanning.DATE_PER_KEY, EOQualifier.QualifierOperatorEqual, session.getEvent().getConvocDate()));
//		EOAndQualifier qualifier2 = new EOAndQualifier(quals);

		session.defaultEditingContext().saveChanges();
		
		erreurSaisieMessage = "Le rendez-vous de " + participant.getNomPrenomAffichage() + " a bien été supprimé.";

		return null;
	}



	public WOActionResults addParticipant() {
		EORdvCandidat rdvCand = EORdvCandidat.fetchByKeyValue(session.defaultEditingContext(), EORdvCandidat.CAND_KEY_KEY, selectedCandidat.candKey());
		if (rdvCand != null) {
			erreurSaisieMessage = Misc.nomPrenomAffichage(selectedCandidat) + " a déjà un rendez-vous le " + rdvCand.convocDate();
			return null;
		}

		EORdvCandidat rdv = EORdvCandidat.create(session().defaultEditingContext(), selectedCandidat.candKey(), application.getRne(), "I");  

		//		if (!selectedCandidat.isPreInscription() || !selectedCandidat.isReInscription())
		//			return null; 
		if (selectedCandidat.etudNumero() != null) {
			System.out.println("Le candidat est aussi un individu !");
		}
		//		rdv.setToPreCandidatRelationship(selectedCandidat);
		//		rdv.setCandKey(selectedCandidat.candKey());
		rdv.setConvocDate(session.getEvent().getStartAsConvocDate());
		rdv.setConvocHeure(session.getEvent().getConvocHeure());
		//		
		//		// TEST
				rdv.setCodEtb(application.getRne());
//				rdv.setCodEtp("447");
//				rdv.setCodVrsVet(2);
				rdv.setTemRdv("I");
//				
//				rdv.setHeureEdition("");
//				rdv.setDateEdition(new NSTimestamp());
		//		// FIN TEST

		session().defaultEditingContext().saveChanges();

		session.getEvent().setTitle(session.getEvent().getNbRdvRest() + " / " + session.getEvent().getNbEtuPer());
		session.getEvent().getParticipants().add(new PojoRdvCandidat(Misc.nomPrenomAffichage(selectedCandidat), rdv.candKey()));
		
		
		// On récupère le planning pour faire les modifications de nombre.
		session.getEvent().setNbRdvRest(session.getEvent().getNbRdvRest()-1);

		// On ne met pas à jour le RdvPlanning : le champs NB_RDV_REST est réservé au RéInscription par le web
		// NSArray<EOQualifier> quals = new NSMutableArray<EOQualifier>();
		// quals.add(new EOKeyValueQualifier(EORdvPlanning.DEB_PERIODE_KEY, EOQualifier.QualifierOperatorEqual, session.getEvent().getConvocHeure()));
		// quals.add(new EOKeyValueQualifier(EORdvPlanning.DATE_PER_KEY, EOQualifier.QualifierOperatorEqual, session.getEvent().getConvocDate()));
		// EOAndQualifier qualifier = new EOAndQualifier(quals);
	
		// EORdvPlanning rdvPlan = EORdvPlanning.fetchByQualifier(session().defaultEditingContext(), qualifier);
		// rdvPlan.setNbRdvRest(session.getEvent().getNbRdvRest());

		session().defaultEditingContext().saveChanges();

		candidat = null;
		erreurSaisieMessage = "Le rendez-vous de " + Misc.nomPrenomAffichage(selectedCandidat) + " a bien été ajouté le " + rdv.convocDate();

		return null;
	}

	public String nomPrenomAffichage() {
		return Misc.nomPrenomAffichage(selectedCandidat);
	}

	public WOActionResults addEtudiant() {
		EORdvCandidat rdv = EORdvCandidat.create(session().defaultEditingContext());

		if (!session().isEtudiant()) return null; 
		rdv.setConvocDate(session.getEvent().getStartAsConvocDate());
		rdv.setConvocHeure(session.getEvent().getStartHourAsString());
		//		rdv.setCodEtp("");
		//		rdv.setCodVrsVet(1);
		//		rdv.setCodEtb("");
		//		rdv.setToRdvPlanningInfoRelationship(value);		
		session.getEvent().setTitle(session.getEvent().getNbRdvRest() + " / " + session.getEvent().getNbEtuPer());
		// TEST : cand_key = 2007013967
		NSArray<EOQualifier> quals = new NSMutableArray<EOQualifier>();
		quals.add(new EOKeyValueQualifier(EORdvPlanning.DEB_PERIODE_KEY, EOQualifier.QualifierOperatorEqual, session.getEvent().getConvocHeure()));
		quals.add(new EOKeyValueQualifier(EORdvPlanning.DATE_PER_KEY, EOQualifier.QualifierOperatorEqual, session.getEvent().getConvocDate()));
		EOAndQualifier qualifier = new EOAndQualifier(quals);

		// On ne met pas à jour le RdvPlanning : ce champs est réservé au RéInscription par le web
		// session.getEvent().setNbRdvRest(session.getEvent().getNbRdvRest()-1);
		// EORdvPlanning rdvPlan = EORdvPlanning.fetchByQualifier(session().defaultEditingContext(), qualifier);
		// rdvPlan.setNbRdvRest(session.getEvent().getNbRdvRest());

		session().defaultEditingContext().saveChanges();
		System.out.println(session.getEvent());
		return null;
	}

	/**
	 * @return the selectCandidat
	 */
	public WOActionResults selectCandidat() {
		selectedCandidat = candidat;
		return null;
	}

	public boolean showAddParticipant() {
		return session().getEvent().getNbRdvScol() > 0;
	}

	public boolean showSelCandidat() {
		return selectedCandidat != null;
	}

}