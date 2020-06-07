package com.baima.massagemanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.baima.massagemanager.EditActivity;
import com.baima.massagemanager.R;
import com.baima.massagemanager.StaffAdapter;
import com.baima.massagemanager.StaffMessageActivity;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class StaffFragment extends Fragment implements View.OnClickListener, StaffAdapter.OnItemListener, TextWatcher, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private static final int ADD = 1;
    private static final int ALTER_HOUR_PERCENTAGE = 2;
    private static final int STAFF_MESSAGE = 3;

    private List<Staff> staffList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    //小时提成
    private double hourPercentage;
    private TextView tv_hour_percentage;
    private StaffAdapter adapter;
    private EditText et_search;
    private TextView tv_clear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff, container, false);
        et_search = (EditText) view.findViewById(R.id.et_search);
        tv_clear = (TextView) view.findViewById(R.id.tv_clear);
        tv_hour_percentage = (TextView) view.findViewById(R.id.tv_hour_percentage);
        TextView tv_add = view.findViewById(R.id.tv_add);
        ListView lv_staff = view.findViewById(R.id.lv_staff);
        tv_hour_percentage.setVisibility(View.VISIBLE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        hourPercentage = sharedPreferences.getFloat("hourPercentage", 0F);
        tv_hour_percentage.setText("提成：" + StringUtil.doubleTrans(hourPercentage, true));

        adapter = new StaffAdapter(getActivity(), staffList);
        lv_staff.setAdapter(adapter);
        refreshListData();

        adapter.setOnItemListener(this);
        et_search.addTextChangedListener(this);
        tv_clear.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_hour_percentage.setOnClickListener(this);
        lv_staff.setOnItemLongClickListener(this);
        lv_staff.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), StaffMessageActivity.class);
        intent.putExtra("staffId", staffList.get(position).getId());
        startActivityForResult(intent, STAFF_MESSAGE);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //列表框项目的长按，弹出 删除对话框
        showDeleteStaffDialog(position);
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //查找 编辑框的监听
        String text = s.toString().trim();
        if (text.length() > 0) {
            tv_clear.setVisibility(View.VISIBLE);
            staffList.clear();
            //查找 编号 和姓名
            staffList.addAll(
                    LitePal.where("number=? or name like ?", text, "%" + text + "%")
                            .order("number").find(Staff.class)
            );
            //查找 手机号
            if (text.length() > 2) {
                staffList.addAll(
                        LitePal.where("phoneNumber like ?", "%" + text + "%")
                                .order("number").find(Staff.class)
                );
            }

            //移除重复
            for (int i = 0; i < staffList.size(); i++) {
                for (int j = i + 1; j < staffList.size(); j++) {
                    if (staffList.get(i).getId() == staffList.get(j).getId()) {
                        staffList.remove(j);
                        j--;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            tv_clear.setVisibility(View.GONE);
            refreshListData();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                et_search.setText("");
                //打开添加员工活动
                Intent intent = new Intent(getActivity(), AddCustomerStaffActivity.class);
                intent.putExtra("type", AddCustomerStaffActivity.ADD_STAFF);
                startActivityForResult(intent, ADD);
                break;
            case R.id.tv_clear:
                //清空
                et_search.setText("");
                tv_clear.setVisibility(View.GONE);
                break;
            case R.id.tv_hour_percentage:
                //修改提成
                Intent intent1 = new Intent(getActivity(), EditActivity.class);
                startActivityForResult(intent1, ALTER_HOUR_PERCENTAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ALTER_HOUR_PERCENTAGE:
                if (resultCode == getActivity().RESULT_OK) {
                    //修改小时提成
                    hourPercentage = Double.valueOf(data.getStringExtra("inputData"));
                    tv_hour_percentage.setText("提成：" + StringUtil.doubleTrans(hourPercentage, true));
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putFloat("hourPercentage", (float) hourPercentage);
                    edit.apply();
                    return;
                }
        }

        if (resultCode==getActivity().RESULT_OK){
            refreshListData();
        }
    }

    @Override
    public void onRecordClick(long staffId) {
//列表项目的组件 点击 事件
    }

    //刷新列表
    private void refreshListData() {
        staffList.clear();
        staffList.addAll(
                LitePal.order("number").find(Staff.class)
        );
        adapter.notifyDataSetChanged();
    }

    //显示 删除员工对话框
    public void showDeleteStaffDialog(final int position) {
        final Staff staff = staffList.get(position);
        String numberName = staff.getNumber() + "号" + staff.getName();
        String msg = "你确定删除 " + numberName + " 吗？";
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        staff.delete();
                        staffList.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();
    }
}
