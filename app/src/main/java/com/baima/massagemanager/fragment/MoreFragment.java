package com.baima.massagemanager.fragment;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baima.massagemanager.MainActivity;
import com.baima.massagemanager.R;

public class MoreFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cb_timestamp_flag;
    private TextView tv_version;
    private TextView tv_about;
    private SharedPreferences defaultSharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        cb_timestamp_flag = view.findViewById(R.id.cb_timestamp_flag);
        tv_version = view.findViewById(R.id.tv_version);
        tv_about = view.findViewById(R.id.tv_about);

        cb_timestamp_flag.setOnCheckedChangeListener(this);

        cb_timestamp_flag.setChecked(MainActivity.isShowTimestampFlag);
//设置版本信息
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            tv_version.setText("版本："+versionName);
        } catch (PackageManager.NameNotFoundException e) {


        }

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MainActivity.isShowTimestampFlag=isChecked;
        MainActivity.recordFragment.refreshListData();

        //保存
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putBoolean(MainActivity.IS_SHOW_TIMESTAMP_FLAG,isChecked);
        edit.apply();
    }
}
