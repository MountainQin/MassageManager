package com.baima.massagemanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.litepal.LitePal;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

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
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.baima.massagemanager", appContext.getPackageName());
    }
}
