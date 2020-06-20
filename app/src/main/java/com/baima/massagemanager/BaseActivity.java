package com.baima.massagemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.Person;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.entity.WorkStaff;
import com.baima.massagemanager.util.CalendarUtil;
import com.baima.massagemanager.util.ConsumeRecordUtil;
import com.baima.massagemanager.util.PersonUtil;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class BaseActivity<T extends Person, E> extends AppCompatActivity implements View.OnClickListener, OnLoadMoreListener, OnItemLongClickListener, OnRefreshListener {

    public static final String EXTRA_PERSON_ID = "personId";

    public static final int RECHARGE = 1;
    public static final int CONSUME = 2;
    public static final int RECORD = 3;
    private static final int ALTER_NUMBER = 5;
    private static final int ALTER_NAME = 6;
    private static final int ALTER_PHONE_NUMBER = 7;
    public static final int ALTER_CURRENT_MONTH_TIME = 8;
    private static final int ALTER_REMARK = 9;
    private static final int PICK_DATE = 10;
    public static final int ALTER_REMAINDER = 11;
    private static final int CALL_PHONE = 12;


    private TextView tv_delete;
    public LRecyclerView lrv_staffer_record;
    private TextView tv_search;
    private TextView tv_number;
    private TextView tv_name;
    private TextView tv_phone_number;
    private TextView tv_call;
    private TextView tv_sms;
    private TextView tv_remark;
    private TextView tv_date;
    public RecyclerView.Adapter adapter;
    public LRecyclerViewAdapter lRecyclerViewAdapter;
    public Calendar calendar;
    public long startTimeInMillis;
    public long endTimeInMillis;
    public T t;
    public List<E> dataList = new ArrayList<>();
    private ProgressDialog progressDialog;
    public TextView tv_lrv;
    public long startTime;

    public abstract List<T> getTList(long personId);

    public abstract RecyclerView.Adapter getAdapter(List<E> dataList);

    public abstract void loadMoreLeast50();

    public abstract void refreshListData(long startTimeInMillis, long endTimeInMillis);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_base);

        initViews();
        initData();
    }

    @Override
    public void onLoadMore() {
        loadMoreLeast50();
    }

    @Override
    public void onRefresh() {
        lrv_staffer_record.refreshComplete(0);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        showDeleteRecordDialog(position);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.tv_delete:
                showDeletePersonDialog();
                break;

            case R.id.tv_number:
                intent = new Intent(this, EditActivity.class);
                intent.putExtra("inputType", InputType.TYPE_CLASS_NUMBER);
                startActivityForResult(intent, ALTER_NUMBER);
                break;
            case R.id.tv_name:
                //修改姓名
                intent = new Intent(this, EditActivity.class);
                intent.putExtra("inputType", InputType.TYPE_CLASS_TEXT);
                startActivityForResult(intent, ALTER_NAME);
                break;
            case R.id.tv_phone_number:
                intent = new Intent(this, EditActivity.class);
                intent.putExtra("inputType", InputType.TYPE_CLASS_PHONE);
                startActivityForResult(intent, ALTER_PHONE_NUMBER);
                break;
            case R.id.tv_call:
                String phoneNumber = t.getPhoneNumber();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(this, "手机号为空，请检查 重试！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
                } else {
                    call(phoneNumber);
                }
                break;
            case R.id.tv_sms:
                String phoneNumber1 = t.getPhoneNumber();
                if (TextUtils.isEmpty(phoneNumber1)) {
                    Toast.makeText(this, "手机号为空，请检查 重试！", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent = new Intent(this, SendSmsActivity.class);
                intent.putExtra("phoneNumber", phoneNumber1);
                startActivity(intent);
                break;
            case R.id.tv_remark:
                intent = new Intent(this, EditActivity.class);
                intent.putExtra("inputType", InputType.TYPE_CLASS_TEXT);
                startActivityForResult(intent, ALTER_REMARK);
                break;
            case R.id.tv_date:
                intent = new Intent(this, PickDateActivity.class);
                intent.putExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                intent.putExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                startActivityForResult(intent, PICK_DATE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, getIntent());

//            refreshListData();在后面
            //刷新 基本信息
            String inputData = data.getStringExtra("inputData");
            switch (requestCode) {
                case PICK_DATE:
                    startTimeInMillis = data.getLongExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                    endTimeInMillis = data.getLongExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                    break;

                case ALTER_NUMBER:
                    int number = Integer.valueOf(inputData);
                    if (number == 0) {
                        startActivityForResult(data, ALTER_NUMBER);
                        return;
                    }
                    t.setNumber(number);
                    t.update(t.getId());

                    //如果 是员工修改消费记录里的员工姓名
                    if (t instanceof Staff) {
                        alterStaffNames((Staff) t);
                        //刷新 员工和记录列表
                        refreshListData(startTimeInMillis, endTimeInMillis);
                        MainActivity.staffFragment.refreshListData();
                        MainActivity.recordFragment.refreshListData();
                    }
                    refreshBaseMessage();
                    return;
                case ALTER_NAME:
                    t.setName(inputData);
                    t.update(t.getId());
                    //如果 是顾客 ，修改消费记录里的顾客 姓名
                    if (t instanceof Customer) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("customeName", t.getName());
                        LitePal.updateAll(ConsumeRecord.class, contentValues, "CustomerId=?", String.valueOf(t.getId()));
                        //不刷新也 行
                        MainActivity.recordFragment.refreshListData();
                    } else if (t instanceof Staff) {
                        //如果 是员工修改消费记录里的员工姓名
                        alterStaffNames((Staff) t);
                        //刷新 员工和记录列表
                        refreshListData(startTimeInMillis, endTimeInMillis);
                        MainActivity.staffFragment.refreshListData();
                        MainActivity.recordFragment.refreshListData();
                    }

                    refreshBaseMessage();
                    return;
                case ALTER_PHONE_NUMBER:
                    t.setPhoneNumber(inputData);
                    t.update(t.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_REMARK:
                    t.setRemark(inputData);
                    t.update(t.getId());
                    refreshBaseMessage();
                    return;
            }

            //刷新 基本信息和列表
            refreshBaseMessage();
            refreshListData(startTimeInMillis, endTimeInMillis);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call(t.getPhoneNumber());
                } else {
                    Toast.makeText(this, "没有获得授权，无法呼叫", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void initViews() {
        tv_delete = findViewById(R.id.tv_delete);
        tv_search = findViewById(R.id.tv_search);

        tv_number = findViewById(R.id.tv_number);
        tv_name = findViewById(R.id.tv_name);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_call = findViewById(R.id.tv_call);
        tv_sms = findViewById(R.id.tv_sms);
        tv_remark = findViewById(R.id.tv_remark);
        tv_date = findViewById(R.id.tv_date);
        tv_lrv = findViewById(R.id.tv_lrv);
        lrv_staffer_record = findViewById(R.id.lrv_customer_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lrv_staffer_record.setLayoutManager(linearLayoutManager);
        adapter = getAdapter(dataList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        lrv_staffer_record.setAdapter(lRecyclerViewAdapter);

        tv_delete.setOnClickListener(this);

        tv_number.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_phone_number.setOnClickListener(this);
        tv_call.setOnClickListener(this);
        tv_sms.setOnClickListener(this);
        tv_remark.setOnClickListener(this);
        tv_date.setOnClickListener(this);

        lrv_staffer_record.setOnLoadMoreListener(this);
        lrv_staffer_record.setOnRefreshListener(this);
        lRecyclerViewAdapter.setOnItemLongClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        long personId = intent.getLongExtra(EXTRA_PERSON_ID, 0);
        List<T> tList = getTList(personId);


        //如果 没有找到顾客员工，退出
        if (tList.size() == 0) {
            finish();
            return;
        }

        t = tList.get(0);
        refreshBaseMessage();

        initDate();
        //加载7天内的数据
        startTime = System.currentTimeMillis() ;
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        startTimeInMillis = calendar.getTimeInMillis();
        refreshListData(startTimeInMillis,endTimeInMillis);

    }

    //删除顾客 或者员工的对话框
    private void showDeletePersonDialog() {
        String numberName = t.getNumber() + "号" + t.getName();
        String msg = "你确定删除 " + numberName + " 吗？";
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        t.delete();
                        if (t instanceof Customer) {
                            //如果 是顾客 删除充值数据 表数据
                            LitePal.deleteAll(RechargeActivity.class, "customerId=?", String.valueOf(t.getId()));
                        }
//                        setResult(RESULT_OK);
                        MainActivity.customerFragment.refreshCustomerList();
                        MainActivity.staffFragment.refreshListData();
                        finish();
                    }
                })
                .show();

    }

    //初始化时间
    private void initDate() {
        calendar = Calendar.getInstance();
        //小时分钟秒毫秒清零
        CalendarUtil.setTimeTo0(calendar);
//往后一天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        startTimeInMillis = calendar.getTimeInMillis();
        endTimeInMillis = calendar.getTimeInMillis();

    }

    //刷新 日期标签
    public void refreshTvDate() {
        String s = new Date(startTimeInMillis).toLocaleString();
        String s1 = new Date(endTimeInMillis).toLocaleString();
        tv_date.setText("时间:" + s + " - " + s1);
        }

    //刷新基本信息
    public void refreshBaseMessage() {
        t = getTList(t.getId()).get(0);
        tv_number.setText("编号：" + t.getNumber());
        tv_name.setText("姓名：" + t.getName());
        tv_phone_number.setText("手机号：" + t.getPhoneNumber());
        tv_remark.setText("备注：" + t.getRemark());
    }

    //删除项目的对话框
    private void showDeleteRecordDialog(final int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("你确定删除这条记录吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Object o = dataList.get(position);
                        //如果 是记录
                        if (o instanceof ConsumeRecord) {
                            ConsumeRecord consumeRecord = (ConsumeRecord) o;
                            consumeRecord.delete();

                            //修改员工表的本月时间， 删除工作员工表的数据
                            List<WorkStaff> workStaffList = LitePal.where("consumeRecordId=?", String.valueOf(consumeRecord.getId())).find(WorkStaff.class);
                            for (WorkStaff workStaff : workStaffList) {
                                Staff staff = PersonUtil.getPerson(Staff.class, workStaff.getStaffId());
                                if (staff != null) {
                                    double workTime = workStaff.getWorkTime();
                                    double currentMontTime = staff.getHoursOfCurrentMonth() - workTime;
                                    staff.setHoursOfCurrentMonth(currentMontTime);
                                    if (currentMontTime == 0) {
                                        staff.setToDefault("hoursOfCurrentMonth");
                                    }
                                    staff.update(staff.getId());
                                }
                                workStaff.delete();
                            }


                            //修改顾客 剩余时间
                            long customerId = consumeRecord.getCustomerId();
                            List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
                            if (customerList.size() > 0) {
                                Customer customer = customerList.get(0);
                                double remainder = customer.getRemainder() + consumeRecord.getConsumeTime();
                                customer.setRemainder(remainder);
                                if (remainder == 0) {
                                    //更新为默认值
                                    customer.setToDefault("remainder");
                                }
                                customer.update(customer.getId());
                            }

                            //
                            refreshBaseMessage();
                            setResult(RESULT_OK, getIntent());

                            dataList.remove(position);
                            adapter.notifyDataSetChanged();
                            //刷新员工列表记录列表
                            MainActivity.customerFragment.refreshCustomerList();
                            MainActivity.staffFragment.refreshListData();
                            MainActivity.recordFragment.refreshListData();
                        } else {
//如果 是充值记录
                            RechargeRecord rechargeRecord = (RechargeRecord) o;
                            //从数据 表删除
                            rechargeRecord.delete();

//修改顾客表数据
                            long customerId = rechargeRecord.getCustomerId();
                            List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
                            if (customerList.size() > 0) {
                                Customer customer = customerList.get(0);
                                double remainder = customer.getRemainder() - rechargeRecord.getRechargeHour();
                                customer.setRemainder(remainder);
                                if (remainder == 0) {
                                    //更新为默认值
                                    customer.setToDefault("remainder");
                                }
                                customer.update(customer.getId());

                                //
                                refreshBaseMessage();
//                                setResult(RESULT_OK);
                                MainActivity.customerFragment.refreshCustomerList();
                            }

                            dataList.remove(position);
                            adapter.notifyDataSetChanged();

                        }
                    }
                })
                .show();

    }

    //呼叫
    private void call(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);

    }


    //修改消费记录里的员工姓名
    private void alterStaffNames(Staff staff) {
        //根据员工ID查找 到消费记录ID
        List<WorkStaff> workStaffList = LitePal.where("staffId=?", String.valueOf(staff.getId())).find(WorkStaff.class);
        for (WorkStaff workStaff : workStaffList) {
            //获取 每条消费记录的所有员工的姓名
            List<WorkStaff> list = LitePal.where("consumeRecordId=?", String.valueOf(workStaff.getConsumeRecordId())).find(WorkStaff.class);
            String staffNames = ConsumeRecordUtil.getStaffNames(list);
            //到消费记录表修改对应的消费记录
            List<ConsumeRecord> consumeRecordList = LitePal.where("id=?", String.valueOf(workStaff.getConsumeRecordId())).find(ConsumeRecord.class);
            for (ConsumeRecord consumeRecord : consumeRecordList) {
                consumeRecord.setStaffName(staffNames);
                consumeRecord.update(consumeRecord.getId());
            }


        }

    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("正在加载");
                        progressDialog.setMessage("正在加载，请稍候！");
        }
        progressDialog.show();
        }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public void showToast(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
