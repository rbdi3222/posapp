package com.pos.minkyu.ryupostapp;

/**
 * Created by minkyu on 2016-06-12.
 */
public class TablePriceInfo {
    int tablenum;
    int sumprice;
    TablePriceInfo(int tablenum, int sumprice){
        this.tablenum = tablenum;
        this.sumprice = sumprice;
    }
    public int getTablenum() {
        return tablenum;
    }

    public int getSumprice() {
        return sumprice;
    }
}
