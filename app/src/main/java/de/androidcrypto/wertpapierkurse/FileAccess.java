package de.androidcrypto.wertpapierkurse;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileAccess {

    final static String stockListFileName = "stocks01.txt";
    static List<String[]> csvStockList = new ArrayList<>();
    final static String[] csvStockHeader = {"isin", "name", "active", "group"};

    static List<String[]> result = null; // filled by loadStocksList

    public static void stocksListExists(Context context) {
        String path = context.getFilesDir().getAbsolutePath();
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

    public static int loadStocksListV3(Context context, ArrayList<StockModel> stockModelArrayList) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
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
                    StockModel stockModel = new StockModel(isin, isinStockName, Boolean.parseBoolean(active), group);
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

    public static int loadStocksListV2(Context context) {
        int records = 0;
        String path = context.getFilesDir().getAbsolutePath();
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
}
