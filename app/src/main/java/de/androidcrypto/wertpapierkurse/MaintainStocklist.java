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

public class MaintainStocklist extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    ConstraintLayout constraintLayout;
    ArrayList<StockModelV2> stockModelArrayList = new ArrayList<>();

    Intent addStockIntent;
    Intent startStockMovementActivityIntent;

    // steuerung des intent verhaltens
    String choosenDate = "";
    String returnToActivity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_stocklist);

        recyclerView = findViewById(R.id.recyclerView);
        constraintLayout = findViewById(R.id.coordinatorLayout);

        addStockIntent = new Intent(MaintainStocklist.this, AddStock.class);
        startStockMovementActivityIntent = new Intent(MaintainStocklist.this, StockMovement.class);

        populateRecyclerView(getBaseContext());
        enableSwipeToDeleteAndUndo();

        Bundle extras = getIntent().getExtras();
        System.out.println("get bundles in MaintainStocklist");
        if (extras != null) {
            System.out.println("extras not null");
            returnToActivity = (String) getIntent().getSerializableExtra("returnToActivity");
            choosenDate = (String) getIntent().getSerializableExtra("choosenDate");
        }


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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                System.out.println("click position: " + position);
                String isin = stockModelArrayList.get(position).getIsin();
                String symbolYahooApi = stockModelArrayList.get(position).getSymbolYahooApi();
                System.out.println("The selected isin is : " + position + " this: " + isin);
                Bundle bundle = new Bundle();
                bundle.putString("selectedIsin", isin);
                bundle.putString("selectedSymbolYahooApi", symbolYahooApi);
                bundle.putString("choosenDate", choosenDate);
                bundle.putString("returnToActivity", returnToActivity);
                /// todo hardcoded activity name
                if (returnToActivity.equals("StockMovement")) {
                    startStockMovementActivityIntent.putExtras(bundle);
                    startActivity(startStockMovementActivityIntent);
                }


            }

            @Override
            public void onLongClick(View view, int position) {
                System.out.println("long click position: " + position);
            }
        }));

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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition(); // getAdapterPosition is deprecated
                //final int position = viewHolder.getBindingAdapterPosition();
                //final String item = mAdapter.getData().get(position);
                final StockModelV2 item = mAdapter.getData().get(position);
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