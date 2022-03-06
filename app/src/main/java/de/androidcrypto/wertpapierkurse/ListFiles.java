package de.androidcrypto.wertpapierkurse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class ListFiles extends AppCompatActivity implements Serializable {

    Button listFiles;
    ListView listViewFiles;

    private String[] fileList;

    Intent startShowPriceChartIntent, startSetupModalIsinYearIntent;

    String folderFromListFolder = "";

    // steuerung des intent verhaltens
    String baseSubfolder = "";
    String returnToActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        listFiles = findViewById(R.id.btnListFilesA);
        listViewFiles = findViewById(R.id.lvFiles);

        startShowPriceChartIntent = new Intent(ListFiles.this, ShowPriceChart.class);
        startSetupModalIsinYearIntent = new Intent(ListFiles.this, SetupModalIsinYear.class);

        listFiles.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles");
        if (extras != null) {
            System.out.println("extras not null");
            String folder = "";
            folder = (String) getIntent().getSerializableExtra("browsedFolder"); //Obtaining data
            baseSubfolder = (String) getIntent().getSerializableExtra("baseSubfolder"); //Obtaining data
            returnToActivity = (String) getIntent().getSerializableExtra("returnToActivity"); //Obtaining data
            //if (!folder.equals("")) {
            if (folder != null) {
                System.out.println("folder not null");
                //folderFromListFolder = folder;
                // todo this is a hardcoded folder, change
                //folderFromListFolder = folder + "/prices";
                System.out.println("ListFile folder: " + folder);
                // todo do what has todo when folder is selected
                listFiles.setVisibility(View.GONE);
                listFiles(getBaseContext(), baseSubfolder, folder);
            }
        }

        listFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listFiles(v.getContext(), ""); // means root directory
                listFiles(v.getContext(), "", "");
            }
        });
    }

    private void listFiles(Context context, String baseSubfolder, String startDirectory) {
        //File internalStorageDir = new File(getFilesDir(), startDirectory);
        // todo this is harded folder
        File internalStorageBaseDir = new File(getFilesDir(), baseSubfolder);
        File internalStorageDir = new File(internalStorageBaseDir, startDirectory);

        File[] files = internalStorageDir.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileNames.add(files[i].getName());
            }
        }
        fileList = fileNames.toArray(new String[0]);
        System.out.println("fileList size: " + fileList.length);
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, fileList);
        listViewFiles.setAdapter(adapter);
        listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("The selected folder is : " + selectedItem);
                Bundle bundle = new Bundle();
                bundle.putString("selectedFile", selectedItem);
                bundle.putString("selectedFolder", startDirectory);
                // the way back intent depends on a bundle item
                if (returnToActivity.equals("ShowPriceChart")) {
                    startShowPriceChartIntent.putExtras(bundle);
                    startActivity(startShowPriceChartIntent);
                }
                if (returnToActivity.equals("SetupModalIsinYear")) {
                    startSetupModalIsinYearIntent.putExtras(bundle);
                    startActivity(startSetupModalIsinYearIntent);
                }
            }
        });
    }

    private void listFilesOld(Context context, String startDirectory) {
        //File internalStorageDir = new File(getFilesDir(), startDirectory);
        // todo this is harded folder
        File internalStorageBaseDir = new File(getFilesDir(), "prices");
        File internalStorageDir = new File(internalStorageBaseDir, startDirectory);

        File[] files = internalStorageDir.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileNames.add(files[i].getName());
            }
        }
        fileList = fileNames.toArray(new String[0]);
        System.out.println("fileList size: " + fileList.length);
        ArrayAdapter adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, fileList);
        listViewFiles.setAdapter(adapter);
        listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("The selected folder is : " + selectedItem);
                Bundle bundle = new Bundle();
                bundle.putString("selectedFile", selectedItem);
                bundle.putString("selectedFolder", startDirectory);
                // the way back intent depends on a bundle item
                if (returnToActivity.equals("ShowPriceChart")) {
                    startShowPriceChartIntent.putExtras(bundle);
                    startActivity(startShowPriceChartIntent);
                }

            }
        });
    }
}