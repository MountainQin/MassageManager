package com.baima.massagemanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class PickDateTimeActivity extends AppCompatActivity {

    private DatePicker date_picker;
    private TimePicker time_picker;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date_time);

        initViews();
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
            //获取 选择后的毫秒值 返回
            calendar.set(date_picker.getYear(), date_picker.getMonth(), date_picker.getDayOfMonth(),
                    time_picker.getCurrentHour(), time_picker.getCurrentMinute(), 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Intent intent = getIntent();
            intent.putExtra("timeInMillis", calendar.getTimeInMillis());
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private void initViews() {
        date_picker = findViewById(R.id.date_picker);
        time_picker = findViewById(R.id.time_picker);

        Intent intent = getIntent();
        long timeInMillis = intent.getLongExtra("timeInMillis", System.currentTimeMillis());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        date_picker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        time_picker.setIs24HourView(true);
        time_picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        time_picker.setCurrentMinute(calendar.get(Calendar.MINUTE));

    }
}
