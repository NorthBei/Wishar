package com.pontus.wishar.view.component;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pontus.wishar.R;
import com.pontus.wishar.data.HotSpotData;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.storage.AccountStorage;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NorthBei on 2018/3/30.
 */

public abstract class AccountInfoDialog extends HotSpotInfoDialog {

    @BindView(R.id.input_account) EditText inputAccount;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.spinner) MaterialSpinner spinner;

    private AccountStorage accountManager;
    private Context context;

    public AccountInfoDialog(Context context, HotSpotData hotSpotData) {
        super(context, hotSpotData);
    }

    @Override
    public void init(Context context) {
        this.context = context;
        this.accountManager = new AccountStorage(context,wifiDesc.getScriptName());
    }

    @Override
    public String getDialogTitle() {
        return hotSpotData.getSSID()+context.getString(R.string.account_dialog_title);
    }

    @Override
    public MaterialDialog customDialogSettings(MaterialDialog.Builder dialogBuilder) {
        MaterialDialog dialog = dialogBuilder.customView(R.layout.edit_dialog,false).build();
        View view = dialog.getCustomView();
        ButterKnife.bind(this, view);

        initSpinner();
        initInputs();

        return dialog;
    }

    @Override
    public void onButtonClick(MaterialDialog dialog, DialogAction which) {
        boolean hasAccount = true;
        String account = inputAccount.getText().toString();
        String password = inputPassword.getText().toString();

        if (account.length() == 0 || password.length() == 0) {
            accountManager.removeAccount();
            hasAccount = false;
            Toast.makeText(context, context.getText(R.string.account_dialog_error), Toast.LENGTH_SHORT).show();
        }
        else{
            //如果wifi type是WISPr , 就拿出spinner選到的index對應到的value並存起來，如果type不是 就存空字串
            String postfix = wifiDesc.getType().equals(WifiDesc.WISPr) ? wifiDesc.getLoginType().get(spinner.getSelectedIndex()).getAccountPostfix() : "";
            //不管有沒有改都重新存檔
            accountManager.setLoginInfo(account,password,postfix);
            dialog.dismiss();
        }
        onStatusChange(wifiDesc.getScriptName(),hasAccount);
    }

    private void initInputs(){
        String wifiDescName = hotSpotData.getWifiDescName();
        boolean isAccountExist = AccountStorage.isAccountExist(context,wifiDescName);
        if(isAccountExist){
            inputAccount.setText(accountManager.getAccount());
            inputPassword.setText(accountManager.getPassword());
        }
    }

    private void initSpinner(){
        int defaultIndex = 0;
        List<WifiDesc.LoginType> options = wifiDesc.getLoginType();
        int size = options.size();

        //不管今天有幾個postfix，都放進下拉式選單
        String[] list = new String[size];
        for (int i = 0; i < size; i++) {
            String displayStringID = options.get(i).getDisplayName();
            try {
                if(options.get(i).getAccountPostfix().equals(accountManager.getPostfix())){
                    defaultIndex = i;
                }
                list[i] = getStringByResName(displayStringID);
            }
            catch (NoSuchFieldException | IllegalAccessException e) {
                //displayStringID但是在string.xml 找不到對應的name，就先用default
                list[i] = "default";
                e.printStackTrace();
            }
        }

        if(size > 0) {
            spinner.setItems(list);
            spinner.setSelectedIndex(defaultIndex);
        }

        int isVisible = size > 1? View.VISIBLE : View.GONE;
        spinner.setVisibility(isVisible);
    }

    private String getStringByResName(String resName) throws NoSuchFieldException, IllegalAccessException {
        Class res= R.string.class;  // 如果是drawable就變成Class res=R.drawable.class
        Field field = res.getField(resName);//name要改成字串名
        int resId = field.getInt(null);

        return context.getString(resId);
        //以上程式碼等於下面這行 但是快很多 http://my2drhapsody.blogspot.tw/2012/10/resources-string-from-name.html
        //int resId = context.getResources().getIdentifier(resName,"string",context.getPackageName());
    }
}
