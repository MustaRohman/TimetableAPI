import static spark.Spark.*;

/**
 * Created by mustarohman on 25/01/2017.
 */

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World");
        get("/gasbag", (req, res) -> "Tahmidul is the biggest gasbag");
        get("/snake", (req, res) -> {
            return "Yezen is the biggest gasbag";
        });


    }
}
