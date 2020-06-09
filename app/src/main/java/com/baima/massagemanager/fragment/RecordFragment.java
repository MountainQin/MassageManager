package com.baima.massagemanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baima.massagemanager.R;
import com.baima.massagemanager.RecordAdapter;
import com.baima.massagemanager.PickDateActivity;
import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.util.CalendarUtil;
import com.baima.massagemanager.util.ConsumeRecordUtil;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int SELECT_DATE = 1;


    private List<ConsumeRecord> consumeRecordList = new ArrayList<>();
    private TextView tv_date;
    private RecyclerView rv_record;
    private RecordAdapter adapter;
    private Calendar calendar;
    private long startTimeInMillis;
    private long endTimeInMillis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        tv_date = view.findViewById(R.id.tv_date);
        rv_record = view.findViewById(R.id.rv_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_record.setLayoutManager(linearLayoutManager);
        adapter = new RecordAdapter(getActivity(), consumeRecordList);
        rv_record.setAdapter(adapter);
        initData();
        refreshTvDate();


        tv_date.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), PickDateActivity.class);
        intent.putExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
        intent.putExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
        startActivityForResult(intent, SELECT_DATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case SELECT_DATE:
                    startTimeInMillis = data.getLongExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                    endTimeInMillis = data.getLongExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                    refreshListData(startTimeInMillis, endTimeInMillis);
                    refreshTvDate();
                    break;
            }
        }
    }


    //根据指定起始时间戳刷新 列表数据
    private void refreshListData(long startTimeInMillis, long endTimeInMillis) {
        consumeRecordList.clear();
        consumeRecordList.addAll(
                LitePal.where("consumeTimestamp >=? and consumeTimestamp<?", String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                        .order("consumeTimestamp desc").find(ConsumeRecord.class)
        );
        adapter.notifyDataSetChanged();
    }


    //时间格式化，年月日
    private String datEFormat(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return simpleDateFormat.format(date);
    }


    //刷新 日期标签
    private void refreshTvDate() {
        String s = datEFormat(startTimeInMillis);
        String s1 = datEFormat(endTimeInMillis);
        tv_date.setText(s + " - " + s1);
    }

    //初始化数据
    private void initData() {
        calendar = Calendar.getInstance();
        //小时分钟秒毫秒清零
        CalendarUtil.setTimeTo0(calendar);
//           startTimeInMillis = calendar.getTimeInMillis();
//往后一天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        startTimeInMillis = calendar.getTimeInMillis();
        endTimeInMillis = calendar.getTimeInMillis();

        while (consumeRecordList.size() < 50) {
            //如果 项目数小于50，再往前 一天查找 ，一直到20200101，
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (calendar.get(Calendar.YEAR) < 2020) {
                break;
            }
            consumeRecordList.addAll(ConsumeRecordUtil.getConsumeRecordListPreviousDay(startTimeInMillis));
            startTimeInMillis = calendar.getTimeInMillis();
        }
        adapter.notifyDataSetChanged();
    }
}
