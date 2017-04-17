package timetable;

import java.util.ArrayList;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Topic {
    private String name;
    private String subjectName;
    private ArrayList<Period> periods;

    public Topic(String name, String subjectName,  int topicDuration, int sessionDuration){
        this.name = name;
        this.subjectName = subjectName;
        generatePeriods(topicDuration, sessionDuration);
    }

    private void generatePeriods(int topicDuration, int sessionDuration) {
        this.periods = new ArrayList<>();
        int numOfPeriods = (int) Math.ceil((double)topicDuration / sessionDuration);
        for (int i = 0; i < numOfPeriods; i++ ) {
            this.periods.add(new Period(Period.PERIOD_TYPE.SUBJECT, name, subjectName, i, sessionDuration));
        }
    }

    public ArrayList<Period> getTopicPeriods() {
        return periods;
    }

    public String getName() {
        return name;
    }
}
