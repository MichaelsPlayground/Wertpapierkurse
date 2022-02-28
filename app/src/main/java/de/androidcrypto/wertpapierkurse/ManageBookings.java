package de.androidcrypto.wertpapierkurse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ManageBookings extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapterBooking mAdapter;
    ConstraintLayout constraintLayout;
    ArrayList<BookingModel> bookingModelArrayList = new ArrayList<>();

    Intent addBookingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        recyclerView = findViewById(R.id.rvBookingList);
        constraintLayout = findViewById(R.id.bookingListLayout);

        // todo change class to AddBooking
        addBookingIntent = new Intent(ManageBookings.this, AddStock.class);

        //populateRecyclerView(getBaseContext());
        populateRecyclerViewOld();
        enableSwipeToDeleteAndUndo();


        FloatingActionButton fab = findViewById(R.id.fabAddBooking);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(addBookingIntent);
                /*
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //populateRecyclerView(getBaseContext());
        populateRecyclerViewOld();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition(); // getAdapterPosition is deprecated
                //final int position = viewHolder.getBindingAdapterPosition();
                //final String item = mAdapter.getData().get(position);
                final BookingModel item = mAdapter.getData().get(position);
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

    private void populateRecyclerViewOld() {
        BookingModel bookingModel = new BookingModel("IE123", "ETF Europe", "2022-01-05", "2022", "234", "buy", "9988,22", "finanzen", "", "", true);
        bookingModelArrayList.add(bookingModel);
        bookingModel = new BookingModel("IE234", "ETF World", "2022-01-08", "2022", "3", "buy", "413,88", "finanzen", "", "", true);
        bookingModelArrayList.add(bookingModel);
        bookingModel = new BookingModel("IE345", "ETF Dax", "2022-01-09", "2022", "5", "buy", "734,65", "finanzen", "", "", true);
        bookingModelArrayList.add(bookingModel);
        bookingModel = new BookingModel("IE123", "ETF Europe", "2022-01-15", "2022", "238", "buy", "9712,02", "finanzen", "", "", true);
        bookingModelArrayList.add(bookingModel);

        mAdapter = new RecyclerViewAdapterBooking(bookingModelArrayList);
        recyclerView.setAdapter(mAdapter);
    }
}