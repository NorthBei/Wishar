package com.pontus.wishar.view.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pontus.wishar.R;
import com.pontus.wishar.data.HotSpotData;
import com.pontus.wishar.data.HotSpotListBuilder;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.data.WifiDescCache;
import com.pontus.wishar.view.component.AccountInfoDialog;
import com.pontus.wishar.view.component.CheckInfoDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NorthBei on 2017/8/31.
 */

public class HotSpotListAdapter extends RecyclerView.Adapter {

    private HotSpotListBuilder builder;
    private List<HotSpotData> dataList;

    public HotSpotListAdapter(HotSpotListBuilder builder) {
        this.builder = builder;
        this.dataList = builder.getHotSpotDataList();
    }

    @Override
    public HotSpotVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_spot_item, null);
        return new HotSpotVH(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((HotSpotVH) holder).setData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void renderDiff(View refView, String wifiDescName, boolean hasRecord){
        builder.update(wifiDescName,hasRecord);
        List<HotSpotData> newList = builder.getHotSpotDataList();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CalDiff(dataList, newList), true);
        dataList = newList;
        diffResult.dispatchUpdatesTo(this);

        List<String> relativeSsidList = builder.getRelativeSsidList(wifiDescName);
        if(relativeSsidList.size() > 1){
            Snackbar.make(refView, "資料與"+relativeSsidList.toString()+"連動", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public class HotSpotVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ssid) TextView ssidName;
        @BindView(R.id.check) ImageView check;
        private HotSpotData data;
        private View view;

        public HotSpotVH(View inflatedView) {
            super(inflatedView);
            view = inflatedView;
            ButterKnife.bind(this, inflatedView);
            inflatedView.setOnClickListener(this);
        }

        public void setData(HotSpotData data){
            this.data = data;
            ssidName.setText(data.getSSID());
            int display = data.hasRecord() ? View.VISIBLE : View.INVISIBLE;
            check.setVisibility(display);
        }

        @Override
        public void onClick(View v) {
            String wifiDescName = data.getWifiDescName();
            Context context = v.getContext();
            WifiDesc wifiDesc = WifiDescCache.getCache().getWifiDesc(context,wifiDescName);
            if(wifiDesc.isNeedLogin()) {
                new AccountInfoDialog(context, data) {
                    @Override
                    public void onStatusChange(String wifiDescName, boolean hasRecord) {
                        renderDiff(view,wifiDescName, hasRecord);
                    }
                };
            }
            else {
                new CheckInfoDialog(context, data) {
                    @Override
                    public void onStatusChange(String wifiDescName, boolean hasRecord) {
                        renderDiff(view,wifiDescName,hasRecord);
                    }
                };
            }
        }
    }

    private class CalDiff extends DiffUtil.Callback {

        private List<HotSpotData> oldList;
        private List<HotSpotData> newList;

        public CalDiff(List<HotSpotData> oldList, List<HotSpotData> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return newList.get(newItemPosition).getSSID().equals(oldList.get(oldItemPosition).getSSID());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return newList.get(newItemPosition).hasRecord() == oldList.get(oldItemPosition).hasRecord();
        }
    }
}