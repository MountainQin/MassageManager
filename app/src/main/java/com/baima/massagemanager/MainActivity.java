package com.baima.massagemanager;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baima.massagemanager.fragment.CustomerFragment;
import com.baima.massagemanager.fragment.StaffFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        fragmentList = new ArrayList<>();
        fragmentList.add(new CustomerFragment());
        fragmentList.add(new StaffFragment());
        MyFragmentPagerAdapter adapter= new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
    }
}
