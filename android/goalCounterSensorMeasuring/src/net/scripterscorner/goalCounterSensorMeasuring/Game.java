package net.scripterscorner.goalCounterSensorMeasuring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zen on 26.10.15.
 */
public class Game extends Activity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accSensor;
    private float gravity[];
    private float linear_acceleration[];

    private String TAG_GRAVITY = "GRAVITY";
    private String TAG_ACCE = "ACCELEARATION";

    private boolean goalSameSide = false;
    private boolean goalOtherSide = false;

    private String LOG_TAG = "GAME";

    String filename = "sensorData";
    FileOutputStream outputStream;
    File dir;
    File file;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

    String curDateExtension;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Date now = new Date();
        curDateExtension = formatter.format(now) + ".txt";

        gravity = new float[3];
        linear_acceleration = new float[3];

        final Button button = (Button) findViewById(R.id.buttonGoalOtherSide);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goalOtherSide = true;
            }
        });

        final Button buttonOtherSide = (Button) findViewById(R.id.buttonGoalSameSide);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goalSameSide = true;
            }
        });

        final Button buttonEndGame = (Button) findViewById(R.id.buttonEndGame);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GoalCounterSensorMeasuring.class);
                startActivity(intent);
            }
        });

        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        dir = new File (sdCard.getAbsolutePath() + "/goalCounter");
        dir.mkdirs();
        file = new File(dir, filename + curDateExtension);
        Log.d("TEST",file.getAbsolutePath());

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.

        final float alpha = 0.8f;

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        String data = "" + formatter.format(new Date()) + gravity[0] + ";" + gravity[1] + ";" + gravity[2] +
                linear_acceleration[0] + ";" + linear_acceleration[1] + ";" + linear_acceleration[2]
                + goalOtherSide + goalSameSide + "\n";

       // Log.d(TAG_GRAVITY, "" + gravity[0] + ";" + gravity[1] + ";" + gravity[2]);
       // Log.d(TAG_ACCE,"" + linear_acceleration[0] + ";" + linear_acceleration[1] + ";" + linear_acceleration[2]);
       // Log.d(LOG_TAG, "" + getDocumentsStorageDir("goalCounter") + filename + curDateExtension);
        try {
            if(isExternalStorageWritable())
            {
               // outputStream = openFileOutput(getDocumentsStorageDir("goalCounter") +
                 //       filename + curDateExtension, Context.MODE_PRIVATE);

                outputStream = new FileOutputStream(file,true);
                outputStream.write(data.getBytes());
                outputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        goalOtherSide = false;
        goalSameSide = false;
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getDocumentsStorageDir(String docName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), docName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public File getDocumentsStorageDir(Context context, String docName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS), docName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}
