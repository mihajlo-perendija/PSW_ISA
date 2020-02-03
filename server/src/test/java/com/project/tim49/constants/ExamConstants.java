package com.project.tim49.constants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ExamConstants {
	public static final Calendar dateCalendar;

	static {
	    dateCalendar = GregorianCalendar.getInstance();
	    dateCalendar.clear();
	    dateCalendar.set(2016, 1, 1);
	}
	public static final Long DB_ID = 1L;
	
	public static final Date DB_DATE = dateCalendar.getTime();   
	public static final int DB_EXAM_POINTS = 20;
	public static final int DB_LAB_POINTS = 70;
	public static final Long DB_COURSE_ID = 1L;
	public static final Long DB_STUDENT_ID = 1L;
	public static final Long DB_EXAM_PERIOD_ID = 1L;
}
