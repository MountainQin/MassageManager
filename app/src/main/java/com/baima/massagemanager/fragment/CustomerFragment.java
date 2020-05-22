package com.baima.massagemanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.AddCustomerStaffActivity;
import com.baima.massagemanager.CustomerAdapter;
import com.baima.massagemanager.R;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.util.PersonUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class CustomerFragment extends Fragment {

    private List<Customer> customerList=new ArrayList<>();
    private CustomerAdapter adapter;
    private ListView lv_customer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_customer, container, false);
        TextView tv_search=view.findViewById(R.id.tv_search);
        TextView tv_add=view.findViewById(R.id.tv_add);
        lv_customer = view.findViewById(R.id.lv_customer);

        //顾客列表添加数据
        customerList = LitePal.order("number").find(Customer.class);
        adapter = new CustomerAdapter(getActivity(), customerList);
        lv_customer.setAdapter(adapter);

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
                switch (requestCode){
            case 1:
                if (resultCode==getActivity().RESULT_OK){
                    //刷新 列表
                    customerList=LitePal.order("number").find(Customer.class);
                    adapter=new CustomerAdapter(getActivity(),customerList);
                    lv_customer.setAdapter(adapter);
//lv_customer.smoothScrollToPosition();
                }
                break;
        }
    }

    //显示 删除对话框
    private void showDeleteDialog(final int position){
        final Customer customer = customerList.get(position);
        int number = customer.getNumber();
        String name = customer.getName();
        String message="你确定删除 " +number +"号" +name +" 吗？";
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(message)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LitePal.delete(Customer.class, customer.getId());
                        customerList.remove(position);
                                adapter.notifyDataSetChanged();
                    }
                })
                .show();
    }
}
