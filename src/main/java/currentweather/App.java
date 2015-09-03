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
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.*;

public class App {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            return "Live weather: " + getCurrentWeather();
        });
    }

    private static String getCurrentWeather() throws IOException, JSONException {
      String addr = "mongo";
      if (System.getenv("MONGO_PORT_6379_TCP_ADDR") != null) {
        addr = System.getenv("MONGO_PORT_6379_TCP_ADDR");
      }

      int port = 27017;
      if (System.getenv("MONGO_PORT_6379_TCP_PORT") != null) {
        port = Integer.parseInt(System.getenv("MONGO_PORT_6379_TCP_PORT"));
      }
      MongoClient mongo = new MongoClient(addr, port);
      DB db = mongo.getDB("currentweather");
      DBCollection col = db.getCollection("data");
      //60 is the minimum
      BasicDBObject index = new BasicDBObject("createdAt", 1);
      BasicDBObject options = new BasicDBObject("expireAfterSeconds", 60);
      col.createIndex(index, options);

      // get the cached weather if available
      String result = getCachedWeather(col);

      // if weather was not cached then make an API call and cache the result
      if (result == null) {
        System.out.println("Querying live weather data");
        JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q=Cologne,DE");

        Double temperature = (Double) json.getJSONObject("main").get("temp") - 273;
        int temp = Integer.valueOf(temperature.intValue());
        Double wind = (Double) json.getJSONObject("wind").get("speed") * 3.6;
        NumberFormat formatter = new DecimalFormat("#0.0");
        result = "The current temperature " + temp + " degrees and the wind is " + formatter.format(wind) + " km/h.";
        
        BasicDBObject document = new BasicDBObject();
        document.put("weather", result);
        document.put("createdAt", new Date());
        col.insert(document);
      } else {
        System.out.println("Using cached data");
      }

      return result;
    }

  private static String getCachedWeather(DBCollection col) {
      DBObject result = col.findOne();
      if(result == null) {
        return null;
      } else {
        return (String)result.get("weather");
      }
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