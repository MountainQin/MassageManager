package com.baima.massagemanager;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomerMessageActivity extends BaseActivity<Customer, Object> {

    private TextView tv_recharge;
    private TextView tv_consume;
    private TextView tv_remainder;

    @Override
    public void initViews() {
        super.initViews();
        tv_recharge = findViewById(R.id.tv_recharge);
        tv_consume = findViewById(R.id.tv_consume);
        tv_remainder = findViewById(R.id.tv_remainder);

        tv_recharge.setVisibility(View.VISIBLE);
        tv_consume.setVisibility(View.VISIBLE);
        tv_remainder.setVisibility(View.VISIBLE);

        tv_recharge.setOnClickListener(this);
        tv_consume.setOnClickListener(this);
        tv_remainder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_recharge:
                intent = new Intent(this, RechargeActivity.class);
                intent.putExtra("customerId", t.getId());
                startActivityForResult(intent, RECHARGE);
                break;
            case R.id.tv_consume:
                intent = new Intent(this, ConsumeActivity.class);
                intent.putExtra("customerId", t.getId());
                startActivityForResult(intent, CONSUME);
                break;
            case R.id.tv_remainder:
                intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, ALTER_REMAINDER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RECHARGE:
                case CONSUME:
                    refreshBaseMessage();
                    refreshListData(startTimeInMillis, endTimeInMillis);
                    return;
                case ALTER_REMAINDER:
                    double remainder = Double.valueOf(data.getStringExtra("inputData"));
                    t.setRemainder(remainder);
                    if (remainder == 0) {
                        t.setToDefault("remainder");
                    }
                    t.update(t.getId());
                    refreshBaseMessage();
                    MainActivity.customerFragment.refreshCustomerList();
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public List<Customer> getTList(long personId) {
        return LitePal.where("id=?", String.valueOf(personId)).find(Customer.class);
    }

    @Override
    public RecyclerView.Adapter getAdapter(List<Object> dataList) {
        return new CustomerRecordAdapter(this, dataList);
    }

    @Override
    //往前一天加载，至少20
    public void loadMoreLeast50() {
        lrv_staffer_record.setLoadMoreEnabled(false);
//showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List list = new ArrayList<>();
                calendar.setTimeInMillis(startTimeInMillis);
                while (list.size() < 50) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    if (calendar.get(Calendar.YEAR) < 2020) {
                        showToast("已经加载到2020年1月1日");
                        break;
                    }
                    list.addAll(getConsumeRechargeRecordList(calendar.getTimeInMillis(), startTimeInMillis));
                    startTimeInMillis = calendar.getTimeInMillis();
                }
                dataList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        refreshTvDate();
//显示 记录数量
                        tv_lrv.setText("记录列表 " + dataList.size());
                        closeProgressDialog();
                        lrv_staffer_record.refreshComplete(0);
                        lrv_staffer_record.setLoadMoreEnabled(true);
                    }
                });
            }
        }).start();
    }

    @Override
    public void refreshListData(final long startTimeInMillis, final long endTimeInMillis) {
        dataList.clear();
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataList.addAll(getConsumeRechargeRecordList(startTimeInMillis, endTimeInMillis));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        lrv_staffer_record.scrollToPosition(0);
                        refreshTvDate();
//显示 记录数量
                        tv_lrv.setText("记录列表 " + dataList.size());
                        closeProgressDialog();
                    }
                });
            }
        }).start();
    }

    @Override
    public void refreshBaseMessage() {
        super.refreshBaseMessage();
        double remainder = t.getRemainder();
        tv_remainder.setText("剩余：" + StringUtil.doubleTrans(remainder) + "小时");

    }

    //根据指定起始时间获取 消费和充值的记录的集合
    private List getConsumeRechargeRecordList(long startTimeInMillis, long endTimeInMillis) {
        long customerId = t.getId();
        List consumeRechargeRecordList = new ArrayList<>();

        //消费记录
        List<ConsumeRecord> consumeRecordList = LitePal.where("customerId=? and consumeTimestamp >=? and consumeTimestamp <?", String.valueOf(customerId), String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(ConsumeRecord.class);
        consumeRechargeRecordList.addAll(consumeRecordList);

        //充值记录
        List<RechargeRecord> rechargeRecordList = LitePal.where("customerId=? and timeStamp >=? and timeStamp <?", String.valueOf(customerId), String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(RechargeRecord.class);
        consumeRechargeRecordList.addAll(rechargeRecordList);

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
