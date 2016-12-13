package Timetable;

import java.util.ArrayList;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Topic {
    private String name;
    private int subjectDuration;
    private int sessionDuration;
    private ArrayList<Period> periods;

    public Topic(String name,  int subjectDuration, int sessionDuration){
        this.name = name;
        this.subjectDuration = subjectDuration;
        this.sessionDuration = sessionDuration;
        this.periods = new ArrayList<Period>();
        int numOfPeriods = (int) Math.ceil(subjectDuration/sessionDuration);
        for (int i = 0; i < numOfPeriods; i++ ) {
            this.periods.add(new Period(Period.PERIOD_TYPE.SUBJECT, this, i));
        }
    }

    public ArrayList<Period> getTopicPeriods() {
        return periods;
    }


}
