package com.example.temperaturaambiental2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor heartRateSensor;
    private TextView txtTemperature;
    private TextView txtHeartRate;

    private float currentTemperature;
    private float currentHeartRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemperature = findViewById(R.id.txtTemperature);
        txtHeartRate = findViewById(R.id.txtHeartRate);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BODY_SENSORS},
                    1);
        } else {
            startMonitoring();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMonitoring();
    }

    private void startMonitoring() {
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopMonitoring() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            currentTemperature = event.values[0];
            txtTemperature.setText("Temperatura: " + currentTemperature);

            checkThresholds();
        } else if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            currentHeartRate = event.values[0];
            txtHeartRate.setText("bpm: " + currentHeartRate);

            checkThresholds();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario implementar nada aquí en este caso
    }

    private void checkThresholds() {
        if (currentTemperature > 40 && currentHeartRate > 120) {
            mostrarAlerta("La temperatura ha superado los 40 grados centrigadosy el ritmo cardíaco ha superado las 150 bpm.");
        } else if (currentTemperature < (10) && currentHeartRate < 60) {
            mostrarAlerta("La temperatura ha caído por debajo de los 10 grados centigrados y el ritmo cardíaco ha bajado de 70 bpm.");
        }
    }

    private void mostrarAlerta(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta");
        builder.setMessage(message);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
