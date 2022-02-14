package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StockMaintenance extends AppCompatActivity {

    final String stockListFileName = "stocks.txt";
    List<String[]> csvStockList = new ArrayList<>();
    String[] csvStockHeader = {"isin", "name"};

    EditText stockIsin, stockName;

    Button getStockName, addStock, listStocks, csvSave, csvLoad;
    String API_URL = "https://data.lemon.markets/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_maintenance);

        stockIsin = findViewById(R.id.etIsin);
        stockName = findViewById(R.id.etStockName);
        getStockName = findViewById(R.id.btnSearchIsin);
        addStock = findViewById(R.id.btnAddStock);
        listStocks = findViewById(R.id.btnListStocks);



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

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // todo wichtig: alte liste vorher einlesen
                // todo check das die felder isin und wertpapiername gefüllt sind

                Editable isin = stockIsin.getText();
                Editable isinStockName = stockName.getText();
                csvStockList.add(csvStockHeader);
                String[] csvRecord = {String.valueOf(isin), String.valueOf(isinStockName)};
                csvStockList.add(csvRecord);

                String path = getFilesDir().getAbsolutePath();
                String csvFilenameComplete = path + "/" + stockListFileName;
                System.out.println("csv file storing: " + csvFilenameComplete);
                CsvWriterSimple writer = new CsvWriterSimple();
                try {
                    writer.writeToCsvFile(csvStockList, new File(csvFilenameComplete));
                    System.out.println("csv file written");
                } catch (IOException e) {
                    e.printStackTrace();
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