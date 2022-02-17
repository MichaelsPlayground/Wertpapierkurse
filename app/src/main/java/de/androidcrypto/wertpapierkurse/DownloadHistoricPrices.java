package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadHistoricPrices extends AppCompatActivity {

    final String stockListFileName = "stocks.txt";
    List<String[]> csvStockList = new ArrayList<>();
    List<String[]> result = null; // filled by loadStocksList

    Button listStocks;
    EditText stocksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_historic_prices);

        listStocks = findViewById(R.id.btnDlListStocks);
        stocksList = findViewById(R.id.etDlStocksList);




        listStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int records = 0;
                records = loadStocksList();
                System.out.println("listStocks records: " + records);
            }
        });




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
                records = listIndex;
                if (records > 0){
                    stocksList.setText(completeContent);
                } else {
                    stocksList.setText(("*** FEHLER *** noch keine Wertpapierliste erfasst"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            stocksList.setText(("*** FEHLER *** noch keine Wertpapierliste erfasst"));
        }

        return records;
    }
}