package org.cocktail.datte.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cocktail.datte.components.controllers.PlanningCtrl;
import org.cocktail.datte.pojo.EvenementPojo;
import org.cocktail.datte.utils.PrintFactory;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlwebapp.server.CktlDataResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestamp;

import er.ajax.AjaxProxy;

public class GenericCalendar extends MyWOComponent {

	private static final long serialVersionUID = -5673113379993989646L;

	private AjaxProxy ajaxProxy;

	private NSTimeZone tz = NSTimeZone.timeZoneWithName("Europe/Paris", true);
	private PlanningCtrl planCtrl;

	private Date oldStart;

	private NSTimestamp nsDatePer;

	private Date currentDate;
	
	public GenericCalendar(WOContext context) {
		super(context);
		currentDate = oldStart = new Date();
	}

	public WOActionResults modifyEvent() {
		// TODO 
		System.out.println("In modifyEvent");
		return pageWithName("AddEvent");
	}

	/**
	 * @return the isEditable
	 */
	public boolean isEditable() {
		
		return true;
	}

	// Transform the JSON feed into Java Object
	public void eventAtIndex(String eventAsString) {
		System.out.println("JSONObject ? " + eventAsString);
		JSONObject json;
		try {
			json = new JSONObject(eventAsString);

			SimpleDateFormat sdf = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss'Z'\"");
			EOIndividu eoind = EOIndividu.fetchByKeyValue(session().defaultEditingContext(), "noIndividu", session.applicationUser().getNoIndividu());

			session.setEvent(planCtrl.requestCalendar(new NSTimestamp (sdf.parse(json.getString("start")).getTime(), tz),
					session().defaultEditingContext().globalIDForObject(eoind),0).get(0));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void addEvent(Date debut, Date fin, boolean allDay) {
		EvenementPojo newEvent = new EvenementPojo(new NSTimestamp(debut.getTime(), tz),new NSTimestamp(fin.getTime(), tz));
		newEvent.setAllDay(allDay);

		System.out.println(newEvent);
		session.setEvent(newEvent);
	}

	/**
	 * @return the ajaxProxy
	 */
	public AjaxProxy ajaxProxy() {
		return ajaxProxy;
	}

	/**
	 * @param ajaxProxy the ajaxProxy to set
	 */
	public void setAjaxProxy(AjaxProxy ajaxProxy) {
		this.ajaxProxy = ajaxProxy;
	}
	/**
	 */
	public void setReservations(Date start, Date end) {
		System.out.println("Start : " + start + " End : " + end);

		NSMutableArray<EvenementPojo> reservations = new NSMutableArray<EvenementPojo>();
		EOIndividu eoind = EOIndividu.fetchByKeyValue(session().defaultEditingContext(), "noIndividu", session.applicationUser().getNoIndividu());

		planCtrl = new PlanningCtrl(session.defaultEditingContext(),session().isEtudiant());

		// reservations.addAll(planCtrl.requestCalendarIndividu(new NSTimestamp(start), new NSTimestamp(end), session().defaultEditingContext().globalIDForObject(eoind), 0));
		reservations.addAll(planCtrl.requestCalendar(new NSTimestamp(start.getTime(), tz), new NSTimestamp(end.getTime(), tz),session().defaultEditingContext().globalIDForObject(eoind),0));

		System.out.println("Reservation : " + reservations.count());
		
		session.setReservations(reservations);
	}

	/**
	 * ShowAction, result accessible par /Datte.woa/ra/ReservationTest/1.json
	 * @return un tableau de POJO
	 */
	public NSArray<EvenementPojo> events(Date start, Date end) {
		if (start != oldStart || session.getReservations() == null) {
			currentDate = oldStart;
			oldStart = start;
			System.out.println("-------------------> set old Start " + oldStart);
			setReservations(start,end);
		}
		return session.getReservations();
	}

	public WOActionResults refresh() {
		System.out.println("Refresh ...");
		session.setReservations(null);
		currentDate = oldStart;
		return null;
	}

	public WOActionResults dateImpression(String eventAsString) {
		System.out.println("JSONObject ? " + eventAsString);
		JSONObject json;
		try {
			json = new JSONObject(eventAsString);

			SimpleDateFormat sdf = new SimpleDateFormat("\"yyyy-MM-dd'T'HH:mm:ss'Z'\"");

			nsDatePer = new NSTimestamp (sdf.parse(json.getString("start")).getTime(), tz);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	public WOActionResults onImprimer() {
			DateFormat jourMois = new SimpleDateFormat("ddMM");
			String datePer = jourMois.format(nsDatePer);
			
			String path = WOApplication.application().resourceManager()
			.pathURLForResourceNamed(
					"reports/" + PrintFactory.JASPER_RENDEZ_VOUS, null,
					null).getPath();
			path = path.substring(0, path
					.lastIndexOf(PrintFactory.JASPER_RENDEZ_VOUS));

//			System.out.println("------------------------------> datePer " + planCtrl.getDatePer() + " fannKey " + application.getFannKey());
			
			NSData data = PrintFactory.printRendezVous(application, session, path, datePer, application.getFannKey().toString());

			if (data == null) {
				// return ((Session) session()).afficherErreur(new Exception("Impossible d'imprimer le bulletin " + fileName));
			}
			String fileName = "rendez-vous" + datePer + application.getFannKey() + ".pdf";

			CktlDataResponse resp = new CktlDataResponse();
			if (data != null) {
				resp.setContent(data, CktlDataResponse.MIME_PDF);
				resp.setFileName(fileName);
			} else {
				resp.setContent("");
				resp.setHeader("0", "Content-Length");
			}
			return resp.generateResponse();
	}

	public long getCurrentDate() {
		return currentDate.getTime();
	}
}