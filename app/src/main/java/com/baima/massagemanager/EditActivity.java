package com.baima.massagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("请输入");
        setContentView(R.layout.activity_edit);

        et = findViewById(R.id.et);
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

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
            String s = et.getText().toString();
            if (!TextUtils.isEmpty(s)) {
                Intent intent = getIntent();
                try {
                    Double aDouble = Double.valueOf(s);
                    intent.putExtra("aDouble ", aDouble);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "你输入的内容为空，请重新输入！", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return true;
    }
}
