package de.androidcrypto.wertpapierkurse;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Arrays;
import java.util.List;

public class StockMaintenance_2022_02_16 extends AppCompatActivity {

    final String stockListFileName = "stocks.txt";
    List<String[]> csvStockList = new ArrayList<>();
    String[] csvStockHeader = {"isin", "name"};

    List<String[]> result = null; // filled by loadStocksList

    EditText stockIsin, stockName, stocksList;

    Button isinLoeschen, getStockName, addStock, listStocks, deleteStock;
    String API_URL = "https://data.lemon.markets/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_maintenance);

        stockIsin = findViewById(R.id.etStockIsin);
        stockName = findViewById(R.id.etStockName);
        stocksList = findViewById(R.id.etStocksList);
        isinLoeschen = findViewById(R.id.btnIsinDelete);
        getStockName = findViewById(R.id.btnSearchIsin);
        addStock = findViewById(R.id.btnAddStock);
        listStocks = findViewById(R.id.btnListStocks);

        // one time only set
        csvStockList.add(csvStockHeader);

        isinLoeschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockIsin.setText("");
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

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // todo wichtig: alte liste vorher einlesen
                // todo check das die felder isin und wertpapiername gefüllt sind

                Editable isin = stockIsin.getText();
                Editable isinStockName = stockName.getText();
                //csvStockList.add(csvStockHeader);
                String[] csvRecord = {String.valueOf(isin), String.valueOf(isinStockName)};

                // empty the existing stocksList
                csvStockList.clear();
                csvStockList.add(csvStockHeader);
                // check if stocks list file exists, if not create one with header
                stocksListExists();
                // now load the existing file
                int records = 0;
                records = loadStocksList();
                System.out.println("add to existing records: " + records);
                // add the new record
                System.out.println("csvStockList records before adding: " + csvStockList.size());
                csvStockList.add(csvRecord);
                System.out.println("csvStockList records after adding: " + csvStockList.size());
                // delete the old csv file
                stocksListDeleteFile();
                // store the new file with complete list in memory
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

        listStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int records = 0;
                records = loadStocksList();
                System.out.println("listStocks records: " + records);
                /*
                String path = getFilesDir().getAbsolutePath();
                String csvFilenameComplete = path + "/" + stockListFileName;
                System.out.println("file reading: " + csvFilenameComplete);
                // check if file exists before reading
                File csvReadingFile = new File(csvFilenameComplete);
                boolean csvReadingFileExists = csvReadingFile.exists();
                System.out.println("The file is existing: " + csvReadingFileExists);
                String completeContent = "";
                if (csvReadingFileExists) {
                    try {
                        CsvParserSimple obj = new CsvParserSimple();
                        List<String[]> result = null;
                        result = obj.readFile(csvReadingFile, 1);
                        int listIndex = 0;
                        for (String[] arrays : result) {
                            System.out.println("\nString[" + listIndex++ + "] : " + Arrays.toString(arrays));
                            completeContent = completeContent + "[nr " + listIndex + "] : " + Arrays.toString(arrays) + "\n";
                            completeContent = completeContent + "-----------------\n";
                            int index = 0;
                            for (String array : arrays) {
                                System.out.println(index++ + " : " + array);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
*/

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

    public void stocksListExists() {
        String path = getFilesDir().getAbsolutePath();
        String csvFilenameComplete = path + "/" + stockListFileName;
        System.out.println("file reading: " + csvFilenameComplete);
        // check if file exists before reading
        File csvReadingFile = new File(csvFilenameComplete);
        boolean csvReadingFileExists = csvReadingFile.exists();
        System.out.println("The file is existing: " + csvReadingFileExists);
        if (!csvReadingFileExists) {
            CsvWriterSimple writer = new CsvWriterSimple();
            try {
                writer.writeToCsvFile(csvStockList, new File(csvFilenameComplete));
                System.out.println("csv file written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stocksListDeleteFile() {
        String path = getFilesDir().getAbsolutePath();
        String csvFilenameComplete = path + "/" + stockListFileName;
        System.out.println("file reading: " + csvFilenameComplete);
        // check if file exists before reading
        File csvReadingFile = new File(csvFilenameComplete);
        boolean csvReadingFileExists = csvReadingFile.exists();
        System.out.println("The file is existing: " + csvReadingFileExists);
        if (csvReadingFileExists) {
            csvReadingFile.delete();
            System.out.println("csv file deleted");
        }
    }

    public int loadStocksList() {
        int records = 0;
        String path = getFilesDir().getAbsolutePath();
        String csvFilenameComplete = path + "/" + stockListFileName;
        System.out.println("file reading: " + csvFilenameComplete);
        // check if file exists before reading
        File csvReadingFile = new File(csvFilenameComplete);
        boolean csvReadingFileExists = csvReadingFile.exists();
        System.out.println("The file is existing: " + csvReadingFileExists);
        String completeContent = "";
        if (csvReadingFileExists) {
            try {
                CsvParserSimple obj = new CsvParserSimple();
                //List<String[]> result = null;
                result = obj.readFile(csvReadingFile, 1);
                int listIndex = 0;
                for (String[] arrays : result) {
                    System.out.println("\nString[" + listIndex++ + "] : " + Arrays.toString(arrays));
                    String listIndex2Digit = String.format("%02d", listIndex);
                    //completeContent = completeContent + "[nr " + listIndex2Digit + "] : " + Arrays.toString(arrays) + "\n";
                    completeContent = completeContent + listIndex2Digit + " " + Arrays.toString(arrays).replace("[", "").replaceAll("]", "") + "\n";
                    //completeContent = completeContent + "-----------------\n";

                    int index = 0;
                    String isin = "";
                    String isinStockName = "";
                    for (String array : arrays) {
                        System.out.println(index++ + " : " + array);
                        if (index == 1) {
                            isin = array;
                        }
                        if (index == 2) {
                            isinStockName = array;
                        }
                    }
                    String[] csvRecord = {String.valueOf(isin), String.valueOf(isinStockName)};
                    System.out.println("loadStocksList csvRecord vor adding: " + csvRecord);
                    csvStockList.add(csvRecord);
                    System.out.println("loadStocksList nach adding: " + csvStockList.size());
                }
                stocksList.setText(completeContent);
                records = listIndex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return records;
    }
}