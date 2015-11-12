package com.urbandroid.doze;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DozeService extends Service  implements SensorEventListener {

    public static final String TAG = "DOZETEST";

    private static final int NOTIFICATION_ID = 42;

    private float[] last = new float[3];
    private float total = -1;

    private List<Data> data;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PowerManager.WakeLock lock;

    private Runnable logger = new Runnable() {
        @Override
        public void run() {
            data.add(new Data(System.currentTimeMillis(), total));
            total = 0;
            Handler h = new Handler();
            h.postDelayed(this, 60000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        data = new ArrayList<Data>();

        Intent stopIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, stopIntent, 0);


        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setContentIntent(contentIntent)
                .setContentTitle("Doze tester")
                .setContentText("Tap to stop and report log");

        notificationBuilder.setSmallIcon(R.drawable.ic_notification);

        startForeground(NOTIFICATION_ID, notificationBuilder.build());

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Doze lock");
        lock.acquire();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Handler h = new Handler();
        h.post(logger);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        StringBuilder sb = new StringBuilder();
        for (Data entry : data){
            sb.append(entry.toString()).append("\n");
            Log.i(TAG, entry.toString());
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, "petr.nalevka@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Doze Test Data");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "Send test results").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        if (lock != null && lock.isHeld()) {
            lock.release();
        }

        Handler h = new Handler();
        h.removeCallbacks(logger);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (total == -1) {
            total = 0;
        } else {
            for (int i = 0; i < 3; i++) {
                total += Math.abs(last[i] - sensorEvent.values[i]);
                last[i] = sensorEvent.values[i];
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
