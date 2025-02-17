package com.eun.percentify;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalculatorManager {
    private static final String TAG = "@@@@CalculatorManager";
    private static Date currentTime = Calendar.getInstance().getTime();
    private static GregorianCalendar gc;
    private static double[] monthDays = { 31,28,31,30,31,30,31,31,30,31,30,31};
    private static double year = Integer.parseInt(new SimpleDateFormat("y",Locale.getDefault()).format(currentTime));       // 년
    private static double month = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(currentTime));     // 달
    private static double day = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(currentTime));       // 일
    private static double hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(currentTime));     // 0-24
    private static double dayNum = Integer.parseInt(new SimpleDateFormat("u", Locale.getDefault()).format(currentTime));    // 일주일 중 몇번 째 요일 1-7
    private static double yearNum = Integer.parseInt(new SimpleDateFormat("D", Locale.getDefault()).format(currentTime));   // 일년 중 몇번 째 일

    public static void init(){
        Date currentTime = Calendar.getInstance().getTime();
        gc = new GregorianCalendar();
        year = Integer.parseInt(new SimpleDateFormat("y",Locale.getDefault()).format(currentTime));       // 년
        month = Integer.parseInt(new SimpleDateFormat("M", Locale.getDefault()).format(currentTime));     // 달
        day = Integer.parseInt(new SimpleDateFormat("d", Locale.getDefault()).format(currentTime));       // 일
        hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(currentTime));     // 0-24
        dayNum = Integer.parseInt(new SimpleDateFormat("u", Locale.getDefault()).format(currentTime));    // 일주일 중 몇번 째 요일 1-7
        yearNum = Integer.parseInt(new SimpleDateFormat("D", Locale.getDefault()).format(currentTime));   // 일년 중 몇번 째 일
    }

    public static int getToday(){
        init();
        hour = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(currentTime));
        int percent = (int) Math.round((hour*100)/24);
        Log.d(TAG, "getToday: percent = "+percent);

        return percent;
    }
    public static int getWeek(){
        init();
        int percent = (int)Math.round(((hour+(dayNum*24))/168)*100);
        Log.d(TAG, "getWeek: percent = " + percent);

        return percent;
    }
    public static int getMonth(){
        init();
        int _hour = (int)((day-1)*24 + hour);
        gc = new GregorianCalendar();

        if(gc.isLeapYear((int)year) && month==2) monthDays[1]+=1;

        int percent = (int)Math.round((_hour/(monthDays[(int)month-1]*24))*100);
        Log.d(TAG, "getMonth: percent = " + percent);
        Log.d(TAG, "getMonth: dayNum = "+dayNum);

        return percent;
    }
    public static int getYear(){
        init();
        int yearSum = 365;
        gc = new GregorianCalendar();
        if(gc.isLeapYear((int)year)) yearSum+=1;

        int percent = (int) Math.round((yearNum/yearSum)*100);
        Log.d(TAG, "getYear: percent = " + percent);

        return percent;
    }
    public static int getMonthEnd(){
        init();
        if(gc.isLeapYear((int)year)) monthDays[1]+=1;
        return (int)monthDays[(int)month-1];
    }
    public static int getYearEnd(){
        init();
        if(gc.isLeapYear((int)year)) return 366;
        return 365;
    }

    public static String getCurrentYear(){
        init();
        return Integer.toString((int)year);
    }

    public static String getCurrentMonth(){
        init();
        return Integer.toString((int)month);
    }

    public static String getCurrentDay(){
        init();
        return Integer.toString((int)day);
    }

    public static String getCurrentHour(){
        init();
        return Integer.toString((int)hour);
    }

    public static String getCurrentDayName(int cur){
        switch (cur){
            case 2:
                return "월";
            case 3:
                return "화";
            case 4:
                return "수";
            case 5:
                return "목";
            case 6:
                return "금";
            case 7:
                return "토";
            case 1:
                return "일";
        }
        return "";
    }
}
