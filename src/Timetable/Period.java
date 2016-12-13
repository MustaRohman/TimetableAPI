package Timetable;

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

    public Period(PERIOD_TYPE type, Topic topic, int number){
        this.type = type;
        this.topic = topic;
        this.number = number;
    }
}
