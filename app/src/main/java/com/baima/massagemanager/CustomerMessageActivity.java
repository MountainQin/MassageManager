package com.baima.massagemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.interfaces.OnItemListener;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerMessageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RECHARGE = 3;
    private static final int CONSUME = 4;
    private static final int ALTER_NUMBER = 5;
    private static final int ALTER_NAME = 6;
    private static final int ALTER_PHONE_NUMBER = 7;
    private static final int ALTER_REMAINDER = 8;
    private static final int ALTER_REMARK = 9;

    private long customerId;
    private CustomerRecordAdapter adapter;
    private List consumeRechargeRecordList = new ArrayList();
    private RecyclerView rv_customer_record;
    private TextView tv_number;
    private TextView tv_name;
    private TextView tv_phone_number;
    private TextView tv_remainder;
    private TextView tv_remark;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("顾客信息");
        setContentView(R.layout.activity_customer_message);

        initViews();
    }

    @Override
    public void onClick(View v) {
        Intent editActivityIntent = null;
        switch (v.getId()) {
            case R.id.tv_delete:
                showDeleteStaffDialog();
                break;
            case R.id.tv_recharge:
                //打开充值活动
                Intent intent = new Intent(this, RechargeActivity.class);
                intent.putExtra("customerId", customerId);
                startActivityForResult(intent, RECHARGE);
                break;
            case R.id.tv_consume:
                //消费
                Intent intent1 = new Intent(CustomerMessageActivity.this, ConsumeActivity.class);
                intent1.putExtra("customerId", customerId);
                startActivityForResult(intent1, CONSUME);
                break;

            case R.id.tv_number:
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_NUMBER);
                startActivityForResult(editActivityIntent, ALTER_NUMBER);
                break;
            case R.id.tv_name:
                //修改姓名
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_TEXT);
                startActivityForResult(editActivityIntent, ALTER_NAME);
                break;
            case R.id.tv_phone_number:
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_PHONE);
                startActivityForResult(editActivityIntent, ALTER_PHONE_NUMBER);
                break;
            case R.id.tv_current_month_time:
                editActivityIntent = new Intent(this, EditActivity.class);
                startActivityForResult(editActivityIntent, ALTER_REMAINDER);
                break;
            case R.id.tv_remark:
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_TEXT);
                startActivityForResult(editActivityIntent, ALTER_REMARK);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //设置返回顾客 列表的结果 ，以便刷新 。
            setResult(RESULT_OK);
            String inputData = "";
            if (data != null) {
                inputData = data.getStringExtra("inputData");
            }
            switch (requestCode) {
                case ALTER_NUMBER:
                    int number = Integer.valueOf(inputData);
                    if (number == 0) {
                        startActivityForResult(data, ALTER_NUMBER);
                        return;
                    }
                    customer.setNumber(number);
                    customer.update(customer.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_NAME:
                    customer.setName(inputData);
                    customer.update(customer.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_PHONE_NUMBER:
                    customer.setPhoneNumber(inputData);
                    customer.update(customer.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_REMAINDER:
                    double remaider = Double.valueOf(inputData);
                    customer.setRemainder(remaider);
                    if (remaider == 0) {
                        customer.setToDefault("remainder");
                    }
                    customer.update(customer.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_REMARK:
                    customer.setRemark(inputData);
                    customer.update(customer.getId());
                    refreshBaseMessage();
                    return;
            }

            //刷新 顾客信息，
            refreshBaseMessage();
            refreshRecordList();
        }
    }

    private void initViews() {
        TextView tv_delete = (TextView) findViewById(R.id.tv_delete);
        TextView tv_search = findViewById(R.id.tv_search);
        TextView tv_recharge = findViewById(R.id.tv_recharge);
        TextView tv_consume = findViewById(R.id.tv_consume);

        tv_number = findViewById(R.id.tv_number);
        tv_name = findViewById(R.id.tv_name);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_remainder = findViewById(R.id.tv_current_month_time);
        tv_remark = findViewById(R.id.tv_remark);
        rv_customer_record = findViewById(R.id.rv_staffer_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_customer_record.setLayoutManager(linearLayoutManager);
        adapter = new CustomerRecordAdapter(this, consumeRechargeRecordList);
        rv_customer_record.setAdapter(adapter);

        //刷新 顾客 信息
        Intent intent = getIntent();
        customerId = intent.getLongExtra("customerId", 0);
        if (customerId > 0) {
            refreshBaseMessage();
            refreshRecordList();
        }

        adapter.setOnItemListener(new OnItemListener() {
            @Override
            public void dataChange(double remainder) {
                String remainderStr = StringUtil.doubleTrans(remainder);
                tv_remainder.setText("剩余：" + remainderStr + "小时");
                setResult(RESULT_OK);
            }
        });

        tv_delete.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_recharge.setOnClickListener(this);
        tv_consume.setOnClickListener(this);

        tv_number.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_phone_number.setOnClickListener(this);
        tv_remainder.setOnClickListener(this);
        tv_remark.setOnClickListener(this);
    }

    //刷新 基本信息
    private void refreshBaseMessage() {
        List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
        if (customerList.size() > 0) {
            customer = customerList.get(0);
            tv_number.setText("编号：" + customer.getNumber());
            tv_name.setText("姓名：" + customer.getName());
            tv_phone_number.setText("手机号：" + customer.getPhoneNumber());
            tv_remainder.setText("剩余：" +
                    StringUtil.doubleTrans(customer.getRemainder()) + "小时");
            tv_remark.setText("备注：" + customer.getRemark());
        }


    }


    //刷新 记录列表
    private void refreshRecordList() {
        consumeRechargeRecordList.clear();
        consumeRechargeRecordList.addAll(getConsumeRechargeRecordList());
        adapter.notifyDataSetChanged();
    }

    //显示 删除员工对话框
    private void showDeleteStaffDialog() {
        List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
        if (customerList.size() < 1) {
            return;
        }
        final Customer customer = customerList.get(0);

        String message = "你确定删除 " +
                customer.getNumber() + "号" +
                customer.getName() + " 吗？";

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //删除顾客 表数据
                        LitePal.delete(Customer.class, customer.getId());
                        //删除充值记录表数据
                        LitePal.deleteAll(RechargeRecord.class, "customerId=?", String.valueOf(customer.getId()));
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();
    }

    //获取 消费和充值的记录的集合
    private List getConsumeRechargeRecordList() {
        List consumeRechargeRecordList = new ArrayList<>();
        //充值记录
        List<RechargeRecord> rechargeRecordList = LitePal.where("customerId=?", String.valueOf(customerId)).order("id desc")
                .find(RechargeRecord.class);
        consumeRechargeRecordList.addAll(rechargeRecordList);

        //消费记录
        List<ConsumeRecord> consumeRecordList = LitePal.where("customerId=?", String.valueOf(customerId)).order("id desc")
                .find(ConsumeRecord.class);
        //去掉重复
        for (int i = 0; i < consumeRecordList.size(); i++) {
            ConsumeRecord consumeRecord = consumeRecordList.get(i);
            for (int j = i + 1; j < consumeRecordList.size(); j++) {
                ConsumeRecord consumeRecord1 = consumeRecordList.get(j);
                if (consumeRecord.getTimestampFlag() == consumeRecord1.getTimestampFlag()) {
                    consumeRecordList.remove(j);
                    j--;
                }
            }
        }
        consumeRechargeRecordList.addAll(consumeRecordList);

        //消费和充值一起按时间排序
        Collections.sort(consumeRechargeRecordList, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                long timestamp1 = 0;
                if (o1 instanceof ConsumeRecord) {
                    timestamp1 = ((ConsumeRecord) o1).getConsumeTimestamp();
                } else if (o1 instanceof RechargeRecord) {
                    timestamp1 = ((RechargeRecord) o1).getTimeStamp();
                }

                long timestamp2 = 0;
                if (o2 instanceof ConsumeRecord) {
                    timestamp2 = ((ConsumeRecord) o2).getConsumeTimestamp();
                } else if (o2 instanceof RechargeRecord) {
                    timestamp2 = ((RechargeRecord) o2).getTimeStamp();
                }

//                return (int)(timestamp2-timestamp1);
                return Long.compare(timestamp2, timestamp1);
            }
        });
        return consumeRechargeRecordList;
    }
}
