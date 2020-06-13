package com.baima.massagemanager;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.Person;
import com.baima.massagemanager.util.CalendarUtil;
import com.baima.massagemanager.util.StringUtil;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class MessageBaseActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RECORD = 3;
    public static final int ALTER_NUMBER = 5;
    public static final int ALTER_NAME = 6;
    public static final int ALTER_PHONE_NUMBER = 7;
    public static final int ALTER_CURRENT_MONTH_TIME = 8;
    public static final int ALTER_REMARK = 9;
    public static final int PICK_DATE = 10;

    public TextView tv_delete;
    public LRecyclerView lrv_staffer_record;
    public TextView tv_search;
    public TextView tv_number;
    public TextView tv_name;
    public TextView tv_phone_number;
    public TextView tv_call;
    public TextView tv_sms;
    public TextView tv_remark;
    public LRecyclerViewAdapter lRecyclerViewAdapter;
    public TextView tv_date;
    public Calendar calendar;
    public long startTimeInMillis;
    public long endTimeInMillis;
    public Person person;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_DATE:
                    startTimeInMillis = data.getLongExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                    endTimeInMillis = data.getLongExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                    refreshTvDate();
                    break;
            }
        }
    }

    //刷新 日期标签
    public void refreshTvDate() {
        String s = new Date(startTimeInMillis).toLocaleString();
        String s1 = new Date(endTimeInMillis).toLocaleString();
        tv_date.setText(s + " - " + s1);
    }

    public void startPickDateActivity() {
        Intent intent = new Intent(this, PickDateActivity.class);
        intent.putExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
        intent.putExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
        startActivityForResult(intent, PICK_DATE);
    }


    //初始化时间
    public void initData() {
        calendar = Calendar.getInstance();
        //小时分钟秒毫秒清零
        CalendarUtil.setTimeTo0(calendar);
//           startTimeInMillis = calendar.getTimeInMillis();
//往后一天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        startTimeInMillis = calendar.getTimeInMillis();
        endTimeInMillis = calendar.getTimeInMillis();
    }

public void refreshBaseMessage(Person person){
    tv_number.setText("编号：" + person.getNumber());
    tv_name.setText("姓名：" + person.getName());
    tv_phone_number.setText("手机号：" + person.getPhoneNumber());
    tv_remark.setText("备注：" + person.getRemark());
}


}