package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

public class StockMovement extends AppCompatActivity {

    Button chooseDate;
    Intent simpleDayPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_movement);

        chooseDate = findViewById(R.id.btnSMChooseDate);
        String choosenDay = ""; // filled by intent return
        String choosenMonth = ""; // filled by intent return
        String choosenYear = ""; // filled by intent return

        simpleDayPicker = new Intent(StockMovement.this, SimpleDayPicker.class);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String day = "";
            String month = "";
            String year = "";
            day = (String) getIntent().getSerializableExtra("selectedDay"); //Obtaining data
            System.out.println("*** we are in StockMovement activity ***");
            if (day != null) {
                choosenDay = day;
                System.out.println("day: " + day);
            }
            month = (String) getIntent().getSerializableExtra("selectedMonth"); //Obtaining data
            if (month != null) {
                choosenMonth = month;
                System.out.println("month: " + month);
                // todo do what has todo when file is selected
                //selectedFile.setText(choosenFolder + " : " + choosenFile);
                System.out.println("choosenFolder: " + choosenFolder + " choosenFile: " + choosenFile);
                //loadCsvFile(choosenFolder, baseSubfolder, choosenFile);
                // load the datasets
                System.out.println("*** load datasets from file ***");
                Editable year = entryYear.getText();
                // todo hardcoded filename
                stockMovementsFilename = "movements";
                loadBookingMovementsDatasets(year.toString(), stockMovementsFilename);

                priceModelArrayList.clear();
                System.out.println("start FileAccess.loadHistoricPrices");
                priceModelArrayList = FileAccess.loadHistoricPrices(getBaseContext(), choosenFolder, choosenFile);
                int priceModelArrayListSize = priceModelArrayList.size();
                System.out.println("priceModelArrayListSize: " + priceModelArrayListSize);
                System.out.println("bookingModelArrayListSize: " + bookingModelArrayList.size());
                if (priceModelArrayListSize == 0) {
                    System.out.println("ERROR: kein Datensatz geladen");
                    return;
                }
                // todo get isin from choosenFile
                String[] parts = choosenFile.split("_");
                //String isin = "IE00BJ0KDQ92";
                String isin = parts[0];
                System.out.println("choosenFile: " + choosenFile + " isin: " + isin);

                for (int i = 0; i < priceModelArrayListSize; i++) {
                    String date = priceModelArrayList.get(i).getDate();
                    String closePrice = priceModelArrayList.get(i).getClosePrice();
                    int foundPosition = searchInBookingModelArrayList(date, isin);
                    if (foundPosition >= 0) {
                        // position found, set price
                        bookingModelArrayList.get(foundPosition).setClosePrice(Float.parseFloat(closePrice));
                    }
                }
                // todo save the datalist
            }
        }

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(simpleDayPicker);
            }
        });

    }
}