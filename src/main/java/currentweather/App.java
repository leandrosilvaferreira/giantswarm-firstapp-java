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

public class App {

    public static void main(String[] args) {
        get("/", (req, res) -> {
            return "Live weather: " + getCurrentWeather();
        });
    }

    private static String getCurrentWeather() throws IOException, JSONException {
      JSONObject json = readJsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q=Cologne");

			String temperature = json.getJSONObject("main").get("temp").toString();	
			String wind = json.getJSONObject("wind").get("speed").toString();	

    	String result = "The current temperature is " + temperature + " farenheit and the wind is " + wind + " mph.";
        //String result = json.toString();

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