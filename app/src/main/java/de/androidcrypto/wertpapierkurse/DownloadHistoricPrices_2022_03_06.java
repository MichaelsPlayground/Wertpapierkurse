package de.androidcrypto.wertpapierkurse;

import static androidx.core.content.FileProvider.getUriForFile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadHistoricPrices_2022_03_06 extends AppCompatActivity {

    final String stockListFileName = "stocks.txt";
    List<String[]> csvStockList = new ArrayList<>();
    List<String[]> result = null; // filled by loadStocksList
    int records = 0; // number of records in csvStockList, filled by loadStocksList
    ArrayList<Entry> pricesClose = new ArrayList<>();
    List<String[]> csvList = new ArrayList<>();
    String[] csvHeaderPrices = {"date", "timestamp", "close"};

    List<String> filenameCsvList = new ArrayList<>(); // filled by getAllStocksPrices

    // for selecting start and end date of download historical prices
    private int yearSelected;
    private int monthSelected;
    private int currentYear;
    private boolean shortMonthsPicker;
    private String startDateIso = "", endDateIso = ""; // format yyyy-mm-dd

    Button listStocks, monthYearPicker, getAllStocksPrices, emailAllStockPrices, emailZipAllStockPrices;
    Button emailZipDirectoryAllStockPrices;
    EditText stocksList, selectedDate, downloadResult, etEmailAddress;
    // todo store email address in shared preferences

    final String stocksPricesZipFilename = "stocksprices"; // year and moth will be added

    // ### just for testing
    String dataToStore = "some data";
    byte[] data = dataToStore.getBytes(StandardCharsets.UTF_8);
    //String emailAddress = "fehr.michael@icloud.com, androidcrypto@gmx.de";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_historic_prices);

        listStocks = findViewById(R.id.btnDlListStocks);
        monthYearPicker = findViewById(R.id.btnDlMonthYearPicker);
        getAllStocksPrices = findViewById(R.id.btnDlAllStocksPrices);
        emailAllStockPrices = findViewById(R.id.btnDlEmailAllStocksPrices);
        emailZipAllStockPrices = findViewById(R.id.btnDlEmailZipAllStocksPrices);
        emailZipDirectoryAllStockPrices = findViewById(R.id.btnDlEmailZipDirectoryAllStocksPrices);

        stocksList = findViewById(R.id.etDlStocksList);
        selectedDate = findViewById(R.id.etDlSelectedDate);
        downloadResult = findViewById(R.id.etDlAllStocksResult);
        etEmailAddress = findViewById(R.id.etEmailAddress);


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

        getAllStocksPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check that stocks list has minimum one record
                if (records < 1) {
                    downloadResult.setText("*** FEHLER *** noch keine Wertpapierliste angezeigt");
                    return;
                }
                // check that a month and year was selected
                if (startDateIso.equals("") || endDateIso.equals("")) {
                    downloadResult.setText("*** FEHLER *** noch kein Monat ausgewaehlt");
                    return;
                }
                downloadResult.setText("");
                filenameCsvList.clear();
                // now iterate through csvStocksList, download the prices and save them
                for (String[] csvStock : csvStockList) {
                    pricesClose = new ArrayList<>();
                    URL urlName = null;
                    csvList.clear();
                    try {
                        String isin = csvStock[0];
                        String urlString = "https://data.lemon.markets/v1/ohlc/d1/?mic=XMUN&isin=" + isin + "&from=" + startDateIso + "&to=" + endDateIso + "&decimals=true&epoch=false&sorting=asc&limit=25&page=1";
                        System.out.println("urlString: " + urlString);
                        urlName = new URL(urlString);
                        //urlName = new URL("https://data.lemon.markets/v1/instruments/?isin=IE00BJ0KDQ92&mic=XMUN");
                        HttpURLConnection httpName = (HttpURLConnection) urlName.openConnection();
                        httpName.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJsZW1vbi5tYXJrZXRzIiwiaXNzIjoibGVtb24ubWFya2V0cyIsInN1YiI6InVzcl9xeURNZFdXUlJDd1JQOGhHME1HZkxscDZTZkZNa3lYenNUIiwiZXhwIjoxNjc1OTg0Njg0LCJpYXQiOjE2NDQ0NDg2ODQsImp0aSI6ImFwa19xeURNZFhYR0dENFFjU0psVm04S1k1Ump4Y25GbnBHcjRrIiwibW9kZSI6InBhcGVyIn0.Li0sacTPoJHdFiSp-yNQ_lPUeDFgR15V1_VHPZGZel0");
                        System.out.println(httpName.getResponseCode() + " ResponseMessage: " + httpName.getResponseMessage());

                        BufferedReader brName = new BufferedReader(new InputStreamReader((httpName.getInputStream())));
                        StringBuilder sbName = new StringBuilder();
                        String outputName;
                        while ((outputName = brName.readLine()) != null) {
                            sbName.append(outputName);
                        }
                        String dataName = sbName.toString();
                        System.out.println("Content:\n" + dataName);

                        // for csv export
                        csvList.add(csvHeaderPrices);

                        parsePrices(dataName);

                        // store in year-month yyyy-mm directories, not in files


                        String ymDirectory = yearSelected + "-" +
                                String.format(Locale.GERMANY, "%02d", monthSelected);
                        File baseDir = new File(getFilesDir(), ymDirectory);
                        if (!baseDir.exists()) {
                            baseDir.mkdirs();
                        }
                        String csvFilename = isin + "_" +
                                yearSelected + "-" +
                                String.format(Locale.GERMANY, "%02d", monthSelected) + ".csv";
                        String csvFilenameComplete = baseDir + "/" + csvFilename;
                        System.out.println("csv file storing: " + csvFilenameComplete);
                        filenameCsvList.add(csvFilename);
                        CsvWriterSimple writer = new CsvWriterSimple();
                        try {
                            writer.writeToCsvFile(csvList, new File(baseDir, csvFilename));
                            System.out.println("csv file written for ISIN " + isin);
                            downloadResult.append("csv written for ISIN " + isin
                                    + " " + startDateIso + " to " + endDateIso + "\n");
                        } catch (IOException e) {
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
                        //setLineChartData(pricesClose);

                        System.out.println("csvStock: " + csvStock[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error: " + e);
                        downloadResult.append("Fehler, ISIN nicht gefunden");
                    }
                }
            }
        });

        emailAllStockPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** email ***");
                System.out.println("records in filenameCsvList: " + filenameCsvList.size());

                String emailAddress = etEmailAddress.getText().toString();
                ArrayList<Uri> uris = new ArrayList<>(); // for multiple files

                // stored in year-month yyyy-mm directories, not in files
                // diese routine findet daher die erzeugten dateien NICHT

                //String filename = "testInternal.txt";
                //String subfolder = "tdat";
                String subfolder = "";

                //boolean writeSuccess = writeFileToInternalStorage(filename, subfolder, data);
                boolean writeSuccess = true;
                System.out.println("writeSuccess: " + writeSuccess);
                //if (writeSuccess) {

                //filename = "IE00BJ0KDQ92_2022-02.txt";
/*
                    File filePath = new File(getFilesDir(), subfolder);
                    File fullFile = new File(filePath, filename);
                    Context context = getApplicationContext();
                    Uri contentUri = getUriForFile(context, "de.androidcrypto.wertpapierkurse.provider", fullFile);
                    System.out.println("contentUri: " + contentUri);
*/
                // build the emailIntent
                try {
                    //String email = "test@test.com"; // change to a real email address you control
                    String email = emailAddress;
                    String subject = "email subject internal";
                    String message = "email message";
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    // iterate through filenameCsvList
                    //emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    // now just 1 file
                    String filename = filenameCsvList.get(1);
                    System.out.println("filename: " + filename);
                    File filePath = new File(getFilesDir(), subfolder);
                    File fullFile = new File(filePath, filename);
                    Context context = getApplicationContext();
                    Uri contentUri = getUriForFile(context, "de.androidcrypto.wertpapierkurse.provider", fullFile);
                    System.out.println("contentUri: " + contentUri);
                    //uris.add(contentUri);

                    if (contentUri != null) {
                        System.out.println("contentUri is not null");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        //emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
                    }

/*
                        filename = filenameCsvList.get(1);
                        System.out.println("filename: " + filename);
                        filePath = new File(getFilesDir(), subfolder);
                        fullFile = new File(filePath, filename);
                        context = getApplicationContext();
                        contentUri = getUriForFile(context, "de.androidcrypto.wertpapierkurse.provider", fullFile);
                        System.out.println("contentUri: " + contentUri);
                        uris.add(contentUri);

                 //       if (contentUri != null) {
                        if (uris != null) {
                            System.out.println("contentUri is not null");
                            //emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            //emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
                            //emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                        }
*/
                    //                      ArrayList<CharSequence> messageList = new ArrayList<>();
                    //                      messageList.add(message);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, messageList);
                    System.out.println("before MainActivity.this.startActivity");

                    // new no further error
                    // source: https://stackoverflow.com/a/59439316/8166854
                    Intent chooser = Intent.createChooser(emailIntent, "Share File");
                    List<ResolveInfo> resInfoList = DownloadHistoricPrices_2022_03_06.this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        DownloadHistoricPrices_2022_03_06.this.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(chooser);

                    System.out.println("after MainActivity.this.startActivity");
                } catch (SecurityException e) {
                    System.out.println("error: " + e.toString());
                    Toast.makeText(DownloadHistoricPrices_2022_03_06.this, "Request failed try again: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
            //}

/*
                // todo store email address in shared preferences
                Editable emailAddress = etEmailAddress.getText();
                // check if email address is entered
                if (emailAddress.equals("")) {
                    // todo put an error dialog here
                    //downloadResult.setText("*** FEHLER *** noch keine Email Adresse eingegeben");
                    return;
                }
                if (filenameCsvList.isEmpty()) {
                    // todo put an error dialog here
                    //downloadResult.setText("*** FEHLER *** noch keine Email Adresse eingegeben");
                    return;
                }
                // iterate through filenameCsvList
                // now just 1 file
                String filename = filenameCsvList.get(0);
                System.out.println("filename: " + filename);
                String subfolder = "";
                File filePath = new File(getFilesDir(), subfolder);
                File fullFile = new File(filePath, filename);
                Context context = getApplicationContext();
                Uri contentUri = getUriForFile(context, "de.androidcrypto.wertpapierkurse.provider", fullFile);
                System.out.println("contentUri: " + contentUri);

                // build the emailIntent
                try {
                    //String email = "test@test.com"; // change to a real email address you control
                    //String email = emailAddress;
                    String subject = "email subject internal from Wertpapierkurse";
                    String message = "email message";
                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{String.valueOf(emailAddress)});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    if (contentUri != null) {
                        System.out.println("contentUri is not null");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    }
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                    System.out.println("before DownloadHistoricPrices.this.startActivity");

                    // new no further error
                    // source: https://stackoverflow.com/a/59439316/8166854
                    Intent chooser = Intent.createChooser(emailIntent, "Share File now");
                    List<ResolveInfo> resInfoList = DownloadHistoricPrices.this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        DownloadHistoricPrices.this.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(chooser);

                    System.out.println("after DownloadHistoricPrices.this.startActivity");
                } catch (SecurityException e) {
                    System.out.println("error: " + e.toString());
                    Toast.makeText(DownloadHistoricPrices.this, "Request failed try again: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }*/


        });

        emailZipAllStockPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** email ***");
                System.out.println("records in filenameCsvList: " + filenameCsvList.size());

                // now we just zip the files

                String zipFilename = stocksPricesZipFilename + "_" +
                        yearSelected + "-" +
                        String.format("%02d", monthSelected) + ".zip";
                //String zipFilename = "zipfiles.zip"; // todo change to include month & year
                boolean zipSuccess = zipMultipleFiles("", filenameCsvList, "", zipFilename);
                System.out.println("zipping success : " + zipSuccess);
                // todo check for zipping success
                String emailAddress = etEmailAddress.getText().toString();
                ArrayList<Uri> uris = new ArrayList<>(); // for multiple files

                // stored in year-month yyyy-mm directories, not in files
                // diese routine findet daher die erzeugten dateien NICHT

                //String filename = "testInternal.txt";;
                //String subfolder = "tdat";
                String subfolder = "";

                //boolean writeSuccess = writeFileToInternalStorage(filename, subfolder, data);
                boolean writeSuccess = true;
                System.out.println("writeSuccess: " + writeSuccess);
                //if (writeSuccess) {

                //filename = "IE00BJ0KDQ92_2022-02.txt";

                // build the emailIntent
                try {
                    //String email = "test@test.com"; // change to a real email address you control
                    String email = emailAddress;
                    String subject = "email subject internal";
                    String message = "email message";
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    // iterate through filenameCsvList
                    //emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    // now just 1 file
                    String filename = zipFilename;
                    System.out.println("filename: " + filename);
                    //File filePath = new File(getFilesDir(), subfolder);
                    File filePath = new File(getCacheDir(), subfolder); // todo using the cache dir
                    File fullFile = new File(filePath, filename);
                    Context context = getApplicationContext();
                    Uri contentUri = getUriForFile(context, "de.androidcrypto.wertpapierkurse.provider", fullFile);
                    System.out.println("contentUri: " + contentUri);
                    //uris.add(contentUri);

                    if (contentUri != null) {
                        System.out.println("contentUri is not null");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        //emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
                    }

                    //                      ArrayList<CharSequence> messageList = new ArrayList<>();
                    //                      messageList.add(message);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, messageList);
                    System.out.println("before MainActivity.this.startActivity");

                    // new no further error
                    // source: https://stackoverflow.com/a/59439316/8166854
                    Intent chooser = Intent.createChooser(emailIntent, "Share File");
                    List<ResolveInfo> resInfoList = DownloadHistoricPrices_2022_03_06.this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        DownloadHistoricPrices_2022_03_06.this.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(chooser);

                    System.out.println("after MainActivity.this.startActivity");
                } catch (SecurityException e) {
                    System.out.println("error: " + e.toString());
                    Toast.makeText(DownloadHistoricPrices_2022_03_06.this, "Request failed try again: " + e.toString(), Toast.LENGTH_LONG).show();
                }

            }

// todo store email address in shared preferences
            // todo put an error dialog here

        });

        // this is indepent from an actual download as it will email all files
        // in a subfolder (yyyy-mm) in zipped form
        emailZipDirectoryAllStockPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** email directory ***");
                //System.out.println("records in filenameCsvList: " + filenameCsvList.size());

                // now we just zip the files

                String zipFilename = stocksPricesZipFilename + "_" +
                        yearSelected + "-" +
                        String.format("%02d", monthSelected) + ".zip";
                //String zipFilename = "zipfiles.zip"; // todo change to include month & year
                boolean zipSuccess = zipDirectory("", "", zipFilename);
                System.out.println("zipping success : " + zipSuccess);
                // todo check for zipping success
                String emailAddress = etEmailAddress.getText().toString();
                ArrayList<Uri> uris = new ArrayList<>(); // for multiple files

                //String filename = "testInternal.txt";;
                //String subfolder = "tdat";
                String subfolder = "";

                //boolean writeSuccess = writeFileToInternalStorage(filename, subfolder, data);
                boolean writeSuccess = true;
                System.out.println("writeSuccess: " + writeSuccess);
                //if (writeSuccess) {

                //filename = "IE00BJ0KDQ92_2022-02.txt";

                // build the emailIntent
                try {
                    //String email = "test@test.com"; // change to a real email address you control
                    String email = emailAddress;
                    String subject = "email subject internal";
                    String message = "email message";
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    // iterate through filenameCsvList
                    //emailIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    // now just 1 file
                    String filename = zipFilename;
                    System.out.println("filename: " + filename);
                    //File filePath = new File(getFilesDir(), subfolder);
                    File filePath = new File(getCacheDir(), subfolder); // todo using the cache dir
                    File fullFile = new File(filePath, filename);
                    Context context = getApplicationContext();
                    Uri contentUri = getUriForFile(context, "de.androidcrypto.wertpapierkurse.provider", fullFile);
                    System.out.println("contentUri: " + contentUri);
                    //uris.add(contentUri);

                    if (contentUri != null) {
                        System.out.println("contentUri is not null");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        //emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
                    }

                    //                      ArrayList<CharSequence> messageList = new ArrayList<>();
                    //                      messageList.add(message);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, messageList);
                    System.out.println("before MainActivity.this.startActivity");

                    // new no further error
                    // source: https://stackoverflow.com/a/59439316/8166854
                    Intent chooser = Intent.createChooser(emailIntent, "Share File");
                    List<ResolveInfo> resInfoList = DownloadHistoricPrices_2022_03_06.this.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        DownloadHistoricPrices_2022_03_06.this.grantUriPermission(packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    startActivity(chooser);

                    System.out.println("after MainActivity.this.startActivity");
                } catch (SecurityException e) {
                    System.out.println("error: " + e.toString());
                    Toast.makeText(DownloadHistoricPrices_2022_03_06.this, "Request failed try again: " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
// todo store email address in shared preferences
            // todo put an error dialog here
        });


    }

    public int loadStocksList() {
        records = 0;
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
                if (records > 0) {
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

    // uses GSON
    // import com.google.gson.JsonArray;
    // import com.google.gson.JsonObject;
    // import com.google.gson.JsonParser;
    // https://devqa.io/how-to-parse-json-in-java/
    public void parsePrices(String json) {
        // JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        // https://stackoverflow.com/questions/60771386/jsonparser-is-deprecated
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        //String pageName = jsonObject.getAsJsonObject("pageInfo").get("pageName").getAsString();
        //System.out.println(pageName);

        JsonArray arr = jsonObject.getAsJsonArray("results");
        for (int i = 0; i < arr.size(); i++) {
            String date = arr.get(i).getAsJsonObject().get("t").getAsString();
            String close = arr.get(i).getAsJsonObject().get("c").getAsString();
            // convert unix timestamp to date
            //long dateL = Long.parseLong(date);
            //LocalDateTime dateTimeLocal = LocalDateTime.ofInstant(Instant.ofEpochSecond(dateL), TimeZone.getDefault().toZoneId());
            System.out.println("date: " + date +
                    //      " dateLocal: " + dateTimeLocal +
                    " close: " + close);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date dateUnix = null;
            try {
                dateUnix = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long unixTime = (long) dateUnix.getTime() / 1000;
            //System.out.println(unixTime );//<- prints 1352504418
            //Float dateFloat = Float.parseFloat(unixTime);
            float dateFloat = Float.valueOf(unixTime);
            float priceCloseFloat = Float.parseFloat(close);
            if (priceCloseFloat != 0) {
                pricesClose.add(new Entry(dateFloat, priceCloseFloat));
                String[] csvRecord = {date.substring(0, 10), String.valueOf(unixTime), close};
                System.out.println("CSV data stored " + date.substring(0, 10) + " " + close);
                csvList.add(csvRecord);
            }
            //stockName.setText(post_id);
        }
    }

    // just for testing
    private boolean writeFileToInternalStorage(String filename, String path, byte[] data) {
        try {
            File dir = new File(getFilesDir(), path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            System.out.println("** dir: " + dir.toString());
            File newFile = new File(dir, filename);
            System.out.println("newFile: " + newFile.toString());
            FileOutputStream output = new FileOutputStream(new File(dir, filename));
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            int DEFAULT_BUFFER_SIZE = 1024;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n = 0;
            n = input.read(buffer, 0, DEFAULT_BUFFER_SIZE);
            while (n >= 0) {
                output.write(buffer, 0, n);
                n = input.read(buffer, 0, DEFAULT_BUFFER_SIZE);
            }
            output.close();
            input.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // source:
    // unzipping on mac gives error, check in terminal
    // unzip -t zipfiles.zip | tail -1
    /*
    End-of-central-directory signature not found.  Either this file is not
  a zipfile, or it constitutes one disk of a multi-part archive.  In the
  latter case the central directory and zipfile comment will be found on
  the last disk(s) of this archive.
     */

    private boolean zipMultipleFiles(String sourcePath, List<String> srcFiles, String zipPath, String zipFilename) {
        boolean result = false;
        try {
            //File zipDir = new File(getFilesDir(), zipPath);
            File zipDir = new File(getCacheDir(), zipPath); // todo check for cacheDir
            if (!zipDir.exists()) {
                zipDir.mkdirs();
            }
            System.out.println("** zipDir: " + zipDir.toString());
            File newFile = new File(zipDir, zipFilename);
            System.out.println("newFile: " + newFile.toString());
            FileOutputStream fos = new FileOutputStream(new File(zipDir, zipFilename));
            //FileOutputStream fos = new FileOutputStream(zipFilename);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (String srcFile : srcFiles) {
                File srcDir = new File(getFilesDir(), sourcePath);
                File fileToZip = new File(srcDir, srcFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                zipOut.closeEntry();
                fis.close();
            }
            zipOut.close();
            fos.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // source: https://www.baeldung.com/java-compress-and-uncompress
    private boolean zipDirectory(String sourceDirectory, String zipPath, String zipFilename) {
        boolean result = false;
        try {
            //File zipDir = new File(getFilesDir(), zipPath);
            File zipDir = new File(getCacheDir(), zipPath); // todo check for cacheDir
            if (!zipDir.exists()) {
                zipDir.mkdirs();
            }
            System.out.println("** zipDir: " + zipDir.toString());
            File newFile = new File(zipDir, zipFilename);
            System.out.println("newFile: " + newFile.toString());

            FileOutputStream fos = new FileOutputStream(new File(zipDir, zipFilename));
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(getFilesDir(), sourceDirectory);
            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // sub procedure from zipDirectory
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

}