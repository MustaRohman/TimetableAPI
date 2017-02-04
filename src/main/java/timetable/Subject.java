package timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Subject {
    private String name;
    private Topic[] topics;
    private ArrayList<Period> periods;
//    private int subjectduration;


    public Subject(String name, Topic...topics) {
        this.name = name;
        this.topics = topics;
        periods = getAllPeriods();
    }

    public Topic[] getTopics() {
        return this.topics;
    }

    private ArrayList<Period> getAllPeriods() {
        ArrayList<Period> subjectPeriods = new ArrayList<>();
        for(Topic topic: topics) {
            subjectPeriods.addAll(topic.getTopicPeriods());
        }
        return subjectPeriods;
    }

    public ArrayList<Period> getPeriods() {
        return periods;
    }

    public String getName() {
        return name;
    }
}
