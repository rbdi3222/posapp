package com.pos.minkyu.ryupostapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by minkyu on 2016-05-27.
 */
public class TableSetAdapter extends BaseAdapter {
    static MainDialog mainDialog; // 메인 다이얼로그 담기
    final int NULLINT = 0;
    LayoutInflater inflater = null;
    Context context = null;
    int tablelayout = 0;
    int addlayout = 0;

    public TableSetAdapter(Context c, int tablelayout, int addlayout) {
        this.context = c;
        this.tablelayout = tablelayout;
        this.addlayout = addlayout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return SplashActivity.dbTablePriceInfoArray.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return SplashActivity.dbTablePriceInfoArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (position == SplashActivity.dbTablePriceInfoArray.size()) {
            convertView = inflater.inflate(addlayout, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.dbAdapter.open();
                    MainActivity.dbAdapter.insertIntoPosDB(SplashActivity.dbTablePriceInfoArray.size() + 1, null, null, NULLINT, NULLINT);
                    MainActivity.dbAdapter.close();
                    Message msg = Message.obtain();
                    MainActivity.TableAddHandler.sendMessage(msg);
                }
            });
        } else {
            convertView = inflater.inflate(tablelayout, parent, false);
            final TextView TablePriceTxt = (TextView) convertView.findViewById(R.id.table_price);
            final TextView TableNameTxt = (TextView) convertView.findViewById(R.id.table_name);
            TablePriceTxt.setText(String.valueOf(SplashActivity.dbTablePriceInfoArray.get(position).getSumprice()));
            TableNameTxt.setText(SplashActivity.dbTablePriceInfoArray.get(position).getTablenum() + "번 테이블");
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDialog(TablePriceTxt, SplashActivity.dbTablePriceInfoArray.get(position).getTablenum());
                    mainDialog.show();
                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("삭제");
                    builder.setNeutralButton("테이블비우기",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            MainActivity.dbAdapter.open();
                            MainActivity.dbAdapter.deleteCountClear(SplashActivity.dbTablePriceInfoArray.get(position).getTablenum());
                            MainActivity.dbAdapter.close();
                            Message msg = Message.obtain();
                            MainActivity.TableAddHandler.sendMessage(msg);
                        }
                    });
                    builder.setPositiveButton("테이블삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            MainActivity.dbAdapter.open();
                            MainActivity.dbAdapter.deleteTable(SplashActivity.dbTablePriceInfoArray.size());
                            MainActivity.dbAdapter.close();
                            Message msg = Message.obtain();
                            MainActivity.TableAddHandler.sendMessage(msg);
                            Toast.makeText(context, "맨뒤부터 삭제 됩니다", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.create().show();

                    return false;
                }
            });
        }
        return convertView;
    }

    public void initDialog(TextView TableCostTextView, int tablenum) {
        mainDialog = new MainDialog(context, TableCostTextView, tablenum);
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        WindowManager.LayoutParams params = mainDialog.getWindow().getAttributes();
        params.width = (int) (dm.widthPixels * 9 / 10);
        params.height = (int) (dm.heightPixels * 9 / 10);
        params.dimAmount = 0.8f;
        mainDialog.getWindow().setAttributes(params);
        mainDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}