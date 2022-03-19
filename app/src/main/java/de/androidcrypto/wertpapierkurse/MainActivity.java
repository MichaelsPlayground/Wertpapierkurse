package de.androidcrypto.wertpapierkurse;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.androidcrypto.wertpapierkurse.apis.YahooFinanceApiRequestV02;
import de.androidcrypto.wertpapierkurse.files.CsvWriterSimple;

public class MainActivity extends AppCompatActivity {
    // lemon.markets docs: https://data.lemon.markets/v1/docs

    Button getPrices, csvSave, csvLoad, stockMaintenance, downloadHistoricPrices;
    Button showPriceChart, maintainStocklist, lineBarChartTest, manageBookings;
    Button workingDayList, setupDatabaseIsinYear, setupModalIsinYear;
    Button stockMovement, yahooApi, storeYahooApiKey, loadYahooApiKey;
    String API_URL = "https://data.lemon.markets/v1/";

    String apiKeyFromSharedPreferences = "";

    ArrayList<Entry> pricesClose = new ArrayList<>();

    List<String[]> csvList = new ArrayList<>();
    String[] csvHeaderPrices = {"date", "timestamp", "close"};

    // msci world IE00BJ0KDQ92 XDWD.DE
    // nasdaq IE00B53SZB19
    // s&p 500 IE00B5BMR087 CSSPX.DE
    // emerging markets IE00BTJRMP35
    // msci world information IE00BM67HT60
    // commodities DE000A0H0728
    // Xtrackers S&P 500 Equal Weight UCITS ETF IE00BLNMYC90 XDEW.DE oder F

    Intent stockMaintenanceIntent, downloadHistoricPricesIntent, showPriceChartIntent;
    Intent maintainStocklistIntent, lineBarChartTestIntent, manageBookingsIntent;
    Intent setupDatabaseIsinYearIntent, setupModalIsinYearIntent;
    Intent stockMovementIntent, yahooApiIntent, storeYahooApiKeyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // be careful with these 2 lines
        // https://stackoverflow.com/a/9289190/8166854
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getPrices = findViewById(R.id.btnGetPrices);
        csvSave = findViewById(R.id.btnCsvSave);
        csvLoad = findViewById(R.id.btnCsvLoad);
        stockMaintenance = findViewById(R.id.btnStockMaintenance);
        downloadHistoricPrices = findViewById(R.id.btnDownloadHistoricPrices);
        showPriceChart = findViewById(R.id.btnShowPriceChart);
        maintainStocklist = findViewById(R.id.btnMaintainStockList);
        lineBarChartTest = findViewById(R.id.btnMLineBarChartTest);
        manageBookings = findViewById(R.id.btnManageBookings);
        workingDayList = findViewById(R.id.btnWorkingDayList);
        setupDatabaseIsinYear = findViewById(R.id.btnSetupDatabaseIsinYear);
        setupModalIsinYear = findViewById(R.id.btnSetupModalIsinYear);
        stockMovement = findViewById(R.id.btnStockMovement);
        yahooApi = findViewById(R.id.btnYahooApi);
        storeYahooApiKey = findViewById(R.id.btnStoreYahooApiKey);
        loadYahooApiKey = findViewById(R.id.btnLoadYahooApiKey);

        stockMaintenanceIntent = new Intent(MainActivity.this, StockMaintenance.class);
        downloadHistoricPricesIntent = new Intent(MainActivity.this, DownloadHistoricPrices.class);
        showPriceChartIntent = new Intent(MainActivity.this, ShowPriceChart.class);
        maintainStocklistIntent = new Intent(MainActivity.this, MaintainStocklist.class);
        lineBarChartTestIntent = new Intent(MainActivity.this, LineBarChartTest.class);
        manageBookingsIntent = new Intent(MainActivity.this, ManageBookings.class);
        setupDatabaseIsinYearIntent = new Intent(MainActivity.this, SetupDatabaseIsinYear.class);
        setupModalIsinYearIntent = new Intent(MainActivity.this, SetupModalIsinYear.class);
        stockMovementIntent = new Intent(MainActivity.this, StockMovement.class);
        yahooApiIntent = new Intent(MainActivity.this, YahooFinanceApiRequestV02.class);
        storeYahooApiKeyIntent = new Intent(MainActivity.this, StoreYahooApiKey.class);

        storeYahooApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(storeYahooApiKeyIntent);
            }
        });

        loadYahooApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String key = sharedPref.getString(getString(R.string.yahoo_api_key), "");
                System.out.println("key: " + key);
                //data.setText(key);
                apiKeyFromSharedPreferences = key;
            }
        });

        csvLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        stockMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(stockMaintenanceIntent);
            }
        });

        downloadHistoricPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(downloadHistoricPricesIntent);
            }
        });

        showPriceChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(showPriceChartIntent);
            }
        });

        maintainStocklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(maintainStocklistIntent);
            }
        });

        lineBarChartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(lineBarChartTestIntent);
            }
        });

        manageBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(manageBookingsIntent);
            }
        });

        workingDayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** WORKING DAY LIST 2022 ***");
                System.out.println("includes 01.01. and 31.12.");

/*
                String s = "2022-01-01";
                String e = "2022-12-31";
                LocalDate start = LocalDate.parse(s);
                LocalDate end = LocalDate.parse(e);
                List<LocalDate> totalDates = new ArrayList<>();
                List<LocalDate> totalDatesWorkdays = new ArrayList<>();
                while (!start.isAfter(end)) {
                    totalDates.add(start);
                    //totalDatesWorkdays.add(start);
                    //System.out.println("date: " + start);
                    start = start.plusDays(1);
                }
                System.out.println("** complete list **");
                System.out.println(Arrays.deepToString(totalDates.toArray()));

                //for (int counter = 0; counter < totalDates.size(); counter++) { // checks for all days
                totalDatesWorkdays.add(totalDates.get(0)); // 01.01.
                for (int counter = 1; counter < (totalDates.size() - 1); counter++) { // checks NOT for 01.01. and 31.12.
                    //System.out.println(totalDatesWorkdays.get(counter));
                    LocalDate date = totalDates.get(counter);
                    if (date.getDayOfWeek().getValue() != 6 & date.getDayOfWeek().getValue() != 7) {
                        totalDatesWorkdays.add(date);
                    }
                }
                totalDatesWorkdays.add(totalDates.get(totalDates.size()-1)); // 31.12.
*/
                String year = "2022"; // needs to be a four digit string
                ArrayList<String> daysInYearWithoutWeenends = getListOfDaysWithoutWeekends(year);

                System.out.println("** only workdays " + daysInYearWithoutWeenends.size());
                System.out.println(Arrays.deepToString(daysInYearWithoutWeenends.toArray()));
            }
        });

        setupDatabaseIsinYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(setupDatabaseIsinYearIntent);
            }
        });

        setupModalIsinYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(setupModalIsinYearIntent);
            }
        });

        stockMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(stockMovementIntent);
            }
        });

        yahooApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("Yahoo");
                    YahooFinanceApiRequestV02.main(v, apiKeyFromSharedPreferences);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //startActivity(yahooApiIntent);
            }
        });

    }

    // this function returns an arraylist of String with all days in a given year in format yyyy-mm-dd
    // excluded are the weekends = saturday and sunday BUT included are
    // 01.01. and 31.12. regardless if they are weekend days
    // this is to force that each table of days starts with 01.01.xxxx and ends with 31.12.xxxx
    private ArrayList<String> getListOfDaysWithoutWeekends(String year) {
        String s =  year + "-01-01"; // e.g. 2022-01-01
        String e = year + "-12-31"; // e.g. 2022-12-31
        LocalDate start = LocalDate.parse(s);
        LocalDate end = LocalDate.parse(e);
        List<LocalDate> totalDates = new ArrayList<>();
        ArrayList<String> totalDatesWorkdays = new ArrayList<>();
        while (!start.isAfter(end)) {
            totalDates.add(start);
            start = start.plusDays(1);
        }
        //System.out.println("** complete list **");
        //System.out.println(Arrays.deepToString(totalDates.toArray()));
        // now remove the weekends weekends
        //for (int counter = 0; counter < totalDates.size(); counter++) { // checks for all days
        totalDatesWorkdays.add(totalDates.get(0).toString()); // 01.01.
        for (int counter = 1; counter < (totalDates.size() - 1); counter++) { // checks NOT for 01.01. and 31.12.
            LocalDate date = totalDates.get(counter);
            if (date.getDayOfWeek().getValue() != 6 & date.getDayOfWeek().getValue() != 7) {
                totalDatesWorkdays.add(date.toString());
            }
        }
        totalDatesWorkdays.add(totalDates.get(totalDates.size()-1).toString()); // 31.12.
        return totalDatesWorkdays;
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date dateUnix = null;
            try {
                dateUnix = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long unixTime = (long) dateUnix.getTime()/1000;
            //System.out.println(unixTime );//<- prints 1352504418
            //Float dateFloat = Float.parseFloat(unixTime);
            float dateFloat = Float.valueOf(unixTime);
            float priceCloseFloat = Float.parseFloat(close);
            if (priceCloseFloat != 0) {
                pricesClose.add(new Entry(dateFloat, priceCloseFloat));
                String[] csvRecord = {date.substring(0, 10), String.valueOf(unixTime), close};
                System.out.println("CSV data stored " + date.substring(0, 10) + " " + close);
                csvList.add(csvRecord);
            }
            //stockName.setText(post_id);
        }
    }

    // uses GSON
    // import com.google.gson.JsonArray;
    // import com.google.gson.JsonObject;
    // import com.google.gson.JsonParser;
    // https://devqa.io/how-to-parse-json-in-java/
    public void parsePricesOrg(String json) {
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date dateUnix = null;
            try {
                dateUnix = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long unixTime = (long) dateUnix.getTime()/1000;
            //System.out.println(unixTime );//<- prints 1352504418
            //Float dateFloat = Float.parseFloat(unixTime);
            float dateFloat = Float.valueOf(unixTime);
            float priceCloseFloat = Float.parseFloat(close);
            if (priceCloseFloat != 0) {pricesClose.add(new Entry(dateFloat, priceCloseFloat));}
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