package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class StockMovement extends AppCompatActivity {

    Button chooseDate, selectIsin, saveBooking, clearAllFields, startMainActivity;
    EditText choosenDate, stockIsin, buyOrSell, numberShares, totalPurchaseCosts;
    Intent simpleDayPickerIntent, selectIsinIntent, startMainActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_movement);

        chooseDate = findViewById(R.id.btnSMChooseDate);
        selectIsin = findViewById(R.id.btnSMIsinSelect);
        saveBooking = findViewById(R.id.btnSMSave);
        clearAllFields = findViewById(R.id.btnSMClearAllFields);
        startMainActivity = findViewById(R.id.btnSMMainActivity);

        choosenDate = findViewById(R.id.etSMDate);
        stockIsin = findViewById(R.id.etSMStockIsin);
        buyOrSell = findViewById(R.id.etSMBuySell);
        numberShares = findViewById(R.id.etSMNumberShares);
        totalPurchaseCosts = findViewById(R.id.etSMTotalPurchaseCosts);

        simpleDayPickerIntent = new Intent(StockMovement.this, SimpleDayPicker.class);
        selectIsinIntent = new Intent(StockMovement.this, MaintainStocklist.class);
        startMainActivityIntent = new Intent(StockMovement.this, MainActivity.class);

        String choosenDay = "01"; // filled by intent return
        String choosenMonth = "01"; // filled by intent return
        String choosenYear = "2022"; // filled by intent return

        String selectedIsin = "ISIN"; // filled by intent return
        String choosenDateIntent = "2022-01-01";

        // buyOrSell allows only + or -, just one character
        buyOrSell.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

        // limit the number of decimals before and after decimal separator
        numberShares.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(20, 5)});
        totalPurchaseCosts.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(20, 2)});

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
            }
            year = (String) getIntent().getSerializableExtra("selectedYear"); //Obtaining data
            if (year != null) {
                choosenYear = year;
                System.out.println("year: " + year);
                //selectedFile.setText(choosenFolder + " : " + choosenFile);
                String completeDate = choosenYear + "-" +
                        String.format(Locale.GERMANY, "%02d", Integer.valueOf(choosenMonth)) + "-" +
                        String.format(Locale.GERMANY, "%02d", Integer.valueOf(choosenDay));
                System.out.println("choosenDate: " + completeDate);
                choosenDate.setText(completeDate);
            }


            String isinSelected = "";
            isinSelected = (String) getIntent().getSerializableExtra("selectedIsin");
            String dateChoosenIntent = "";
            dateChoosenIntent = (String) getIntent().getSerializableExtra("choosenDate");
            if (isinSelected != null) {
                selectedIsin = isinSelected;
                System.out.println("isinSelected: " + isinSelected);
                stockIsin.setText(selectedIsin);
            }
            if (dateChoosenIntent != null) {
                choosenDateIntent = dateChoosenIntent;
                System.out.println("choosenDateIntent: " + dateChoosenIntent);
                choosenDate.setText(choosenDateIntent);
            }
        };

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("selectedIsin", stockIsin.getText().toString());
                bundle.putString("returnToActivity", "StockMovement");
                simpleDayPickerIntent.putExtras(bundle);
                startActivity(simpleDayPickerIntent);
            }
        });

        selectIsin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("choosenDate", choosenDate.getText().toString());
                bundle.putString("returnToActivity", "StockMovement");
                selectIsinIntent.putExtras(bundle);
                startActivity(selectIsinIntent);
            }
        });

        saveBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = choosenDate.getText().toString();
                String isin = stockIsin.getText().toString();
                String plusMinus = buyOrSell.getText().toString();
                String numberOfShares = numberShares.getText().toString();
                String totalCostsOfPurchase = totalPurchaseCosts.getText().toString();
                // is it a buy + or sell -
                float multiplicator = 1;
                if (plusMinus.equals("-")) {
                    multiplicator = -1;
                }
                try {
                    float numberOfSharesFloat = Float.valueOf(numberOfShares) * multiplicator;
                    float totalCostsOfPurchaseFloat = Float.valueOf(totalCostsOfPurchase) * multiplicator;
                    System.out.println("dataset saved");
                    System.out.println("numberOfSharesFloat: " + numberOfSharesFloat);
                    System.out.println("totalCostsOfPurchaseFloat: " + totalCostsOfPurchaseFloat);

                    // todo save dataset
                    // todo hardcoded year 2022
                    // load, append, save
                    FileAccess.loadBookingMovementsDatasets(v.getContext(), "2022", Constants.STOCK_MOVEMENTS_FILENAME);
                    FileAccess.appendBookingToArrayList(date, isin, numberOfSharesFloat, totalCostsOfPurchaseFloat);
                    FileAccess.saveBookingMovementsDatasets(v.getContext(), "2022", Constants.STOCK_MOVEMENTS_FILENAME);
                    // clear the form
                    clearAllFieldsForm();
                } catch (NumberFormatException e) {
                    System.out.println("Error, Exception: " + e.toString());
                }
            }
        });

        clearAllFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFieldsForm();
            }
        });

        startMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startMainActivityIntent);
            }
        });
    }

    private void clearAllFieldsForm() {
        choosenDate.setText("");
        stockIsin.setText("");
        buyOrSell.setText("");
        numberShares.setText("");
        totalPurchaseCosts.setText("");
    }
}