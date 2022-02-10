package de.androidcrypto.wertpapierkurse;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText stockIsin, stockName, date;
    DatePickerDialog datePickerDialog;
    Button getStockName, getPrices;
    String API_URL = "https://data.lemon.markets/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // be careful with these 2 lines
        // https://stackoverflow.com/a/9289190/8166854
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        stockIsin = findViewById(R.id.etIsin);
        stockName = findViewById(R.id.etStockName);
        getStockName = findViewById(R.id.btnSearchIsin);
        getPrices = findViewById(R.id.btnGetPrices);

        // initiate the date picker and a button
        date = (EditText) findViewById(R.id.etStartDate);
        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(year + "." + (monthOfYear+1) + "." + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        getStockName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** get name ***");

                URL urlName = null;
                try {
                    Editable isin = stockIsin.getText();
                    System.out.println("für ISIN: " + isin);
                    String urlString = API_URL + "instruments/?isin=" + isin + "&mic=XMUN";
                    urlName = new URL(urlString);
                    //urlName = new URL("https://data.lemon.markets/v1/instruments/?isin=IE00BJ0KDQ92&mic=XMUN");
                    HttpURLConnection httpName = (HttpURLConnection) urlName.openConnection();
                    httpName.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJsZW1vbi5tYXJrZXRzIiwiaXNzIjoibGVtb24ubWFya2V0cyIsInN1YiI6InVzcl9xeURNZFdXUlJDd1JQOGhHME1HZkxscDZTZkZNa3lYenNUIiwiZXhwIjoxNjc1OTg0Njg0LCJpYXQiOjE2NDQ0NDg2ODQsImp0aSI6ImFwa19xeURNZFhYR0dENFFjU0psVm04S1k1Ump4Y25GbnBHcjRrIiwibW9kZSI6InBhcGVyIn0.Li0sacTPoJHdFiSp-yNQ_lPUeDFgR15V1_VHPZGZel0");
                    System.out.println(httpName.getResponseCode() + " ResponseMessage: " + httpName.getResponseMessage());

                    BufferedReader brName = new BufferedReader(new InputStreamReader((httpName.getInputStream())));
                    StringBuilder sbName = new StringBuilder();
                    String outputName;
                    while ((outputName = brName.readLine()) != null) {
                        sbName.append(outputName);
                    }
                    String dataName = sbName.toString();
                    System.out.println("Content:\n" + dataName);
                    parseName(dataName, stockName);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e);
                    stockName.setText("Fehler, ISIN nicht gefunden");
                }


            }
        });

    }

    // uses GSON
    // import com.google.gson.JsonArray;
    // import com.google.gson.JsonObject;
    // import com.google.gson.JsonParser;
    // https://devqa.io/how-to-parse-json-in-java/
    public static void parseName(String json, EditText stockName) {
        // JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        // https://stackoverflow.com/questions/60771386/jsonparser-is-deprecated
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        //String pageName = jsonObject.getAsJsonObject("pageInfo").get("pageName").getAsString();
        //System.out.println(pageName);

        JsonArray arr = jsonObject.getAsJsonArray("results");
        for (int i = 0; i < arr.size(); i++) {
            String post_id = arr.get(i).getAsJsonObject().get("name").getAsString();
            System.out.println("Name: " + post_id);
            stockName.setText(post_id);
        }

    }

}