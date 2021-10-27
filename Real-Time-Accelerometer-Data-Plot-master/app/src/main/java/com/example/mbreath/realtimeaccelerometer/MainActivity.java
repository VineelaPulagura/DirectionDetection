package com.example.mbreath.realtimeaccelerometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class     MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private  Sensor sensors;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private float[] orient = new float[3];
    private float[] accel = new float[3];
    private float[] mag = new float[3];
    private float[] accelOrient = new float[3];
    private float[] rotationMatrix = new float[9];

    private float azimuth;
    private float pitch;
    private float roll;
    private int window_size = 20;
    private Map<Integer, Double> accelMap = new HashMap<Integer, Double>();
    private Map<Integer, Double> initialAccelMap = new HashMap<Integer, Double>();
    private int i = 0;
    private double threshold = 2.3;
    private boolean peak;
    private boolean flag;
    private ImageView compassimage;
    private float DegreeStart = 0f;
    TextView DegreeTV;
    private long seconds = 0;
    private long startTime = System.currentTimeMillis();
    SimpleDateFormat format = new SimpleDateFormat("mm:ss.SS");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

//        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

//        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//
//        for(int i=0; i<sensors.size(); i++){
//            Log.d(TAG, "onCreate: Sensor "+ i + ": " + sensors.get(i).toString());
//        }
//
//        if (mAccelerometer != null) {
//            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//        }
//        if (mMagnetometer != null) {
//            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
//        }

        mChart = (LineChart) findViewById(R.id.chart1);
        PrepareLineChart(mChart);
        compassimage = (ImageView) findViewById(R.id.compass_image);
       /* // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);*/
        Log.d("CREATION","Started============================Time"+startTime);
//        runTimer();
        feedMultiple();


    }

    private void runTimer()
    {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                seconds = (System.currentTimeMillis() - startTime);
                Log.d("Time","Started============================seconds"+format.format(System.currentTimeMillis() - startTime));
                handler.postDelayed(this, 10);
            }
        });
    }
    private void PrepareLineChart(LineChart mChart )
    {
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.getLegend().setEnabled(true);
        mChart.getLegend().setTextSize(12f);
        mChart.setDrawGridBackground(false);
        mChart.animateY(1100);
        mChart.setDrawBorders(false);
        mChart.setHardwareAccelerationEnabled(true);

//        List<ILineDataSet> sets = new ArrayList<ILineDataSet>();
//        Log.d("CREATION","Started============================Linechart");
//        sets.add(CreateLineDataSet(Color.MAGENTA, "X"));
//        sets.add(CreateLineDataSet(Color.GREEN, "Y"));
//        sets.add(CreateLineDataSet(Color.BLUE, "Z"));



        LineData data = new LineData();
        mChart.setData(data);
        XAxis xl = mChart.getXAxis();
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }
    private LineDataSet CreateLineDataSet(int mcolor, String mLabel)
    {
        LineDataSet set = new LineDataSet(null, "Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(mcolor);
        set.setHighlightEnabled(false);
        set.setLabel(mLabel);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }
    private void addEntry(float[] event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
//                set = createSet();
                set = createSet(Color.MAGENTA, "X");
                data.addDataSet(set);

                data.addDataSet(createSet(Color.BLUE, "Y"));
                data.addDataSet(createSet(Color.GREEN, "Z"));


            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
//            data.addEntry(new Entry(set.getEntryCount(), event.values[0] + 5), 0);
//            data.addEntry(new Entry(set.getEntryCount(), event.values[1] + 5), 1);
//            data.addEntry(new Entry(set.getEntryCount(), event.values[2] + 5), 2);
            data.addEntry(new Entry(set.getEntryCount(), event[0] + 5), 0);
            data.addEntry(new Entry(set.getEntryCount(), event[1] + 5), 1);
            data.addEntry(new Entry(set.getEntryCount(), event[2] + 5), 2);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(150);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet(int mColor, String mLabel) {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(mColor);
        set.setLabel(mLabel);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null){
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true){
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        mSensorManager.unregisterListener(this);

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        Log.d("CREATION", "Sensor type===================================" + sensorType);

        switch (sensorType) {
            case Sensor.TYPE_ORIENTATION:

                System.arraycopy(event.values, 0, orient, 0, 3);
                break;
            case Sensor.TYPE_ACCELEROMETER:
//                final float alpha = (float) 0.8;
//                float[] gravity = new float[]{(float)9.8, (float)9.8, (float)9.8};
////                // Isolate the force of gravity with the low-pass filter.
//                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//                // Remove the gravity contribution with the high-pass filter.
//                accel[0] = event.values[0] - gravity[0];
//                accel[1] = event.values[1] - gravity[1];
//                accel[2] = event.values[2] - gravity[2];
                System.arraycopy(event.values, 0, accel , 0, 3);
                getAccelMagOrientation();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mag, 0, 3);
                break;
        }

        if(i<window_size){
            initialAccelMap.put(i, (double) accel[2]);
            i++;
        }
        else
        {
            /*if(accelMap.size()>0)
            {
                calculateFusedOrientationTask(initialAccelMap, accelMap);
                accelMap = initialAccelMap;
                i=0;
            }
            else
            {
                accelMap = initialAccelMap;
                i=0;
            }*/
            calculateFusedOrientationTask(initialAccelMap, accelMap);
            i=0;
        }
        /*if(i<window_size){
            initialAccelMap.put(i, (double) accel[2]);
            i++;
        }
        if(i>=window_size){
            double mean = initialAccelMap.values().stream().mapToDouble(value -> value).sum()/initialAccelMap.size();
            double SD = Math.sqrt((initialAccelMap.values().stream().mapToDouble(v -> Math.pow(v - mean, 2)).sum())/initialAccelMap.size());
            accelMap.put(1, mean);
            accelMap.put(2, SD);
            calculateFusedOrientationTask(initialAccelMap, accelMap);
        }*/

    }
    public void getAccelMagOrientation()
    {
        if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, mag)) {
            SensorManager.getOrientation(rotationMatrix, accelOrient);
        }
    }
    public float updateAzimuth(float azimuth)
    {
        return azimuth > 180 ? (-360+azimuth) : (azimuth < -180 ? (360 + azimuth) : azimuth);

    }
    public void calculateFusedOrientationTask(Map<Integer, Double> initialAccelMap, Map<Integer, Double> accelMap) {

        double meanInitial = initialAccelMap.values().stream().mapToDouble(value -> value).sum()/initialAccelMap.size();
        /*double meanAccel = initialAccelMap.values().stream().mapToDouble(value -> value).sum()/initialAccelMap.size();
       if(meanInitial-meanAccel>threshold)
       {
           peak = meanInitial>meanAccel?true:false;
       }*/
        peak = meanInitial>threshold?true:false;

        Log.d("CREATION", "accelMap===================================" + accelMap);
        /*if ((((double) accel[2]) - accelMap.get(1)) > threshold * accelMap.get(2)) {
            peak = ((double) accel[2]) > accelMap.get(1) ? true : false;

        }*/
        Log.d("CREATION", "accelOrient===================================" + accelOrient[0]*180/Math.PI+","+ accelOrient[1]*180/Math.PI+","+ accelOrient[2]*180/Math.PI);
        Log.d("CREATION", "Orient===================================" + orient[0]+","+ orient[1]+","+ orient[2]);
//        azimuth = orient[0];
//        pitch = orient[1];
//        roll = orient[2];
        float azimuthVal = (float) Math.toDegrees(accelOrient[0]);
        azimuth = azimuthVal;
        pitch = (float) Math.toDegrees(accelOrient[1]);
        roll = (float) Math.toDegrees(accelOrient[2]);
        Log.d("Creation=======", "Azimuth" + azimuth + "Pitch" + pitch + "Roll" + roll);
        Log.d("CREATION", "Printing the data accelerometer==========" + accel[2]);
        if (pitch < 0) {
            azimuth = updateAzimuth(azimuth + roll);
            azimuth = updateAzimuth(peak ? azimuth + 180 : azimuth);
        } else {
            azimuth = updateAzimuth(azimuth - roll);
            azimuth = updateAzimuth(!peak ? azimuth + 180 : azimuth);
        }
        float orientAzimuth = orient[0];
        if(orient[1]<0)
        {
            orientAzimuth = orientAzimuth + orient[2];
            orientAzimuth = peak ? orientAzimuth + 180 : orientAzimuth;
        } else {
            orientAzimuth = orientAzimuth - orient[2];
            orientAzimuth = !peak ? orientAzimuth + 180 : orientAzimuth;
        }

        float aziVal = azimuth<0 ? -180-azimuth : 180 - azimuth;
        float[] values = new float[]{azimuth, pitch, roll};
        float[] values2 = new float[]{orientAzimuth, orient[1], orient[2]};
        try
        {

            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            //File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists())
            {
                root.mkdirs();
            }
            File gpxfile = new File(root, "output.csv");

            Log.d("CREATED==============","Files====================");
            FileWriter writer = new FileWriter(gpxfile,true);
            StringBuilder sb = new StringBuilder();
            if(!(gpxfile.length()>0)) {
                sb.append("Time in Seconds");
                sb.append(",");
                sb.append("Acceleration[0]");
                sb.append(",");
                sb.append("Acceleration[1]");
                sb.append(",");
                sb.append("Acceleration[2]");
                sb.append(",");
                sb.append("Orient[0]");
                sb.append(",");
                sb.append("Orient[1]");
                sb.append(",");
                sb.append("Orient[2]");
                sb.append(",");
                sb.append("accelOrient[0]");
                sb.append(",");
                sb.append("accelOrient[1]");
                sb.append(",");
                sb.append("accelOrient[2]");
                sb.append(",");
                sb.append("orientAzimuth");
                sb.append(",");
                sb.append("accelAzimuth");
                sb.append("\n");
            }

//            sb.append(String.valueOf(seconds)!=null?String.valueOf(seconds):"");
//            sb.append(",");
            sb.append(format.format(System.currentTimeMillis() - startTime));
            sb.append(",");
            sb.append(String.valueOf(accel[0])!=null?String.valueOf(accel[0]):"");
            sb.append(",");
            sb.append(String.valueOf(accel[1])!=null?String.valueOf(accel[1]):"");
            sb.append(",");
            sb.append(String.valueOf(meanInitial)!=null?String.valueOf(meanInitial):"");
            sb.append(",");
            sb.append(String.valueOf(orient[0])!=null?String.valueOf(orient[0]):"");
            sb.append(",");
            sb.append(String.valueOf(orient[1])!=null?String.valueOf(orient[1]):"");
            sb.append(",");
            sb.append(String.valueOf(orient[2])!=null?String.valueOf(orient[2]):"");
            sb.append(",");
            sb.append(String.valueOf(Math.toDegrees(accelOrient[0]))!=null?String.valueOf(Math.toDegrees(accelOrient[0])):"");
            sb.append(",");
            sb.append(String.valueOf(Math.toDegrees(accelOrient[1]))!=null?String.valueOf(Math.toDegrees(accelOrient[1])):"");
            sb.append(",");
            sb.append(String.valueOf(Math.toDegrees(accelOrient[2]))!=null?String.valueOf(Math.toDegrees(accelOrient[2])):"");
            sb.append(",");
            sb.append(orientAzimuth);
            sb.append(",");
            sb.append(azimuth);
            sb.append("\n");
            writer.append(sb.toString());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
        float degree = aziVal;
//        DegreeTV.setText("Heading: " + Float.toString(degree) + " degrees");
        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // set the compass an\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\imation after the end of the reservation status
        ra.setFillAfter(true);
        // set how long the animation for the compass image will take place
        ra.setDuration(210);
        // Start animation of compass image
        compassimage.startAnimation(ra);
        DegreeStart = -degree;
        Log.d("CREATED==============","Files====================");
        if (plotData) {
            addEntry(values);
            plotData = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(MainActivity.this);
        thread.interrupt();

        super.onDestroy();
    }
}
