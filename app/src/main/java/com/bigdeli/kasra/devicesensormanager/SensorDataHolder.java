package com.bigdeli.kasra.devicesensormanager;

import android.hardware.Sensor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kbigdeli on 7/16/2015.
 */
public class SensorDataHolder {

    Object lockData = new Object();

    Sensor mSensor;

    boolean isSelected = false;
    boolean isBidirectional = false;

    boolean[] isActiveAxis = {true, true, true};
    boolean isSmoothened = true;

    int refreshRate = 3;


    ArrayList<Double> x = new ArrayList<>();
    ArrayList<Double> y = new ArrayList<>();
    ArrayList<Double> z = new ArrayList<>();
    ArrayList<Long> t = new ArrayList<>();

    double avX = 0;
    double avY = 0;
    double avZ = 0;
    double maxValue = 0.00001;

    public SensorDataHolder(Sensor sensor) {
        this.mSensor = sensor;
    }

    void onDataReceived(double x, double y, double z, long t) {

        synchronized (lockData) {

            if (!isBidirectional) {
                if ((x < 0) || (y < 0) || (z < 0))
                    isBidirectional = true;
            }

            if (maxValue < Math.abs(x))
                maxValue = Math.abs(x);

            if (maxValue < Math.abs(y))
                maxValue = Math.abs(y);

            if (maxValue < Math.abs(z))
                maxValue = Math.abs(z);

            if (isSmoothened) {
                avX = avX * 0.8 + x * 0.2;
                avY = avY * 0.8 + y * 0.2;
                avZ = avZ * 0.8 + z * 0.2;
            } else {
                avX = x;
                avY = y;
                avZ = z;
            }


            this.x.add(x);
            this.y.add(y);
            this.z.add(z);
            this.t.add(t);

        }
    }

    void clearData() {

        synchronized (lockData) {
            x.clear();
            y.clear();
            z.clear();
            t.clear();
            maxValue = 0.0001;
            avX = 0;
            avY = 0;
            avZ = 0;
        }
    }

    double getLastX() {

        if (!isActiveAxis[0])
            return 0;

        synchronized (lockData) {
            return avX;
        }
    }

    double getLastY() {

        if (!isActiveAxis[1])
            return 0;

        synchronized (lockData) {
            return avY;
        }
    }

    double getLastZ() {

        if (!isActiveAxis[2])
            return 0;

        synchronized (lockData) {
            return avZ;
        }
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
                sensorTypeName = "(" + idx + ") Unknown";
                break;
        }
        return sensorTypeName;
    }

    public String getName() {
        return mSensor.getName();
    }

    public int getType() {
        return mSensor.getType();
    }

    public String getVendor() {
        return mSensor.getVendor();
    }
}
