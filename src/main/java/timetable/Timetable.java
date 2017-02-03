package timetable;

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
    private Map<LocalDate, ArrayList<Period>> timetableAssignment;
    private LocalDateTime currentDateTime;


    public Timetable(ArrayList<Subject> subjects, Period rewardPeriod ,Calendar startDate, LocalDate examStartDate, REVISION_STYLE style, int periodDuration, int breakSize) {
        this.subjects = subjects;
        this.rewardPeriod = rewardPeriod;
        this.startDate = startDate;
        this.examStartDate = examStartDate;
        this.periodDuration = periodDuration;
        this.breakSize = breakSize;
        this.style = style;

        timetableAssignment = generateTimetable();
    }

    private Map<LocalDate, ArrayList<Period>> generateTimetable() {
        int subjectCounter = 0;
        // Total of periods assigned for each subject
        int[] totalSubPeriodsAssigned = new int[subjects.size()];
        int totalPeriods = getTotalPeriods();
        boolean rewardTaken = false;
        int endHour = 21;

        currentDateTime = LocalDateTime.of(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), startDate.get(Calendar.HOUR),
                startDate.get(Calendar.MINUTE));
        Map assignment = Collections.synchronizedMap(new HashMap<LocalDate, ArrayList<Period>>());
        ArrayList<Period> periodsForDay = new ArrayList<>();
//        List of all periods ordered by the timetable assignment
        Period[] orderedTimetablePeriods = new Period[totalPeriods];

        for (int i = 0; i < totalPeriods; i++) {
//            System.out.println(currentDateTime.toLocalDate().toString() + " " + currentDateTime.toLocalTime().toString());
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
            System.out.println(currentPeriod.toString() + " added");
            orderedTimetablePeriods[i] = currentPeriod;

            totalSubPeriodsAssigned[subjectCounter]++;
//            System.out.println(currentPeriod.toString());
            currentDateTime = currentDateTime.plusMinutes(currentPeriod.getPeriodDuration());

            if (currentDateTime.getHour() >= endHour) {
//                System.out.println("END OF DAY (21:00)");
//                System.out.println("Subject " + subjectCounter + "/" + totalSubPeriodsAssigned.length +  totalSubPeriodsAssigned[subjectCounter]);
                subjectCounter = (subjectCounter + 1) % (subjects.size());
//                System.out.println(subjects.get(subjectCounter).getName());
                assignment.put(currentDateTime.toLocalDate(), periodsForDay);
                currentDateTime = incrementDay(assignment, currentDateTime);
                periodsForDay = new ArrayList<>();
                rewardTaken = false;
            } else if(currentDateTime.getHour() >= (13) && !rewardTaken && i != totalPeriods - 1) {
//                System.out.println(currentDateTime.toLocalDate().toString() + " " + currentDateTime.toLocalTime().toString());
                rewardPeriod.setDateTime(currentDateTime);
//                System.out.println(rewardPeriod.toString());
                periodsForDay.add(rewardPeriod);
                currentDateTime = currentDateTime.plusMinutes(60);
                rewardTaken = true;
            } else if (i != totalPeriods - 1) {
//                System.out.println(currentDateTime.toLocalDate().toString() + " " + currentDateTime.toLocalTime().toString());
                Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null, 0, breakSize);
                breakPeriod.setDateTime(currentDateTime);
//                System.out.println(breakPeriod.toString());
                periodsForDay.add(breakPeriod);
                currentDateTime = currentDateTime.plusMinutes(breakSize);
            }
        }
        if (!periodsForDay.isEmpty() && currentDateTime.getHour() < endHour) {
            assignment.put(currentDateTime.toLocalDate(), periodsForDay);
        }
        spareDays = calculateSpareDays(currentDateTime);
        return assignment;
    }

    private int getTotalPeriods() {
        int total = 0;
        for (Subject subject : subjects) {
            total += subject.getPeriods().size();
        }
        return total;
    }

    private LocalDateTime incrementDay(Map<LocalDate, ArrayList<Period>> dayPeriodsMap, LocalDateTime localDateTime) {
//        System.out.println(localDateTime.toLocalDate().toString() + " " + dayPeriodsMap.get(localDateTime.toLocalDate()));
        return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth() + 1, 9, 0);
    }

    public void printTimetable() {
        Iterator it = timetableAssignment.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

        }
    }

    private long calculateSpareDays(LocalDateTime currentDateTime){
        long spareDays = DAYS.between(currentDateTime.toLocalDate(), examStartDate);
        return spareDays;
    }

    public long getSpareDays() {
        return spareDays;
    }

    public void addBreakDay() {

    }

    public Map<LocalDate, ArrayList<Period>> getAssignment() {
        return timetableAssignment;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

}
