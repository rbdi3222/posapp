package com.pos.minkyu.ryupostapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by minkyu on 2016-06-06.
 */
class FoodSetAdapter extends BaseAdapter {

    /*    ArrayList<String> FoodName;
        ArrayList<Integer> FoodCountNum, FoodPrice;*/
    LayoutInflater inflater = null;
    Context context = null;
    int tablelayout = 0;
    int addlayout = 0;
    int tablenum;
    String menuename;
    ArrayList<FoodInfoArray> foodInfoArrays = null;


    public FoodSetAdapter(Context c, int tablelayout, int addlayout, int tablenum, int menuenum) {
        context = c;
        this.tablelayout = tablelayout;
        this.addlayout = addlayout;
        this.tablenum = tablenum;
        if(menuenum != -1) {
            this.menuename = SplashActivity.dbMenueArray.get(menuenum);
        }else{
            menuename = null;
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        foodInfoArrays = new ArrayList<FoodInfoArray>();
        Cursor cursor = MainActivity.dbAdapter.selectFoodInfo(menuename);
        while (cursor.moveToNext()) {
            String foodname = cursor.getString(0);
            int foodprice = cursor.getInt(1);
            int foodcount = 0;
            foodInfoArrays.add(new FoodInfoArray(foodname, foodprice, foodcount));
        }
        cursor.close();
    }

    @Override
    public int getCount() {
        return foodInfoArrays.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return foodInfoArrays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (position == foodInfoArrays.size()) {
            convertView = inflater.inflate(addlayout, parent, false);
            ImageView FoodAddbtn = (ImageView) convertView.findViewById(R.id.menue_add_btn);
            FoodAddbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SplashActivity.dbMenueArray.size() == 0){
                        Toast.makeText(context, "메뉴를 추가해주세요", Toast.LENGTH_SHORT).show();
                    }else {
                        FoodAddDialog foodAddDialog = new FoodAddDialog(context, SplashActivity.dbMenueArray.indexOf(menuename));
                        foodAddDialog.show();
                    }
                }
            });
        } else {
            convertView = inflater.inflate(tablelayout, parent, false);
            final TextView foodNameTextView = (TextView) convertView.findViewById(R.id.foodname_txt);
            final TextView foodPriceTextView = (TextView) convertView.findViewById(R.id.price_text);
            final TextView foodCountTextView = (TextView) convertView.findViewById(R.id.foodcount_txt);
            final LinearLayout FoodMinusCountBtn = (LinearLayout) convertView.findViewById(R.id.minus_btn);
            final LinearLayout FoodAddCountBtn = (LinearLayout) convertView.findViewById(R.id.add_btn);

            foodNameTextView.setText(String.valueOf(foodInfoArrays.get(position).getFoodname()));
            foodPriceTextView.setText(String.valueOf(foodInfoArrays.get(position).getFoodprice()));

            Cursor cursor = MainActivity.dbAdapter.selectCountInfo(tablenum, menuename, foodInfoArrays.get(position).getFoodname());
            try{
                foodCountTextView.setText(String.valueOf(cursor.getInt(0)));
            }catch (CursorIndexOutOfBoundsException e){
                MainActivity.dbAdapter.insertIntoPosDB(tablenum, menuename, foodInfoArrays.get(position).getFoodname(), foodInfoArrays.get(position).getFoodprice(), 0);
                foodCountTextView.setText(String.valueOf(0));
            }
            cursor.close();

            FoodSumPrice(tablenum);

            FoodMinusCountBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.valueOf(String.valueOf(foodCountTextView.getText()));

                    if (count == 0) {
                       Toast.makeText(context,  "더 이상 뺄 수 없습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        count--;
                        MainActivity.dbAdapter.updateCountInfo(tablenum, menuename, foodInfoArrays.get(position).getFoodname(), count);
                        FoodSumPrice(tablenum);
                    }
                    foodCountTextView.setText(String.valueOf(count));
                }
            });

            FoodAddCountBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.valueOf(String.valueOf(foodCountTextView.getText()));
                    count++;
                    MainActivity.dbAdapter.updateCountInfo(tablenum, menuename, foodInfoArrays.get(position).getFoodname(), count);
                    FoodSumPrice(tablenum);
                    foodCountTextView.setText(String.valueOf(count));
                }
            });
        }


        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "롱클릭리스너", Toast.LENGTH_SHORT).show();
                ArrayList<String> deleteFoodArrays = new ArrayList<String>();
                deleteFoodArrays.add(menuename);
                deleteFoodArrays.add(foodInfoArrays.get(position).getFoodname());

                Message msg = Message.obtain();
                msg.obj = deleteFoodArrays;
                MainDialog.FoodDeleteHandler.sendMessage(msg);

                return false;
            }
        });
        return convertView;
    }



    public void FoodSumPrice(int tablenum) {
        Cursor cursor = MainActivity.dbAdapter.selectSumpriceInfo(tablenum);
        int sumprice = 0;
        while (cursor.moveToNext()) {
            sumprice = sumprice + cursor.getInt(0);
        }
        cursor.close();
        MainDialog.sumPriceTxt.setText(sumprice + "");

        cursor = MainActivity.dbAdapter.selectPreviewpriceInfo(tablenum);
        String previewStr = "";
        while (cursor.moveToNext()) {
            String foodname = cursor.getString(0);
            int foodcount = cursor.getInt(1);

            previewStr = previewStr +  "(" + foodname + " x " + foodcount + ") + ";
        }
        cursor.close();
        MainDialog.previewPriceTxt.setText(previewStr);

    /*    String str = "";
        if (FOODSTATENUM) {
         //   FoodInfoArray.get(position).setCount(FoodInfoArray.get(position).getCount() + 1);
            FoodSumCost = FoodSumCost + FoodInfoArray.get(position).getPrice();
        } else {
            if (FoodInfoArray.get(position).getCount() <= 0) {
                Toast.makeText(context, "더 이상 뺄 수 없습니다", Toast.LENGTH_LONG).show();
            } else {
                FoodInfoArray.get(position).setCount(FoodInfoArray.get(position).getCount() - 1);
                FoodSumCost = FoodSumCost - FoodInfoArray.get(position).getPrice();
            }
        }
        MainDialog.sum_price_text.setText(FoodSumCost + "");

        for (int i = 0; i < FoodInfoArray.size(); i++) {
            if (FoodInfoArray.get(i).getCount() != 0) {
                str = str + FoodInfoArray.get(i).getName() + FoodInfoArray.get(i).getCount() + "개 + ";
            }
        } MainDialog.cost_list_view_text.setText(str);*/
    }
}
