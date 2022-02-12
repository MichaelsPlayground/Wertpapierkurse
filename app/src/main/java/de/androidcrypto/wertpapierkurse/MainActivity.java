package de.androidcrypto.wertpapierkurse;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    // lemon.markets docs: https://data.lemon.markets/v1/docs

    EditText stockIsin, stockName, date;
    DatePickerDialog datePickerDialog;
    Button getStockName, getPrices;
    String API_URL = "https://data.lemon.markets/v1/";

    //ArrayList<Entry> pricesClose = new ArrayList<>();

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

        getPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** get prices ***");
                URL urlName = null;
                try {
                    Editable isin = stockIsin.getText();
                    System.out.println("für ISIN: " + isin);
                    // https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=IE00BJ0KDQ92&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1
                    //String urlString = API_URL + "ohlc/d1/?mic=XMUN&from=2022-01-01&decimals=true&epoch=false&sorting=asc&limit=25&page=1" + "?isin=" + isin;
                    String urlString ="https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=IE00BJ0KDQ92&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1";
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
                    parsePrices(dataName);

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

    // uses GSON
    // import com.google.gson.JsonArray;
    // import com.google.gson.JsonObject;
    // import com.google.gson.JsonParser;
    // https://devqa.io/how-to-parse-json-in-java/
    public void parsePrices(String json) {
        // JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        // https://stackoverflow.com/questions/60771386/jsonparser-is-deprecated
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        //String pageName = jsonObject.getAsJsonObject("pageInfo").get("pageName").getAsString();
        //System.out.println(pageName);

        JsonArray arr = jsonObject.getAsJsonArray("results");
        for (int i = 0; i < arr.size(); i++) {
            String date = arr.get(i).getAsJsonObject().get("t").getAsString();
            String close = arr.get(i).getAsJsonObject().get("c").getAsString();
            // convert unix timestamp to date
            //long dateL = Long.parseLong(date);
            //LocalDateTime dateTimeLocal = LocalDateTime.ofInstant(Instant.ofEpochSecond(dateL), TimeZone.getDefault().toZoneId());
            System.out.println("date: " + date +
                    //      " dateLocal: " + dateTimeLocal +
                    " close: " + close);
            //Float dateFloat = Float.parseFloat(date);
            //Float priceCloseFloat = Float.parseFloat(close);
            //if (priceCloseFloat != 0) {pricesClose.add(new Entry(dateFloat, priceCloseFloat));}
            //stockName.setText(post_id);
        }
    }
}

/*
https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=IE00BJ0KDQ92&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1

{
  "results": [
    {
      "isin": "IE00BJ0KDQ92",
      "o": 86.534,
      "h": 87.06,
      "l": 86.248,
      "c": 87.05,
      "t": "2022-01-03T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 87.308,
      "h": 87.526,
      "l": 86.746,
      "c": 87.046,
      "t": "2022-01-04T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 87.008,
      "h": 87.046,
      "l": 85.604,
      "c": 85.604,
      "t": "2022-01-05T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 85.298,
      "h": 85.876,
      "l": 84.76,
      "c": 85.376,
      "t": "2022-01-06T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 85.514,
      "h": 85.514,
      "l": 84.494,
      "c": 84.87,
      "t": "2022-01-07T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 85.128,
      "h": 85.242,
      "l": 83.196,
      "c": 84.504,
      "t": "2022-01-10T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 84.842,
      "h": 85.28,
      "l": 84.254,
      "c": 85.21,
      "t": "2022-01-11T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 85.478,
      "h": 85.58,
      "l": 84.842,
      "c": 85.18,
      "t": "2022-01-12T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 84.986,
      "h": 85.086,
      "l": 83.758,
      "c": 83.812,
      "t": "2022-01-13T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 83.972,
      "h": 84.186,
      "l": 83.348,
      "c": 84.05,
      "t": "2022-01-14T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 84.236,
      "h": 84.47,
      "l": 83.988,
      "c": 84.384,
      "t": "2022-01-17T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 84.034,
      "h": 84.08,
      "l": 83.242,
      "c": 83.458,
      "t": "2022-01-18T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 82.844,
      "h": 83.774,
      "l": 82.486,
      "c": 82.486,
      "t": "2022-01-19T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 83.206,
      "h": 83.846,
      "l": 82.186,
      "c": 82.186,
      "t": "2022-01-20T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 81.938,
      "h": 82.112,
      "l": 80.376,
      "c": 80.472,
      "t": "2022-01-21T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 80.854,
      "h": 81.188,
      "l": 77.254,
      "c": 80.59,
      "t": "2022-01-24T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 79.8,
      "h": 80.81,
      "l": 78.842,
      "c": 80.03,
      "t": "2022-01-25T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 79.992,
      "h": 81.484,
      "l": 79.392,
      "c": 80.32,
      "t": "2022-01-26T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 79.364,
      "h": 81.782,
      "l": 79.298,
      "c": 80.406,
      "t": "2022-01-27T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 80.682,
      "h": 81.502,
      "l": 79.196,
      "c": 81.502,
      "t": "2022-01-28T00:00:00.000+00:00",
      "mic": "XMUN"
    },
    {
      "isin": "IE00BJ0KDQ92",
      "o": 82.044,
      "h": 82.538,
      "l": 81.198,
      "c": 82.538,
      "t": "2022-01-31T00:00:00.000+00:00",
      "mic": "XMUN"
    }
  ],
  "previous": null,
  "next": null,
  "total": 21,
  "page": 1,
  "pages": 1
}
 */