import database.TimetableTable;
import org.junit.Test;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

/**
 * Created by mustarohman on 26/01/2017.
 */
public class TimetableTest {

    final int sessionSize = 45;
    final int breakSize = 15;
    Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);

    Period rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, null, 0, 75);
    String CIS = "CIS";
    String INS = "INS";
    String CSL = "CSL";
    String CFL = "CFL";
    Subject cisSubject = new Subject(CIS, new Topic("RSA", CIS, 240, sessionSize), new Topic("DES", CIS, 240, sessionSize), new Topic("Diffie Hellman", CIS,
            240, sessionSize), new Topic("Kerberos",CIS,  240, sessionSize), new Topic("Block Cipher Modes", CIS, 240, sessionSize), new Topic("Modulo", CIS, 240, sessionSize),
            new Topic("Fiat Shamir", CIS,  240, sessionSize), new Topic("El-Gamal", CIS, 240, sessionSize));
    Subject insSubject = new Subject(INS , new Topic("IP", INS, 240, sessionSize), new Topic("TCP", INS, 240, sessionSize), new Topic("HTTP", INS, 240, sessionSize), new Topic("Virtualisation",INS, 240, sessionSize));
    Subject cslSubject = new Subject(CSL , new Topic("Logic", CSL, 240, sessionSize), new Topic("Trees", CSL, 240, sessionSize), new Topic("Logic2", CSL, 240, sessionSize),
            new Topic("Trees2", CSL, 240, sessionSize), new Topic("Logic3", CSL, 240, sessionSize), new Topic("Trees3", CSL, 240, sessionSize),
            new Topic("Logic4", CSL, 240, sessionSize), new Topic("Trees4", CSL, 240, sessionSize), new Topic("Logic5", CSL, 240, sessionSize), new Topic("Trees5", CSL, 240, sessionSize));
    Subject cflSubject = new Subject(CFL , new Topic("Rexp", CFL, 240, sessionSize), new Topic("Automata", CFL, 240, sessionSize), new Topic("Lexing", CFL, 240, sessionSize),
            new Topic("Grammars", CFL, 240, sessionSize), new Topic("Interpreter", CFL, 240, sessionSize), new Topic("Compiler", CFL, 240, sessionSize),
            new Topic("Functional", CFL, 240, sessionSize), new Topic("Optimise", CFL, 240, sessionSize));

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
        Map<LocalDate, ArrayList<Period>> assignment = timetable.getAssignment();
        assertNotNull(assignment);
    }

    @Test
    public void twoSubjects() {
        // Test (input of 2 subjects)

        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cisSubject);
        subjects.add(insSubject);
        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
        Map<LocalDate, ArrayList<Period>> assignment = timetable.getAssignment();
        assertNotNull(assignment);
    }

    @Test
    public void threeSubjects() {
        // Test (input of 3 subjects)
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cslSubject);
        subjects.add(insSubject);
        subjects.add(cisSubject);

        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2017, 1, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
        Map<LocalDate, ArrayList<Period>> assignment = timetable.getAssignment();
        assertNotNull(assignment);
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
        Map<LocalDate, ArrayList<Period>> assignment = timetable.getAssignment();
        assertNotNull(assignment);
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
        assertTrue(timetable.getExtraDays() > 0);
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
        assertTrue(timetable.getExtraDays() <= 0);
    }

    @Test
    public void testWrongDates() {
        // Test (input of 3 subjects)
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(cslSubject);
        subjects.add(insSubject);
        subjects.add(cisSubject);

        Timetable timetable = new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(LocalDateTime.of(2016, 12, 15, 9, 0))
                .addExamDate(LocalDate.of(2016, 12, 16))
                .addBreakDuration(breakSize)
                .createTimetable();
        Map<LocalDate, ArrayList<Period>> assignment = timetable.getAssignment();
        assertNull(assignment);
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
