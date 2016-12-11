package Timetable;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Period {

    private String name;
    private Topic topic;
    private int number;

    public Period(String name, Topic topic, int number){
        this.name = name;
        this.topic = topic;
        this.number = number;
    }
}
