package currentweather;

import static spark.Spark.get;

public class App {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            return "hello from sparkjava.com " + getCurrentWeather();
        });
    }

    private static String getCurrentWeather() {
        String result = "";

        return result;
    }
}