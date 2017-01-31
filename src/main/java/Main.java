import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {

//        TODO add exam date as input, calculate spare days
//        TODO Timetable: add method that regenerates timetable when a period changes




        final int sessionSize = 45;
        final int breakSize = 15;

        ArrayList<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("CSL" , new Topic("Logic", 240, sessionSize), new Topic("Trees", 240, sessionSize), new Topic("Logic2", 240, sessionSize),
                new Topic("Trees2", 240, sessionSize), new Topic("Logic3", 240, sessionSize), new Topic("Trees3", 240, sessionSize)));
        subjects.add(new Subject("INS" , new Topic("IP", 240, sessionSize), new Topic("TCP", 240, sessionSize), new Topic("HTTP", 240, sessionSize), new Topic("Virtualisation", 240, sessionSize)));
        Calendar startDateTime = new GregorianCalendar(2016, 11, 15, 9, 0);

        Period rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, 0, 75);

        Timetable timetable = new Timetable(subjects, rewardPeriod, startDateTime, LocalDate.of(2017, 12, 17),  Timetable.REVISION_STYLE.SEQ, sessionSize, breakSize);
        System.out.println(timetable.getSpareDays());


//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        System.out.println(gson.toJson(timetable));

//        JsonParser parser = new JsonParser();

//        try {
//            Object obj = parser.parse(new FileReader("test-files/test1.json"));
//            JsonObject jsonObject = (JsonObject) obj;
//
//            System.out.println(jsonObject.get("subjects"));
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
    }
}
