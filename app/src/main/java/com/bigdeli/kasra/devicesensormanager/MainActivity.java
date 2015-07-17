package com.bigdeli.kasra.devicesensormanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    // Debugging
    private final String TAG = AppConfig.getClassName(this);
    private final boolean D = AppConfig.IS_DEBUG_ON;

    List<SensorDataHolder> sensorViewDatas = new ArrayList<>();
    List<SensorDataHolder> allSensors;
    SensorListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView sensorListView = (ListView) findViewById(R.id.listViewSensors);
        allSensors = AppApplication.getInstance().getSensors();

        listAdapter = new SensorListAdapter(sensorViewDatas);
        sensorListView.setAdapter(listAdapter);

        findViewById(R.id.add_button).getBackground().setColorFilter(Color.parseColor("#ff009688"), PorterDuff.Mode.MULTIPLY);

    }

    @Override
    protected void onResume() {

        super.onResume();

        sensorViewDatas.clear();

        for (int idx = 0; idx < allSensors.size(); idx++) {
            if (allSensors.get(idx).isSelected) {
                sensorViewDatas.add(allSensors.get(idx));
                allSensors.get(idx).clearData();
                AppApplication.getInstance().mSensorManager.registerListener
                        (this, allSensors.get(idx).mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        listAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppApplication.getInstance().mSensorManager.unregisterListener(this);
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        for (SensorDataHolder s : sensorViewDatas){
            if (s.mSensor==sensorEvent.sensor){
                s.onDataReceived(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2], sensorEvent.timestamp);
                return;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private class SensorListAdapter extends BaseAdapter {

        List<SensorDataHolder> mSensorViewDatas;

        SensorListAdapter(List<SensorDataHolder> sensorItems) {
            this.mSensorViewDatas = sensorItems;
        }

        @Override
        public int getCount() {
            return mSensorViewDatas.size();
        }

        @Override
        public SensorDataHolder getItem(int position) {
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

            SensorDataHolder sensorItem = mSensorViewDatas.get(arg0);

            Button removeButton = (Button) convertView.findViewById(R.id.remove_graph);
            removeButton.getBackground().setColorFilter(Color.parseColor("#FF3311"), PorterDuff.Mode.MULTIPLY);
            removeButton.setTag(sensorItem);

            ImageButton infoButton = (ImageButton) convertView.findViewById(R.id.info_graph);
            infoButton.getBackground().setColorFilter(Color.parseColor("#00746d"), PorterDuff.Mode.MULTIPLY);
            infoButton.setTag(sensorItem);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);

                    alertDialogBuilder.setTitle("Deleting Sensor");

                    alertDialogBuilder
                            .setMessage("Do you want to delete the sensor?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mSensorViewDatas.remove(((SensorDataHolder) view.getTag()));
                                    ((SensorDataHolder) view.getTag()).isSelected = false;

                                    AppApplication.getInstance().mSensorManager.unregisterListener(MainActivity.this,
                                            ((SensorDataHolder) view.getTag()).mSensor);

                                    listAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                    alertDialogBuilder.create().show();

                }
            });

            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d("XX", "ON CLICKED infoButton___________________________");

                }
            });

            ((TextView) convertView.findViewById(R.id.text_chart_title)).
                    setText(SensorDataHolder.convertSensorTypeToString(sensorItem.getType()));

            ((SensorView) convertView.findViewById(R.id.sensor_view)).setSensor(sensorItem);

            return convertView;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

    }

}