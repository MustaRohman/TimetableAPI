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

        generateTimetable(style);
    }

    private void generateTimetable(REVISION_STYLE style) {
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
        int[] totalSubPeriodsAssigned = new int[subjects.size()];

        int totalPeriods = getTotalPeriods();
        Calendar currentDateTime = startDate;
        Map<Date, ArrayList<Period>> dayPeriodsMap = new HashMap<Date, ArrayList<Period>>();
        ArrayList<Period> periodsForDay = new ArrayList<>();


        for (int i = 0; i < totalPeriods; i++) {
            System.out.println("Period " + i + "/" + totalPeriods);
            System.out.println(SDF.format(currentDateTime.getTime()) + " " + "Date: " + currentDateTime.get(Calendar.DATE));
            Subject currentSubject = subjects.get(subjectCounter);

            // Checks to see if assigned all periods belonging to a subject
            // If true, then we increment the counter, thus moving on to the next subject
            // Loop thru subjects to find one that has unassigned periods
            while (totalSubPeriodsAssigned[subjectCounter] >= currentSubject.getAllPeriods().size()) {
                System.out.println("totalSubPeriodsAssigned[subjectCounter] " + totalSubPeriodsAssigned[subjectCounter]  + ">" +  currentSubject.getAllPeriods().size());
                subjectCounter = (subjectCounter + 1) % (subjects.size());
                currentSubject = subjects.get(subjectCounter);
            }


            if (totalSubPeriodsAssigned[subjectCounter] < subjects.get(subjectCounter).getAllPeriods().size()) {
                for (int j = 0; j < totalSubPeriodsAssigned.length; j++) {
                    System.out.println("Value of totalSubPeriodsAssigned: " + totalSubPeriodsAssigned[j] + "/" + subjects.get(j).getAllPeriods().size());
                }
            }
            Period currentPeriod = currentSubject.getAllPeriods().get(totalSubPeriodsAssigned[subjectCounter]);
            currentPeriod.setDateTime(currentDateTime);
            periodsForDay.add(currentPeriod);

            totalSubPeriodsAssigned[subjectCounter]++;
            System.out.println(currentPeriod.toString());
            currentDateTime.add(Calendar.MINUTE, currentPeriod.getPeriodDuration());


            if (currentDateTime.get(Calendar.HOUR_OF_DAY) >= 21) {
                System.out.println("END OF DAY (21:00)");
                System.out.println("Subject " + subjectCounter + "/" + totalSubPeriodsAssigned.length +  totalSubPeriodsAssigned[subjectCounter]);
                subjectCounter = incrementDayAndSubject(dayPeriodsMap, currentDateTime, periodsForDay, subjectCounter);
                System.out.println(subjects.get(subjectCounter).getName());
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

    private void assignSEQ() {

    }

    private void assignALT() {

    }

    private int getTotalPeriods() {
        int total = 0;
        for (Subject subject : subjects) {
            total += subject.getAllPeriods().size();
        }
        return total;
    }

    private int incrementDayAndSubject(Map<Date, ArrayList<Period>> dayPeriodsMap, Calendar currentDateTime, ArrayList<Period> periodsForDay, int subjectCounter) {
        dayPeriodsMap.put(currentDateTime.getTime(), periodsForDay);
        periodsForDay.clear();
        currentDateTime.set(Calendar.HOUR_OF_DAY, 9);
        currentDateTime.set(Calendar.MINUTE, 0);
        currentDateTime.set(Calendar.DATE, currentDateTime.get(Calendar.DATE) + 1);
        return (subjectCounter + 1) % (subjects.size());

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
