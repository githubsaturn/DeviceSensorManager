package com.bigdeli.kasra.devicesensormanager;

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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
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

    int[] REFRESH_SPEEDS = {SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_FASTEST};

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
                        (this, allSensors.get(idx).mSensor, REFRESH_SPEEDS[allSensors.get(idx).refreshRate]);
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

        for (SensorDataHolder s : sensorViewDatas) {
            if (s.mSensor == sensorEvent.sensor) {

                double x = 0;
                double y = 0;
                double z = 0;

                try {
                    x = sensorEvent.values[0];
                    y = sensorEvent.values[1];
                    z = sensorEvent.values[2];
                } catch (ArrayIndexOutOfBoundsException e) {
                    // for some sensors, not all three values are
                    // available... in this case, do nothing...
                }


                s.onDataReceived(x, y, z, sensorEvent.timestamp);
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


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    // Get the layout inflater
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                    builder.setTitle("Sensor Settings");
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    final View viewDialog = inflater.inflate(R.layout.sensor_config_window, null);

                    final SensorDataHolder sensor = (SensorDataHolder) view.getTag();
                    String str = "<b>Name:</b> " + sensor.getName() + "<br/>" +
                            "<b>Type:</b> " + SensorDataHolder.convertSensorTypeToString(sensor.getType()) + " Sensor"
                            + "<br/>" + "<b>Made by:</b> " + sensor.getVendor();

                    ((CheckBox) viewDialog.findViewById(R.id.check_ax1)).setChecked(sensor.isActiveAxis[0]);
                    ((CheckBox) viewDialog.findViewById(R.id.check_ax2)).setChecked(sensor.isActiveAxis[1]);
                    ((CheckBox) viewDialog.findViewById(R.id.check_ax3)).setChecked(sensor.isActiveAxis[2]);
                    ((SeekBar) viewDialog.findViewById(R.id.refresh_rate)).setProgress(sensor.refreshRate);

                    ((CheckBox) viewDialog.findViewById(R.id.check_smoothen)).setChecked(sensor.isSmoothened);
                    ((CheckBox) viewDialog.findViewById(R.id.check_smoothen)).setText(Html.fromHtml("<br/><b>Remove noise</b><br/>" +
                            "<i>may cause data distortion</i>"));


                    ((TextView) viewDialog.findViewById(R.id.sensor_info_text)).setText(Html.fromHtml(str));
                    builder.setView(viewDialog)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    sensor.isActiveAxis[0] = ((CheckBox) viewDialog.findViewById(R.id.check_ax1)).isChecked();
                                    sensor.isActiveAxis[1] = ((CheckBox) viewDialog.findViewById(R.id.check_ax2)).isChecked();
                                    sensor.isActiveAxis[2] = ((CheckBox) viewDialog.findViewById(R.id.check_ax3)).isChecked();

                                    sensor.refreshRate = ((SeekBar) viewDialog.findViewById(R.id.refresh_rate)).getProgress();

                                    sensor.isSmoothened = ((CheckBox) viewDialog.findViewById(R.id.check_smoothen)).isChecked();

                                    // unregister for now
                                    AppApplication.getInstance().mSensorManager.unregisterListener(MainActivity.this,
                                            sensor.mSensor);
                                    sensor.clearData();
                                    AppApplication.getInstance().mSensorManager.registerListener
                                            (MainActivity.this, sensor.mSensor, REFRESH_SPEEDS[sensor.refreshRate]);

                                }
                            });

                    builder.create().show();

                }
            });

            ((TextView) convertView.findViewById(R.id.text_chart_title)).
                    setText(SensorDataHolder.convertSensorTypeToString(sensorItem.getType()));

            ((SensorView) convertView.findViewById(R.id.sensor_view)).setSensor(sensorItem);

            return convertView;
        }

        // make the list items unclickable
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        // make the list items unclickable
        @Override
        public boolean isEnabled(int position) {
            return false;
        }

    }

}