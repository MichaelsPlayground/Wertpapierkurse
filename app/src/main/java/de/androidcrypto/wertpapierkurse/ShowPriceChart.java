package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class ShowPriceChart extends AppCompatActivity {

    // for selecting start and end date of download historical prices
    private int yearSelected;
    private int monthSelected;
    private int currentYear;
    private boolean shortMonthsPicker;
    private String startDateIso, endDateIso; // format yyyy-mm-dd

    Button selectFile;

    EditText selectedFile;

    Intent listFolderIntent, listFilesIntent, browseFolderIntent;

    final String baseSubfolder = "prices"; // todo change hardcoded
    String choosenFolder = ""; // filled by ListFiles Intent
    String choosenFile = ""; // filled by ListFiles Intent

    private LineChart lineChart;
    ArrayList<Entry> pricesClose = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_price_chart);

        selectFile = findViewById(R.id.btnSpcSelectFile);
        selectedFile = findViewById(R.id.etSpcSelectedFile);

        lineChart = findViewById(R.id.spcLinechart);
        configureLineChart();

        browseFolderIntent = new Intent(ShowPriceChart.this, BrowseFolder.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String folder = "";
            String file = "";
            folder = (String) getIntent().getSerializableExtra("selectedFolder"); //Obtaining data
            if (folder != null) {
                choosenFolder = folder;
                System.out.println("Activity folder: " + folder);
                // todo do what has todo when folder is selected
            }
            file = (String) getIntent().getSerializableExtra("selectedFile"); //Obtaining data
            if (file != null) {
                choosenFile = file;
                System.out.println("Activity file: " + file);
                // todo do what has todo when file is selected
                selectedFile.setText(choosenFolder + " : " + choosenFile);
                loadCsvFile(choosenFolder, choosenFile);
            }
        }

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // todo hardcoded subfolder name
                bundle.putString("baseSubfolder", baseSubfolder);
                bundle.putString("showListFilesActivity", "true");
                bundle.putString("returnToActivity", "ShowPriceChart");
                browseFolderIntent.putExtras(bundle);
                startActivity(browseFolderIntent);
                //startActivity(browseFolderIntent);
            }
        });

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

    // todo change this method to FileAccess
    private void loadCsvFile(String directory, String filename) {
        // load filename from directory in internal storage
        pricesClose.clear();
        try {
            // todo hardcoded folder
            File baseFolderDir = new File(getFilesDir(), baseSubfolder);
            File baseDir = new File(baseFolderDir, directory);
            File csvFile = new File(baseDir, filename);
            CsvParserSimple obj = new CsvParserSimple();
            List<String[]> result = null;
            result = obj.readFile(csvFile, 1);
            int listIndex = 0;
            String completeContent = "";
            for (String[] arrays : result) {
                System.out.println("\nString[" + listIndex++ + "] : " + Arrays.toString(arrays));
                completeContent = completeContent + "[nr " + listIndex + "] : " + Arrays.toString(arrays) + "\n";
                completeContent = completeContent + "-----------------\n";
                int index = 0;
                for (String array : arrays) {
                    System.out.println(index++ + " : " + array);
                }
                String arDate = arrays[1];
                String arClose = arrays[2];
                float dateFloat = Float.parseFloat(arDate);
                //float dateFloat = Float.valueOf(unixTime);
                float priceCloseFloat = Float.parseFloat(arClose);
                if (priceCloseFloat != 0) {
                    pricesClose.add(new Entry(dateFloat, priceCloseFloat));
                }
            }
        } catch (Exception e) {
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

    }
}