package org.cocktail.datte.utils;
import java.text.ParseException;

import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

public class DateOperation {

	static public NSTimestamp createTimestamp(String hhmmddmmyyyy) {
		if (hhmmddmmyyyy==null || "".equals(hhmmddmmyyyy)) return null;
		NSTimestamp ts = null;
		try {
			ts = (NSTimestamp) new NSTimestampFormatter("HHmmddMMyyyy").parseObject(hhmmddmmyyyy);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return ts;
	}
}
