package com.baima.massagemanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baima.massagemanager.AddCustomerStaffActivity;
import com.baima.massagemanager.ConsumeActivity;
import com.baima.massagemanager.CustomerAdapter;
import com.baima.massagemanager.CustomerMessageActivity;
import com.baima.massagemanager.R;
import com.baima.massagemanager.RechargeActivity;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.util.PersonUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CustomerFragment extends Fragment {

    private static final String TAG = "CustomerFragment";

    private static final int ADD = 1;
    private static final int MESSAGE = 2;
    private static final int RECHARGE = 3;
    private static final int CONSUME = 4;

    private List<Customer> customerList = new ArrayList<>();
    private CustomerAdapter adapter;
    private ListView lv_customer;
    private TextView tv_clear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        final EditText et_search = (EditText) view.findViewById(R.id.et_search);
        tv_clear = (TextView) view.findViewById(R.id.tv_clear);
        TextView tv_add = view.findViewById(R.id.tv_add);
        lv_customer = view.findViewById(R.id.lv_customer);

        adapter = new CustomerAdapter(getActivity(), customerList) {
            @Override
            public void onRechargeClick(long customerId) {
                //打开充值活动
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra("customerId", customerId);
                startActivityForResult(intent, RECHARGE);
            }

            @Override
            public void onConsumeClick(long customerId) {
                //打开消费活动
                Intent intent = new Intent(getActivity(), ConsumeActivity.class);
                intent.putExtra("customerId", customerId);
                startActivityForResult(intent, CONSUME);
            }
        };

        lv_customer.setAdapter(adapter);
        refreshCustomerList();

        //查找 编辑框内容变化 事件
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                if (text.length() > 0) {
                    tv_clear.setVisibility(View.VISIBLE);
                    customerList.clear();
                    //查找 编号 姓名
                    customerList.addAll(
                            LitePal.where("number=? or name like ?", text, "%" + text + "%")
                                    .order("number").find(Customer.class)
                    );
                    if (text.length() > 2) {
                        //如果 字符 串个数大于2再查找 手机号
                        customerList.addAll(
                                LitePal.where("phoneNumber like ?", "%" + text + "%")
                                        .order("number").find(Customer.class)
                        );
                    }
                    //删除重复的
                    for (int i = 0; i < customerList.size(); i++) {
                        for (int j = i + 1; j < customerList.size(); j++) {
                            if (customerList.get(i).getId() == customerList.get(j).getId()) {
                                customerList.remove(j);
                                j--;
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    //如果 查找 编辑框内容为空，显示 所有顾客 ，隐藏清空标签
                    refreshCustomerList();
                    tv_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //清空标签的点击 事件
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                tv_clear.setVisibility(View.GONE);
            }
        });

        //添加的点击 事件
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开添加客户界面
                et_search.setText("");
                Intent intent = new Intent(getActivity(), AddCustomerStaffActivity.class);
                int newNumber = PersonUtil.getNewNumber(Customer.class);
                intent.putExtra("number", newNumber);
                intent.putExtra("type", AddCustomerStaffActivity.ADD_CUSTOMER);
                startActivityForResult(intent, ADD);
            }
        });

        //顾客 列表项目的点击 事件，查看顾客 信息
        lv_customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = customerList.get(position);
                long customerId = customer.getId();
                Intent intent = new Intent(getActivity(), CustomerMessageActivity.class);
//                intent.putExtra(CustomerMessageActivity.EXTRA_PERSON_ID,customerId);
                intent.putExtra("personId",customerId);
                startActivityForResult(intent, MESSAGE);
            }
        });

        //顾客列表项目长按事件，删除
        lv_customer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteDialog(position);
                return true;
            }
        });
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            refreshCustomerList();
        }
    }

    //显示 删除对话框
    private void showDeleteDialog(final int position) {
        final Customer customer = customerList.get(position);
        int number = customer.getNumber();
        String name = customer.getName();
        String message = "你确定删除 " + number + "号" + name + " 吗？";
        new AlertDialog.Builder(getActivity())
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
                        customerList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    //刷新 顾客 列表
    public void refreshCustomerList() {
        customerList.clear();
        customerList.addAll(
                LitePal.order("number").find(Customer.class)
        );
        adapter.notifyDataSetChanged();
    }
}
