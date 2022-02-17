package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DownloadHistoricPrices extends AppCompatActivity {

    final String stockListFileName = "stocks.txt";
    List<String[]> csvStockList = new ArrayList<>();
    List<String[]> result = null; // filled by loadStocksList

    // for selecting start and end date of download historical prices
    private int yearSelected;
    private int monthSelected;
    private int currentYear;
    private boolean shortMonthsPicker;
    private String startDateIso, endDateIso; // format yyyy-mm-dd

    Button listStocks, monthYearPicker;
    EditText stocksList, selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_historic_prices);

        listStocks = findViewById(R.id.btnDlListStocks);
        monthYearPicker = findViewById(R.id.btnDlMonthYearPicker);

        stocksList = findViewById(R.id.etDlStocksList);
        selectedDate = findViewById(R.id.etDlSelectedDate);



        listStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int records = 0;
                records = loadStocksList();
                System.out.println("listStocks records: " + records);
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
                EditText range = (EditText) findViewById(R.id.etDlSelectedDate);
                range.setText(startDateIso + " bis " + endDateIso);
                //updateViews();
            }
        });

        dialogFragment.show(getSupportFragmentManager(), null);
    }
}