package de.androidcrypto.wertpapierkurse;

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

public class BrowseFolder extends AppCompatActivity implements Serializable {
    Button listFolder;
    ListView listViewFolder;

    private String[] folderList;

    Intent startListFileActivityIntent;

    // steuerung des intent verhaltens
    String baseSubfolder = "";
    String returnToActivity = "";
    String showListFilesActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_folder);

        listFolder = findViewById(R.id.btnBrowseFolderB);
        listViewFolder = findViewById(R.id.lvBrowseFolder);

        startListFileActivityIntent = new Intent(BrowseFolder.this, ListFiles.class);

        listFolder.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles in BrowseFolder");
        if (extras != null) {
            System.out.println("extras not null");
            baseSubfolder = (String) getIntent().getSerializableExtra("baseSubfolder"); //Obtaining data
            showListFilesActivity = (String) getIntent().getSerializableExtra("listFilesActivity"); //Obtaining data
            returnToActivity = (String) getIntent().getSerializableExtra("returnToActivity"); //Obtaining data
            //if (!folder.equals("")) {
            if (baseSubfolder != null) {
                System.out.println("folder not null");
                //folderFromListFolder = folder;
                // todo this is a hardcoded folder, change
                //folderFromListFolder = folder + "/prices";
                System.out.println(" BrowseFolder baseSubfolder: " + baseSubfolder);
                // todo do what has todo when folder is selected
            }
        }


        //File internalStorageDir = new File(getFilesDir(), "");
        // todo this hardcoded - change
        //File internalStorageDir = new File(getFilesDir(), "prices");
        File internalStorageDir = new File(getFilesDir(), baseSubfolder);

        File[] files = internalStorageDir.listFiles();
        ArrayList<String> folderNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                folderNames.add(files[i].getName());
            }
        }
        folderList = folderNames.toArray(new String[0]);
        System.out.println("fileList size: " + folderList.length);
        ArrayAdapter adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, folderList);
        listViewFolder.setAdapter(adapter);
        listViewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                System.out.println("The selected folder is : " + selectedItem);
                Bundle bundle = new Bundle();
                bundle.putString("browsedFolder", selectedItem);
                bundle.putString("baseSubfolder", baseSubfolder);
                bundle.putString("returnToActivity", returnToActivity);
                startListFileActivityIntent.putExtras(bundle);
                startActivity(startListFileActivityIntent);
            }
        });

        listFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                listViewFolder.setAdapter(adapter);
                listViewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        System.out.println("The selected folder is : " + selectedItem);
                        Bundle bundle = new Bundle();
                        bundle.putString("browsedFolder", selectedItem);
                        startListFileActivityIntent.putExtras(bundle);
                        startActivity(startListFileActivityIntent);
                    }
                });
            }
        });
    }
}
