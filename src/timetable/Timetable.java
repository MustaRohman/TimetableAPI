package timetable;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by mustarohman on 13/12/2016.
 */

public class Timetable {

    public enum REVISION_STYLE {
        ALT,
        SEQ
    }

    private ArrayList<Subject> subjects;
    private Calendar startDate;
    private int sessionSize;

    public Timetable(ArrayList<Subject> subjects, Calendar startDate, REVISION_STYLE style, int sessionSize, int breakSize) {
        this.subjects = subjects;
        this.startDate = startDate;
        this.sessionSize = sessionSize;
        generateTimetable(style);
    }

    private void generateTimetable(REVISION_STYLE style) {
//        Timetable Algo goes here


    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

}
