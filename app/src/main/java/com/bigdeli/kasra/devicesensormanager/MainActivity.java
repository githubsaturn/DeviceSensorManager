package com.bigdeli.kasra.devicesensormanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    // Debugging
    private final String TAG = AppConfig.getClassName(this);
    private final boolean D = AppConfig.IS_DEBUG_ON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView sensorListView = (ListView) findViewById(R.id.listViewSensors);
        List<SensorViewData> sensorViewDatas = new ArrayList<>();
        for (int idx = 0; idx < 5; idx++) {
            SensorViewData a = new SensorViewData();
            sensorViewDatas.add(a);
        }
        SensorListAdapter listAdapter = new SensorListAdapter(sensorViewDatas);
        sensorListView.setAdapter(listAdapter);

    }


    public void addSensorClicked(View v) {
        Intent intent = new Intent(this, AddSensorActivity.class);
        startActivity(intent);
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


    private class SensorViewData {
        String name;
        String vendor;
        String type;
    }

    private class SensorListAdapter extends BaseAdapter {

        List<SensorViewData> mSensorViewDatas;

        SensorListAdapter(List<SensorViewData> sensorItems) {
            this.mSensorViewDatas = sensorItems;
        }

        @Override
        public int getCount() {
            return mSensorViewDatas.size();
        }

        @Override
        public SensorViewData getItem(int position) {
            return mSensorViewDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.sensor_view, parent, false);
            }

//            TextView sensorName = (TextView) convertView.findViewById(R.id.textName);
//            TextView sensorType = (TextView) convertView.findViewById(R.id.textType);
//            TextView sensorVendor = (TextView) convertView.findViewById(R.id.textVendor);

//            SensorViewData sensorItem = mSensorViewDatas.get(arg0);
//
//            sensorName.setText(sensorItem.name);
//            sensorType.setText(sensorItem.type);
//            sensorVendor.setText(sensorItem.vendor);

            return convertView;
        }

    }

}