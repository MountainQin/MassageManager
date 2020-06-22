package com.baima.massagemanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.litepal.LitePal;

/**
 * 只是用来和litepal同步
 */
public class DBOperHelper extends SQLiteOpenHelper {

    private OnUpgradeListener onUpgradeListener;
    private boolean upgrading;

    public DBOperHelper(Context context) {
        super(context, "sqlite.db", null,
                LitePal.getDatabase().getVersion());
    }

    public void setOnUpgradeListener(OnUpgradeListener onUpgradeListener) {
        this.onUpgradeListener = onUpgradeListener;
    }

    public boolean isUpgrading() {
        return upgrading;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public interface OnUpgradeListener {
        /**
         * 开始更新
         */
        public abstract void onUpgradeStart();

        /**
         * 数据库升级完成
         */
        public abstract void onUpgradeFinish();
    }
}
