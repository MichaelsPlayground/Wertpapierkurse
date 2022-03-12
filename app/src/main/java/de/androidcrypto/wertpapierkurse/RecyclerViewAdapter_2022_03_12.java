package de.androidcrypto.wertpapierkurse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter_2022_03_12 extends RecyclerView.Adapter<RecyclerViewAdapter_2022_03_12.MyViewHolder> {

    //private ArrayList<String> data;
    private ArrayList<StockModelV2> data;

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
    public RecyclerViewAdapter_2022_03_12(ArrayList<StockModelV2> data) {
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

    public void restoreItem(StockModelV2 item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }
    /*
    public void restoreItem(String item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }
     */

    public ArrayList<StockModelV2> getData() {
        return data;
    }
    /*
    public ArrayList<String> getData() {
        return data;
    }
     */
}
