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
import java.util.ArrayList;
import java.util.Arrays;

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

        post("/login", (req, res) -> {
            createUsersTable();
            String userId = req.headers("Id");
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
//            Table table = TimetableTable.createTimetablesTable(dynamoDB);
//            if (table != null) {
//                TimetableTable.addItem(dynamoDB, userId, timetable);
//            }
            res.type("application/json");
            return gson.toJson(timetable);
        });

        post("/break/:date", (req, res) -> {
            if (!"application/json".equals(req.contentType())) {
                res.status(400);
                return res.status();
            }

            LocalDate localDate = LocalDate.parse(req.params(":date"));
            Timetable timetable = gson.fromJson(req.body(), Timetable.class);
            if (timetable.addBreakDay(localDate)) {
                res.type("application/json");
                return gson.toJson(timetable);
            }

            res.status(400);
            return res.status();
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
