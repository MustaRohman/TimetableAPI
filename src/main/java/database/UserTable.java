package database;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import org.mindrot.jbcrypt.BCrypt;
import timetable.Timetable;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by mustarohman on 04/04/2017.
 */
public class UserTable {

    public final static String TABLE_NAME = "Users";
    private final static String USER_ID_ATTR = "UserId";
    public final static String CODE_ATTR = "Code";

    public static Item getItem(DynamoDB dynamoDB, String userId) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        return table.getItem(USER_ID_ATTR, userId);
    }

    public static String getUserIdByCode(DynamoDB dynamoDB, String code) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        ScanSpec spec = new ScanSpec()
                .withFilterExpression(CODE_ATTR + "= :v_id")
                .withValueMap(new ValueMap()
                        .withString(":v_id", code));
        ItemCollection<ScanOutcome> items = table.scan(spec);
        Item item = null;
        if (items.iterator().hasNext()) {
            item = items.iterator().next();
        }
        if (item == null) {
            return null;
        }
        return item.getString(USER_ID_ATTR);
    }


    public static Item addItem(DynamoDB dynamoDB, String userId) {
        Table table = dynamoDB.getTable(TABLE_NAME);
//        String count = String.valueOf(table.getDescription().getItemCount() + 1);
//        String code = BCrypt.hashpw(count, BCrypt.gensalt(4));
        String code = UUID.randomUUID().toString();
        code = code.substring(0,13);
        System.out.println(code);
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression(CODE_ATTR + "= :v_id")
                .withValueMap(new ValueMap()
                        .withString(":v_id", code));

        ItemCollection<QueryOutcome> items = table.query(spec);
        while (items.getAccumulatedScannedCount() > 0) {
            System.out.println("Regenerating code...");
            code = UUID.randomUUID().toString().substring(0,13);
            System.out.println(code);
            items = table.query(spec);
        }
        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey(USER_ID_ATTR, userId)
                    .withString(CODE_ATTR, code)
            );

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            return table.getItem(USER_ID_ATTR, userId);

        } catch (Exception e) {
            System.err.println("Unable to add item: " + " id: " + userId);
            System.err.println(e.getMessage());
            return null;
        }
    }
}
