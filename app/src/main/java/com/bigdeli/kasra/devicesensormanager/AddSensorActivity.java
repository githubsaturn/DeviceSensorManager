package com.bigdeli.kasra.devicesensormanager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class AddSensorActivity extends Activity {

    private static final int MAX_SENSORS_SELECT = 5;

    // Debugging
    private final String TAG = AppConfig.getClassName(this);
    private final boolean D = AppConfig.IS_DEBUG_ON;

    int numberOfSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sensor);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setIcon(R.drawable.ic_launcher);
        }



        Button dissmissButton = (Button) findViewById(R.id.dissmiss_btn);
        dissmissButton.getBackground().setColorFilter(Color.parseColor("#00746d"), PorterDuff.Mode.MULTIPLY);
        dissmissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSensorActivity.this.finish();
            }
        });

        final ListView sensorListView = (ListView) findViewById(R.id.listViewSensors);

        final SensorListAdapter listAdapter = new SensorListAdapter(AppApplication.getInstance().getSensors());

        sensorListView.setAdapter(listAdapter);

        for (int i = 0; i < AppApplication.getInstance().getSensors().size(); i++) {
            if (AppApplication.getInstance().getSensors().get(i).isSelected)
                numberOfSelected++;
        }

        sensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SensorDataHolder clickedSensor = AppApplication.getInstance().getSensors().get(position);
                if ((numberOfSelected >= MAX_SENSORS_SELECT) && (!clickedSensor.isSelected)) {
                    Toast.makeText(getApplicationContext(), "Cannot select more than " + MAX_SENSORS_SELECT + " sensors.", Toast.LENGTH_SHORT).show();
                    return;
                }
                clickedSensor.isSelected = !clickedSensor.isSelected;
                if (clickedSensor.isSelected) {
                    numberOfSelected++;
                } else {
                    numberOfSelected--;
                }
                refreshHeader();
                listAdapter.notifyDataSetChanged();
            }
        });

        refreshHeader();


    }

    private void refreshHeader() {

        ((TextView) findViewById(R.id.textViewSensorHeader)).setText(numberOfSelected + " selected (up to 5 sensors)");

    }


    private class SensorListAdapter extends BaseAdapter {

        List<SensorDataHolder> mSensorItems;

        SensorListAdapter(List<SensorDataHolder> sensorItems) {
            this.mSensorItems = sensorItems;
        }

        @Override
        public int getCount() {
            return mSensorItems.size();
        }

        @Override
        public SensorDataHolder getItem(int position) {
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

            SensorDataHolder sensorItem = mSensorItems.get(arg0);


            sensorName.setText(Html.fromHtml("<b>Name:</b> " + sensorItem.getName()));
            sensorType.setText(Html.fromHtml("<b>Type:</b> " + SensorDataHolder.convertSensorTypeToString(sensorItem.getType()) + " Sensor"));
            sensorVendor.setText(Html.fromHtml("<b>Made by:</b> " + sensorItem.getVendor()));

            if (sensorItem.isSelected) {
                isSelectedIcon.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(Color.argb(50, 0, 200, 200));
            } else {
                isSelectedIcon.setVisibility(View.INVISIBLE);
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

    }
}
