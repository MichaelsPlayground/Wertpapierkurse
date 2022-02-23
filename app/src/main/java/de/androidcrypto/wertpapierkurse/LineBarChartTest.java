package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class LineBarChartTest extends AppCompatActivity {

    Button test;
    private LineChart lineChart;
    ArrayList<Entry> pricesClose = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_bar_chart_test);

        test = findViewById(R.id.btnLineBarChartTest);
        lineChart = findViewById(R.id.spcLineBarchart);
        configureLineChart();

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1641337200 = 05.01.22 bis 11.01.22
                pricesClose.add(new Entry(Float.parseFloat("1641337200"), Float.parseFloat("43.77")));
                pricesClose.add(new Entry(Float.parseFloat("1641423600"), Float.parseFloat("43.415")));
                pricesClose.add(new Entry(Float.parseFloat("1641510000"), Float.parseFloat("43.195")));
                pricesClose.add(new Entry(Float.parseFloat("1641769200"), Float.parseFloat("42.435")));
                pricesClose.add(new Entry(Float.parseFloat("1641855600"), Float.parseFloat("42.725")));

                Comparator<Entry> comparator = new Comparator<Entry>() {
                    @Override
                    public int compare(Entry o1, Entry o2) {
                        return Float.compare(o1.getX(), o2.getX());
                    }
                };

                pricesClose.sort(comparator);
                //setLineChartData(pricesHigh, pricesLow, pricesClose);
                setLineChartData(pricesClose);

                // todo add a bar chart with wins and losses
            }
        });

    }

    private void configureLineChart() {
        Description desc = new Description();
        desc.setText("Stock Price History 2");
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


}