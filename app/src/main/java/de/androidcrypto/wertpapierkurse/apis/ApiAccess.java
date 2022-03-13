package de.androidcrypto.wertpapierkurse.apis;

import android.os.StrictMode;
import android.text.Editable;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiAccess {

    static String API_URL = "https://data.lemon.markets/v1/";
    // todo store API_TOKEN in encrypted shared data
    static String API_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJsZW1vbi5tYXJrZXRzIiwiaXNzIjoibGVtb24ubWFya2V0cyIsInN1YiI6InVzcl9xeURNZFdXUlJDd1JQOGhHME1HZkxscDZTZkZNa3lYenNUIiwiZXhwIjoxNjc1OTg0Njg0LCJpYXQiOjE2NDQ0NDg2ODQsImp0aSI6ImFwa19xeURNZFhYR0dENFFjU0psVm04S1k1Ump4Y25GbnBHcjRrIiwibW9kZSI6InBhcGVyIn0.Li0sacTPoJHdFiSp-yNQ_lPUeDFgR15V1_VHPZGZel0";

    public static String getStockName(String isin) {
        // be careful with these 2 lines
        // https://stackoverflow.com/a/9289190/8166854
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        System.out.println("*** get name ***");
        String stockName = "";
        URL urlName = null;
        try {
            //Editable isin = stockIsin.getText();
            System.out.println("für ISIN: " + isin);
            String urlString = API_URL + "instruments/?isin=" + isin + "&mic=XMUN";
            urlName = new URL(urlString);
            //urlName = new URL("https://data.lemon.markets/v1/instruments/?isin=IE00BJ0KDQ92&mic=XMUN");
            HttpURLConnection httpName = (HttpURLConnection) urlName.openConnection();
            //httpName.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJsZW1vbi5tYXJrZXRzIiwiaXNzIjoibGVtb24ubWFya2V0cyIsInN1YiI6InVzcl9xeURNZFdXUlJDd1JQOGhHME1HZkxscDZTZkZNa3lYenNUIiwiZXhwIjoxNjc1OTg0Njg0LCJpYXQiOjE2NDQ0NDg2ODQsImp0aSI6ImFwa19xeURNZFhYR0dENFFjU0psVm04S1k1Ump4Y25GbnBHcjRrIiwibW9kZSI6InBhcGVyIn0.Li0sacTPoJHdFiSp-yNQ_lPUeDFgR15V1_VHPZGZel0");
            httpName.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            System.out.println(httpName.getResponseCode() + " ResponseMessage: " + httpName.getResponseMessage());

            BufferedReader brName = new BufferedReader(new InputStreamReader((httpName.getInputStream())));
            StringBuilder sbName = new StringBuilder();
            String outputName;
            while ((outputName = brName.readLine()) != null) {
                sbName.append(outputName);
            }
            String dataName = sbName.toString();
            System.out.println("Content:\n" + dataName);
            stockName = parseName(dataName);
            return stockName;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: " + e);
            return "Fehler, ISIN nicht gefunden";
        }

    }

    // uses GSON
    // import com.google.gson.JsonArray;
    // import com.google.gson.JsonObject;
    // import com.google.gson.JsonParser;
    // https://devqa.io/how-to-parse-json-in-java/
    public static String parseName(String json) {
        // JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        // https://stackoverflow.com/questions/60771386/jsonparser-is-deprecated
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        //String pageName = jsonObject.getAsJsonObject("pageInfo").get("pageName").getAsString();
        //System.out.println(pageName);

        JsonArray arr = jsonObject.getAsJsonArray("results");
        for (int i = 0; i < arr.size(); i++) {
            String post_id = arr.get(i).getAsJsonObject().get("name").getAsString();
            System.out.println("Name: " + post_id);
            return post_id;
        }
        return "";
    }
}
