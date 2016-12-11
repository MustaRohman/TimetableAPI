package Timetable;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Subject {
    private String name;
    private Topic[] topics;
    private int subjectduration;


    public Subject(String name, int sessionDuration, Topic[] topics){
        this.name = name;
        this.subjectduration = (topics.length * sessionDuration) /60;
    }

}
