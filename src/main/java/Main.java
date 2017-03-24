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
import database.TimetableTable;
import spark.Spark;
import timetable.Period;
import timetable.Subject;
import timetable.Timetable;
import timetable.Topic;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

import static spark.Spark.*;

public class Main {

    private static DynamoDB dynamoDB = new DynamoDB(new AmazonDynamoDBClient()
            .withRegion(Regions.EU_WEST_1)
            .withEndpoint("http://localhost:8000"));

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


        get("/list", (req, res) -> {
            String userId = req.headers("UserId");
            if (userId == null) {
                res.status(400);
                return res.status();
            }
            ArrayList<String> list = TimetableTable.getTimetableList(dynamoDB, userId);
            res.type("application/json");
            return gson.toJson(list);
        });

        get("/agenda/day/:date", (req, res) -> {
            // Returns Subjects and their topics that are assigned to the day
            String userId = req.headers("UserId");
            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.getTimetableAssignment(dynamoDB, userId);
            String dateParam = req.params("date");
            LocalDate date = LocalDate.parse(dateParam);
            ArrayList<String> agenda = getAgendaForDay(assignment, date);
            if (agenda == null || agenda.isEmpty()) {
                res.status(400);
                return res.status();
            }
            res.type("application/json");
            return gson.toJson(agenda);
        });

        get("/agenda/week/:week", (req, res) -> {
            // Returns Subjects and their topics that are assigned to the week
            String userId = req.headers("UserId");
            Map<LocalDate, ArrayList<Period>> assignment = TimetableTable.getTimetableAssignment(dynamoDB, userId);
//            2015-W49
            String week = req.params("week");
            ArrayList<String> agenda = getAgendaForWeek(assignment, week);
            res.type("application/json");
            return gson.toJson(agenda);
        });

        get("/free", (req, res) -> {
            // Number of free days available
            String userId = req.headers("UserId");
            return TimetableTable.getFreeDays(dynamoDB, userId);
        });

        get("/progress/subject/:subject", (req, res) -> {
            String userId = req.headers("UserId");
            return null;
        });

        get("/progress/revision", (req, res) -> {
            String userId = req.headers("UserId");
            return null;
        });

        post("/login", (req, res) -> {
            createUsersTable();
            String userId = req.headers("UserId");
            if (checkUser(userId)) addUser(userId);
            res.status(200);
            return res.status();
        });

        post("/create", (req, res) -> {
            String userId = req.headers("UserId");

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
//            TimetableTable.deleteTimetablesTable(dynamoDB);
            Table table = TimetableTable.createTimetablesTable(dynamoDB);
            Item item = null;
            if (table != null) {
                item = TimetableTable.addItem(dynamoDB, userId, timetable);
            }
            res.type("application/json");

            System.out.println(item);
            return item.getJSON("Assignment");
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
                return res.status();
            }

            LocalDate localDate = LocalDate.parse(req.params(":date"));
            Timetable timetable = gson.fromJson(req.body(), Timetable.class);
            timetable = Timetable.addBreakDay(localDate, timetable);
            if (timetable != null) {
                System.out.println("Timetable is not null");
                res.type("application/json");
                return gson.toJson(timetable);
            }

            System.out.println("Timetable is null");
            res.status(400);
            return res.status();
        }) ;

        post("/extra/:subject", (req, res) -> {
            // Assign extra day to a particular subject/topic
            String userId = req.headers("UserId");
            return null;
        });

    }

    private static ArrayList<String> getAgendaForWeek(Map<LocalDate, ArrayList<Period>> assignment, String weekNumber) {
        String[] split = weekNumber.split("-");
        int year = Integer.parseInt(split[0]);
        int week = Integer.parseInt(split[1].substring(1));
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate ldt = LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1);
        ArrayList<String> topicsAgenda = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            System.out.println(ldt.toString());
            ArrayList<String> dayAgenda = getAgendaForDay(assignment, ldt);
            if (dayAgenda != null) {
                topicsAgenda.addAll(dayAgenda);
            }
            ldt = ldt.plusDays(1);
        }
        return topicsAgenda;
    }

    private static ArrayList<String> getAgendaForDay(Map<LocalDate, ArrayList<Period>> assignment, LocalDate date) {
        ArrayList<Period> periodsForDay = assignment.get(date);
        ArrayList<String> topicsAgenda = new ArrayList<>();
        if (periodsForDay == null || periodsForDay.isEmpty()) {
            return null;
        }
//        periodsForDay.stream().filter(period -> !topicsAgenda.contains(period.getTopicName())).forEach(period -> {
//            topicsAgenda.add(period.getTopicName());
//        });
        for (Period period: periodsForDay) {
            if (!topicsAgenda.contains(period.getTopicName()) && period.getTopicName() != null) {
                topicsAgenda.add(period.getTopicName());
            }
        }
        return topicsAgenda;
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

    private static void createUsersTable() {
        String tableName = "Users";

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(
                            new KeySchemaElement("id", KeyType.HASH)), //Partition key
                    Arrays.asList(
                            new AttributeDefinition("id", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }

    }

    private static boolean  checkUser(String userId) {

        Table table = dynamoDB.getTable("Users");
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey("id", userId);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);
            return true;

        } catch (Exception e) {
            System.err.println("Unable to read item: " + " id: " + userId);
            System.err.println(e.getMessage());
            return false;
        }
    }

    private static void addUser(String userId) {
        Table table = dynamoDB.getTable("Users");
        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("id", userId));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            System.err.println("Unable to add item: " + " id: " + userId);
            System.err.println(e.getMessage());
        }
    }

}
