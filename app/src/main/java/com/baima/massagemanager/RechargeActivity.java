package com.baima.massagemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "RechargeActivity";

    private static final int DATE_TIME = 1;
    private static final int ALTER_HOUR_PRICE = 2;
    private EditText et_remark;
    private EditText et_recharge_hour;
    private EditText et_recharge_amount;
    private TextView tv_number_name;
    private long customerId;
    private Customer customer;
    private TextView tv_date_time;
    private long timeInMillis;
    private EditText et_remainder;
    private TextView tv_recharge_amount;
    private double hourPrice;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("充值");
        setContentView(R.layout.activity_recharge);

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date_time:
                //打开选择日期时间活动
                Intent intent = new Intent(this, PickDateTimeActivity.class);
                intent.putExtra("timeInMillis", timeInMillis);
                startActivityForResult(intent, DATE_TIME);
                break;
            case R.id.tv_recharge_amount:
                //修改小时价格
                Intent intent1 = new Intent(this, EditActivity.class);
                startActivityForResult(intent1, ALTER_HOUR_PRICE);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //充值金额编辑框内容变化 ，小时数编辑框剩余编辑框也 变化
        String text = s.toString();
        if (text.length() > 0) {
            try {
                double rechargeAmount = Double.valueOf(text);
                double rechargeHour = rechargeAmount / hourPrice;
                et_recharge_hour.setText(StringUtil.doubleTrans(rechargeHour));
                double remainder = customer.getRemainder() + rechargeHour;
                et_remainder.setText(StringUtil.doubleTrans(remainder));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            //如果 内容为空，其他两个编辑设置为空
            et_recharge_hour.setText("");
            et_remainder.setText("");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DATE_TIME:
                    timeInMillis = data.getLongExtra("timeInMillis", timeInMillis);
                    tv_date_time.setText(new Date(timeInMillis).toLocaleString());
                    break;
                case ALTER_HOUR_PRICE:
                    hourPrice = data.getDoubleExtra("inputData", hourPrice);
                    tv_recharge_amount.setText("充值金额(" + StringUtil.doubleTrans(hourPrice, true) + "元=1小时)");
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putFloat("hourPrice", (float) hourPrice);
                    edit.apply();
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
        switch (item.getItemId()) {
            case R.id.item_save:
                saveData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        tv_number_name = findViewById(R.id.tv_number_name);
        tv_recharge_amount = findViewById(R.id.tv_recharge_amount);
        et_recharge_amount = findViewById(R.id.et_recharge_amount);
        et_recharge_hour = findViewById(R.id.et_recharge_hour);
        et_remainder = findViewById(R.id.et_remainder);
        tv_date_time = findViewById(R.id.tv_date_time);
        et_remark = findViewById(R.id.et_remark);

        //显示 编号 和姓名
        Intent intent = getIntent();
        customerId = intent.getLongExtra("customerId", 0);
        if (customerId != 0) {
            List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
            if (customerList.size() > 0) {
                customer = customerList.get(0);
                tv_number_name.setText(customer.getNumber() + "号 " + customer.getName() +
                        " " + StringUtil.doubleTrans(customer.getRemainder()) + "小时");

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                timeInMillis = calendar.getTimeInMillis();
                tv_date_time.setText(new Date(timeInMillis).toLocaleString());
                tv_date_time.setOnClickListener(this);
            }
        }
        ///小时价格
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        hourPrice = sharedPreferences.getFloat("hourPrice", 88.0F);
        tv_recharge_amount.setText("充值金额(" + StringUtil.doubleTrans(hourPrice, true) + "元=1小时)");
        tv_recharge_amount.setOnClickListener(this);
        et_recharge_amount.addTextChangedListener(this);
    }

    //保存充值记录
    private void saveData() {
        try {
            //获取 编辑框内容
            double rechargeAmount = Double.valueOf(et_recharge_amount.getText().toString().trim());
            double rechargeHour = Double.valueOf(et_recharge_hour.getText().toString().trim());
            double remainder = Double.valueOf(et_remainder.getText().toString());
            String remark = et_remark.getText().toString().trim();

            //设置顾客 剩余时间
            customer.setRemainder(remainder);
            if (remainder == 0) {
                customer.setToDefault("remainder");
            }
            customer.update(customer.getId());

            //保存记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setCustomerId(customerId);
            rechargeRecord.setTimeStamp(timeInMillis);
            rechargeRecord.setRechargeAmount(rechargeAmount);
            rechargeRecord.setRechargeHour(rechargeHour);
            rechargeRecord.setRemainder(remainder);
            rechargeRecord.setRemark(remark);
            rechargeRecord.setTimestampFlag(System.currentTimeMillis());
            boolean isSaved = rechargeRecord.save();

            //保存成功
            if (isSaved) {
                Toast.makeText(this, "充值成功！", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "你输入的数据 有误，请检查 后重试！", Toast.LENGTH_SHORT).show();
        }
    }
}
