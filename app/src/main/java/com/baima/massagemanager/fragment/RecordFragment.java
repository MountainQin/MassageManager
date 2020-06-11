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
import android.widget.Toast;

import com.baima.massagemanager.OnLoadMoreListener;
import com.baima.massagemanager.PickDateActivity;
import com.baima.massagemanager.R;
import com.baima.massagemanager.RecordAdapter;
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
    private int y;
    private boolean isInitData;

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
        rv_record.setOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(int lastPosition) {
                loadLeast20();
                refreshTvDate();
                //往前一毫秒
                calendar.setTimeInMillis(startTimeInMillis - 1);
                if (calendar.get(Calendar.YEAR) < 2020) {
                    Toast.makeText(getActivity(), "已经加载到2020年1月1日！", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    //移除消费记录
    public void removeConsumeRecord(ConsumeRecord consumeRecord) {
        for (int i = 0; i < consumeRecordList.size(); i++) {
            if (consumeRecord.getId() == consumeRecordList.get(i).getId()) {
                consumeRecordList.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    //刷新 列表数据
    public void refreshListData() {
        consumeRecordList.clear();
        consumeRecordList.addAll(
                LitePal.where("consumeTimestamp >=? and consumeTimestamp <?", String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                        .order("consumeTimestamp desc").find(ConsumeRecord.class)
        );
        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
        rv_record.scrollToPosition(0);
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
        loadLeast20();
    }

    //往前一天加载，至少20
    private void loadLeast20() {
        List<ConsumeRecord> list = new ArrayList<>();
        calendar.setTimeInMillis(startTimeInMillis);
        while (list.size() < 20) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (calendar.get(Calendar.YEAR) < 2020) {
                break;
            }
            list.addAll(ConsumeRecordUtil.getConsumeRecordListPreviousDay(startTimeInMillis));
            startTimeInMillis = calendar.getTimeInMillis();
        }
        consumeRecordList.addAll(list);
        adapter.notifyDataSetChanged();
    }
}
