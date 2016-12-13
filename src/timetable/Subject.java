package timetable;

import java.util.ArrayList;

/**
 * Created by mustarohman on 08/12/2016.
 */
public class Subject {
    private String name;
    private Topic[] topics;
    private int subjectduration;


    public Subject(String name, int subjectduration, Topic...topics){
        this.name = name;
        this.subjectduration = subjectduration;
        this.topics = topics;
    }

    public Topic[] getTopics() {
        return this.topics;
    }

    public ArrayList<Period> getAllPeriods() {
        ArrayList<Period> subjectPeriods = null;
        for(Topic topic: topics) {
            subjectPeriods.addAll(topic.getTopicPeriods());
        }

        return subjectPeriods;
    }

}
