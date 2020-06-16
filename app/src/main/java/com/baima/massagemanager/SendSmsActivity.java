package com.baima.massagemanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class SendSmsActivity extends AppCompatActivity {

    private EditText et_phone_number;
    private EditText et_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        menu.findItem(R.id.item_save).setTitle("发送");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        requestPermissionSendSms();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms(et_phone_number.getText().toString().trim(),
                            et_sms.getText().toString().trim());
                } else {
                    Toast.makeText(this, "拒绝授权将无法发送短信！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initViews() {
        et_phone_number = findViewById(R.id.et_phone_number);
        et_sms = findViewById(R.id.et_sms);

        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        et_phone_number.setText(phoneNumber);
    }

    //申请 授权发送短信
    private void requestPermissionSendSms() {
        String phoneNumber = et_phone_number.getText().toString().trim();
        String sms = et_sms.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(sms)) {
            Toast.makeText(this, "你的输入为空，请检查 重试！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        } else {
            sendSms(phoneNumber, sms);
        }
    }

    //发送短信
    private void sendSms(String phoneNumber, String sms) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                phoneNumber,
                null,
                sms,
                null,
                null
        );
        finish();
    }
}
