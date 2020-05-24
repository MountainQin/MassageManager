package com.baima.massagemanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.AddCustomerStaffActivity;
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

    private List<Customer> customerList = new ArrayList<>();
    private CustomerAdapter adapter;
    private ListView lv_customer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        TextView tv_search = view.findViewById(R.id.tv_search);
        TextView tv_add = view.findViewById(R.id.tv_add);
        lv_customer = view.findViewById(R.id.lv_customer);

        adapter = new CustomerAdapter(getActivity(), customerList) {
            @Override
            public void onRechargeClick(long customerId) {
                //打开充值活动
                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                intent.putExtra("customerId", customerId);
                startActivityForResult(intent, 3);
            }
        };
        lv_customer.setAdapter(adapter);
        refreshCustomerList();

        //查找 的点击 事件
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "search customer", Toast.LENGTH_SHORT).show();
            }
        });
        //添加的点击 事件
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开添加客户界面
                Intent intent = new Intent(getActivity(), AddCustomerStaffActivity.class);
                int newNumber = PersonUtil.getNewNumber(Customer.class);
                intent.putExtra("number", newNumber);
                intent.putExtra("type", AddCustomerStaffActivity.ADD_CUSTOMER);
                startActivityForResult(intent, 1);
            }
        });

        //顾客 列表项目的点击 事件，查看顾客 信息
        lv_customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = customerList.get(position);
                long customerId = customer.getId();
                Intent intent = new Intent(getActivity(), CustomerMessageActivity.class);
                intent.putExtra("customerId", customerId);
                startActivityForResult(intent, 2);
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
    private void refreshCustomerList() {
        customerList.clear();
        customerList.addAll(
                LitePal.order("number").find(Customer.class)
        );
        adapter.notifyDataSetChanged();
    }
}
