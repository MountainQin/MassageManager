package com.baima.massagemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.entity.WorkStaff;
import com.baima.massagemanager.util.PersonUtil;
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
    public void onItemLongClick(View view, int position) {
        //和BaseActivity的方法是一样的
        showDeleteRecordDialog(position);
    }

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
        dataList.addAll(getConsumeRechargeRecordList(startTimeInMillis, endTimeInMillis));
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

    //删除项目的对话框
    private void showDeleteRecordDialog1(final int position) {
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
                            //删除数据 表的数据 ，如果 有相同记录都删除
                            long timestampFlag = consumeRecord.getTimestampFlag();
                            List<ConsumeRecord> all = LitePal.findAll(ConsumeRecord.class);
                            for (int i = 0; i < all.size(); i++) {
                                if (timestampFlag == all.get(i).getTimestampFlag()) {
                                    //修改员工时间
                                    long staffId = all.get(i).getStaffId();
                                    List<Staff> staffList = LitePal.where("id=?", String.valueOf(staffId)).find(Staff.class);
                                    if (staffList.size() > 0) {
                                        Staff staff = staffList.get(0);
                                        double hoursOfCurrentMonth = staff.getHoursOfCurrentMonth();
                                        double workTime = consumeRecord.getWorkTime();
                                        hoursOfCurrentMonth -= workTime;
                                        staff.setHoursOfCurrentMonth(hoursOfCurrentMonth);
                                        if (hoursOfCurrentMonth == 0) {
                                            staff.setToDefault("hoursOfCurrentMonth");
                                        }
                                        staff.update(staffId);
                                    }
//删除记录
                                    all.get(i).delete();
                                }
                            }

                            //刷新员工列表记录列表
                            MainActivity.customerFragment.refreshCustomerList();
                            MainActivity.staffFragment.refreshListData();
                            MainActivity.recordFragment.refreshListData();

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

                                //
                                String remainderStr = StringUtil.doubleTrans(remainder);
                                tv_remainder.setText("剩余：" + remainderStr + "小时");
                                setResult(RESULT_OK, getIntent());
                            }

                            dataList.remove(position);
                            adapter.notifyDataSetChanged();
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
                                String remainderStr = StringUtil.doubleTrans(remainder);
                                tv_remainder.setText("剩余：" + remainderStr + "小时");
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

    private void showDeleteRecordDialog(final int position) {
        new AlertDialog.Builder(this)
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
                            //删除消费记录
                            consumeRecord.delete();
                            //修改工作员工表的对应时间， 删除工作员工表的数据
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

                                //
                                String remainderStr = StringUtil.doubleTrans(remainder);
                                tv_remainder.setText("剩余：" + remainderStr + "小时");
                                setResult(RESULT_OK, getIntent());
                            }

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
                                String remainderStr = StringUtil.doubleTrans(remainder);
                                tv_remainder.setText("剩余：" + remainderStr + "小时");
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
}
