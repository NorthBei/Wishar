package com.pontus.wishar.view.component;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pontus.wishar.R;
import com.pontus.wishar.data.HotSpotData;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.data.WifiDescCache;

/**
 * Created by NorthBei on 2018/3/30.
 */

public abstract class HotSpotInfoDialog {

    protected WifiDesc wifiDesc;
    protected HotSpotData hotSpotData;

    public HotSpotInfoDialog(Context context, HotSpotData hotSpotData){
        this.hotSpotData = hotSpotData;
        String wifiDescName = hotSpotData.getWifiDescName();
        wifiDesc = WifiDescCache.getCache().getWifiDesc(context,wifiDescName);
        init(context);
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(context)
                .title(getDialogTitle())
                .positiveText(R.string.dialog_check_btn)
                .negativeText(R.string.dialog_cancel_btn)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        if(DialogAction.NEGATIVE.equals(which)){
                            return;
                        }
                        onButtonClick(dialog,which);
                    }
                });

        customDialogSettings(dialogBuilder).show();
    }

    public abstract String getDialogTitle();

    public abstract MaterialDialog customDialogSettings(MaterialDialog.Builder dialogBuilder);

    public abstract void init(Context context);

    public abstract void onButtonClick(MaterialDialog dialog, DialogAction which);

    public abstract void onStatusChange(String wifiDescName,boolean hasRecord);
}
