package com.baima.massagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        et = findViewById(R.id.et);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Intent intent = getIntent();
        int inputType = intent.getIntExtra("inputType", 0);
        if (inputType != 0) {
            et.setInputType(inputType);
        }

        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    editFinish();
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        menu.findItem(R.id.item_save).setTitle("确定");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_save) {
            editFinish();
        }
        return true;
    }

    private void editFinish() {
        String data = et.getText().toString();
        if (TextUtils.isEmpty(data)) {
            //文本型手机号内容可以为空
            //如果 内容为空，如果 内容不是文本型和手机号，返回，
            int inputType = et.getInputType();
            if (inputType != InputType.TYPE_CLASS_TEXT && inputType != InputType.TYPE_CLASS_PHONE) {
                Toast.makeText(this, "你输入的内容为空，请重新输入！", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = getIntent();
        intent.putExtra("inputData", data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
