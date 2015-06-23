package com.example.administrator.dbutils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Ceshibean ceshibean = new Ceshibean();
        ceshibean.setName("傻蛋");
        ceshibean.setSex("男");

        Mypersional mypersional = new Mypersional();
        mypersional.setName("李四");
        mypersional.setAge("12");
        mypersional.setMoney("2000");
        mypersional.setDate(new Date() + "");
        mypersional.setCeshibean(ceshibean);


        Fdbutils fdbutils = Fdbutils.getmFdbutilsInstance(getApplicationContext());

        fdbutils.saveBean(mypersional);







        List<Mypersional> age_query = fdbutils.query(Mypersional.class,"date");
        /*for (int i=0;i<query_id.size();i++){

            Log.w("得到的bean", "query_id" + query_id.get(i).toString());

        }*/

        for (int i=0;i<age_query.size();i++){

            Log.w("得到的bean", "age_query" + age_query.get(i).toString());
            Log.w("得到的bean", "age_query"+age_query.get(0).getCeshibean().toString());
        }

       // System.out.println(save);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
