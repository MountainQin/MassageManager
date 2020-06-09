package com.baima.massagemanager;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CURRENT_MONTH_TIME_LATER = 1;
    private TextView tv_name;
    private EditText et_customer_name;
    private TextView tv_current_month_time;
    private TextView tv_current_month_time_later;
    private EditText et_remark;
    private Staff staff;
    private long timeInMillis;
    private TextView tv_date_time;
    private long baseTimeInMillis;
    private TextView tv_work_time;
    private double workTime;
    private double currentMonthTimeLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("记钟");
        setContentView(R.layout.activity_record);

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_work_time:
                showSelectTimeMenu();
                break;
            case R.id.tv_current_month_time_later:
                Intent intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, CURRENT_MONTH_TIME_LATER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CURRENT_MONTH_TIME_LATER:
                    String inputData = data.getStringExtra("inputData");
                    currentMonthTimeLater = Double.valueOf(inputData);
                    tv_current_month_time_later.setText("= " + StringUtil.doubleTrans(currentMonthTimeLater));
                    break;
                case R.id.tv_date_time:
                    timeInMillis = data.getLongExtra("timeInMillis", timeInMillis);
                    tv_date_time.setText(new Date(timeInMillis).toLocaleString());
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_save) {
            saveData();
        }
        return true;
    }

    private void initViews() {
        tv_name = findViewById(R.id.tv_name);
        et_customer_name = findViewById(R.id.et_customer_name);
        tv_current_month_time = findViewById(R.id.tv_current_month_time);
        tv_work_time = findViewById(R.id.tv_work_time);
        tv_current_month_time_later = findViewById(R.id.tv_current_month_time_later);
        tv_date_time = findViewById(R.id.tv_date_time);
        et_remark = findViewById(R.id.et_remark);

//设置时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        baseTimeInMillis = calendar.getTimeInMillis();

        Intent intent = getIntent();
        long staffId = intent.getLongExtra("staffId", 0);
        List<Staff> staffList = LitePal.where("id=?", String.valueOf(staffId)).find(Staff.class);
        if (staffList.size() > 0) {
            staff = staffList.get(0);
            tv_name.setText(staff.getNumber() + "号 " + staff.getName());
            double currentMonthTime = staff.getHoursOfCurrentMonth();
            tv_current_month_time.setText(StringUtil.doubleTrans(currentMonthTime));
            workTime = 1;

            refreshData();
        }

        tv_work_time.setOnClickListener(this);
        tv_current_month_time_later.setOnClickListener(this);
        tv_date_time.setOnClickListener(this);
    }

    //选择时间的菜单
    private void showSelectTimeMenu() {
        PopupMenu popupMenu = new PopupMenu(this, tv_work_time, Gravity.CENTER);
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < 20; i++) {
            double time = (i + 1) * 0.5;
            menu.add(0, i, 0, StringUtil.doubleTrans(time) + "小时");
        }
        popupMenu.getMenuInflater().inflate(R.menu.select_time, menu);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                workTime = (item.getItemId() + 1) * 0.5;
                refreshData();
                return true;
            }
        });
    }

    private void refreshData() {
        currentMonthTimeLater = staff.getHoursOfCurrentMonth() + workTime;
        timeInMillis = (long) (baseTimeInMillis - workTime * 1000 * 60 * 60);
        tv_work_time.setText("+ " + StringUtil.doubleTrans(workTime) + "小时");
        tv_current_month_time_later.setText("= " + StringUtil.doubleTrans(currentMonthTimeLater));
        tv_date_time.setText(new Date(timeInMillis).toLocaleString());
    }

    //保存数据
    private void saveData() {
        String customerName = et_customer_name.getText().toString().trim();
        String remark = et_remark.getText().toString().trim();

        ConsumeRecord consumeRecord = new ConsumeRecord();
        consumeRecord.setConsumeTimestamp(timeInMillis);
        consumeRecord.setCustomerId(-1);
        consumeRecord.setConsumeTime(workTime);
        consumeRecord.setCustomeName(customerName);

        consumeRecord.setStaffId(staff.getId());
        consumeRecord.setStaffName(staff.getName());
        consumeRecord.setWorkTime(workTime);
        consumeRecord.setCurrentMonthTime(currentMonthTimeLater);
        consumeRecord.setRemark(remark);
        consumeRecord.setTimestampFlag(System.currentTimeMillis());
        consumeRecord.save();

        //修改员工本月时间
        staff.setHoursOfCurrentMonth(currentMonthTimeLater);
        if (currentMonthTimeLater==0){
            staff.setToDefault("hoursOfCurrentMonth");
        }
        staff.update(staff.getId());

        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, getIntent());
        finish();
    }
}