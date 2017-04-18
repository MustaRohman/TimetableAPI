package timetable;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class Timetable {

    private transient ArrayList<Subject> subjects;
    private transient Period rewardPeriod;
    private  LocalDateTime startDateTime;
    private LocalDate examStartDate;
    private LocalDate revisionEndDate;

    private long extraDays;
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
        int totalNumberOfPeriods = getTotalNumberOfPeriods();
        boolean rewardTaken = false;
        int endHour = 21;

        LocalDateTime currentDateTime = startDateTime;
        Map assignment = Collections.synchronizedMap(new HashMap<LocalDate, ArrayList<Period>>());
        ArrayList<Period> periodsForDay = new ArrayList<>();
//        List of all periods ordered by the timetable assignment

        for (int i = 0; i < totalNumberOfPeriods; i++) {
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
            } else if(currentDateTime.getHour() >= (13) && !rewardTaken && i != totalNumberOfPeriods - 1) {
                System.out.println("Added reward: " + rewardPeriod.getPeriodDuration());
                rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, null, 0, rewardPeriod.getPeriodDuration());
                rewardPeriod.setDateTime(currentDateTime);
                periodsForDay.add(rewardPeriod);
                currentDateTime = currentDateTime.plusMinutes(60);
                rewardTaken = true;
            } else if (i != totalNumberOfPeriods - 1) {
                Period breakPeriod = new Period(Period.PERIOD_TYPE.BREAK, null, null, 0, breakSize);
                breakPeriod.setDateTime(currentDateTime);
                periodsForDay.add(breakPeriod);
                currentDateTime = currentDateTime.plusMinutes(breakSize);
            }
        }
        if (!periodsForDay.isEmpty() && currentDateTime.getHour() < endHour) {
            assignment.put(currentDateTime.toLocalDate(), periodsForDay);
        }
        revisionEndDate = currentDateTime.toLocalDate();
        extraDays = calculateSpareDays(revisionEndDate, examStartDate);
        if (extraDays <= 0) {
            return null;
        }
        return assignment;
    }

    private int getTotalNumberOfPeriods() {
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

    public static ArrayList<String> getAgendaForWeek(Map<LocalDate, ArrayList<Period>> assignment, String weekNumber, boolean isWeekend) {
        String[] split = weekNumber.split("-");
        int year = Integer.parseInt(split[0]);
        int week = Integer.parseInt(split[1].substring(1));
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate ldt = LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), (isWeekend) ? 6 : 1);
        ArrayList<String> topicsAgenda = new ArrayList<>();
        for (int i = 0; i < ((isWeekend) ? 2 : 7); i++) {
            System.out.println(ldt.getDayOfWeek());
            ArrayList<String> dayAgenda = getAgendaForDay(assignment, ldt);
            if (dayAgenda != null) {
                topicsAgenda.addAll(dayAgenda);
            }
            ldt = ldt.plusDays(1);
        }
        Set<String> set = new HashSet<>();
        set.addAll(topicsAgenda);
        topicsAgenda.clear();
        topicsAgenda.addAll(set);
        return topicsAgenda;
    }


    public static ArrayList<String> getAgendaForDay(Map<LocalDate, ArrayList<Period>> assignment, LocalDate date) {
        ArrayList<Period> periodsForDay = assignment.get(date);
        ArrayList<String> topicsAgenda = new ArrayList<>();
        if (periodsForDay == null || periodsForDay.isEmpty()) {
            return null;
        }

        for (Period period: periodsForDay) {
            if (!topicsAgenda.contains(period.getTopicName()) && period.getTopicName() != null) {
                topicsAgenda.add(period.getTopicName());
            }
        }
        return topicsAgenda;
    }

    public long getExtraDays() {
        return extraDays;
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
