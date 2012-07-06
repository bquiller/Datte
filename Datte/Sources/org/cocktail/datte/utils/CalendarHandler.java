//
// CalendarHandler.java
// Gedagogie
//
// Created by Adour on Fri Mar 05 2004.
// Copyright (c) 2004 __Universite de La Rochelle__. All rights reserved.
//

package org.cocktail.datte.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.webobjects.foundation.NSTimestamp;

public class CalendarHandler {

	private GregorianCalendar calendar;

	public CalendarHandler() {
		// calendar = new GregorianCalendar( NSTimeZone.systemTimeZone() );
		calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
	}

	/** retourne l'annee en cours : indiquee par le calendrier */
	public int currentYear() {
		return calendar.get(Calendar.YEAR);
	}

	public int getDay(NSTimestamp time) {
		calendar.setTime(time);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public void raz() {
		calendar.setTime(new NSTimestamp());
	}

	public void setHour(int hour) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
	}

	public void setMinute(int min) {
		calendar.set(Calendar.MINUTE, min);
	}

	public void setDay(int day) {
		calendar.add(Calendar.DAY_OF_MONTH, day);
	}

	public void setMonth(int month) {
		calendar.add(Calendar.MONTH, month);
	}

	public void setYear(int year) {
		calendar.add(Calendar.YEAR, year);
	}

}
