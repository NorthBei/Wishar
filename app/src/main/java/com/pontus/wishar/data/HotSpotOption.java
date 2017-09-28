package com.pontus.wishar.data;

import com.google.gson.annotations.SerializedName;

public class HotSpotOption {
    //json file內key為display_stringID的value會對應到到 displayStringID這個變數
    @SerializedName("display_stringID")
    private String displayStringID;
    @SerializedName("account_postfix")
    private String accountPostfix;

    public String getDisplayStringID() {
        return displayStringID;
    }

    public void setDisplayStringID(String display_stringID) {
        this.displayStringID = display_stringID;
    }

    public String getAccountPostfix() {
        return accountPostfix;
    }

    public void setAccountPostfix(String account_postfix) {
        this.accountPostfix = account_postfix;
    }
}