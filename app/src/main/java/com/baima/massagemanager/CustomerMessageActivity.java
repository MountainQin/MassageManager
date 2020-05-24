package com.baima.massagemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CustomerMessageActivity extends AppCompatActivity implements View.OnClickListener {


    private  long customerId              ;
    private CustomerRecordAdapter adapter;
    private List<RechargeRecord> rechargeRecordList=new ArrayList<>();
    private RecyclerView rv_customer_record;
    private TextView tv_number;
    private TextView tv_name;
    private TextView tv_phone_number;
    private TextView tv_remainder;
    private TextView tv_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("顾客信息");
        setContentView(R.layout.activity_customer_message);

        initViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_delete:
                showDeleteDialog();
                break;
            case R.id.tv_recharge:
                //打开充值活动
                Intent intent = new Intent(this, RechargeActivity.class);
                intent.putExtra("customerId", customerId);
                startActivityForResult(intent,2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
if (resultCode==RESULT_OK){
    //刷新 顾客信息，设置返回顾客 列表的结果 ，以便刷新 。
    refreshCustomerMessage();
    setResult(RESULT_OK);
}
    }

    private void initViews() {
        TextView tv_delete = findViewById(R.id.tv_delete);
        TextView tv_search = findViewById(R.id.tv_search);
        TextView tv_recharge = findViewById(R.id.tv_recharge);
        TextView tv_consume = findViewById(R.id.tv_consume);

        tv_number = findViewById(R.id.tv_number);
        tv_name = findViewById(R.id.tv_name);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_remainder = findViewById(R.id.tv_remainder);
        tv_remark = findViewById(R.id.tv_remark);
        rv_customer_record = findViewById(R.id.rv_customer_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_customer_record.setLayoutManager(linearLayoutManager);
        adapter=new CustomerRecordAdapter(this, rechargeRecordList);
        rv_customer_record.setAdapter(adapter);

        //刷新 顾客 信息
        Intent intent = getIntent();
        customerId = intent.getLongExtra("customerId", 0);
        if (customerId>0){
            refreshCustomerMessage();
        }

        tv_delete.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_recharge.setOnClickListener(this);
        tv_consume.setOnClickListener(this);
    }

    //刷新 顾客 信息
    private void refreshCustomerMessage() {
        //刷新 基本信息
        List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
        if (customerList.size() > 0) {
            Customer customer = customerList.get(0);
            tv_number.setText("编号：" + customer.getNumber());
            tv_name.setText("姓名：" + customer.getName());
            tv_phone_number.setText("手机号：" + customer.getPhoneNumber());
            tv_remainder.setText("剩余：" +
    StringUtil.doubleTrans            (customer.getRemainder()) + "小时");
            tv_remark.setText("备注：" + customer.getRemark());
        }

        //记录
        rechargeRecordList.clear();
        rechargeRecordList.addAll(
                LitePal.where("customerId=?", String.valueOf(customerId)).order("timeStamp desc").find(RechargeRecord.class)
        );
        adapter.notifyDataSetChanged();
    }

   //显示 删除对话框
   private void showDeleteDialog(){
       List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
       if (customerList.size()<1){
           return ;
       }
       final Customer customer = customerList.get(0);

       String message="你确定删除 "+
                customer.getNumber() +"号"+
               customer.getName() +" 吗？";

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
 }
