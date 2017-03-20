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

    private  ArrayList<Subject> subjects;
    private  Period rewardPeriod;
    private  LocalDateTime startDateTime;
    private LocalDate examStartDate;
    private LocalDate revisionEndDate;
    private long spareDays;
    private int breakSize;
    private Map<LocalDate, ArrayList<Period>> timetableAssignment;
    private String name;

    private Timetable(String name, ArrayList<Subject> subjects, Period rewardPeriod , LocalDateTime startDateTime, LocalDate examStartDate,  int breakSize) {
        this.subjects = subjects;
        this.rewardPeriod = rewardPeriod;
        this.startDateTime = startDateTime;
        this.examStartDate = examStartDate;
        this.breakSize = breakSize;
        this.name = name;

        timetableAssignment = generateTimetable();
    }

    private Map<LocalDate, ArrayList<Period>> generateTimetable() {
        int subjectCounter = 0;
        // Total of periods assigned for each subject
        int[] totalSubPeriodsAssigned = new int[subjects.size()];
        int totalPeriods = getTotalPeriods();
        boolean rewardTaken = false;
        int endHour = 21;

        LocalDateTime currentDateTime = startDateTime;
        Map assignment = Collections.synchronizedMap(new HashMap<LocalDate, ArrayList<Period>>());
        ArrayList<Period> periodsForDay = new ArrayList<>();
//        List of all periods ordered by the timetable assignment

        for (int i = 0; i < totalPeriods; i++) {
            Subject currentSubject = subjects.get(subjectCounter);
            while (totalSubPeriodsAssigned[subjectCounter] >= currentSubject.getPeriods().size()) {
                subjectCounter = (subjectCounter + 1) % (subjects.size());
                currentSubject = subjects.get(subjectCounter);
            }

            Period currentPeriod = currentSubject.getPeriods().get(totalSubPeriodsAssigned[subjectCounter]);
            currentPeriod.setDateTime(currentDateTime);
            periodsForDay.add(currentPeriod);

            totalSubPeriodsAssigned[subjectCounter]++;
            currentDateTime = currentDateTime.plusMinutes(currentPeriod.getPeriodDuration());

            if (currentDateTime.getHour() >= endHour) {
                subjectCounter = (subjectCounter + 1) % (subjects.size());
                assignment.put(currentDateTime.toLocalDate(), periodsForDay);
                currentDateTime = incrementDay(currentDateTime);
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
        spareDays = calculateSpareDays(revisionEndDate, examStartDate);
        return assignment;
    }

    private int getTotalPeriods() {
        int total = 0;
        for (Subject subject : subjects) {
            total += subject.getPeriods().size();
        }
        return total;
    }

    private LocalDateTime incrementDay(LocalDateTime localDateTime) {
        try {
            return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth() + 1, 9, 0);
        } catch (DateTimeException e) {
            return LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth().getValue() + 1, 1, 9, 0);
        }
    }

    private long calculateSpareDays(LocalDate revisionEndDate, LocalDate examStartDate){
        return DAYS.between(revisionEndDate, examStartDate);
    }

    public long getSpareDays() {
        return spareDays;
    }

    public boolean addBreakDay(LocalDate breakDate) {
        if (!timetableAssignment.containsKey(breakDate) || spareDays <= 0) {
           return false;
        }
        ArrayList<Period> breakDay = new ArrayList<>();
        breakDay.add(new Period(Period.PERIOD_TYPE.BREAK_DAY, null, 0, 1500));
        ArrayList<Period> periods = timetableAssignment.replace(breakDate, breakDay);
        LocalDate date = breakDate.plusDays(1);
        for (Period period: periods) {
            period.setDateTime(period.getDateTime().plusDays(1));
        }

        while (timetableAssignment.containsKey(date)) {
            periods = timetableAssignment.replace(date, periods);
            for (Period period: periods) {
                period.setDateTime(period.getDateTime().plusDays(1));
            }
            date = date.plusDays(1);
        }

        spareDays--;
        revisionEndDate.plusDays(1);

        return true;
    }

    public Map<LocalDate, ArrayList<Period>> getAssignment() {
        return timetableAssignment;
    }

    public ArrayList<Subject> getSubjects() {
        return subjects;
    }

    public Period getRewardPeriod() {
        return rewardPeriod;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDate getExamStartDate() {
        return examStartDate;
    }

    public LocalDate getRevisionEndDate() {
        return revisionEndDate;
    }

    public int getBreakSize() {
        return breakSize;
    }

    public Map<LocalDate, ArrayList<Period>> getTimetableAssignment() {
        return timetableAssignment;
    }

    public String getName() {
        return name;
    }

    public static class TimetableBuilder {
        private ArrayList<Subject> nestedSubjects;
        private Period nestedRewardPeriod;
        private LocalDateTime nestedStartDateTime;
        private LocalDate nestedExamDate;
        private int nestedPeriodDuration;
        private int nestedBreakDuration;
        private String nestedName;

        public TimetableBuilder() {

        }

        public TimetableBuilder addSubjects(ArrayList<Subject> nestedSubjects) {
            this.nestedSubjects = nestedSubjects;
            return this;
        }

        public TimetableBuilder addRewardPeriod(Period nestedRewardPeriod) {
            this.nestedRewardPeriod = nestedRewardPeriod;
            return this;
        }

        public TimetableBuilder addStartDate(LocalDateTime nestedStartDate) {
            this.nestedStartDateTime = nestedStartDate;
            return this;
        }

        public TimetableBuilder addExamDate(LocalDate nestedExamDate) {
            this.nestedExamDate= nestedExamDate;
            return this;
        }

        public TimetableBuilder addBreakDuration(int breakDuration) {
            this.nestedBreakDuration = breakDuration;
            return this;
        }

        public TimetableBuilder addName(String name) {
            this.nestedName = name;
            return this;
        }

        public Timetable createTimetable() {
            return new Timetable(nestedName, nestedSubjects, nestedRewardPeriod, nestedStartDateTime, nestedExamDate, nestedBreakDuration);
        }
    }

}
