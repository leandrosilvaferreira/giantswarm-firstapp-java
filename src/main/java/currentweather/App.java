package currentweather;

import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

public class App {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            return "Live weather: " + getCurrentWeather();
        });
    }

    public static final String OpenWeatherMapAPIKey = "182564eaf55f709a58a13c40086fb5bb";

    private static String getCurrentWeather() throws IOException, JSONException {

      String addr = "redis";
      int port = 6379;

      // get the cached weather if available
      Jedis jedis = new Jedis(addr,port);
      jedis.connect();
      String result = jedis.get("weather");

      // if weather was not cached then make an API call and cache the result
      if (result == null) {
        System.out.println("Querying live weather data");
        JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q=Cologne,DE&appid=" + OpenWeatherMapAPIKey);

        Double temperature = (Double) json.getJSONObject("main").get("temp") - 273.0;
        int temp = Integer.valueOf(temperature.intValue());
        Double wind = (Double) json.getJSONObject("wind").get("speed") * 3.6;
        NumberFormat formatter = new DecimalFormat("#0.0");

        result = "The current temperature " + temp + " degrees and the wind is " + formatter.format(wind) + " km/h.";

        jedis.setex("weather", 60, result);
      } else {
        System.out.println("Using cached data");
      }

      return result;
    }

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }
}
