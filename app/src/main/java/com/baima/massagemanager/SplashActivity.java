package com.baima.massagemanager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import org.litepal.tablemanager.Connector;

public class SplashActivity extends AppCompatActivity implements DBOperHelper.OnUpgradeListener {

    private RelativeLayout rl_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        checkDBVersion();
    }

    private void checkDBVersion() {
        SQLiteDatabase database = Connector.getDatabase();
        DBOperHelper dbOperHelper = new DBOperHelper(this);
        dbOperHelper.setOnUpgradeListener(this);
        SQLiteDatabase readableDatabase = dbOperHelper.getReadableDatabase();
        if (!dbOperHelper.isUpgrading()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            database.close();
            readableDatabase.close();
        }
    }

    private void initViews() {
        rl_pb = findViewById(R.id.rl_pb);
    }

    @Override
    public void onUpgradeStart() {
        rl_pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUpgradeFinish() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
