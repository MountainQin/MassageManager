package com.baima.massagemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.baima.massagemanager.fragment.CustomerFragment;
import com.baima.massagemanager.fragment.RecordFragment;
import com.baima.massagemanager.fragment.StaffFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    public static StaffFragment staffFragment;
    public static CustomerFragment customerFragment;
    private RecordFragment recordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        fragmentList = new ArrayList<>();
        customerFragment = new CustomerFragment();
        fragmentList.add(customerFragment);
        staffFragment = new StaffFragment();
        fragmentList.add(staffFragment);
        recordFragment = new RecordFragment();
        fragmentList.add(recordFragment);

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
    }

}
