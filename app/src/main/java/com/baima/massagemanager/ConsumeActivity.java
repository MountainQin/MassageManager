package com.baima.massagemanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsumeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    private Spinner spinner_time;
    private EditText et_remainder;
    private TextView tv_select_staff;
    private EditText et_other_staff;
    private EditText et_remark;
    private double consumeHour;
    private DatePicker date_picker;
    private TimePicker time_picker;
    private long timeMillis;
    private Calendar calendar;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消费");
        setContentView(R.layout.activity_consume);

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                saveData();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_staff:
                showSelectStaffPopWindow();
                break;
        }
    }

        @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //时间下拉的选择 事件,调整日期时间选择器时间，修改剩余小时数编辑框的内容
        consumeHour = position * 0.5;
        long consumeMillis = (long) (consumeHour * 1000 * 60 * 60);

        calendar.setTimeInMillis(timeMillis - consumeMillis);
        date_picker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        time_picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        time_picker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        et_remainder.setText(
                StringUtil.doubleTrans(customer.getRemainder() - consumeHour)
        );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void initViews() {
        TextView tv_number_name = (TextView) findViewById(R.id.tv_number_name);
        spinner_time = (Spinner) findViewById(R.id.spinner_time);
        date_picker = (DatePicker) findViewById(R.id.date_picker);
        time_picker = (TimePicker) findViewById(R.id.time_picker);
        et_remainder = (EditText) findViewById(R.id.et_remainder);
        tv_select_staff = (TextView) findViewById(R.id.tv_select_staff);
        et_other_staff = (EditText) findViewById(R.id.et_other_staff);
        et_remark = (EditText) findViewById(R.id.et_remark);

        //设置编号 姓名
        Intent intent = getIntent();
        long customerId = intent.getLongExtra("customerId", 0);
        if (customerId > 0) {
            List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
            if (customerList.size() > 0) {
                customer = customerList.get(0);
                tv_number_name.setText(customer.getNumber() + "号 "
                        + customer.getName());
            }
        }

        //设置时间下拉框
        ArrayList<String> timeList = new ArrayList<>();
        timeList.add("请选择消费的时间");
        for (double d = 1; d < 41; d++) {
            timeList.add(
                    StringUtil.doubleTrans(d * 0.5) + "小时"
            );
        }
        spinner_time.setDropDownWidth(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, timeList);
        spinner_time.setAdapter(adapter);

        calendar = Calendar.getInstance();
        timeMillis = System.currentTimeMillis();
        time_picker.setIs24HourView(true);


        tv_select_staff.setOnClickListener(this);
        spinner_time.setOnItemSelectedListener(this);
    }

    //保存数据
    private void saveData() {
        //获取 数据 】
        //消费时间戳
        calendar.set(date_picker.getYear(), date_picker.getMonth(), date_picker.getDayOfMonth(),
                time_picker.getCurrentHour(), time_picker.getCurrentMinute(), 0);
        //秒数毫秒数清0
        calendar.set(Calendar.MILLISECOND, 0);
        long consumeTimestamp = calendar.getTimeInMillis();
    }

    //显示 选择员工悬浮 窗口
    private void showSelectStaffPopWindow() {
        //员工悬浮 窗
        final PopupWindow pw_staff = new PopupWindow(tv_select_staff, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                View view = LayoutInflater.from(this)
                .inflate(R.layout.select_staff, null);
        ListView lv_staff = (ListView) view.findViewById(R.id.lv_staff);
        final List<Staff> staffList = LitePal.order("number").find(Staff.class);
        ArrayList<String> titleList = new ArrayList<>();
        for (Staff staff : staffList) {
            titleList.add(staff.getNumber() + "号 " + staff.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titleList);
        lv_staff.setAdapter(adapter);
        lv_staff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long staffId = staffList.get(position).getId();
                //时间悬浮 窗
                PopupWindow pw_time =new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                ListView lv_time = new ListView(ConsumeActivity.this);
                ArrayList<String> titleList = new ArrayList<>();
                for (int i = 1; i < 21; i++) {
                    titleList.add(
                            StringUtil.doubleTrans(i* 0.5) + "小时"
                    );
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsumeActivity.this, android.R.layout.simple_list_item_1, titleList);
                lv_time.setAdapter(adapter);
                //显示 时间悬浮 窗
                pw_time.setContentView(lv_time);
                pw_time.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                pw_staff.dismiss();

                if (!pw_time.isShowing()){
                    pw_time.showAtLocation(tv_select_staff, Gravity.CENTER,0,0);
                }
            }
        });

        //设置显示 员工悬浮 窗
        pw_staff.setContentView(view);
pw_staff.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        if (!pw_staff.isShowing()) {
            pw_staff.showAtLocation(tv_select_staff, Gravity.CENTER, 0, 0);
        }
    }
}
