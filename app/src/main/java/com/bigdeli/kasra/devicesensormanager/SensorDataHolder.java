package com.bigdeli.kasra.devicesensormanager;

import android.hardware.Sensor;

import java.util.ArrayList;

/**
 * Created by kbigdeli on 7/16/2015.
 */
public class SensorDataHolder {

    Object lockData = new Object();

    Sensor mSensor;

    boolean isSelected = false;

    ArrayList<Double> x = new ArrayList<>();
    ArrayList<Double> y = new ArrayList<>();
    ArrayList<Double> z = new ArrayList<>();
    ArrayList<Double> t = new ArrayList<>();

    public SensorDataHolder(Sensor sensor) {
        this.mSensor = sensor;
    }

    void onDataReceived(double x, double y, double z, double t) {

        synchronized (lockData) {
            this.x.add(x);
            this.y.add(y);
            this.z.add(z);
            this.t.add(t);
        }
    }

    double getX() {

        synchronized (lockData) {
            return x.get(x.size() - 1);
        }
    }

    double getY() {
        synchronized (lockData) {
            return y.get(y.size() - 1);
        }
    }

    double getZ() {
        synchronized (lockData) {
            return z.get(z.size() - 1);
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
