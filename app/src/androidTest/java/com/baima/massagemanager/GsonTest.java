package com.baima.massagemanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.entity.Staff;
import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GsonTest {


    @Test
    public void toJson(){
        Map map=new HashMap();
        List<ConsumeRecord> consumeRecordList = LitePal.findAll(ConsumeRecord.class);
        map                .put("consumeRecordList",consumeRecordList);
        List<RechargeRecord> rechargeRecordList = LitePal.findAll(RechargeRecord.class);
        map.put("rechargeRecordList",rechargeRecordList);
        List<Customer> customerList = LitePal.findAll(Customer.class);
        map.put("customerList",customerList);
        List<Staff> staffList = LitePal.findAll(Staff.class);
        map.put("staffList",staffList);
        String s = new Gson().toJson(map);
Log.i("baima",s);

    }

    @Test
    public void toJsonRechargeRecord(){
        List<RechargeRecord> rechargeRecordList = LitePal.findAll(RechargeRecord.class);
        Map map =new HashMap();
        map.put("rechargeRecordList",rechargeRecordList);

        Log.i("baima","hi");
        Log.i("baima",new Gson().toJson(map));
    }
    @Test
    public void addRecord(){
        Customer customer = LitePal.where("number=?", "1").find(Customer.class).get(0);
        for (int i = 0; i < 20; i++) {
            ConsumeRecord consumeRecord = new ConsumeRecord();
            consumeRecord.setCustomerId(customer.getId());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            consumeRecord.setConsumeTimestamp(System.currentTimeMillis());
            consumeRecord.setTimestampFlag(System.currentTimeMillis());
            consumeRecord.save();
        }
        Log.i("baima", "add record !");
    }

    @Test
    public void findCustomer(){
        Log.i("baima","find customer");
        List<Customer> all = LitePal.findAll(Customer.class);
        for (Customer customer : all) {
            Log.i("baima",customer.toString());
        }
    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.baima.massagemanager", appContext.getPackageName());
    }
}
