package com.baima.massagemanager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.ScreenUtil;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConsumeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MONTH_TIME_LATER = 1;
    private static final int CONSUME_TIME = 2;
    private static final int REMAINDER_LATER = 3;
    private List<ConsumeRecord> consumeRecordList = new ArrayList<>();
    private TextView tv_select_staff;
    private long timeMillis;
    private Calendar calendar;
    private Customer customer;
    private LinearLayout layout_staff;
    private TextView tv_consume_time;
    private TextView tv_remainder_later;
    private double remainderLater;
    private double consumeTime;
    private TextView tv_date_time;
    private EditText et_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消费");
        setContentView(R.layout.activity_consume);

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                saveData();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_staff:
                showSelectStaffPopWindow();
                break;
            case R.id.tv_consume_time:
                Intent intent = new Intent(ConsumeActivity.this, EditActivity.class);
                startActivityForResult(intent, CONSUME_TIME);
                break;
            case R.id.tv_remainder_later:
                Intent intent1 = new Intent(ConsumeActivity.this, EditActivity.class);
                startActivityForResult(intent1, REMAINDER_LATER);
                break;
            case R.id.tv_date_time:
                showSelectDateTimePopWindow();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            double aDouble = data.getDoubleExtra("aDouble ", 0);
            switch (requestCode) {
                case MONTH_TIME_LATER:
                    //修改标签内容，修改消费记录的员工时间
                    int childId = data.getIntExtra("childId", 0);
                    consumeRecordList.get(childId).setCurrentMonthTime(aDouble);
                    LinearLayout layoutStaffChild = (LinearLayout) layout_staff.getChildAt(childId);
                    ((TextView) layoutStaffChild.getChildAt(3)).setText("= " + StringUtil.doubleTrans(aDouble));
                    break;
                case CONSUME_TIME:
                    tv_consume_time.setText("-" + StringUtil.doubleTrans(aDouble) + "小时");
                    consumeTime = aDouble;
                    remainderLater = customer.getRemainder() - consumeTime;
                    tv_remainder_later.setText("= " + StringUtil.doubleTrans(remainderLater));
                    refreshDateTime();
                    break;
                case REMAINDER_LATER:
                    tv_remainder_later.setText("= " + StringUtil.doubleTrans(aDouble));
                    remainderLater = aDouble;
                    break;
            }
        }
    }

    private void initViews() {
        ScrollView scroll_view = findViewById(R.id.scroll_view);
        TextView tv_number_name = findViewById(R.id.tv_number_name);
        layout_staff = findViewById(R.id.layout_staff);
        tv_select_staff = findViewById(R.id.tv_select_staff);

        TextView tv_remainder = findViewById(R.id.tv_remainder);
        tv_consume_time = findViewById(R.id.tv_consume_time);
        tv_remainder_later = findViewById(R.id.tv_remainder_later);

        tv_date_time = findViewById(R.id.tv_date_time);
        et_remark = findViewById(R.id.et_remark);

        //设置编号 姓名
        Intent intent = getIntent();
        long customerId = intent.getLongExtra("customerId", 0);
        if (customerId > 0) {
            List<Customer> customerList = LitePal.where("id=?", String.valueOf(customerId)).find(Customer.class);
            if (customerList.size() > 0) {
                customer = customerList.get(0);
                tv_number_name.setText(customer.getNumber() + "号 " + customer.getName());
                tv_remainder.setText(StringUtil.doubleTrans(customer.getRemainder()));
                refreshStaffConsumeData();
            }
        }

        //日期时间
        calendar = Calendar.getInstance();
        //秒数毫秒数清0
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timeMillis = calendar.getTimeInMillis();
        tv_date_time.setText(new Date(timeMillis).toLocaleString());

        tv_select_staff.setOnClickListener(this);
        tv_consume_time.setOnClickListener(this);
        tv_remainder_later.setOnClickListener(this);
        tv_date_time.setOnClickListener(this);
    }

    //保存数据
    private void saveData() {
        //如果 没选择员工,设置员工ID为-1
        if (consumeRecordList.size() == 0) {
            ConsumeRecord consumeRecord = new ConsumeRecord();
            setCustomerData(consumeRecord);
            consumeRecord.setStaffId(-1);
            consumeRecord.setWorkTime(consumeTime);
            consumeRecord.save();
        }
        //如果 选择了员工
        for (ConsumeRecord consumeRecord : consumeRecordList) {
            setCustomerData(consumeRecord);
            consumeRecord.save();
        }

        //保存顾客 表的数据
        customer.setRemainder(remainderLater);
        customer.update(customer.getId());
        Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    //显示 选择员工悬浮 窗口
    private void showSelectStaffPopWindow() {
        //员工悬浮 窗
        final PopupWindow pw_staff = new PopupWindow(tv_select_staff, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.select_staff, null);
        final TextView tv_select_mode = view.findViewById(R.id.tv_select_mode);
        final TextView tv_ok = view.findViewById(R.id.tv_ok);
        final ListView lv_staff = view.findViewById(R.id.lv_staff);

        //员工列表
        ArrayList<String> titleList = new ArrayList<>();
        final List<Staff> staffList = LitePal.order("number").find(Staff.class);
        for (Staff staff : staffList) {
            titleList.add(staff.getNumber() + "号 " + staff.getName());
        }
        final SelectStaffAdapter adapter = new SelectStaffAdapter(this, titleList);
        lv_staff.setAdapter(adapter);
        //是否选中的数组
        final boolean[] checks = new boolean[staffList.size()];

        //显示 员工悬浮 窗
        pw_staff.setContentView(view);
        pw_staff.setBackgroundDrawable(new ColorDrawable(0x88888888));
        if (!pw_staff.isShowing()) {
            pw_staff.showAtLocation(tv_select_staff, Gravity.CENTER, 0, 0);
        }

        //单选多选的点击 事件
        tv_select_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换选择模式，多选项目就显示复选框，显示 完成按钮，单选就隐藏
                adapter.setMoreSelect(!adapter.isMoreSelect());
                boolean moreSelect = adapter.isMoreSelect();
                if (moreSelect) {
                    tv_select_mode.setText("单选");
                    Toast.makeText(ConsumeActivity.this, "切换到多选", Toast.LENGTH_SHORT).show();
                    tv_ok.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ConsumeActivity.this, "切换到单选", Toast.LENGTH_SHORT).show();
                    tv_select_mode.setText("多选");
                    tv_ok.setVisibility(View.INVISIBLE);
                }
            }
        });

        //完成的点击 事件
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加选中的员工
                for (int i = 0; i < checks.length; i++) {
                    if (checks[i]) {
                        addStaffToLayout(staffList.get(i));
                    }
                }
                pw_staff.dismiss();
            }
        });

        //列表框项目点击 事件
        lv_staff.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (adapter.isMoreSelect()) {
//如果 是多选模式，设置项目的选中状态
                    CheckBox cb = ((SelectStaffAdapter.ViewHolder) view.getTag()).cb;
                    cb.setChecked(!cb.isChecked());
                    if (cb.isChecked()) {
                        Toast.makeText(ConsumeActivity.this, "已选中", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ConsumeActivity.this, "未选中", Toast.LENGTH_SHORT).show();
                    }

                    checks[position] = cb.isChecked();
                    //设置完成按钮
                    int count = 0;
                    for (boolean check : checks) {
                        if (check) {
                            count++;
                        }
                    }
                    tv_ok.setText("确定(" + count + ")");
                } else {
                    //如果 是单选模式，添加员工
                    Staff staff = staffList.get(position);
                    addStaffToLayout(staff);
                    pw_staff.dismiss();
                }

            }
        });

    }

    //选择日期时间悬浮 窗
    private void showSelectDateTimePopWindow() {
        PopupWindow popupWindow = new PopupWindow(tv_date_time, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        View view = getLayoutInflater().inflate(R.layout.date_time_picker, null);
        DatePicker date_picker = view.findViewById(R.id.date_picker);
        TimePicker time_picker = view.findViewById(R.id.time_picker);

        time_picker.setIs24HourView(true);
        time_picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        time_picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x88888888));
        popupWindow.setContentView(view);
        if (!popupWindow.isShowing()) {
            popupWindow.showAtLocation(tv_date_time, Gravity.CENTER, 0, 0);
        }

        //java.lang.NoSuchMethodError:
//        date_picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
        date_picker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                tv_date_time.setText(new Date(calendar.getTimeInMillis()).toLocaleString());
            }
        });
        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                tv_date_time.setText(new Date(calendar.getTimeInMillis()).toLocaleString());
            }
        });
    }

    //添加员工到布局
    @SuppressLint("ServiceCast")
    private void addStaffToLayout(final Staff staff) {
        final View view = getLayoutInflater().inflate(R.layout.child_add_staff, null);
        final TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_current_month_time = view.findViewById(R.id.tv_current_month_time);
        final TextView tv_work_time = view.findViewById(R.id.tv_work_time);
        final TextView tv_month_time_later = view.findViewById(R.id.tv_month_time_later);

        view.setMinimumHeight(ScreenUtil.getScreenHeightPix(this) / 10);
        String name = staff.getNumber() + "号 " + staff.getName();
        tv_name.setText(name);

        final double currentMonthTime = staff.getHoursOfCurrentMonth();
        tv_current_month_time.setText(StringUtil.doubleTrans(currentMonthTime));
        double workTime = 1;
        tv_work_time.setText("+ " + StringUtil.doubleTrans(workTime) + "小时");
        tv_month_time_later.setText("= " + StringUtil.doubleTrans(currentMonthTime +
                workTime));

        layout_staff.addView(view);

        //保存消费记录到集合
        ConsumeRecord consumeRecord = new ConsumeRecord();
        consumeRecord.setStaffId(staff.getId());
        consumeRecord.setWorkTime(workTime);
        consumeRecord.setCurrentMonthTime(currentMonthTime + workTime);
        consumeRecordList.add(consumeRecord);

        refreshDateTime();
        //刷新 顾客 消费数据
        refreshStaffConsumeData();

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                //显示 删除对话框
                new AlertDialog.Builder(ConsumeActivity.this)
                        .setTitle("提示")
                        .setMessage("你确定删除" + tv_name.getText().toString() + "吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //从集合中删除员工
                                consumeRecordList.remove(layout_staff.indexOfChild(view));

                                //刷新 顾客 的消费剩余数据
                                refreshStaffConsumeData();
                                refreshDateTime();
                                layout_staff.removeView(view);

                            }
                        })
                        .show();
                return true;
            }
        });

        //员工的工作时间点击 事件
        tv_work_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //选择时间的菜单
                PopupMenu popupMenu = new PopupMenu(ConsumeActivity.this, tv_work_time, Gravity.CENTER);
                Menu menu = popupMenu.getMenu();
                for (int i = 0; i < 20; i++) {
                    menu.add(0, i, 0,
                            StringUtil.doubleTrans((i + 1) * 0.5) + "小时");
                }
                popupMenu.getMenuInflater().inflate(R.menu.select_time, menu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        double workTime = (item.getItemId() + 1) * 0.5;

                        //设置员工顾客 数据
                        int childId = layout_staff.indexOfChild(view);
                        consumeRecordList.get(childId).setWorkTime(workTime);
                        double monthTimeLater = staff.getHoursOfCurrentMonth() + workTime;
                        consumeRecordList.get(childId).setCurrentMonthTime(monthTimeLater);
                        tv_work_time.setText("+ " + StringUtil.doubleTrans(workTime) + "小时");
                        tv_month_time_later.setText("= " + StringUtil.doubleTrans(monthTimeLater));

                        refreshStaffConsumeData();
                        refreshDateTime();
                        return true;
                    }
                });

            }
        });

        //本月之后时间点击 事件，打开编辑界面
        tv_month_time_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsumeActivity.this, EditActivity.class);
                intent.putExtra("childId", layout_staff.indexOfChild(view));
                startActivityForResult(intent, MONTH_TIME_LATER);
            }
        });
    }

    //获取 消费后的剩余小时
    private double getRemainderLater() {
        return customer.getRemainder() - getConsumeTime();
    }

    //获取 顾客 的消费数,所有选择的员工的工作加起来
    private double getConsumeTime() {
        double consumeCount = 0;
        for (ConsumeRecord consumeRecord : consumeRecordList) {
            consumeCount += consumeRecord.getWorkTime();
        }
        return consumeCount;
    }


    //刷新顾客 消费数据
    private void refreshStaffConsumeData() {
        consumeTime = getConsumeTime();
        remainderLater = getRemainderLater();
        tv_consume_time.setText("-" + StringUtil.doubleTrans(getConsumeTime()) + "小时");
        tv_remainder_later.setText("= " + StringUtil.doubleTrans(getRemainderLater()));

    }

    //刷新 日期时间，减去员工最大工作的时间
    private void refreshDateTime() {
        double maxWorkTime = 0;
        for (ConsumeRecord consumeRecord : consumeRecordList) {
            if (consumeRecord.getWorkTime() > maxWorkTime) {
                maxWorkTime = consumeRecord.getWorkTime();
            }
        }

        long workMillis = (long) (maxWorkTime * 1000 * 60 * 60);

        calendar.setTimeInMillis(timeMillis - workMillis);
        tv_date_time.setText(new Date(calendar.getTimeInMillis()).toLocaleString());
    }


    //打开编辑界面
    private void clickStartEditActivity(TextView textView, final int requestCode, final int childIndex, final String prefix) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConsumeActivity.this, EditActivity.class);
                intent.putExtra("childId", childIndex);
                intent.putExtra("prefix", prefix);
                startActivityForResult(intent, requestCode);

            }
        });
    }

    //设置顾客 数据
    private void setCustomerData(ConsumeRecord consumeRecord) {
        consumeRecord.setConsumeTimestamp(calendar.getTimeInMillis());
        consumeRecord.setCustomerId(customer.getId());
        consumeRecord.setConsumeTime(consumeTime);
        consumeRecord.setRemainder(remainderLater);
        consumeRecord.setCustomeName(customer.getName());
        consumeRecord.setRemark(et_remark.getText().toString());
    }
}