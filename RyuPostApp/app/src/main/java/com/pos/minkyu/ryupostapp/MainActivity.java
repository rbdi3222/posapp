package com.pos.minkyu.ryupostapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends Activity {
    static DataBaseHelper dbAdapter;
    static Handler TableAddHandler;
    TableSetAdapter adapter;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priceview);
        dbAdapter = new DataBaseHelper(this);
        startActivity(new Intent(this, SplashActivity.class));
        gridView = (GridView) findViewById(R.id.main_gridView);

        TableAddHandler = new Handler() {
            public void handleMessage(Message msg) {
                dbAdapter.open();
                SplashActivity.dbTablePriceInfoArray.clear();
                Cursor cursor = MainActivity.dbAdapter.selectAllSumpriceInfo();
                while (cursor.moveToNext()) {
                    int tablenum = cursor.getInt(0);
                    int sumprice = cursor.getInt(1);
                    SplashActivity.dbTablePriceInfoArray.add(new TablePriceInfo(tablenum, sumprice));
                }
                cursor.close();
                dbAdapter.close();

                adapter = new TableSetAdapter(MainActivity.this, R.layout.priceview_griditem, R.layout.priceview_item_gridadd);
                gridView.setAdapter(adapter);
            }
        };
    }
}
