package com.baima.massagemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baima.massagemanager.fragment.CustomerFragment;
import com.baima.massagemanager.fragment.MoreFragment;
import com.baima.massagemanager.fragment.RecordFragment;
import com.baima.massagemanager.fragment.StaffFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String IS_SHOW_TIMESTAMP_FLAG = "isShowTimestampFlag ";

    public static boolean isShowTimestampFlag;
    public static StaffFragment staffFragment;
    public static CustomerFragment customerFragment;
    public static RecordFragment recordFragment;

    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private MoreFragment moreFragment;
    private TextView tv_customer;
    private TextView tv_staff;
    private TextView tv_record;
    private TextView tv_more;
    private LinearLayout ll_tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isShowTimestampFlag = defaultSharedPreferences.getBoolean(IS_SHOW_TIMESTAMP_FLAG, false);

        initViews();
    }

    private void initViews() {
        viewPager = findViewById(R.id.view_pager);
        ll_tab=findViewById(R.id.ll_tab);
        tv_customer = findViewById(R.id.tv_customer);
        tv_staff = findViewById(R.id.tv_staff);
        tv_record = findViewById(R.id.tv_record);
        tv_more = findViewById(R.id.tv_more);


        fragmentList = new ArrayList<>();
        customerFragment = new CustomerFragment();
        fragmentList.add(customerFragment);
        staffFragment = new StaffFragment();
        fragmentList.add(staffFragment);
        recordFragment = new RecordFragment();
        fragmentList.add(recordFragment);
        moreFragment = new MoreFragment();
        fragmentList.add(moreFragment);

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);

        tv_customer.setOnClickListener(this);
        tv_staff.setOnClickListener(this);
        tv_record.setOnClickListener(this);
        tv_more.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_customer:
            case R.id.tv_staff:
            case R.id.tv_record:
            case R.id.tv_more:
                int i = ll_tab.indexOfChild(v);
                viewPager.setCurrentItem(i);
                break;
        }
    }
}
