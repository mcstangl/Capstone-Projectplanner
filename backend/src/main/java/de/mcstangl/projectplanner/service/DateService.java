package de.mcstangl.projectplanner.service;

import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;

@Service
public class DateService {

    public Date addBusinessDays(Date date, int days) {
        int businessDaysToAdd = calculateBusinessDays(date, days);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, businessDaysToAdd);
        return new Date(calendar.getTimeInMillis());
    }

    private int calculateBusinessDays(Date date, int daysToAdd){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && daysToAdd <= 4){
            return daysToAdd;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && daysToAdd > 4 && daysToAdd <=9){
            return daysToAdd + 2;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && daysToAdd > 9){
            return daysToAdd + 4;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && daysToAdd <= 3){
            return daysToAdd;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && daysToAdd > 3 && daysToAdd <= 8){
            return daysToAdd + 2;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && daysToAdd > 8){
            return daysToAdd + 4;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && daysToAdd <= 2){
            return daysToAdd;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && daysToAdd > 2 && daysToAdd <=7){
            return daysToAdd + 2;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && daysToAdd > 7){
            return daysToAdd + 4;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && daysToAdd == 1){
            return daysToAdd;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && daysToAdd > 1 && daysToAdd <=6){
            return daysToAdd + 2;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && daysToAdd > 6){
            return daysToAdd + 4;
        }

        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && daysToAdd <=5){
            return daysToAdd + 2;
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && daysToAdd > 5){
            return daysToAdd + 4;
        }

        throw new IllegalArgumentException("Calculation of Business days failed!");
    }

}
