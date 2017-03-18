package database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import timetable.Timetable;

import java.util.Arrays;

/**
 * Created by mustarohman on 18/03/2017.
 */
public class TimetableTable {



    public static Table createTimetablesTable(DynamoDB dynamoDB) {
        String tableName = "Timetables";
        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
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
            return null;
        }
    }

    public static void addItem(DynamoDB dynamoDB, String userId, Timetable timetable) {
        Table table = dynamoDB.getTable("Timetables");
        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("UserId", userId, "Name", timetable.getName())
                    .withList("Subjects", timetable.getSubjects())
                    .with("Reward", timetable.getRewardPeriod())
                    .with("StartDateTime", timetable.getStartDateTime())
                    .with("ExamStartDate", timetable.getExamStartDate())
                    .with("RevisionEndDate", timetable.getRevisionEndDate())
//                    .with()
            );

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            System.err.println("Unable to add item: " + " id: " + userId);
            System.err.println(e.getMessage());
        }
    }
}
