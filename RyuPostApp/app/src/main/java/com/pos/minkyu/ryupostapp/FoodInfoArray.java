package com.pos.minkyu.ryupostapp;

/**
 * Created by minkyu on 2016-06-11.
 */
public class FoodInfoArray {
    String foodname;
    int foodprice;
    int foodcount;

    FoodInfoArray(String foodname, int foodprice, int foodcount){
        this.foodname = foodname;
        this.foodprice = foodprice;
        this.foodcount = foodcount;
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
