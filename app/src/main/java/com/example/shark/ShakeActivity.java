package com.example.shark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.widget.TextView;


public class ShakeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensroMgr;
    private Vibrator mVibrator;
    private TextView tv_times;
    private static final int GO_HOME = 0;//去主页
    int i=0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME://去主页
                    Intent intent = new Intent(ShakeActivity.this, MainActivity.class);
                    intent.setClass(ShakeActivity.this,MainActivity.class);
                    intent.putExtra("times",i);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);
        mSensroMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        tv_times=findViewById(R.id.tv_times);
        mHandler.sendEmptyMessageDelayed(GO_HOME, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensroMgr.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensroMgr.registerListener(this, mSensroMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;
            if ((Math.abs(values[0]) > 15 || Math.abs(values[1]) > 15 || Math.abs(values[2]) > 15))
            {
                i++;
                tv_times.setText("徐海帆:"+i+"");
                // tv_shake.setText(Utils.getNowDateTimeFormat() + "  恭喜您摇到奖品了！");
                // 系统检测到摇一摇事件后，震动手机提示用户
                mVibrator.vibrate(300);

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //当传感器精度改变时回调该方法，一般无需处理
    }


}
