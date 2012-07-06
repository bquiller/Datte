package org.cocktail.datte.pojo;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.cocktail.datte.utils.CalendarHandler;
import org.cocktail.datte.utils.DateOperation;
import org.cocktail.scolarix.serveur.metier.eos.EORdvCandidat;
import org.cocktail.scolarix.serveur.metier.eos.EORdvPlanning;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import com.webobjects.foundation.NSTimestamp;


public class EvenementPojo implements Serializable {

	private static final long serialVersionUID = 8771948519909077244L;
	private String className;
	private String title;
	private NSTimestamp start;
	private NSTimestamp end;
	private String libelle;
	private List<PojoRdvCandidat> participants;

	private int nbEtuPer;
	private int nbRdvRest;
	private int nbRdvScol;
	private int nbRdvMax;
	private int jour;
	private int resa;
	private int deplace;
	private int rang;

	private boolean allDay;

	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");
	SimpleDateFormat convocHeure = new SimpleDateFormat("HHmm");
	SimpleDateFormat convocDate = new SimpleDateFormat("ddMM");

	public EvenementPojo() {
	}
	public EvenementPojo(NSTimestamp  debut, NSTimestamp fin) {
		start = debut;
		end = fin;
	}

	public EvenementPojo(EORdvPlanning creneau) {
		// Un créneau, en BDD n'a pas d'année ; on va donc dire qu'on travaille sur l'année courante !
		String year = "" + Calendar.getInstance().get(Calendar.YEAR);
				
		NSTimestamp dateDeb = DateOperation.createTimestamp(creneau.debPeriode() + "" + creneau.datePer() + year);
		NSTimestamp dateFin = DateOperation.createTimestamp(creneau.finPeriode() + "" + creneau.datePer() + year);

		this.setNsStart(dateDeb);
		this.setNsEnd(dateFin);

		Integer jour = new Integer(new CalendarHandler().getDay(dateDeb));
		this.setJour(jour);
		this.setResa(new Integer(1));
		StringBuffer titre = new StringBuffer();
		titre.append(creneau.nbRdvRest() + " / " + creneau.nbEtuPer());
		
		if (creneau.nbRdvRest().equals(creneau.nbEtuPer())) {
			this.setClassName("libre");
		} else if (creneau.nbRdvRest() > 0) {
			this.setClassName("entre2");
		} else {
			this.setClassName("plein");
		}
		
//		event.setParticipants(participants);
		this.setNbEtuPer(creneau.nbEtuPer());
		this.setNbRdvRest(creneau.nbRdvRest());
		
		this.setTitle(titre.toString());
		this.setDeplace(new Integer(1));
		this.setRang(1);

		
		//				plans.addObject(dRes);
	}
	
	
	public EvenementPojo(JSONObject json) {
        final SimpleDateFormat sdf = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss'Z'\"");
        
		try {
			this.title = json.getString("title");
			this.libelle = json.getString("libelle");
			
			this.start = new NSTimestamp (sdf.parse(json.getString("start")));
        	this.end = new NSTimestamp (sdf.parse(json.getString("end")));
        	
        	JsonFactory factory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper(factory); 
            TypeReference<List<EORdvCandidat>> typeRef = new TypeReference<List<EORdvCandidat>>() {}; 
            
			this.participants = mapper.readValue(json.getString("participants"), typeRef);

			this.className = json.getString("className");
			if (json.getString("allDay").equals("1")) this.allDay = true;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		return "Title : " + title + " Start : " + getStart() + " End : " + getEnd();
	}

	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getLibelle() {
		return libelle;
	}
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	public int getJour() {
		return jour;
	}
	public void setJour(int jour) {
		this.jour = jour;
	}
	public int getResa() {
		return resa;
	}
	public void setResa(int resa) {
		this.resa = resa;
	}
	public int getDeplace() {
		return deplace;
	}
	public void setDeplace(int deplace) {
		this.deplace = deplace;
	}
	public int getRang() {
		return rang;
	}
	public void setRang(int rang) {
		this.rang = rang;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public NSTimestamp getStart() {
		return start;
	}
	public String getStartAsString() {
		return dateFormatter.format(start);
	}
	public String getStartHourAsString() {
		return hourFormatter.format(start);
	}
	public String getConvocHeure() {
		return convocHeure.format(start);
	}
	public String getConvocDate() {
		return convocDate.format(start);
	}
	public NSTimestamp getStartAsConvocDate() {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(start.getTime());
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		
		return new NSTimestamp(cal.getTime());
	}

	public void setNsStart(NSTimestamp start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public String getEndAsString() {
		return dateFormatter.format(end);
	}
	public String getEndHourAsString() {
		return hourFormatter.format(end);
	}
	public void setNsEnd(NSTimestamp end) {
		this.end = end;
	}

	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	public List<PojoRdvCandidat> getParticipants() {
		return participants;
	}
	public void setParticipants(List<PojoRdvCandidat> participants) {
		this.participants = participants;
	}
	public int getNbEtuPer() {
		return nbEtuPer;
	}
	public void setNbEtuPer(int nbEtuPer) {
		this.nbEtuPer = nbEtuPer;
	}
	public int getNbRdvRest() {
		return nbRdvRest;
	}
	public void setNbRdvRest(int nbRdvRest) {
		this.nbRdvRest = nbRdvRest;
	}
	public int getNbRdvScol() {
		return nbRdvScol;
	}
	public void setNbRdvScol(int nbRdvScol) {
		this.nbRdvScol = nbRdvScol;
	}
	public int getNbRdvMax() {
		return nbRdvMax;
	}
	public void setNbRdvMax(int nbRdvMax) {
		this.nbRdvMax = nbRdvMax;
	}
}
