package com.mr.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    static public SensorManager mSensorManager;
    private Sensor mSensor;


    private int screenWidth;
    private int screenHeight;
    private int imgEdgeSize;
    private boolean layoutReady;
    private ConstraintLayout mainContainer;
    private Path upPath;
    private Path downPath;
    private boolean animFlag = false;
    private boolean isShaking = false;

    private float sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            // Success!.
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        else {
            // Failure! set text  or display dialog
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float temp = event.values[0];

        if(Math.abs(temp) > 0){
            animate();
            isShaking= true;
            sum += temp;
        }
        else if(isShaking){
            generateAnswer(sum);
            isShaking = false;
            sum = 0;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onResume(){
        super.onResume();

        if(mSensor != null){
            mSensorManager.registerListener(this, mSensor, 100000);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(mSensor != null){
            mSensorManager.unregisterListener(this, mSensor);
        }
    }

    private void animate(){

    }
    private void generateAnswer(float x){
        TextView answerTV = findViewById(R.id.answerTextView);
        String shortcuts[] = getResources().getStringArray(R.array.values);

        final int n = new Random().nextInt(16);
        answerTV.setText(shortcuts[n]);

    }
}
