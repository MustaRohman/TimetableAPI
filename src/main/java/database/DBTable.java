package database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.Arrays;

//Based on code from dynamo-db sdk examples http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/WorkingWithTables.html
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
}
