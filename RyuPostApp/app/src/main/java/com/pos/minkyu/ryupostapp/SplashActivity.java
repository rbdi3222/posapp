package com.pos.minkyu.ryupostapp;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by minkyu on 2016-05-10.
 */
public class SplashActivity extends Activity {

    static ArrayList<DBInfoArray> dbInfoArray = null;
    static ArrayList<String> dbMenueArray = null;
    static ArrayList<FoodInfoArray> dbFoodArray = null;
    static ArrayList<TablePriceInfo> dbTablePriceInfoArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dbInfoArray = new ArrayList<DBInfoArray>();
        dbMenueArray = new ArrayList<String>();
        dbFoodArray = new ArrayList<FoodInfoArray>();
        dbTablePriceInfoArray = new ArrayList<TablePriceInfo>();

        MainActivity.dbAdapter.open();
        Cursor cursor = MainActivity.dbAdapter.seletAllPosDB();
        while (cursor.moveToNext()) {
            int tablenum = cursor.getInt(1);
            String menuename = cursor.getString(2);
            String foodname = cursor.getString(3);
            int foodprice = cursor.getInt(4);
            int foodcount = cursor.getInt(5);
            dbInfoArray.add(new DBInfoArray(tablenum, menuename, foodname, foodprice, foodcount));
        }
        cursor.close();
        MainActivity.dbAdapter.close();

        Message msg = Message.obtain();
        MainActivity.TableAddHandler.sendMessage(msg);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

}

