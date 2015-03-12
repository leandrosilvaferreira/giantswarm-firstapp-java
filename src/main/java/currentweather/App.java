package currentweather;

import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

public class App {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            return "Live weather: " + getCurrentWeather();
        });
    }

    private static String getCurrentWeather() throws IOException, JSONException {
      
      String addr = "redis";
      if (System.getenv("REDIS_PORT_6379_TCP_ADDR") != null) {
        addr = System.getenv("REDIS_PORT_6379_TCP_ADDR");
      }

      int port = 6379;
      if (System.getenv("REDIS_PORT_6379_TCP_PORT") != null) {
        port = Integer.parseInt(System.getenv("REDIS_PORT_6379_TCP_PORT"));
      }

      Jedis jedis = new Jedis(addr,port);
      jedis.connect();
      String result = jedis.get("weather");

      if (result == null) {
        JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q=Cologne");

        String temperature = json.getJSONObject("main").get("temp").toString(); 
        String wind = json.getJSONObject("wind").get("speed").toString(); 

        result = "The current temperature is " + temperature + " farenheit and the wind is " + wind + " mph.";

        jedis.set("weather", result);
        jedis.expire("weather", 60);
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