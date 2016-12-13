import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {

        final int sessionSize = 45;
        final int breakSize = 15;

        ArrayList<Subject> subjects = new ArrayList<Subject>();
        subjects.add(new Subject("CSL", 8 , new Topic("Logic", 8, 4), new Topic("Trees", 8, 4)));
        Timetable timetable = new Timetable(subjects, new GregorianCalendar(2016, 12, 15), Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
    }
}
