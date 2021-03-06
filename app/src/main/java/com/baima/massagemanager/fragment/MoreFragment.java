package com.baima.massagemanager.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.HelpActivity;
import com.baima.massagemanager.MainActivity;
import com.baima.massagemanager.R;
import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.entity.WorkStaff;
import com.baima.massagemanager.util.BackupRetuceUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.LitePalSupport;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MoreFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final int BACKUP = 1;
    private static final int RETUCE = 2;


    //备份的文件的路径
    private String filePath = new File(Environment.getExternalStorageDirectory(), "BaiMa/MassageManager/db.json").getAbsolutePath();
    private String propertiesPath = new File(Environment.getExternalStorageDirectory(), "BaiMa/MassageManager/dsp.json").getAbsolutePath();
    private Class<LitePalSupport>[] classes = new Class[]{ConsumeRecord.class, RechargeRecord.class, Customer.class, Staff.class, WorkStaff.class};

    private int backupOrRetuce;
    private CheckBox cb_timestamp_flag;
    private TextView tv_version;
    private TextView tv_about;
    private SharedPreferences defaultSharedPreferences;
    private TextView tv_backup;
    private TextView tv_retuce;
    private TextView tv_help;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        cb_timestamp_flag = view.findViewById(R.id.cb_timestamp_flag);
        tv_backup = view.findViewById(R.id.tv_backup);
        tv_retuce = view.findViewById(R.id.tv_retuce);
        tv_help = view.findViewById(R.id.tv_help);
        tv_version = view.findViewById(R.id.tv_version);
        tv_about = view.findViewById(R.id.tv_about);

        cb_timestamp_flag.setOnCheckedChangeListener(this);
        tv_backup.setOnClickListener(this);
        tv_retuce.setOnClickListener(this);
        tv_help.setOnClickListener(this);

        cb_timestamp_flag.setChecked(MainActivity.isShowTimestampFlag);
//设置版本信息
        PackageManager packageManager = getActivity().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            tv_version.setText("版本：" + versionName);
        } catch (PackageManager.NameNotFoundException e) {


        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_backup:
                backupOrRetuce = BACKUP;
                requestPermissionBackupRetuce();
                break;
            case R.id.tv_retuce:
                backupOrRetuce = RETUCE;
                requestPermissionBackupRetuce();
                break;
            case R.id.tv_help:
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MainActivity.isShowTimestampFlag = isChecked;
        MainActivity.recordFragment.refreshListData();

        //保存
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putBoolean(MainActivity.IS_SHOW_TIMESTAMP_FLAG, isChecked);
        edit.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backupRetuce();
                }
                break;
        }
    }

    //申请 权限备份还原
    private void requestPermissionBackupRetuce() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            backupRetuce();
        }
    }

    //备份还原
    private void backupRetuce() {
        switch (backupOrRetuce) {
            case BACKUP:
                showBackupDialog();
                break;
            case RETUCE:
                //如果 是还原没有备份文件直接 返回
                if (!new File(filePath).exists()) {
                    Toast.makeText(getActivity(), "没有备份的文件，请重试！", Toast.LENGTH_SHORT).show();
                    return;
                }
                showRetuceDialog();
                break;
        }
    }

    //显示 备份对话框
    private void showBackupDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("你确定要备份吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("正在备份，请稍候！");
                        progressDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BackupRetuceUtil.backup(filePath, classes);
                                    //备份小时金额和提成
                                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    double hourPercentage = defaultSharedPreferences.getFloat("hourPercentage", 0F);
                                    double hourPrice = defaultSharedPreferences.getFloat("hourPrice", 88.0F);
                                    Map<String, Double> map = new HashMap<>();
                                    map.put("hourPrice", hourPrice);
                                    map.put("hourPercentage", hourPercentage);

                                    JSONObject jsonObject = new JSONObject(map);
                                    FileWriter fileWriter = new FileWriter(propertiesPath);
                                    fileWriter.write(jsonObject.toString());
                                    fileWriter.flush();
                                    fileWriter.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "备份失败，请检查 重试！", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();

                            }
                        }).start();
                    }
                })
                .show();

    }

    //显示 还原确认对话框
    private void showRetuceDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("你确定还原吗？这将删除现有的数据！")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String msg = "正在还原，请稍候！";
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage(msg);
                        progressDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BackupRetuceUtil.retuce(filePath, classes);
                                    //还原小时金额提成
                                    FileReader fileReader = new FileReader(propertiesPath);
                                    StringBuffer stringBuffer = new StringBuffer();
                                    int len = -1;
                                    char[] chars = new char[1024];
                                    while ((len = fileReader.read(chars)) != -1) {
                                        stringBuffer.append(chars, 0, len);
                                    }
                                    fileReader.close();

                                    JSONObject jsonObject = new JSONObject(stringBuffer.toString());
                                    double hourPrice = jsonObject.getDouble("hourPrice");
                                    double hourPercentage = jsonObject.getDouble("hourPercentage");

                                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                                    edit.putFloat("hourPrice", (float) hourPrice);
                                    edit.putFloat("hourPercentage", (float) (hourPercentage));
                                    edit.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "解析文件错误！", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "读取文件错误！", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();

                                //还原成功后重新打开主界面
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).start();

                    }
                })
                .show();

    }

}