package com.pontus.wishar.data;

import com.google.gson.annotations.SerializedName;

public class HotSpotOption {
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

    public String getAccount_postfix() {
        return accountPostfix;
    }

    public void setAccount_postfix(String account_postfix) {
        this.accountPostfix = account_postfix;
    }
}