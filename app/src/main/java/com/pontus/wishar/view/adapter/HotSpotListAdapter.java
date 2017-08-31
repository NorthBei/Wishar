package com.pontus.wishar.view.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pontus.wishar.R;
import com.pontus.wishar.data.HotSpot;

import java.util.ArrayList;

/**
 * Created by NorthBei on 2017/8/31.
 */

public class HotSpotListAdapter extends  RecyclerView.Adapter {

    private ArrayList<HotSpot> list;

    public HotSpotListAdapter(ArrayList<HotSpot> list){
        this.list = list;
    }

    @Override
    public HotSpotVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_spot_item, null);
        HotSpotVH viewHolder = new HotSpotVH(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((HotSpotVH)holder).textView.setText(list.get(position).getSsid());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Item " + position + " is clicked.", Toast.LENGTH_SHORT).show();
                customDialog(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HotSpotVH extends RecyclerView.ViewHolder{
        public TextView textView;

        public HotSpotVH(View itemView) {
            super(itemView);
            // 設定 textView 為 item_title 這個 layout 物件
            textView = (TextView) itemView.findViewById(R.id.ssidTest);
        }
    }

    private void customDialog(View v){
        final View item = LayoutInflater.from(v.getContext()).inflate(R.layout.edit_dialog, null);
        new AlertDialog.Builder(v.getContext())
                .setTitle("input")
                .setView(item)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // EditText editText = (EditText) item.findViewById(R.id.edit_text);
                        //Toast.makeText(getApplicationContext(), getString(R.string.hi) + editText.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}