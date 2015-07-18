package com.itskasra.devicesensormanager;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kbigdeli on 7/16/2015.
 */
public class AppApplication extends Application {

    ArrayList<SensorDataHolder> sensors = null;
    static AppApplication mInstance = null;
    SensorManager mSensorManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<>();
        boolean isOneSensorSelected = false;

        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < sensorList.size(); i++) {
            SensorDataHolder sensor = new SensorDataHolder(sensorList.get(i));
            sensors.add(sensor);
            if ((!isOneSensorSelected) &&
                    (sensor.getHardwareSensor().getType() == Sensor.TYPE_ACCELEROMETER)) {
                sensor.setIsSelected(true);
                isOneSensorSelected = true;
            }
        }

    }

    public ArrayList<SensorDataHolder> getSensors() {
        return sensors;
    }

    public static AppApplication getInstance() {
        return mInstance;
    }


}
