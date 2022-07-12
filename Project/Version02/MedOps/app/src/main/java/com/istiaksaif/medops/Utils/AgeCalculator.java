package com.istiaksaif.medops.Utils;

import java.util.Calendar;
import java.util.Locale;

public class AgeCalculator {
    private int startYear, startMonth, startDay, endYear, endMonth, endDay, resYear, resMonth, resDay;
    private Calendar calendar;
    private String dayName;
    public String getCurrentDate()
    {
        calendar=Calendar.getInstance();
        endYear=calendar.get(Calendar.YEAR);
        endMonth=calendar.get(Calendar.MONTH);
        endMonth++;
        endDay=calendar.get(Calendar.DAY_OF_MONTH);
        return endDay+":"+endMonth+":"+endYear;
    }
    public String getNameOfDay()
    {
        calendar=Calendar.getInstance();
        dayName=calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
        return dayName;
    }
    public void setDateOfBirth(int sYear, int sMonth, int sDay)
    {
        startYear=sYear;
        startMonth=sMonth;
        startMonth++;
        startDay=sDay;

    }
    public void calcualteYear()
    {
        resYear=endYear-startYear;

    }

    public void calcualteMonth()
    {
        if(endMonth>=startMonth)
        {
            resMonth= endMonth-startMonth;
        }
        else
        {
            resMonth=endMonth-startMonth;
            resMonth=12+resMonth;
            resYear--;
        }

    }
    public void  calcualteDay()
    {

        if(endDay>=startDay)
        {
            resDay= endDay-startDay;
        }
        else
        {
            resDay=endDay-startDay;
            resDay=30+resDay;
            if(resMonth==0)
            {
                resMonth=11;
                resYear--;
            }
            else
            {
                resMonth--;
            }

        }
    }

    public String getResult()
    {
        return resYear+"";
    }
    public String getCurrentDateOfMonthName()
    {
        calendar=Calendar.getInstance();
        String monthname;
        monthname = calendar.getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.ENGLISH);
        endYear=calendar.get(Calendar.YEAR);
        endDay=calendar.get(Calendar.DAY_OF_MONTH);
        return monthname+" "+endDay+", "+endYear;
    }
}
