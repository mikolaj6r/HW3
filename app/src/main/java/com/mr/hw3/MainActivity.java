package com.mr.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    static public SensorManager mSensorManager;
    private Sensor mSensor;
    private float gravity = 0.0f; //needed for TYPE.ACCELEROMETER

    private int screenWidth;
    private int imgEdgeSize;
    private ConstraintLayout mainContainer;
    private boolean isAnimating = false;
    private boolean isShaking = false;
    private float sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        else {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        setContentView(R.layout.activity_main);

        final ImageView ballIV = findViewById(R.id.ballImageView);

        mainContainer = findViewById(R.id.sensor_container);
        mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout(){
                imgEdgeSize = ballIV.getWidth();
                screenWidth = mainContainer.getWidth();
                mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onSensorChanged(SensorEvent event) {

        float temp;
        if(mSensor.getType() ==  Sensor.TYPE_ACCELEROMETER){
            final float alpha = 0.8f;
            gravity = alpha * gravity + (1 - alpha) * event.values[0];
            temp = event.values[0] - gravity;
        }
        else{
            temp = event.values[0];
        }

        Log.i("val", Float.toString(temp));

        if(Math.abs(temp) > 1){
            sum += temp;
            if(!isAnimating) animate();
            isShaking= true;
        }
        else{
            if(sum == 0){
                isShaking = false;
            }
            if(isShaking ){
                generateAnswer(sum);
                sum = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


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
        final TextView answerTV = findViewById(R.id.answerTextView);
        final ImageView ballIV = findViewById(R.id.ballImageView);

        final RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, ballIV.getPivotX(), ballIV.getPivotY());
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(1);
        rotateAnimation.setDuration(170);


        final TranslateAnimation rightAnimation = new TranslateAnimation(0f,  (screenWidth - imgEdgeSize)/2f, 0f, 0f);
        rightAnimation.setDuration(170);
        rightAnimation.setRepeatCount(1);
        rightAnimation.setRepeatMode(Animation.REVERSE);
        rightAnimation.setInterpolator(new DecelerateInterpolator());
        rightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ballIV.setImageResource(R.drawable.hw3ball_front);
                answerTV.setVisibility(View.INVISIBLE);
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!isShaking){
                    isAnimating = false;
                    ballIV.setImageResource(R.drawable.hw3ball_empty);
                    answerTV.setVisibility(View.VISIBLE);
                    return;
                }
                TranslateAnimation leftAnimation = new TranslateAnimation(0f, -(screenWidth - imgEdgeSize)/2f, 0f, 0f);
                leftAnimation.setDuration(170);
                leftAnimation.setRepeatCount(1);
                leftAnimation.setInterpolator(new DecelerateInterpolator());
                leftAnimation.setRepeatMode(Animation.REVERSE);

                leftAnimation.setAnimationListener(new Animation.AnimationListener() {

                       @Override
                       public void onAnimationStart(Animation animation) {

                       }

                       @Override
                       public void onAnimationEnd(Animation animation) {
                           if(!isShaking){
                               ballIV.setImageResource(R.drawable.hw3ball_empty);
                               answerTV.setVisibility(View.VISIBLE);
                               isAnimating = false;
                               return;
                           }
                           AnimationSet temp = new AnimationSet(false);
                           temp.addAnimation(rotateAnimation);
                           temp.addAnimation(rightAnimation);
                           ballIV.clearAnimation();
                           ballIV.startAnimation(temp);


                       }

                       @Override
                       public void onAnimationRepeat(Animation animation) {

                       }
                });
                AnimationSet temp = new AnimationSet(false);
                temp.addAnimation(rotateAnimation);
                temp.addAnimation(leftAnimation);

                ballIV.clearAnimation();
                ballIV.startAnimation(temp);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });
        AnimationSet temp = new AnimationSet(false);
        temp.addAnimation(rotateAnimation);
        temp.addAnimation(rightAnimation);
        ballIV.clearAnimation();
        ballIV.startAnimation(temp);

    }


    private void generateAnswer(float sum){
        TextView answerTV = findViewById(R.id.answerTextView);
        String shortcuts[] = getResources().getStringArray(R.array.values);

        final int n = Math.abs(Math.round(sum*1000))%20;
        Log.i("sum", Float.toString(sum));
        Log.i("n", Integer.toString(n));

        answerTV.setText(shortcuts[n]);
    }


}
