import org.junit.Assert;
import org.junit.Test;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

//    @Test
//    public void oneSubject() {
////        Test (input of 1 subject)
//
//
//        ArrayList<Subject> subjects = new ArrayList<>();
//        subjects.add(new Subject("CIS", new Topic("RSA", 240, sessionSize), new Topic("DES", 240, sessionSize), new Topic("Diffie Hellman",
//                240, sessionSize), new Topic("Kerberos", 240, sessionSize), new Topic("Block Cipher Modes", 240, sessionSize), new Topic("Modulo", 240, sessionSize),
//                new Topic("Fiat Shamir", 240, sessionSize), new Topic("El-Gamal", 240, sessionSize)));
//        Timetable timetable = new Timetable(subjects, rewardPeriod,  startDateTime, Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
//    }
//
//    @Test
//    public void twoSubjects() {
//        // Test (input of 2 subjects)
//
//        ArrayList<Subject> subjects = new ArrayList<>();
//        subjects.add(new Subject("CSL" , new Topic("Logic", 240, sessionSize), new Topic("Trees", 240, sessionSize), new Topic("Logic2", 240, sessionSize),
//                new Topic("Trees2", 240, sessionSize), new Topic("Logic3", 240, sessionSize), new Topic("Trees3", 240, sessionSize)));
//        subjects.add(new Subject("INS" , new Topic("IP", 240, sessionSize), new Topic("TCP", 240, sessionSize), new Topic("HTTP", 240, sessionSize), new Topic("Virtualisation", 240, sessionSize)));
//        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
//        Timetable timetable = new Timetable(subjects, rewardPeriod, startDateTime, Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
//    }

    @Test
    public void threeSubjects() {
        // Test (input of 3 subjects)
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CSL" , new Topic("Logic", 240, sessionSize), new Topic("Trees", 240, sessionSize), new Topic("Logic2", 240, sessionSize),
                new Topic("Trees2", 240, sessionSize), new Topic("Logic3", 240, sessionSize), new Topic("Trees3", 240, sessionSize),
                new Topic("Logic4", 240, sessionSize), new Topic("Trees4", 240, sessionSize), new Topic("Logic5", 240, sessionSize), new Topic("Trees5", 240, sessionSize)));
        subjects.add(new Subject("INS" , new Topic("IP", 240, sessionSize), new Topic("TCP", 240, sessionSize), new Topic("HTTP", 240, sessionSize),
                new Topic("XML, HTML", 240, sessionSize), new Topic("SOAP", 240, sessionSize), new Topic("Security", 240, sessionSize), new Topic("Virtualisation", 240, sessionSize)));
        subjects.add(new Subject("CIS", new Topic("RSA", 240, sessionSize), new Topic("DES", 240, sessionSize), new Topic("Diffie Hellman",
                240, sessionSize), new Topic("Kerberos", 240, sessionSize), new Topic("Block Cipher Modes", 240, sessionSize), new Topic("Modulo", 240, sessionSize),
                new Topic("Fiat Shamir", 240, sessionSize), new Topic("El-Gamal", 240, sessionSize)));

        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
        Timetable timetable = new Timetable(subjects, rewardPeriod , startDateTime, LocalDate.of(2017,1,15), Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
    }
//
//    @Test
//    public void fourSubjects() {
//        // Test (input of 4 subjects)
//        ArrayList<Subject> subjects = new ArrayList<>();
//        subjects.add(new Subject("CSL" , new Topic("Logic", 240, sessionSize), new Topic("Trees", 240, sessionSize), new Topic("Logic2", 240, sessionSize),
//                new Topic("Trees2", 240, sessionSize), new Topic("Logic3", 240, sessionSize), new Topic("Trees3", 240, sessionSize),
//                new Topic("Logic4", 240, sessionSize), new Topic("Trees4", 240, sessionSize), new Topic("Logic5", 240, sessionSize), new Topic("Trees5", 240, sessionSize)));
//        subjects.add(new Subject("CFL" , new Topic("Rexp", 240, sessionSize), new Topic("Automata", 240, sessionSize), new Topic("Lexing", 240, sessionSize),
//                new Topic("Grammars", 240, sessionSize), new Topic("Interpreter", 240, sessionSize), new Topic("Compiler", 240, sessionSize),
//                new Topic("Functional", 240, sessionSize), new Topic("Optimise", 240, sessionSize)));
//        subjects.add(new Subject("INS" , new Topic("IP", 240, sessionSize), new Topic("TCP", 240, sessionSize), new Topic("HTTP", 240, sessionSize),
//                new Topic("XML, HTML", 240, sessionSize), new Topic("SOAP", 240, sessionSize), new Topic("Security", 240, sessionSize), new Topic("Virtualisation", 240, sessionSize)));
//        subjects.add(new Subject("CIS", new Topic("RSA", 240, sessionSize), new Topic("DES", 240, sessionSize), new Topic("Diffie Hellman",
//                240, sessionSize), new Topic("Kerberos", 240, sessionSize), new Topic("Block Cipher Modes", 240, sessionSize), new Topic("Modulo", 240, sessionSize),
//                new Topic("Fiat Shamir", 240, sessionSize), new Topic("El-Gamal", 240, sessionSize)));
//
//        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);
//        Timetable timetable = new Timetable(subjects, rewardPeriod , startDateTime, Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
//    }

    @Test
    public void testSpareDays() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CIS", new Topic("RSA", 240, sessionSize), new Topic("DES", 240, sessionSize), new Topic("Diffie Hellman",
                240, sessionSize), new Topic("Kerberos", 240, sessionSize), new Topic("Block Cipher Modes", 240, sessionSize), new Topic("Modulo", 240, sessionSize),
                new Topic("Fiat Shamir", 240, sessionSize), new Topic("El-Gamal", 240, sessionSize)));
        Timetable timetable = new Timetable(subjects, rewardPeriod,  startDateTime, LocalDate.of(2017, 1, 16) , Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
        assertTrue(timetable.getSpareDays() > 0);
    }

    @Test
    public void testSpareDaysFail() {
        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CIS", new Topic("RSA", 240, sessionSize), new Topic("DES", 240, sessionSize), new Topic("Diffie Hellman",
                240, sessionSize), new Topic("Kerberos", 240, sessionSize), new Topic("Block Cipher Modes", 240, sessionSize), new Topic("Modulo", 240, sessionSize),
                new Topic("Fiat Shamir", 240, sessionSize), new Topic("El-Gamal", 240, sessionSize)));
        Timetable timetable = new Timetable(subjects, rewardPeriod,  startDateTime, LocalDate.of(2016, 12, 16) , Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
        assertTrue(timetable.getSpareDays() < 0);
    }

    @Test
    public void testIncrementTimeAndSubject() {

    }

}