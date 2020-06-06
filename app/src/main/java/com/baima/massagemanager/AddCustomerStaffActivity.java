package com.baima.massagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.PersonUtil;

import java.util.Calendar;
import java.util.Date;

public class AddCustomerStaffActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int DATE_TIME = 1;
    /**
     * 添加顾客
     */
    public static final int ADD_CUSTOMER = 1;
    /**
     * 添加员工
     */
    public static final int ADD_STAFF = 2;
    private EditText ed_number;
    private EditText ed_name;
    private EditText ed_phone_number;
    private EditText ed_recharge_amount;
    private EditText ed_remainder;
    private EditText ed_remark;
    private LinearLayout layout_customer;
    private int type;
    private TextView tv_date_time;
    private long timeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_staff);

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date_time:
                //打开选择日期时间
                Intent intent = new Intent(this, PickDateTimeActivity.class);
                intent.putExtra("timeInMillis", timeInMillis);
                startActivityForResult(intent, DATE_TIME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DATE_TIME:
                if (resultCode == RESULT_OK) {
                    timeInMillis = data.getLongExtra("timeInMillis", timeInMillis);
                    tv_date_time.setText(new Date(timeInMillis).toLocaleString());
                }
                break;
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

    //初始化组件
    private void initViews() {
        ed_number = findViewById(R.id.ed_number);
        ed_name = findViewById(R.id.ed_name);
        ed_phone_number = findViewById(R.id.ed_phone_number);
        layout_customer = findViewById(R.id.layout_customer);
        ed_recharge_amount = findViewById(R.id.ed_recharge_amount);
        ed_remainder = findViewById(R.id.ed_remainder);
        tv_date_time = findViewById(R.id.tv_date_time);
        ed_remark = findViewById(R.id.ed_remark);

        setTitle("添加");
        Intent intent = getIntent();
        int number = intent.getIntExtra("number", 0);
        type = intent.getIntExtra("type", 0);

        if (type == ADD_CUSTOMER) {
            //如果 是添加客户
            layout_customer.setVisibility(View.VISIBLE);
        } else if (type == ADD_STAFF) {
            //如果 是添加员工
            number = PersonUtil.getNewNumber(Staff.class);
        }
        ed_number.setText(String.valueOf(number));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timeInMillis = calendar.getTimeInMillis();
        tv_date_time.setText(new Date(timeInMillis).toLocaleString());
        tv_date_time.setOnClickListener(this);
    }


    private void saveData() {
        boolean isSaved = false;
        try {
            //获取 编辑框数据
            int number = Integer.valueOf(ed_number.getText().toString().trim());
            String name = ed_name.getText().toString().trim();
            String phoneNumber = ed_phone_number.getText().toString().trim();
            String remark = ed_remark.getText().toString().trim();

            //编号 不能小于1
            if (number < 1) {
                ed_number.setText("");
                Toast.makeText(this, "编号 不能小于1。请重新输入！", Toast.LENGTH_SHORT).show();
                return;
            }

            if (type == ADD_CUSTOMER) {
                if (PersonUtil.isExists(number, Customer.class)) {
                    //如果这个编号 的顾客 已经存在
                    ed_number.setText("");
                    Toast.makeText(this, number + "号顾客已经存在，请输入其他编号！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double rechargeAmount = Double.valueOf(ed_recharge_amount.getText().toString().trim());
                Double rechargeHour = Double.valueOf(ed_remainder.getText().toString().trim());
//保存顾客
                Customer customer = new Customer();
                customer.setNumber(number);
                customer.setName(name);
                customer.setPhoneNumber(phoneNumber);
                customer.setRemainder(rechargeHour);
                customer.setRemark(remark);
                isSaved = customer.save();

                //保存充值记录
                long customerIdd = customer.getId();
                //秒数毫秒数清零
                remark = "";
                RechargeRecord rechargeRecord = new RechargeRecord(customerIdd, timeInMillis, rechargeAmount, rechargeHour, rechargeHour, remark);
                isSaved = rechargeRecord.save();

            } else if (type == ADD_STAFF) {
                //如果 是添加员工，保存员工
                isSaved = new Staff(number, name, phoneNumber, 0)
                        .save();
            }

            if (isSaved) {
                //如果 保存成功
                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "保存失败，请检查 后重试！", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "你的数据有误，请检查后重试！", Toast.LENGTH_SHORT).show();
        }
    }
}
