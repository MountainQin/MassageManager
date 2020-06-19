package com.baima.massagemanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.PickDateActivity;
import com.baima.massagemanager.R;
import com.baima.massagemanager.RecordAdapter;
import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.util.CalendarUtil;
import com.baima.massagemanager.util.ConsumeRecordUtil;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordFragment extends Fragment implements View.OnClickListener, OnLoadMoreListener {

    private static final int PICK_DATE = 1;


    private List<ConsumeRecord> consumeRecordList = new ArrayList<>();
    private TextView tv_date;
    private LRecyclerView lrv_record;
    private RecordAdapter adapter;
    private Calendar calendar;
    private long startTimeInMillis;
    private long endTimeInMillis;
    private int y;
    private boolean isInitData;
    private LRecyclerViewAdapter lRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        tv_date = view.findViewById(R.id.tv_date);
        lrv_record = view.findViewById(R.id.lrv_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lrv_record.setLayoutManager(linearLayoutManager);
        adapter = new RecordAdapter(getActivity(), consumeRecordList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        lrv_record.setAdapter(lRecyclerViewAdapter);

        initData();
        consumeRecordList.clear();
        loadMoreLeast20();
        refreshTvDate();


        tv_date.setOnClickListener(this);
        lrv_record.setOnLoadMoreListener(this);
        //如果不设置下拉 的时候会出现 正在刷新
        lrv_record.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                lrv_record.refreshComplete(0);
            }
        });
        return view;
    }

    @Override
    public void onLoadMore() {
        loadMoreLeast20();
        lrv_record.refreshComplete(0);

        LinearLayoutManager layoutManager = (LinearLayoutManager) lrv_record.getLayoutManager();
        if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
            Toast.makeText(getActivity(), "已经加载到2020年1月1日！", Toast.LENGTH_SHORT).show();
        }
        refreshTvDate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                Intent intent = new Intent(getActivity(), PickDateActivity.class);
                intent.putExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                intent.putExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                startActivityForResult(intent, PICK_DATE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case PICK_DATE:
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
        refreshListData(startTimeInMillis, endTimeInMillis);
    }


    //根据指定起始时间戳刷新 列表数据
    private void refreshListData(long startTimeInMillis, long endTimeInMillis) {
        consumeRecordList.clear();
        consumeRecordList.addAll(getConsumeRecordList(startTimeInMillis, endTimeInMillis));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


    //根据起始时间获取 消费记录，
    private List<ConsumeRecord> getConsumeRecordList(long startTimeInMillis, long endTimeInMillis) {
        List<ConsumeRecord> list = LitePal.where("consumeTimestamp >=? and consumeTimestamp<?", String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(ConsumeRecord.class);
        return ConsumeRecordUtil.sortConsumeTimestampDesc(list);
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
    }

    //往前一天加载，至少20
    private void loadMoreLeast20() {
        List<ConsumeRecord> list = new ArrayList<>();
        calendar.setTimeInMillis(startTimeInMillis);
        while (list.size() < 20) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (calendar.get(Calendar.YEAR) < 2020) {
                break;
            }
            list.addAll(getConsumeRecordList(calendar.getTimeInMillis(), startTimeInMillis));
            startTimeInMillis = calendar.getTimeInMillis();
        }
        consumeRecordList.addAll(list);
        adapter.notifyDataSetChanged();
    }
}
