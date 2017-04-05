package database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.Arrays;

/**
 * Created by mustarohman on 04/04/2017.
 */
public class DBTable {

    public static Table createTable(DynamoDB dynamoDB, String tableName) {
        Table table = null;
        try {
            System.out.println("Attempting to create table; please wait...");
            table = dynamoDB.createTable(tableName,
                    Arrays.asList(
                            new KeySchemaElement("UserId", KeyType.HASH)), //Partition key
                    Arrays.asList(
                            new AttributeDefinition("UserId", ScalarAttributeType.S)),
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

    public static void deleteTable(DynamoDB dynamoDB, String tableName) {
        Table table = dynamoDB.getTable(tableName);
        try {
            table.delete();
            table.waitForDelete();
        } catch (InterruptedException e) {
            System.out.println("Unable to delete Timetables table");
            e.printStackTrace();
        }
    }
}
