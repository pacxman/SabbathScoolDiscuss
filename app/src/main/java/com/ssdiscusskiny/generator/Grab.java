package com.ssdiscusskiny.generator;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Grab {

	/* Developed by N. Bahati Pacifique 21/03/2023 */

	public static Calendar lastFriday(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(cal.getTime());
		calendar.set(GregorianCalendar.DAY_OF_WEEK, Calendar.FRIDAY);
		calendar.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, -1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static Calendar lastSaturday(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(cal.getTime());
		calendar.set(GregorianCalendar.DAY_OF_WEEK, Calendar.SATURDAY);
		calendar.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, -1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static Calendar firstSaturday(Calendar cal) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(GregorianCalendar.DAY_OF_WEEK, Calendar.SATURDAY);
		calendar.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	public static String year(Calendar cal) {
		Calendar calP = Calendar.getInstance();
		calP.setTime(cal.getTime());
		calP.set(Calendar.HOUR, 0);
		calP.set(Calendar.MINUTE, 0);
		calP.set(Calendar.SECOND, 0);
		calP.set(Calendar.MILLISECOND, 0);

		Calendar calDec = Calendar.getInstance();
		calDec.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		calDec.set(Calendar.MONTH, Calendar.DECEMBER);
		calDec.setTime(lastSaturday(calDec).getTime());

		calDec.set(Calendar.HOUR, 0);
		calDec.set(Calendar.MINUTE, 0);
		calDec.set(Calendar.SECOND, 0);
		calDec.set(Calendar.MILLISECOND, 0);
		if (calP.compareTo(calDec) >= 0) {
			return String.valueOf(calDec.get(Calendar.YEAR) + 1);
		}
		return String.valueOf(calDec.get(Calendar.YEAR));

	}

	public static String quarter(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Calendar q1S = Calendar.getInstance();
		q1S.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q1S.add(Calendar.YEAR, -1);
		q1S.set(q1S.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q1S.setTime(lastSaturday(q1S).getTime());
		//System.out.println("\nQ1 Start "+q1S.getTime());

		Calendar q1E = Calendar.getInstance();
		q1E.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q1E.set(q1E.get(Calendar.YEAR), Calendar.MARCH, 1);
		q1E.setTime(lastSaturday(q1E).getTime());
		q1E.add(Calendar.DATE, -1);
		//System.out.println("Q1 END "+q1E.getTime());

		Calendar q2S = Calendar.getInstance();
		q2S.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q2S.set(q2S.get(Calendar.YEAR), Calendar.MARCH, 1);
		q2S.setTime(lastSaturday(q2S).getTime());
		//System.out.println("Q2 Start "+q2S.getTime());

		Calendar q2E = Calendar.getInstance();
		q2E.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q2E.set(q2E.get(Calendar.YEAR), Calendar.JUNE, 1);
		q2E.setTime(lastSaturday(q2E).getTime());
		q2E.add(Calendar.DATE, -1);
		//System.out.println("Q2 END "+q2E.getTime());

		Calendar q3S = Calendar.getInstance();
		q3S.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q3S.set(q3S.get(Calendar.YEAR), Calendar.JUNE, 1);
		q3S.setTime(lastSaturday(q3S).getTime());
		//System.out.println("Q3 Start "+q3S.getTime());

		Calendar q3E = Calendar.getInstance();
		q3E.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q3E.set(q3E.get(Calendar.YEAR), Calendar.SEPTEMBER, 1);
		q3E.setTime(lastSaturday(q3E).getTime());
		q3E.add(Calendar.DATE, -1);
		//System.out.println("Q3 END: "+q3E.getTime());

		Calendar q4S = Calendar.getInstance();
		q4S.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q4S.set(q4S.get(Calendar.YEAR), Calendar.SEPTEMBER, 1);
		q4S.setTime(lastSaturday(q4S).getTime());
		//System.out.println("Q4 Start "+q4S.getTime());

		Calendar q4E = Calendar.getInstance();
		q4E.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q4E.set(q4E.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q4E.setTime(lastSaturday(q4E).getTime());
		q4E.add(Calendar.DATE, -1);

		Calendar q1SN = Calendar.getInstance();
		q1SN.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		q1SN.set(q1SN.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q1SN.setTime(lastSaturday(q1SN).getTime());
		//System.out.println("Q1SNY Start "+q1SN.getTime()+"\n");

		Calendar q1SNE = Calendar.getInstance();
		q1SNE.setTime(q1SN.getTime());
		q1SNE.add(Calendar.YEAR, 1);
		q1SNE.set(q1SNE.get(Calendar.YEAR), Calendar.MARCH, 1);
		q1SNE.setTime(lastSaturday(q1SNE).getTime());

		if (cal.compareTo(q1S) >= 0 && cal.compareTo(q1E) <= 0) {
			return "Q1";
		}
		if (cal.compareTo(q2S) >= 0 && cal.compareTo(q2E) <= 0) {
			return "Q2";
		}
		if (cal.compareTo(q3S) >= 0 && cal.compareTo(q3E) <= 0) {
			return "Q3";
		}
		if (cal.compareTo(q4S) >= 0 && cal.compareTo(q4E) <= 0) {
			return "Q4";
		}
		if (cal.compareTo(q1SN) >= 0 && cal.compareTo(q1SNE) <= 0) {
			return "Q1";
		}
		return "Q_ERROR";
	}

	public static Calendar quarterStart(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Calendar q1S = Calendar.getInstance();
		q1S.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q1S.add(Calendar.YEAR, -1);
		q1S.setTime(lastSaturday(q1S).getTime());

		Calendar q1E = Calendar.getInstance();
		q1E.set(cal.get(Calendar.YEAR), Calendar.MARCH, 1);
		q1E.setTime(lastSaturday(q1E).getTime());
		q1E.add(Calendar.DATE, -1);
		System.out.println("Q1 END: "+q1E.getTime());

		Calendar q2S = Calendar.getInstance();
		q2S.set(cal.get(Calendar.YEAR), Calendar.MARCH, 1);
		q2S.setTime(lastSaturday(q2S).getTime());

		Calendar q2E = Calendar.getInstance();
		q2E.set(cal.get(Calendar.YEAR), Calendar.JUNE, 1);
		q2E.setTime(lastSaturday(q2E).getTime());
		q2E.add(Calendar.DATE, -1);
		System.out.println("Q2 END: "+q2E.getTime());

		Calendar q3S = Calendar.getInstance();
		q3S.set(cal.get(Calendar.YEAR), Calendar.JUNE, 1);
		q3S.setTime(lastSaturday(q3S).getTime());

		Calendar q3E = Calendar.getInstance();
		q3E.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, 1);
		q3E.setTime(lastSaturday(q3E).getTime());
		q3E.add(Calendar.DATE, -1);
		System.out.println("Q3 END: "+q3E.getTime());

		Calendar q4S = Calendar.getInstance();
		q4S.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, 1);
		q4S.setTime(lastSaturday(q4S).getTime());

		Calendar q4E = Calendar.getInstance();
		q4E.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q4E.setTime(lastSaturday(q4E).getTime());
		q4E.add(Calendar.DATE, -1);
		System.out.println("Q4 END: "+q4E.getTime());

		Calendar q1SN = Calendar.getInstance();
		q1SN.setTime(cal.getTime());
		q1SN.set(Calendar.MONTH, Calendar.DECEMBER);
		q1SN.setTime(lastSaturday(q1SN).getTime());

		Calendar q1SNE = Calendar.getInstance();
		q1SNE.set(cal.get(Calendar.YEAR), Calendar.MARCH, 1);
		q1SNE.add(Calendar.YEAR, 1);
		q1SNE.setTime(lastSaturday(q1SNE).getTime());
		q1SNE.add(Calendar.DATE, -1);
		System.out.println("Q1SN END: "+q1E.getTime());

		if (cal.compareTo(q1S) >= 0 && cal.compareTo(q1E) <= 0) {
			return q1S;
		}
		if (cal.compareTo(q2S) >= 0 && cal.compareTo(q2E) <= 0) {
			return q2S;
		}
		if (cal.compareTo(q3S) >= 0 && cal.compareTo(q3E) <= 0) {
			return q3S;
		}
		if (cal.compareTo(q4S) >= 0 && cal.compareTo(q4E) <= 0) {
			return q4S;
		}
		if (cal.compareTo(q1SN) >= 0 && cal.compareTo(q1SNE) <= 0) {
			return q1SN;
		}

		return cal;
	}

	public static Calendar quarterEnd(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Calendar q1S = Calendar.getInstance();
		q1S.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q1S.add(Calendar.YEAR, -1);
		q1S.setTime(lastSaturday(q1S).getTime());

		Calendar q1E = Calendar.getInstance();
		q1E.set(cal.get(Calendar.YEAR), Calendar.MARCH, 1);
		q1E.setTime(lastSaturday(q1E).getTime());
		q1E.add(Calendar.DAY_OF_WEEK, -1);

		Calendar q2S = Calendar.getInstance();
		q2S.set(cal.get(Calendar.YEAR), Calendar.MARCH, 1);
		q2S.setTime(lastSaturday(q2S).getTime());

		Calendar q2E = Calendar.getInstance();
		q2E.set(cal.get(Calendar.YEAR), Calendar.JUNE, 1);
		q2E.setTime(lastSaturday(q2E).getTime());
		q2E.add(Calendar.DAY_OF_WEEK, -1);

		Calendar q3S = Calendar.getInstance();
		q3S.set(cal.get(Calendar.YEAR), Calendar.JUNE, 1);
		q3S.setTime(lastSaturday(q3S).getTime());

		Calendar q3E = Calendar.getInstance();
		q3E.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, 1);
		q3E.setTime(lastSaturday(q3E).getTime());
		q3E.add(Calendar.DAY_OF_WEEK, -1);

		Calendar q4S = Calendar.getInstance();
		q4S.set(cal.get(Calendar.YEAR), Calendar.SEPTEMBER, 1);
		q4S.setTime(lastSaturday(q4S).getTime());

		Calendar q4E = Calendar.getInstance();
		q4E.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q4E.setTime(lastSaturday(q4E).getTime());
		q4E.add(Calendar.DAY_OF_WEEK, -1);

		Calendar q1SN = Calendar.getInstance();
		q1SN.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 1);
		q1SN.setTime(lastSaturday(q1SN).getTime());

		Calendar q1SNE = Calendar.getInstance();
		q1SNE.set(cal.get(Calendar.YEAR), Calendar.MARCH, 1);
		q1SNE.add(Calendar.YEAR, 1);
		q1SNE.setTime(lastSaturday(q1SNE).getTime());
		q1SNE.add(Calendar.DAY_OF_WEEK, -1);

		if (cal.compareTo(q1S) >= 0 && cal.compareTo(q1E) <= 0) {
			return q1E;
		}
		if (cal.compareTo(q2S) >= 0 && cal.compareTo(q2E) <= 0) {
			return q2E;
		}
		if (cal.compareTo(q3S) >= 0 && cal.compareTo(q3E) <= 0) {
			return q3E;
		}
		if (cal.compareTo(q4S) >= 0 && cal.compareTo(q4E) <= 0) {
			return q4E;
		}
		if (cal.compareTo(q1SN) >= 0 && cal.compareTo(q1SNE) <= 0) {
			return q1SNE;
		}
		return cal;
	}

	public static String lesson(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Calendar l1S = Calendar.getInstance();
		l1S.setTime(quarterStart(cal).getTime());

		Calendar l1E = Calendar.getInstance();
		l1E.setTime(l1S.getTime());
		l1E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l2S = Calendar.getInstance();
		l2S.setTime(l1E.getTime());
		l2S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l2E = Calendar.getInstance();
		l2E.setTime(l2S.getTime());
		l2E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l3S = Calendar.getInstance();
		l3S.setTime(l2E.getTime());
		l3S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l3E = Calendar.getInstance();
		l3E.setTime(l3S.getTime());
		l3E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l4S = Calendar.getInstance();
		l4S.setTime(l3E.getTime());
		l4S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l4E = Calendar.getInstance();
		l4E.setTime(l4S.getTime());
		l4E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l5S = Calendar.getInstance();
		l5S.setTime(l4E.getTime());
		l5S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l5E = Calendar.getInstance();
		l5E.setTime(l5S.getTime());
		l5E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l6S = Calendar.getInstance();
		l6S.setTime(l5E.getTime());
		l6S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l6E = Calendar.getInstance();
		l6E.setTime(l6S.getTime());
		l6E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l7S = Calendar.getInstance();
		l7S.setTime(l6E.getTime());
		l7S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l7E = Calendar.getInstance();
		l7E.setTime(l7S.getTime());
		l7E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l8S = Calendar.getInstance();
		l8S.setTime(l7E.getTime());
		l8S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l8E = Calendar.getInstance();
		l8E.setTime(l8S.getTime());
		l8E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l9S = Calendar.getInstance();
		l9S.setTime(l8E.getTime());
		l9S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l9E = Calendar.getInstance();
		l9E.setTime(l9S.getTime());
		l9E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l10S = Calendar.getInstance();
		l10S.setTime(l9E.getTime());
		l10S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l10E = Calendar.getInstance();
		l10E.setTime(l10S.getTime());
		l10E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l11S = Calendar.getInstance();
		l11S.setTime(l10E.getTime());
		l11S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l11E = Calendar.getInstance();
		l11E.setTime(l11S.getTime());
		l11E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l12S = Calendar.getInstance();
		l12S.setTime(l11E.getTime());
		l12S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l12E = Calendar.getInstance();
		l12E.setTime(l12S.getTime());
		l12E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar l13S = Calendar.getInstance();
		l13S.setTime(l12E.getTime());
		l13S.add(Calendar.DAY_OF_MONTH, 1);

		Calendar l13E = Calendar.getInstance();
		l13E.setTime(l13S.getTime());
		l13E.add(Calendar.DAY_OF_MONTH, 6);

		Calendar lastFr = Calendar.getInstance();
		lastFr.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		lastFr.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		lastFr.setTime(lastSaturday(lastFr).getTime());
		lastFr.add(Calendar.DAY_OF_WEEK, -1);
		lastFr.setTime(quarterEnd(lastFr).getTime());

		// comparison
		if (cal.compareTo(l1S) >= 0 && cal.compareTo(l1E) <= 0) {
			return "L1";
		}
		if (cal.compareTo(l2S) >= 0 && cal.compareTo(l2E) <= 0) {
			return "L2";
		}
		if (cal.compareTo(l3S) >= 0 && cal.compareTo(l3E) <= 0) {
			return "L3";
		}
		if (cal.compareTo(l4S) >= 0 && cal.compareTo(l4E) <= 0) {
			return "L4";
		}
		if (cal.compareTo(l5S) >= 0 && cal.compareTo(l5E) <= 0) {
			return "L5";
		}
		if (cal.compareTo(l6S) >= 0 && cal.compareTo(l6E) <= 0) {
			return "L6";
		}
		if (cal.compareTo(l7S) >= 0 && cal.compareTo(l7E) <= 0) {
			return "L7";
		}
		if (cal.compareTo(l8S) >= 0 && cal.compareTo(l8E) <= 0) {
			return "L8";
		}
		if (cal.compareTo(l9S) >= 0 && cal.compareTo(l9E) <= 0) {
			return "L9";
		}
		if (cal.compareTo(l10S) >= 0 && cal.compareTo(l10E) <= 0) {
			return "L10";
		}
		if (cal.compareTo(l11S) >= 0 && cal.compareTo(l11E) <= 0) {
			return "L11";
		}
		if (cal.compareTo(l12S) >= 0 && cal.compareTo(l12E) <= 0) {
			return "L12";
		}
		if (cal.compareTo(l13S) >= 0 && cal.compareTo(l13E) <= 0) {
			return "L13";
		}
		if (cal.compareTo(l13E) > 0 && cal.compareTo(lastFr) <= 0) {
			return "L14";
		}
		if (cal.compareTo(lastFr) > 0 && cal.compareTo(quarterEnd(cal)) <= 0) {
			return "L1";
		}

		return "L_ERROR";

	}

	public static String lessonName(String lessKey){
		switch (lessKey){
			case "L1":
				return "Icyigisho cya 1";
			case "L2":
				return "Icyigisho cya 2";
			case "L3":
				return "Icyigisho cya 3";
			case "L4":
				return "Icyigisho cya 4";
			case "L5":
				return "Icyigisho cya 5";
			case "L6":
				return "Icyigisho cya 6";
			case "L7":
				return "Icyigisho cya 7";
			case "L8":
				return "Icyigisho cya 8";
			case "L9":
				return "Icyigisho cya 9";
			case "L10":
				return "Icyigisho cya 10";
			case "L11":
				return "Icyigisho cya 11";
			case "L12":
				return "Icyigisho cya 12";
			case "13":
				return "Icyigisho cya 13";
			case "L14":
				return "Icyigisho cya 14";
			default:
				return "";

		}
	}

}