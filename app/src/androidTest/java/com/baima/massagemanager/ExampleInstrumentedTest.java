package com.baima.massagemanager;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.entity.WorkStaff;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void addCustomer() {
        for (int i = 0; i < 20; i++) {
            Customer customer = new Customer();
            Random random = new Random();
            customer.setNumber(random.nextInt(100) + 1);
            customer.setName(customer.getNumber()+"号顾客 ");
            customer.setRemainder(random.nextInt(20) + 1);
            customer.save();
        }
    }

    @Test
    public void addStaff() {
        for (int i = 0; i < 20; i++) {
            Staff staff = new Staff();
            Random random = new Random();
            staff.setNumber(random.nextInt(100) + 1);
            staff.setName(staff.getNumber()+"号员工 ");
            staff.setHoursOfCurrentMonth(random.nextInt(10) + 21);
            staff.save();
        }
    }

    @Test
    public void addRecharge() {
        for (int i = 0; i < 1000; i++) {
            RechargeRecord rechargeRecord = new RechargeRecord();
            long l = System.currentTimeMillis();
            Random random = new Random();
            rechargeRecord.setCustomerId(random.nextInt(20) + 1);
            int day = random.nextInt(151) ;;
            rechargeRecord.setTimeStamp(l - day * 1000 * 60 * 60 * 24l);
            rechargeRecord.setRechargeAmount(random.nextInt(401) + 800);
            rechargeRecord.setRechargeHour(random.nextInt(11) + 10);
            rechargeRecord.setRemainder(random.nextInt(21) + 10);
            rechargeRecord.save();
        }
    }

    @Test
    public void addConsumeRecord() {
        for (int i = 0; i < 3000; i++) {
            ConsumeRecord consumeRecord = new ConsumeRecord();
            long l = System.currentTimeMillis();
            Random random = new Random();
            int day = random.nextInt(151);
            consumeRecord.setConsumeTimestamp(l - day * 1000 * 60 * 60 * 24l);
            consumeRecord.setCustomerId(random.nextInt(20) + 1);

            consumeRecord.setConsumeTime((random.nextInt(7) + 1) * 0.5);
            int remainder = random.nextInt(16) + 5;
            consumeRecord.setRemainder(remainder * 0.5);
            int worktime = random.nextInt(3) + 2;
            consumeRecord.setWorkTime(worktime * 0.5);
            consumeRecord.save();

            int count = random.nextInt(2) + 1;
            for (int j = 0; j < count; j++) {
                WorkStaff workStaff = new WorkStaff();
                workStaff.setConsumeRecordId(consumeRecord.getId());
                workStaff.setStaffId(random.nextInt(20) + 1);
                workStaff.setWorkTime(worktime * 0.5);
                int time = random.nextInt(61) + 40;
                workStaff.setCurrentMonthTime(time * 0.5);
                workStaff.setConsumeTimestamp(consumeRecord.getConsumeTimestamp());
                workStaff.save();
            }
        }
    }
@Test
public void add(){
        addCustomer();
        addStaff();
        addRecharge();
        addConsumeRecord();
}
    @Test
    public void findAllLitepal() {
        Class<LitePalSupport>[] classes = new Class[]{ConsumeRecord.class, RechargeRecord.class, Customer.class, Staff.class, WorkStaff.class};
        for (Class<LitePalSupport> aClass : classes) {
            List<LitePalSupport> all = LitePal.findAll(aClass);
            Log.i("baima", aClass.getSimpleName() + all.size());
        }
    }

    @Test
    public void addRecord() {
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
    public void setCustomerName() {
        List<Customer> all = LitePal.findAll(Customer.class);
        for (Customer customer : all) {
            customer.setName(customer.getNumber() + "号顾客 ");
            customer.update(customer.getId());


            ContentValues contentValues = new ContentValues();
            contentValues.put("customeName", customer.getName());
            LitePal.updateAll(ConsumeRecord.class, contentValues, "customerId=?", String.valueOf(customer.getId()));
        }
        List<ConsumeRecord> all1 = LitePal.findAll(ConsumeRecord.class);
        for (ConsumeRecord consumeRecord : all1) {

        }
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.baima.massagemanager", appContext.getPackageName());
    }
}
