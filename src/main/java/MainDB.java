import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainDB {
    public static void main(String[] args) {

// This client will default to US West (Oregon)
        AmazonDynamoDBClient client = new AmazonDynamoDBClient()
                .withRegion(Regions.EU_WEST_1)
                .withEndpoint("http://localhost:8000");

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "Users";

//        try {
//            System.out.println("Attempting to create table; please wait...");
//            Table table = dynamoDB.createTable(tableName,
//                    Arrays.asList(
//                            new KeySchemaElement("id", KeyType.HASH)), //Partition key
//                    Arrays.asList(
//                            new AttributeDefinition("id", ScalarAttributeType.S)),
//                    new ProvisionedThroughput(10L, 10L));
//            table.waitForActive();
//            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());
//
//        } catch (Exception e) {
//            System.err.println("Unable to create table: ");
//            System.err.println(e.getMessage());
//        }

        Table table = dynamoDB.getTable("Users");
        System.out.println(table.describe());
//        addItem(table);
//        readItem(table);

    }

    public static void addItem(Table table) {
        int year = 2015;
        String title = "The Big New Movie";

        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("plot",  "Nothing happens at all.");
        infoMap.put("rating",  0);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey("year", year, "title", title)
                    .withMap("info", infoMap));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        } catch (Exception e) {
            System.err.println("Unable to add item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
    }

    public static void readItem(Table table) {

        int year = 2015;
        String title = "The Big New Movie";

        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey("id", year);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome);

        } catch (Exception e) {
            System.err.println("Unable to read item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
    }
}
