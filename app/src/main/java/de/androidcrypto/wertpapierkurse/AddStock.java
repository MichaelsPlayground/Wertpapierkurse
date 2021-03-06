package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import de.androidcrypto.wertpapierkurse.apis.ApiAccess;
import de.androidcrypto.wertpapierkurse.files.FileAccess;

public class AddStock extends AppCompatActivity {

    Button isinDelete, groupDelete, symbolYahooDelete;
    Button getStockName, addStock;
    EditText stockIsin, stockName, stockGroup, stockSymbolYahooApi;
    CheckBox stockActive;

    // steuerung des intent verhaltens
    String selectedPosition = "";
    String returnToActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        isinDelete = findViewById(R.id.btnIsinDelete);
        groupDelete = findViewById(R.id.btnGroupDelete);
        symbolYahooDelete = findViewById(R.id.btnSymbolYahooApiDelete);
        getStockName = findViewById(R.id.btnSearchIsin);
        addStock = findViewById(R.id.btnAddStock);

        stockIsin = findViewById(R.id.etStockIsin);
        stockName = findViewById(R.id.etStockName);
        stockSymbolYahooApi = findViewById(R.id.etStockSymbolYahooApi);
        stockGroup = findViewById(R.id.etStockGroup);

        stockActive = findViewById(R.id.cbxStockActive);

//        addStock.setBackgroundColor(Color.GRAY);
        addStock.setEnabled(false);

        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles in AddStock");
        if (extras != null) {
            System.out.println("extras not null");
            returnToActivity = (String) getIntent().getSerializableExtra("returnToActivity");
            selectedPosition = (String) getIntent().getSerializableExtra("selectedPosition");
            System.out.println("selectedPosition in stockModelArrayList: " + selectedPosition);
            if (returnToActivity.equals(Constants.MAINTAIN_STOCKLIST_CLASS)) {
                int pos = Integer.valueOf(selectedPosition);
                stockIsin.setText(FileAccess.stockModelArrayList.get(pos).getIsin());
                stockName.setText(FileAccess.stockModelArrayList.get(pos).getIsinName());
                stockSymbolYahooApi.setText(FileAccess.stockModelArrayList.get(pos).getSymbolYahooApi());
                stockGroup.setText(FileAccess.stockModelArrayList.get(pos).getGroup());
                stockActive.setChecked(FileAccess.stockModelArrayList.get(pos).getActive());
                addStock.setText("Wertpapier aendern");
                addStock.setEnabled(true);
            }
        }

        isinDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockIsin.setText("");
            }
        });

        getStockName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("*** get name ***");
                Editable isin = stockIsin.getText();
                stockName.setText(ApiAccess.getStockName(isin.toString()));
                //addStock.setBackgroundColor(getColor(R.color.blue));
                addStock.setEnabled(true);
            }
        });

        symbolYahooDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockSymbolYahooApi.setText("");
            }
        });

        groupDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockGroup.setText("");
            }
        });

        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // todo check das die felder isin und wertpapiername tats??chlich gef??llt sind
                Editable isin = stockIsin.getText();
                Editable isinStockName = stockName.getText();
                Editable yahooSymbol = stockSymbolYahooApi.getText();
                boolean activeChecked = stockActive.isChecked();
                Editable group = stockGroup.getText();
                //csvStockList.add(csvStockHeader);
                String[] csvRecord = {String.valueOf(isin), String.valueOf(isinStockName), String.valueOf(yahooSymbol), "", String.valueOf(activeChecked), String.valueOf(group)};


                // empty the existing stocksList
                FileAccess.csvStockList.clear();
                FileAccess.csvStockList.add(FileAccess.csvStockHeader);
                // check if stocks list file exists, if not create one with header
                FileAccess.stocksListExists(v.getContext());
                // now load the existing file
                int records = 0;
                records = FileAccess.loadStocksListV4(v.getContext());
                System.out.println("add to existing records: " + records);
                // add the new record
                System.out.println("csvStockList records before adding: " + FileAccess.csvStockList.size());
                if (returnToActivity.equals(Constants.MAINTAIN_STOCKLIST_CLASS)) {
                    // editing an existing entry
                    // todo write the code for editing the data
                } else {
                    // add a new entry
                    FileAccess.csvStockList.add(csvRecord);
                }
                System.out.println("csvStockList records after adding: " + FileAccess.csvStockList.size());
                // delete the old csv file
                FileAccess.stocksListDeleteFile(v.getContext());
                boolean writeSuccess = FileAccess.writeStocksList(v.getContext(), FileAccess.csvStockList);
      /*
                // store the new file with complete list in memory
                String path = getFilesDir().getAbsolutePath();
                String csvFilenameComplete = path + "/" + FileAccess.stockListFileName;
                System.out.println("csv file storing: " + csvFilenameComplete);
                CsvWriterSimple writer = new CsvWriterSimple();
                try {
                    writer.writeToCsvFile(FileAccess.csvStockList, new File(csvFilenameComplete));
                    System.out.println("csv file written");
                } catch (IOException e) {
                    e.printStackTrace();
                }

       */
                System.out.println("write the new csvStockList: " + writeSuccess);



                // after storage
                stockIsin.setText("");
                stockName.setText("");
                stockActive.setChecked(true);
                addStock.setEnabled(false);
            }
        });
    }
}