package com.baima.massagemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;

import org.litepal.LitePal;

import java.util.List;

public class RechargeActivity extends AppCompatActivity {

    private EditText et_remark;
    private EditText et_recharge_hour;
    private EditText et_recharge_amount;
    private TextView tv_number_name;
    private long customerId;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("充值");
        setContentView(R.layout.activity_recharge);

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
                saveRechargeRecord();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        tv_number_name = findViewById(R.id.tv_number_name);
        et_recharge_amount = findViewById(R.id.et_recharge_amount);
        et_recharge_hour = findViewById(R.id.et_recharge_hour);
        et_remark = findViewById(R.id.et_remark);

        //显示 编号 和姓名
        Intent intent = getIntent();
        customerId = intent.getLongExtra("customerId", 0);
        if (customerId != 0) {
            List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
            if (customerList.size() > 0) {
                customer = customerList.get(0);
                tv_number_name.setText(customer.getNumber() + "号 " + customer.getName());
            }
        }

    }

    //保存充值记录
    private void saveRechargeRecord() {
        try {
            //获取 编辑框内容
            double rechargeAmount = Double.valueOf(et_recharge_amount.getText().toString().trim());
            double rechargeHour = Double.valueOf(et_recharge_hour.getText().toString().trim());
            String remark = et_remark.getText().toString().trim();
            //修改顾客 的剩余小时数
            double remainder = customer.getRemainder();
            remainder += rechargeHour;
            customer.setRemainder(remainder);
            customer.update(customer.getId());

            //保存记录
            //秒数毫秒数清零
            long timeStamp = System.currentTimeMillis() / 1000 / 60 * 1000 * 60;
            RechargeRecord rechargeRecord = new RechargeRecord(customerId, timeStamp, rechargeAmount, rechargeHour, remainder, remark);
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
