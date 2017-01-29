package timetable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by mustarohman on 13/12/2016.
 */

public class Timetable {

    public enum REVISION_STYLE {
        ALT,
        SEQ
    }

    private ArrayList<Subject> subjects;
    private Period rewardPeriod;
    private Calendar startDate;
    private LocalDate examStartDate;
    private LocalDate revisionEndDate;
    private long spareDays;
    private REVISION_STYLE style;
    private int periodDuration;
    private int breakSize;
    private Map<Date, ArrayList<Period>> dayPeriodsAssignment;


    public Timetable(ArrayList<Subject> subjects, Period rewardPeriod ,Calendar startDate, LocalDate examStartDate ,REVISION_STYLE style, int periodDuration, int breakSize) {
        this.subjects = subjects;
        this.rewardPeriod = rewardPeriod;
        this.startDate = startDate;
        this.examStartDate = examStartDate;
        this.periodDuration = periodDuration;
        this.breakSize = breakSize;
        this.style = style;

        generateTimetable(style);
    }

    private void generateTimetable(REVISION_STYLE style) {
        /*
         * Get all subjects periods
         * Create a Map
         * Mapping between each day, to a list of periods assigned to that day
         * These periods consisting initially consisting only of revision and breaks
         *
         * map =  Map<Calendar, List<Period>>
         *
         * periods = subjects.getPeriods
         * currentDateTime = startDate
         *
         * periods.forEach(
         *
         * )
         *
         */

        final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm");


        // we rotate thru the subjects. We keep track using a counter subjectCounter
        int subjectCounter = 0;
        // Total of periods assigned for each subject
        int[] totalSubPeriodsAssigned = new int[subjects.size()];

        int totalPeriods = getTotalPeriods();
        Calendar currentDateTime = startDate;
        Map<Date, ArrayList<Period>> dayPeriodsMap = new HashMap<Date, ArrayList<Period>>();
        ArrayList<Period> periodsForDay = new ArrayList<>();
        boolean rewardTaken = false;

//        List of all periods ordered by the timetable assignment
        Period[] orderedTimetablePeriods = new Period[totalPeriods];

        for (int i = 0; i < totalPeriods; i++) {
//            System.out.println("Period " + i + "/" + totalPeriods);
            System.out.println(SDF.format(currentDateTime.getTime()) + " " + "Date: " + currentDateTime.get(Calendar.DATE));
            Subject currentSubject = subjects.get(subjectCounter);

            // Checks to see if assigned all periods belonging to a subject
            // If true, then we increment the counter, thus moving on to the next subject
            // Loop thru subjects to find one that has unassigned periods
            while (totalSubPeriodsAssigned[subjectCounter] >= currentSubject.getPeriods().size()) {
//                System.out.println("totalSubPeriodsAssigned[subjectCounter] " + totalSubPeriodsAssigned[subjectCounter]  + ">" +  currentSubject.getPeriods().size());
                subjectCounter = (subjectCounter + 1) % (subjects.size());
                currentSubject = subjects.get(subjectCounter);
            }

//            if (totalSubPeriodsAssigned[subjectCounter] < subjects.get(subjectCounter).getPeriods().size()) {
//                for (int j = 0; j < totalSubPeriodsAssigned.length; j++) {
//                    System.out.println("Value of totalSubPeriodsAssigned: " + totalSubPeriodsAssigned[j] + "/" + subjects.get(j).getPeriods().size());
//                }
//            }
            Period currentPeriod = currentSubject.getPeriods().get(totalSubPeriodsAssigned[subjectCounter]);
            currentPeriod.setDateTime(currentDateTime);
            periodsForDay.add(currentPeriod);
            orderedTimetablePeriods[i] = currentPeriod;

            totalSubPeriodsAssigned[subjectCounter]++;
            System.out.println(currentPeriod.toString());
            currentDateTime.add(Calendar.MINUTE, currentPeriod.getPeriodDuration());

            if (currentDateTime.get(Calendar.HOUR_OF_DAY) >= 21) {
                System.out.println("END OF DAY (21:00)");
                System.out.println("Subject " + subjectCounter + "/" + totalSubPeriodsAssigned.length +  totalSubPeriodsAssigned[subjectCounter]);
                subjectCounter = incrementDayAndSubjectCounter(dayPeriodsMap, currentDateTime, periodsForDay, subjectCounter);
                System.out.println(subjects.get(subjectCounter).getName());
                rewardTaken = false;
            } else if(currentDateTime.get(Calendar.HOUR_OF_DAY) >= (13) && !rewardTaken) {
                System.out.println(SDF.format(currentDateTime.getTime()));
                rewardPeriod.setDateTime(currentDateTime);
                System.out.println(rewardPeriod.toString());
                periodsForDay.add(rewardPeriod);
                currentDateTime.add(Calendar.MINUTE, 60);
                rewardTaken = true;
            } else {
                System.out.println(SDF.format(currentDateTime.getTime()));
                Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null, 0, breakSize);
                breakPeriod.setDateTime(currentDateTime);
                System.out.println(breakPeriod.toString());
                periodsForDay.add(breakPeriod);
                currentDateTime.add(Calendar.MINUTE, breakSize);
            }
        }
        spareDays = calculateSpareDays(currentDateTime);
        dayPeriodsAssignment = dayPeriodsMap;
    }

    private void assignSEQ() {

    }

    private void assignALT() {

    }

    private int getTotalPeriods() {
        int total = 0;
        for (Subject subject : subjects) {
            total += subject.getPeriods().size();
        }
        return total;
    }

    private int incrementDayAndSubjectCounter(Map<Date, ArrayList<Period>> dayPeriodsMap, Calendar currentDateTime, ArrayList<Period> periodsForDay, int subjectCounter) {
        dayPeriodsMap.put(currentDateTime.getTime(), periodsForDay);
        periodsForDay.clear();
        currentDateTime.set(Calendar.HOUR_OF_DAY, 9);
        currentDateTime.set(Calendar.MINUTE, 0);
        currentDateTime.add(Calendar.DATE, 1);
        return (subjectCounter + 1) % (subjects.size());

    }

    public void printTimetable() {
        Iterator it = dayPeriodsAssignment.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

        }
    }

    private long calculateSpareDays(Calendar currentDateTime){
        revisionEndDate = LocalDate.of(currentDateTime.get(Calendar.YEAR), currentDateTime.get(Calendar.MONTH) + 1, currentDateTime.get(Calendar.DATE));
        long spareDays = DAYS.between(revisionEndDate, examStartDate);
        return spareDays;
    }

    public long getSpareDays() {
        return spareDays;
    }

    public void addBreakPeriod(Period lastCompletedPeriod, Period breakPeriod) {

    }

    public void addBreakDay() {

    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

}
