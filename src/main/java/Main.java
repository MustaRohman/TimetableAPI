
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.time.LocalDate;
import java.util.*;

public class Main {

    public static void main(String[] args) {

//        TODO add exam date as input, calculate spare days
//        TODO Timetable: add method that regenerates timetable when a period changes




        final int sessionSize = 45;
        final int breakSize = 15;

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

        Period rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, 0, 75);

        Timetable timetable = new Timetable(subjects, rewardPeriod, startDateTime, LocalDate.of(2017, 1, 17),  Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
        System.out.println(timetable.getSpareDays());


        Iterator it = timetable.getAssignment().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + ": " + pair.getValue());
        }
    }
}
