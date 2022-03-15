package de.androidcrypto.wertpapierkurse.files;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.androidcrypto.wertpapierkurse.PriceModel;
import de.androidcrypto.wertpapierkurse.models.StockModelV2;
import de.androidcrypto.wertpapierkurse.models.StockMovementsModalV2;

public class FileAccess {

    final static String stockListFileName = "stocks02.txt";
    public static List<String[]> csvStockList = new ArrayList<>();
    public final static String[] csvStockHeader = {"isin", "name", "symbolYahooApi", "symbolApi", "active", "group"};

    static List<String[]> result = null; // filled by loadStocksList

    final static String historicPricesBaseFolder = "prices";

    public static ArrayList<StockModelV2> stockModelArrayList = new ArrayList<>();

    public static ArrayList<StockMovementsModalV2> bookingModelArrayList = new ArrayList<>();

    /* Stocks list section */

    // searches a Yahoo Api-Symbol in StocksList, returns the ISIN if fould
    public static String searchForSymbolYahooApi(String symbolYahoo) {
        String isin = "";
        if (stockModelArrayList.size() > 0) {
            for (int i = 0; i < stockModelArrayList.size(); i++) {
                String valueYahoo = stockModelArrayList.get(i).getSymbolYahooApi();
                if (valueYahoo.equals(symbolYahoo)) {
                    return stockModelArrayList.get(i).getIsin();
                }
            }
        }
        return isin;
    }

    public static void stocksListExists(Context context) {
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
        String csvFilenameComplete = path + "/" + stockListFileName;
        System.out.println("file reading: " + csvFilenameComplete);
        // check if file exists before reading
        File csvReadingFile = new File(csvFilenameComplete);
        boolean csvReadingFileExists = csvReadingFile.exists();
        System.out.println("The file is existing: " + csvReadingFileExists);
        if (!csvReadingFileExists) {
            CsvWriterSimple writer = new CsvWriterSimple();
            try {
                writer.writeToCsvFile(csvStockList, new File(csvFilenameComplete));
                System.out.println("csv file written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stocksListDeleteFile(Context context) {
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
        String csvFilenameComplete = path + "/" + stockListFileName;
        System.out.println("file reading: " + csvFilenameComplete);
        // check if file exists before reading
        File csvReadingFile = new File(csvFilenameComplete);
        boolean csvReadingFileExists = csvReadingFile.exists();
        System.out.println("The file is existing: " + csvReadingFileExists);
        if (csvReadingFileExists) {
            csvReadingFile.delete();
            System.out.println("csv file deleted");
        }
    }

    public static int loadStocksList(Context context) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
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
                //stocksList.setText(completeContent);
                records = listIndex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return records;
    }

    // same as version V3 but uses the class internal ArrayList<StockModelV2> stockModelArrayList
    // returns the number of records
    public static int loadStocksListV5(Context context) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
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
                    String symbolYahooApi = "";
                    String symbolApi = "";
                    String active = "";
                    String group = "";
                    for (String array : arrays) {
                        System.out.println(index++ + " : " + array);
                        if (index == 1) {
                            isin = array;
                            System.out.println("isin: " + isin);
                        }
                        if (index == 2) {
                            isinStockName = array;
                            System.out.println("isinStockName: " + isinStockName);
                        }
                        if (index == 3) {
                            symbolYahooApi = array;
                        }
                        if (index == 4) {
                            symbolApi = array;
                        }
                        if (index == 5) {
                            active = array;
                        }
                        if (index == 6) {
                            group = array;
                        }
                    }
                    String[] csvRecord = {isin, isinStockName, symbolYahooApi, symbolApi, active, group};
                    System.out.println("loadStocksList csvRecord vor adding: " + csvRecord);
                    csvStockList.add(csvRecord);
                    StockModelV2 stockModel = new StockModelV2(isin, isinStockName, symbolYahooApi, symbolApi, Boolean.parseBoolean(active), group);
                    stockModelArrayList.add(stockModel);
                    System.out.println("loadStocksList nach adding: " + csvStockList.size());
                }
                //stocksList.setText(completeContent);
                records = listIndex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return records;
    }

    public static int loadStocksListV3(Context context, ArrayList<StockModelV2> stockModelArrayList) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
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
                    String symbolYahooApi = "";
                    String symbolApi = "";
                    String active = "";
                    String group = "";
                    for (String array : arrays) {
                        System.out.println(index++ + " : " + array);
                        if (index == 1) {
                            isin = array;
                            System.out.println("isin: " + isin);
                        }
                        if (index == 2) {
                            isinStockName = array;
                            System.out.println("isinStockName: " + isinStockName);
                        }
                        if (index == 3) {
                            symbolYahooApi = array;
                        }
                        if (index == 4) {
                            symbolApi = array;
                        }
                        if (index == 5) {
                            active = array;
                        }
                        if (index == 6) {
                            group = array;
                        }
                    }
                    String[] csvRecord = {isin, isinStockName, symbolYahooApi, symbolApi, active, group};
                    System.out.println("loadStocksList csvRecord vor adding: " + csvRecord);
                    csvStockList.add(csvRecord);
                    StockModelV2 stockModel = new StockModelV2(isin, isinStockName, symbolYahooApi, symbolApi, Boolean.parseBoolean(active), group);
                    stockModelArrayList.add(stockModel);
                    System.out.println("loadStocksList nach adding: " + csvStockList.size());
                }
                //stocksList.setText(completeContent);
                records = listIndex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return records;
    }

    public static int loadStocksListV4(Context context) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
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
                    String symbolYahooApi = "";
                    String symbolApi = "";
                    String active = "";
                    String group = "";
                    for (String array : arrays) {
                        System.out.println(index++ + " : " + array);
                        if (index == 1) {
                            isin = array;
                            System.out.println("isin: " + isin);
                        }
                        if (index == 2) {
                            isinStockName = array;
                            System.out.println("isinStockName: " + isinStockName);
                        }
                        if (index == 3) {
                            symbolYahooApi = array;
                        }
                        if (index == 4) {
                            symbolApi = array;
                        }
                        if (index == 5) {
                            active = array;
                        }
                        if (index == 6) {
                            group = array;
                        }
                    }
                    String[] csvRecord = {isin, isinStockName, symbolYahooApi, symbolApi, active, group};
                    System.out.println("loadStocksList csvRecord vor adding: " + csvRecord);
                    csvStockList.add(csvRecord);
                    //stockModelArrayList.add(csvRecord);
                    System.out.println("loadStocksList nach adding: " + csvStockList.size());
                }
                //stocksList.setText(completeContent);
                records = listIndex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return records;
    }


    public static int loadStocksListV2(Context context) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
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
                    String active = "";
                    String group = "";
                    for (String array : arrays) {
                        System.out.println(index++ + " : " + array);
                        if (index == 1) {
                            isin = array;
                            System.out.println("isin: " + isin);
                        }
                        if (index == 2) {
                            isinStockName = array;
                            System.out.println("isinStockName: " + isinStockName);
                        }
                        if (index == 3) {
                            active = array;
                        }
                        if (index == 4) {
                            group = array;
                        }
                    }
                    String[] csvRecord = {isin, isinStockName, active, group};
                    System.out.println("loadStocksList csvRecord vor adding: " + csvRecord);
                    csvStockList.add(csvRecord);
                    //stockModelArrayList.add(csvRecord);
                    System.out.println("loadStocksList nach adding: " + csvStockList.size());
                }
                //stocksList.setText(completeContent);
                records = listIndex;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return records;
    }

    public static boolean writeStocksList(Context context, List<String[]> csvStockList) {
        boolean success = false;
        // store the new file with complete list in memory
        String path = context.getFilesDir().getAbsolutePath();
        // todo use pathSeparator instead of /
        String csvFilenameComplete = path + "/" + FileAccess.stockListFileName;
        System.out.println("csv file storing: " + csvFilenameComplete);
        CsvWriterSimple writer = new CsvWriterSimple();
        try {
            writer.writeToCsvFile(csvStockList, new File(csvFilenameComplete));
            System.out.println("csv file written");
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    /* section write and load historic prices */

    // returns the csv filename when success
    public static String writeHistoricPrices(Context context, int year, int month, String isin, List<String[]> csvList) {
        // format folder name yyyy-mm
        String ymDirectory = year + "-" +
                String.format(Locale.GERMANY, "%02d", month);
        // first setup a base folder for historic prices
        File baseFolderDir = new File(context.getFilesDir().getAbsolutePath(), historicPricesBaseFolder);
        if (!baseFolderDir.exists()) {
            baseFolderDir.mkdirs();
        }
        // second setup the subfolder vpr yyyy-mm
        File baseDir = new File(baseFolderDir, ymDirectory);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        String csvFilename = isin + "_" +
                year + "-" +
                String.format(Locale.GERMANY, "%02d", month) + ".csv";
        // todo use pathSeparator instead of /
        String csvFilenameComplete = baseDir + "/" + csvFilename;
        System.out.println("csv file storing: " + csvFilenameComplete);
        CsvWriterSimple writer = new CsvWriterSimple();
        try {
            writer.writeToCsvFile(csvList, new File(baseDir, csvFilename));
            System.out.println("csv file written for ISIN " + isin);
            return csvFilename;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    //
    public static ArrayList<PriceModel> loadHistoricPrices(Context context, String directory, String filename) {
        // load filename from directory in internal storage
        ArrayList<PriceModel> priceList = new ArrayList<>();
        priceList.clear();
        try {
            File baseFolderDir = new File(context.getFilesDir(), historicPricesBaseFolder);
            File baseDir = new File(baseFolderDir, directory);
            File csvFile = new File(baseDir, filename);
            CsvParserSimple obj = new CsvParserSimple();
            List<String[]> result = null;
            result = obj.readFile(csvFile, 1);
            int listIndex = 0;
            String completeContent = "";
            for (String[] arrays : result) {
                //System.out.println("\nString[" + listIndex++ + "] : " + Arrays.toString(arrays));
                completeContent = completeContent + "[nr " + listIndex + "] : " + Arrays.toString(arrays) + "\n";
                completeContent = completeContent + "-----------------\n";
                int index = 0;
                for (String array : arrays) {
                    //System.out.println(index++ + " : " + array);
                }
                String arDate = arrays[0];
                String arDateUnix = arrays[1];
                String arClose = arrays[2];
                float priceCloseFloat = Float.parseFloat(arClose);
                if (priceCloseFloat != 0) {
                    //System.out.println("adding priceModel");
                    PriceModel priceModel = new PriceModel(arDate, arDateUnix, arClose);
                    priceList.add(priceModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceList;
    }

    /* section bookings */

    public static void loadBookingMovementsDatasets(Context context, String year, String filename) {
        // store in year directories, not directly in files
        File baseDir = new File(context.getFilesDir(), year.toString());
        if (!baseDir.exists()) {
            System.out.println("*** ERROR no directory found ***");
            return;
        }
        String dataFilename = filename + "_" +
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

    public static void saveBookingMovementsDatasets(Context context, String year, String filename) {
        // store in year directories, not directly in files
        File baseDir = new File(context.getFilesDir(), year.toString());
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        String dataFilename = filename + "_" +
                year + ".dat";
        String dataFilenameComplete = baseDir + File.separator + dataFilename;
        System.out.println("data will be saved in " + dataFilenameComplete);
        FileOutputStream fout = null;
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


    public static void appendBookingToArrayList(String date, String isin, float totalNumberShares, float totalPurchaseCosts) {
        int position = searchInBookingModelArrayList(date, isin);
        if (position >= 0) {
            // append values
            bookingModelArrayList.get(position).setTotalNumberShares(totalNumberShares);
            bookingModelArrayList.get(position).setTotalPurchaseCosts(totalPurchaseCosts);
        } else {
            System.out.println("*** ERROR *** no dataset found for date " + date + " isin " + isin);
        }
    }

    public static int searchInBookingModelArrayList(String searchDate, String searchIsin) {
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

    // this function checks that a datarow is available for an ISIN to avoid
    // a second insertion
    public static int searchIsinInBookingModelArrayList(String searchIsin) {
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

}
