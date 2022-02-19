package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ShowPriceChart extends AppCompatActivity {

    // for selecting start and end date of download historical prices
    private int yearSelected;
    private int monthSelected;
    private int currentYear;
    private boolean shortMonthsPicker;
    private String startDateIso, endDateIso; // format yyyy-mm-dd

    Button monthYearPicker, getPriceData, fileChooser;

    EditText selectedDate;

    ListView lv;

    boolean isKitKat = false;
    String realPath_1 = "", file_1 = "";
    private String[] SavedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_price_chart);

        monthYearPicker = findViewById(R.id.btnSpcMonthYearPicker);
        selectedDate = findViewById(R.id.etSpcSelectedDate);

        getPriceData = findViewById(R.id.btnSpcGetPrices);

        fileChooser = findViewById(R.id.btnSpcChooser);
        lv = findViewById(R.id.lv);

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

        getPriceData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check that a month and year was selected
                if (startDateIso.equals("") || endDateIso.equals("")) {
                    //downloadResult.setText("*** FEHLER *** noch kein Monat ausgewaehlt");
                    System.out.println("*** FEHLER *** noch kein Monat ausgewaehlt");
                    return;
                }
                // todo setup error dialog

                // todo list available isin, choose one, show content in chart

            }
        });

        fileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showFileChooser();
                //SavedFiles = getFilesDir().list();
                //File mydir = v.getContext().getDir("2021-12", Context.MODE_PRIVATE);

                // listet alle dateien in 2022-01
                //File mydir = new File(getFilesDir(), "2022-01");
                //File lister = mydir.getAbsoluteFile();
/*
                File mydir = new File(getFilesDir(), "");
                File lister = mydir.getAbsoluteFile();

                System.out.println("lister: " + lister.getAbsolutePath());
                for (String list : lister.list())
                {
                    System.out.println("list: " + list);
                }
                SavedFiles = lister.list();
                System.out.println("SavedFiles size: " + SavedFiles.length);
                ShowSavedFiles();
*/

                File internalStorageDir = new File(getFilesDir(), "");
                File[] files = internalStorageDir.listFiles();
                ArrayList<String> fileNames = new ArrayList<>();
                for(int i=0; i< files.length; i++){
                    if (files[i].isDirectory()) {
                        fileNames.add(files[i].getName());
                    }
                }

                SavedFiles = fileNames.toArray(new String[0]);
                System.out.println("SavedFiles size: " + SavedFiles.length);
                ShowSavedFiles();
            }
        });



    }

    void ShowSavedFiles(){
        ArrayList<String> filteredList = new ArrayList<String>();
        //SavedFiles = fileList();

        /*
        // just files with .csv
        for(String str: SavedFiles) {
            if (str.trim().contains(".csv")) {
                filteredList.add(str.trim());}
        }

         */

        // just folder
        /*
        for(String str: SavedFiles) {
            if (str.trim().contains(".csv")) {
                filteredList.add(str.trim());}
        }

         */

        // filtered list
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filteredList);

        // unfiltered list
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SavedFiles);

        lv.setAdapter(adapter);
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
                //EditText range = (EditText) findViewById(R.id.etStartDate);
                selectedDate.setText(startDateIso + " bis " + endDateIso);
                //updateViews();
            }
        });

        dialogFragment.show(getSupportFragmentManager(), null);
    }

    private void showFileChooser() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            isKitKat = true;
            startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
        } else {
            isKitKat = false;
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select file"), 1);
        }
    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (data != null && data.getData() != null && resultCode == getActivity().RESULT_OK) {

                boolean isImageFromGoogleDrive = false;

                Uri uri = data.getData();

                if (isKitKat && DocumentsContract.isDocumentUri(getActivity(), uri)) {
                    if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        String[] split = docId.split(":");
                        String type = split[0];

                        if ("primary".equalsIgnoreCase(type)) {
                            realPath_1 = Environment.getExternalStorageDirectory() + "/" + split[1];
                        } else {
                            Pattern DIR_SEPORATOR = Pattern.compile("/");
                            Set<String> rv = new HashSet<>();
                            String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                            String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                            String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                            if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                                if (TextUtils.isEmpty(rawExternalStorage)) {
                                    rv.add("/storage/sdcard0");
                                } else {
                                    rv.add(rawExternalStorage);
                                }
                            } else {
                                String rawUserId;
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    rawUserId = "";
                                } else {
                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                    String[] folders = DIR_SEPORATOR.split(path);
                                    String lastFolder = folders[folders.length - 1];
                                    boolean isDigit = false;
                                    try {
                                        Integer.valueOf(lastFolder);
                                        isDigit = true;
                                    } catch (NumberFormatException ignored) {
                                    }
                                    rawUserId = isDigit ? lastFolder : "";
                                }
                                if (TextUtils.isEmpty(rawUserId)) {
                                    rv.add(rawEmulatedStorageTarget);
                                } else {
                                    rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                                }
                            }
                            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                                String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                                Collections.addAll(rv, rawSecondaryStorages);
                            }
                            String[] temp = rv.toArray(new String[rv.size()]);
                            for (int i = 0; i < temp.length; i++) {
                                File tempf = new File(temp[i] + "/" + split[1]);
                                if (tempf.exists()) {
                                    realPath_1 = temp[i] + "/" + split[1];
                                }
                            }
                        }
                    } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                        String id = DocumentsContract.getDocumentId(uri);
                        Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                        Cursor cursor = null;
                        String column = "_data";
                        String[] projection = {column};
                        try {
                            cursor = getActivity().getContentResolver().query(contentUri, projection, null, null,
                                    null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int column_index = cursor.getColumnIndexOrThrow(column);
                                realPath_1 = cursor.getString(column_index);
                            }
                        } finally {
                            if (cursor != null)
                                cursor.close();
                        }
                    } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        String[] split = docId.split(":");
                        String type = split[0];

                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        String selection = "_id=?";
                        String[] selectionArgs = new String[]{split[1]};

                        Cursor cursor = null;
                        String column = "_data";
                        String[] projection = {column};

                        try {
                            cursor = getActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                int column_index = cursor.getColumnIndexOrThrow(column);
                                realPath_1 = cursor.getString(column_index);
                            }
                        } finally {
                            if (cursor != null)
                                cursor.close();
                        }
                    } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                        isImageFromGoogleDrive = true;
                    }
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};

                    try {
                        cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            realPath_1 = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                    realPath_1 = uri.getPath();
                }

                try {
                    Log.d(TAG, "Real Path 1 : " + realPath_1);
                    file_1 = realPath_1.substring(realPath_1.lastIndexOf('/') + 1, realPath_1.length());
                    Log.i("File Name 1 ", file_1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };*/
}