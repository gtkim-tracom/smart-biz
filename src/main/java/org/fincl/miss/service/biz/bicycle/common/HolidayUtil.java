package org.fincl.miss.service.biz.bicycle.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

import com.ibm.icu.util.ChineseCalendar;


import java.util.Arrays;

public class HolidayUtil {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static String[] solarArr =  new String[]{"0101", "0301", "0505", "0606", "0815", "1225"};
	private static String[] lunarArr = new String[]{"0101", "0102", "0408", "0814", "0815", "0816"};
	
	/**
	 * 해당일자가 법정공휴일, 대체공휴일, 토요일, 일요일인지 확인
	 * @param date 양력날짜 (yyyyMMdd)
	 * @return 법정공휴일, 대체공휴일, 토요일, 일요일이면 true, 아니면 false
	 */
	
	public static boolean isHoliday(String date) {
		try{
			return isHolidaySolar(date) || isHolidayLunar(date) || isHolidayAlternate(date) || isWeekend(date);
		}catch(ParseException e){
			
			
			return false;
		}
	}
	
	/**
	 * 토요일 또는 일요일인지 확인하는 로직 (주말이면 true)
	 * @param date 양력날짜 (yyyyMMdd)
	 * @return 토요일인지, 일요일인지
	 * @throws java.text.ParseException 
	 */
	private static boolean isWeekend(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(date));
		
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}
	
	/**
	 * 법정공휴일인지 확인하는 로직 (공휴일이면 true)
	 * @param date 양력날짜 (yyyyMMdd)
	 * @return 음력 공휴일이면 true
	 */
	private static boolean isHolidayLunar(String date) {
		try{
			Calendar cal = Calendar.getInstance();
			ChineseCalendar chinaCal = new ChineseCalendar();
			
			cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0,4)));
			cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4,6)) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

			chinaCal.setTimeInMillis(cal.getTimeInMillis());
			
			// 음력으로 변환된 월과 일자
			
			int mm = chinaCal.get(ChineseCalendar.MONTH) + 1;
			int dd = chinaCal.get(ChineseCalendar.DAY_OF_MONTH);
			
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%02d", mm));
			sb.append(String.format("%02d", dd));
			
			// 음력 12월의 마지막날 (설날 첫번째 휴일)인지 확인
			if(mm == 12){
				int lastDate = chinaCal.getActualMaximum(ChineseCalendar.DAY_OF_MONTH);
				
				if(dd == lastDate){
					return true;
				}
			}
			
			// 음력 휴일에 포함되는지 여부 리턴
			
			return Arrays.asList(lunarArr).contains(sb.toString());
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 해당일자가 양력 법정공휴일에 해당되는지 확인
	 * @param date 양력날짜 (yyyyMMdd)
	 * @return 양력 공휴일이면 true
	 */
	private static boolean isHolidaySolar(String date){
		try{
			// 공휴일에 포함여부 리턴
			return Arrays.asList(solarArr).contains(date.substring(4));
		}catch(Exception e){
			System.out.println(e.getStackTrace());
			return false;
		}
	}
	
	/**
	 * 해당일자가 대체공휴일에 해당하는지 확인
	 * @param 양력날짜 (yyyyMMdd)
	 * @return 대체공휴일이면 true
	 */
	private static boolean isHolidayAlternate(String date){
		int year = Integer.parseInt(date.substring(0, 4));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		// 설날
		String dayFirst2 = convertLunarToSolar(year + "0101");
		String dayFirst3 = convertLunarToSolar(year + "0102");
		String dayFirst1 = String.valueOf(Integer.parseInt(dayFirst2) - 1);
		
		// 추석
		String dayThanks1 = convertLunarToSolar(year + "0814");
		String dayThanks2 = convertLunarToSolar(year + "0815");
		String dayThanks3 = convertLunarToSolar(year + "0816");
		
		// 어린이날
		String dayChild = year + "0505";
		
		// 해당 년도의 대체휴일 목록
		List<String> altHoliday = new ArrayList<String>();
		
		if(getDayOfWeek(dayFirst1) == Calendar.SUNDAY || getDayOfWeek(dayFirst2) == Calendar.SUNDAY || getDayOfWeek(dayFirst3) == Calendar.SUNDAY || 
				isHolidaySolar(dayFirst1) || isHolidaySolar(dayFirst2) || isHolidaySolar(dayFirst3)){
			int y = Integer.parseInt(dayFirst3.substring(0, 4));
			int m = Integer.parseInt(dayFirst3.substring(4, 6)) - 1;
			int d = Integer.parseInt(dayFirst3.substring(6)) + 1;
			
			Calendar c = Calendar.getInstance();
			c.set(y, m, d);
			altHoliday.add(sdf.format(c.getTime()));
		}
		
		if(getDayOfWeek(dayThanks1) == Calendar.SUNDAY || getDayOfWeek(dayThanks2) == Calendar.SUNDAY || getDayOfWeek(dayThanks3) == Calendar.SUNDAY || 
				isHolidaySolar(dayThanks1) || isHolidaySolar(dayThanks2) || isHolidaySolar(dayThanks3)){
			int y = Integer.parseInt(dayThanks3.substring(0, 4));
			int m = Integer.parseInt(dayThanks3.substring(4, 6)) - 1;
			int d = Integer.parseInt(dayThanks3.substring(6)) + 1;
			
			Calendar c = Calendar.getInstance();
			c.set(y, m, d);
			altHoliday.add(sdf.format(c.getTime()));
		}
		
		int childWeek = getDayOfWeek(dayChild);
		
		if(childWeek == Calendar.SATURDAY || childWeek == Calendar.SUNDAY){
			int y = Integer.parseInt(dayChild.substring(0, 4));
			int m = Integer.parseInt(dayChild.substring(4, 6)) - 1;
			int d = Integer.parseInt(dayChild.substring(6)) + 1;
			
			Calendar c = Calendar.getInstance();
			c.set(y, m, d);
			altHoliday.add(sdf.format(c.getTime()));
		}
		
		return altHoliday.contains(date);
	}
	
	/**
	 * 음력날짜를 양력날자로 변환
	 * @param 음력날짜 (yyyyMMdd)
	 * @return 양력날짜 (yyyyMMdd)
	 */
	private static String convertLunarToSolar(String yyyyMMdd){
		ChineseCalendar cc = new ChineseCalendar();
		Calendar cal = Calendar.getInstance();
		
		if(yyyyMMdd == null)
			return "";
		
		String date = yyyyMMdd.trim();
		
		if(date.length() != 8){
			if(date.length() == 4)
				date = date + "0101";
			else if (date.length() == 6)
				date = date + "01";
				else if (date.length() > 8)
					date = date.substring(0, 8);
				else
					return "";
		}
		
		cc.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
		cc.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
		cc.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));
		
		cal.setTimeInMillis(cc.getTimeInMillis());
		
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH) + 1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		
		StringBuffer ret = new StringBuffer();
		ret.append(String.format("%04d", y));
		ret.append(String.format("%02d", m));
		ret.append(String.format("%02d", d));
		
		return ret.toString();
	}
	
	/**
	 * 양력날짜의 요일을 리턴
	 * @param 양력날짜 (yyyyMMdd)
	 * @return 요일 (int)
	 */
	private static int getDayOfWeek(String day){
		int y = Integer.parseInt(day.substring(0, 4));
		int m = Integer.parseInt(day.substring(4, 6)) - 1;
		int d = Integer.parseInt(day.substring(6));
		
		Calendar c = Calendar.getInstance();
		c.set(y, m, d);
		
		return c.get(Calendar.DAY_OF_WEEK);
	}
	
}
