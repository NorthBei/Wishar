package com.pontus.wishar.data;

import android.content.Context;

import com.pontus.wishar.Constants;
import com.pontus.wishar.storage.AssetsStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by NorthBei on 2018/3/30.
 */

public class HotSpotListBuilder {

    private List<DescCorr> descCorrList;
    private Map<String,DescSupport> wifiDescSupportInfo = new TreeMap<>();
    private Context context;

    public HotSpotListBuilder(Context context, List<DescCorr> descCorrList){
        this.context = context;
        this.descCorrList = descCorrList;
        descCorrList2Map();
        initHasRecord();
    }
    
    private void descCorrList2Map(){
        for (DescCorr descCorr : descCorrList) {

            String wifiDescName = descCorr.getWifiDescFileName();
            String ssid = descCorr.getSsid();

            if(!wifiDescSupportInfo.containsKey(wifiDescName)){
                wifiDescSupportInfo.put(wifiDescName,new DescSupport());
            }

            wifiDescSupportInfo.get(wifiDescName).addSsid(ssid);
        }
    }
    
    private void initHasRecord(){
        String[] records = AssetsStorage.getRecords(context);
        for (String record : records) {
            record = record.replace(".xml","");
            if(!record.startsWith(Constants.WIFI_DESC_SCRIPT_PREFIX))
                continue;

            wifiDescSupportInfo.get(record).setHasRecord(true);
        }
    }

    public List<String> getRelativeSsidList(String wifiDescName){
        return wifiDescSupportInfo.get(wifiDescName).getSsidList();
    }

    public List<HotSpotData> getHotSpotDataList(){
        ArrayList<HotSpotData> dataList = new ArrayList<>();
        for (DescCorr descCorr : descCorrList) {
            boolean hasRecord =  wifiDescSupportInfo.get(descCorr.getWifiDescFileName()).hasRecord();
            dataList.add(new HotSpotData(descCorr,hasRecord));
        }
        return dataList;
    }

    public void update(String wifiDescName, boolean hasRecord) {
        wifiDescSupportInfo.get(wifiDescName).setHasRecord(hasRecord);
    }

    private class DescSupport {
        private List<String> ssidList;
        private boolean hasRecord;

        public DescSupport(){
            ssidList = new ArrayList<>();
            hasRecord = false;
        }

        public List<String> getSsidList(){
            return ssidList;
        }

        public void addSsid(String ssid){
            ssidList.add(ssid);
        }

        public boolean hasRecord() {
            return hasRecord;
        }

        public void setHasRecord(boolean hasRecord) {
            this.hasRecord = hasRecord;
        }
    }
}
