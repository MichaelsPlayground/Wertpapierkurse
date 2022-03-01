package de.androidcrypto.wertpapierkurse;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // lemon.markets docs: https://data.lemon.markets/v1/docs

    EditText stockIsin, stockName, date;
    DatePickerDialog datePickerDialog;
    Button getStockName, getPrices, monthYearPicker, csvSave, csvLoad, stockMaintenance, downloadHistoricPrices;
    Button showPriceChart, maintainStocklist, lineBarChartTest, manageBookings;
    Button workingDayList;
    String API_URL = "https://data.lemon.markets/v1/";

    private LineChart lineChart;
    ArrayList<Entry> pricesClose = new ArrayList<>();

    List<String[]> csvList = new ArrayList<>();
    String[] csvHeaderPrices = {"date", "timestamp", "close"};

    // for selecting start and end date of download historical prices
    private int yearSelected;
    private int monthSelected;
    private int currentYear;
    private boolean shortMonthsPicker;
    private String startDateIso, endDateIso; // format yyyy-mm-dd

    // msci world IE00BJ0KDQ92
    // nasdaq IE00B53SZB19
    // s&p 500 IE00B5BMR087
    // emerging markets IE00BTJRMP35
    // msci world information IE00BM67HT60
    // commodities DE000A0H0728

    Intent stockMaintenanceIntent, downloadHistoricPricesIntent, showPriceChartIntent;
    Intent maintainStocklistIntent, lineBarChartTestIntent, manageBookingsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // be careful with these 2 lines
        // https://stackoverflow.com/a/9289190/8166854
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        lineChart = findViewById(R.id.activity_main_linechart);

        stockIsin = findViewById(R.id.etStockIsin);
        stockName = findViewById(R.id.etStockName);
        getStockName = findViewById(R.id.btnSearchIsin);
        monthYearPicker = findViewById(R.id.btnMonthYearPicker);
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

        stockMaintenanceIntent = new Intent(MainActivity.this, StockMaintenance.class);
        downloadHistoricPricesIntent = new Intent(MainActivity.this, DownloadHistoricPrices.class);
        showPriceChartIntent = new Intent(MainActivity.this, ShowPriceChart.class);
        maintainStocklistIntent = new Intent(MainActivity.this, MaintainStocklist.class);
        lineBarChartTestIntent = new Intent(MainActivity.this, LineBarChartTest.class);
        manageBookingsIntent = new Intent(MainActivity.this, ManageBookings.class);

        final MonthYearPickerDialogFragment[] dialogFragment = new MonthYearPickerDialogFragment[1];

        configureLineChart();

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
                pricesClose = new ArrayList<>();
                URL urlName = null;
                try {
                    Editable isin = stockIsin.getText();
                    System.out.println("für ISIN: " + isin);
                    // https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=IE00BJ0KDQ92&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1
                    //String urlString = API_URL + "ohlc/d1/?mic=XMUN&from=2022-01-01&decimals=true&epoch=false&sorting=asc&limit=28&page=1" + "?isin=" + isin;

                    // variable dates from startDateIso and endDateIso
                    String urlString ="https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=" + isin + "&from=" + startDateIso + "&to=" + endDateIso + "&decimals=true&epoch=false&sorting=asc&limit=25&page=1";

                    // fixed date
                    //String urlString ="https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=" + isin + "&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1";


                    //String urlString ="https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=IE00B53SZB19&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1";
                    //String urlString ="https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=IE00BJ0KDQ92&from=2022-01-01&to=2022-01-31&decimals=true&epoch=false&sorting=asc&limit=25&page=1";
                    System.out.println("urlString: " + urlString);
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

                    // for csv export
                    csvList.add(csvHeaderPrices);

                    parsePrices(dataName);

                    String path = getFilesDir().getAbsolutePath();
                    String csvFilename = isin + "_" +
                            yearSelected + "-" +
                            String.format("%02d", monthSelected) + ".txt";
                    String csvFilenameComplete = path + "/" + csvFilename;
                    System.out.println("csv file storing: " + csvFilenameComplete);
                    CsvWriterSimple writer = new CsvWriterSimple();
                    try {
                        writer.writeToCsvFile(csvList, new File(csvFilenameComplete));
                        System.out.println("csv file written");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Comparator<Entry> comparator = new Comparator<Entry>() {
                        @Override
                        public int compare(Entry o1, Entry o2) {
                            return Float.compare(o1.getX(), o2.getX());
                        }
                    };

                    pricesClose.sort(comparator);
                    //setLineChartData(pricesHigh, pricesLow, pricesClose);
                    setLineChartData(pricesClose);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e);
                    stockName.setText("Fehler, ISIN nicht gefunden");
                }
            }
        });

        monthYearPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                currentYear = calendar.get(Calendar.YEAR);
                yearSelected = currentYear;
                monthSelected = calendar.get(Calendar.MONTH);

                shortMonthsPicker = true;
                //currentYear = Year.now().getValue();
                displayMonthYearPickerDialogFragment(true, false);
            }
        });

        csvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable isin = stockIsin.getText();
                String path = getFilesDir().getAbsolutePath();
                String csvFilename = isin + "_" +
                        yearSelected + "-" +
                        String.format("%02d", monthSelected) + ".txt";
                String csvFilenameComplete = path + "/" + csvFilename;
                System.out.println("file storing: " + csvFilenameComplete);
                List<String[]> list = new ArrayList<>();
                String[] header = {"date", "close"};
                list.add(header);

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


    private MonthYearPickerDialogFragment createDialog(boolean customTitle) {
        return MonthYearPickerDialogFragment
                .getInstance(monthSelected,
                        yearSelected,
                        customTitle ? getString(R.string.custom_title).toUpperCase() : null,
                        shortMonthsPicker ? MonthFormat.SHORT : MonthFormat.LONG);
    }

    private MonthYearPickerDialogFragment createDialogWithRanges(boolean customTitle) {
        final int minYear = 2010;
        final int maxYear = currentYear;
        final int maxMoth = 11;
        final int minMoth = 0;
        final int minDay = 1;
        final int maxDay = 31;
        long minDate;
        long maxDate;

        Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set(minYear, minMoth, minDay);
        minDate = calendar.getTimeInMillis();

        calendar.clear();
        calendar.set(maxYear, maxMoth, maxDay);
        maxDate = calendar.getTimeInMillis();

        return MonthYearPickerDialogFragment
                .getInstance(monthSelected,
                        yearSelected,
                        minDate,
                        maxDate,
                        customTitle ? getString(R.string.custom_title).toUpperCase() : null,
                        shortMonthsPicker ? MonthFormat.SHORT : MonthFormat.LONG);
    }

    private void displayMonthYearPickerDialogFragment(boolean withRanges,
                                                      boolean customTitle) {
        MonthYearPickerDialogFragment dialogFragment = withRanges ?
                createDialogWithRanges(customTitle) :
                createDialog(customTitle);

        dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                monthSelected = monthOfYear + 1;
                yearSelected = year;
                // get last date of month
                LocalDate startDate = LocalDate.of(yearSelected, monthSelected, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                startDateIso = String.valueOf(startDate);
                endDateIso = String.valueOf(endDate);
                System.out.println("Range von " + startDate + " bis " + endDate);
                EditText range = (EditText) findViewById(R.id.etStartDate);
                range.setText(startDateIso + " bis " + endDateIso);
                //updateViews();
            }
        });

        dialogFragment.show(getSupportFragmentManager(), null);
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

    private void configureLineChart() {
        Description desc = new Description();
        desc.setText("Stock Price History");
        desc.setTextSize(20);
        lineChart.setDescription(desc);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });
    }

    private void setLineChartData(ArrayList<Entry> pricesClose) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
/*
        if (highCheckBox.isChecked()) {
            LineDataSet highLineDataSet = new LineDataSet(pricesHigh, stockTickerTextInputLayout.getEditText().getText().toString() + " Price (High)");
            highLineDataSet.setDrawCircles(true);
            highLineDataSet.setCircleRadius(4);
            highLineDataSet.setDrawValues(false);
            highLineDataSet.setLineWidth(3);
            highLineDataSet.setColor(Color.GREEN);
            highLineDataSet.setCircleColor(Color.GREEN);
            dataSets.add(highLineDataSet);
        }

        if (lowCheckBox.isChecked()) {
            LineDataSet lowLineDataSet = new LineDataSet(pricesLow, stockTickerTextInputLayout.getEditText().getText().toString() + " Price (Low)");
            lowLineDataSet.setDrawCircles(true);
            lowLineDataSet.setCircleRadius(4);
            lowLineDataSet.setDrawValues(false);
            lowLineDataSet.setLineWidth(3);
            lowLineDataSet.setColor(Color.RED);
            lowLineDataSet.setCircleColor(Color.RED);
            dataSets.add(lowLineDataSet);
        }
*/
        //if (closeCheckBox.isChecked()) {
            //LineDataSet closeLineDataSet = new LineDataSet(pricesClose, stockTickerTextInputLayout.getEditText().getText().toString() + " Price (Close)");
        LineDataSet closeLineDataSet = new LineDataSet(pricesClose, "ISIN " + " Price (Close)");
            //closeLineDataSet.setDrawCircles(true);
            closeLineDataSet.setDrawCircles(false);
            closeLineDataSet.setCircleRadius(4);
            closeLineDataSet.setDrawValues(false);
            closeLineDataSet.setLineWidth(3);
            closeLineDataSet.setColor(Color.rgb(255, 165, 0));
            closeLineDataSet.setCircleColor(Color.rgb(255, 165, 0));
            dataSets.add(closeLineDataSet);
        //}

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();
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