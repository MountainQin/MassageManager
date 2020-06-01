package com.baima.massagemanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baima.massagemanager.AddCustomerStaffActivity;
import com.baima.massagemanager.R;

public class StaffFragment extends Fragment implements View.OnClickListener {

    private static final int ADD = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff, container, false);
        EditText et_search = (EditText) view.findViewById(R.id.et_search);
        TextView tv_clear = (TextView) view.findViewById(R.id.tv_clear);
        TextView tv_percentage = (TextView) view.findViewById(R.id.tv_percentage);
        TextView tv_add = view.findViewById(R.id.tv_add);
        ListView lv_staff = view.findViewById(R.id.lv_staff);
        tv_percentage.setVisibility(View.VISIBLE);

        tv_add.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                //打开添加员工活动
                Intent intent = new Intent(getActivity(), AddCustomerStaffActivity.class);
                intent.putExtra("type", AddCustomerStaffActivity.ADD_STAFF);
                startActivityForResult(intent, ADD);
                break;
        }
    }
}
