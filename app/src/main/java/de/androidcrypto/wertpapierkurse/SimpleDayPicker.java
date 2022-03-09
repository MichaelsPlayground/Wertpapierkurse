package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

public class SimpleDayPicker extends AppCompatActivity {

    DatePicker simpleDatePicker;
    Intent stockMovementIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_day_picker);

        simpleDatePicker = findViewById(R.id.sdp);

        stockMovementIntent = new Intent(SimpleDayPicker.this, StockMovement.class);

        simpleDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                System.out.println("*** simple date picker");
                int dayChoosen = simpleDatePicker.getDayOfMonth();
                int monthChoosen = simpleDatePicker.getMonth() + 1; // starts with 0
                int yearChoosen = simpleDatePicker.getYear();
                // todo check for weekends - not allowed !!
                System.out.println("choosen date d: " + dayChoosen + " m: " + monthChoosen + " y: " + yearChoosen);
                Bundle bundle = new Bundle();
                bundle.putString("selectedYear", String.valueOf(yearChoosen));
                bundle.putString("selectedMonth", String.valueOf(monthChoosen));
                bundle.putString("selectedDay", String.valueOf(dayChoosen));
                stockMovementIntent.putExtras(bundle);
                startActivity(stockMovementIntent);
            }
        });

    }
}