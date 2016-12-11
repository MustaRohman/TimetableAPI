package Timetable;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Topic {
    private String name;
    private Subject subject;
    private int subjectDuration;
    private int sessionDuration;

    public Topic(String name, Subject subject, int subjectDuration, int sessionDuration){
        this.name = name;
        this.subject = subject;
        this.subjectDuration = subjectDuration;
        this.sessionDuration = sessionDuration;

    }
}
