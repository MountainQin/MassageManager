package com.baima.massagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.baima.massagemanager.util.DatePickerUtil;

import java.util.Calendar;

public class PickDateActivity extends AppCompatActivity {

    public static final String START_TIME_IN_MILLIS = "startTimeInMillis";
    public static final String END_TIME_IN_MILLIS = "endTimeInMillis";

    private DatePicker dp_start;
    private DatePicker dp_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);

        initViews();
    }

    private void initViews() {
        dp_start = findViewById(R.id.dp_start);
        dp_end = findViewById(R.id.dp_end);

        Intent intent = getIntent();
        long startTimeInMillis = intent.getLongExtra(PickDateActivity.START_TIME_IN_MILLIS, 0);
        long endTimeInMillis = intent.getLongExtra(PickDateActivity.END_TIME_IN_MILLIS, 0);

        DatePickerUtil.updateDate(dp_start, startTimeInMillis);
        DatePickerUtil.updateDate(dp_end, endTimeInMillis);

        //下面的方式手动输入的时候不会调用onDateChange
//        dp_start.init(int year, int monthOfYear, int dayOfMonth, OnDateChangedListener onDateChangedListener) -  void
//        dp_end.init(int year, int monthOfYear, int dayOfMonth, OnDateChangedListen… -  void/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        menu.findItem(R.id.item_save).setTitle("确定");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                Intent intent = getIntent();
                Calendar calendar = Calendar.getInstance();
                //清除焦点才能获取 到输入的内容
                dp_start.clearFocus();
                calendar.set(
                        dp_start.getYear(), dp_start.getMonth(), dp_start.getDayOfMonth(),
                        0, 0, 0
                );
                calendar.set(Calendar.MILLISECOND, 0);
                long startTimeInMillis = calendar.getTimeInMillis();

                dp_end.clearFocus();
                calendar.set(dp_end.getYear(), dp_end.getMonth(), dp_end.getDayOfMonth(),
                        0, 0, 0);
                long endTimeInMillis = calendar.getTimeInMillis();

                intent.putExtra(END_TIME_IN_MILLIS, endTimeInMillis);
                intent.putExtra(START_TIME_IN_MILLIS, startTimeInMillis);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return true;
    }
}
