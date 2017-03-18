package timetable;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String name;
    private Topic[] topics;
    private ArrayList<Period> periods;

    public Subject(String name, Topic...topics) {
        this.name = name;
        this.topics = topics;
        periods = getAllPeriods();
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
}
