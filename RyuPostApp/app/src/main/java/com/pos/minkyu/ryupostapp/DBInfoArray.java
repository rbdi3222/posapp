package com.pos.minkyu.ryupostapp;

/**
 * Created by minkyu on 2016-06-07.
 */
public class DBInfoArray {
    int tablenum;
    String menuename;
    String foodname;
    int foodprice;
    int foodcount;

    public DBInfoArray(int tablenum, String menuename, String foodname, int foodprice, int foodcount){
        this.tablenum = tablenum;
        this.menuename = menuename;
        this.foodname = foodname;
        this.foodprice = foodprice;
        this.foodcount = foodcount;
    }

    public int getTablenum() {
        return tablenum;
    }

    public String getMenuename() {
        return menuename;
    }

    public String getFoodname() {
        return foodname;
    }

    public int getFoodprice() {
        return foodprice;
    }

    public int getFoodcount() {
        return foodcount;
    }
}
