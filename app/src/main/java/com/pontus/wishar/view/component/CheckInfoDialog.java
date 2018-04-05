package com.pontus.wishar.view.component;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pontus.wishar.R;
import com.pontus.wishar.data.HotSpotData;
import com.pontus.wishar.storage.CheckStorage;

/**
 * Created by NorthBei on 2018/3/30.
 */

public abstract class CheckInfoDialog extends HotSpotInfoDialog {

    private CheckStorage chStorage;
    private String wifiDescName;

    public CheckInfoDialog(Context context, HotSpotData hotSpotData){
        super(context,hotSpotData);
    }

    @Override
    public void init(Context context) {
        wifiDescName = hotSpotData.getWifiDescName();
        chStorage = new CheckStorage(context,wifiDescName);
    }

    @Override
    public String getDialogTitle() {
        return hotSpotData.getSSID();
    }

    @Override
    public MaterialDialog customDialogSettings(MaterialDialog.Builder dialogBuilder) {
        dialogBuilder.checkBoxPromptRes(R.string.check_dialog_msg, chStorage.isChecked(), null);
        return dialogBuilder.build();
    }

    @Override
    public void onButtonClick(MaterialDialog dialog, DialogAction which) {
        boolean isCheck = dialog.isPromptCheckBoxChecked();
        chStorage.setCheckedStatus(isCheck);
        onStatusChange(wifiDescName,isCheck);
    }
}
