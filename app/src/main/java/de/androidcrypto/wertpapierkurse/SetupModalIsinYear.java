package de.androidcrypto.wertpapierkurse;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.data.Entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SetupModalIsinYear extends AppCompatActivity {

    Button isinDelete, yearDelete, generateDataset, printDataset;
    Button saveDataset, loadDataset, searchDataset, appendClosePrices;
    Button appendBookings, calculateBookings, returnToMainActivity;
    EditText stockIsin, entryYear;

    String stockMovementsFilename;

    Intent listFolderIntent, listFilesIntent, browseFolderIntent;
    Intent mainActivityIntent;
    String choosenFolder = ""; // filled by ListFiles Intent
    String choosenFile = ""; // filled by ListFiles Intent
    final String baseSubfolder = "prices"; // todo change hardcoded

    String bookingYear = "2022";; // todo change hardcoded

    ArrayList<StockMovementsModalV2> bookingModelArrayList;
    ArrayList<Entry> pricesClose = new ArrayList<>();
    ArrayList<PriceModel> priceModelArrayList = new ArrayList<>(); // filled by FileAcess.loadHistoricPrices

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_modal_isin_year);

        isinDelete = findViewById(R.id.btnSDIYIsinDelete);
        yearDelete = findViewById(R.id.btnSDIYYearDelete);
        generateDataset = findViewById(R.id.btnSDIYGenerate);
        printDataset = findViewById(R.id.btnSDIYPrint);
        saveDataset = findViewById(R.id.btnSDIYSave);
        loadDataset = findViewById(R.id.btnSDIYLoad);
        searchDataset = findViewById(R.id.btnSDIYSearch);
        appendClosePrices = findViewById(R.id.btnSDIYAppendClosePrices);
        appendBookings = findViewById(R.id.btnSDIYAppendBookings);
        calculateBookings = findViewById(R.id.btnSDIYCalculateBookings);
        returnToMainActivity = findViewById(R.id.btnSDIYReturnToMainActivity);

        stockIsin = findViewById(R.id.etSDIYStockIsin);
        entryYear = findViewById(R.id.etSDIYYear);

        bookingModelArrayList = new ArrayList<StockMovementsModalV2>();



        browseFolderIntent = new Intent(SetupModalIsinYear.this, BrowseFolder.class);
        mainActivityIntent = new Intent(SetupModalIsinYear.this, MainActivity.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String folder = "";
            String file = "";
            folder = (String) getIntent().getSerializableExtra("selectedFolder"); //Obtaining data
            System.out.println("*** we are in SetupModalIsinYear activity ***");
            if (folder != null) {
                choosenFolder = folder;
                System.out.println("Activity folder: " + folder);
                // todo do what has todo when folder is selected
            }
            file = (String) getIntent().getSerializableExtra("selectedFile"); //Obtaining data
            if (file != null) {
                choosenFile = file;
                System.out.println("SetupModalIsinYear Activity file: " + file);
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

        // todo calculate the daily account value
        // todo set end of month prices even if the are weekends from last available date

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
                // todo load existing datasets and append the new ones only

                System.out.println("*** generate new datasets in the database ***");
                Editable isin = stockIsin.getText();
                Editable year = entryYear.getText();
                System.out.println("for ISIN: " + isin.toString() + " for year: " + year);
                // first check that ISIN is NOT available in bookingModelArrayList
                int searchResult = searchIsinInBookingModelArrayList(isin.toString());
                if (searchResult >= 0) {
                    // isin allready present in list, don't append a second data row
                    System.out.println("ISIN " + isin.toString() + " is allready present in dataset");
                    return;
                }

                ArrayList<String> daysInYearWithoutWeenends = getListOfDaysWithoutWeekends(year.toString());
                System.out.println("arraylist generated with entries: " + daysInYearWithoutWeenends.size());
/*
    public StockMovementsModal(String date, String dateUnix,
                               String stockName, String stockIsin,
                               String direction, String amountEuro,
                               String numberShares, String bank,
                               String securitiesAccount,
                               String note, String totalNumberShares,
                               String totalPurchaseCosts,
                               String dataYear, String dataMonth,
                               String active) {
 */

                // now store each date
                for (int i = 0; i < daysInYearWithoutWeenends.size(); i++) {
                    String date = daysInYearWithoutWeenends.get(i);
                    float unixDate = Utils.dateToUnixDate(date);
                    StockMovementsModalV2 bookingModel = new StockMovementsModalV2(
                            date, unixDate, "stockname", isin.toString(),
                            "", "", "", "",
                            "", "",0,
                            0, 0, 0,
                            0,
                            year.toString(), "", "");
                    bookingModelArrayList.add(bookingModel);
                }

                System.out.println("datasets created:" + bookingModelArrayList.size());

            }
        });

        printDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** generated dataset ***");
                if (bookingModelArrayList == null) {
                    System.out.println("bookingModelArrayList ist NULL");
                    return;
                }
                int datasetSize = bookingModelArrayList.size();
                System.out.println("total datasets: " + datasetSize);
                for (int i = 0; i < datasetSize; i++) {
                    System.out.println("i: " + i +
                            " de: " + bookingModelArrayList.get(i).getDate() +
                            " is: " + bookingModelArrayList.get(i).getStockIsin() +
                            " cp: " + bookingModelArrayList.get(i).getClosePrice() +
                            " ts: " + bookingModelArrayList.get(i).getTotalNumberShares() +
                            " tp: " + bookingModelArrayList.get(i).getTotalPurchaseCosts() +
                            " ta: " + bookingModelArrayList.get(i).getSecuritiesAccountAmountEuro() +
                            " wl: " + bookingModelArrayList.get(i).getWinLossEuro());
                }
                System.out.println("++ printout completed ++");
            }
        });

        // todo move to FileAccess
        saveDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** save datasets to file ***");
                Editable year = entryYear.getText();
                stockMovementsFilename = "movements";

                // store in year directories, not directly in files
                File baseDir = new File(getFilesDir(), year.toString());
                if (!baseDir.exists()) {
                    baseDir.mkdirs();
                }
                String dataFilename = stockMovementsFilename + "_" +
                        year + ".dat";
                String dataFilenameComplete = baseDir + File.separator + dataFilename;
                System.out.println("data will be saved in " + dataFilenameComplete);
                FileOutputStream fout= null;
                try {
                    fout = new FileOutputStream(dataFilenameComplete);
                    ObjectOutputStream oos = new ObjectOutputStream(fout);
                    oos.writeObject(bookingModelArrayList);
                    fout.close();
                    System.out.println("data saved");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: data NOT saved");
                }
            }
        });

        loadDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** load datasets from file ***");
                Editable year = entryYear.getText();
                // todo hardcoded filename
                stockMovementsFilename = "movements";
                loadBookingMovementsDatasets(year.toString(), stockMovementsFilename);
                /*
                // store in year directories, not directly in files
                File baseDir = new File(getFilesDir(), year.toString());
                if (!baseDir.exists()) {
                    System.out.println("*** ERROR no directory found ***");
                    return;
                }
                String dataFilename = stockMovementsFilename + "_" +
                        year + ".dat";
                String dataFilenameComplete = baseDir + File.separator + dataFilename;
                System.out.println("data will be loaded from " + dataFilenameComplete);
                File loadFile = new File(dataFilenameComplete);
                if (!loadFile.exists()) {
                    System.out.println("*** ERROR no data file found ***");
                    return;
                }

                //ArrayList<StockMovementsModal> bookingModelArrayListLoad = new ArrayList<>();
                bookingModelArrayList.clear();
                FileInputStream fin= null;
                try {
                    fin = new FileInputStream(dataFilenameComplete);
                    ObjectInputStream ois = new ObjectInputStream(fin);
                    bookingModelArrayList = (ArrayList<StockMovementsModal>)ois.readObject();
                    fin.close();
                } catch (FileNotFoundException | ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("data loaded");
                int datasetSize = bookingModelArrayList.size();
                System.out.println("total datasets: " + datasetSize);
                for (int i = 0; i < datasetSize; i++) {
                    System.out.println("i: " + i +
                            " date: " + bookingModelArrayList.get(i).getDate() +
                            " isin: " + bookingModelArrayList.get(i).getStockIsin() +
                            " closePrice: " + bookingModelArrayList.get(i).getClosePrice());
                }
                System.out.println("++ printout completed ++");
                 */
            }
        });

        searchDataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** search dataset ***");
                String dateSearch = "2022-01-05";
                String isinSearch = "IE00BJ0KDQ92";
                System.out.println("to search: date " + dateSearch + " ISIN " + isinSearch);
                if (bookingModelArrayList == null) {
                    System.out.println("ERROR: no datasets in bookingModelArrayList available");
                    return;
                }
                int listSize = bookingModelArrayList.size();
                if (listSize == 0) {
                    System.out.println("ERROR: no datasets in bookingModelArrayList available");
                    return;
                } else {
                    System.out.println("size all datasets: " + listSize);
                }
                // iterate through all datasets
                StockMovementsModalV2 stockMovementsModal;
                for (int i = 0; i < listSize; i++) {
                    stockMovementsModal = bookingModelArrayList.get(i);
                    String date = stockMovementsModal.getDate();
                    String isin = stockMovementsModal.getStockIsin();
                    if (date.equals(dateSearch) && isin.equals(isinSearch)) {
                        System.out.println("dataset found: " + i);
                        return;
                    }
                }
                System.out.println("no dataset found");
            }
        });

        appendClosePrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** append close prices from csv file ***");
                //startActivity(browseFolderIntent);
                Bundle bundle = new Bundle();
                // todo hardcoded subfolder name
                bundle.putString("baseSubfolder", baseSubfolder);
                bundle.putString("showListFilesActivity", "true");
                bundle.putString("returnToActivity", "SetupModalIsinYear");
                browseFolderIntent.putExtras(bundle);
                startActivity(browseFolderIntent);
            }
        });

        appendBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** append bookings ***");
                System.out.println("this is a manual function to append some bookings to the database");
                String date, isin;
                float totalNumberShares, totalPurchaseCosts;
                // search dataset in boking ModelArrayList
                isin = "IE00BJ0KDQ92";

                date = "2022-07-01";
                totalNumberShares = 10f;
                totalPurchaseCosts = 40.10f;
                appendBookingToArrayList("2022-02-01", isin, 10f, 830.1f);
                appendBookingToArrayList("2022-02-02", isin, 20f, 1650.1f);
                appendBookingToArrayList("2022-02-03", isin, 30f, 2500.1f);
                appendBookingToArrayList("2022-02-04", isin, 40f, 3401.1f);
                appendBookingToArrayList("2022-02-07", isin, 50f, 4400.1f);

                /*
                int position = searchInBookingModelArrayList(date, isin);
                if (position >= 0) {
                    // append values
                    bookingModelArrayList.get(position).setTotalNumberShares(totalNumberShares);
                    bookingModelArrayList.get(position).setTotalPurchaseCosts(totalPurchaseCosts);
                } else {
                    System.out.println("*** ERROR *** no dataset found for date " + date + " isin " + isin);
                }*/
                // todo don't forget so save the list ....
            }
        });

        calculateBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this function recalculates the securitiesAccountAmountEuro
                // and the winLossEuro for each entry

                // check that bookingModelArrayList has entries
                System.out.println("*** calculate datasets ***");
                if (bookingModelArrayList == null) {
                    System.out.println("bookingModelArrayList ist NULL");
                    return;
                }
                int datasetSize = bookingModelArrayList.size();
                System.out.println("total datasets: " + datasetSize);
                float totalNumberShares, totalPurchaseCosts, closePrice, securitiesAmountEuro, winLossEuro;
                for (int i = 0; i < datasetSize; i++) {
                    totalNumberShares = bookingModelArrayList.get(i).getTotalNumberShares();
                    totalPurchaseCosts = bookingModelArrayList.get(i).getTotalPurchaseCosts();
                    closePrice = bookingModelArrayList.get(i).getClosePrice();
                    securitiesAmountEuro = totalNumberShares * closePrice;
                    winLossEuro = securitiesAmountEuro - totalPurchaseCosts;
                    bookingModelArrayList.get(i).setSecuritiesAccountAmountEuro(securitiesAmountEuro);
                    bookingModelArrayList.get(i).setWinLossEuro(winLossEuro);
                }
                // todo don't forget to save the list
                System.out.println("++ calculation completed +++");
            }
        });

        returnToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainActivityIntent);
            }
        });

    }

    private void appendBookingToArrayList(String date, String isin, float totalNumberShares, float totalPurchaseCosts) {
        int position = searchInBookingModelArrayList(date, isin);
        if (position >= 0) {
            // append values
            bookingModelArrayList.get(position).setTotalNumberShares(totalNumberShares);
            bookingModelArrayList.get(position).setTotalPurchaseCosts(totalPurchaseCosts);
        } else {
            System.out.println("*** ERROR *** no dataset found for date " + date + " isin " + isin);
        }
    }

    // todo move to FileAccess
    private void loadBookingMovementsDatasets(String year, String filename) {
        // store in year directories, not directly in files
        File baseDir = new File(getFilesDir(), year.toString());
        if (!baseDir.exists()) {
            System.out.println("*** ERROR no directory found ***");
            return;
        }
        String dataFilename = stockMovementsFilename + "_" +
                year + ".dat";
        String dataFilenameComplete = baseDir + File.separator + dataFilename;
        System.out.println("data will be loaded from " + dataFilenameComplete);
        File loadFile = new File(dataFilenameComplete);
        if (!loadFile.exists()) {
            System.out.println("*** ERROR no data file found ***");
            return;
        }

        //ArrayList<StockMovementsModal> bookingModelArrayListLoad = new ArrayList<>();
        bookingModelArrayList.clear();
        FileInputStream fin= null;
        try {
            fin = new FileInputStream(dataFilenameComplete);
            ObjectInputStream ois = new ObjectInputStream(fin);
            bookingModelArrayList = (ArrayList<StockMovementsModalV2>)ois.readObject();
            fin.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("data loaded");
        int datasetSize = bookingModelArrayList.size();
        System.out.println("total datasets: " + datasetSize);
        for (int i = 0; i < datasetSize; i++) {
            System.out.println("i: " + i +
                    " date: " + bookingModelArrayList.get(i).getDate() +
                    " isin: " + bookingModelArrayList.get(i).getStockIsin() +
                    " closePrice: " + bookingModelArrayList.get(i).getClosePrice());
        }
        System.out.println("++ printout completed ++");
    }

    private int searchInBookingModelArrayList(String searchDate, String searchIsin) {
        int result = -1;
        if (bookingModelArrayList == null) {
            System.out.println("ERROR: no datasets in bookingModelArrayList available");
            return result;
        }
        int listSize = bookingModelArrayList.size();
        if (listSize == 0) {
            System.out.println("ERROR: no datasets in bookingModelArrayList available");
            return result;
        } else {
            System.out.println("size all datasets: " + listSize);
        }
        // iterate through all datasets
        StockMovementsModalV2 stockMovementsModal;
        for (int i = 0; i < listSize; i++) {
            stockMovementsModal = bookingModelArrayList.get(i);
            String date = stockMovementsModal.getDate();
            String isin = stockMovementsModal.getStockIsin();
            if (date.equals(searchDate) && isin.equals(searchIsin)) {
                System.out.println("dataset found: " + i);
                return i;
            }
        }
        return result;
    }

    // todo move to FileAccess
    // this function checks that a datarow is available for an ISIN to avoid
    // a second insertion
    private int searchIsinInBookingModelArrayList(String searchIsin) {
        int result = -1;
        if (bookingModelArrayList == null) {
            System.out.println("ERROR: no datasets in bookingModelArrayList available");
            return result;
        }
        int listSize = bookingModelArrayList.size();
        if (listSize == 0) {
            System.out.println("ERROR: no datasets in bookingModelArrayList available");
            return result;
        } else {
            System.out.println("size all datasets: " + listSize);
        }
        // iterate through all datasets
        StockMovementsModalV2 stockMovementsModal;
        for (int i = 0; i < listSize; i++) {
            stockMovementsModal = bookingModelArrayList.get(i);
            String isin = stockMovementsModal.getStockIsin();
            if (isin.equals(searchIsin)) {
                System.out.println("dataset found: " + i);
                return i;
            }
        }
        return result;
    }


    private void loadCsvFile(String directory, String baseSubfolder, String filename) {
        // load filename from directory in internal storage
        pricesClose.clear();
        try {
            File baseSubfolderDir = new File(getFilesDir(), baseSubfolder);
            File baseDir = new File(baseSubfolderDir, directory);
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
        //
        // setLineChartData(pricesClose);
        System.out.println("* csv file loaded with size: " + pricesClose.size());

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