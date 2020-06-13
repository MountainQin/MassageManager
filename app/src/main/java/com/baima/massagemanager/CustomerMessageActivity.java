package com.baima.massagemanager;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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


    @Override
    public void initViews() {
        super.initViews();
        tv_recharge.setVisibility(View.VISIBLE);
        tv_consume.setVisibility(View.VISIBLE);
        tv_remainder.setVisibility(View.VISIBLE);


    }

    @Override
    public List<Customer> getTList(long personId) {
        return LitePal.where("id=?", String.valueOf(personId)).find(Customer.class);
    }

    @Override
    public RecyclerView.Adapter getAdapter(List<Object> dataList) {
return new CustomerRecordAdapter(this,dataList);
    }

    @Override
    //往前一天加载，至少20
    public void loadMoreLeast20() {
        List list = new ArrayList<>();
        calendar.setTimeInMillis(startTimeInMillis);
        while (list.size() < 20) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (calendar.get(Calendar.YEAR) < 2020) {
                break;
            }
            list.addAll(getConsumeRechargeRecordList(calendar.getTimeInMillis(), startTimeInMillis));
            startTimeInMillis = calendar.getTimeInMillis();
        }
        dataList.addAll(list);
        lRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshListData(long startTimeInMillis, long endTimeInMillis) {
        dataList.clear();
        dataList.addAll(getConsumeRechargeRecordList(startTimeInMillis,endTimeInMillis));
        lRecyclerViewAdapter.notifyDataSetChanged();
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
        //充值记录
        List<RechargeRecord> rechargeRecordList = LitePal.where("customerId=? and timeStamp >=? and timeStamp <?", String.valueOf(customerId), String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(RechargeRecord.class);
        consumeRechargeRecordList.addAll(rechargeRecordList);

        //消费记录
        List<ConsumeRecord> consumeRecordList = LitePal.where("customerId=? and consumeTimestamp >=? and consumeTimestamp <?", String.valueOf(customerId), String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(ConsumeRecord.class);
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
