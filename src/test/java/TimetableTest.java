import org.junit.Assert;
import org.junit.Test;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by mustarohman on 26/01/2017.
 */
public class TimetableTest {

    final int sessionSize = 45;
    final int breakSize = 15;
    Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);

    Period rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, 0, 75);
    Subject cisSubject = new Subject("CIS", new Topic("RSA", 240, sessionSize), new Topic("DES", 240, sessionSize), new Topic("Diffie Hellman",
            240, sessionSize), new Topic("Kerberos", 240, sessionSize), new Topic("Block Cipher Modes", 240, sessionSize), new Topic("Modulo", 240, sessionSize),
            new Topic("Fiat Shamir", 240, sessionSize), new Topic("El-Gamal", 240, sessionSize));
    Subject insSubject = new Subject("INS" , new Topic("IP", 240, sessionSize), new Topic("TCP", 240, sessionSize), new Topic("HTTP", 240, sessionSize), new Topic("Virtualisation", 240, sessionSize));
    Subject cslSubject = new Subject("CSL" , new Topic("Logic", 240, sessionSize), new Topic("Trees", 240, sessionSize), new Topic("Logic2", 240, sessionSize),
            new Topic("Trees2", 240, sessionSize), new Topic("Logic3", 240, sessionSize), new Topic("Trees3", 240, sessionSize),
            new Topic("Logic4", 240, sessionSize), new Topic("Trees4", 240, sessionSize), new Topic("Logic5", 240, sessionSize), new Topic("Trees5", 240, sessionSize));
    Subject cflSubject = new Subject("CFL" , new Topic("Rexp", 240, sessionSize), new Topic("Automata", 240, sessionSize), new Topic("Lexing", 240, sessionSize),
            new Topic("Grammars", 240, sessionSize), new Topic("Interpreter", 240, sessionSize), new Topic("Compiler", 240, sessionSize),
            new Topic("Functional", 240, sessionSize), new Topic("Optimise", 240, sessionSize));

    @Test
    public void oneSubject() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
    }

    @Test
    public void twoSubjects() {
        // Test (input of 2 subjects)

        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        subjects.add(insSubject);
        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
    }

    @Test
    public void threeSubjects() {
        // Test (input of 3 subjects)
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cslSubject);
        subjects.add(insSubject);
        subjects.add(cisSubject);

        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
    }

    @Test
    public void fourSubjects() {
        // Test (input of 4 subjects)
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cslSubject);
        subjects.add(cflSubject);
        subjects.add(insSubject);
        subjects.add(cisSubject);

        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
    }

    @Test
    public void testSpareDays() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
        assertTrue(timetable.getSpareDays() > 0);
    }

    @Test
    public void testSpareDaysFail() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 17, 9, 0))
                .addExamDate(LocalDate.of(2016, 12, 18))
                .addBreakDuration(breakSize)
                .createTimetable();
        assertTrue(timetable.getSpareDays() <= 0);
    }

    @Test
    public void testNoRepeatPeriod() {

    }

    @Test
    public void testAddBreakDay() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cslSubject);
        subjects.add(insSubject);
        subjects.add(cisSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 10, 17))
                .addBreakDuration(breakSize)
                .createTimetable();
        timetable.addBreakDay(LocalDate.of(2016, 12, 16));
        ArrayList<Period> periods = timetable.getAssignment().get(LocalDate.of(2016, 12, 16));
        assertEquals(periods.get(0).getType(), Period.PERIOD_TYPE.BREAK_DAY);
    }

    @Test
    public void testAddBreakDayOrder() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cslSubject);
        subjects.add(insSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2016, 12, 16))
                .addBreakDuration(breakSize)
                .createTimetable();

        ArrayList<Period> periods = timetable.getAssignment().get(LocalDate.of(2016, 11, 17));
        timetable.addBreakDay(LocalDate.of(2016, 11, 16));
        assertEquals(periods, timetable.getAssignment().get(LocalDate.of(2016, 11, 18)));
    }

    @Test
    public void testOneRewardAddedToEachDay() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
        Iterator it = timetable.getAssignment().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Period> periods = (ArrayList<Period>) pair.getValue();
            assertTrue(Collections.frequency(periods, rewardPeriod) <= 1);
        }
    }

    @Test
    public void testUniqueDayAssignment() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
        assertEquals(timetable.getAssignment().values().stream().distinct().count(), timetable.getAssignment().size());
    }
}
