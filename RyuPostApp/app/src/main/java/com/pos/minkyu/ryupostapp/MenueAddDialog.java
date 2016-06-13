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
public class MenueAddDialog extends Dialog {
    Context context;
    EditText menue_add_text;
    Button add_btn, cancel_btn;

    public MenueAddDialog(Context c) {
        super(c);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_menue);
        this.context = c;
        menue_add_text = (EditText) findViewById(R.id.menue_add_text);
        add_btn = (Button) findViewById(R.id.add_menue_add);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(menue_add_text.getText()).equals("")){
                    Toast.makeText(context, "메뉴이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    Message msg = Message.obtain();
                    msg.obj = String.valueOf(menue_add_text.getText());
                    MainDialog.MenueAddHandler.sendMessage(msg);
                    dismiss();
                }
            }
        });

        cancel_btn = (Button) findViewById(R.id.cancel_menue_add);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainDialog.menueAddDialog.dismiss();
            }
        });
    }
}
