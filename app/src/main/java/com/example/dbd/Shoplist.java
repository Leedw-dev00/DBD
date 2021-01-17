package com.example.dbd;

public class Shoplist {

    String userID;
    String item_Name;
    String price;
    String store_Code;

    public String getUserID(){ return userID; }
    public void setUserID(String userID){this.userID = userID;}

    public String getItem_Name(){ return item_Name; }
    public void setItem_Name(String item_Name){this.item_Name = item_Name;}

    public String getPrice(){ return price; }
    public void setPrice(String price){this.price = price;}

    public String getStore_Code(){ return store_Code; }
    public void setStore_Code(String store_Code){this.store_Code = store_Code;}

    public Shoplist(String userID, String item_Name, String price, String store_Code){
        this.userID = userID;
        this.item_Name = item_Name;
        this.price = price;
        this.store_Code = store_Code;
    }

}
