package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class StockMovement extends AppCompatActivity {

    Button chooseDate;
    EditText choosenDate;
    Intent simpleDayPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_movement);

        chooseDate = findViewById(R.id.btnSMChooseDate);
        choosenDate = findViewById(R.id.etSMDate);

        simpleDayPicker = new Intent(StockMovement.this, SimpleDayPicker.class);

        String choosenDay = ""; // filled by intent return
        String choosenMonth = ""; // filled by intent return
        String choosenYear = ""; // filled by intent return

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
            }

            // todo do what has todo when file is selected
            //selectedFile.setText(choosenFolder + " : " + choosenFile);
            String completeDate = year + "-" +
                    String.format(Locale.GERMANY, "%02d", Integer.valueOf(month)) + "-" +
                    String.format(Locale.GERMANY, "%02d", Integer.valueOf(day));
            System.out.println("choosenDate: " + completeDate);
            choosenDate.setText(completeDate);
            // todo get isin from choosenFile
            // todo save the datalist
        };

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(simpleDayPicker);
            }
        });

    }
}