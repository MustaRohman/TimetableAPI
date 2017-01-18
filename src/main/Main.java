package main;

import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {

        final int sessionSize = 45;
        final int breakSize = 15;

        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CSL", 8 , new Topic("Logic", 240, sessionSize), new Topic("Trees", 240, sessionSize)));
        subjects.add(new Subject("DB", 8 , new Topic("FD", 240, sessionSize), new Topic("Norm", 240, sessionSize)));
        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
        new Timetable(subjects, startDateTime, Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
    }
}
