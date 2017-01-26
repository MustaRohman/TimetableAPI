package timetable;

import java.util.Calendar;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Period {

    public enum PERIOD_TYPE {
        SUBJECT,
        BREAK,
        REWARD
    }

    private PERIOD_TYPE type;
    private Topic topic;
    private int number;
    private Calendar dateTime;
    private int periodDuration;

    public Period(PERIOD_TYPE type, Topic topic, int number, int periodDuration){
        this.type = type;
        this.topic = topic;
        this.number = number;
        this.dateTime = null;
        this.periodDuration = periodDuration;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public int getHourOfDay() {
        return dateTime.get(Calendar.HOUR_OF_DAY);
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public int getPeriodDuration() {
        return periodDuration;
    }

    @Override
    public String toString() {
        if (type == PERIOD_TYPE.BREAK) {
            return "BREAK";
        } else if(type == PERIOD_TYPE.REWARD) {
            return "REWARD";
        } else {
            return topic.getName() + " " + "Part: " + number;
        }
    }

}
