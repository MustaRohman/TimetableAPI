package timetable;

import java.time.DateTimeException;
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


    public Timetable(ArrayList<Subject> subjects, Period rewardPeriod , Calendar startDate, LocalDate examStartDate, REVISION_STYLE style, int periodDuration, int breakSize) {
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
            currentDateTime = currentDateTime.plusMinutes(currentPeriod.getPeriodDuration());

            if (currentDateTime.getHour() >= endHour) {
                subjectCounter = (subjectCounter + 1) % (subjects.size());
                assignment.put(currentDateTime.toLocalDate(), periodsForDay);
                currentDateTime = incrementDay(assignment, currentDateTime);
                periodsForDay = new ArrayList<>();
                rewardTaken = false;
            } else if(currentDateTime.getHour() >= (13) && !rewardTaken && i != totalPeriods - 1) {
                rewardPeriod.setDateTime(currentDateTime);
                periodsForDay.add(rewardPeriod);
                currentDateTime = currentDateTime.plusMinutes(60);
                rewardTaken = true;
            } else if (i != totalPeriods - 1) {
                Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null, 0, breakSize);
                breakPeriod.setDateTime(currentDateTime);
                periodsForDay.add(breakPeriod);
                currentDateTime = currentDateTime.plusMinutes(breakSize);
            }
        }
        if (!periodsForDay.isEmpty() && currentDateTime.getHour() < endHour) {
            assignment.put(currentDateTime.toLocalDate(), periodsForDay);
        }
        revisionEndDate = currentDateTime.toLocalDate();
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
        try {
            return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth() + 1, 9, 0);
        } catch (DateTimeException e) {
            return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth().getValue() + 1, 1, 9, 0);
        }
    }

    private long calculateSpareDays(LocalDateTime currentDateTime){
        return DAYS.between(revisionEndDate, examStartDate);
    }

    public long getSpareDays() {
        return spareDays;
    }

    public boolean addBreakDay(LocalDate localDate) {
        if (!timetableAssignment.containsKey(localDate) && spareDays <= 0) {
           return false;
        }
        ArrayList<Period> breakDay = new ArrayList<>();
        breakDay.add(new Period(Period.PERIOD_TYPE.BREAK_DAY, null, 0, 1500));
        ArrayList<Period> periods = timetableAssignment.replace(localDate, breakDay);
        LocalDate date = localDate.plusDays(1);

        while (timetableAssignment.containsKey(date)) {
            periods = timetableAssignment.replace(date, periods);
            date = date.plusDays(1);
        }

        return true;
    }

    public Map<LocalDate, ArrayList<Period>> getAssignment() {
        return timetableAssignment;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

}
