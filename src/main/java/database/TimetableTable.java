package database;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import timetable.Period;
import timetable.Timetable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mustarohman on 18/03/2017.
 */
public class TimetableTable {

    private final static String TABLE_NAME = "Timetables";
    private final static String USER_ID_ATTR = "UserId";
    private final static String NAME_ATTR = "Name";
    private final static String SUBJECTS_ATTR = "Subjects";
    private final static String REWARD_ATTR = "Reward";
    private final static String START_DATE_TIME_ATTR = "StartDateTime";
    private final static String EXAM_START_DATE_ATTR = "ExamStartDate";
    private final static String REVISION_END_DATE_ATTR = "RevisionEndDate";
    private final static String SPARE_DAYS_ATTR = "ExtraDays";
    private final static String BREAK_SIZE_ATTR = "BreakSize";
    private final static String ASSIGNMENT_ATTR= "Assignment";

    static final Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();


    public static Table createTimetablesTable(DynamoDB dynamoDB) {
        Table table = null;
        try {
            System.out.println("Attempting to create table; please wait...");
            table = dynamoDB.createTable(TABLE_NAME,
                    Arrays.asList(
                            new KeySchemaElement(USER_ID_ATTR, KeyType.HASH)), //Partition key
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

    public static Item getItem(DynamoDB dynamoDB, String userId) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        Item item = table.getItem(USER_ID_ATTR, userId);
        return item;
    }

    public static void deleteTimetablesTable(DynamoDB dynamoDB) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        try {
            table.delete();
            table.waitForDelete();
        } catch (InterruptedException e) {
            System.out.println("Unable to delete Timetables table");
            e.printStackTrace();
        }
    }

    public static Item addItem(DynamoDB dynamoDB, String userId, Timetable timetable) {
        Table table = dynamoDB.getTable("Timetables");
        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey(USER_ID_ATTR, userId)
                    .withString(NAME_ATTR, timetable.getName())
                    .withJSON(SUBJECTS_ATTR, gson.toJson(timetable.getSubjects()))
                    .withJSON(REWARD_ATTR,  gson.toJson(timetable.getRewardPeriod()))
                    .withJSON(START_DATE_TIME_ATTR, gson.toJson(timetable.getStartDateTime()))
                    .withJSON(EXAM_START_DATE_ATTR, gson.toJson(timetable.getExamStartDate()))
                    .withJSON(REVISION_END_DATE_ATTR, gson.toJson(timetable.getRevisionEndDate()))
                    .withLong(SPARE_DAYS_ATTR, timetable.getExtraDays())
                    .withInt(BREAK_SIZE_ATTR, timetable.getBreakSize())
                    .withJSON(ASSIGNMENT_ATTR, gson.toJson(timetable.getTimetableAssignment()))
            );

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            return table.getItem(USER_ID_ATTR, userId);

        } catch (Exception e) {
            System.err.println("Unable to add item: " + " id: " + userId);
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static ArrayList<String> getTimetableList(DynamoDB dynamoDB, String userId) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        // Query table with user id for all timetables
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression(USER_ID_ATTR + "= :v_id")
                .withValueMap(new ValueMap()
                        .withString(":v_id", userId));

        ItemCollection<QueryOutcome> items = table.query(spec);
        ArrayList<String> list = new ArrayList<>();

        Iterator<Item> iterator = items.iterator();
        Item item = null;
        while (iterator.hasNext()) {
            item = iterator.next();
            list.add(item.getString(NAME_ATTR));
        }
        return list;
    }


    public static Map<LocalDate, ArrayList<Period>> getTimetableAssignment(DynamoDB dynamoDB, String userId) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        Item item = getItem(dynamoDB, userId);
        String json = item.getJSON(ASSIGNMENT_ATTR);
        Type type = new TypeToken<Map<LocalDate, ArrayList<Period>>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static int getFreeDays(DynamoDB dynamoDB, String userId) {
        Item item = getItem(dynamoDB, userId);
        return item.getInt(SPARE_DAYS_ATTR);
    }

    public static LocalDate[] getStartAndEndDates(DynamoDB dynamoDB, String userId) {
        Item item = getItem(dynamoDB, userId);
        LocalDate[] startAndEndDates = new LocalDate[2];
        String startJson = item.getJSON(START_DATE_TIME_ATTR);
        String endJson = item.getJSON(REVISION_END_DATE_ATTR);
        LocalDateTime ldt = gson.fromJson(startJson, LocalDateTime.class);
        startAndEndDates[0] = ldt.toLocalDate();
        startAndEndDates[1] = gson.fromJson(endJson, LocalDate.class);
        return startAndEndDates;
    }

    public static Map<LocalDate,ArrayList<Period>> assignExtraRevisionDay(DynamoDB dynamoDB, String userId, String subject) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        Item item = table.getItem(USER_ID_ATTR, userId);

        String json = item.getJSON(ASSIGNMENT_ATTR);
        Type type = new TypeToken<Map<LocalDate, ArrayList<Period>>>(){}.getType();
        Map<LocalDate, ArrayList<Period>> assignment = gson.fromJson(json, type);

        LocalDate revisionEndDate = gson.fromJson(item.getJSON(REVISION_END_DATE_ATTR), LocalDate.class);
        revisionEndDate = revisionEndDate.plusDays(1);

        long freeDays = item.getLong(SPARE_DAYS_ATTR);

        ArrayList<Period> extraRevisionDay = new ArrayList<>();
        extraRevisionDay.add(new Period(Period.PERIOD_TYPE.EXTRA_REVISION_DAY, subject, 0, 1500));
        assignment.put(revisionEndDate, extraRevisionDay);
        freeDays--;

        boolean success = updateTimetableAssignment(table, item, userId, freeDays, revisionEndDate, assignment);

        if (success) {
            return assignment;
        } else {
            return null;
        }
    }

    public static  Map<LocalDate, ArrayList<Period>> addBreakDay(DynamoDB dynamoDB, String userId, LocalDate breakDate) {
        Table table = dynamoDB.getTable(TABLE_NAME);
        Item item = getItem(dynamoDB, userId);
        Map<LocalDate, ArrayList<Period>> assignment = getTimetableAssignment(dynamoDB, userId);
        long freeDays = getFreeDays(dynamoDB, userId);
        LocalDate revisionEndDate = getStartAndEndDates(dynamoDB, userId)[1];
        if (!assignment.containsKey(breakDate) || freeDays <= 0) {
            System.out.println(freeDays);
            return null;
        }
        ArrayList<Period> breakDay = new ArrayList<>();
        breakDay.add(new Period(Period.PERIOD_TYPE.BREAK_DAY, null, 0, 1500));
        ArrayList<Period> periods = assignment.replace(breakDate, breakDay);
        assignment.put(revisionEndDate.plusDays(1), null);
        LocalDate date = breakDate.plusDays(1);
        for (Period period: periods) {
            period.setDateTime(period.getDateTime().plusDays(1));
        }

        while (assignment.containsKey(date)) {
            periods = assignment.replace(date, periods);
            if (periods != null) {
                for (Period period: periods) {
                    period.setDateTime(period.getDateTime().plusDays(1));
                }
            }
            date = date.plusDays(1);
        }

        freeDays--;
        revisionEndDate.plusDays(1);

        boolean success = updateTimetableAssignment(table, item, userId, freeDays, revisionEndDate, assignment);

        if (success) {
            return assignment;
        } else {
            return null;
        }
    }

    private static boolean updateTimetableAssignment(Table table, Item item, String userId, long freeDays,
                                                  LocalDate revisionEndDate, Map<LocalDate, ArrayList<Period>> assignment) {
        try {
            System.out.println("Updating existing item...");
            PutItemOutcome outcome = table.putItem(new Item()
                    .withPrimaryKey(USER_ID_ATTR, userId)
                    .withString(NAME_ATTR, item.getString(NAME_ATTR))
                    .withJSON(SUBJECTS_ATTR, item.getJSON(SUBJECTS_ATTR))
                    .withJSON(REWARD_ATTR,  item.getJSON(REWARD_ATTR))
                    .withJSON(START_DATE_TIME_ATTR, item.getJSON(START_DATE_TIME_ATTR))
                    .withJSON(EXAM_START_DATE_ATTR, item.getJSON(EXAM_START_DATE_ATTR))
                    .withJSON(REVISION_END_DATE_ATTR, gson.toJson(revisionEndDate))
                    .withLong(SPARE_DAYS_ATTR, freeDays)
                    .withInt(BREAK_SIZE_ATTR, item.getInt(BREAK_SIZE_ATTR))
                    .withJSON(ASSIGNMENT_ATTR, gson.toJson(assignment))
            );
            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            return true;
        } catch (Exception e) {
            System.err.println("Unable to add item: " + " id: " + userId);
            System.err.println(e.getMessage());
            return false;
        }
    }
}