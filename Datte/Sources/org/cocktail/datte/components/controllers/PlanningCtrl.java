package org.cocktail.datte.components.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.cocktail.datte.Application;
import org.cocktail.datte.pojo.EvenementPojo;
import org.cocktail.datte.pojo.PojoRdvCandidat;
import org.cocktail.datte.utils.Misc;
import org.cocktail.scolarix.serveur.metier.eos.EOPreCandidat;
import org.cocktail.scolarix.serveur.metier.eos.EORdvCandidat;
import org.cocktail.scolarix.serveur.metier.eos.EORdvPlanning;

import com.ibm.icu.util.Calendar;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.qualifiers.ERXInQualifier;
import er.extensions.qualifiers.ERXAndQualifier;

public class PlanningCtrl {

	private EOEditingContext context;

	private NSMutableArray<EvenementPojo> plans;

	private boolean etudiant;

	public PlanningCtrl(EOEditingContext ec, boolean etudiant) {
		this.context = ec;
		this.etudiant = etudiant;
	}

	public NSMutableArray<EvenementPojo> requestCalendar(
			NSTimestamp premier,
			EOGlobalID globalIDForObject, int i) {


		DateFormat jourMois = new SimpleDateFormat("ddMM");
		DateFormat heureMinute = new SimpleDateFormat("HHmm");
		java.util.GregorianCalendar calPrem = new GregorianCalendar();
		calPrem.setTimeInMillis(premier.getTime());

		try {
			if (plans != null) {
				// on vide le planning
				plans.removeAllObjects();
			} else {
				plans = new NSMutableArray<EvenementPojo>();
			}

			EOQualifier qualifier1 = EOQualifier.qualifierWithQualifierFormat(EORdvPlanning.DATE_PER_KEY + "=%@", new NSArray<String>( jourMois.format(calPrem.getTime())));
			EOQualifier qualifier2 = EOQualifier.qualifierWithQualifierFormat(EORdvPlanning.DEB_PERIODE_KEY + "=%@", new NSArray<String>( heureMinute.format(calPrem.getTime())));

			NSArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
			qualifiers.add(qualifier1);
			qualifiers.add(qualifier2);

			ERXAndQualifier qualifier = new ERXAndQualifier(qualifiers);

			NSArray<EORdvPlanning> creneaux = EORdvPlanning.fetchAll(context, qualifier, new NSArray());

			for (EORdvPlanning creneau : creneaux) {
				plans.addObject(eventFromCreneau(creneau));
			}
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			plans = new NSMutableArray<EvenementPojo>();
		}

		return plans;

	}

	private EvenementPojo eventFromCreneau(EORdvPlanning creneau) {
		EvenementPojo event = new EvenementPojo(creneau);

		if (!etudiant) {
			NSMutableArray<Object> conditions2 = new NSMutableArray<Object>();
			Date date = new Date(event.getStart().getTime());
			System.out.println("----------------------------> date : " + date);

			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(date.getTime());
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			
			conditions2.add(cal.getTime());
			conditions2.add(creneau.debPeriode());
			EOQualifier qualifier2 = EOQualifier.qualifierWithQualifierFormat(EORdvCandidat.CONVOC_DATE_KEY 
					+ " = %@ AND " + EORdvCandidat.CONVOC_HEURE_KEY + " = %@", conditions2);
			NSArray<EORdvCandidat> candidats = EORdvCandidat.fetchAll(context, qualifier2, new NSArray()); 
			
			List<PojoRdvCandidat> participants = new ArrayList<PojoRdvCandidat>();
			StringBuffer desc = new StringBuffer();
			PojoRdvCandidat pojoRdvCandidat;
			for (EORdvCandidat eoRdvCandidat : candidats) {
				EOQualifier qualifier = new EOKeyValueQualifier(EORdvCandidat.CAND_KEY_KEY, EOQualifier.QualifierOperatorEqual, eoRdvCandidat.candKey());
				EOPreCandidat candidat = EOPreCandidat.fetchByQualifier(this.context, qualifier);
				
				// String nomPrenom = "" + desc.append(Misc.nomPrenomAffichage(candidat) + "<br/>");
				String nomPrenom = Misc.nomPrenomAffichage(candidat);
				desc.append(nomPrenom + "<br/>");
				pojoRdvCandidat = new PojoRdvCandidat(nomPrenom, eoRdvCandidat.candKey());
				participants.add(pojoRdvCandidat);
			}

			event.setParticipants(participants);
			event.setLibelle(desc.toString());
			
			event.setNbRdvMax(Application.getMaxRdvScol());
			event.setNbRdvScol(Application.getMaxRdvScol() - participants.size() + (event.getNbEtuPer() - event.getNbRdvRest()));
			
			event.setTitle(event.getTitle()+ " (" + event.getNbRdvScol() + " / " + event.getNbRdvMax() +")");
		} else {
			event.setLibelle(event.getStartAsString() + "  " + event.getNbRdvRest() + " place(s) disponible(s).");
		}

		return event;
	}

	public NSMutableArray<EvenementPojo> requestCalendar(
			NSTimestamp premier, NSTimestamp dernier,
			EOGlobalID globalIDForObject, int i) {


		DateFormat sdf = new SimpleDateFormat("ddMM");
		GregorianCalendar calPrem = new GregorianCalendar();
		calPrem.setTimeInMillis(premier.getTime());
		GregorianCalendar calDer = new GregorianCalendar();
		calDer.setTimeInMillis(dernier.getTime());

		try {
			if (plans != null) {
				// on vide le planning
				plans.removeAllObjects();
			} else {
				plans = new NSMutableArray<EvenementPojo>();
			}

			NSMutableArray<String> conditions = new NSMutableArray<String>();
			NSMutableArray<EvenementPojo> liensPdf = new NSMutableArray<EvenementPojo>();
			GregorianCalendar cal = calPrem;
			while (cal.before(calDer)) {
				conditions.add(sdf.format(cal.getTime()));
				EvenementPojo pdf = new EvenementPojo();
				pdf.setNsStart(new NSTimestamp(cal.getTime()));
				pdf.setNsEnd(new NSTimestamp(cal.getTime()));
				pdf.setTitle("Imprimer les rendez vous");
				pdf.setClassName("pdf");
				pdf.setLibelle("Imprimer le planning de la journée");
				pdf.setAllDay(true);
				liensPdf.addObject(pdf);
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			conditions.add(sdf.format(calDer.getTime()));

			ERXInQualifier qualifier = new ERXInQualifier(EORdvPlanning.DATE_PER_KEY, conditions);
			NSArray<EORdvPlanning> creneaux = EORdvPlanning.fetchAll(context, qualifier, new NSArray());
			// NSArray<EORdvPlanning> creneaux = EORdvPlanning.fetchAllFwkScolarix_RdvPlannings(context);

			for (EORdvPlanning creneau : creneaux) {
				plans.addObject(eventFromCreneau(creneau));
			}
			if (!plans.isEmpty()) plans.addAll(liensPdf);
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			plans = new NSMutableArray<EvenementPojo>();
		}

		return plans;
	}


}
