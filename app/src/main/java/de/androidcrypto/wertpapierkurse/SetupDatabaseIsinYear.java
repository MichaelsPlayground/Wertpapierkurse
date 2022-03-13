package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.androidcrypto.wertpapierkurse.models.BookingModel;

public class SetupDatabaseIsinYear extends AppCompatActivity {

    Button isinDelete, yearDelete, generateDataset;
    EditText stockIsin, entryYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_database_isin_year);

        isinDelete = findViewById(R.id.btnSDIYIsinDelete);
        yearDelete = findViewById(R.id.btnSDIYYearDelete);
        generateDataset = findViewById(R.id.btnSDIYGenerate);
        stockIsin = findViewById(R.id.etSDIYStockIsin);
        entryYear = findViewById(R.id.etSDIYYear);

        ArrayList<BookingModel> bookingModelArrayList = new ArrayList<>();

        isinDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockIsin.setText("");
            }
        });

        yearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearDelete.setText("");
            }
        });

        generateDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** generate new datasets in the database ***");
                Editable isin = stockIsin.getText();
                Editable year = entryYear.getText();
                System.out.println("for ISIN: " + isin.toString() + " for year: " + year);

                ArrayList<String> daysInYearWithoutWeenends = getListOfDaysWithoutWeekends(year.toString());
                System.out.println("arraylist generated with entries: " + daysInYearWithoutWeenends.size());

                // now store each date
                for (int i = 0; i < daysInYearWithoutWeenends.size(); i++) {
                    String date = daysInYearWithoutWeenends.get(i);
                    BookingModel bookingModel = new BookingModel(date, "", year.toString(), isin.toString(), "", "", "", "", "", "", "", "", "", true);
                    bookingModelArrayList.add(bookingModel);
                }

                System.out.println("datasets created:" + bookingModelArrayList.size());

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
}