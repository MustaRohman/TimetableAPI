package timetable;

import java.text.SimpleDateFormat;
import java.util.*;

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
    private REVISION_STYLE style;
    private int periodDuration;
    private int breakSize;
    private Map<Date, ArrayList<Period>> dayPeriodsAssignment;

    private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");

    public Timetable(ArrayList<Subject> subjects, Calendar startDate, REVISION_STYLE style, int periodDuration, int breakSize) {
        this.subjects = subjects;
        this.startDate = startDate;
        this.periodDuration = periodDuration;
        this.breakSize = breakSize;
        this.style = style;

        generateTimetable();
    }

    private void generateTimetable() {
        /**
         * Get all subjects periods
         * Create a Map
         * Mapping between each day, to a list of periods assigned to that day
         * These periods consisting initially consisting only of revision and breaks
         *
         * map =  Map<Calendar, List<Period>>
         *
         * periods = subjects.getAllPeriods
         * currentDateTime = startDate
         *
         * periods.forEach(
         *
         * )
         *
         */

        // we rotate thru the subjects. We keep track using a counter subjectCounter
        int subjectCounter = 0;
        // Total of periods assigned for each subject
        int[] subPeriodAssigned = new int[subjects.size()];

        int totalPeriods = getTotalPeriods();



        Calendar currentDateTime = startDate;
        Map<Date, ArrayList<Period>> dayPeriodsMap = new HashMap<Date, ArrayList<Period>>();
        ArrayList<Period> periodsForDay = new ArrayList<>();

        for (int i = 0; i < totalPeriods; i++) {
            System.out.println(SDF.format(currentDateTime.getTime()) + " " + currentDateTime.get(Calendar.DATE));
            Subject currentSubject = subjects.get(subjectCounter);

            // Checks to see if assigned all periods belonging to a subject
            // If true, then we increment the counter, thus moving on to the next subject
            if (subPeriodAssigned[subjectCounter] >= currentSubject.getAllPeriods().size() && subjectCounter < subjects.size()) {
                subjectCounter = (subjectCounter + 1) % (subjects.size());
            }


//            System.out.println("Subject Periods: " + currentSubject.getAllPeriods().size());
//            System.out.println(subPeriodAssigned[subjectCounter]);
            Period currentPeriod = currentSubject.getAllPeriods().get(subPeriodAssigned[subjectCounter]);
            currentPeriod.setDateTime(currentDateTime);
            periodsForDay.add(currentPeriod);

            subPeriodAssigned[subjectCounter]++;

            System.out.println(currentPeriod.toString());

            currentDateTime.add(Calendar.MINUTE, currentPeriod.getPeriodDuration());


            if (currentDateTime.get(Calendar.HOUR_OF_DAY) >= 21) {
                System.out.println("END OF DAY (21:00)");
                incrementDayAndSubject(dayPeriodsMap, currentDateTime, periodsForDay, subjectCounter);
            } else {
                System.out.println(SDF.format(currentDateTime.getTime()));
                Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null, 0, breakSize);
                System.out.println(breakPeriod.toString());
                periodsForDay.add(breakPeriod);
                currentDateTime.add(Calendar.MINUTE, breakSize);
            }
        }
        dayPeriodsAssignment = dayPeriodsMap;
    }

    private int getTotalPeriods() {
        int total = 0;
        for(Subject subject: subjects) {
            total += subject.getAllPeriods().size();
        }
        return total;
    }

    private void incrementDayAndSubject(Map<Date, ArrayList<Period>> dayPeriodsMap, Calendar currentDateTime, ArrayList<Period> periodsForDay, int subjectCounter) {
        dayPeriodsMap.put(currentDateTime.getTime(), periodsForDay);
        periodsForDay = new ArrayList<>();
        currentDateTime.set(Calendar.HOUR_OF_DAY, 9);
        currentDateTime.set(Calendar.MINUTE, 0);
        currentDateTime.set(Calendar.DATE, currentDateTime.get(Calendar.DATE) + 1);
        subjectCounter = (subjectCounter + 1) % (subjects.size());
        System.out.println(subjectCounter);
    }

    public void printTimetable() {
        Iterator it = dayPeriodsAssignment.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

        }
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

}
