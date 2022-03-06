package de.androidcrypto.wertpapierkurse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShowPriceChart_2022_03_06 extends AppCompatActivity {

    // for selecting start and end date of download historical prices
    private int yearSelected;
    private int monthSelected;
    private int currentYear;
    private boolean shortMonthsPicker;
    private String startDateIso, endDateIso; // format yyyy-mm-dd

    Button selectFile;

    EditText selectedFile;

    Intent listFolderIntent, listFilesIntent, browseFolderIntent;

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

        browseFolderIntent = new Intent(ShowPriceChart_2022_03_06.this, BrowseFolder.class);

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
                startActivity(browseFolderIntent);
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

    private void loadCsvFile(String directory, String filename) {
        // load filename from directory in internal storage
        pricesClose.clear();
        try {
            File baseDir = new File(getFilesDir(), directory);
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