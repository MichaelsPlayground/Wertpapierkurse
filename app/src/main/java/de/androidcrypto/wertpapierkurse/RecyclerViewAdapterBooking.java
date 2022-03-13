package de.androidcrypto.wertpapierkurse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.androidcrypto.wertpapierkurse.models.BookingModel;

public class RecyclerViewAdapterBooking extends RecyclerView.Adapter<RecyclerViewAdapterBooking.MyViewHolder> {

    //private ArrayList<String> data;
    private ArrayList<BookingModel> data;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mIsinName;
        RelativeLayout relativeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txtTitle);
            mIsinName = itemView.findViewById(R.id.txtIsinName);
        }
    }
/*
    public RecyclerViewAdapter(ArrayList<String> data) {
        this.data = data;
    }
    */
    public RecyclerViewAdapterBooking(ArrayList<BookingModel> data) {
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //holder.mTitle.setText(data.get(position));
        holder.mTitle.setText(data.get(position).getIsin());
        holder.mIsinName.setText(data.get(position).getIsinName());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(BookingModel item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }
    /*
    public void restoreItem(String item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }
     */

    public ArrayList<BookingModel> getData() {
        return data;
    }
    /*
    public ArrayList<String> getData() {
        return data;
    }
     */
}
