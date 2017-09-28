package com.pontus.wishar.view.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pontus.wishar.R;
import com.pontus.wishar.data.HotSpot;
import com.pontus.wishar.data.HotSpotOption;
import com.pontus.wishar.data.WifiDesc;
import com.pontus.wishar.storage.AccountStorage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NorthBei on 2017/8/31.
 */

public class HotSpotListAdapter extends RecyclerView.Adapter {

    private ArrayList<HotSpot> list;

    public HotSpotListAdapter(ArrayList<HotSpot> list) {
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
        ((HotSpotVH) holder).ssidName.setText(list.get(position).getSsid());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HotSpotVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ssidTest) TextView ssidName;

        public HotSpotVH(View inflatedView) {
            super(inflatedView);
            ButterKnife.bind(this, inflatedView);
            inflatedView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getLayoutPosition();
            new AccountInfoDialog(v.getContext(), list.get(index));
        }
    }

    protected class AccountInfoDialog implements View.OnClickListener {

        @BindView(R.id.input_account) EditText inputAccount;
        @BindView(R.id.input_password) EditText inputPassword;
        @BindView(R.id.spinner) MaterialSpinner spinner;

        private Context context;
        private AlertDialog dialog;
        private String SSID;
        private AccountStorage accountManager;
        private HotSpot wifiHopSpot;

        //http://shawnba.blogspot.tw/2013/06/android-positivebutton-alertdialog.html

        public AccountInfoDialog(Context context, HotSpot wifiHopSpot) {
            this.context = context;
            this.wifiHopSpot = wifiHopSpot;
            this.SSID = wifiHopSpot.getSsid();
            this.accountManager = new AccountStorage(context,SSID);

            initDialog();
        }

        private void initDialog(){
            View dialogView = LayoutInflater.from(context).inflate(R.layout.edit_dialog, null);
            ButterKnife.bind(this, dialogView);

            dialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.account_dialog_title))
                    .setView(dialogView)
                    .setNegativeButton(context.getString(R.string.account_dialog_cancel_btn), null)
                    .setPositiveButton(context.getString(R.string.account_dialog_check_btn), null)
                    .create();

            showSpinner();

            boolean isAccountExist = AccountStorage.isAccountExist(context,SSID);
            if(isAccountExist){
                inputAccount.setText(accountManager.getAccount());
                inputPassword.setText(accountManager.getPassword());
            }

            dialog.show();
            //為了取消click positive button之後dialog直接關閉,把click event重新設定Listener
            //call dialog.show() 之後才能call getButton() ,
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        }

        private void showSpinner(){
            int defaultIndex = 0;
            List<HotSpotOption> options = wifiHopSpot.getCategory();
            int size = options.size();

            //不管今天有幾個postfix，都放進下拉式選單
            String[] list = new String[size];
            for (int i = 0; i < size; i++) {
                String displayStringID = options.get(i).getDisplayStringID();
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
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, list);
//                spinner.setAdapter(adapter);
            spinner.setItems(list);
            spinner.setSelectedIndex(defaultIndex);

            int isVisiable = size > 1? View.VISIBLE : View.GONE;
            spinner.setVisibility(isVisiable);
        }

        private String getStringByResName(String resName) throws NoSuchFieldException, IllegalAccessException {

            Class res= R.string.class;  // 如果是drawable就變成Class res=R.drawable.class
            Field field = res.getField(resName);//name要改成字串名
            int resId = field.getInt(null);

            return context.getString(resId);
            //以上程式碼等於下面這行 但是快很多 http://my2drhapsody.blogspot.tw/2012/10/resources-string-from-name.html
            //int resId = context.getResources().getIdentifier(resName,"string",context.getPackageName());
        }


        @Override
        public void onClick(View v) {
            String account = inputAccount.getText().toString();
            String password = inputPassword.getText().toString();

            if (account.length() == 0 || password.length() == 0) {
                accountManager.removeAccount();
                Toast.makeText(context, context.getText(R.string.account_dialog_error), Toast.LENGTH_SHORT).show();
                return;
            }

            //如果wifi type是wispr , 就拿出spinner選到的index對應到的value並存起來，如果type不是 就存空字串
            String postfix = wifiHopSpot.getType().equals(WifiDesc.WISPr) ? wifiHopSpot.getCategory().get(spinner.getSelectedIndex()).getAccountPostfix() : "";

            //不管有沒有改都重新存檔
            accountManager.setLoginInfo(account,password,postfix);
            dialog.dismiss();
        }
    }
}