package com.bigdeli.kasra.devicesensormanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AddSensorActivity extends Activity {

    // Debugging
    private final String TAG = AppConfig.getClassName(this);
    private final boolean D = AppConfig.IS_DEBUG_ON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setIcon(R.drawable.ic_launcher);
        }
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final List<SensorItem> sensorItems = new ArrayList<>();

        List<Sensor> mList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < mList.size(); i++) {
            SensorItem sensor = new SensorItem();
            sensor.name = "Name: " + mList.get(i).getName();
            sensor.type = "Type: " + convertSensorTypeToString(mList.get(i).getType()) + " Sensor";
            sensor.vendor = "Made by: " + mList.get(i).getVendor();
            sensorItems.add(sensor);
        }

        int totalSensorSize = mList.size();
        ((TextView) findViewById(R.id.textViewSensorHeader)).setText("Your android device has " + totalSensorSize + " sensors.");

        final ListView sensorListView = (ListView) findViewById(R.id.listViewSensors);

        final SensorListAdapter listAdapter = new SensorListAdapter(sensorItems);

        sensorListView.setAdapter(listAdapter);
        sensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sensorItems.get(i).isSelected = !sensorItems.get(i).isSelected;
                listAdapter.notifyDataSetChanged();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    static String convertSensorTypeToString(int idx) {
        String sensorTypeName;
        switch (idx) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorTypeName = "Accelerometer";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorTypeName = "Ambient Temperature";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                sensorTypeName = "Game Rotation";
                break;
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                sensorTypeName = "Geomagnetic Rotation";
                break;
            case Sensor.TYPE_GRAVITY:
                sensorTypeName = "Gravity";
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorTypeName = "Gyroscope";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                sensorTypeName = "Gyroscope Uncalibrated";
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorTypeName = "Heart Rate";
                break;
            case Sensor.TYPE_LIGHT:
                sensorTypeName = "Light";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorTypeName = "Linear Acceleration";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorTypeName = "Magnetic Field";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                sensorTypeName = "Magnetic Field Uncalibrated";
                break;
            case Sensor.TYPE_PRESSURE:
                sensorTypeName = "Pressure";
                break;
            case Sensor.TYPE_PROXIMITY:
                sensorTypeName = "Proximity";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorTypeName = "Relative Humidity";
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                sensorTypeName = "Rotation";
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                sensorTypeName = "Step Detector";
                break;
            case Sensor.TYPE_STEP_COUNTER:
                sensorTypeName = "Step Counter";
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                sensorTypeName = "Significant Motion";
                break;
            case Sensor.TYPE_ORIENTATION:
                sensorTypeName = "Orientation";
                break;
            case Sensor.TYPE_TEMPERATURE:
                sensorTypeName = "Temperature";
                break;
            default:
                sensorTypeName = "Code=" + idx + " is Unknown Type";
                break;
        }
        return sensorTypeName;
    }

    private class SensorItem {
        String name;
        String vendor;
        String type;
        boolean isSelected = false;
    }

    private class SensorListAdapter extends BaseAdapter {

        List<SensorItem> mSensorItems;

        SensorListAdapter(List<SensorItem> sensorItems) {
            this.mSensorItems = sensorItems;
        }

        @Override
        public int getCount() {
            return mSensorItems.size();
        }

        @Override
        public SensorItem getItem(int position) {
            return mSensorItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) AddSensorActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.sensor_item, parent, false);
            }

            TextView sensorName = (TextView) convertView.findViewById(R.id.textName);
            TextView sensorType = (TextView) convertView.findViewById(R.id.textType);
            TextView sensorVendor = (TextView) convertView.findViewById(R.id.textVendor);
            ImageView isSelectedIcon = (ImageView) convertView.findViewById(R.id.is_selected);

            SensorItem sensorItem = mSensorItems.get(arg0);

            sensorName.setText(sensorItem.name);
            sensorType.setText(sensorItem.type);
            sensorVendor.setText(sensorItem.vendor);

            if (sensorItem.isSelected)
                isSelectedIcon.setVisibility(View.VISIBLE);
            else
                isSelectedIcon.setVisibility(View.INVISIBLE);

            return convertView;
        }

    }
}
