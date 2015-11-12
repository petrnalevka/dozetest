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
import com.urbandroid.common.error.DefaultConfigurationBuilder;
import com.urbandroid.common.error.ErrorReporter;
import com.urbandroid.common.error.ErrorReporterConfiguration;
import com.urbandroid.common.logging.Logger;

public class DozeService extends Service  implements SensorEventListener {

    private static final int NOTIFICATION_ID = 42;

    private float[] last = new float[3];
    private float total = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PowerManager.WakeLock lock;

    private Runnable logger = new Runnable() {
        @Override
        public void run() {
            Logger.logInfo("Data " + total);
            total = 0;
            Handler h = new Handler();
            h.postDelayed(this, 60000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.initialize(this, "DOZE", 200, Logger.DEBUG_LEVEL, Logger.INFO_LEVEL);

        ErrorReporterConfiguration configuration = new DefaultConfigurationBuilder.Builder(this, new Handler(), "Doze", new String[] {"petr.nalevka@gmail.com"}).withLockupDatection(false).build();
        ErrorReporter.initialize(this, configuration);

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
