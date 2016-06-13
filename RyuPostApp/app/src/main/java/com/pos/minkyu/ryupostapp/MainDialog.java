package com.pos.minkyu.ryupostapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by minkyu on 2016-05-27.
 */
public class MainDialog extends Dialog {
    static MenueAddDialog menueAddDialog;
    static Handler MenueAddHandler;
    static Handler FoodAddHandler;
    static Handler FoodDeleteHandler;
    ArrayList<DBInfoArray> dbInfoArray = new ArrayList<DBInfoArray>();
    final int MAXUPPERMENUENUM = 6;
    final int NULLINT = 0;
    Context context;
    FoodSetAdapter adapter = null;
    static TextView sumPriceTxt;
    static TextView previewPriceTxt;
    ImageView saveBtn, closeBtn, menueAddBtn;
    ListView foodList;
    int tablenum = 0;
    int menuenum = 0;
    final TextView menue_txt[] = new TextView[MAXUPPERMENUENUM];
    final LinearLayout menue_layout[] = new LinearLayout[MAXUPPERMENUENUM];
    int menueTextID[] = {R.id.menue_add_text01, R.id.menue_add_text02, R.id.menue_add_text03, R.id.menue_add_text04, R.id.menue_add_text05, R.id.menue_add_text06};
    int menueLayoutID[] = {R.id.menue_layout01, R.id.menue_layout02, R.id.menue_layout03, R.id.menue_layout04, R.id.menue_layout05, R.id.menue_layout06};

    public MainDialog(final Context context, final TextView tablePriceTxt,final int tablenum) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_main);
        this.context = context;
        this.tablenum = tablenum;
        previewPriceTxt = (TextView) findViewById(R.id.preview_price);
        sumPriceTxt = (TextView) findViewById(R.id.sum_price);
        menueAddBtn= (ImageView) findViewById(R.id.add_menue_btn);
        closeBtn = (ImageView) findViewById(R.id.close_dialog_btn);
        saveBtn = (ImageView) findViewById(R.id.save_dialog_btn);

        MainActivity.dbAdapter.open();
        MainActivity.dbAdapter.BeginTransaction();
        MenueAdd();

        if(SplashActivity.dbMenueArray.size() != 0){
            FoodUpdate(tablenum, menuenum); // 초기 메뉴
        }else{
            FoodUpdate(tablenum, -1);
        }
        menue_txt[0].setTextColor(Color.rgb(192,192,192));

        menueAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SplashActivity.dbMenueArray.size() == MAXUPPERMENUENUM) {
                    Toast.makeText(context, "더 이상 추가하실 수 없습니다", Toast.LENGTH_LONG).show();
                } else {
                    menueAddDialog = new MenueAddDialog(context);
                    menueAddDialog.show();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbAdapter.SetTransactionSuccessful();
                MainActivity.dbAdapter.EndTransaction();
                MainActivity.dbAdapter.close();
                Message msg = Message.obtain();
                MainActivity.TableAddHandler.sendMessage(msg);
                dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbAdapter.EndTransaction();
                MainActivity.dbAdapter.close();
                dismiss();
            }
        });

        MenueAddHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj != null) {
                    String menuename = (String)msg.obj;
                    MainActivity.dbAdapter.insertIntoPosDB(tablenum, menuename, null, NULLINT, NULLINT);
                    SplashActivity.dbMenueArray.add(menuename);
                    FoodUpdate(tablenum, menuenum);
                   // SplashActivity.dbtableArray.add(new DBInfoArray(tablenum, menuename, ));
                    MenueAdd();
                }
            }
        };

        FoodAddHandler = new Handler() {
            public void handleMessage(Message msg) {
                int menuenum = msg.arg1;
                int price = msg.arg2;
                String name = (String) msg.obj;
                MainActivity.dbAdapter.insertIntoPosDB(tablenum, SplashActivity.dbMenueArray.get(menuenum), name, price, 0);
                SplashActivity.dbFoodArray.add(new FoodInfoArray(name, price, 0));
                FoodUpdate(tablenum, menuenum);
            }
        };
        FoodDeleteHandler = new Handler() {
            public void handleMessage(Message msg) {
                ArrayList<String> foodDeleteArray = (ArrayList<String>) msg.obj;
                MainActivity.dbAdapter.deleteFoodInfo(foodDeleteArray.get(0), foodDeleteArray.get(1));
                FoodUpdate(tablenum, menuenum);
            }
        };
    }

    public void FoodUpdate(int tablenum, int menuenum){
        adapter = new FoodSetAdapter(context, R.layout.dialog_main_listitem, R.layout.dialog_main_listitem_add, tablenum, menuenum);
        foodList = (ListView) findViewById(R.id.menue_list);
        foodList.setAdapter(adapter);
        foodList.setDivider(new ColorDrawable(Color.LTGRAY));
        foodList.setDividerHeight(5);

    }

    public void MenueAcceptInfo(){
        SplashActivity.dbMenueArray.clear();
        Cursor cursor = MainActivity.dbAdapter.selectMenueInfo();
        while (cursor.moveToNext()) {
            String menuename = cursor.getString(0);
            SplashActivity.dbMenueArray.add(menuename);
        }
        cursor.close();
    }

    public void MenueArrayView(int i){
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 7 / 24);
        if (i < SplashActivity.dbMenueArray.size()) {
            menue_txt[i].setText(SplashActivity.dbMenueArray.get(i));
            menue_txt[i].setWidth(width);
            menue_layout[i].setPadding((int) (width / 20), (int) (width / 20), 0, (int) (width / 20));
        } else {
            menue_txt[i].setWidth(0);
            menue_layout[i].setPadding(0,0,0,0);
            menue_txt[i].setText(null);
        }
    }

    public void MenueAdd() {
        MenueAcceptInfo();

        for (int i = 0; i < MAXUPPERMENUENUM; i++) {
            menue_txt[i] = (TextView) findViewById(menueTextID[i]);
            menue_layout[i] = (LinearLayout) findViewById(menueLayoutID[i]);

            MenueArrayView(i);
            //  if(i % 2 == 1) menue_txt[i].setBackgroundColor(Color.rgb(255,255,126));
            menue_layout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < MAXUPPERMENUENUM; i++) {
                        menue_txt[i].setTextColor(Color.BLACK);
                    }
                    switch (v.getId()) {
                        case R.id.menue_layout01:
                            menue_txt[0].setTextColor(Color.rgb(192, 192, 192));
                            menuenum = 0;
                            FoodUpdate(tablenum, menuenum);
                            break;
                        case R.id.menue_layout02:
                            menue_txt[1].setTextColor(Color.rgb(192, 192, 192));
                            menuenum = 1;
                            FoodUpdate(tablenum, menuenum);
                            break;
                        case R.id.menue_layout03:
                            menue_txt[2].setTextColor(Color.rgb(192, 192, 192));
                            menuenum = 2;
                            FoodUpdate(tablenum, menuenum);
                            break;
                        case R.id.menue_layout04:
                            menue_txt[3].setTextColor(Color.rgb(192, 192, 192));
                            menuenum = 3;
                            FoodUpdate(tablenum, menuenum);
                            break;
                        case R.id.menue_layout05:
                            menue_txt[4].setTextColor(Color.rgb(192, 192, 192));
                            menuenum = 4;
                            FoodUpdate(tablenum, menuenum);
                            break;
                        case R.id.menue_layout06:
                            menue_txt[5].setTextColor(Color.rgb(192, 192, 192));
                            menuenum = 5;
                            FoodUpdate(tablenum, menuenum);
                            break;
                    }
                }
            });
            menue_layout[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    switch (v.getId()) {
                        case R.id.menue_layout01:
                            MainActivity.dbAdapter.deleteMenue(SplashActivity.dbMenueArray.get(0));
                            MenueAdd();
                            break;
                        case R.id.menue_layout02:
                            MainActivity.dbAdapter.deleteMenue(SplashActivity.dbMenueArray.get(1));
                            MenueAdd();
                            break;
                        case R.id.menue_layout03:
                            MainActivity.dbAdapter.deleteMenue(SplashActivity.dbMenueArray.get(2));
                            MenueAdd();
                            break;
                        case R.id.menue_layout04:
                            MainActivity.dbAdapter.deleteMenue(SplashActivity.dbMenueArray.get(3));
                            MenueAdd();
                            break;
                        case R.id.menue_layout05:
                            MainActivity.dbAdapter.deleteMenue(SplashActivity.dbMenueArray.get(4));
                            MenueAdd();
                            break;
                        case R.id.menue_layout06:
                            MainActivity.dbAdapter.deleteMenue(SplashActivity.dbMenueArray.get(5));
                            MenueAdd();
                            break;
                    }
                    return false;
                }
            });
        }
    }
}

