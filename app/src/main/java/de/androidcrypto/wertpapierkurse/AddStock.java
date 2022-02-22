package de.androidcrypto.wertpapierkurse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddStock extends AppCompatActivity {

    Button isinDelete, groupDelete;
    Button getStockName, addStock;
    EditText stockIsin, stockName, stockGroup;
    CheckBox stockActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        isinDelete = findViewById(R.id.btnIsinDelete);
        groupDelete = findViewById(R.id.btnGroupDelete);
        getStockName = findViewById(R.id.btnSearchIsin);
        addStock = findViewById(R.id.btnAddStock);

        stockIsin = findViewById(R.id.etStockIsin);
        stockName = findViewById(R.id.etStockName);
        stockGroup = findViewById(R.id.etStockGroup);

        stockActive = findViewById(R.id.cbxStockActive);

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

                // after storing
                stockIsin.setText("");
                stockName.setText("");
                stockActive.setChecked(true);
            }
        });
    }
}