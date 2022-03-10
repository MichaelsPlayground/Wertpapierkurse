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
import java.util.List;

public class SelectStock extends AppCompatActivity {
    Button listEntries;
    ListView listViewEntries;

    //List<String[]> csvStockList = new ArrayList<>();
    //List<String[]> result = null; // filled by loadStocksList
    int records = 0; // number of records in csvStockList, filled by loadStocksList


    //private String[] folderList;

    Intent startStockMovementActivityIntent;

    // steuerung des intent verhaltens
    String returnToActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_folder);

        listEntries = findViewById(R.id.btnSTSelectStock);
        listViewEntries = findViewById(R.id.lvSTListStocks);

        startStockMovementActivityIntent = new Intent(SelectStock.this, StockMovement.class);

        //listEntries.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles in SelectStock");
        if (extras != null) {
            System.out.println("extras not null");
            returnToActivity = (String) getIntent().getSerializableExtra("returnToActivity"); //Obtaining data

        }


        //File internalStorageDir = new File(getFilesDir(), "");
        // todo this hardcoded - change
        //File internalStorageDir = new File(getFilesDir(), "prices");
        //File internalStorageDir = new File(getFilesDir(), "");

        //File[] entries = internalStorageDir.listFiles();

        ArrayList<StockModel> stockModelArrayList = new ArrayList<>();
        int numberOfStocks = FileAccess.loadStocksListV3(getBaseContext(), stockModelArrayList);
        System.out.println("numberOfStocks: " + numberOfStocks);
        ArrayList<String> entryNames = new ArrayList<>();
        for (int i = 0; i < stockModelArrayList.size(); i++) {
            System.out.println("stockModelArrayList " + i);
            String isin = stockModelArrayList.get(i).getIsin();
            String isinName = stockModelArrayList.get(i).getIsinName();
            String entry = isin + " | " + isinName;
            entryNames.add(entry);
        }
        //folderList = folderNames.toArray(new String[0]);
        //System.out.println("fileList size: " + folderList.length);
        System.out.println("vor adapter, entryNames: " + entryNames.size());
        ArrayAdapter adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, entryNames);
        listViewEntries.setAdapter(adapter);
        listViewEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("The selected isin is : " + selectedItem);
                Bundle bundle = new Bundle();
                bundle.putString("selectedIsin", selectedItem);
                bundle.putString("returnToActivity", returnToActivity);
                /// todo hardcoded activity name
                if (returnToActivity.equals("StockMovement")) {
                    startStockMovementActivityIntent.putExtras(bundle);
                    startActivity(startStockMovementActivityIntent);
                }
            }
        });

        listEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* at the moment no activity
                File internalStorageDir = new File(getFilesDir(), "");
                File[] files = internalStorageDir.listFiles();
                ArrayList<String> folderNames = new ArrayList<>();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        folderNames.add(files[i].getName());
                    }
                }
                folderList = folderNames.toArray(new String[0]);
                System.out.println("fileList size: " + folderList.length);
                ArrayAdapter adapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, folderList);
                listViewEntries.setAdapter(adapter);
                listViewEntries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        System.out.println("The selected folder is : " + selectedItem);
                        Bundle bundle = new Bundle();
                        bundle.putString("browsedFolder", selectedItem);
                        startStockMovementActivityIntent.putExtras(bundle);
                        startActivity(startStockMovementActivityIntent);
                    }
                }); */
            }
        });
    }
}
