package com.itskasra.devicesensormanager;

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
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    private static final String KEY_RECORDING = "recording";

    // Debugging
    private final String TAG = AppConfig.getClassName(this);
    private final boolean D = AppConfig.IS_DEBUG_ON;

    List<SensorDataHolder> sensorViewDatas = new ArrayList<>();
    List<SensorDataHolder> allSensors;
    SensorListAdapter listAdapter;

    boolean isRecording = false;

    final int[] REFRESH_SPEEDS = {SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI,
            SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_FASTEST};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            isRecording = savedInstanceState.getBoolean(KEY_RECORDING);

        setContentView(R.layout.activity_main);

        ListView sensorListView = (ListView) findViewById(R.id.listViewSensors);
        allSensors = AppApplication.getInstance().getSensors();

        listAdapter = new SensorListAdapter(sensorViewDatas);
        sensorListView.setAdapter(listAdapter);

        findViewById(R.id.add_button).getBackground().setColorFilter(Color.parseColor("#ff009688"), PorterDuff.Mode.MULTIPLY);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_RECORDING, isRecording);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {

        super.onResume();

        for (SensorDataHolder s : allSensors) {

            sensorViewDatas.remove(s);

            if (s.getIsSelected()) {
                sensorViewDatas.add(s);
                AppApplication.getInstance().mSensorManager.registerListener
                        (this, s.getHardwareSensor(), REFRESH_SPEEDS[s.getRefreshRate()]);
            } else {
                AppApplication.getInstance().mSensorManager.unregisterListener(MainActivity.this,
                        (s.getHardwareSensor()));
            }

        }

        listAdapter.notifyDataSetChanged();
        updateHeader();

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppApplication.getInstance().mSensorManager.unregisterListener(this);
    }

    public void addSensorClicked(View v) {

        cancelRecording();
        Intent intent = new Intent(this, AddSensorActivity.class);
        startActivity(intent);
    }

    private void cancelRecording() {
        if (isRecording) {
            Toast.makeText(getApplicationContext(), "Recording is aborted...", Toast.LENGTH_SHORT).show();
        }
        isRecording = false;
        updateHeader();
    }

    private void updateHeader() {

        if (sensorViewDatas.size() > 0) {
            findViewById(R.id.add_sensor_text).setVisibility(View.GONE);
            findViewById(R.id.layout_record).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layout_record).setVisibility(View.GONE);
            findViewById(R.id.add_sensor_text).setVisibility(View.VISIBLE);
        }


        Button recButton = (Button) findViewById(R.id.rec_button);

        if (isRecording) {
            recButton.setText("Stop");
            recButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop_icon, 0, 0, 0);
            findViewById(R.id.recording_text).setVisibility(View.VISIBLE);

            TextView recordingText = (TextView) findViewById(R.id.recording_text);
            Animation fadeFlashAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_anim);
            recordingText.startAnimation(fadeFlashAnimation);

        } else {

            recButton.setText("Record");
            recButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rec_icon, 0, 0, 0);

            TextView recordingText = (TextView) findViewById(R.id.recording_text);
            recordingText.clearAnimation();
            findViewById(R.id.recording_text).setVisibility(View.GONE);

        }

    }

    public void recordButtonClicked(View v) {


        isRecording = !isRecording;

        updateHeader();

        if (isRecording) {

            for (SensorDataHolder s : allSensors) {
                s.clearData();
            }

        } else {

            saveAndEmailData();
        }
    }

    private void saveAndEmailData() {

        String fileName = "sensordata.csv";

        StringBuilder fileData = new StringBuilder();

        long earliestTimeStamp = 0;
        if (sensorViewDatas.size() > 0)
            earliestTimeStamp = sensorViewDatas.get(0).getEarliestTimeStamp();

        for (SensorDataHolder s : sensorViewDatas) {
            if (s.getEarliestTimeStamp() < earliestTimeStamp)
                earliestTimeStamp = s.getEarliestTimeStamp();
        }

        for (SensorDataHolder s : sensorViewDatas) {
            fileData.append(s.generateReport(earliestTimeStamp));
            fileData.append("\n\n\n\n\n\n\n");
        }
        String data = fileData.toString();
        Log.d(TAG, "file size is: " + data.length());
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            fos.write(data.getBytes());
            fos.close();
            Log.d(TAG, "Created and saved the file! ");
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        try {
            // Not working ... Needs content providers...

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Sensor Data");
            intent.putExtra(Intent.EXTRA_TEXT, "Please see attached for the sensor data file.");
            intent.setType("*/*");
            
            // TODO try Intent.FLAG_GRANT_READ_URI_PERMISSION 
            Uri uri = Uri.parse("content://" + getPackageName() + "/" + fileName);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No emailing app was found...", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {

            Log.e(TAG, "Unable to send data... " + e.toString());
            Toast.makeText(this, "Unable to email data...", Toast.LENGTH_LONG).show();

        }

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
        if (id == R.id.action_about) {

            cancelRecording();

            Intent i = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        for (SensorDataHolder s : sensorViewDatas) {
            if (s.getHardwareSensor() == sensorEvent.sensor) {

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


                    cancelRecording();

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MainActivity.this);

                    alertDialogBuilder.setTitle("Deleting Sensor");

                    alertDialogBuilder
                            .setMessage("Do you want to delete the sensor?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mSensorViewDatas.remove(((SensorDataHolder) view.getTag()));
                                    ((SensorDataHolder) view.getTag()).setIsSelected(false);

                                    AppApplication.getInstance().mSensorManager.unregisterListener(MainActivity.this,
                                            ((SensorDataHolder) view.getTag()).getHardwareSensor());

                                    listAdapter.notifyDataSetChanged();
                                    updateHeader();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setCancelable(true);

                    alertDialogBuilder.create().show();

                }
            });

            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    cancelRecording();

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

                    ((CheckBox) viewDialog.findViewById(R.id.check_ax1)).setChecked(sensor.getIsActiveAxis(0));
                    ((CheckBox) viewDialog.findViewById(R.id.check_ax2)).setChecked(sensor.getIsActiveAxis(1));
                    ((CheckBox) viewDialog.findViewById(R.id.check_ax3)).setChecked(sensor.getIsActiveAxis(2));
                    ((SeekBar) viewDialog.findViewById(R.id.refresh_rate)).setProgress(sensor.getRefreshRate());

                    ((CheckBox) viewDialog.findViewById(R.id.check_smoothen)).setChecked(sensor.getIsSmoothened());
                    ((CheckBox) viewDialog.findViewById(R.id.check_smoothen)).setText(Html.fromHtml("<br/><b>Remove noise</b><br/>" +
                            "<i>may cause data distortion</i>"));


                    ((TextView) viewDialog.findViewById(R.id.sensor_info_text)).setText(Html.fromHtml(str));
                    builder.setView(viewDialog)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    sensor.setIsActiveAxis(0, ((CheckBox) viewDialog.findViewById(R.id.check_ax1)).isChecked());
                                    sensor.setIsActiveAxis(1, ((CheckBox) viewDialog.findViewById(R.id.check_ax2)).isChecked());
                                    sensor.setIsActiveAxis(2, ((CheckBox) viewDialog.findViewById(R.id.check_ax3)).isChecked());

                                    sensor.setRefreshRate(((SeekBar) viewDialog.findViewById(R.id.refresh_rate)).getProgress());

                                    sensor.setIsSmoothened(((CheckBox) viewDialog.findViewById(R.id.check_smoothen)).isChecked());

                                    // re-register now
                                    AppApplication.getInstance().mSensorManager.unregisterListener(MainActivity.this,
                                            sensor.getHardwareSensor());
                                    sensor.clearData();
                                    AppApplication.getInstance().mSensorManager.registerListener
                                            (MainActivity.this, sensor.getHardwareSensor(), REFRESH_SPEEDS[sensor.getRefreshRate()]);

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
