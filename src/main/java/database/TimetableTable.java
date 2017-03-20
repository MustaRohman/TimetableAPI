package database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import timetable.Timetable;

import java.util.Arrays;

/**
 * Created by mustarohman on 18/03/2017.
 */
public class TimetableTable {

    static String tableName = "Timetables";
    static final Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();


    public static Table createTimetablesTable(DynamoDB dynamoDB) {
        Table table = null;
        try {
            System.out.println("Attempting to create table; please wait...");
            table = dynamoDB.createTable(tableName,
                    Arrays.asList(
                            new KeySchemaElement("UserId", KeyType.HASH), //Partition key
                            new KeySchemaElement("Name", KeyType.RANGE)), //Sort Key
                    Arrays.asList(
                            new AttributeDefinition("UserId", ScalarAttributeType.S),
                            new AttributeDefinition("Name", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
            return table;

        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
            table = dynamoDB.getTable("Timetables");
            if (table != null) {
                return table;
            }
            return null;
        }
    }

    public static Item getItem(DynamoDB dynamoDB, String userId, String name) {
        Table table = dynamoDB.getTable(tableName);
        Item item = table.getItem("UserId", userId, "Name", name);
        return item;
    }

    public static void deleteTimetablesTable(DynamoDB dynamoDB) {
        Table table = dynamoDB.getTable(tableName);
        table.delete();
        try {
            table.waitForDelete();
        } catch (InterruptedException e) {
            System.out.println("Unable to delete Timetables table");
            e.printStackTrace();
        }
    }

    public static void addItem(DynamoDB dynamoDB, String userId, Timetable timetable) {
        Table table = dynamoDB.getTable("Timetables");
        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("UserId", userId, "Name", timetable.getName())
                    .withJSON("Subjects", gson.toJson(timetable.getSubjects()))
                    .withJSON("Reward",  gson.toJson(timetable.getRewardPeriod()))
                    .with("StartDateTime", timetable.getStartDateTime().toString())
                    .with("ExamStartDate", timetable.getExamStartDate().toString())
                    .with("RevisionEndDate", timetable.getRevisionEndDate().toString())
                    .withLong("SpareDays", timetable.getSpareDays())
                    .withInt("BreakSize", timetable.getBreakSize())
                    .withJSON("Assignment", gson.toJson(timetable.getTimetableAssignment()))
            );

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            Item item = table.getItem("UserId", userId, "Name", timetable.getName());

        } catch (Exception e) {
            System.err.println("Unable to add item: " + " id: " + userId);
            System.err.println(e.getMessage());
        }
    }
}
