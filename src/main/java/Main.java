import com.google.gson.*;
import timetable.Period;

import java.time.LocalDate;

import static spark.Spark.*;

/**
 * Created by mustarohman on 25/01/2017.
 */

public class Main {
    public static void main(String[] args) {
        Period period = new Period(Period.PERIOD_TYPE.SUBJECT, "TEST" ,3, 45);
        Gson gson = new Gson();
        get("/hello", (req, res) -> {
            res.type("application/json");
            return gson.toJson(period);
        });
        post("/test", (req, res) -> {
            return gson.toJson(period);
        });

        post("/timetable", (req, res) -> {
            if ("application/json".equals(req.contentType())) {
                JsonElement input = new JsonParser().parse(req.body());
                JsonObject reqJsonObj = input.getAsJsonObject();
                LocalDate examStartDate = null;
                LocalDate revisionStartDate = null;

                if (reqJsonObj.has("config")) {
                    JsonObject configJsonObj = reqJsonObj.getAsJsonObject("config");
                    examStartDate = LocalDate.parse(configJsonObj.get("exam-start-date").getAsString());
                    revisionStartDate = LocalDate.parse(configJsonObj.get("revision-start-date").getAsString());


                } else {
                    res.status(400);
                    return res.status();
                }

                if (reqJsonObj.has("subjects")) {
                    JsonArray subjectsJsonArray = reqJsonObj.getAsJsonArray("subjects");
                    for (final JsonElement element: subjectsJsonArray) {
                        JsonObject subject = element.getAsJsonObject();
                        System.out.println(subject.get("name"));
                    }
                } else {
                    res.status(400);
                    return res.status();
                }
            }
            res.status(200);
            return res.status();
        });
    }
}
