package timetable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Map<LocalDate, ArrayList<Period>> dayPeriodsAssignment;
    private LocalDateTime currentDateTime;


    public Timetable(ArrayList<Subject> subjects, Period rewardPeriod ,Calendar startDate, LocalDate examStartDate, REVISION_STYLE style, int periodDuration, int breakSize) {
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
        currentDateTime = LocalDateTime.of(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), startDate.get(Calendar.HOUR),
                startDate.get(Calendar.MINUTE));
        Map<LocalDate, ArrayList<Period>> dayPeriodsMap = new HashMap<LocalDate, ArrayList<Period>>();
        ArrayList<Period> periodsForDay = new ArrayList<>();
        boolean rewardTaken = false;

//        List of all periods ordered by the timetable assignment
        Period[] orderedTimetablePeriods = new Period[totalPeriods];

        for (int i = 0; i < totalPeriods; i++) {
            System.out.println(currentDateTime.toLocalDate().toString() + " " + currentDateTime.toLocalTime().toString());
            Subject currentSubject = subjects.get(subjectCounter);

            // Checks to see if assigned all periods belonging to a subject
            // If true, then we increment the counter, thus moving on to the next subject
            // Loop thru subjects to find one that has unassigned periods
            while (totalSubPeriodsAssigned[subjectCounter] >= currentSubject.getPeriods().size()) {
                subjectCounter = (subjectCounter + 1) % (subjects.size());
                currentSubject = subjects.get(subjectCounter);
            }

            Period currentPeriod = currentSubject.getPeriods().get(totalSubPeriodsAssigned[subjectCounter]);
            currentPeriod.setDateTime(currentDateTime);
            periodsForDay.add(currentPeriod);
            orderedTimetablePeriods[i] = currentPeriod;

            totalSubPeriodsAssigned[subjectCounter]++;
            System.out.println(currentPeriod.toString());
            currentDateTime = currentDateTime.plusMinutes(currentPeriod.getPeriodDuration());
//                    add(Calendar.MINUTE, currentPeriod.getPeriodDuration());

            if (currentDateTime.getHour() >= 21) {
                System.out.println("END OF DAY (21:00)");
                System.out.println("Subject " + subjectCounter + "/" + totalSubPeriodsAssigned.length +  totalSubPeriodsAssigned[subjectCounter]);
                subjectCounter = incrementDayAndSubjectCounter(dayPeriodsMap, currentDateTime, periodsForDay, subjectCounter);
                System.out.println(subjects.get(subjectCounter).getName());
                rewardTaken = false;
            } else if(currentDateTime.getHour() >= (13) && !rewardTaken) {
                System.out.println(currentDateTime.toLocalDate().toString() + " " + currentDateTime.toLocalTime().toString());
                rewardPeriod.setDateTime(currentDateTime);
                System.out.println(rewardPeriod.toString());
                periodsForDay.add(rewardPeriod);
                currentDateTime = currentDateTime.plusMinutes(60);
//                        add(Calendar.MINUTE, 60);
                rewardTaken = true;
            } else {
                System.out.println(currentDateTime.toLocalDate().toString() + " " + currentDateTime.toLocalTime().toString());
                Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null, 0, breakSize);
                breakPeriod.setDateTime(currentDateTime);
                System.out.println(breakPeriod.toString());
                periodsForDay.add(breakPeriod);
                currentDateTime = currentDateTime.plusMinutes(breakSize);
//                currentDateTime.add(Calendar.MINUTE, breakSize);
            }
        }
        spareDays = calculateSpareDays(currentDateTime);
        dayPeriodsAssignment = dayPeriodsMap;
    }

    private int getTotalPeriods() {
        int total = 0;
        for (Subject subject : subjects) {
            total += subject.getPeriods().size();
        }
        return total;
    }

    private int incrementDayAndSubjectCounter(Map<LocalDate, ArrayList<Period>> dayPeriodsMap, LocalDateTime localDateTime, ArrayList<Period> periodsForDay, int subjectCounter) {
        dayPeriodsMap.put(localDateTime.toLocalDate(), periodsForDay);
        periodsForDay.clear();
        currentDateTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(),
                localDateTime.getDayOfMonth() + 1, 9, 0);
        return (subjectCounter + 1) % (subjects.size());

    }

    public void printTimetable() {
        Iterator it = dayPeriodsAssignment.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

        }
    }

    private long calculateSpareDays(LocalDateTime currentDateTime){
//        revisionEndDate = LocalDate.of(currentDateTime.get(Calendar.YEAR), currentDateTime.get(Calendar.MONTH) + 1, currentDateTime.get(Calendar.DATE));
        long spareDays = DAYS.between(currentDateTime.toLocalDate(), examStartDate);
        return spareDays;
    }

    public long getSpareDays() {
        return spareDays;
    }

    public void addBreakDay() {

    }

//    private Period getLastCompletedPeriod() {
//
//    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

}
