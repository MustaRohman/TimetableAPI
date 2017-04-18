package timetable;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.StringJoiner;

public class Period {

    public enum PERIOD_TYPE {
        SUBJECT,
        BREAK,
        REWARD,
        BREAK_DAY,
    }

    private PERIOD_TYPE type;
    private String topicName;
    private String subjectName;
    private int number;
    private LocalDateTime dateTime;
    private int periodDuration;

    public Period(PERIOD_TYPE type, String topicName, String subjectName,int number, int periodDuration){
        this.type = type;
        this.topicName = topicName;
        this.subjectName = subjectName;
        this.number = number;
        this.dateTime = null;
        this.periodDuration = periodDuration;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getPeriodDuration() {
        return periodDuration;
    }

    @Override
    public String toString() {
        switch (type) {
            case SUBJECT: return topicName + " " + "Part: " + number;
            case BREAK: return "BREAK";
            case REWARD: return "REWARD";
            case BREAK_DAY: return "BREAK DAY";
            default: return null;
        }
    }

    public PERIOD_TYPE getType() {
        return type;
    }
}
