package de.androidcrypto.wertpapierkurse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MaintainStocklist_2022_03_10 extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    ConstraintLayout constraintLayout;
    ArrayList<StockModel> stockModelArrayList = new ArrayList<>();

    Intent addStockIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_stocklist);

        recyclerView = findViewById(R.id.recyclerView);
        constraintLayout = findViewById(R.id.coordinatorLayout);

        addStockIntent = new Intent(MaintainStocklist_2022_03_10.this, AddStock.class);

        populateRecyclerView(getBaseContext());
        enableSwipeToDeleteAndUndo();


        FloatingActionButton fab = findViewById(R.id.fabAddStock);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addStockIntent);

                /*
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        // todo edit existing entry

        // todo show group

        // todo show active flag (green mark)

        // todo delete entry when swiped away
    }

    @Override
    protected void onResume() {
        super.onResume();
        stockModelArrayList.clear();
        populateRecyclerView(getBaseContext());
    }

    private void populateRecyclerView(Context context) {
        // empty the existing stocksList
        FileAccess.csvStockList.clear();
        stockModelArrayList.clear();
        FileAccess.csvStockList.clear();
        FileAccess.csvStockList.add(FileAccess.csvStockHeader);
        // check if stocks list file exists, if not create one with header
        FileAccess.stocksListExists(context);
        // now load the existing file
        int records = 0;
        records = FileAccess.loadStocksListV3(context, stockModelArrayList);
        System.out.println("existing records: " + records);

        mAdapter = new RecyclerViewAdapter(stockModelArrayList);
        recyclerView.setAdapter(mAdapter);
    }

    private void populateRecyclerViewOld() {
        StockModel stockModel = new StockModel("IE123", "ETF Europe", true, "");
        stockModelArrayList.add(stockModel);
        stockModel = new StockModel("IE345", "ETF World", true, "");
        stockModelArrayList.add(stockModel);
        stockModel = new StockModel("LU111222333", "ETF Emerging Markets", true, "");
        stockModelArrayList.add(stockModel);

        mAdapter = new RecyclerViewAdapter(stockModelArrayList);
        recyclerView.setAdapter(mAdapter);
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition(); // getAdapterPosition is deprecated
                //final int position = viewHolder.getBindingAdapterPosition();
                //final String item = mAdapter.getData().get(position);
                final StockModel item = mAdapter.getData().get(position);
                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);

                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}