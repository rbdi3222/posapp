package com.pos.minkyu.ryupostapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by minkyu on 2016-06-06.
 */
public class FoodAddDialog extends Dialog {
    Context context;
    EditText foodnameTxt, foodpriceTxt;
    Button add_btn, cancel_btn;

    public FoodAddDialog(Context c, final int menuenum) {
        super(c);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_food);
        this.context = c;

        add_btn = (Button) findViewById(R.id.add_menue_add);
        cancel_btn = (Button) findViewById(R.id.cancel_menue_add);
        foodnameTxt = (EditText) findViewById(R.id.dialog_foodnametxt);
        foodpriceTxt = (EditText) findViewById(R.id.foodprice_txt);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                if(String.valueOf(foodnameTxt.getText()).equals("") || String.valueOf(foodpriceTxt.getText()).equals("")){
                    Toast.makeText(context, "이름과 가격을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    msg.obj = String.valueOf(foodnameTxt.getText());
                    msg.arg1 = menuenum;
                    msg.arg2 = Integer.valueOf(String.valueOf(foodpriceTxt.getText()));

                    MainDialog.FoodAddHandler.sendMessage(msg);
                    dismiss();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
