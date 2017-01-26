package timetable;

import java.util.ArrayList;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Topic {
    private String name;
    private int topicDuration;
    private int sessionDuration;
    private ArrayList<Period> periods;

    public Topic(String name, int topicDuration, int sessionDuration){
        this.name = name;
        this.topicDuration = topicDuration;
        this.sessionDuration = sessionDuration;

        generatePeriods(topicDuration, sessionDuration);
    }

    public void generatePeriods(int topicDuration, int sessionDuration) {
        this.periods = new ArrayList<Period>();
        int numOfPeriods = topicDuration / sessionDuration;
        for (int i = 0; i < numOfPeriods; i++ ) {
            this.periods.add(new Period(Period.PERIOD_TYPE.SUBJECT, this, i, sessionDuration));
        }
    }

    public ArrayList<Period> getTopicPeriods() {
        return periods;
    }

    public String getName() {
        return name;
    }
}
