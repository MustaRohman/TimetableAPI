package timetable;

import java.time.LocalDateTime;
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
    private String topicName;
    private int number;
    private LocalDateTime dateTime;
    private int periodDuration;
    private boolean completed;

    public Period(PERIOD_TYPE type, String topicName, int number, int periodDuration){
        this.type = type;
        this.topicName = topicName;
        this.number = number;
        this.dateTime = null;
        this.periodDuration = periodDuration;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getHourOfDay() {
        return dateTime.getHour();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getPeriodDuration() {
        return periodDuration;
    }

    public void complete() {
        completed = true;
    }

    public boolean isComplete() {
        return completed;
    }

    @Override
    public String toString() {
        if (type == PERIOD_TYPE.BREAK) {
            return "BREAK";
        } else if(type == PERIOD_TYPE.REWARD) {
            return "REWARD";
        } else {
            return topicName + " " + "Part: " + number;
        }
    }

}
