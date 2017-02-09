import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.*;
import spark.Spark;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static spark.Spark.*;

public class Main {

    private enum OBJECT_TYPE {
        CONFIG,
        SUBJECT
    }

    public static void main(String[] args) {

        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

        final Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

        post("/create", (req, res) -> {
            if (!"application/json".equals(req.contentType())) {
                res.status(400);
                return res.status();
            }
            JsonElement input = new JsonParser().parse(req.body());
            JsonObject reqJsonObj = input.getAsJsonObject();
            if (!reqJsonObj.has("config") || !reqJsonObj.has("subjects")) {
                res.status(400);
                return res.status();
            }

            JsonObject configJsonObj = reqJsonObj.getAsJsonObject("config");
            JsonArray subjectsJsonArray = reqJsonObj.getAsJsonArray("subjects");
            if (subjectsJsonArray.size() == 0) {
                res.status(400);
                return res.status();
            }

            Timetable timetable = createTimetable(configJsonObj, subjectsJsonArray);
            if (timetable == null) {
                res.status(400);
                return res.status();
            }

            res.type("application/json");
            return gson.toJson(timetable);
        });

        post("/break/:date", (req, res) -> {
            if (!"application/json".equals(req.contentType())) {
                res.status(400);
                return res.status();
            }
            JsonElement input = new JsonParser().parse(req.body());

            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");

            LocalDate localDate = LocalDate.parse(req.params(":date"));
            Timetable timetable = gson.fromJson(req.body(), Timetable.class);
            timetable.addBreakDay(localDate);
            res.type("application/json");
            return gson.toJson(timetable);

        }) ;
    }

    private static boolean jsonObjHasProps (JsonObject jsonObject, OBJECT_TYPE type) {
        if (jsonObject == null) {
            System.out.println("Object is null");
            return false;
        }
        if (type == OBJECT_TYPE.CONFIG) {
            return jsonObject.has("exam-start-date") && jsonObject.has("revision-start-date") &&
                    jsonObject.has("session-duration") && jsonObject.has("break-duration") &&
                    jsonObject.has("reward");
        } else {
            return jsonObject.has("name") && jsonObject.has("topic-duration") && jsonObject.has("topics");
        }
    }

    private static Timetable createTimetable(JsonObject configJsonObj, JsonArray subjectsJsonArray) {
        ArrayList<Subject> subjects = new ArrayList<>();
        LocalDate examStartDate = null;
        LocalDate revisionStartDate = null;
        int sessionDuration = 0;
        int breakDuration = 0;
        Period rewardPeriod = null;

        for (final JsonElement subjectElem: subjectsJsonArray) {
            if (jsonObjHasProps(configJsonObj, OBJECT_TYPE.CONFIG)) {
                String examDateString = configJsonObj.get("exam-start-date").getAsString();
                examStartDate = LocalDate.parse(examDateString);
                revisionStartDate = LocalDate.parse(configJsonObj.get("revision-start-date").getAsString());
                sessionDuration = configJsonObj.get("session-duration").getAsInt();
                breakDuration = configJsonObj.get("break-duration").getAsInt();
                JsonObject rewardJsonObj = configJsonObj.getAsJsonObject("reward");
                rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, 0, rewardJsonObj.get("duration").getAsInt());
            } else {
                return null;
            }

            final JsonObject subjectObj = subjectElem.getAsJsonObject();
            if (!jsonObjHasProps(subjectObj, OBJECT_TYPE.SUBJECT)) {
                return null;
            }
            int topicDuration = subjectObj.get("topic-duration").getAsInt();
            JsonArray topicsJsonArray = subjectObj.getAsJsonArray("topics");

            Topic[] topics = new Topic[topicsJsonArray.size()];
            for (int i = 0; i < topicsJsonArray.size(); i++) {
                topics[i] = (new Topic(topicsJsonArray.get(i).getAsString(), topicDuration, sessionDuration));
            }
            // Create new subject with topics
            subjects.add(new Subject(subjectObj.get("name").getAsString(), topics));
        }

        return new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(revisionStartDate.atTime(9, 0))
                .addExamDate(examStartDate)
                .addBreakDuration(breakDuration)
                .createTimetable();
    }

}
