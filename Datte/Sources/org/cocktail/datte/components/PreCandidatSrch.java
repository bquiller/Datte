package org.cocktail.datte.components;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxUserQuestionDelegate;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumn;
import org.cocktail.fwkcktlajaxwebext.serveur.component.tableview.column.CktlAjaxTableViewColumnAssociation;
import org.cocktail.fwkcktldroitsutils.common.util.LRLogger;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.scolarix.serveur.metier.eos.EOPreCandidat;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.eocontrol.EOAndQualifier;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

public class PreCandidatSrch extends WOComponent {
	
	private static final long serialVersionUID = -5774873091154273762L;
	public static final String BINDING_updateContainerID = "updateContainerID";
	private String srchPrenom;
	private String srchNom;
	private String erreurSaisieMessage;
	private NSArray<EOPreCandidat> candidats;
	private EOPreCandidat selectedCandidat;
	private WODisplayGroup displayGroup;
	private NSMutableArray<CktlAjaxTableViewColumn> colonnes;
	private final CktlAjaxUserQuestionDelegate userQuestionDelegate;
	public String callbackOnSelectionnerCandidat;
	public String updateContainerID;

	public PreCandidatSrch(WOContext context) {
		super(context);
		userQuestionDelegate = new CktlAjaxUserQuestionDelegate(this);
	}

	public EOPreCandidat getSelectedPersonne() {
		selectedCandidat = (EOPreCandidat) valueForBinding("selectedCandidat");
		return selectedCandidat;
	}

	/**
	 * Effectue une recherche à partir des criteres saisis et met a jour le displayGroup.
	 */
	public WOActionResults rechercher() {
		erreurSaisieMessage = "";
		if (srchNom == null || srchPrenom == null) {
			erreurSaisieMessage = "Vous devez renseigner le nom ET le prénom.";
			return null;
		}
		
		try {

			NSArray<EOQualifier> quals = new NSMutableArray<EOQualifier>();
			
			quals.add(new EOKeyValueQualifier(EOPreCandidat.CAND_NOM_KEY, EOQualifier.QualifierOperatorCaseInsensitiveLike, "*" + srchNom + "*"));
			quals.add(new EOKeyValueQualifier(EOPreCandidat.CAND_PRENOM_KEY, EOQualifier.QualifierOperatorCaseInsensitiveLike, "*" + srchPrenom + "*"));
			
			EOAndQualifier qualifier = new EOAndQualifier(quals);

			candidats = EOPreCandidat.fetchAll(session().defaultEditingContext(), qualifier);
			displayGroup.setObjectArray(getCandidats());
		} catch (Exception e) {
			if (LRLogger.getCurrentLevel() >= LRLogger.LVL_ERROR) {
				e.printStackTrace();
			}
			erreurSaisieMessage = e.getMessage();

		}
		return null;
	}


	public WODisplayGroup getDisplayGroup() {
		if (displayGroup == null) {
			displayGroup = new WODisplayGroup();
			displayGroup.setObjectArray(getCandidats());
		}
		return displayGroup;
	}
    public NSArray<CktlAjaxTableViewColumn> getColonnes() {
        if (colonnes == null) {
            String numEtudKey = "selectedCandidat." + EOPreCandidat.ETUD_NUMERO_KEY;
            String numKey = "selectedCandidat." + EOPreCandidat.CAND_NUMERO_KEY;
            String nomKey = "selectedCandidat." + EOPreCandidat.CAND_NOM_KEY;
            String prenomKey = "selectedCandidat." + EOPreCandidat.CAND_PRENOM_KEY;
            colonnes = new NSMutableArray<CktlAjaxTableViewColumn>();
            
            CktlAjaxTableViewColumn col0 = new CktlAjaxTableViewColumn();
            col0.setOrderKeyPath(EOPreCandidat.ETUD_NUMERO_KEY);
            col0.setLibelle("Numéro étudiant");
            CktlAjaxTableViewColumnAssociation ass0 = new CktlAjaxTableViewColumnAssociation(numEtudKey, "");
            col0.setAssociations(ass0);
            colonnes.addObject(col0);
            
            CktlAjaxTableViewColumn col1 = new CktlAjaxTableViewColumn();
            col1.setOrderKeyPath(EOPreCandidat.CAND_NUMERO_KEY);
            col1.setLibelle("Numéro candidat");
            CktlAjaxTableViewColumnAssociation ass1 = new CktlAjaxTableViewColumnAssociation(numKey, "");
            col1.setAssociations(ass1);
            colonnes.addObject(col1);
            
            CktlAjaxTableViewColumn col2 = new CktlAjaxTableViewColumn();
            col2.setOrderKeyPath(EOPreCandidat.CAND_NOM_KEY);
            col2.setLibelle("Nom");
            CktlAjaxTableViewColumnAssociation ass2 = new CktlAjaxTableViewColumnAssociation(nomKey, "");
            col2.setAssociations(ass2);
            colonnes.addObject(col2);
            
            CktlAjaxTableViewColumn col3 = new CktlAjaxTableViewColumn();
            col3.setOrderKeyPath(null);
            col3.setLibelle("Prénom");
            CktlAjaxTableViewColumnAssociation ass3 = new CktlAjaxTableViewColumnAssociation(prenomKey, "");
            col3.setAssociations(ass3);
            colonnes.addObject(col3);
        }
        return colonnes;
}

	/**
	 * @return the srchPrenom
	 */
	public String srchPrenom() {
		return srchPrenom;
	}

	/**
	 * @param srchPrenom the srchPrenom to set
	 */
	public void setSrchPrenom(String srchPrenom) {
		this.srchPrenom = srchPrenom;
	}

	/**
	 * @return the srchNom
	 */
	public String srchNom() {
		return srchNom;
	}

	/**
	 * @param srchNom the srchNom to set
	 */
	public void setSrchNom(String srchNom) {
		this.srchNom = srchNom;
	}


	public EOPreCandidat getSelectedCandidat() {
		return selectedCandidat;
	}


	public void setSelectedCandidat(EOPreCandidat selectedCandidat) {
		this.selectedCandidat = selectedCandidat;
	}


	public String getErreurSaisieMessage() {
		return erreurSaisieMessage;
	}


	public NSArray<EOPreCandidat> getCandidats() {
		return candidats;
	}


	/**
	 * @return the selectCandidat
	 */
	public WOActionResults selectCandidat() {
		setValueForBinding(selectedCandidat, "selectedCandidat");

		return performParentAction(onSelectionnerCandidat());
	}

	public String onSelectionnerCandidat() {
		if (callbackOnSelectionnerCandidat == null) {
			callbackOnSelectionnerCandidat = (String) valueForBinding("callbackOnSelectionnerCandidat");
		}
		return callbackOnSelectionnerCandidat;
	}
	
	public WOActionResults onCreerCandidat() {
		//edc().revert();
		boolean doIt = false;

		NSArray<EOQualifier> quals = new NSMutableArray<EOQualifier>();
		
		quals.add(new EOKeyValueQualifier(EOPreCandidat.CAND_NOM_KEY, EOQualifier.QualifierOperatorEqual, srchNom));
		quals.add(new EOKeyValueQualifier(EOPreCandidat.CAND_PRENOM_KEY, EOQualifier.QualifierOperatorEqual, srchPrenom));
		
		EOAndQualifier qualifier = new EOAndQualifier(quals);

		NSArray<EOPreCandidat> res = EOPreCandidat.fetchAll(session().defaultEditingContext(), qualifier);

		if (res.count() > 0) {
			doIt = false;
			EOPreCandidat doublon = (EOPreCandidat) res.objectAtIndex(0);
			String s = "Une personne avec le même nom et prénom existe deja dans le référentiel : " + doublon.candCivilite() + " " + doublon.candPrenom() + " " + doublon.candNom() + " (" + doublon.etudNumero()
					+ "), voulez-vous quand meme en créer une nouvelle ?";
			Boolean confirm = userQuestionDelegate.askUserAsBoolean("question1", s, getUpdateContainerID());
			if (confirm == null) {
				return null;
			}
			else {
				doIt = confirm.booleanValue();
			}
		}
		else {
			doIt = true;
		}

		if (doIt) {
			EOPreCandidat newEOPreCandidat = EOPreCandidat.create(session().defaultEditingContext());
			newEOPreCandidat.setCandNom(srchNom);
			newEOPreCandidat.setCandPrenom(srchPrenom);
			
			session().defaultEditingContext().saveChanges();
			
			userQuestionDelegate.clearAnswers();
			
			selectedCandidat = newEOPreCandidat;
			return selectCandidat();
		}
		else {
			userQuestionDelegate.clearAnswers();
		}
		return null;
	}

	public String getUpdateContainerID() {
		return (String) valueForBinding(BINDING_updateContainerID);
	}

	/**
	 * @param getSubmitTitleIndividu the getSubmitTitleIndividu to set
	 */

	public String getTitleCreateCandidat() {
		return "Créer " + (srchNom() != null ? MyStringCtrl.initCap(srchNom()) : "") + " " + (srchPrenom() != null ? MyStringCtrl.initCap(srchPrenom()) : "") + " dans les candidats";
	}

}