package com.example.dbd;

public class Producer {
    String item_Code;
    String item_Name;
    String count;
    String warehouse_Code;

    public String getItem_Code(){ return item_Code; }
    public void setItem_Code(String item_Code){this.item_Code = item_Code;}

    public String getItem_Name(){ return item_Name; }
    public void setItem_Name(String item_Name){this.item_Name = item_Name;}

    public String getCount(){ return count; }
    public void setCount(String count){this.count = count;}

    public String getWarehouse_Code(){ return warehouse_Code; }
    public void setWarehouse_Code(String warehouse_Code){this.warehouse_Code = warehouse_Code;}

    public Producer(String item_Code, String item_Name, String count, String warehouse_Code){
        this.item_Code = item_Code;
        this.item_Name = item_Name;
        this.count = count;
        this.warehouse_Code = warehouse_Code;
    }
}
