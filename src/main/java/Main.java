import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.*;
import database.DBTable;
import database.TimetableTable;
import database.UserTable;
import spark.Spark;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;
import static spark.Spark.*;

public class Main {

//    Based on code from http://sparkjava.com

    private static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient()
            .withRegion(Regions.US_EAST_1));

    private enum OBJECT_TYPE {
        CONFIG,
        SUBJECT
    }

    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        Spark.exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
        });

        final Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

        get("/", (req, res) -> "Welcome to the StudyFriend Timetable API");

        get("/subjects", (req, res) -> {
            String userId = req.headers("UserId");
            ArrayList<String> subjectList = TimetableTable.getSubjectsList(dynamoDB, userId);
            if (subjectList == null) {
                System.out.println("No timetable data under UserId");
                res.status(400);
                return "No timetable data under UserId";
            }
            res.type("application/json");
            return gson.toJson(subjectList);
        });

        get("/task/:date/:time", (req, res) -> {
            String userId = req.headers("UserId");
            LocalDate date = LocalDate.parse(req.params("date"));
            LocalTime time = LocalTime.parse(req.params("time"));
            Period task = TimetableTable.getTaskAtTimeAndDate(dynamoDB, userId, date, time);
            if (task == null) {
                System.out.println("Unable to get task");
                res.status(400);
                return "Unable to get task at that date and time";
            }
            res.type("application/json");
            return gson.toJson(task);
        });

        get("/agenda/day/:date", (req, res) -> {
            // Returns Subjects and their topics that are assigned to the day
            String userId = req.headers("UserId");
            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.getTimetableAssignment(dynamoDB, userId);
            if (assignment == null) {
                System.out.println("No timetable data under UserId");
                res.status(400);
                return "No timetable data under UserId";
            }
            String dateParam = req.params("date");
            LocalDate date = LocalDate.parse(dateParam);
            ArrayList<String> agenda = Timetable.getAgendaForDay(assignment, date);
            if (agenda == null) {
                System.out.println("Error");
                res.status(400);
                return "Bad request";
            }

            if (agenda.isEmpty()) {
                System.out.println("No event has been assigned for that date");
                res.status(400);
                return "No event has been assigned for that date";
            }
            System.out.println("Sending agenda");
            res.type("application/json");
            return gson.toJson(agenda);
        });

        get("/agenda/week/:week", (req, res) -> {
            // Returns Subjects and their topics that are assigned to the week
            // Input: 2015-W49
            String userId = req.headers("UserId");
            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.getTimetableAssignment(dynamoDB, userId);
            if (assignment == null) {
                System.out.println("No timetable data under UserId");
                res.status(400);
                return "No timetable data under UserId";
            }
//            2015-W49
            String week = req.params("week");
            ArrayList<String> agenda = Timetable.getAgendaForWeek(assignment, week, false);
            res.type("application/json");
            return gson.toJson(agenda);
        });

        get("/agenda/weekend/:week", (req, res) -> {
            String userId = req.headers("UserId");
            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.getTimetableAssignment(dynamoDB, userId);
            if (assignment == null) {
                System.out.println("No timetable data under UserId");
                res.status(400);
                return "No timetable data under UserId";
            }
//            2015-W49-WE
            String week = req.params("week");
            ArrayList<String> agenda = Timetable.getAgendaForWeek(assignment, week, true);
            res.type("application/json");
            return gson.toJson(agenda);
        });

        get("/free", (req, res) -> {
            // Number of free days available
            String userId = req.headers("UserId");
            Integer freeDays = TimetableTable.getFreeDays(dynamoDB, userId);
            if (freeDays == null) {
                res.status(400);
                return "No timetable data under UserId";
            } else {
                return freeDays;
            }
        });

        get("/progress/subject/:subject", (req, res) -> {
            String userId = req.headers("UserId");
            return null;
        });

        get("/progress/revision/:date", (req, res) -> {
            String userId = req.headers("UserId");
            LocalDate date = LocalDate.parse(req.params("date"));
            LocalDate[] startAndEndDates = TimetableTable.getRevisionStartAndEndDates(dynamoDB, userId);
            long length = DAYS.between(startAndEndDates[0], startAndEndDates[1]);
            long progress = DAYS.between(startAndEndDates[0], date);
            if (progress < 0) {
                res.status(400);
                return "Invalid Date";
            }
            System.out.println("Progress: " + progress);
            System.out.println("Length: " + length);
            float perc = 100 * progress/length;
            if (perc > 100) {
                return 100;
            }
            System.out.println(perc);
            return String.format("%.2f", perc);
        });

        get("/exam-start", (req, res) -> {
            String userId = req.headers("UserId");
            LocalDate ldt = TimetableTable.getExamStartDate(dynamoDB, userId);
            if (ldt == null) {
                res.status(400);
                return "Unable to get exam start date";
            } else {
                System.out.println(ldt.toString());
                return ldt.toString();
            }
        });

        get("/days-until-exam/:date", (req, res) -> {
            String userId = req.headers("UserId");
            LocalDate ldt = LocalDate.parse(req.params("date"));
            Long days = TimetableTable.getDaysUntilExamStart(dynamoDB, userId, ldt);
            if (days == null) {
                res.status(400);
                return "No timetable data under UserId";
            } else {
                return days;
            }
        });

        get("/login", (req, res) -> {
            Table table = DBTable.createTable(dynamoDB, UserTable.TABLE_NAME);
            String code = req.headers("code");
            String userId = UserTable.getUserIdByCode(dynamoDB, code);
            if (userId == null) {
                res.status(400);
                return "Unable to get code";
            }
            res.status(200);
            return userId;
        });


        post("/create", (req, res) -> {
            String userId = req.headers("UserId");
            System.out.println(userId);

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

            if (timetable.getExtraDays() <= 0) {
                res.status(400);
                return "Revision start date and Exam start date are too close.";
            }
//            TimetableTable.deleteTimetablesTable(dynamoDB);
            Table table = DBTable.createTable(dynamoDB, TimetableTable.TABLE_NAME);
            Item item = null;
            if (table != null) {
                item = TimetableTable.addItem(dynamoDB, userId, timetable);
            }
            res.type("application/json");

            System.out.println(item);
            return item.getJSON("Assignment");
        });

        post("/launch", (req, res) -> {
            String userId = req.headers("UserId");
            if (userId == null) {
                res.status(400);
                return res.status();
            }
            Item item = UserTable.getItem(dynamoDB, userId);
            if (item != null) {
                System.out.println("Item already exists");
                return item.getString(UserTable.CODE_ATTR);
            }

            item = UserTable.addItem(dynamoDB, userId);
            return item.getString(UserTable.CODE_ATTR);
        });

        post("/break/:date", (req, res) -> {
            System.out.println("Adding break day...");
            String userId = req.headers("UserId");
            if (userId == null) {
                System.out.println("UserId is null");
                res.status(400);
                return res.status();
            }
            if (!"application/json".equals(req.contentType())) {
                System.out.println();
                res.status(400);
                return "Invalid content type";
            }

            LocalDate localDate = LocalDate.parse(req.params(":date"));
            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.addBreakDay(dynamoDB, userId, localDate);
            if (assignment != null) {
                System.out.println("Timetable is not null");
                res.type("application/json");
                return gson.toJson(assignment.get(localDate));
            }

            System.out.println("Timetable is null");
            res.status(400);
            return "Unable to add break day";
        }) ;

//        post("/extra/:subject", (req, res) -> {
//            // Assign extra day to a particular subject/topic
//            String userId = req.headers("UserId");
//            String subject = req.params("subject");
//            Integer freeDays = TimetableTable.getFreeDays(dynamoDB, userId);
//            if (freeDays == null) {
//                res.status(400);
//                return "No timetable under UserId";
//            }
//            if (freeDays <= 0) {
//                res.status(400);
//                return "No spare days left";
//            }
//            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.assignExtraRevisionDay(dynamoDB, userId, subject);
//            if (assignment == null) {
//                res.status(400);
//                return "Unable to assign extra revision day";
//            }
//            res.type("application/json");
//            return gson.toJson(assignment);
//        });

    }


    private static boolean jsonObjHasProps (JsonObject jsonObject, OBJECT_TYPE type) {
        if (jsonObject == null) {
            System.out.println("Object is null");
            return false;
        }
        if (type == OBJECT_TYPE.CONFIG) {
            return jsonObject.has("exam-start-date") && jsonObject.has("revision-start-date") &&
                    jsonObject.has("session-duration") && jsonObject.has("break-duration") &&
                    jsonObject.has("reward") && jsonObject.has("name");
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
        String name = null;

        for (final JsonElement subjectElem: subjectsJsonArray) {
            if (jsonObjHasProps(configJsonObj, OBJECT_TYPE.CONFIG)) {
                name = configJsonObj.get("name").getAsString();
                String examDateString = configJsonObj.get("exam-start-date").getAsString();
                examStartDate = LocalDate.parse(examDateString);
                revisionStartDate = LocalDate.parse(configJsonObj.get("revision-start-date").getAsString());
                sessionDuration = configJsonObj.get("session-duration").getAsInt();
                breakDuration = configJsonObj.get("break-duration").getAsInt();
                JsonObject rewardJsonObj = configJsonObj.getAsJsonObject("reward");
                rewardPeriod = new Period(Period.PERIOD_TYPE.REWARD, null, null, 0, rewardJsonObj.get("duration").getAsInt());
            } else {
                return null;
            }

            final JsonObject subjectObj = subjectElem.getAsJsonObject();
            String subjectName = subjectObj.get("name").getAsString();
            if (!jsonObjHasProps(subjectObj, OBJECT_TYPE.SUBJECT)) {
                return null;
            }
            int topicDuration = subjectObj.get("topic-duration").getAsInt();
            JsonArray topicsJsonArray = subjectObj.getAsJsonArray("topics");

            Topic[] topics = new Topic[topicsJsonArray.size()];
            for (int i = 0; i < topicsJsonArray.size(); i++) {
                topics[i] = (new Topic(topicsJsonArray.get(i).getAsString(), subjectName,topicDuration, sessionDuration));
            }
            // Create new subject with topics
            subjects.add(new Subject(subjectName, topics));
        }

        return new Timetable.TimetableBuilder()
                .addSubjects(subjects)
                .addRewardPeriod(rewardPeriod)
                .addStartDate(revisionStartDate.atTime(9, 0))
                .addExamDate(examStartDate)
                .addBreakDuration(breakDuration)
                .addName(name)
                .createTimetable();
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
